package com.prac.githubrepo.ui.profile

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val BOTTOM_PROFILE = "bottomProfile"

fun NavGraphBuilder.profileScreen(
    onLogout: () -> Unit
) {
    composable(
        route = BOTTOM_PROFILE
    ) {
        ProfileScreen(
            onLogout = onLogout
        )
    }
}