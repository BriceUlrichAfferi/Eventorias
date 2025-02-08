package com.example.eventorias.presentation.sign_in

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SignInViewModel : ViewModel() {

    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    /**
     * Updates the sign-in state based on the result of a sign-in attempt.
     * @param result The result of the sign-in operation.
     */
    fun onSignInResult(result: SignInResult) {
        _state.update { currentState ->
            currentState.copy(
                isSignInSuccessful = result.data != null,
                signInError = result.errorMessage,
                userData = result.data
            )
        }
    }

    /**
     * Resets the sign-in state to its initial values.
     */
    fun resetState() {
        _state.update { SignInState() }
    }

    /**
     * Retrieves the current user data from the state.
     * @return The Userdata if available, otherwise null.
     */
    fun getUserData(): Userdata? = _state.value.userData
}
