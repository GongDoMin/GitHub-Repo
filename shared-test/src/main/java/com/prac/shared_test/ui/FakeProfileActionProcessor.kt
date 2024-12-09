package com.prac.shared_test.ui

import com.prac.core.common.mvi.action.ActionProcessor
import com.prac.feature.profile.model.Action
import com.prac.feature.profile.model.Event
import com.prac.feature.profile.model.Mutation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow

class FakeProfileActionProcessor : ActionProcessor<Action, Mutation, Event> {
    override fun invoke(action: Action): Flow<Pair<Mutation?, Event?>> =
        flow {
            when (action) {
                is Action.UserAction.OnClickLogoutButton -> handleClickLogoutButton()
                is Action.UserAction.DialogDismiss -> handleDialogDismiss()
                is Action.UserAction.OnClickNegativeButton -> handleClickNegativeButton()
                is Action.UserAction.OnClickPositiveButton -> handleClickPositiveButton()
            }
        }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.handleClickLogoutButton() {
        emit(Mutation.ShowDialog to null)
    }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.handleDialogDismiss() {
        emit(Mutation.ShowIdle to null)
    }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.handleClickNegativeButton() {
        emit(Mutation.ShowIdle to null)
    }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.handleClickPositiveButton() {
        emit(Mutation.ShowLoading to null)

        emit(null to Event.Logout)
    }
}