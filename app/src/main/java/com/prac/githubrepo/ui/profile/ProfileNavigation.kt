package com.prac.githubrepo.ui.profile

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.prac.githubrepo.ui.Routes.PROFILE

fun NavGraphBuilder.profileScreen(
    onLogout: () -> Unit
) {
    composable<PROFILE> {
        ProfileScreen(
            onLogout = onLogout
        )
    }
}