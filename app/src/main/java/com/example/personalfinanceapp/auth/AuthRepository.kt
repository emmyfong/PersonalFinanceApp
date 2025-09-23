package com.example.personalfinanceapp.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await

//firebase calls
// controls all firebase calls so ui doesn't directly do it
class AuthRepository (private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()){
    val currentUser: FirebaseUser? get() = firebaseAuth.currentUser

    suspend fun signUp(name: String, email: String, password: String): FirebaseUser? {
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user

        //add name to user profile
        user?.let { firebaseUse ->
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
            firebaseUse.updateProfile(profileUpdates).await()
        }
        return user
    }

    suspend fun login(email: String, password: String): FirebaseUser? {
        val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        return result.user
    }

    //login with google
    suspend fun loginWithGoogle(idToken: String): FirebaseUser? {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = firebaseAuth.signInWithCredential(credential).await()
        return result.user
    }

    fun logout(){
        firebaseAuth.signOut()
    }
}