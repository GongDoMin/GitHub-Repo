package com.prac.feature.detail

import com.prac.core.common.backoff.BackOffWorkManager
import com.prac.core.common.constants.CONNECTION_FAIL
import com.prac.core.common.constants.INVALID_REPOSITORY
import com.prac.core.common.constants.INVALID_TOKEN
import com.prac.core.common.constants.UNKNOWN
import com.prac.core.common.mvi.action.ActionProcessor
import com.prac.data.exception.CommonException
import com.prac.data.exception.RepositoryException
import com.prac.data.model.RepoDetailModel
import com.prac.data.repository.RepoRepository
import com.prac.data.repository.TokenRepository
import com.prac.feature.detail.model.Action
import com.prac.feature.detail.model.Event
import com.prac.feature.detail.model.Mutation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow

internal class DetailActionProcessor(
    private val repoRepository: RepoRepository,
    private val tokenRepository: TokenRepository,
    private val backOffWorkManager: BackOffWorkManager,
) : ActionProcessor<Action, Mutation, Event> {
    override fun invoke(action: Action): Flow<Pair<Mutation?, Event?>> =
        flow {
            when (action) {
                is Action.InternalAction.GetRepository -> getRepository(action.userName, action.repoName)
                is Action.UserAction.OnClickUnStar -> handleClickUnStar(action.repoDetailModel)
                is Action.UserAction.OnClickStar -> handleClickStar(action.repoDetailModel)
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

        repoRepository.getRepository(userName, repoName)
            .onSuccess {
                handleGetRepositorySuccess(it)
            }
            .onFailure {
                handleGetRepositoryFailure(it)
            }
    }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.handleClickUnStar(repoDetailModel: RepoDetailModel) {
        repoRepository.starLocalRepository(repoDetailModel.id, repoDetailModel.stargazersCount + 1)

        repoRepository.starRepository(repoDetailModel.owner.login, repoDetailModel.name)
            .onFailure {
                handleStarRepositoryFailure(it, repoDetailModel)
            }
    }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.handleClickStar(repoDetailModel: RepoDetailModel) {
        repoRepository.unStarLocalRepository(repoDetailModel.id, repoDetailModel.stargazersCount - 1)

        repoRepository.unStarRepository(repoDetailModel.owner.login, repoDetailModel.name)
            .onFailure {
                handleUnStarRepositoryFailure(it, repoDetailModel)
            }
    }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.handleDialogDismiss() {
        emit(null to Event.Error)
    }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.handleLogoutDialogDismiss() {
        emit(null to Event.Logout)
    }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.handleGetRepositorySuccess(repoDetailModel: RepoDetailModel) {
        repoRepository.getStarStateAndStarCount(repoDetailModel.id).collect { pair ->
            val isStarred = pair.first
            val stargazersCount = pair.second

            // Room 에서 repoDetailEntity.id 값이 없을 경우에 null 을 반환한다.
            if (stargazersCount == null) {
                emit(Mutation.ShowError(INVALID_REPOSITORY) to null)
                return@collect
            }

            // List 화면에서 Star Check 가 완료되기 전에 사용자가 Detail 화면으로 넘어온 경우 null 을 반환한다.
            if (isStarred == null) {
                repoRepository.isStarred(repoDetailModel.id, repoDetailModel.name)
            }

            emit(Mutation.ShowRepository(repository = repoDetailModel.copy(isStarred = isStarred,stargazersCount = stargazersCount)) to null)
        }
    }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.handleGetRepositoryFailure(t: Throwable) {
        when (t) {
            is CommonException.NetworkError -> {
                emit(Mutation.ShowError(CONNECTION_FAIL) to null)
            }
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

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.handleStarRepositoryFailure(t: Throwable, repoDetailModel: RepoDetailModel) {
        when (t) {
            is CommonException.NetworkError -> {
                backOffWorkManager.addWork(
                    uniqueID = "star_${repoDetailModel.id}",
                    work = { repoRepository.starRepository(repoDetailModel.owner.login, repoDetailModel.name) }
                )
            }
            is CommonException.AuthorizationError -> {
                logout()
            }
            is RepositoryException.NotFoundRepository -> {
                repoRepository.unStarLocalRepository(repoDetailModel.id, repoDetailModel.stargazersCount)

                emit(Mutation.ShowError(INVALID_REPOSITORY) to null)
            }
            else -> {
                repoRepository.unStarLocalRepository(repoDetailModel.id, repoDetailModel.stargazersCount)

                emit(Mutation.ShowError(UNKNOWN) to null)
            }
        }
    }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.handleUnStarRepositoryFailure(t: Throwable, repoDetailModel: RepoDetailModel) {
        when (t) {
            is CommonException.NetworkError -> {
                backOffWorkManager.addWork(
                    uniqueID = "star_${repoDetailModel.id}",
                    work = { repoRepository.unStarRepository(repoDetailModel.owner.login, repoDetailModel.name) }
                )
            }
            is CommonException.AuthorizationError -> {
                logout()
            }
            is RepositoryException.NotFoundRepository -> {
                repoRepository.starLocalRepository(repoDetailModel.id, repoDetailModel.stargazersCount)

                emit(Mutation.ShowError(INVALID_REPOSITORY) to null)
            }
            else -> {
                repoRepository.starLocalRepository(repoDetailModel.id, repoDetailModel.stargazersCount)

                emit(Mutation.ShowError(UNKNOWN) to null)
            }
        }
    }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.logout() {
        tokenRepository.clearToken()
        repoRepository.clearRepositories()
        backOffWorkManager.clearWork()

        emit(Mutation.ShowError(INVALID_TOKEN) to null)
    }
}