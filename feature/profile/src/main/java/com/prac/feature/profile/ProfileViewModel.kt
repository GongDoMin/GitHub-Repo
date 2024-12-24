package com.prac.feature.profile

import androidx.lifecycle.ViewModel
import com.prac.core.common.dispatcher.IODispatcher
import com.prac.core.common.mvi.action.ActionProcessor
import com.prac.core.common.mvi.model.model
import com.prac.core.common.mvi.reducer.Reducer
import com.prac.feature.profile.di.ProfileActionAnnotation
import com.prac.feature.profile.di.ProfileReducerAnnotation
import com.prac.feature.profile.model.Action
import com.prac.feature.profile.model.Event
import com.prac.feature.profile.model.Mutation
import com.prac.feature.profile.view.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    @ProfileReducerAnnotation private val profileReducerProcessor: Reducer<Mutation, UiState>,
    @ProfileActionAnnotation private val profileActionProcessor: ActionProcessor<Action, Mutation, Event>,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {
    private val model by model(
        reducerProcessor = profileReducerProcessor,
        actionProcessor = profileActionProcessor,
        initialState = UiState.Idle,
        dispatcher = ioDispatcher
    )

    val uiStateFlow = model.uiState
    val eventFlow = model.event

    fun process(action: Action) = model.process(action)
}