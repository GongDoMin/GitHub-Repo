package com.prac.githubrepo.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch

class Model<UiState, Action, Event>(
    private val actionProcessors: List<ActionProcessor<Action, UiState, Event>>,
    private val ioDispatcher: CoroutineDispatcher,
    private val coroutineScope: CoroutineScope,
    private val _uiState: MutableStateFlow<UiState>,
    private val _event: MutableSharedFlow<Event>,
) {
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    val event: SharedFlow<Event> = _event.asSharedFlow()

    fun process(action: Action) {
        coroutineScope.launch(ioDispatcher) {
            actionProcessors
                .map { actionProcessor -> actionProcessor(action) }
                .merge()
                .collect { (uiState, event) ->
                    uiState?.let { _uiState.emit(it) }
                    event?.let { _event.emit(it) }
                }
        }
    }
}
