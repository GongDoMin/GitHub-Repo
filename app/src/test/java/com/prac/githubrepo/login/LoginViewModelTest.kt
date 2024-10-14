package com.prac.githubrepo.login

import com.prac.data.exception.CommonException
import com.prac.data.repository.TokenRepository
import com.prac.githubrepo.constants.CONNECTION_FAIL
import com.prac.githubrepo.main.MainViewModel
import com.prac.githubrepo.util.StandardTestDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest {

    @get:Rule
    val standardTestDispatcherRule = StandardTestDispatcherRule()

    @Mock
    private lateinit var tokenRepository: TokenRepository

    private lateinit var loginViewModel: LoginViewModel

    @Test
    fun checkAutoLogin_userIsLoggedIn_eventSuccess() = runTest {
        whenever(tokenRepository.isLoggedIn()).thenReturn(true)
        loginViewModel = LoginViewModel(tokenRepository, standardTestDispatcherRule.testDispatcher)

        val result = loginViewModel.event.first()

        assertTrue(result is LoginViewModel.Event.Success)
    }

    @Test
    fun loginWithGitHub_success_eventSuccess() = runTest {
        val code = "code"
        whenever(tokenRepository.isLoggedIn()).thenReturn(false)
        loginViewModel = LoginViewModel(tokenRepository, standardTestDispatcherRule.testDispatcher)
        whenever(tokenRepository.authorizeOAuth(code)).thenReturn(Result.success(Unit))

        loginViewModel.loginWithGitHub(code)

        val event = loginViewModel.event.first()
        assertTrue(event is LoginViewModel.Event.Success)
    }

    @Test
    fun loginWithGitHub_networkError() = runTest {
        val code = "code"
        whenever(tokenRepository.isLoggedIn()).thenReturn(false)
        loginViewModel = LoginViewModel(tokenRepository, standardTestDispatcherRule.testDispatcher)
        whenever(tokenRepository.authorizeOAuth(code)).thenReturn(Result.failure(CommonException.NetworkError()))

        loginViewModel.loginWithGitHub(code)
        advanceUntilIdle()

        val uiState = loginViewModel.uiState.first()
        assertTrue(uiState is LoginViewModel.UiState.Error)
        assertEquals((uiState as LoginViewModel.UiState.Error).errorMessage, CONNECTION_FAIL)
    }
}