package com.example.eventorias

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.eventorias.model.Event
import com.example.eventorias.presentation.ProfileScreen
import com.example.eventorias.presentation.event.EventDetailsScreen
import com.example.eventorias.presentation.event.EventListScreen
import com.example.eventorias.presentation.sign_in.EmailAuthClient
import com.example.eventorias.presentation.sign_in.EmailSignInScreen
import com.example.eventorias.presentation.sign_in.GoogleAuthUiClient
import com.example.eventorias.presentation.sign_in.SignInScreen
import com.example.eventorias.presentation.sign_in.SignInViewModel
import com.example.eventorias.presentation.sign_in.Userdata
import com.example.eventorias.ui.theme.EventoriasTheme
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch
import com.example.eventorias.presentation.botomNavigation.EventoriasBottomNavigation
import com.example.eventorias.presentation.botomNavigation.email_log_in.LoginScreen
import com.example.eventorias.presentation.botomNavigation.email_log_in.PasswordRecoveryScreen
import com.example.eventorias.presentation.email_sign_up.SignUpScreen
import com.example.eventorias.presentation.event.CreateEventScreen

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
                val navController = rememberNavController()
                val currentDestination by navController.currentBackStackEntryAsState()

                // Show the bottom bar for the current route
                val showBottomBar = when (currentDestination?.destination?.route) {
                    "event_list", "profile" -> true
                    else -> false
                }

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            EventoriasBottomNavigation(
                                currentRoute = currentDestination?.destination?.route,

                                onEventClick = {
                                    navController.navigate("event_list")
                                },
                                onProfileClick = {
                                    navController.navigate("profile")
                                }

                            )
                        }
                    }
                ) { paddingValues ->

                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        color = MaterialTheme.colorScheme.background,
                    ) {
                        NavHost(navController = navController, startDestination = "sign_in") {
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
                                                val signInResult = googleAuthUiClient.signInWithIntent(intent = result.data ?: return@launch)
                                                Log.d("SignIn", "Google Sign-In Result: $signInResult")
                                                viewModel.onSignInResult(signInResult)
                                                if (signInResult.data != null) {
                                                    Log.d("SignIn", "Navigating to profile")
                                                    navController.navigate("event_list") {
                                                        popUpTo(navController.graph.startDestinationId)
                                                        launchSingleTop = true
                                                    }
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
                                    EmailSignInScreen(
                                        onLogInClick = {
                                            navController.navigate("log_in") {
                                                // Clears elements from the back stack

                                                popUpTo("event_list") { inclusive = false }
                                            }
                                        },
                                        onSignUpClick = {
                                            navController.navigate("sign_up") {
                                                popUpTo("event_list") { inclusive = false }
                                            }
                                        },
                                        navController = navController
                                    )
                                }


                            composable("profile") {
                                ProfileScreen(
                                    userdata = googleAuthUiClient.getSignedInUser()
                                        ?: emailAuthClient.getSignedInUser(),
                                    googleAuthUiClient = googleAuthUiClient, // Pass googleAuthUiClient here
                                    onSignOut = {
                                        lifecycleScope.launch {
                                            googleAuthUiClient.signOut()
                                            emailAuthClient.signOut()
                                            Toast.makeText(
                                                applicationContext,
                                                "Signed out",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            navController.navigate("sign_in") {
                                                popUpTo(0) { inclusive = true }
                                            }
                                        }
                                    }
                                )
                            }


                            // Route for EventScreen
                            composable("create_event") {
                                val context = LocalContext.current
                                CreateEventScreen(
                                    onBackClick = { navController.popBackStack() },
                                    onSaveClick = {
                                        Toast.makeText(
                                            context,
                                            "Event saved successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        navController.popBackStack()
                                    }
                                )
                            }

                            composable("event_list") {
                                val context = LocalContext.current
                                val users =
                                    rememberSaveable { mutableMapOf<String, Userdata>() }

                                val onEventClick: (Event) -> Unit = { event ->
                                    try {
                                        navController.navigate("event_details/${event.id}/${event.category}")
                                    } catch (e: Exception) {
                                        Toast.makeText(
                                            context,
                                            "Failed to navigate: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                                EventListScreen(
                                    modifier = Modifier,
                                    users = users,
                                    onFABClick = {
                                        navController.navigate("create_event")
                                    },
                                    onEventClick = onEventClick,
                                    context = context
                                )
                            }

                            composable("event_details/{eventId}/{eventCategory}") { backStackEntry ->
                                val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
                                val eventCategory =
                                    backStackEntry.arguments?.getString("eventCategory")
                                        ?: "Event Details"

                                EventDetailsScreen(
                                    eventId = eventId,
                                    eventCategory = eventCategory,
                                    onBackClick = { navController.popBackStack() }
                                )
                            }
                            // Route for Sign Up Screen
                            composable("sign_up") {
                                SignUpScreen(
                                    onLoginSuccess = {
                                        navController.navigate("event_list") {
                                            // Clears 'sign_up' from the back stack
                                            popUpTo("sign_up") { inclusive = true }
                                        }
                                    },
                                      navController = navController
                                )
                            }

                            composable("log_in") {
                                LoginScreen(
                                    navController = navController
                                )
                            }

                            composable("password_recovery") {
                                PasswordRecoveryScreen(
                                    navController = navController
                                )
                            }

                        }
                    }
                }
            }
        }
    }

}