package com.prac.feature.detail.model

sealed interface Event {
    data object Error : Event
    data object Logout : Event
}