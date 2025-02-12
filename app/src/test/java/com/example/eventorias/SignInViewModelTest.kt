package com.example.eventorias

import com.example.eventorias.presentation.sign_in.SignInResult
import com.example.eventorias.presentation.sign_in.SignInViewModel
import com.example.eventorias.presentation.sign_in.Userdata
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking

import kotlinx.coroutines.flow.first
import org.junit.Before
import org.junit.Test

class SignInViewModelTest {

    private lateinit var viewModel: SignInViewModel

    @Before
    fun setUp() {
        viewModel = SignInViewModel()
    }

    @Test
    fun `onSignInResult updates state for successful sign-in`() = runBlocking {
        // Arrange
        val mockResult = SignInResult(
            data = Userdata(
                userId = "1",
                userName = "Brice Ulrich",
                email = "brico.dj@yahoo.com",
                profilePictureUrl = "http://example.com/profile.jpg",
                photoUrl = "http://example.com/photo.jpg"
            ),
            errorMessage = null
        )

        // Act
        viewModel.onSignInResult(mockResult)

        val state = viewModel.state.first()
        assertTrue(state.isSignInSuccessful)
        assertNull(state.signInError)
        assertNotNull(state.userData)
    }

    @Test
    fun `onSignInResult updates state for failed sign-in`() = runBlocking {
        // Arrange
        val mockResult = SignInResult(
            data = null,
            errorMessage = "Invalid credentials"
        )

        // Act
        viewModel.onSignInResult(mockResult)

        // Assert: Check the updated state for failed sign-in
        val state = viewModel.state.first()
        assertFalse(state.isSignInSuccessful)
        assertEquals("Invalid credentials", state.signInError)
        assertNull(state.userData)
    }

    @Test
    fun `onSignInSuccess updates state with user data`() = runBlocking {
        // Arrange
        val userData = Userdata(
            userId = "1",
            userName = "Jody Choco",
            email = "jody.choco@example.com",
            profilePictureUrl = "http://example.com/profile.jpg",
            photoUrl = "http://example.com/photo.jpg"
        )

        // Act
        viewModel.onSignInSuccess(userData)

    // Assert
        val state = viewModel.state.first()
        assertTrue(state.isSignInSuccessful)
        assertEquals(userData, state.userData)
    }

    @Test
    fun `resetState resets the state`() = runBlocking {
        // Arrange
        val mockResult = SignInResult(
            data = Userdata(
                userId = "1",
                userName = "Ulrich Fabrice",
                email = "fabrice.Ul@example.com",
                profilePictureUrl = "http://example.com/profile.jpg",
                photoUrl = "http://example.com/photo.jpg"
            ),
            errorMessage = null
        )
        viewModel.onSignInResult(mockResult)

        // Act
        viewModel.resetState()

        // Assert
        val state = viewModel.state.first()
        assertFalse(state.isSignInSuccessful)
        assertNull(state.signInError)
        assertNull(state.userData)
    }
}
