package com.prac.githubrepo.login

import app.cash.turbine.test
import com.prac.data.repository.TokenRepository
import com.prac.githubrepo.constants.CONNECTION_FAIL
import com.prac.githubrepo.ui.login.LoginViewModel
import com.prac.githubrepo.ui.login.model.Action
import com.prac.githubrepo.ui.login.model.Event
import com.prac.githubrepo.util.StandardTestDispatcherRule
import com.prac.shared_test.data.FakeTokenRepository
import com.prac.shared_test.ui.FakeLoginReducerProcessor
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {

    @get:Rule
    val standardTestDispatcherRule = StandardTestDispatcherRule()

    private lateinit var loginViewModel: LoginViewModel

    private val loginActionProcessor = FakeLoginReducerProcessor()
    private lateinit var tokenRepository: TokenRepository

    @Test
    fun process_actionIsCheckAutoLogin_emitLoginSuccess() = runTest {
        tokenRepository = FakeTokenRepository(
            token = "test"
        )
        loginViewModel = LoginViewModel(tokenRepository, loginActionProcessor, standardTestDispatcherRule.testDispatcher)

        loginViewModel.process(Action.InternalAction.CheckAutoLogin)

        val result = loginViewModel.eventFlow.first()
        assertTrue(result is Event.SuccessLogin)
    }

    @Test
    fun process_actionIsCheckAutoLogin_emitNothing() = runTest {
        tokenRepository = FakeTokenRepository()
        loginViewModel = LoginViewModel(tokenRepository, loginActionProcessor, standardTestDispatcherRule.testDispatcher)

        loginViewModel.process(Action.InternalAction.CheckAutoLogin)

        loginViewModel.eventFlow.test {
            expectNoEvents()
        }
    }

    @Test
    fun process_actionIsOAuthAuthenticated_emitLoginSuccess() = runTest {
        tokenRepository = FakeTokenRepository()
        loginViewModel = LoginViewModel(tokenRepository, loginActionProcessor, standardTestDispatcherRule.testDispatcher)

        loginViewModel.process(Action.InternalAction.AuthenticateOAuth("success"))

        loginViewModel.eventFlow.test {
            val result = awaitItem()
            assertTrue(result is Event.SuccessLogin)
        }
    }

    @Test
    fun process_actionIsOAuthAuthenticated_emitError() = runTest {
        tokenRepository = FakeTokenRepository()
        loginViewModel = LoginViewModel(tokenRepository, loginActionProcessor, standardTestDispatcherRule.testDispatcher)

        loginViewModel.process(Action.InternalAction.AuthenticateOAuth("ioException"))

        loginViewModel.uiStateFlow.test {
            awaitItem() // idle
            val loading = awaitItem()
            assertTrue(loading.isLoading)
            val result = awaitItem()
            assertTrue(result.isError)
            assertEquals(result.errorMessage, CONNECTION_FAIL)
        }
    }

    @Test
    fun process_actionIsDialogDismiss_emitIdle() = runTest {
        tokenRepository = FakeTokenRepository()
        loginViewModel = LoginViewModel(tokenRepository, loginActionProcessor, standardTestDispatcherRule.testDispatcher)

        loginViewModel.process(Action.UserAction.DialogDismiss)

        loginViewModel.uiStateFlow.test {
            val result = awaitItem()
            assertFalse(result.isLoading)
            assertFalse(result.isError)
        }
    }
}