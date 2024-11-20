package com.prac.githubrepo.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

inline fun <reified UiState, reified Action, reified Event> ViewModel.model(
    actionProcessors: List<ActionProcessor<Action, UiState, Event>>,
    ioDispatcher: CoroutineDispatcher,
    initialState: UiState,
) =
    ModelProperty(
        viewModel = this,
        actionProcessors = actionProcessors,
        ioDispatcher = ioDispatcher,
        uiState = MutableStateFlow<UiState>(initialState),
        event = MutableSharedFlow<Event>(),
    )

class ModelProperty<UiState, Action, Event>(
    private val viewModel: ViewModel,
    private val actionProcessors: List<ActionProcessor<Action, UiState, Event>>,
    private val ioDispatcher: CoroutineDispatcher,
    private val uiState: MutableStateFlow<UiState>,
    private val event: MutableSharedFlow<Event>,
) : ReadOnlyProperty<Any, Model<UiState, Action, Event>> {
    override fun getValue(thisRef: Any, property: KProperty<*>): Model<UiState, Action, Event> =
        Model(
            actionProcessors = actionProcessors,
            ioDispatcher = ioDispatcher,
            coroutineScope = viewModel.viewModelScope,
            _uiState = uiState,
            _event = event,
        )
}
