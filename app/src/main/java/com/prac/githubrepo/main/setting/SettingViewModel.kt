package com.prac.githubrepo.main.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prac.data.repository.RepoRepository
import com.prac.data.repository.TokenRepository
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
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val repoRepository: RepoRepository,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    private val backOffWorkManager: BackOffWorkManager
) : ViewModel() {
    sealed class UiState {
        data object Idle : UiState()

        data object Loading : UiState()

        data class Dialog(
            val message : String
        ) : UiState()
    }

    sealed class Event {
        data object Success : Event()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<Event>()
    val event = _event.asSharedFlow()

    fun setUiState(uiState: UiState) {
        _uiState.update { uiState }
    }

    fun logout() {
        viewModelScope.launch(ioDispatcher) {
            _uiState.update { UiState.Loading }

            tokenRepository.clearToken()
            backOffWorkManager.clearWork()
            repoRepository.clearRepositories()

            _event.emit(Event.Success)
        }
    }
}