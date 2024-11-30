package com.prac.feature.main.model

sealed interface Event {
    data class OpenRepositoryDetail(
        val userName: String,
        val repoName: String
    ) : Event
    data object Retry : Event
    data object Logout : Event
}