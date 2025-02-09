package com.example.eventorias.presentation.sign_in

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await

class EmailAuthClient {
    private val auth = Firebase.auth

    suspend fun signIn(email: String, password: String): SignInResult {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            SignInResult(
                data = result.user?.run {
                    Userdata(
                        userId = uid,
                        userName = displayName ?: "No Name",
                        email = email,
                        profilePictureUrl = photoUrl?.toString(),
                        photoUrl = photoUrl?.toString()
                    )
                },
                errorMessage = null
            )
        } catch (e: FirebaseAuthException) {
            Log.e("EmailSignInError", "Sign-in failed: ${e.localizedMessage}", e)
            SignInResult(
                data = null,
                errorMessage = when (e.errorCode) {
                    "ERROR_USER_NOT_FOUND" -> "User not found"
                    "ERROR_WRONG_PASSWORD" -> "Wrong password"
                    else -> "Sign in failed: ${e.localizedMessage}"
                }
            )
        }
    }

    suspend fun createUser(email: String, password: String): SignInResult {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            SignInResult(
                data = result.user?.run {
                    Userdata(
                        userId = uid,
                        userName = email, // Initially set username to email
                        email = email,
                        profilePictureUrl = null, // No profile picture at creation
                        photoUrl = photoUrl?.toString())
                },
                errorMessage = null
            )
        } catch (e: FirebaseAuthException) {
            Log.e("EmailSignUpError", "Sign-up failed: ${e.localizedMessage}", e)
            SignInResult(
                data = null,
                errorMessage = when (e.errorCode) {
                    "ERROR_EMAIL_ALREADY_IN_USE" -> "Email already in use"
                    "ERROR_WEAK_PASSWORD" -> "Weak password"
                    "ERROR_INVALID_EMAIL" -> "Invalid email"
                    else -> "Sign up failed: ${e.localizedMessage}"
                }
            )
        }
    }

    fun signOut() {
        auth.signOut()
    }

    fun getSignedInUser(): Userdata? = auth.currentUser?.run {
        email?.let {
            Userdata(
                userId = uid,
                userName = displayName ?: email ?: "No Name",
                email = it,
                profilePictureUrl = photoUrl?.toString(),
                photoUrl = photoUrl?.toString()
            )
        }
    }
}