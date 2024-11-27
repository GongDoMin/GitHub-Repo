package com.prac.feature.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prac.core.common.backoff.BackOffWorkManager
import com.prac.core.common.constants.CONNECTION_FAIL
import com.prac.core.common.constants.INVALID_REPOSITORY
import com.prac.core.common.constants.INVALID_TOKEN
import com.prac.core.common.constants.UNKNOWN
import com.prac.core.common.dispatcher.IODispatcher
import com.prac.core.common.mvi.model.eventModel
import com.prac.core.common.mvi.model.stateModel
import com.prac.core.common.mvi.reducer.Reducer
import com.prac.data.entity.RepoDetailEntity
import com.prac.data.repository.RepoRepository
import com.prac.data.repository.TokenRepository
import com.prac.feature.detail.di.DetailReducerAnnotation
import com.prac.feature.detail.model.Action
import com.prac.feature.detail.model.Event
import com.prac.feature.detail.model.Mutation
import com.prac.feature.detail.view.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repoRepository: RepoRepository,
    private val tokenRepository: TokenRepository,
    private val backOffWorkManager: BackOffWorkManager,
    @DetailReducerAnnotation detailReducerProcessor: Reducer<Mutation, UiState>,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val stateModel by stateModel(
        reducerProcessor = detailReducerProcessor,
        initialState = UiState()
    )

    private val eventModel by eventModel<Event>()

    internal val uiStateFlow: StateFlow<UiState> = stateModel.uiState
    internal val eventFlow: SharedFlow<Event> = eventModel.event

    fun process(action: Action) {
        when (action) {
            is Action.InternalAction.GetRepository -> getRepository()
            is Action.UserAction.OnClickUnStar -> onClickUnStar(action.repoDetailEntity)
            is Action.UserAction.OnClickStar -> onClickStar(action.repoDetailEntity)
            is Action.UserAction.DialogDismiss -> dialogDismiss()
            is Action.UserAction.LogoutDialogDismiss -> logoutDialogDismiss()
        }
    }

    private fun getRepository() {
        val userName = savedStateHandle.get<String>("userName")
        val repoName = savedStateHandle.get<String>("repoName")

        Mutation.ShowLoading.handleMutation()

        if (userName == null || repoName == null) {
            Mutation.ShowError("잘못된 접근입니다.").handleMutation()
            return
        }

        viewModelScope.launch(ioDispatcher) {
            repoRepository.getRepository(userName, repoName)
                .onSuccess {
                    handleGetRepositorySuccess(it)
                }
                .onFailure {
                    handleGetRepositoryFailure(it)
                }
        }
    }

    private fun onClickUnStar(repoDetailEntity: RepoDetailEntity) {
        viewModelScope.launch(ioDispatcher) {
            repoRepository.starLocalRepository(repoDetailEntity.id, repoDetailEntity.stargazersCount + 1)

            repoRepository.starRepository(repoDetailEntity.owner.login, repoDetailEntity.name)
                .onFailure {
                    handleStarRepositoryFailure(it, repoDetailEntity)
                }
        }
    }

    private fun onClickStar(repoDetailEntity: RepoDetailEntity) {
        viewModelScope.launch(ioDispatcher) {
            repoRepository.unStarLocalRepository(repoDetailEntity.id, repoDetailEntity.stargazersCount - 1)

            repoRepository.unStarRepository(repoDetailEntity.owner.login, repoDetailEntity.name)
                .onFailure {
                    handleUnStarRepositoryFailure(it, repoDetailEntity)
                }
        }
    }

    private fun dialogDismiss() {
        Event.Error.handleEvent()
    }

    private fun logoutDialogDismiss() {
        Event.Logout.handleEvent()
    }

    private suspend fun handleGetRepositorySuccess(repoDetailEntity: RepoDetailEntity) {
        repoRepository.getStarStateAndStarCount(repoDetailEntity.id).collect { pair ->
            val isStarred = pair.first
            val stargazersCount = pair.second

            // Room 에서 repoDetailEntity.id 값이 없을 경우에 null 을 반환한다.
            if (stargazersCount == null) {
                Mutation.ShowError(INVALID_REPOSITORY).handleMutation()
                return@collect
            }

            // List 화면에서 Star Check 가 완료되기 전에 사용자가 Detail 화면으로 넘어온 경우 null 을 반환한다.
            if (isStarred == null) {
                repoRepository.isStarred(repoDetailEntity.id, repoDetailEntity.name)
            }

            Mutation.ShowRepository(
                repository = repoDetailEntity
                    .copy(
                        isStarred = isStarred,
                        stargazersCount = stargazersCount
                    )
            ).handleMutation()
        }
    }

    private fun handleGetRepositoryFailure(t: Throwable) {
        when (t) {
            is com.prac.exception.CommonException.NetworkError -> {
                Mutation.ShowError(CONNECTION_FAIL).handleMutation()
            }
            is com.prac.exception.CommonException.AuthorizationError -> {
                Mutation.ShowError(INVALID_TOKEN).handleMutation()
            }
            is com.prac.exception.RepositoryException.NotFoundRepository -> {
                Mutation.ShowError(INVALID_REPOSITORY).handleMutation()
            }
            else -> {
                Mutation.ShowError(UNKNOWN).handleMutation()
            }
        }
    }

    private suspend fun handleStarRepositoryFailure(t: Throwable, repoDetailEntity: RepoDetailEntity) {
        when (t) {
            is com.prac.exception.CommonException.NetworkError -> {
                backOffWorkManager.addWork(
                    uniqueID = "star_${repoDetailEntity.id}",
                    work = { repoRepository.starRepository(repoDetailEntity.owner.login, repoDetailEntity.name) }
                )
            }
            is com.prac.exception.CommonException.AuthorizationError -> {
                Mutation.ShowError(INVALID_TOKEN).handleMutation()
            }
            is com.prac.exception.RepositoryException.NotFoundRepository -> {
                repoRepository.unStarLocalRepository(repoDetailEntity.id, repoDetailEntity.stargazersCount)

                Mutation.ShowError(errorMessage = INVALID_REPOSITORY).handleMutation()
            }
            else -> {
                repoRepository.unStarLocalRepository(repoDetailEntity.id, repoDetailEntity.stargazersCount)

                Mutation.ShowError(errorMessage = UNKNOWN).handleMutation()
            }
        }
    }

    private suspend fun handleUnStarRepositoryFailure(t: Throwable, repoDetailEntity: RepoDetailEntity) {
        when (t) {
            is com.prac.exception.CommonException.NetworkError -> {
                backOffWorkManager.addWork(
                    uniqueID = "star_${repoDetailEntity.id}",
                    work = { repoRepository.unStarRepository(repoDetailEntity.owner.login, repoDetailEntity.name) }
                )
            }
            is com.prac.exception.CommonException.AuthorizationError -> {
                Mutation.ShowError(INVALID_TOKEN).handleMutation()
            }
            is com.prac.exception.RepositoryException.NotFoundRepository -> {
                repoRepository.starLocalRepository(repoDetailEntity.id, repoDetailEntity.stargazersCount)

                Mutation.ShowError(errorMessage = INVALID_REPOSITORY).handleMutation()
            }
            else -> {
                repoRepository.starLocalRepository(repoDetailEntity.id, repoDetailEntity.stargazersCount)

                Mutation.ShowError(errorMessage = UNKNOWN).handleMutation()
            }
        }
    }

    private fun Mutation.handleMutation() = stateModel.process(this)

    private fun Event.handleEvent() = eventModel.process(this)

    fun logout() {
        viewModelScope.launch {
            tokenRepository.clearToken()
            backOffWorkManager.clearWork()
        }
    }

    init {
        process(Action.InternalAction.GetRepository)
    }
}