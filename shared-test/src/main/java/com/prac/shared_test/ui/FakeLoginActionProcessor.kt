package com.prac.shared_test.ui

import com.prac.core.common.constants.CONNECTION_FAIL
import com.prac.core.common.constants.LOGIN_FAIL
import com.prac.core.common.mvi.action.ActionProcessor
import com.prac.data.exception.CommonException
import com.prac.feature.login.model.Action
import com.prac.feature.login.model.Event
import com.prac.feature.login.model.Mutation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow

class FakeLoginActionProcessor(
    private val isLoggedIn: Boolean = false,
    private val throwable: Throwable? = null
) : ActionProcessor<Action, Mutation, Event> {

    override fun invoke(action: Action): Flow<Pair<Mutation?, Event?>> =
        flow {
            when(action) {
                is Action.UserAction.OnClickLoginButton -> handleLoginButtonClick()
                is Action.UserAction.DialogDismiss -> handleDialogDismiss()
                is Action.InternalAction.AuthenticateOAuth -> authenticateOAuth(action.code)
                is Action.InternalAction.CheckAutoLogin -> checkAuthLogin()
            }
        }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.handleLoginButtonClick() {
        emit(null to Event.OpenBrowser)
    }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.handleDialogDismiss() {
        emit(Mutation.ShowIdle to null)
    }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.authenticateOAuth(code: String) {
        emit(Mutation.ShowLoading to null)

        throwable?.let {
            val errorMessage = handleLoginErrorMessage(throwable)
            emit(Mutation.ShowError(errorMessage) to null)
            return
        }

        emit(null to Event.SuccessLogin)
    }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.checkAuthLogin() {
        if (isLoggedIn) emit(null to Event.SuccessLogin)
    }

    private fun handleLoginErrorMessage(t: Throwable) =
        when (t) {
            is CommonException.NetworkError -> CONNECTION_FAIL
            else -> LOGIN_FAIL
        }
}