package com.prac.githubrepo.ui.login.model

sealed class Event {
    data object LaunchLoginIntent : Event()
    data object LoginSuccess : Event()
}