package com.example.personalfinanceapp.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.personalfinanceapp.R
import com.example.personalfinanceapp.ui.theme.Blue
import com.example.personalfinanceapp.ui.theme.Mint
import com.example.personalfinanceapp.ui.theme.Black
import com.example.personalfinanceapp.ui.theme.SubText
import com.example.personalfinanceapp.ui.theme.White

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel = viewModel(),
    onLoginSuccess: () -> Unit,
    onNavigateToSignup: () -> Unit,
    onGoogleSignIn: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val user by authViewModel.user.collectAsState()
    val error by authViewModel.error.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    if (user != null) {
        onLoginSuccess()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // If you have a back navigation available
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Back", color = MaterialTheme.colorScheme.secondary, style = MaterialTheme.typography.titleMedium)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        color = MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)
                    )
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Welcome Section
                Text(
                    "Welcome Back",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    "Your path is right here.",
                    style = MaterialTheme.typography.bodyLarge.copy(color = SubText),
                    modifier = Modifier.padding(bottom = 48.dp)
                )

                // --- Inputs ---

                // Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Enter email") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Black,
                        unfocusedTextColor = Black,
                        focusedBorderColor = Black,
                        unfocusedBorderColor = SubText,
                        cursorColor = Black,
                        focusedLabelColor = Black,
                        unfocusedLabelColor = SubText
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Password
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Black,
                        unfocusedTextColor = Black,
                        focusedBorderColor = Black,
                        unfocusedBorderColor = SubText,
                        cursorColor = Black,
                        focusedLabelColor = Black,
                        unfocusedLabelColor = SubText
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(32.dp))

                // Login Button
                Button(
                    onClick = { authViewModel.login(email, password) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Log In", fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(32.dp))

                // --- Social Logins ---
                Text("— Sign in with —", color = SubText, modifier = Modifier.padding(bottom = 16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Google Button
                    Button(
                        onClick = onGoogleSignIn,
                        shape = CircleShape,
                        modifier = Modifier.size(64.dp).shadow(
                            elevation = 8.dp,
                            shape = CircleShape,
                            ambientColor = Color.Black.copy(alpha = 0.1f),
                            spotColor = Color.Black.copy(alpha = 0.8f)
                        ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        ),
                        contentPadding = PaddingValues(12.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.googlelogo),
                            contentDescription = "Google logo",
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                }

                Spacer(Modifier.height(32.dp))

                // Navigate to Sign Up
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Don't have an account? ",
                        color = SubText
                    )
                    TextButton(
                        onClick = { onNavigateToSignup() },
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.height(IntrinsicSize.Min)
                    ) {
                        Text(
                            "Sign Up",
                            color = Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Error Message
                error?.let {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}