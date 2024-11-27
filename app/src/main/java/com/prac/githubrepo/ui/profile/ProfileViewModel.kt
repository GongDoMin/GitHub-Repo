package com.prac.githubrepo.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prac.core.common.mvi.model.eventModel
import com.prac.core.common.mvi.model.stateModel
import com.prac.data.repository.RepoRepository
import com.prac.data.repository.TokenRepository
import com.prac.core.common.mvi.reducer.Reducer
import com.prac.core.common.dispatcher.IODispatcher
import com.prac.githubrepo.di.ProfileReducerAnnotation
import com.prac.githubrepo.ui.profile.model.Action
import com.prac.githubrepo.ui.profile.model.Event
import com.prac.githubrepo.ui.profile.model.Event.*
import com.prac.githubrepo.ui.profile.model.Mutation
import com.prac.githubrepo.ui.profile.model.Mutation.*
import com.prac.githubrepo.ui.profile.view.UiState
import com.prac.githubrepo.util.BackOffWorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val repoRepository: RepoRepository,
    @ProfileReducerAnnotation private val profileReducerProcessor: Reducer<Mutation, UiState>,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    private val backOffWorkManager: BackOffWorkManager
) : ViewModel() {
    private val stateModel by stateModel(
        reducerProcessor = profileReducerProcessor,
        initialState = UiState()
    )
    private val eventModel by eventModel<Event>()

    internal val uiStateFlow: StateFlow<UiState> get() = stateModel.uiState
    internal val eventFlow: SharedFlow<Event> get() = eventModel.event

    fun process(action: Action) {
        when (action) {
            is Action.UserAction.OnClickLogoutButton -> onClickLogoutButton()
            is Action.UserAction.DialogDismiss -> dialogDismiss()
            is Action.UserAction.OnClickCheckButton -> onClickCheckButton()
        }
    }

    private fun onClickLogoutButton() {
        ShowDialog.handleMutation()
    }

    private fun dialogDismiss() {
        ShowIdle.handleMutation()
    }

    private fun onClickCheckButton() {
        viewModelScope.launch(ioDispatcher) {
            ShowLoading.handleMutation()

            tokenRepository.clearToken()
            backOffWorkManager.clearWork()
            repoRepository.clearRepositories()

            Logout.handleEvent()
        }
    }

    private fun Mutation.handleMutation() = stateModel.process(this)

    private fun Event.handleEvent() = eventModel.process(this)
}