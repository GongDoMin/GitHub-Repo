package com.prac.githubrepo.ui

import kotlinx.serialization.Serializable

sealed class NavigationDestinations {
    @Serializable data object LOGIN : NavigationDestinations()

    @Serializable data object HOME : NavigationDestinations() {
        @Serializable data object MAIN : NavigationDestinations()

        @Serializable data class DETAIL(val userName: String, val repoName: String) : NavigationDestinations() {
            companion object {
                const val USER_NAME = "userName"
                const val REPO_NAME = "repoName"
            }
        }
    }

    @Serializable data object PROFILE : NavigationDestinations()
}