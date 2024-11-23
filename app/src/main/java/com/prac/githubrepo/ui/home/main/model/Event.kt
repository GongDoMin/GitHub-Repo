package com.prac.githubrepo.ui.home.main.model

sealed interface Event {
    data class OpenRepositoryDetail(
        val userName: String,
        val repoName: String
    ) : Event
    data object Reload : Event
    data object Logout : Event
}