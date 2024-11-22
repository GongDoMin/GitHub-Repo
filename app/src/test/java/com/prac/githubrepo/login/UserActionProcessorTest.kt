package com.prac.githubrepo.login

import com.prac.githubrepo.ui.login.UserActionProcessor
import com.prac.githubrepo.ui.login.model.Action
import com.prac.githubrepo.ui.login.model.Event
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class UserActionProcessorTest {

    private val userActionProcessor = UserActionProcessor()

    @Test
    fun invoke_actionIsOnClickLoginButton_emitLaunchLoginIntent() = runTest {
        val result = userActionProcessor.invoke(Action.UserAction.OnClickLoginButton).first()

        val uiState = result.first
        val event = result.second
        assertTrue(uiState == null)
        assertTrue(event is Event.OpenBrowser)
    }

    @Test
    fun invoke_actionIsDialogDismiss_emitIdle() = runTest {
        val result = userActionProcessor.invoke(Action.UserAction.DialogDismiss).first()

        val uiState = result.first
        val event = result.second
        assertTrue(uiState?.isError == false && !uiState.isLoading)
        assertTrue(event == null)
    }
}