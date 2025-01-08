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
    fun 자동로그인_성공이벤트_발행() = runTest {
        // given
        initialLoginViewModel(isLoggedIn = true)

        // when, then
        loginViewModel.eventFlow.test {
            val result = awaitItem()
            assertTrue(result is Event.SuccessLogin)
        }
    }

    @Test
    fun 자동로그인_이벤트발생없음() = runTest {
        // given
        initialLoginViewModel()

        // when, thne
        loginViewModel.eventFlow.test {
            expectNoEvents()
        }
    }

    @Test
    fun OAuth_인증성공_로그인이벤트_발행() = runTest {
        // given
        initialLoginViewModel()

        // when, then
        loginViewModel.eventFlow.test {
            loginViewModel.process(Action.InternalAction.AuthenticateOAuth(""))

            val result = awaitItem()
            assertTrue(result is Event.SuccessLogin)
        }
    }

    @Test
    fun OAuth_인증실패_에러상태_발행() = runTest {
        // given
        initialLoginViewModel(
            throwable = CommonException.NetworkError()
        )

        // when, then
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
    fun 다이얼로그해제_아이들상태_발행() = runTest {
        // given
        initialLoginViewModel(
            throwable = CommonException.NetworkError()
        )

        // when, then
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

    private fun initialLoginViewModel(
        isLoggedIn: Boolean = false,
        throwable: Throwable? = null
    ) {
        loginActionProcessor = FakeLoginActionProcessor(
            isLoggedIn = isLoggedIn,
            throwable = throwable
        )
        loginViewModel = LoginViewModel(
            loginReducerProcessor = loginReducerProcessor,
            loginActionProcessor = loginActionProcessor,
            ioDispatcher = standardTestDispatcherRule.testDispatcher
        )
    }
}