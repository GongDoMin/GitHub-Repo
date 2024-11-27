package com.prac.githubrepo.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prac.exception.CommonException
import com.prac.data.repository.TokenRepository
import com.prac.core.common.mvi.reducer.Reducer
import com.prac.core.common.mvi.model.stateModel
import com.prac.core.common.mvi.model.eventModel
import com.prac.core.common.constants.CONNECTION_FAIL
import com.prac.core.common.constants.LOGIN_FAIL
import com.prac.core.common.dispatcher.IODispatcher
import com.prac.githubrepo.di.LoginReducerAnnotation
import com.prac.githubrepo.ui.login.model.Action
import com.prac.githubrepo.ui.login.model.Event
import com.prac.githubrepo.ui.login.model.Event.OpenBrowser
import com.prac.githubrepo.ui.login.model.Event.SuccessLogin
import com.prac.githubrepo.ui.login.model.Mutation
import com.prac.githubrepo.ui.login.model.Mutation.ShowError
import com.prac.githubrepo.ui.login.model.Mutation.ShowIdle
import com.prac.githubrepo.ui.login.model.Mutation.ShowLoading
import com.prac.githubrepo.ui.login.view.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val tokenRepository: TokenRepository,
    @LoginReducerAnnotation private val loginReducerProcessor: Reducer<Mutation, UiState>,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
): ViewModel() {
    private val stateModel by stateModel(
        reducerProcessor = loginReducerProcessor,
        initialState = UiState()
    )
    private val eventModel by eventModel<Event>()

    internal val uiStateFlow: StateFlow<UiState> get() = stateModel.uiState
    internal val eventFlow: SharedFlow<Event> get() = eventModel.event

    fun process(action: Action) {
        when (action) {
            is Action.UserAction.OnClickLoginButton -> onClickLoginButton()
            is Action.UserAction.DialogDismiss -> dialogDismiss()
            is Action.InternalAction.AuthenticateOAuth -> authenticateOAuth(action.code)
            is Action.InternalAction.CheckAutoLogin -> checkAuthLogin()
        }
    }

    private fun onClickLoginButton() {
        OpenBrowser.handleEvent()
    }

    private fun dialogDismiss() {
        ShowIdle.handleMutation()
    }

    private fun authenticateOAuth(code: String) {
        viewModelScope.launch(ioDispatcher) {
            ShowLoading.handleMutation()

            tokenRepository.authorizeOAuth(code = code)
                .onSuccess {
                    SuccessLogin.handleEvent()
                }.onFailure {
                    val errorMessage = handleLoginErrorMessage(it)
                    ShowError(errorMessage).handleMutation()
                }
        }
    }

    private fun checkAuthLogin() {
        viewModelScope.launch(ioDispatcher) {
            if (tokenRepository.isLoggedIn()) SuccessLogin.handleEvent()
        }
    }

    private fun handleLoginErrorMessage(t: Throwable) =
        when (t) {
            is com.prac.exception.CommonException.NetworkError -> CONNECTION_FAIL
            else -> LOGIN_FAIL
        }

    private fun Mutation.handleMutation() = stateModel.process(this)

    private fun Event.handleEvent() = eventModel.process(this)

    init {
        process(Action.InternalAction.CheckAutoLogin)
    }
}
