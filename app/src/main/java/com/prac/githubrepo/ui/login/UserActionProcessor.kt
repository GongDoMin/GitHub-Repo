package com.prac.githubrepo.ui.login

import com.prac.githubrepo.common.ActionProcessor
import com.prac.githubrepo.ui.login.model.Action
import com.prac.githubrepo.ui.login.model.Event
import com.prac.githubrepo.ui.login.model.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow

class UserActionProcessor : ActionProcessor<Action, UiState, Event> {
    override fun invoke(action: Action): Flow<Pair<UiState?, Event?>> =
        flow {
            when (action) {
                is Action.OnClickLoginButton -> onClickLoginButton()
                is Action.DialogDismiss -> dialogDismiss()
                else -> Unit
            }
        }

    private suspend fun FlowCollector<Pair<UiState?, Event?>>.onClickLoginButton() {
        emit(null to Event.LaunchLoginIntent)
    }

    private suspend fun FlowCollector<Pair<UiState?, Event?>>.dialogDismiss() {
        emit(UiState.Idle to null)
    }
}