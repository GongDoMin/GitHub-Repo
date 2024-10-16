package com.prac.githubrepo.main

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
import com.prac.githubrepo.constants.INVALID_REPOSITORY
import com.prac.githubrepo.constants.INVALID_TOKEN
import com.prac.githubrepo.constants.UNKNOWN
import com.prac.githubrepo.di.IODispatcher
import com.prac.githubrepo.util.BackOffWorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
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
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
): ViewModel() {
    sealed class UiState {
        data object Idle : UiState()

        data class Content(
            val repositories : PagingData<RepoEntity>,
            val loadState: LoadState? = null,
            val dialogMessage: String = ""
        ) : UiState()
    }

    sealed class SideEffect {
        data object LogoutDialogDismiss : SideEffect()
        data object StarDialogDismiss : SideEffect()
        data class StarClick(val repoEntity: RepoEntity) : SideEffect()
        data class UnStarClick(val repoEntity: RepoEntity) : SideEffect()
        data class RepositoryClick(val repoEntity: RepoEntity) : SideEffect()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<SideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    fun setSideEffect(sideEffect: SideEffect) {
        viewModelScope.launch {
            _sideEffect.emit(sideEffect)
        }
    }

    private fun getRepositories() {
        viewModelScope.launch {
            if (_uiState.value != UiState.Idle) return@launch

            repoRepository.getRepositories().cachedIn(viewModelScope).collect { pagingData ->
                _uiState.update { UiState.Content(pagingData) }
            }
        }
    }

    private fun updateLoadState(loadState: LoadState) {
        if (_uiState.value !is UiState.Content) return

        _uiState.update {
            (it as UiState.Content).copy(loadState = loadState)
        }
    }

    fun starRepository(repoEntity: RepoEntity) {
        viewModelScope.launch(ioDispatcher) {
            repoRepository.starLocalRepository(repoEntity.id, repoEntity.stargazersCount + 1)

            repoRepository.starRepository(repoEntity.owner.login, repoEntity.name)
                .onFailure {
                    handleStarRepositoryFailure(it, repoEntity)
                }
        }
    }

    fun unStarRepository(repoEntity: RepoEntity) {
        viewModelScope.launch(ioDispatcher) {
            repoRepository.unStarLocalRepository(repoEntity.id, repoEntity.stargazersCount - 1)

            repoRepository.unStarRepository(repoEntity.owner.login, repoEntity.name)
                .onFailure {
                    handleUnStarRepositoryFailure(it, repoEntity)
                }
        }
    }

    fun handleLoadStates(combinedLoadStates: CombinedLoadStates) {
        if (combinedLoadStates.refresh is LoadState.Error) {
            if ((combinedLoadStates.refresh as LoadState.Error).error !is IOException) {
                viewModelScope.launch(ioDispatcher) {
                    logout()
                }
                return
            }
            updateLoadState(combinedLoadStates.refresh)
        }

        if (combinedLoadStates.append is LoadState.Error) {
            if ((combinedLoadStates.append as LoadState.Error).error !is IOException) {
                viewModelScope.launch(ioDispatcher) {
                    logout()
                }
                return
            }
            updateLoadState(combinedLoadStates.append)
        }

        updateLoadState(combinedLoadStates.append)
    }

    private suspend fun logout() {
        tokenRepository.clearToken()
        backOffWorkManager.clearWork()

        _uiState.update {
            (it as UiState.Content).copy(dialogMessage = INVALID_TOKEN)
        }
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
                logout()
            }
            is RepositoryException.NotFoundRepository -> {
                repoRepository.unStarLocalRepository(repoEntity.id, repoEntity.stargazersCount)

                _uiState.update { (it as UiState.Content).copy(dialogMessage = INVALID_REPOSITORY) }
            }
            else -> {
                repoRepository.unStarLocalRepository(repoEntity.id, repoEntity.stargazersCount)

                _uiState.update { (it as UiState.Content).copy(dialogMessage = UNKNOWN) }
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
                logout()
            }
            is RepositoryException.NotFoundRepository -> {
                repoRepository.starLocalRepository(repoEntity.id, repoEntity.stargazersCount)

                _uiState.update { (it as UiState.Content).copy(dialogMessage = INVALID_REPOSITORY) }
            }
            else -> {
                repoRepository.starLocalRepository(repoEntity.id, repoEntity.stargazersCount)

                _uiState.update { (it as UiState.Content).copy(dialogMessage = UNKNOWN) }
            }
        }
    }

    init {
        getRepositories()
    }
}