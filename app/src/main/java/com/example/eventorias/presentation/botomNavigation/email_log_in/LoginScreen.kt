package com.example.eventorias.presentation.botomNavigation.email_log_in

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eventorias.R
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController
) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val emailError = remember { mutableStateOf<String?>(null) }
    val passwordError = remember { mutableStateOf<String?>(null) }
    val currentStep = remember { mutableStateOf(1) } // Step 1 for email, Step 2 for password
    val auth = FirebaseAuth.getInstance()
    var passwordVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(
                    text = stringResource(id = R.string.sign_in),
                    color = Color.White
                ) },
                navigationIcon = {
                    IconButton(
                        onClick = {
                        if (currentStep.value > 1) {
                            currentStep.value -= 1 // Go back to the previous step
                        } else {
                            navController.navigate("email_sign_in") // Navigate to AccountManagementScreen

                        }
                    }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.contentDescription_go_back),
                            tint= Color.White

                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.eventorias777ff),
                contentDescription = "App Icon",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(27f / 25f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Step 1: Email input
            if (currentStep.value == 1) {
                TextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    label = { Text("Email Address") },
                    isError = emailError.value != null,
                    modifier = Modifier.fillMaxWidth()
                )
                emailError.value?.let {
                    Text(text = it, color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        emailError.value = when {
                            email.value.isBlank() -> "Email cannot be empty"
                            !isValidEmail(email.value) -> "Email not valid"
                            else -> null
                        }
                        if (emailError.value == null) {
                            currentStep.value = 2 // Proceed to password input step
                        }
                    },
                    modifier = Modifier
                    .padding(16.dp, bottom = 16.dp)
                    .width(150.dp)
                    .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    shape = RectangleShape,
                ) {
                    Text("Next", color = Color.White)
                }
            }

            // Step 2: Password input
            if (currentStep.value == 2) {
                // Display the welcome back message
                // Display the welcome back message with bold email
                val welcomeBackText = if (email.value.isNotBlank()) {
                    buildAnnotatedString {
                        val welcomeText = stringResource(R.string.sign_in, email.value)
                        append(welcomeText)
                        if (welcomeText.contains(email.value)) {
                            val start = welcomeText.indexOf(email.value)
                            val end = start + email.value.length
                            if (end <= welcomeText.length) {
                                addStyle(
                                    style = SpanStyle(fontWeight = FontWeight.Bold),
                                    start = start,
                                    end = end
                                )
                            }
                        }
                    }
                } else {
                    stringResource(R.string.sign_in, "")
                }


                Spacer(modifier = Modifier.height(16.dp))

                // Password input with visibility toggle
                TextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    label = { Text("Password") },
                    isError = passwordError.value != null,
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        val description = if (passwordVisible) "Hide password" else "Show password"
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image,
                                contentDescription = description,
                                tint = Color.Black)
                        }
                    }
                )
                passwordError.value?.let {
                    Text(text = it, color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Sign In button
                Button(
                    onClick = {
                        // Validate email and password
                        passwordError.value = if (password.value.isBlank()) "Password cannot be empty" else null

                        if (emailError.value == null && passwordError.value == null) {
                            auth.signInWithEmailAndPassword(email.value, password.value)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        navController.navigate("event_list") {
                                            popUpTo(0) { inclusive = true } // Clears the entire back stack
                                        }


                                        Toast.makeText(navController.context, "Sign in successful", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(navController.context, "Authentication failed", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                    },
                    modifier = Modifier
                        .padding(16.dp, bottom = 16.dp)
                        .width(150.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    shape = RectangleShape,
                ) {
                    Text(text = stringResource(id = R.string.sign_in), color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Forgot Password clickable text
                TextButton(onClick = {
                    navController.navigate("password_recovery")
                }) {
                    Text("Trouble signing in?", color = Color.White)
                }
            }
        }
    }
}

fun isValidEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$".toRegex()
    return emailRegex.matches(email)
}
