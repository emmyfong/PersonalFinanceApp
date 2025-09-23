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
fun SignupScreen(
    authViewModel: AuthViewModel = viewModel(),
    onLoginSuccess: () -> Unit,
    onGoogleSignIn: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateBack: () -> Unit
){
    val user by authViewModel.user.collectAsState()
    val error by authViewModel.error.collectAsState()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    if (user != null) {
        onLoginSuccess()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Black)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onNavigateBack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Back", color = White, style = MaterialTheme.typography.titleMedium)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        color = White,
                        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)
                    )
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Welcome Section (Top Text)
                Text(
                    "Create Your Account",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Black
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    "Ready to start managing your finances?",
                    style = MaterialTheme.typography.bodyLarge.copy(color = SubText),
                    modifier = Modifier.padding(bottom = 48.dp)
                )

                // --- Inputs ---

                // Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Enter full name") },
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
                    label = { Text("Enter password") },
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

                // Sign Up Button (Uses Theme Primary: Black background, White text)
                Button(
                    onClick = { authViewModel.signUp(name, email, password) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary, // Black
                        contentColor = MaterialTheme.colorScheme.onPrimary  // White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Get Started", fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(32.dp))

                // --- Social Logins ---
                Text("— Sign up with —", color = SubText, modifier = Modifier.padding(bottom = 16.dp))

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

                // Navigate to Login
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Already have an account? ",
                        color = SubText
                    )
                    TextButton(
                        onClick = { onNavigateToLogin() },
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier
                            .height(IntrinsicSize.Min)
                            .padding(start = 2.dp)
                    ) {
                        Text(
                            "Login",
                            color = Black,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge
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