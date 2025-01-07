package com.prac.shared_test.ui

import com.prac.core.common.constants.INVALID_REPOSITORY
import com.prac.core.common.constants.INVALID_TOKEN
import com.prac.core.common.constants.UNKNOWN
import com.prac.core.common.mvi.action.ActionProcessor
import com.prac.data.exception.CommonException
import com.prac.data.exception.RepositoryException
import com.prac.feature.main.model.Action
import com.prac.feature.main.model.Event
import com.prac.feature.main.model.Mutation
import com.prac.data.model.Repository
import com.prac.feature.main.refresh.RefreshState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow

class FakeMainActionProcessor : ActionProcessor<Action, Mutation, Event> {
    private lateinit var throwable: Throwable

    override fun invoke(action: Action): Flow<Pair<Mutation?, Event?>> =
        flow {
            when (action) {
                is Action.InternalAction.Load -> Unit
                is Action.InternalAction.FetchStarState -> Unit
                is Action.InternalAction.Logout -> logout()
                is Action.InternalAction.UpdateRefreshState -> handleUpdateRefreshState(action.refreshState)
                is Action.UserAction.DialogDismiss -> handleDialogDismiss()
                is Action.UserAction.LogoutDialogDismiss -> handleLogoutDialogDismiss()
                is Action.UserAction.OnClickRepository -> handleClickRepository(action.repository)
                is Action.UserAction.OnClickRetry -> handleClickRetry()
                is Action.UserAction.OnClickStar -> handleClickStar()
                is Action.UserAction.OnClickUnStar -> handleClickUnStar()
            }
        }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.handleClickRepository(repository: Repository) {
        emit(null to Event.OpenRepositoryDetail(repository.owner.login, repository.name))
    }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.handleClickRetry() {
        emit(null to Event.Retry)
    }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.handleDialogDismiss() {
        emit(Mutation.ShowContent to null)
    }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.handleLogoutDialogDismiss() {
        emit(null to Event.Logout)
    }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.handleClickUnStar() {
        if (!::throwable.isInitialized) {
            throw NotImplementedError("throwable is not initialized")
        }
        handleStarRepositoryFailure(throwable)
    }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.handleClickStar() {
        if (!::throwable.isInitialized) {
            throw NotImplementedError("throwable is not initialized")
        }

        handleUnStarRepositoryFailure(throwable)
    }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.handleUpdateRefreshState(refreshState: RefreshState) {
        emit(Mutation.UpdateRefreshState(refreshState) to null)
    }


    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.handleStarRepositoryFailure(t: Throwable) {
        when (t) {
            is CommonException.NetworkError -> Unit
            is CommonException.AuthorizationError -> {
                logout()
            }
            is RepositoryException.NotFoundRepository -> {
                emit(Mutation.ShowError(INVALID_REPOSITORY) to null)
            }
            else -> {
                emit(Mutation.ShowError(UNKNOWN) to null)
            }
        }
    }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.handleUnStarRepositoryFailure(t: Throwable) {
        when (t) {
            is CommonException.NetworkError -> Unit
            is CommonException.AuthorizationError -> {
                logout()
            }
            is RepositoryException.NotFoundRepository -> {
                emit(Mutation.ShowError(INVALID_REPOSITORY) to null)
            }
            else -> {
                emit(Mutation.ShowError(UNKNOWN) to null)
            }
        }
    }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.logout() {
        emit(Mutation.ShowError(INVALID_TOKEN) to null)
    }

    /*
    * this method is only for test
    */
    fun setThrowable(throwable: Throwable) {
        this.throwable = throwable
    }
}