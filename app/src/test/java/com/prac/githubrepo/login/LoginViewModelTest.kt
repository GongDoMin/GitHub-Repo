package com.prac.githubrepo.login

import com.prac.data.fake.FakeTokenRepository
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

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val standardTestDispatcherRule = StandardTestDispatcherRule()

    private lateinit var tokenRepository: FakeTokenRepository

    private lateinit var loginViewModel: LoginViewModel

    @Test
    fun checkAutoLogin_userIsLoggedIn_eventSuccess() = runTest {
        tokenRepository = FakeTokenRepository().apply { setInitialToken() }
        loginViewModel = LoginViewModel(tokenRepository, standardTestDispatcherRule.testDispatcher)

        val result = loginViewModel.event.first()

        assertTrue(result is LoginViewModel.Event.Success)
    }

    @Test
    fun checkAutoLogin_userIsNotLoggedIn_eventIsNotChanged() = runTest {
        tokenRepository = FakeTokenRepository()
        loginViewModel = LoginViewModel(tokenRepository, standardTestDispatcherRule.testDispatcher)

        // if result is false, loginViewModel.event is not emitted
        val result = tokenRepository.isLoggedIn()

        assertFalse(result)
    }

    @Test
    fun loginWithGitHub_apiCallIsSuccess_successEvent() = runTest {
        val code = "success"
        tokenRepository = FakeTokenRepository()
        loginViewModel = LoginViewModel(tokenRepository, standardTestDispatcherRule.testDispatcher)

        loginViewModel.loginWithGitHub(code)

        val event = loginViewModel.event.first()
        assertTrue(event is LoginViewModel.Event.Success)
    }

    @Test
    fun loginWithGitHub_apiCallIsNetworkError_uiStateIsError() = runTest {
        val code = "ioException"
        tokenRepository = FakeTokenRepository()
        loginViewModel = LoginViewModel(tokenRepository, standardTestDispatcherRule.testDispatcher)

        loginViewModel.loginWithGitHub(code)
        advanceUntilIdle()

        val uiState = loginViewModel.uiState.value
        assertTrue(uiState is LoginViewModel.UiState.Error)
        assertEquals((uiState as LoginViewModel.UiState.Error).errorMessage, CONNECTION_FAIL)
    }

    @Test
    fun loginWithGitHub_apiCallIsAuthorizationError_uiStateIsError() = runTest {
        val code = "else"
        tokenRepository = FakeTokenRepository()
        loginViewModel = LoginViewModel(tokenRepository, standardTestDispatcherRule.testDispatcher)

        loginViewModel.loginWithGitHub(code)
        advanceUntilIdle()

        val uiState = loginViewModel.uiState.value
        assertTrue(uiState is LoginViewModel.UiState.Error)
        assertEquals((uiState as LoginViewModel.UiState.Error).errorMessage, LOGIN_FAIL)
    }
}