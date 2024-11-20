package com.prac.githubrepo.ui.login

import androidx.lifecycle.ViewModel
import com.prac.githubrepo.common.ActionProcessor
import com.prac.githubrepo.common.model
import com.prac.githubrepo.di.IODispatcher
import com.prac.githubrepo.di.LoginActionAnnotation
import com.prac.githubrepo.di.UserActionAnnotation
import com.prac.githubrepo.ui.login.model.Action
import com.prac.githubrepo.ui.login.model.Event
import com.prac.githubrepo.ui.login.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    @LoginActionAnnotation private val loginActionProcessor: ActionProcessor<Action, UiState, Event>,
    @UserActionAnnotation  private val userActionProcessor: ActionProcessor<Action, UiState, Event>,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
): ViewModel() {
    private val model by model(listOf(loginActionProcessor, userActionProcessor), ioDispatcher, UiState.Idle)

    internal val uiStateFlow: StateFlow<UiState> get() = model.uiState
    internal val eventFlow: SharedFlow<Event> get() = model.event

    fun process(action: Action) = model.process(action)
}