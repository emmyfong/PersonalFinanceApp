package com.example.personalfinanceapp.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

//holds state for the ui and calls the AuthRepository
class AuthViewModel(
    private val repo: AuthRepository = AuthRepository()
): ViewModel() {

    private val _user = MutableStateFlow<FirebaseUser?>(repo.currentUser)
    val user: StateFlow<FirebaseUser?> = _user

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val result = repo.login(email, password)
                _user.value = result
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun signUp(name: String, email: String, password: String, onUserCreated: () -> Unit){
        viewModelScope.launch {
            try {
                val result = repo.signUp(name, email, password)
                _user.value = result

                if (result != null) {
                    onUserCreated()
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            try {
                val result = repo.loginWithGoogle(idToken)
                _user.value = result
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun logout(){
        repo.logout()
        _user.value = null
    }
}