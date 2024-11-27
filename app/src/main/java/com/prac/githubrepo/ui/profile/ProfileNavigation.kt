package com.prac.githubrepo.ui.profile

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.prac.githubrepo.ui.NavigationActions
import com.prac.githubrepo.ui.NavigationDestinations.PROFILE
import com.prac.feature.profile.view.ProfileScreen

fun NavGraphBuilder.profileScreen(
    navigationActions: NavigationActions
) {
    composable<PROFILE> {
        com.prac.feature.profile.view.ProfileScreen(
            onNavigateToLogin = { navigationActions.navigateToLogin() }
        )
    }
}