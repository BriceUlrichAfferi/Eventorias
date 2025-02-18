package com.example.eventorias

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.eventorias.presentation.sign_in.EmailAuthClient
import com.example.eventorias.presentation.sign_in.GoogleAuthUiClient
import com.example.eventorias.ui.theme.EventoriasTheme
import com.google.android.gms.auth.api.identity.Identity
import com.example.eventorias.presentation.botomNavigation.EventoriasBottomNavigation
import com.example.eventorias.routes.appNavigation

class MainActivity : ComponentActivity() {

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    private val emailAuthClient by lazy { EmailAuthClient() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            EventoriasTheme {
                val navController = rememberNavController()
                val currentDestination by navController.currentBackStackEntryAsState()

                // Show the bottom bar for specific routes
                val showBottomBar = when (currentDestination?.destination?.route) {
                    "event_list", "profile" -> true
                    else -> false
                }

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            EventoriasBottomNavigation(
                                currentRoute = currentDestination?.destination?.route,
                                onEventClick = { navController.navigate("event_list") },
                                onProfileClick = { navController.navigate("profile") }
                            )
                        }
                    }
                ) { paddingValues ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = "sign_in"
                        ) {
                            appNavigation(
                                navController = navController,
                                googleAuthUiClient = googleAuthUiClient,
                                emailAuthClient = emailAuthClient,
                                lifecycleScope = lifecycleScope
                            )
                        }
                    }
                }
            }
        }
    }
}
