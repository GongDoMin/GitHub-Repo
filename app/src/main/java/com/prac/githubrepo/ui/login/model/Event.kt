package com.prac.githubrepo.ui.login.model

sealed interface Event {
    data object LaunchLoginIntent : Event
    data object LoginSuccess : Event
}