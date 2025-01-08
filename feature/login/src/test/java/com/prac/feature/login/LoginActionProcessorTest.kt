package com.prac.feature.login

import app.cash.turbine.test
import com.prac.core.common.constants.CONNECTION_FAIL
import com.prac.core.common.constants.LOGIN_FAIL
import com.prac.data.repository.TokenRepository
import com.prac.domain.AuthorizeOAuthUseCase
import com.prac.feature.login.model.Action
import com.prac.feature.login.model.Event
import com.prac.feature.login.model.Mutation
import com.prac.shared_test.data.FakeTokenRepository
import com.prac.shared_test.domain.FakeAuthorizeOAuthUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class LoginActionProcessorTest {

    private lateinit var tokenRepository: TokenRepository
    private val authorizeOAuthUseCase: AuthorizeOAuthUseCase = FakeAuthorizeOAuthUseCase()
    private lateinit var loginActionProcessor: LoginActionProcessor

    @Test
    fun 액션이_onClickLoginButton일때_event는_OpenBrowser() = runTest {
        // given
        initialLoginActionProcessorWithNothing()

        // when, then
        loginActionProcessor(Action.UserAction.OnClickLoginButton).test {
            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation == null)
            assertTrue(event is Event.OpenBrowser)
        }
    }

    @Test
    fun 액션이_DialogDismiss일때_mutation은_ShowIdle() = runTest {
        // given
        initialLoginActionProcessorWithNothing()

        // when, then
        loginActionProcessor(Action.UserAction.DialogDismiss).test {
            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation is Mutation.ShowIdle)
            assertTrue(event == null)
        }
    }

    @Test
    fun 액션이_AuthenticateOAuth일때_event는_SuccessLogin() = runTest {
        // given
        initialLoginActionProcessorWithNothing()

        // when, then
        loginActionProcessor(Action.InternalAction.AuthenticateOAuth("success")).test {
            awaitItem() // loadingState

            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation == null)
            assertTrue(event is Event.SuccessLogin)
        }
    }

    @Test
    fun 액션이_AuthenticateOAuth이지만_IOException에러가_발생할때_mutation은_ShowError() = runTest {
        // given
        initialLoginActionProcessorWithNothing()

        // when, then
        loginActionProcessor(Action.InternalAction.AuthenticateOAuth("ioException")).test {
            awaitItem() // loadingState

            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation is Mutation.ShowError)
            assertTrue((mutation as Mutation.ShowError).errorMessage == CONNECTION_FAIL)
            assertTrue(event == null)
        }
    }

    @Test
    fun 액션이_AuthenticateOAuth이지만_else에러가_발생할때_mutation은_ShowError() = runTest {
        // given
        initialLoginActionProcessorWithNothing()

        // when, then
        loginActionProcessor(Action.InternalAction.AuthenticateOAuth("else")).test {
            awaitItem() // loadingState

            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation is Mutation.ShowError)
            assertTrue((mutation as Mutation.ShowError).errorMessage == LOGIN_FAIL)
            assertTrue(event == null)
        }
    }

    @Test
    fun 액션이_CheckAutoLogin일때_이미로그인되어있다면_event는_SuccessLogin() = runTest {
        // given
        initialLoginActionProcessorWithFakeToken()

        // when, then
        loginActionProcessor(Action.InternalAction.CheckAutoLogin).test {
            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation == null)
            assertTrue(event is Event.SuccessLogin)
        }
    }

    @Test
    fun 액션이_CheckAutoLogin일때_이미로그인되어있다면_아무것도emit하지않음() = runTest {
        // given
        initialLoginActionProcessorWithNothing()

        // when, then
        loginActionProcessor(Action.InternalAction.CheckAutoLogin).test {
            awaitComplete()
        }
    }

    private fun initialLoginActionProcessorWithNothing() {
        tokenRepository = FakeTokenRepository()
        loginActionProcessor = LoginActionProcessor(tokenRepository, authorizeOAuthUseCase)
    }

    private fun initialLoginActionProcessorWithFakeToken() {
        tokenRepository = FakeTokenRepository(FAKE_TOKEN)
        loginActionProcessor = LoginActionProcessor(tokenRepository, authorizeOAuthUseCase)
    }

    companion object {
        private const val FAKE_TOKEN = "fakeToken"
    }
}
