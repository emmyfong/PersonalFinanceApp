package com.example.personalfinanceapp.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

//Navigation Routes

sealed class Screen(val route: String, val icon: ImageVector, val label: String) {
    object Dashboard : Screen("dashboard", Icons.Filled.Home, "Dashboard")
    object Transactions : Screen("transactions", Icons.Filled.List, "Transactions")
    object Settings : Screen("settings", Icons.Filled.Settings, "Settings")

    //Auth routes
    object Login : Screen("login", Icons.Filled.Lock, "Login")
    object Signup : Screen("signup", Icons.Filled.Person, "Signup")
}

val NavItems = listOf(
    Screen.Transactions,
    Screen.Dashboard,
    Screen.Settings
)