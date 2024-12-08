package com.prac.feature.login

import app.cash.turbine.test
import com.prac.core.common.constants.CONNECTION_FAIL
import com.prac.data.repository.TokenRepository
import com.prac.feature.login.model.Action
import com.prac.feature.login.model.Event
import com.prac.shared_test.data.FakeTokenRepository
import com.prac.shared_test.rules.StandardTestDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import kotlin.math.log

class LoginViewModelTest {

    @get:Rule
    val standardTestDispatcherRule = StandardTestDispatcherRule()

    private lateinit var loginViewModel: LoginViewModel

    private val loginReducerProcessor = LoginReducerProcessor()
    private lateinit var loginActionProcessor: LoginActionProcessor
    private lateinit var tokenRepository: TokenRepository

    @Test
    fun process_actionIsCheckAutoLogin_eventIsSuccessLogin() = runTest {
        tokenRepository = FakeTokenRepository(token = "test")
        loginActionProcessor = LoginActionProcessor(tokenRepository)
        loginViewModel = LoginViewModel(
            loginReducerProcessor = loginReducerProcessor,
            loginActionProcessor = loginActionProcessor,
            ioDispatcher = standardTestDispatcherRule.testDispatcher
        )

        loginViewModel.eventFlow.test {
            val result = awaitItem()
            assertTrue(result is Event.SuccessLogin)
        }
    }

    @Test
    fun process_actionIsCheckAutoLogin_emitNothing() = runTest {
        tokenRepository = FakeTokenRepository()
        loginActionProcessor = LoginActionProcessor(tokenRepository)
        loginViewModel = LoginViewModel(
            loginReducerProcessor = loginReducerProcessor,
            loginActionProcessor = loginActionProcessor,
            ioDispatcher = standardTestDispatcherRule.testDispatcher
        )

        loginViewModel.eventFlow.test {
            expectNoEvents()
        }
    }

    @Test
    fun process_actionIsOAuthAuthenticated_eventIsLoginSuccess() = runTest {
        tokenRepository = FakeTokenRepository()
        loginActionProcessor = LoginActionProcessor(tokenRepository)
        loginViewModel = LoginViewModel(
            loginReducerProcessor = loginReducerProcessor,
            loginActionProcessor = loginActionProcessor,
            ioDispatcher = standardTestDispatcherRule.testDispatcher
        )

        loginViewModel.eventFlow.test {
            loginViewModel.process(Action.InternalAction.AuthenticateOAuth("success"))

            val result = awaitItem()
            assertTrue(result is Event.SuccessLogin)
        }
    }

    @Test
    fun process_actionIsOAuthAuthenticated_uiStateIsError() = runTest {
        tokenRepository = FakeTokenRepository()
        loginActionProcessor = LoginActionProcessor(tokenRepository)
        loginViewModel = LoginViewModel(
            loginReducerProcessor = loginReducerProcessor,
            loginActionProcessor = loginActionProcessor,
            ioDispatcher = standardTestDispatcherRule.testDispatcher
        )

        loginViewModel.uiStateFlow.test {
            awaitItem() // initialState

            loginViewModel.process(Action.InternalAction.AuthenticateOAuth("ioException"))

            awaitItem() // loadingState

            val result = awaitItem()
            assertTrue(result.isError)
            assertEquals(result.errorMessage, CONNECTION_FAIL)
        }
    }

    @Test
    fun process_actionIsDialogDismiss_emitIdle() = runTest {
        tokenRepository = FakeTokenRepository()
        loginActionProcessor = LoginActionProcessor(tokenRepository)
        loginViewModel = LoginViewModel(
            loginReducerProcessor = loginReducerProcessor,
            loginActionProcessor = loginActionProcessor,
            ioDispatcher = standardTestDispatcherRule.testDispatcher
        )

        loginViewModel.uiStateFlow.test {
            awaitItem() // initialState

            loginViewModel.process(Action.InternalAction.AuthenticateOAuth("ioException"))
            awaitItem() // loadingState
            awaitItem() // errorState

            loginViewModel.process(Action.UserAction.DialogDismiss)

            val result = awaitItem()
            assertFalse(result.isLoading)
            assertFalse(result.isError)
            assertTrue(result.errorMessage.isEmpty())
        }
    }
}