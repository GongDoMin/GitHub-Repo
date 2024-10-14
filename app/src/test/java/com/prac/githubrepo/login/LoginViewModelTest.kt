package com.prac.githubrepo.login

import com.prac.data.repository.TokenRepository
import com.prac.githubrepo.util.StandardTestDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever

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

        Assert.assertTrue(result is LoginViewModel.Event.Success)
    }
}