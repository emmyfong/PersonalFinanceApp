// AppMainScreen.kt

package com.example.personalfinanceapp.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.personalfinanceapp.auth.AuthViewModel
import com.example.personalfinanceapp.dashboard.DashboardScreen
import com.example.personalfinanceapp.transaction.TransactionsScreen
import com.example.personalfinanceapp.transaction.AddTransactionScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppMainScreen(
    authViewModel: AuthViewModel,
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
                DashboardScreen(authViewModel = authViewModel,
                    onLogout = onLogout,
                    onNavigateToAddTransaction = { navHostController.navigate("add_transaction") })
            }
            composable(Screen.Transactions.route) {
                TransactionsScreen(
                    onNavigateToAddTransaction = { navHostController.navigate("add_transaction") },
                    onNavigateToManageCategories = { navHostController.navigate("manage_categories") }
                )
            }
            composable(Screen.Settings.route) {
                // TODO: Add SettingsScreen composable here
                // SettingsScreen()
            }
            composable("add_transaction") {
                AddTransactionScreen (
                    onTransactionAdded = {navHostController.popBackStack()}
                )
            }
        }
    }
}