package com.prac.githubrepo.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class EventModel<Event>(
    private val coroutineScope: CoroutineScope,
    private val _event: MutableSharedFlow<Event>
) {
    val event = _event.asSharedFlow()

    fun process(event: Event) {
        coroutineScope.launch {
            _event.emit(event)
        }
    }
}

inline fun <reified Event> ViewModel.eventModel(
    replay: Int = 0,
    extraBufferCapacity: Int = 0,
    onBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND
) =
    EventModelProperty<Event>(
        coroutineScope = this.viewModelScope,
        event = MutableSharedFlow<Event>(
            replay = replay,
            extraBufferCapacity = extraBufferCapacity,
            onBufferOverflow = onBufferOverflow
        )
    )

class EventModelProperty<Event>(
    private val coroutineScope: CoroutineScope,
    private val event: MutableSharedFlow<Event>
) : ReadOnlyProperty<Any, EventModel<Event>> {
    override fun getValue(thisRef: Any, property: KProperty<*>): EventModel<Event> =
        EventModel(
            coroutineScope = coroutineScope,
            _event = event
        )
}