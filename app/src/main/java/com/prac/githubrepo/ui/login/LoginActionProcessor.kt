package com.prac.githubrepo.ui.login

import com.prac.data.exception.CommonException
import com.prac.data.repository.TokenRepository
import com.prac.githubrepo.common.ActionProcessor
import com.prac.githubrepo.constants.CONNECTION_FAIL
import com.prac.githubrepo.constants.LOGIN_FAIL
import com.prac.githubrepo.ui.login.model.Action
import com.prac.githubrepo.ui.login.model.Event
import com.prac.githubrepo.ui.login.model.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow

class LoginActionProcessor(
    private val tokenRepository: TokenRepository
) : ActionProcessor<Action, UiState, Event> {
    override fun invoke(action: Action): Flow<Pair<UiState?, Event?>> =
        flow {
            when (action) {
                is Action.OAuthAuthenticated -> oAuthAuthenticated(action.code)
                is Action.CheckAutoLogin -> checkAutoLogin()
                else -> Unit
            }
        }

    private suspend fun FlowCollector<Pair<UiState?, Event?>>.oAuthAuthenticated(code: String) {
        emit(UiState(isLoading = true) to null)

        tokenRepository.authorizeOAuth(code = code)
            .onSuccess {
                emit(null to Event.LoginSuccess)
            }.onFailure {
                val errorMessage = handleLoginError(it)
                emit(UiState(isError = true, errorMessage = errorMessage) to null)
            }
    }

    private suspend fun FlowCollector<Pair<UiState?, Event?>>.checkAutoLogin() {
        if (tokenRepository.isLoggedIn()) emit(null to Event.LoginSuccess)
    }

    private fun handleLoginError(t: Throwable) =
        when (t) {
            is CommonException.NetworkError -> {
                CONNECTION_FAIL
            }
            else -> {
                LOGIN_FAIL
            }
        }
}