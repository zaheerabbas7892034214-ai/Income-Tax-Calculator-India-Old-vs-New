package com.yourcompany.incometax

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.yourcompany.incometax.data.database.TaxDatabase
import com.yourcompany.incometax.domain.billing.BillingManager
import com.yourcompany.incometax.domain.repository.EntitlementRepository
import com.yourcompany.incometax.ui.navigation.NavGraph
import com.yourcompany.incometax.ui.navigation.Screen
import com.yourcompany.incometax.ui.screens.DisclaimerScreen
import com.yourcompany.incometax.ui.theme.IncomeTaxCalculatorTheme
import com.yourcompany.incometax.ui.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {

    private lateinit var billingManager: BillingManager
    private lateinit var viewModelFactory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = TaxDatabase.getDatabase(applicationContext)
        val entitlementRepository = EntitlementRepository(database.entitlementDao())
        
        billingManager = BillingManager(
            context = applicationContext,
            entitlementRepository = entitlementRepository
        )

        viewModelFactory = ViewModelFactory(
            application = application,
            billingManager = billingManager
        )

        setContent {
            IncomeTaxCalculatorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var showDisclaimer by remember { 
                        mutableStateOf(isFirstLaunch())
                    }

                    if (showDisclaimer) {
                        DisclaimerScreen(
                            onDismiss = {
                                showDisclaimer = false
                                setDisclaimerShown()
                            }
                        )
                    }

                    val navController = rememberNavController()
                    NavGraph(
                        navController = navController,
                        startDestination = Screen.Splash.route,
                        viewModelFactory = viewModelFactory
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        billingManager.processPurchases()
    }

    override fun onDestroy() {
        super.onDestroy()
        billingManager.disconnect()
    }

    private fun isFirstLaunch(): Boolean {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_FIRST_LAUNCH, true)
    }

    private fun setDisclaimerShown() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
    }

    companion object {
        private const val PREFS_NAME = "tax_calculator_prefs"
        private const val KEY_FIRST_LAUNCH = "first_launch"
    }
}
