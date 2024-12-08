package com.prac.feature.main

import android.util.SparseArray
import com.prac.core.common.backoff.BackOffWorkManager
import com.prac.core.common.constants.INVALID_REPOSITORY
import com.prac.core.common.constants.INVALID_TOKEN
import com.prac.core.common.constants.UNKNOWN
import com.prac.core.common.mvi.action.ActionProcessor
import com.prac.data.exception.CommonException
import com.prac.data.exception.RepositoryException
import com.prac.data.model.RepoModel
import com.prac.data.repository.RepoRepository
import com.prac.data.repository.TokenRepository
import com.prac.feature.main.model.Action
import com.prac.feature.main.model.Event
import com.prac.feature.main.model.Mutation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow

class MainActionProcessor(
    private val tokenRepository: TokenRepository,
    private val repoRepository: RepoRepository,
    private val backOffWorkManager: BackOffWorkManager
) : ActionProcessor<Action, Mutation, Event> {

    private val _starRequestJobManager: SparseArray<Unit> = SparseArray()

    override fun invoke(action: Action): Flow<Pair<Mutation?, Event?>> =
        flow {
            when(action) {
                is Action.InternalAction.Load -> Unit
                is Action.InternalAction.FetchStarState -> fetchStarState(action.repoModel)
                is Action.InternalAction.Logout -> logout()
                is Action.UserAction.OnClickRepository -> handleClickRepository(action.repoModel)
                is Action.UserAction.OnClickUnStar -> handleClickUnStar(action.repoModel)
                is Action.UserAction.OnClickStar -> handleClickStar(action.repoModel)
                is Action.UserAction.OnClickRetry -> handleClickRetry()
                is Action.UserAction.DialogDismiss -> handleDialogDismiss()
                is Action.UserAction.LogoutDialogDismiss -> handleLogoutDialogDismiss()
            }
        }

    private suspend fun fetchStarState(repoModel: RepoModel) {
        if (_starRequestJobManager[repoModel.id] == null) {
            _starRequestJobManager.put(repoModel.id, Unit)

            repoRepository.isStarred(repoModel.id, repoModel.name)
        }
    }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.handleClickRepository(repoModel: RepoModel) {
        emit(null to Event.OpenRepositoryDetail(repoModel.owner.login, repoModel.name))
    }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.handleClickUnStar(repoModel: RepoModel) {
        repoRepository.starLocalRepository(repoModel.id, repoModel.stargazersCount + 1)

        repoRepository.starRepository(repoModel.owner.login, repoModel.name)
            .onFailure {
                handleStarRepositoryFailure(it, repoModel)
            }
    }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.handleClickStar(repoModel: RepoModel) {
        repoRepository.unStarLocalRepository(repoModel.id, repoModel.stargazersCount - 1)

        repoRepository.unStarRepository(repoModel.owner.login, repoModel.name)
            .onFailure {
                handleUnStarRepositoryFailure(it, repoModel)
            }
    }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.handleClickRetry() {
        emit(null to Event.Retry)
    }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.handleDialogDismiss() {
        emit(Mutation.ShowRepositories to null)
    }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.handleLogoutDialogDismiss() {
        emit(null to Event.Logout)
    }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.handleStarRepositoryFailure(t: Throwable, repoModel: RepoModel) {
        when (t) {
            is CommonException.NetworkError -> {
                backOffWorkManager.addWork(
                    uniqueID = "star_${repoModel.id}",
                    work = { repoRepository.starRepository(repoModel.owner.login, repoModel.name) }
                )
            }
            is CommonException.AuthorizationError -> {
                logout()
            }
            is RepositoryException.NotFoundRepository -> {
                repoRepository.unStarLocalRepository(repoModel.id, repoModel.stargazersCount)

                emit(Mutation.ShowError(INVALID_REPOSITORY) to null)
            }
            else -> {
                repoRepository.unStarLocalRepository(repoModel.id, repoModel.stargazersCount)

                emit(Mutation.ShowError(UNKNOWN) to null)
            }
        }
    }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.handleUnStarRepositoryFailure(t: Throwable, repoModel: RepoModel) {
        when (t) {
            is CommonException.NetworkError -> {
                backOffWorkManager.addWork(
                    uniqueID = "star_${repoModel.id}",
                    work = { repoRepository.unStarRepository(repoModel.owner.login, repoModel.name) }
                )
            }
            is CommonException.AuthorizationError -> {
                logout()
            }
            is RepositoryException.NotFoundRepository -> {
                repoRepository.starLocalRepository(repoModel.id, repoModel.stargazersCount)

                emit(Mutation.ShowError(INVALID_REPOSITORY) to null)
            }
            else -> {
                repoRepository.starLocalRepository(repoModel.id, repoModel.stargazersCount)

                emit(Mutation.ShowError(UNKNOWN) to null)
            }
        }
    }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.logout() {
        tokenRepository.clearToken()
        backOffWorkManager.clearWork()

        emit(Mutation.ShowError(INVALID_TOKEN) to null)
    }
}