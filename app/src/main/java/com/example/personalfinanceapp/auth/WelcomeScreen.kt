package com.example.personalfinanceapp.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.personalfinanceapp.ui.theme.Black
import com.example.personalfinanceapp.ui.theme.White
import com.example.personalfinanceapp.ui.theme.Mint

@Composable
fun WelcomeScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToSignup: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    "Welcome to Personal Finance",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = Black
                    ),
                    modifier = Modifier.padding(bottom = 32.dp, start = 8.dp, end = 32.dp)
                )

                Text(
                    "Take control of your money, effortlessly.",
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.headlineMedium.copy(color = Mint),
                    modifier = Modifier.padding(start = 8.dp, end = 32.dp)
                )

                // Placeholder for an image or illustration (optional)
                Spacer(modifier = Modifier.height(16.dp))
                //
            }

            //Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onNavigateToLogin,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(64.dp)
                ) {
                    Text("LOG IN", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }

                Spacer(Modifier.height(32.dp))

                // Sign Up Button
                OutlinedButton(
                    onClick = onNavigateToSignup,
                    border = BorderStroke(1.dp, Black),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Black,
                        containerColor = White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(64.dp)
                ) {
                    Text("CREATE ACCOUNT", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}