package com.prac.core.common.mvi.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prac.core.common.mvi.action.ActionProcessor
import com.prac.core.common.mvi.reducer.Reducer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SharedFlowValues(
    val replay: Int = 0,
    val extraBufferCapacity: Int = 0,
    val onBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND
)

fun <Action, UiState, Mutation, Event> ViewModel.model(
    reducerProcessor: Reducer<Mutation, UiState>,
    actionProcessor: ActionProcessor<Action, Mutation, Event>,
    initialState: UiState,
    sharedFlowValues: SharedFlowValues = SharedFlowValues(),
    dispatcher: CoroutineDispatcher
) =
    Model(
        reducerProcessor = reducerProcessor,
        actionProcessor = actionProcessor,
        coroutineScope = this.viewModelScope,
        dispatcher = dispatcher,
        initialState = initialState,
        sharedFlowValues = sharedFlowValues
    )

class Model<Action, UiState, Mutation, Event> internal constructor(
    private val reducerProcessor: Reducer<Mutation, UiState>,
    private val actionProcessor: ActionProcessor<Action, Mutation, Event>,
    private val coroutineScope: CoroutineScope,
    private val dispatcher: CoroutineDispatcher,
    private val initialState: UiState,
    private val sharedFlowValues: SharedFlowValues
) {
    private val _uiState = MutableStateFlow(initialState)
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<Event>(
        replay = sharedFlowValues.replay,
        extraBufferCapacity = sharedFlowValues.extraBufferCapacity,
        onBufferOverflow = sharedFlowValues.onBufferOverflow
    )
    val event = _event.asSharedFlow()

    fun process(action: Action) {
        coroutineScope.launch(dispatcher) {
            actionProcessor(action).collect { (mutation, event) ->
                mutation?.let { handleMutation(it) }
                event?.let { _event.emit(it) }
            }
        }
    }

    private fun handleMutation(mutation: Mutation) {
        _uiState.update { uiState ->
            reducerProcessor(mutation, uiState)
        }
    }
}