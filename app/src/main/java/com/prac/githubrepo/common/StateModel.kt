package com.prac.githubrepo.common

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class StateModel<UiState, Mutation>(
    private val reducerProcessor: Reducer<Mutation, UiState>,
    private val _uiState : MutableStateFlow<UiState>
) {
    val uiState = _uiState.asStateFlow()

    fun process(mutation: Mutation) {
        _uiState.update { uiState ->
            reducerProcessor.invoke(mutation, uiState)
        }
    }
}

inline fun <reified UiState, reified Mutation> stateModel(
    reducerProcessor: Reducer<Mutation, UiState>,
    initialState: UiState,
) =
    StateModelProperty(
        reducerProcessor = reducerProcessor,
        uiState = MutableStateFlow<UiState>(initialState),
    )

class StateModelProperty<UiState, Mutation>(
    private val reducerProcessor: Reducer<Mutation, UiState>,
    private val uiState: MutableStateFlow<UiState>,
) : ReadOnlyProperty<Any, StateModel<UiState, Mutation>> {
    override fun getValue(thisRef: Any, property: KProperty<*>): StateModel<UiState, Mutation> =
        StateModel(
            reducerProcessor = reducerProcessor,
            _uiState = uiState
        )
}