package com.prac.githubrepo.ui.login

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.prac.githubrepo.ui.NavigationActions
import com.prac.core.navigation.NavigationDestinations.LOGIN

fun NavGraphBuilder.loginScreen(
    navigationActions: NavigationActions
) {
    composable<LOGIN> {
        com.prac.feature.login.view.LoginScreen(
            onNavigateToMain = { navigationActions.navigateToMain() }
        )
    }
}