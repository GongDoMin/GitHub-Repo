package com.prac.githubrepo.ui.home.main

import android.util.SparseArray
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.prac.data.entity.RepoEntity
import com.prac.data.exception.CommonException
import com.prac.data.exception.RepositoryException
import com.prac.data.repository.RepoRepository
import com.prac.data.repository.TokenRepository
import com.prac.core.common.mvi.reducer.Reducer
import com.prac.core.common.mvi.model.stateModel
import com.prac.core.common.mvi.model.eventModel
import com.prac.core.common.constants.INVALID_REPOSITORY
import com.prac.core.common.constants.INVALID_TOKEN
import com.prac.core.common.constants.UNKNOWN
import com.prac.githubrepo.di.IODispatcher
import com.prac.githubrepo.di.MainReducerAnnotation
import com.prac.githubrepo.ui.home.main.model.Action
import com.prac.githubrepo.ui.home.main.model.Event
import com.prac.githubrepo.ui.home.main.model.Mutation
import com.prac.githubrepo.ui.home.main.view.UiState
import com.prac.githubrepo.util.BackOffWorkManager
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

    private val _repositories = MutableStateFlow<PagingData<RepoEntity>>(PagingData.empty())
    val repositories = _repositories.asStateFlow()

    private val _starRequestJobManager: SparseArray<Unit> = SparseArray()

    fun process(action: Action) {
        when (action) {
            is Action.InternalAction.Load -> load()
            is Action.InternalAction.FetchStarState -> fetchStarState(action.repoEntity)
            is Action.InternalAction.UpdateRepositories -> updateRepositories(action.repositories, action.loadState)
            is Action.UserAction.OnClickRepository -> onClickRepository(action.repoEntity)
            is Action.UserAction.OnClickUnStar -> onClickUnStar(action.repoEntity)
            is Action.UserAction.OnClickStar -> onClickStar(action.repoEntity)
            is Action.UserAction.OnClickRetry -> onClickRetry()
            is Action.UserAction.DialogDismiss -> dialogDismiss()
            is Action.UserAction.LogoutDialogDismiss -> logoutDialogDismiss()
        }
    }

    private fun load() {
        viewModelScope.launch {
            repoRepository.getRepositories().cachedIn(viewModelScope).collect { pagingData ->
                _repositories.update { pagingData }
            }
        }
    }

    private fun fetchStarState(repoEntity: RepoEntity) {
        _starRequestJobManager.put(repoEntity.id, Unit)

        viewModelScope.launch(ioDispatcher) {
            repoRepository.isStarred(repoEntity.id, repoEntity.name)

            _starRequestJobManager.remove(repoEntity.id)
        }
    }

    private fun updateRepositories(repositories: List<RepoEntity>, loadState: LoadState) {
        viewModelScope.launch(ioDispatcher) {
            Mutation.UpdateRepositories(repositories, loadState).handleMutation()
        }
    }

    private fun onClickRepository(repoEntity: RepoEntity) {
        Event.OpenRepositoryDetail(repoEntity.owner.login, repoEntity.name).handleEvent()
    }

    private fun onClickUnStar(repoEntity: RepoEntity) {
        viewModelScope.launch(ioDispatcher) {
            repoRepository.starLocalRepository(repoEntity.id, repoEntity.stargazersCount + 1)

            repoRepository.starRepository(repoEntity.owner.login, repoEntity.name)
                .onFailure {
                    handleStarRepositoryFailure(it, repoEntity)
                }
        }
    }

    private fun onClickStar(repoEntity: RepoEntity) {
        viewModelScope.launch(ioDispatcher) {
            repoRepository.unStarLocalRepository(repoEntity.id, repoEntity.stargazersCount - 1)

            repoRepository.unStarRepository(repoEntity.owner.login, repoEntity.name)
                .onFailure {
                    handleUnStarRepositoryFailure(it, repoEntity)
                }
        }
    }

    private fun onClickRetry() {
        Event.Reload.handleEvent()
    }

    private fun dialogDismiss() {
        Mutation.ShowRepositories.handleMutation()
    }

    private fun logoutDialogDismiss() {
        Event.Logout.handleEvent()
    }

    private suspend fun handleStarRepositoryFailure(t: Throwable, repoEntity: RepoEntity) {
        when (t) {
            is CommonException.NetworkError -> {
                backOffWorkManager.addWork(
                    uniqueID = "star_${repoEntity.id}",
                    work = { repoRepository.starRepository(repoEntity.owner.login, repoEntity.name) }
                )
            }
            is CommonException.AuthorizationError -> {
                Event.Logout.handleEvent()
            }
            is RepositoryException.NotFoundRepository -> {
                repoRepository.unStarLocalRepository(repoEntity.id, repoEntity.stargazersCount)

                Mutation.ShowError(INVALID_REPOSITORY).handleMutation()
            }
            else -> {
                repoRepository.unStarLocalRepository(repoEntity.id, repoEntity.stargazersCount)

                Mutation.ShowError(UNKNOWN).handleMutation()
            }
        }
    }

    private suspend fun handleUnStarRepositoryFailure(t: Throwable, repoEntity: RepoEntity) {
        when (t) {
            is CommonException.NetworkError -> {
                backOffWorkManager.addWork(
                    uniqueID = "star_${repoEntity.id}",
                    work = { repoRepository.unStarRepository(repoEntity.owner.login, repoEntity.name) }
                )
            }
            is CommonException.AuthorizationError -> {
                Event.Logout.handleEvent()
            }
            is RepositoryException.NotFoundRepository -> {
                repoRepository.starLocalRepository(repoEntity.id, repoEntity.stargazersCount)

                Mutation.ShowError(INVALID_REPOSITORY).handleMutation()
            }
            else -> {
                repoRepository.starLocalRepository(repoEntity.id, repoEntity.stargazersCount)

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