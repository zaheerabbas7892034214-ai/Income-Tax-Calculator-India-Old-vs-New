package com.yourcompany.incometax.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yourcompany.incometax.data.model.TaxInputs
import com.yourcompany.incometax.ui.viewmodel.ResultViewModel
import kotlinx.coroutines.delay

@Composable
fun ExportScreen(
    inputs: TaxInputs,
    financialYear: String,
    onExportComplete: (Boolean, String) -> Unit,
    resultViewModel: ResultViewModel = viewModel()
) {
    val context = LocalContext.current
    var exportStatus by remember { mutableStateOf<ExportStatus>(ExportStatus.Idle) }
    
    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri: Uri? ->
        if (uri != null) {
            exportStatus = ExportStatus.Exporting
            LaunchedEffect(Unit) {
                try {
                    resultViewModel.exportPdf(context, inputs, financialYear)
                    delay(500)
                    exportStatus = ExportStatus.Success
                    onExportComplete(true, "PDF exported successfully")
                } catch (e: Exception) {
                    exportStatus = ExportStatus.Error(e.message ?: "Export failed")
                    onExportComplete(false, e.message ?: "Export failed")
                }
            }
        } else {
            exportStatus = ExportStatus.Cancelled
            onExportComplete(false, "Export cancelled")
        }
    }
    
    LaunchedEffect(Unit) {
        val fileName = "Tax_Report_${financialYear.replace(" ", "_")}.pdf"
        createDocumentLauncher.launch(fileName)
    }
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (exportStatus) {
            is ExportStatus.Idle -> {
                CircularProgressIndicator()
            }
            is ExportStatus.Exporting -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Exporting PDF...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            is ExportStatus.Success -> {
                Text(
                    text = "PDF exported successfully",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            is ExportStatus.Error -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Export failed",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = (exportStatus as ExportStatus.Error).message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            is ExportStatus.Cancelled -> {
                Text(
                    text = "Export cancelled",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

sealed class ExportStatus {
    object Idle : ExportStatus()
    object Exporting : ExportStatus()
    object Success : ExportStatus()
    object Cancelled : ExportStatus()
    data class Error(val message: String) : ExportStatus()
}
