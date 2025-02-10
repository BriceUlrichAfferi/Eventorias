package com.example.eventorias.presentation.botomNavigation.email_log_in

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eventorias.R
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordRecoveryScreen(
    navController: NavController
) {
    val email = remember { mutableStateOf(TextFieldValue("")) }
    val emailError = remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(
                    text = stringResource(id = R.string.password_recovery),
                    color = Color.White

                ) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.contentDescription_go_back),
                            tint = Color.White

                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.password_recovery_label),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(32.dp))

            TextField(
                value = email.value,
                onValueChange = { email.value = it },
                label = { Text(
                    text = stringResource(id = R.string.email),
                    color = Color.White

                ) },
                isError = emailError.value != null,
                modifier = Modifier.fillMaxWidth()
            )
            emailError.value?.let {
                Text(text = it, color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    emailError.value = if (email.value.text.isBlank()) "Email cannot be empty" else null

                    if (emailError.value == null) {
                        // Send password reset email
                        auth.sendPasswordResetEmail(email.value.text)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    showDialog = true
                                } else {
                                    task.exception?.let {
                                        Log.e("PasswordRecovery", "Error sending reset email: ${it.message}")
                                        if (it.message?.contains("no user record", ignoreCase = true) == true) {
                                            emailError.value = "No account found with this email address."
                                        } else {
                                            emailError.value = "Failed to send reset email. Error: ${it.message}"
                                        }
                                    }
                                }
                            }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Send Reset Email", color = Color.White)
            }

            // Show dialog when reset email is sent successfully
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = {
                        showDialog = false
                        navController.navigateUp()
                    },
                    title = { Text("Password Reset", color = Color.White
                    ) },
                    text = {
                        Text(
                            text = stringResource(id = R.string.password_recovery_message, email.value.text),
                            color = Color.White

                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDialog = false
                                navController.navigateUp()
                            }
                        ) {
                            Text("OK", color = Color.White
                            )
                        }
                    }
                )
            }
        }
    }
}