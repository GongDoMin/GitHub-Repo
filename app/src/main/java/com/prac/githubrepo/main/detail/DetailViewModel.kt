package com.prac.githubrepo.main.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prac.data.entity.RepoDetailEntity
import com.prac.data.exception.CommonException
import com.prac.data.exception.RepositoryException
import com.prac.data.repository.RepoRepository
import com.prac.data.repository.TokenRepository
import com.prac.githubrepo.constants.CONNECTION_FAIL
import com.prac.githubrepo.constants.INVALID_REPOSITORY
import com.prac.githubrepo.constants.INVALID_TOKEN
import com.prac.githubrepo.constants.UNKNOWN
import com.prac.githubrepo.di.IODispatcher
import com.prac.githubrepo.util.BackOffWorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repoRepository: RepoRepository,
    private val tokenRepository: TokenRepository,
    private val backOffWorkManager: BackOffWorkManager,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    sealed class UiState {
        data object Idle : UiState()

        data object Loading : UiState()

        data class Content(
            val repository : RepoDetailEntity
        ) : UiState()

        data class Error(
            val errorMessage: String
        ) : UiState()
    }

    sealed class SideEffect {
        data object BasicDialogDismiss : SideEffect() // IOException, 404 에러의 alert dialog 가 dismiss 되는 경우
        data object LogoutDialogDismiss : SideEffect()
        data class StarClick(val repoDetailEntity: RepoDetailEntity) : SideEffect()
        data class UnStarClick(val repoDetailEntity: RepoDetailEntity) : SideEffect()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: Flow<UiState> = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<SideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    fun setSideEffect(sideEffect: SideEffect) {
        viewModelScope.launch {
            _sideEffect.emit(sideEffect)
        }
    }

    fun getRepository(userName: String?, repoName: String?) {
        if (_uiState.value != UiState.Idle) return

        _uiState.update { UiState.Loading }

        if (userName == null || repoName == null) {
            _uiState.update { UiState.Error("잘못된 접근입니다.") }
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

    fun starRepository(repoDetailEntity: RepoDetailEntity) {
        viewModelScope.launch(ioDispatcher) {
            repoRepository.starLocalRepository(repoDetailEntity.id, repoDetailEntity.stargazersCount + 1)

            repoRepository.starRepository(repoDetailEntity.owner.login, repoDetailEntity.name)
                .onFailure {
                    handleStarRepositoryFailure(it, repoDetailEntity)
                }
        }
    }

    fun unStarRepository(repoDetailEntity: RepoDetailEntity) {
        viewModelScope.launch(ioDispatcher) {
            repoRepository.unStarLocalRepository(repoDetailEntity.id, repoDetailEntity.stargazersCount - 1)

            repoRepository.unStarRepository(repoDetailEntity.owner.login, repoDetailEntity.name)
                .onFailure {
                    handleUnStarRepositoryFailure(it, repoDetailEntity)
                }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            tokenRepository.clearToken()
            backOffWorkManager.clearWork()

            _uiState.update {
                UiState.Error(errorMessage = INVALID_TOKEN)
            }
        }
    }

    private suspend fun handleGetRepositorySuccess(repoDetailEntity: RepoDetailEntity) {
        repoRepository.getStarStateAndStarCount(repoDetailEntity.id).collect { pair ->
            val isStarred = pair.first
            val stargazersCount = pair.second

            // Room 에서 repoDetailEntity.id 값이 없을 경우에 null 을 반환한다.
            if (stargazersCount == null) {
                _uiState.update { UiState.Error(errorMessage = INVALID_REPOSITORY) }
                return@collect
            }

            // List 화면에서 Star Check 가 완료되기 전에 사용자가 Detail 화면으로 넘어온 경우 null 을 반환한다.
            if (isStarred == null) {
                repoRepository.isStarred(repoDetailEntity.id, repoDetailEntity.name)
                return@collect
            }

            _uiState.update {
                UiState.Content(repoDetailEntity.copy(isStarred = isStarred, stargazersCount = stargazersCount))
            }
        }
    }

    private fun handleGetRepositoryFailure(t: Throwable) {
        when (t) {
            is CommonException.NetworkError -> {
                _uiState.update { UiState.Error(errorMessage = CONNECTION_FAIL) }
            }
            is CommonException.AuthorizationError -> {
                logout()
            }
            is RepositoryException.NotFoundRepository -> {
                _uiState.update { UiState.Error(errorMessage = INVALID_REPOSITORY) }
            }
            else -> {
                _uiState.update { UiState.Error(errorMessage = UNKNOWN) }
            }
        }
    }

    private suspend fun handleStarRepositoryFailure(t: Throwable, repoDetailEntity: RepoDetailEntity) {
        when (t) {
            is CommonException.NetworkError -> {
                backOffWorkManager.addWork(
                    uniqueID = "star_${repoDetailEntity.id}",
                    work = { repoRepository.starRepository(repoDetailEntity.owner.login, repoDetailEntity.name) }
                )
            }
            is CommonException.AuthorizationError -> {
                logout()
            }
            is RepositoryException.NotFoundRepository -> {
                repoRepository.unStarLocalRepository(repoDetailEntity.id, repoDetailEntity.stargazersCount)

                _uiState.update { UiState.Error(errorMessage = INVALID_REPOSITORY) }
            }
            else -> {
                _uiState.update { UiState.Error(errorMessage = UNKNOWN) }
            }
        }
    }

    private suspend fun handleUnStarRepositoryFailure(t: Throwable, repoDetailEntity: RepoDetailEntity) {
        when (t) {
            is CommonException.NetworkError -> {
                backOffWorkManager.addWork(
                    uniqueID = "star_${repoDetailEntity.id}",
                    work = { repoRepository.unStarRepository(repoDetailEntity.owner.login, repoDetailEntity.name) }
                )
            }
            is CommonException.AuthorizationError -> {
                logout()
            }
            is RepositoryException.NotFoundRepository -> {
                repoRepository.starLocalRepository(repoDetailEntity.id, repoDetailEntity.stargazersCount)

                _uiState.update { UiState.Error(errorMessage = INVALID_REPOSITORY) }
            }
            else -> {
                _uiState.update { UiState.Error(errorMessage = UNKNOWN) }
            }
        }
    }
}