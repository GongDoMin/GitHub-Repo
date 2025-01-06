package com.prac.feature.main

import com.prac.core.common.backoff.BackOffWorkManager
import com.prac.core.common.constants.INVALID_REPOSITORY
import com.prac.core.common.constants.INVALID_TOKEN
import com.prac.core.common.constants.UNKNOWN
import com.prac.core.common.mvi.action.ActionProcessor
import com.prac.data.exception.CommonException
import com.prac.data.exception.RepositoryException
import com.prac.data.repository.RepoRepository
import com.prac.domain.ClearLocalDataUseCase
import com.prac.feature.main.model.Action
import com.prac.feature.main.model.Event
import com.prac.feature.main.model.Mutation
import com.prac.feature.main.model.Repository
import com.prac.feature.main.refresh.RefreshState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow

internal class MainActionProcessor(
    private val repoRepository: RepoRepository,
    private val clearLocalDataUseCase: ClearLocalDataUseCase,
    private val backOffWorkManager: BackOffWorkManager
) : ActionProcessor<Action, Mutation, Event> {

    private val _starRequestJobManager: HashMap<Int, Unit> = HashMap()

    override fun invoke(action: Action): Flow<Pair<Mutation?, Event?>> =
        flow {
            when(action) {
                is Action.InternalAction.Load -> Unit
                is Action.InternalAction.FetchStarState -> fetchStarState(action.repository)
                is Action.InternalAction.Logout -> logout()
                is Action.InternalAction.UpdateRefreshState -> handleUpdateRefreshState(action.refreshState)
                is Action.UserAction.OnClickRepository -> handleClickRepository(action.repository)
                is Action.UserAction.OnClickUnStar -> handleClickUnStar(action.repository)
                is Action.UserAction.OnClickStar -> handleClickStar(action.repository)
                is Action.UserAction.OnClickRetry -> handleClickRetry()
                is Action.UserAction.DialogDismiss -> handleDialogDismiss()
                is Action.UserAction.LogoutDialogDismiss -> handleLogoutDialogDismiss()
            }
        }

    private suspend fun fetchStarState(repository: Repository) {
        if (_starRequestJobManager[repository.id] == null) {
            _starRequestJobManager[repository.id] = Unit

            repoRepository.isStarred(repository.id, repository.name)
        }
    }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.handleClickRepository(repository: Repository) {
        emit(null to Event.OpenRepositoryDetail(repository.owner.login, repository.name))
    }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.handleClickUnStar(repository: Repository) {
        repoRepository.starLocalRepository(repository.id, repository.stargazersCount + 1)

        repoRepository.starRepository(repository.owner.login, repository.name)
            .onFailure {
                handleStarRepositoryFailure(it, repository)
            }
    }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.handleClickStar(repository: Repository) {
        repoRepository.unStarLocalRepository(repository.id, repository.stargazersCount - 1)

        repoRepository.unStarRepository(repository.owner.login, repository.name)
            .onFailure {
                handleUnStarRepositoryFailure(it, repository)
            }
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

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.handleUpdateRefreshState(refreshState: RefreshState) {
        if (refreshState == RefreshState.Refreshing) _starRequestJobManager.clear()

        emit(Mutation.UpdateRefreshState(refreshState) to null)
    }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.handleStarRepositoryFailure(t: Throwable, repository: Repository) {
        when (t) {
            is CommonException.NetworkError -> {
                backOffWorkManager.addWork(
                    uniqueID = "star_${repository.id}",
                    work = { repoRepository.starRepository(repository.owner.login, repository.name) }
                )
            }
            is CommonException.AuthorizationError -> {
                logout()
            }
            is RepositoryException.NotFoundRepository -> {
                repoRepository.unStarLocalRepository(repository.id, repository.stargazersCount)

                emit(Mutation.ShowError(INVALID_REPOSITORY) to null)
            }
            else -> {
                repoRepository.unStarLocalRepository(repository.id, repository.stargazersCount)

                emit(Mutation.ShowError(UNKNOWN) to null)
            }
        }
    }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.handleUnStarRepositoryFailure(t: Throwable, repository: Repository) {
        when (t) {
            is CommonException.NetworkError -> {
                backOffWorkManager.addWork(
                    uniqueID = "star_${repository.id}",
                    work = { repoRepository.unStarRepository(repository.owner.login, repository.name) }
                )
            }
            is CommonException.AuthorizationError -> {
                logout()
            }
            is RepositoryException.NotFoundRepository -> {
                repoRepository.starLocalRepository(repository.id, repository.stargazersCount)

                emit(Mutation.ShowError(INVALID_REPOSITORY) to null)
            }
            else -> {
                repoRepository.starLocalRepository(repository.id, repository.stargazersCount)

                emit(Mutation.ShowError(UNKNOWN) to null)
            }
        }
    }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.logout() {
        clearLocalDataUseCase.invoke()
        backOffWorkManager.clearWork()

        emit(Mutation.ShowError(INVALID_TOKEN) to null)
    }
}