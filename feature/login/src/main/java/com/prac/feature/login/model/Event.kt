package com.prac.feature.login.model

sealed interface Event {
    data object OpenBrowser : Event
    data object SuccessLogin : Event
}