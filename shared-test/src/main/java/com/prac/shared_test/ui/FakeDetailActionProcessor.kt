package com.prac.shared_test.ui

import com.prac.core.common.constants.CONNECTION_FAIL
import com.prac.core.common.constants.INVALID_REPOSITORY
import com.prac.core.common.constants.INVALID_TOKEN
import com.prac.core.common.constants.UNKNOWN
import com.prac.core.common.mvi.action.ActionProcessor
import com.prac.data.exception.CommonException
import com.prac.data.exception.RepositoryException
import com.prac.data.model.Owner
import com.prac.data.model.RepositoryDetail
import com.prac.feature.detail.model.Action
import com.prac.feature.detail.model.Event
import com.prac.feature.detail.model.Mutation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow

class FakeDetailActionProcessor(
    private var throwable: Throwable? = null
) : ActionProcessor<Action, Mutation, Event> {
    override fun invoke(action: Action): Flow<Pair<Mutation?, Event?>> =
        flow {
            when (action) {
                is Action.InternalAction.GetRepository -> getRepository(action.userName, action.repoName)
                is Action.UserAction.OnClickUnStar -> handleClickUnStar()
                is Action.UserAction.OnClickStar -> handleClickStar()
                is Action.UserAction.DialogDismiss -> handleDialogDismiss()
                is Action.UserAction.LogoutDialogDismiss -> handleLogoutDialogDismiss()
            }
        }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.getRepository(userName: String?, repoName: String?) {
        emit(Mutation.ShowLoading to null)

        if (userName == null || repoName == null) {
            emit(Mutation.ShowError("잘못된 접근입니다.") to null)
            return
        }

        throwable?.let {
            handleGetRepositoryFailure(it)
            return
        }

        emit(Mutation.ShowRepository(repository = RepositoryDetail(name = repoName, owner = Owner(login = userName))) to null)
    }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.handleClickUnStar() {
        throwable?.let { handleStarRepositoryFailure(it) }
    }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.handleClickStar() {
        throwable?.let { handleStarRepositoryFailure(it) }
    }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.handleDialogDismiss() {
        emit(null to Event.Error)
    }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.handleLogoutDialogDismiss() {
        emit(null to Event.Logout)
    }


    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.handleGetRepositoryFailure(t: Throwable) {
        when (t) {
            is CommonException.NetworkError -> emit(Mutation.ShowError(CONNECTION_FAIL) to null)
            is CommonException.AuthorizationError -> logout()
            is RepositoryException.NotFoundRepository -> emit(Mutation.ShowError(INVALID_REPOSITORY) to null)
            else -> emit(Mutation.ShowError(UNKNOWN) to null)
        }
    }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.handleStarRepositoryFailure(t: Throwable) {
        when (t) {
            is CommonException.NetworkError -> Unit
            is CommonException.AuthorizationError -> logout()
            is RepositoryException.NotFoundRepository -> emit(Mutation.ShowError(INVALID_REPOSITORY) to null)
            else -> emit(Mutation.ShowError(UNKNOWN) to null)
        }
    }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.handleUnStarRepositoryFailure(t: Throwable) {
        when (t) {
            is CommonException.NetworkError -> Unit
            is CommonException.AuthorizationError -> logout()
            is RepositoryException.NotFoundRepository -> emit(Mutation.ShowError(INVALID_REPOSITORY) to null)
            else -> emit(Mutation.ShowError(UNKNOWN) to null)
        }
    }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.logout() {
        emit(Mutation.ShowError(INVALID_TOKEN) to null)
    }

    fun setThrowable(throwable: Throwable) {
        this.throwable = throwable
    }
}