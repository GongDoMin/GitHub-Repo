package com.prac.feature.main

import android.util.SparseArray
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.prac.core.common.backoff.BackOffWorkManager
import com.prac.core.common.constants.INVALID_REPOSITORY
import com.prac.core.common.constants.INVALID_TOKEN
import com.prac.core.common.constants.UNKNOWN
import com.prac.core.common.dispatcher.IODispatcher
import com.prac.core.common.mvi.model.eventModel
import com.prac.core.common.mvi.model.stateModel
import com.prac.core.common.mvi.reducer.Reducer
import com.prac.data.model.RepoModel
import com.prac.data.repository.RepoRepository
import com.prac.data.repository.TokenRepository
import com.prac.feature.main.di.MainReducerAnnotation
import com.prac.feature.main.model.Action
import com.prac.feature.main.model.Event
import com.prac.feature.main.model.Mutation
import com.prac.feature.main.view.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repoRepository: RepoRepository,
    private val tokenRepository: TokenRepository,
    private val backOffWorkManager: BackOffWorkManager,
    @MainReducerAnnotation private val mainReducerProcessor: Reducer<Mutation, UiState>,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
): ViewModel() {
    private val stateModel by stateModel(
        reducerProcessor = mainReducerProcessor,
        initialState = UiState()
    )

    private val eventModel by eventModel<Event>()

    internal val uiStateFlow: StateFlow<UiState> = stateModel.uiState
    internal val eventFlow: SharedFlow<Event> = eventModel.event

    private val _repositories = MutableStateFlow<PagingData<RepoModel>>(PagingData.empty())
    val repositories = _repositories.asStateFlow()

    private val _starRequestJobManager: SparseArray<Unit> = SparseArray()

    fun process(action: Action) {
        when (action) {
            is Action.InternalAction.Load -> load()
            is Action.InternalAction.FetchStarState -> fetchStarState(action.repoModel)
            is Action.UserAction.OnClickRepository -> handleClickRepository(action.repoModel)
            is Action.UserAction.OnClickUnStar -> handleClickUnStar(action.repoModel)
            is Action.UserAction.OnClickStar -> handleClickStar(action.repoModel)
            is Action.UserAction.OnClickRetry -> handleClickRetry()
            is Action.UserAction.DialogDismiss -> handleDialogDismiss()
            is Action.UserAction.LogoutDialogDismiss -> handleLogoutDialogDismiss()
        }
    }

    private fun load() {
        viewModelScope.launch {
            repoRepository.getRepositories().cachedIn(viewModelScope).collect { pagingData ->
                _repositories.update { pagingData }
            }
        }
    }

    private fun fetchStarState(repoModel: RepoModel) {
        if (_starRequestJobManager[repoModel.id] == null) {
            _starRequestJobManager.put(repoModel.id, Unit)

            viewModelScope.launch(ioDispatcher) {
                repoRepository.isStarred(repoModel.id, repoModel.name)
            }
        }
    }

    private fun handleClickRepository(repoModel: RepoModel) {
        Event.OpenRepositoryDetail(repoModel.owner.login, repoModel.name).handleEvent()
    }

    private fun handleClickUnStar(repoModel: RepoModel) {
        viewModelScope.launch(ioDispatcher) {
            repoRepository.starLocalRepository(repoModel.id, repoModel.stargazersCount + 1)

            repoRepository.starRepository(repoModel.owner.login, repoModel.name)
                .onFailure {
                    handleStarRepositoryFailure(it, repoModel)
                }
        }
    }

    private fun handleClickStar(repoModel: RepoModel) {
        viewModelScope.launch(ioDispatcher) {
            repoRepository.unStarLocalRepository(repoModel.id, repoModel.stargazersCount - 1)

            repoRepository.unStarRepository(repoModel.owner.login, repoModel.name)
                .onFailure {
                    handleUnStarRepositoryFailure(it, repoModel)
                }
        }
    }

    private fun handleClickRetry() {
        Event.Retry.handleEvent()
    }

    private fun handleDialogDismiss() {
        Mutation.ShowRepositories.handleMutation()
    }

    private fun handleLogoutDialogDismiss() {
        Event.Logout.handleEvent()
    }

    private suspend fun handleStarRepositoryFailure(t: Throwable, repoModel: RepoModel) {
        when (t) {
            is com.prac.exception.CommonException.NetworkError -> {
                backOffWorkManager.addWork(
                    uniqueID = "star_${repoModel.id}",
                    work = { repoRepository.starRepository(repoModel.owner.login, repoModel.name) }
                )
            }
            is com.prac.exception.CommonException.AuthorizationError -> {
                Event.Logout.handleEvent()
            }
            is com.prac.exception.RepositoryException.NotFoundRepository -> {
                repoRepository.unStarLocalRepository(repoModel.id, repoModel.stargazersCount)

                Mutation.ShowError(INVALID_REPOSITORY).handleMutation()
            }
            else -> {
                repoRepository.unStarLocalRepository(repoModel.id, repoModel.stargazersCount)

                Mutation.ShowError(UNKNOWN).handleMutation()
            }
        }
    }

    private suspend fun handleUnStarRepositoryFailure(t: Throwable, repoModel: RepoModel) {
        when (t) {
            is com.prac.exception.CommonException.NetworkError -> {
                backOffWorkManager.addWork(
                    uniqueID = "star_${repoModel.id}",
                    work = { repoRepository.unStarRepository(repoModel.owner.login, repoModel.name) }
                )
            }
            is com.prac.exception.CommonException.AuthorizationError -> {
                Event.Logout.handleEvent()
            }
            is com.prac.exception.RepositoryException.NotFoundRepository -> {
                repoRepository.starLocalRepository(repoModel.id, repoModel.stargazersCount)

                Mutation.ShowError(INVALID_REPOSITORY).handleMutation()
            }
            else -> {
                repoRepository.starLocalRepository(repoModel.id, repoModel.stargazersCount)

                Mutation.ShowError(UNKNOWN).handleMutation()
            }
        }
    }

    private fun Mutation.handleMutation() = stateModel.process(this)

    private fun Event.handleEvent() = eventModel.process(this)

    fun handleLoadStates(combinedLoadStates: CombinedLoadStates) : LoadState {
        if (combinedLoadStates.refresh is LoadState.Error) {
            if ((combinedLoadStates.refresh as LoadState.Error).error !is IOException) {
                Event.Logout.handleEvent()
                return combinedLoadStates.refresh
            }
            return combinedLoadStates.refresh
        }

        if (combinedLoadStates.refresh is LoadState.Loading) {
            return combinedLoadStates.refresh
        }

        if (combinedLoadStates.append is LoadState.Error) {
            if ((combinedLoadStates.append as LoadState.Error).error !is IOException) {
                Event.Logout.handleEvent()
                return combinedLoadStates.append
            }
            return combinedLoadStates.append
        }

        return combinedLoadStates.append
    }

    fun logout() {
        viewModelScope.launch {
            tokenRepository.clearToken()
            backOffWorkManager.clearWork()

            Mutation.ShowError(INVALID_TOKEN).handleMutation()
        }
    }

    init {
        process(Action.InternalAction.Load)
    }
}