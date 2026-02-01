package com.yourcompany.incometax.ui.screens

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.yourcompany.incometax.data.model.TaxInputs
import com.yourcompany.incometax.ui.viewmodel.BillingViewModel
import com.yourcompany.incometax.ui.viewmodel.ProfileViewModel
import com.yourcompany.incometax.ui.viewmodel.ResultViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    navController: NavController,
    inputs: TaxInputs,
    financialYear: String,
    resultViewModel: ResultViewModel = viewModel(),
    billingViewModel: BillingViewModel,
    profileViewModel: ProfileViewModel
) {
    val context = LocalContext.current
    val activity = context as? Activity
    
    val oldRegimeResult by resultViewModel.oldRegimeResult.collectAsState()
    val newRegimeResult by resultViewModel.newRegimeResult.collectAsState()
    val recommendedRegime by resultViewModel.recommendedRegime.collectAsState()
    val isPro by billingViewModel.isPro.collectAsState()
    
    var showSaveDialog by remember { mutableStateOf(false) }
    var profileName by remember { mutableStateOf("") }
    
    LaunchedEffect(inputs) {
        resultViewModel.calculateTax(inputs)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tax Calculation Result") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (oldRegimeResult == null || newRegimeResult == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Text(
                    text = "Financial Year: $financialYear",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RegimeCard(
                        modifier = Modifier.weight(1f),
                        title = "Old Regime",
                        result = oldRegimeResult!!,
                        isRecommended = recommendedRegime == "Old"
                    )
                    
                    RegimeCard(
                        modifier = Modifier.weight(1f),
                        title = "New Regime",
                        result = newRegimeResult!!,
                        isRecommended = recommendedRegime == "New"
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Tax Breakdown",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        BreakdownRow("Gross Total Income", formatCurrency(inputs.totalIncome))
                        BreakdownRow("Standard Deduction", formatCurrency(inputs.standardDeduction))
                        BreakdownRow("Other Deductions", formatCurrency(inputs.section80Deductions))
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        BreakdownRow(
                            "Old Regime Tax",
                            formatCurrency(oldRegimeResult!!.taxPayable),
                            fontWeight = FontWeight.Bold
                        )
                        BreakdownRow(
                            "New Regime Tax",
                            formatCurrency(newRegimeResult!!.taxPayable),
                            fontWeight = FontWeight.Bold
                        )
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        BreakdownRow(
                            "Savings with ${recommendedRegime ?: "Best"} Regime",
                            formatCurrency(
                                kotlin.math.abs(
                                    oldRegimeResult!!.taxPayable - newRegimeResult!!.taxPayable
                                )
                            ),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = {
                        if (isPro) {
                            resultViewModel.exportPdf(context, inputs, financialYear)
                        } else {
                            navController.navigate("paywall")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isPro) "Export PDF" else "Export PDF (Pro)")
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedButton(
                    onClick = {
                        if (isPro) {
                            showSaveDialog = true
                        } else {
                            navController.navigate("paywall")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isPro) "Save Profile" else "Save Profile (Pro)")
                }
            }
        }
    }
    
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Save Profile") },
            text = {
                OutlinedTextField(
                    value = profileName,
                    onValueChange = { profileName = it },
                    label = { Text("Profile Name") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (profileName.isNotBlank()) {
                            profileViewModel.saveProfile(profileName, inputs)
                            showSaveDialog = false
                            profileName = ""
                        }
                    },
                    enabled = profileName.isNotBlank()
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun RegimeCard(
    modifier: Modifier = Modifier,
    title: String,
    result: com.yourcompany.incometax.data.model.TaxResult,
    isRecommended: Boolean
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (isRecommended) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            if (isRecommended) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "RECOMMENDED",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Tax Payable",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = formatCurrency(result.taxPayable),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Effective Rate",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${String.format("%.2f", result.effectiveTaxRate)}%",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun BreakdownRow(
    label: String,
    value: String,
    fontWeight: FontWeight = FontWeight.Normal,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = fontWeight,
            color = color
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = fontWeight,
            color = color
        )
    }
}

private fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    return format.format(amount)
}
