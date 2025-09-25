package com.example.personalfinanceapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.personalfinanceapp.auth.AuthViewModel
import com.example.personalfinanceapp.auth.LoginScreen
import com.example.personalfinanceapp.auth.SignupScreen
import com.example.personalfinanceapp.auth.WelcomeScreen
import com.example.personalfinanceapp.navigation.AppMainScreen
import com.example.personalfinanceapp.transaction.TransactionViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.example.personalfinanceapp.ui.theme.PersonalFinanceAppTheme


class MainActivity : ComponentActivity() {

    private val auth = FirebaseAuth.getInstance()
    private lateinit var authViewModel: AuthViewModel
    private lateinit var googleSignInClient: GoogleSignInClient

    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)

            Log.d("GoogleSignIn", "Account Received: ${account.email}")

            val idToken = account?.idToken

            if (idToken != null) {
                Log.d("GoogleSignIn", "ID Token received successfully -> Calling ViewModel: $idToken")
                authViewModel.loginWithGoogle(idToken)
            } else {
                Log.e("GoogleSignIn", "Google sign-in succeeded but ID Token was null.")
            }
        } catch (e: ApiException) {
            Log.e("GoogleSignIn", "Google sign in failed with code: ${e.statusCode}", e)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authViewModel = AuthViewModel()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            PersonalFinanceAppTheme {
                PersonalFinanceApp(authViewModel, onGoogleSignIn = {
                    val signInIntent = googleSignInClient.signInIntent
                    launcher.launch(signInIntent)
                })
            }

        }
    }
}

@Composable
fun PersonalFinanceApp(authViewModel: AuthViewModel, onGoogleSignIn: () -> Unit) {
    //how we navigate the pages
    val navController = rememberNavController()
    val userState by authViewModel.user.collectAsState()

    //Defines all the screens
    NavHost(
        navController = navController,
        startDestination = if (userState != null) "app_main_screen" else "welcome"
    ) {
        composable("welcome") {
            WelcomeScreen(
                onNavigateToLogin = { navController.navigate("login") },
                onNavigateToSignup = { navController.navigate("signup") }
            )
        }

        composable("login") {
            LoginScreen(
                authViewModel = authViewModel,
                onGoogleSignIn = onGoogleSignIn,
                onNavigateToSignup = { navController.navigate("signup") },
                onLoginSuccess = { navController.navigate("app_main_screen") { popUpTo("login") { inclusive = true } } },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("signup") {
            val transactionViewModel: TransactionViewModel = viewModel()
            SignupScreen(
                authViewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate("app_main_screen") {
                        popUpTo("signup") { inclusive = true }
                    }
                },
                onUserCreated = { transactionViewModel.addDefaultCategoriesOnSignUp() },
                onNavigateToLogin = { navController.navigate("login") },
                onGoogleSignIn = onGoogleSignIn,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("app_main_screen") {
            AppMainScreen(
                authViewModel = authViewModel,
                onLogout = { navController.navigate("login") { popUpTo("app_main_screen") { inclusive = true } } }
            )
        }
    }
}
