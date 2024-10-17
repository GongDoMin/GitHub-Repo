package com.prac.githubrepo.login

import com.prac.data.fake.repository.FakeTokenRepository
import com.prac.githubrepo.constants.CONNECTION_FAIL
import com.prac.githubrepo.constants.LOGIN_FAIL
import com.prac.githubrepo.util.StandardTestDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val standardTestDispatcherRule = StandardTestDispatcherRule()

    private lateinit var tokenRepository: FakeTokenRepository

    private lateinit var loginViewModel: LoginViewModel

    private val code = "code"

    @Test
    fun checkAutoLogin_userIsLoggedIn_eventSuccess() = runTest {
        tokenRepository = FakeTokenRepository()
        tokenRepository.setInitialToken()
        loginViewModel = LoginViewModel(tokenRepository, standardTestDispatcherRule.testDispatcher)

        val result = loginViewModel.event.first()

        assertTrue(result is LoginViewModel.Event.Success)
    }

    @Test
    fun checkAutoLogin_userIsNotLoggedIn() = runTest {
        tokenRepository = FakeTokenRepository()
        loginViewModel = LoginViewModel(tokenRepository, standardTestDispatcherRule.testDispatcher)

        // if result is false, loginViewModel.event is not emitted
        val result = tokenRepository.isLoggedIn()

        assertFalse(result)
    }

    @Test
    fun loginWithGitHub_updateSuccessEvent() = runTest {
        val code = "testCode"
        tokenRepository = FakeTokenRepository()
        loginViewModel = LoginViewModel(tokenRepository, standardTestDispatcherRule.testDispatcher)

        loginViewModel.loginWithGitHub(code)

        val event = loginViewModel.event.first()
        assertTrue(event is LoginViewModel.Event.Success)
    }

    @Test
    fun loginWithGitHub_networkError_updateUiStateToError() = runTest {
        tokenRepository = FakeTokenRepository()
        tokenRepository.setThrowable(IOException())
        loginViewModel = LoginViewModel(tokenRepository, standardTestDispatcherRule.testDispatcher)

        loginViewModel.loginWithGitHub(code)
        advanceUntilIdle()

        val uiState = loginViewModel.uiState.value
        assertTrue(uiState is LoginViewModel.UiState.Error)
        assertEquals((uiState as LoginViewModel.UiState.Error).errorMessage, CONNECTION_FAIL)
    }

    @Test
    fun loginWithGitHub_authorizationError_updateUiStateToError() = runTest {
        tokenRepository = FakeTokenRepository()
        tokenRepository.setThrowable(Exception())
        loginViewModel = LoginViewModel(tokenRepository, standardTestDispatcherRule.testDispatcher)

        loginViewModel.loginWithGitHub(code)
        advanceUntilIdle()

        val uiState = loginViewModel.uiState.value
        assertTrue(uiState is LoginViewModel.UiState.Error)
        assertEquals((uiState as LoginViewModel.UiState.Error).errorMessage, LOGIN_FAIL)
    }
}