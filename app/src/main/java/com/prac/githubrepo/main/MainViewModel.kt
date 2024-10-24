package com.prac.githubrepo.main

import android.util.SparseArray
import android.util.SparseBooleanArray
import android.util.SparseIntArray
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
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
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
    data class Content(
        val repositories : Flow<PagingData<RepoEntity>> = flow { emit(PagingData.empty()) },
        val dialogMessage: String = ""
    )

    sealed class SideEffect {
        data object LogoutDialogDismiss : SideEffect()
        data object StarDialogDismiss : SideEffect()
        data class StarClick(val repoEntity: RepoEntity) : SideEffect()
        data class UnStarClick(val repoEntity: RepoEntity) : SideEffect()
        data class RepositoryClick(val repoEntity: RepoEntity) : SideEffect()
    }

    private val _uiState = MutableStateFlow(Content())
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<SideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    private val _starRequestJobManager: SparseArray<Unit> = SparseArray()

    fun setSideEffect(sideEffect: SideEffect) {
        viewModelScope.launch {
            _sideEffect.emit(sideEffect)
        }
    }

    private fun getRepositories() {
        viewModelScope.launch {
            _uiState.update { Content(repositories = repoRepository.getRepositories().cachedIn(viewModelScope)) }
        }
    }

    fun fetchStarState(repoEntity: RepoEntity) {
        if (_starRequestJobManager[repoEntity.id] == null) {
            _starRequestJobManager.put(repoEntity.id, Unit)

            viewModelScope.launch(ioDispatcher) {
                repoRepository.isStarred(repoEntity.id, repoEntity.name)

                _starRequestJobManager.remove(repoEntity.id)
            }
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

    fun handleLoadStates(combinedLoadStates: CombinedLoadStates) : LoadState? {
        if (combinedLoadStates.refresh is LoadState.Error) {
            if ((combinedLoadStates.refresh as LoadState.Error).error !is IOException) {
                viewModelScope.launch(ioDispatcher) {
                    logout()
                }
                return null
            }
            return combinedLoadStates.refresh
        }

        if (combinedLoadStates.refresh is LoadState.Loading) {
            return combinedLoadStates.refresh
        }

        if (combinedLoadStates.append is LoadState.Error) {
            if ((combinedLoadStates.append as LoadState.Error).error !is IOException) {
                viewModelScope.launch(ioDispatcher) {
                    logout()
                }
                return null
            }
            return combinedLoadStates.append
        }

        return combinedLoadStates.append
    }

    private suspend fun logout() {
        tokenRepository.clearToken()
        backOffWorkManager.clearWork()

        _uiState.update {
            it.copy(dialogMessage = INVALID_TOKEN)
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

                _uiState.update { it.copy(dialogMessage = INVALID_REPOSITORY) }
            }
            else -> {
                repoRepository.unStarLocalRepository(repoEntity.id, repoEntity.stargazersCount)

                _uiState.update { it.copy(dialogMessage = UNKNOWN) }
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

                _uiState.update { it.copy(dialogMessage = INVALID_REPOSITORY) }
            }
            else -> {
                repoRepository.starLocalRepository(repoEntity.id, repoEntity.stargazersCount)

                _uiState.update { it.copy(dialogMessage = UNKNOWN) }
            }
        }
    }

    init {
        getRepositories()
    }
}