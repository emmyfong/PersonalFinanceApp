package com.example.personalfinanceapp.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.personalfinanceapp.auth.AuthViewModel

@Composable
fun DashboardScreen(
    authViewModel: AuthViewModel,
    onLogout: () -> Unit
) {
    Scaffold { paddingValues ->
        // Use a Box or Column to fill the screen and apply the content padding
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Applies padding consumed by system bars/ime
                .padding(horizontal = 24.dp, vertical = 32.dp), // Add structural padding
            horizontalAlignment = Alignment.Start
        ) {
            // Main App Title/Header Text
            Text(
                text = "Dashboard",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )

            // Welcome/Status Text
            Text(
                text = "Welcome! You're logged in 🎉",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 16.dp)
            )

            // Logout Button
            Button(
                onClick = {
                    authViewModel.logout()
                    onLogout()
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Logout")
            }

            // ... Your future dashboard widgets will go here
        }
    }
}