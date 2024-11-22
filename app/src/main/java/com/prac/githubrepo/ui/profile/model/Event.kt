package com.prac.githubrepo.ui.profile.model

sealed interface Event {
    data object Logout : Event
}