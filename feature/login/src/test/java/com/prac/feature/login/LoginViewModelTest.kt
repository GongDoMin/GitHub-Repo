package com.prac.feature.login

import app.cash.turbine.test
import com.prac.core.common.constants.CONNECTION_FAIL
import com.prac.core.common.mvi.action.ActionProcessor
import com.prac.data.exception.CommonException
import com.prac.feature.login.model.Action
import com.prac.feature.login.model.Event
import com.prac.feature.login.model.Mutation
import com.prac.feature.login.view.UiState
import com.prac.shared_test.rules.StandardTestDispatcherRule
import com.prac.shared_test.ui.FakeLoginActionProcessor
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {

    @get:Rule
    val standardTestDispatcherRule = StandardTestDispatcherRule()

    private val loginReducerProcessor = LoginReducerProcessor()

    private lateinit var loginActionProcessor: ActionProcessor<Action, Mutation, Event>
    private lateinit var loginViewModel: LoginViewModel

    @Test
    fun process_actionIsCheckAutoLogin_eventIsSuccessLogin() = runTest {
        initViewModel(isLoggedIn = true)

        loginViewModel.eventFlow.test {
            val result = awaitItem()
            assertTrue(result is Event.SuccessLogin)
        }
    }

    @Test
    fun process_actionIsCheckAutoLogin_emitNothing() = runTest {
        initViewModel()

        loginViewModel.eventFlow.test {
            expectNoEvents()
        }
    }

    @Test
    fun process_actionIsOAuthAuthenticated_eventIsLoginSuccess() = runTest {
        initViewModel()

        loginViewModel.eventFlow.test {
            loginViewModel.process(Action.InternalAction.AuthenticateOAuth(""))

            val result = awaitItem()
            assertTrue(result is Event.SuccessLogin)
        }
    }

    @Test
    fun process_actionIsOAuthAuthenticated_uiStateIsError() = runTest {
        initViewModel()
        setLoginActionProcessorThrowable(CommonException.NetworkError())

        loginViewModel.uiStateFlow.test {
            awaitItem() // initialState

            loginViewModel.process(Action.InternalAction.AuthenticateOAuth(""))

            awaitItem() // loadingState

            val result = awaitItem()
            assertTrue(result is UiState.Error)
            assertEquals((result as UiState.Error).message, CONNECTION_FAIL)
        }
    }

    @Test
    fun process_actionIsDialogDismiss_emitIdle() = runTest {
        initViewModel()
        setLoginActionProcessorThrowable(CommonException.NetworkError())

        loginViewModel.uiStateFlow.test {
            awaitItem() // initialState

            loginViewModel.process(Action.InternalAction.AuthenticateOAuth("ioException"))
            awaitItem() // loadingState
            awaitItem() // errorState

            loginViewModel.process(Action.UserAction.DialogDismiss)

            val result = awaitItem()
            assertFalse(result is UiState.Loading)
            assertFalse(result is UiState.Error)
        }
    }

    private fun initViewModel(isLoggedIn: Boolean = false) {
        loginActionProcessor = FakeLoginActionProcessor(isLoggedIn)
        loginViewModel = LoginViewModel(
            loginReducerProcessor = loginReducerProcessor,
            loginActionProcessor = loginActionProcessor,
            ioDispatcher = standardTestDispatcherRule.testDispatcher
        )
    }

    private fun setLoginActionProcessorThrowable(throwable: Throwable) {
        (loginActionProcessor as FakeLoginActionProcessor).setThrowable(throwable)
    }
}