package com.prac.githubrepo.login

import com.prac.data.repository.TokenRepository
import com.prac.githubrepo.util.StandardTestDispatcherRule
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest {

    @get:Rule
    val standardTestDispatcherRule = StandardTestDispatcherRule()

    @Mock
    private lateinit var tokenRepository: TokenRepository

    private lateinit var loginViewModel: LoginViewModel
}