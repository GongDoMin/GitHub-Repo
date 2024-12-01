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
import com.prac.core.navigation.Routes.HOME.DETAIL.Companion.REPO_NAME
import com.prac.core.navigation.Routes.HOME.DETAIL.Companion.USER_NAME
import com.prac.data.model.RepoDetailModel
import com.prac.data.repository.RepoRepository
import com.prac.data.repository.TokenRepository
import com.prac.exception.CommonException
import com.prac.exception.RepositoryException
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
            is Action.UserAction.OnClickUnStar -> handleClickUnStar(action.repoDetailModel)
            is Action.UserAction.OnClickStar -> handleClickStar(action.repoDetailModel)
            is Action.UserAction.DialogDismiss -> handleDialogDismiss()
            is Action.UserAction.LogoutDialogDismiss -> handleLogoutDialogDismiss()
        }
    }

    private fun getRepository() {
        val userName = savedStateHandle.get<String>(USER_NAME)
        val repoName = savedStateHandle.get<String>(REPO_NAME)

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

    private fun handleClickUnStar(repoDetailModel: RepoDetailModel) {
        viewModelScope.launch(ioDispatcher) {
            repoRepository.starLocalRepository(repoDetailModel.id, repoDetailModel.stargazersCount + 1)

            repoRepository.starRepository(repoDetailModel.owner.login, repoDetailModel.name)
                .onFailure {
                    handleStarRepositoryFailure(it, repoDetailModel)
                }
        }
    }

    private fun handleClickStar(repoDetailModel: RepoDetailModel) {
        viewModelScope.launch(ioDispatcher) {
            repoRepository.unStarLocalRepository(repoDetailModel.id, repoDetailModel.stargazersCount - 1)

            repoRepository.unStarRepository(repoDetailModel.owner.login, repoDetailModel.name)
                .onFailure {
                    handleUnStarRepositoryFailure(it, repoDetailModel)
                }
        }
    }

    private fun handleDialogDismiss() {
        Event.Error.handleEvent()
    }

    private fun handleLogoutDialogDismiss() {
        Event.Logout.handleEvent()
    }

    private suspend fun handleGetRepositorySuccess(repoDetailModel: RepoDetailModel) {
        repoRepository.getStarStateAndStarCount(repoDetailModel.id).collect { pair ->
            val isStarred = pair.first
            val stargazersCount = pair.second

            // Room 에서 repoDetailEntity.id 값이 없을 경우에 null 을 반환한다.
            if (stargazersCount == null) {
                Mutation.ShowError(INVALID_REPOSITORY).handleMutation()
                return@collect
            }

            // List 화면에서 Star Check 가 완료되기 전에 사용자가 Detail 화면으로 넘어온 경우 null 을 반환한다.
            if (isStarred == null) {
                repoRepository.isStarred(repoDetailModel.id, repoDetailModel.name)
            }

            Mutation.ShowRepository(
                repository = repoDetailModel
                    .copy(
                        isStarred = isStarred,
                        stargazersCount = stargazersCount
                    )
            ).handleMutation()
        }
    }

    private fun handleGetRepositoryFailure(t: Throwable) {
        when (t) {
            is CommonException.NetworkError -> {
                Mutation.ShowError(CONNECTION_FAIL).handleMutation()
            }
            is CommonException.AuthorizationError -> {
                Mutation.ShowError(INVALID_TOKEN).handleMutation()
            }
            is RepositoryException.NotFoundRepository -> {
                Mutation.ShowError(INVALID_REPOSITORY).handleMutation()
            }
            else -> {
                Mutation.ShowError(UNKNOWN).handleMutation()
            }
        }
    }

    private suspend fun handleStarRepositoryFailure(t: Throwable, repoDetailModel: RepoDetailModel) {
        when (t) {
            is CommonException.NetworkError -> {
                backOffWorkManager.addWork(
                    uniqueID = "star_${repoDetailModel.id}",
                    work = { repoRepository.starRepository(repoDetailModel.owner.login, repoDetailModel.name) }
                )
            }
            is CommonException.AuthorizationError -> {
                Mutation.ShowError(INVALID_TOKEN).handleMutation()
            }
            is RepositoryException.NotFoundRepository -> {
                repoRepository.unStarLocalRepository(repoDetailModel.id, repoDetailModel.stargazersCount)

                Mutation.ShowError(errorMessage = INVALID_REPOSITORY).handleMutation()
            }
            else -> {
                repoRepository.unStarLocalRepository(repoDetailModel.id, repoDetailModel.stargazersCount)

                Mutation.ShowError(errorMessage = UNKNOWN).handleMutation()
            }
        }
    }

    private suspend fun handleUnStarRepositoryFailure(t: Throwable, repoDetailModel: RepoDetailModel) {
        when (t) {
            is CommonException.NetworkError -> {
                backOffWorkManager.addWork(
                    uniqueID = "star_${repoDetailModel.id}",
                    work = { repoRepository.unStarRepository(repoDetailModel.owner.login, repoDetailModel.name) }
                )
            }
            is CommonException.AuthorizationError -> {
                Mutation.ShowError(INVALID_TOKEN).handleMutation()
            }
            is RepositoryException.NotFoundRepository -> {
                repoRepository.starLocalRepository(repoDetailModel.id, repoDetailModel.stargazersCount)

                Mutation.ShowError(errorMessage = INVALID_REPOSITORY).handleMutation()
            }
            else -> {
                repoRepository.starLocalRepository(repoDetailModel.id, repoDetailModel.stargazersCount)

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