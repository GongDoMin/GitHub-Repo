package com.prac.core.common.mvi.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prac.core.common.mvi.action.ActionProcessor
import com.prac.core.common.mvi.reducer.Reducer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.RENDEZVOUS
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

inline fun <reified Action, reified UiState, reified Mutation, reified Event> ViewModel.model(
    reducerProcessor: Reducer<Mutation, UiState>,
    actionProcessor: ActionProcessor<Action, Mutation, Event>,
    initialState: UiState,
    dispatcher: CoroutineDispatcher
) =
    ModelProperty(
        viewModel = this,
        reducerProcessor = reducerProcessor,
        actionProcessor = actionProcessor,
        dispatcher = dispatcher,
        uiState = MutableStateFlow(initialState),
        event = Channel(
            capacity = RENDEZVOUS,
            onBufferOverflow = BufferOverflow.SUSPEND
        )
    )

class ModelProperty<Action, UiState, Mutation, Event>(
    private val viewModel: ViewModel,
    private val reducerProcessor: Reducer<Mutation, UiState>,
    private val actionProcessor: ActionProcessor<Action, Mutation, Event>,
    private val dispatcher: CoroutineDispatcher,
    private val uiState: MutableStateFlow<UiState>,
    private val event: Channel<Event>
) : ReadOnlyProperty<Any, Model<Action, UiState, Mutation, Event>> {
    override fun getValue(
        thisRef: Any,
        property: KProperty<*>,
    ): Model<Action, UiState, Mutation, Event> =
        Model(
            reducerProcessor = reducerProcessor,
            actionProcessor = actionProcessor,
            coroutineScope = viewModel.viewModelScope,
            dispatcher = dispatcher,
            _uiState = uiState,
            _event = event
        )
}

class Model<Action, UiState, Mutation, Event> internal constructor(
    private val reducerProcessor: Reducer<Mutation, UiState>,
    private val actionProcessor: ActionProcessor<Action, Mutation, Event>,
    private val coroutineScope: CoroutineScope,
    private val dispatcher: CoroutineDispatcher,
    private val _uiState: MutableStateFlow<UiState>,
    private val _event: Channel<Event>
) {
    val uiState = _uiState.asStateFlow()

    val event = _event.receiveAsFlow()

    fun process(action: Action) {
        coroutineScope.launch(dispatcher) {
            actionProcessor(action).collect { (mutation, event) ->
                mutation?.let { handleMutation(it) }
                event?.let { _event.send(it) }
            }
        }
    }

    private fun handleMutation(mutation: Mutation) {
        _uiState.update { uiState ->
            reducerProcessor(mutation, uiState)
        }
    }
}