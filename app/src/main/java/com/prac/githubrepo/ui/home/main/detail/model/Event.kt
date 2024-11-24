package com.prac.githubrepo.ui.home.main.detail.model

sealed interface Event {
    data object Error : Event
    data object Logout : Event
}