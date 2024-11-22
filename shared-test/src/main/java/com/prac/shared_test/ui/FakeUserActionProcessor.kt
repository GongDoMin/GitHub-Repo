package com.prac.shared_test.ui

import com.prac.githubrepo.common.ActionProcessor
import com.prac.githubrepo.ui.login.model.Action
import com.prac.githubrepo.ui.login.model.Event
import com.prac.githubrepo.ui.login.model.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeUserActionProcessor : ActionProcessor<Action, UiState, Event> {
    override fun invoke(action: Action): Flow<Pair<UiState?, Event?>> =
        flow {
            println("?????")
            when (action) {
                is Action.OnClickLoginButton -> emit(null to Event.OpenBrowser)
                is Action.DialogDismiss -> emit(UiState.Idle to null)
                else -> Unit
            }
        }
}