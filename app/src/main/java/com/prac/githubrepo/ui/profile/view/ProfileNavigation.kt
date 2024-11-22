package com.prac.githubrepo.ui.profile.view

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.prac.githubrepo.ui.NavigationActions
import com.prac.githubrepo.ui.NavigationDestinations.PROFILE

fun NavGraphBuilder.profileScreen(
    navigationActions: NavigationActions
) {
    composable<PROFILE> {
        ProfileScreen(
            onNavigateToLogin = { navigationActions.navigateToLogin() }
        )
    }
}