package com.prac.githubrepo.login

import com.prac.githubrepo.ui.login.LoginActionProcessor
import com.prac.githubrepo.ui.login.model.Action
import com.prac.githubrepo.ui.login.model.Event
import com.prac.githubrepo.ui.login.model.UiState
import com.prac.shared_test.data.FakeTokenRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import kotlin.test.assertFailsWith

class LoginActionProcessorTest {

    private lateinit var tokenRepository: FakeTokenRepository

    private lateinit var loginActionProcessor: LoginActionProcessor

    @Test
    fun invoke_userIsLoggedIn_emitLoginSuccess() = runTest {
        tokenRepository = FakeTokenRepository("test")
        loginActionProcessor = LoginActionProcessor(tokenRepository)

        val result = loginActionProcessor.invoke(Action.CheckAutoLogin).first()

        val uiState = result.first
        val event = result.second
        Assert.assertTrue(uiState == null)
        Assert.assertTrue(event is Event.LoginSuccess)
    }

    @Test
    fun invoke_userIsNotLoggedIn_emitNothing() = runTest {
        tokenRepository = FakeTokenRepository()
        loginActionProcessor = LoginActionProcessor(tokenRepository)

        assertFailsWith<NoSuchElementException> {
            loginActionProcessor.invoke(Action.CheckAutoLogin).first()
        }
    }

    @Test
    fun invoke_actionIsOAuthAuthenticated_emitLoadingAndLoginSuccess() = runTest {
        tokenRepository = FakeTokenRepository()
        loginActionProcessor = LoginActionProcessor(tokenRepository)

        val result = loginActionProcessor.invoke(Action.OAuthAuthenticated("success")).take(2).toList()

        val fUiState = result[0].first
        val fEvent = result[0].second
        val sUiState = result[1].first
        val sEvent = result[1].second
        Assert.assertTrue(fUiState is UiState.Loading)
        Assert.assertTrue(fEvent == null)
        Assert.assertTrue(sUiState == null)
        Assert.assertTrue(sEvent is Event.LoginSuccess)
    }

    @Test
    fun invoke_actionIsOAuthAuthenticated_emitLoadingAndConnectionError() = runTest {
        tokenRepository = FakeTokenRepository()
        loginActionProcessor = LoginActionProcessor(tokenRepository)

        val result = loginActionProcessor.invoke(Action.OAuthAuthenticated("ioException")).take(2).toList()

        val fUiState = result[0].first
        val fEvent = result[0].second
        val sUiState = result[1].first
        val sEvent = result[1].second
        Assert.assertTrue(fUiState is UiState.Loading)
        Assert.assertTrue(fEvent == null)
        Assert.assertTrue(sUiState is UiState.Error)
        Assert.assertTrue(sEvent == null)
    }
}