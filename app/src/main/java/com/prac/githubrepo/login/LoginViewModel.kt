package com.prac.githubrepo.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prac.data.exception.CommonException
import com.prac.data.repository.TokenRepository
import com.prac.githubrepo.constants.CONNECTION_FAIL
import com.prac.githubrepo.constants.LOGIN_FAIL
import com.prac.githubrepo.constants.UNKNOWN
import com.prac.githubrepo.di.IODispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val tokenRepository: TokenRepository,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
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

    private val _event = MutableSharedFlow<Event>()
    val event = _event.asSharedFlow()

    private val _sideEffect = MutableSharedFlow<SideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    init {
        checkAutoLogin()
    }

    fun setUiState(uiState: UiState) {
        _uiState.update { uiState }
    }

    private fun setEvent(event: Event) {
        viewModelScope.launch {
            _event.emit(event)
        }
    }

    fun setSideEffect(sideEffect: SideEffect) {
        viewModelScope.launch {
            _sideEffect.emit(sideEffect)
        }
    }

    fun loginWithGitHub(code: String) {
        viewModelScope.launch(ioDispatcher) {
            if (uiState.value != UiState.Idle) return@launch

            setUiState(UiState.Loading)

            tokenRepository.authorizeOAuth(code = code)
                .onSuccess {
                    setEvent(Event.Success)
                }.onFailure {
                    handleLoginError(it)
                }
        }
    }

    private fun checkAutoLogin() {
        viewModelScope.launch(ioDispatcher) {
            if (uiState.value != UiState.Idle) return@launch

            if (tokenRepository.isLoggedIn()) setEvent(Event.Success)
        }
    }

    private fun handleLoginError(t: Throwable) {
        when (t) {
            is CommonException.NetworkError -> {
                setUiState(UiState.Error(errorMessage = CONNECTION_FAIL))
            }
            is CommonException.AuthorizationError -> {
                setUiState(UiState.Error(errorMessage = LOGIN_FAIL))
            }
            else -> {
                setUiState(UiState.Error(errorMessage = UNKNOWN))
            }
        }
    }
}