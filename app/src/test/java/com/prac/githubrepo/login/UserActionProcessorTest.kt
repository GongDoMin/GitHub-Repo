package com.prac.githubrepo.login

import com.prac.githubrepo.ui.login.UserActionProcessor
import com.prac.githubrepo.ui.login.model.Action
import com.prac.githubrepo.ui.login.model.Event
import com.prac.githubrepo.ui.login.model.UiState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class UserActionProcessorTest {

    private val userActionProcessor = UserActionProcessor()

    @Test
    fun invoke_actionIsOnClickLoginButton_emitLaunchLoginIntent() = runTest {
        val result = userActionProcessor.invoke(Action.OnClickLoginButton).first()

        val uiState = result.first
        val event = result.second
        assertTrue(uiState == null)
        assertTrue(event is Event.LaunchLoginIntent)
    }

    @Test
    fun invoke_actionIsDialogDismiss_emitIdle() = runTest {
        val result = userActionProcessor.invoke(Action.DialogDismiss).first()

        val uiState = result.first
        val event = result.second
        assertTrue(uiState is UiState.Idle)
        assertTrue(event == null)
    }
}