package com.prac.feature.profile.model

sealed interface Event {
    data object Logout : Event
}