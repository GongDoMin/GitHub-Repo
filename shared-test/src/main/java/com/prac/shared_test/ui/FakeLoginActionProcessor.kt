package com.prac.shared_test.ui

import com.prac.githubrepo.common.ActionProcessor
import com.prac.githubrepo.ui.login.model.Action
import com.prac.githubrepo.ui.login.model.Event
import com.prac.githubrepo.ui.login.model.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeLoginActionProcessor(
    private val isLoggedIn: Boolean = false,
    private val errorMessage: String = ""
): ActionProcessor<Action, UiState, Event> {
    override fun invoke(action: Action): Flow<Pair<UiState?, Event?>> =
        flow {
            when (action) {
                is Action.OAuthAuthenticated -> {
                    emit(UiState.Loading to null)
                    if (errorMessage.isEmpty()) emit(null to Event.SuccessLogin)
                    else emit(UiState.Error(errorMessage) to null)
                }
                is Action.CheckAutoLogin -> {
                    if (isLoggedIn) emit(null to Event.SuccessLogin)
                }
                else -> Unit
            }
        }
}