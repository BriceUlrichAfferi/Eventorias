package com.example.eventorias

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eventorias.presentation.ProfileScreen
import com.example.eventorias.presentation.event.EventScreen
import com.example.eventorias.presentation.sign_in.EmailAuthClient
import com.example.eventorias.presentation.sign_in.EmailSignInScreen
import com.example.eventorias.presentation.sign_in.GoogleAuthUiClient
import com.example.eventorias.presentation.sign_in.SignInScreen
import com.example.eventorias.presentation.sign_in.SignInViewModel
import com.example.eventorias.ui.theme.EventoriasTheme
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    private val emailAuthClient by lazy {
        EmailAuthClient()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EventoriasTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "create_event") {
                        composable("sign_in") {
                            val viewModel = viewModel<SignInViewModel>()
                            val state by viewModel.state.collectAsStateWithLifecycle()

                            SignInScreen(
                                state = state,
                                onGoogleSignInClick = {
                                    navController.navigate("google_sign_in")
                                },
                                onEmailSignInClick = {
                                    navController.navigate("email_sign_in")
                                }
                            )
                        }

                        composable("google_sign_in") {
                            val viewModel = viewModel<SignInViewModel>()
                            val state by viewModel.state.collectAsStateWithLifecycle()

                            val googleSignInLauncher = rememberLauncherForActivityResult(
                                contract = ActivityResultContracts.StartIntentSenderForResult(),
                                onResult = { result ->
                                    if (result.resultCode == RESULT_OK) {
                                        lifecycleScope.launch {
                                            val signInResult = googleAuthUiClient.signInWithIntent(
                                                intent = result.data ?: return@launch
                                            )
                                            viewModel.onSignInResult(signInResult)
                                            if (signInResult.data != null) {
                                                navController.navigate("profile")
                                            }
                                        }
                                    }
                                }
                            )

                            LaunchedEffect(key1 = Unit) {
                                val intentSender = googleAuthUiClient.signIn()
                                if (intentSender != null) {
                                    googleSignInLauncher.launch(
                                        IntentSenderRequest.Builder(intentSender).build()
                                    )
                                }
                            }
                        }

                        composable("email_sign_in") {
                            var email by remember { mutableStateOf("") }
                            var password by remember { mutableStateOf("") }
                            val context = LocalContext.current
                            val viewModel = viewModel<SignInViewModel>()

                            EmailSignInScreen(
                                email = email,
                                password = password,
                                onEmailChange = { email = it },
                                onPasswordChange = { password = it },
                                onSignInClick = {
                                    lifecycleScope.launch {
                                        val signInResult = emailAuthClient.signIn(email, password)
                                        viewModel.onSignInResult(signInResult)
                                        if (signInResult.data != null) {
                                            navController.navigate("profile")
                                        } else {
                                            Toast.makeText(context, signInResult.errorMessage, Toast.LENGTH_LONG).show()
                                        }
                                    }
                                },
                                onSignUpClick = {
                                    lifecycleScope.launch {
                                        val signUpResult = emailAuthClient.createUser(email, password)
                                        viewModel.onSignInResult(signUpResult)
                                        if (signUpResult.data != null) {
                                            navController.navigate("profile")
                                        } else {
                                            Toast.makeText(context, signUpResult.errorMessage, Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }
                            )
                        }

                        composable("profile") {
                            ProfileScreen(
                                userdata = googleAuthUiClient.getSignedInUser() ?: emailAuthClient.getSignedInUser(),
                                onSignOut = {
                                    lifecycleScope.launch {
                                        googleAuthUiClient.signOut()
                                        emailAuthClient.signOut()
                                        Toast.makeText(
                                            applicationContext,
                                            "Signed out",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        navController.popBackStack("sign_in", false)
                                    }
                                }
                            )
                        }

                        // New route for EventScreen
                        composable("create_event") {
                            val context = LocalContext.current // Move this here
                            EventScreen(
                                onBackClick = { navController.popBackStack() },
                                onSaveClick = {
                                    // Optionally, show success or navigate back
                                    Toast.makeText(
                                        context, // Use the context remembered here
                                        "Event saved successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.popBackStack()
                                }
                            )
                        }

                    }
                }
            }
        }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EventoriasTheme {
        Greeting("Android")
    }
}