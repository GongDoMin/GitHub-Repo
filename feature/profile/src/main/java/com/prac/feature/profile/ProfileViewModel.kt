package com.prac.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prac.core.common.backoff.BackOffWorkManager
import com.prac.core.common.dispatcher.IODispatcher
import com.prac.core.common.mvi.action.ActionProcessor
import com.prac.core.common.mvi.model.eventModel
import com.prac.core.common.mvi.model.model
import com.prac.core.common.mvi.model.stateModel
import com.prac.core.common.mvi.reducer.Reducer
import com.prac.data.repository.RepoRepository
import com.prac.data.repository.TokenRepository
import com.prac.feature.profile.di.ProfileActionAnnotation
import com.prac.feature.profile.di.ProfileReducerAnnotation
import com.prac.feature.profile.model.Action
import com.prac.feature.profile.model.Event
import com.prac.feature.profile.model.Event.Logout
import com.prac.feature.profile.model.Mutation
import com.prac.feature.profile.model.Mutation.ShowDialog
import com.prac.feature.profile.model.Mutation.ShowIdle
import com.prac.feature.profile.model.Mutation.ShowLoading
import com.prac.feature.profile.view.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    @ProfileReducerAnnotation private val profileReducerProcessor: Reducer<Mutation, UiState>,
    @ProfileActionAnnotation private val profileActionProcessor: ActionProcessor<Action, Mutation, Event>,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {
    private val model = model(
        reducerProcessor = profileReducerProcessor,
        actionProcessor = profileActionProcessor,
        initialState = UiState(),
        dispatcher = ioDispatcher
    )

    internal val uiStateFlow: StateFlow<UiState> get() = model.uiState
    internal val eventFlow: SharedFlow<Event> get() = model.event

    fun process(action: Action) = model.process(action)
}