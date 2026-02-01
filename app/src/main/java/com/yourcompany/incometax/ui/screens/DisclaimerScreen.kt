package com.yourcompany.incometax.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun DisclaimerScreen(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    
    Dialog(onDismissRequest = { }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Disclaimer",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Text(
                    text = buildDisclaimerText(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                
                Button(
                    onClick = {
                        saveDisclaimerAccepted(context)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Accept and Continue")
                }
            }
        }
    }
}

private fun buildDisclaimerText(): String {
    return """
        This Income Tax Calculator is provided for informational and educational purposes only.
        
        Important Points:
        
        • The calculations provided are estimates and should not be considered as professional tax advice.
        
        • Tax laws are subject to change, and this calculator may not reflect the most current regulations.
        
        • Individual tax situations vary, and this calculator does not account for all possible deductions, exemptions, or special circumstances.
        
        • We recommend consulting with a qualified tax professional or chartered accountant for accurate tax planning and filing.
        
        • The developers and publishers of this app are not liable for any errors, omissions, or consequences arising from the use of this calculator.
        
        • By using this app, you acknowledge that you understand these limitations and accept full responsibility for your tax-related decisions.
    """.trimIndent()
}

private fun saveDisclaimerAccepted(context: Context) {
    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    sharedPreferences.edit().putBoolean("disclaimer_accepted", true).apply()
}

fun isDisclaimerAccepted(context: Context): Boolean {
    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    return sharedPreferences.getBoolean("disclaimer_accepted", false)
}
