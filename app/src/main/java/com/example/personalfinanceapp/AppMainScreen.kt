// AppMainScreen.kt

package com.example.personalfinanceapp.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.personalfinanceapp.auth.AuthViewModel
import com.example.personalfinanceapp.dashboard.DashboardScreen
import com.example.personalfinanceapp.dashboard.DashboardViewModel
import com.example.personalfinanceapp.transaction.TransactionsScreen
import com.example.personalfinanceapp.transaction.AddTransactionScreen
import com.example.personalfinanceapp.transaction.TransactionViewModel
import com.example.personalfinanceapp.settings.Settings


@Composable
fun AppMainScreen(
    authViewModel: AuthViewModel,
    transactionViewModel: TransactionViewModel,
    dashboardViewModel: DashboardViewModel,
    onLogout: () -> Unit
) {
    val navHostController = rememberNavController()
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Use a Scaffold to define the overall screen layout
    Scaffold(
        bottomBar = {
            // Only show the NavBar if the current destination is one of the main app screens
            if (currentDestination?.route in NavItems.map { it.route }) {
                NavBar(navController = navHostController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navHostController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    authViewModel = authViewModel,
                    transactionViewModel = transactionViewModel,
                    dashboardViewModel = dashboardViewModel,
                )
            }
            composable(Screen.Transactions.route) {
                TransactionsScreen(
                    transactionViewModel = transactionViewModel,
                    onNavigateToAddTransaction = { navHostController.navigate("add_transaction") },
                )
            }
            composable(Screen.Settings.route) {
                Settings(
                    authViewModel = authViewModel,
                    onLogout = onLogout
                )
            }
            composable("add_transaction") {
                AddTransactionScreen(
                    onNavigateBack = { navHostController.popBackStack() },
                    transactionViewModel = transactionViewModel,
                    onTransactionAdded = { navHostController.popBackStack() }
                )
            }
        }
    }
}