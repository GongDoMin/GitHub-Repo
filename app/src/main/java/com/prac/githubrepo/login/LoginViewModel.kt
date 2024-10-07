package com.prac.githubrepo.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prac.data.repository.TokenRepository
import com.prac.githubrepo.constants.CONNECTION_FAIL
import com.prac.githubrepo.constants.LOGIN_FAIL
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val tokenRepository: TokenRepository
): ViewModel() {
    sealed class UiState {
        data object Idle : UiState()

        data object Loading : UiState()

        data class Error(
            val errorMessage : String
        ) : UiState()
    }

    sealed class Event {
        data object Success : Event()
    }

    sealed class SideEffect {
        data object LoginButtonClick : SideEffect()
        data object ErrorAlertDialogDismiss : SideEffect()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<Event>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val event = _event.asSharedFlow()

    private val _sideEffect = MutableSharedFlow<SideEffect>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val sideEffect = _sideEffect.asSharedFlow()

    init {
        checkAutoLogin()
    }

    fun setUiState(uiState: UiState) {
        _uiState.update { uiState }
    }

    fun setEvent(event: Event) {
        _event.tryEmit(event)
    }

    fun setSideEffect(sideEffect: SideEffect) {
        _sideEffect.tryEmit(sideEffect)
    }

    fun loginWithGitHub(code: String) {
        viewModelScope.launch {
            if (uiState.value != UiState.Idle) return@launch

            setUiState(UiState.Loading)

            tokenRepository.authorizeOAuth(code = code)
                .onSuccess {
                    setEvent(Event.Success)
                }.onFailure {
                    when (it) {
                        is IOException -> {
                            setUiState(UiState.Error(errorMessage = CONNECTION_FAIL))
                        }
                        else -> {
                            setUiState(UiState.Error(errorMessage = LOGIN_FAIL))
                        }
                    }
                }
        }
    }

    private fun checkAutoLogin() {
        viewModelScope.launch {
            if (uiState.value != UiState.Idle) return@launch

            if (tokenRepository.isLoggedIn()) setEvent(Event.Success)
        }
    }

}