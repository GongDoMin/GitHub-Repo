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
    fun invoke_actionIsOnClickLoginButton_eventIsOpenBrowser() = runTest {
        tokenRepository = FakeTokenRepository()
        loginActionProcessor = LoginActionProcessor(tokenRepository, authorizeOAuthUseCase)

        loginActionProcessor(Action.UserAction.OnClickLoginButton).test {
            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation == null)
            assertTrue(event is Event.OpenBrowser)
        }
    }

    @Test
    fun invoke_actionIsDialogDismiss_mutationIsShowIdle() = runTest {
        tokenRepository = FakeTokenRepository()
        loginActionProcessor = LoginActionProcessor(tokenRepository, authorizeOAuthUseCase)

        loginActionProcessor(Action.UserAction.DialogDismiss).test {
            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation is Mutation.ShowIdle)
            assertTrue(event == null)
        }
    }

    @Test
    fun invoke_actionIsAuthenticateOAuth_eventIsSuccessLogin() = runTest {
        tokenRepository = FakeTokenRepository()
        loginActionProcessor = LoginActionProcessor(tokenRepository, authorizeOAuthUseCase)

        loginActionProcessor(Action.InternalAction.AuthenticateOAuth("success")).test {
            awaitItem() // loadingState

            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation == null)
            assertTrue(event is Event.SuccessLogin)
        }
    }

    @Test
    fun invoke_actionIsAuthenticateOAuth_mutationIsError_whenNetworkError() = runTest {
        tokenRepository = FakeTokenRepository()
        loginActionProcessor = LoginActionProcessor(tokenRepository, authorizeOAuthUseCase)

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
    fun invoke_actionIsAuthenticateOAuth_mutationIsError_whenUnknownError() = runTest {
        tokenRepository = FakeTokenRepository()
        loginActionProcessor = LoginActionProcessor(tokenRepository, authorizeOAuthUseCase)

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
    fun invoke_actionIsCheckAutoLogin_eventIsSuccessLogin_whenTokenIsExist() = runTest {
        tokenRepository = FakeTokenRepository("test")
        loginActionProcessor = LoginActionProcessor(tokenRepository, authorizeOAuthUseCase)

        loginActionProcessor(Action.InternalAction.CheckAutoLogin).test {
            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation == null)
            assertTrue(event is Event.SuccessLogin)
        }
    }

    @Test
    fun invoke_actionIsCheckAutoLogin_emitNothing_whenTokenIsNotExist() = runTest {
        tokenRepository = FakeTokenRepository()
        loginActionProcessor = LoginActionProcessor(tokenRepository, authorizeOAuthUseCase)

        loginActionProcessor(Action.InternalAction.CheckAutoLogin).test {
            awaitComplete()
        }
    }
}
