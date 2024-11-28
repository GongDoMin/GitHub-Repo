package com.prac.feature.profile.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.prac.core.navigation.NavigationActions
import com.prac.core.navigation.Routes.PROFILE
import com.prac.feature.profile.view.ProfileScreen

fun NavGraphBuilder.profileScreen(
    onNavigateToLogin: () -> Unit
) {
    composable<PROFILE> {
        ProfileScreen(
            onNavigateToLogin = onNavigateToLogin
        )
    }
}