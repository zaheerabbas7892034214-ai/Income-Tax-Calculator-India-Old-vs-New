package com.yourcompany.incometax.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.yourcompany.incometax.ui.viewmodel.DeductionViewModel
import com.yourcompany.incometax.ui.viewmodel.IncomeViewModel
import com.yourcompany.incometax.ui.viewmodel.ResultViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeductionsScreen(
    navController: NavController,
    deductionViewModel: DeductionViewModel = viewModel(),
    incomeViewModel: IncomeViewModel = viewModel(),
    resultViewModel: ResultViewModel = viewModel()
) {
    val section80C by deductionViewModel.section80C.collectAsState()
    val section80D by deductionViewModel.section80D.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Deductions") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Enter Deduction Details",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Text(
                    text = "ℹ These deductions apply to Old Regime only",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(16.dp)
                )
            }
            
            OutlinedTextField(
                value = section80C,
                onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) deductionViewModel.updateSection80C(it) },
                label = { Text("Section 80C") },
                supportingText = { Text("PPF, EPF, Life Insurance, ELSS etc. (Max ₹1.5L)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                prefix = { Text("₹ ") }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = section80D,
                onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) deductionViewModel.updateSection80D(it) },
                label = { Text("Section 80D") },
                supportingText = { Text("Health Insurance Premium") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                prefix = { Text("₹ ") }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = {
                    val incomeInputs = incomeViewModel.getTaxInputs()
                    val completeInputs = deductionViewModel.applyDeductions(incomeInputs)
                    resultViewModel.calculateTax(completeInputs)
                    navController.navigate("result")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "Calculate",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
