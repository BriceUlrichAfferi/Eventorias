package com.example.eventorias.presentation.sign_in

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.core.UserData

data class SignInResult(
    val data: Userdata?,
    val errorMessage: String?
)

data class Userdata(
    val userId: String,
    val userName: String,
    val email: String,
    val profilePictureUrl: String?,
    val photoUrl: String?
)