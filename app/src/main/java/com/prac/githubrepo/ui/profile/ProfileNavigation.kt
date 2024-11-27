package com.prac.githubrepo.ui.profile

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.prac.githubrepo.ui.NavigationActions
import com.prac.core.navigation.Routes.PROFILE

fun NavGraphBuilder.profileScreen(
    navigationActions: NavigationActions
) {
    composable<PROFILE> {
        com.prac.feature.profile.view.ProfileScreen(
            onNavigateToLogin = { navigationActions.navigateToLogin() }
        )
    }
}