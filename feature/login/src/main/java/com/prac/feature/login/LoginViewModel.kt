package com.prac.feature.login

import androidx.lifecycle.ViewModel
import com.prac.core.common.dispatcher.IODispatcher
import com.prac.core.common.mvi.action.ActionProcessor
import com.prac.core.common.mvi.model.model
import com.prac.core.common.mvi.reducer.Reducer
import com.prac.feature.login.di.LoginActionAnnotation
import com.prac.feature.login.di.LoginReducerAnnotation
import com.prac.feature.login.model.Action
import com.prac.feature.login.model.Event
import com.prac.feature.login.model.Mutation
import com.prac.feature.login.view.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    @LoginReducerAnnotation private val loginReducerProcessor: Reducer<Mutation, UiState>,
    @LoginActionAnnotation private val loginActionProcessor: ActionProcessor<Action, Mutation, Event>,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
): ViewModel() {
    private val model by model(
        reducerProcessor = loginReducerProcessor,
        actionProcessor = loginActionProcessor,
        initialState = UiState.Idle,
        dispatcher = ioDispatcher
    )

    val uiStateFlow = model.uiState
    val eventFlow = model.event

    fun process(action: Action) = model.process(action)

    init {
        process(Action.InternalAction.CheckAutoLogin)
    }
}
