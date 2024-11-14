package com.prac.githubrepo.ui

import kotlinx.serialization.Serializable

sealed class Routes {
    @Serializable data object LOGIN : Routes()

    @Serializable data object HOME : Routes() {
        @Serializable data object MAIN : Routes()

        @Serializable data class DETAIL(val userName: String, val repoName: String) : Routes()
    }

    @Serializable data object PROFILE : Routes()
}