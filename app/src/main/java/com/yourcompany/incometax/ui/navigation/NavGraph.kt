package com.yourcompany.incometax.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.yourcompany.incometax.ui.screens.*
import com.yourcompany.incometax.ui.viewmodel.*

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object IncomeInputs : Screen("incomeInputs")
    object Deductions : Screen("deductions")
    object Result : Screen("result")
    object Profiles : Screen("profiles")
    object Export : Screen("export")
    object Paywall : Screen("paywall")
    object Settings : Screen("settings")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Splash.route,
    viewModelFactory: ViewModelFactory
) {
    val incomeViewModel: IncomeViewModel = viewModel(factory = viewModelFactory)
    val deductionViewModel: DeductionViewModel = viewModel(factory = viewModelFactory)
    val resultViewModel: ResultViewModel = viewModel(factory = viewModelFactory)
    val homeViewModel: HomeViewModel = viewModel(factory = viewModelFactory)
    val profileViewModel: ProfileViewModel = viewModel(factory = viewModelFactory)
    val billingViewModel: BillingViewModel = viewModel(factory = viewModelFactory)

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                navController = navController,
                viewModel = homeViewModel
            )
        }

        composable(Screen.IncomeInputs.route) {
            IncomeInputsScreen(
                navController = navController,
                viewModel = incomeViewModel
            )
        }

        composable(Screen.Deductions.route) {
            DeductionsScreen(
                navController = navController,
                deductionViewModel = deductionViewModel,
                incomeViewModel = incomeViewModel,
                resultViewModel = resultViewModel
            )
        }

        composable(Screen.Result.route) {
            val selectedYear by homeViewModel.selectedFinancialYear.collectAsState()
            val incomeInputs = incomeViewModel.getTaxInputs()
            val finalInputs = deductionViewModel.applyDeductions(incomeInputs)

            ResultScreen(
                navController = navController,
                inputs = finalInputs,
                financialYear = selectedYear,
                resultViewModel = resultViewModel,
                billingViewModel = billingViewModel,
                profileViewModel = profileViewModel
            )
        }

        composable(Screen.Profiles.route) {
            ProfilesScreen(
                navController = navController,
                profileViewModel = profileViewModel,
                onProfileSelected = { profile ->
                    val inputs = profileViewModel.loadProfile(profile)
                    incomeViewModel.setInputs(inputs)
                    deductionViewModel.setInputs(inputs)
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Export.route) {
            ExportScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Paywall.route) {
            PaywallScreen(
                navController = navController,
                billingViewModel = billingViewModel
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
