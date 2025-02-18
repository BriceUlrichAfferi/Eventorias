package com.example.eventorias.routes


import android.app.Activity.RESULT_OK
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import com.example.eventorias.presentation.ProfileScreen
import com.example.eventorias.presentation.botomNavigation.email_log_in.LoginScreen
import com.example.eventorias.presentation.botomNavigation.email_log_in.PasswordRecoveryScreen
import com.example.eventorias.presentation.email_sign_up.SignUpScreen
import com.example.eventorias.presentation.event.CreateEventScreen
import com.example.eventorias.presentation.event.EventDetailsScreen
import com.example.eventorias.presentation.event.EventListScreen
import com.example.eventorias.presentation.sign_in.EmailAuthClient
import com.example.eventorias.presentation.sign_in.EmailSignInScreen
import com.example.eventorias.presentation.sign_in.GoogleAuthUiClient
import com.example.eventorias.presentation.sign_in.SignInScreen
import com.example.eventorias.presentation.sign_in.SignInViewModel
import com.example.eventorias.presentation.sign_in.Userdata
import kotlinx.coroutines.launch

fun NavGraphBuilder.appNavigation(
    navController: NavController,
    googleAuthUiClient: GoogleAuthUiClient,
    emailAuthClient: EmailAuthClient,
    lifecycleScope: LifecycleCoroutineScope
) {
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
                        val signInResult = googleAuthUiClient.signInWithIntent(result.data ?: return@launch)
                        viewModel.onSignInResult(signInResult)
                        if (signInResult.data != null) {
                            navController.navigate("event_list") {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                    }
                }
            }
        )

        LaunchedEffect(Unit) {
            googleAuthUiClient.signIn()?.let { intentSender ->
                googleSignInLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
            }
        }
    }

    composable("email_sign_in") {
        EmailSignInScreen(
            onLogInClick = { navController.navigate("log_in") },
            onSignUpClick = { navController.navigate("sign_up") },
            navController = navController
        )
    }

    composable("profile") {
        val context = LocalContext.current

        ProfileScreen(
            userdata = googleAuthUiClient.getSignedInUser() ?: emailAuthClient.getSignedInUser(),
            onSignOut = {
                lifecycleScope.launch {
                    googleAuthUiClient.signOut()
                    emailAuthClient.signOut()
                    Toast.makeText(
                        context,
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

    composable("event_list") {
        val context = LocalContext.current
        val users = rememberSaveable { mutableMapOf<String, Userdata>() }

        EventListScreen(
            users = users,
            onFABClick = { navController.navigate("create_event") },
            onEventClick = { event ->
                navController.navigate("event_details/${event.id}/${event.category}")
            },
            context = context
        )
    }

    composable("event_details/{eventId}/{eventCategory}") { backStackEntry ->
        val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
        val eventCategory = backStackEntry.arguments?.getString("eventCategory") ?: "Event Details"

        EventDetailsScreen(
            eventId = eventId,
            eventCategory = eventCategory,
            onBackClick = { navController.popBackStack() }
        )
    }

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

    composable("sign_up") {
        SignUpScreen(
            onLoginSuccess = {
                navController.navigate("event_list") {
                    popUpTo("sign_up") { inclusive = true }
                }
            },
            navController = navController
        )
    }

    composable("log_in") {
        LoginScreen(navController = navController)
    }

    composable("password_recovery") {
        PasswordRecoveryScreen(navController = navController)
    }
}