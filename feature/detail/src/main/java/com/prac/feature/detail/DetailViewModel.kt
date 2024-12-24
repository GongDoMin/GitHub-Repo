package com.prac.feature.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.prac.core.common.dispatcher.IODispatcher
import com.prac.core.common.mvi.action.ActionProcessor
import com.prac.core.common.mvi.model.model
import com.prac.core.common.mvi.reducer.Reducer
import com.prac.core.navigation.Routes.HOME.DETAIL.Companion.REPO_NAME
import com.prac.core.navigation.Routes.HOME.DETAIL.Companion.USER_NAME
import com.prac.feature.detail.di.DetailActionAnnotation
import com.prac.feature.detail.di.DetailReducerAnnotation
import com.prac.feature.detail.model.Action
import com.prac.feature.detail.model.Event
import com.prac.feature.detail.model.Mutation
import com.prac.feature.detail.view.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
internal class DetailViewModel @Inject constructor(
    @DetailReducerAnnotation private val detailReducerProcessor: Reducer<Mutation, UiState>,
    @DetailActionAnnotation private val detailActionProcessor: ActionProcessor<Action, Mutation, Event>,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val model by model(
        reducerProcessor = detailReducerProcessor,
        actionProcessor = detailActionProcessor,
        initialState = UiState.Loading,
        dispatcher = ioDispatcher
    )

    val uiStateFlow = model.uiState
    val eventFlow = model.event

    fun process(action: Action) = model.process(action)

    init {
        process(
            Action.InternalAction.GetRepository(
                userName = savedStateHandle.get<String>(USER_NAME),
                repoName = savedStateHandle.get<String>(REPO_NAME)
            )
        )
    }
}