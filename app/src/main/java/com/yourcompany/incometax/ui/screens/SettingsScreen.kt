package com.yourcompany.incometax.ui.screens

import android.content.pm.PackageManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.yourcompany.incometax.ui.viewmodel.BillingViewModel
import com.yourcompany.incometax.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    billingViewModel: BillingViewModel,
    profileViewModel: ProfileViewModel
) {
    val context = LocalContext.current
    var showClearDialog by remember { mutableStateOf(false) }
    var showRestoreMessage by remember { mutableStateOf(false) }
    var restoreSuccess by remember { mutableStateOf(false) }
    
    val purchaseState by billingViewModel.purchaseState.collectAsState()
    val profiles by profileViewModel.profiles.collectAsState()
    
    LaunchedEffect(purchaseState) {
        if (purchaseState == BillingViewModel.PurchaseState.Success) {
            showRestoreMessage = true
            restoreSuccess = true
            billingViewModel.resetPurchaseState()
        } else if (purchaseState == BillingViewModel.PurchaseState.Failed) {
            showRestoreMessage = true
            restoreSuccess = false
            billingViewModel.resetPurchaseState()
        }
    }
    
    val appVersion = try {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName
    } catch (e: PackageManager.NameNotFoundException) {
        "Unknown"
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SettingsItem(
                title = "Restore Purchase",
                subtitle = "Restore your Pro subscription",
                onClick = { billingViewModel.restorePurchase() },
                showLoading = purchaseState == BillingViewModel.PurchaseState.Loading
            )
            
            Divider()
            
            SettingsItem(
                title = "Clear All Profiles",
                subtitle = "${profiles.size} profile(s) saved",
                onClick = { showClearDialog = true },
                enabled = profiles.isNotEmpty()
            )
            
            Divider()
            
            SettingsItem(
                title = "Privacy Policy",
                subtitle = "View our privacy policy",
                onClick = { /* Navigate to privacy policy */ }
            )
            
            Divider()
            
            SettingsItem(
                title = "About",
                subtitle = "Version $appVersion",
                onClick = { /* Show about dialog */ },
                showArrow = false
            )
        }
    }
    
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear All Profiles") },
            text = {
                Text("Are you sure you want to delete all ${profiles.size} saved profile(s)? This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        profiles.forEach { profile ->
                            profileViewModel.deleteProfile(profile.id)
                        }
                        showClearDialog = false
                    }
                ) {
                    Text("Delete All", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    if (showRestoreMessage) {
        AlertDialog(
            onDismissRequest = { showRestoreMessage = false },
            title = {
                Text(if (restoreSuccess) "Success" else "Restore Failed")
            },
            text = {
                Text(
                    if (restoreSuccess)
                        "Your purchase has been restored successfully."
                    else
                        "Unable to restore purchase. Please make sure you're using the same Google account."
                )
            },
            confirmButton = {
                TextButton(onClick = { showRestoreMessage = false }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
private fun SettingsItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    showArrow: Boolean = true,
    showLoading: Boolean = false
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick),
        color = if (enabled) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (showLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp)
                )
            } else if (showArrow) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
