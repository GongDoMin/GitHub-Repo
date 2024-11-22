package com.prac.githubrepo.login

import app.cash.turbine.test
import com.prac.githubrepo.ui.login.LoginViewModel
import com.prac.githubrepo.ui.login.model.Action
import com.prac.githubrepo.ui.login.model.Event
import com.prac.githubrepo.ui.login.model.UiState
import com.prac.githubrepo.util.StandardTestDispatcherRule
import com.prac.shared_test.ui.FakeLoginActionProcessor
import com.prac.shared_test.ui.FakeUserActionProcessor
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

    @Test
    fun process_actionIsCheckAutoLogin_emitLoginSuccess() = runTest {
        val loginActionProcessor = FakeLoginActionProcessor(
            isLoggedIn = true
        )
        val userActionProcessor = FakeUserActionProcessor()
        loginViewModel = LoginViewModel(loginActionProcessor, userActionProcessor, standardTestDispatcherRule.testDispatcher)

        loginViewModel.process(Action.InternalAction.CheckAutoLogin)

        val result = loginViewModel.eventFlow.first()
        assertTrue(result is Event.LoginSuccess)
    }

    @Test
    fun process_actionIsCheckAutoLogin_emitNothing() = runTest {
        val loginActionProcessor = FakeLoginActionProcessor(
            isLoggedIn = false
        )
        val userActionProcessor = FakeUserActionProcessor()
        loginViewModel = LoginViewModel(loginActionProcessor, userActionProcessor, standardTestDispatcherRule.testDispatcher)

        loginViewModel.process(Action.InternalAction.CheckAutoLogin)

        loginViewModel.eventFlow.test {
            expectNoEvents()
        }
    }

    @Test
    fun process_actionIsOAuthAuthenticated_emitLoginSuccess() = runTest {
        val loginActionProcessor = FakeLoginActionProcessor(
            isLoggedIn = false
        )
        val userActionProcessor = FakeUserActionProcessor()
        loginViewModel = LoginViewModel(loginActionProcessor, userActionProcessor, standardTestDispatcherRule.testDispatcher)

        loginViewModel.process(Action.InternalAction.AuthenticateOAuth("test"))

        loginViewModel.eventFlow.test {
            val result = awaitItem()
            assertTrue(result is Event.LoginSuccess)
        }
    }

    @Test
    fun process_actionIsOAuthAuthenticated_emitError() = runTest {
        val errorMessage = "error"
        val loginActionProcessor = FakeLoginActionProcessor(
            isLoggedIn = false,
            errorMessage = errorMessage
        )
        val userActionProcessor = FakeUserActionProcessor()
        loginViewModel = LoginViewModel(loginActionProcessor, userActionProcessor, standardTestDispatcherRule.testDispatcher)

        loginViewModel.process(Action.InternalAction.AuthenticateOAuth("test"))

        loginViewModel.uiStateFlow.test {
            awaitItem() // idle
            val loading = awaitItem()
            assertTrue(loading.isLoading)
            val result = awaitItem()
            assertTrue(result.isError)
            assertEquals(result.errorMessage, errorMessage)
        }
    }

    @Test
    fun process_actionIsDialogDismiss_emitIdle() = runTest {
        val loginActionProcessor = FakeLoginActionProcessor(
            isLoggedIn = false
        )
        val userActionProcessor = FakeUserActionProcessor()
        loginViewModel = LoginViewModel(loginActionProcessor, userActionProcessor, standardTestDispatcherRule.testDispatcher)

        loginViewModel.process(Action.UserAction.DialogDismiss)

        loginViewModel.uiStateFlow.test {
            val result = awaitItem()
            assertFalse(result.isLoading)
            assertFalse(result.isError)
        }
    }
}