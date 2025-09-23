package com.example.personalfinanceapp.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.personalfinanceapp.auth.AuthViewModel

@Composable
fun DashboardScreen(
    authViewModel: AuthViewModel,
    onLogout: () -> Unit,
    onNavigateToAddTransaction: () -> Unit
) {
    val userName = authViewModel.user.collectAsState().value?.displayName ?: "User"

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddTransaction) {
                Icon(Icons.Filled.Add, contentDescription = "Add Transaction")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.Start
        ) {
            //Hello User
            Text(
                text = "Hello, $userName!",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Main App Title/Header Text
            Text(
                text = "Dashboard",
                style = MaterialTheme.typography.headlineMedium,
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

        }
    }
}