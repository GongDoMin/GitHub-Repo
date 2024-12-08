package com.prac.feature.login

import com.prac.core.common.constants.CONNECTION_FAIL
import com.prac.core.common.constants.LOGIN_FAIL
import com.prac.core.common.mvi.action.ActionProcessor
import com.prac.data.exception.CommonException
import com.prac.data.repository.TokenRepository
import com.prac.feature.login.model.Action
import com.prac.feature.login.model.Event
import com.prac.feature.login.model.Mutation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow

class LoginActionProcessor(
    private val tokenRepository: TokenRepository
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

        tokenRepository.authorizeOAuth(code = code)
            .onSuccess {
                emit(null to Event.SuccessLogin)
            }.onFailure {
                val errorMessage = handleLoginErrorMessage(it)
                emit(Mutation.ShowError(errorMessage) to null)
            }
    }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.checkAuthLogin() {
        if (tokenRepository.isLoggedIn()) emit(null to Event.SuccessLogin)
    }

    private fun handleLoginErrorMessage(t: Throwable) =
        when (t) {
            is CommonException.NetworkError -> CONNECTION_FAIL
            else -> LOGIN_FAIL
        }
}