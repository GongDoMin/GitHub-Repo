package com.prac.githubrepo.ui.login.model

sealed interface Event {
    data object OpenBrowser : Event
    data object SuccessLogin : Event
}