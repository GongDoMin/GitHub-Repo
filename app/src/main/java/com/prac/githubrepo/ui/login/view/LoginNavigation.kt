package com.prac.githubrepo.ui.login.view

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.prac.githubrepo.ui.NavigationActions
import com.prac.githubrepo.ui.NavigationDestinations.LOGIN

fun NavGraphBuilder.loginScreen(
    navigationActions: NavigationActions
) {
    composable<LOGIN> {
        LoginScreen(
            onNavigateToMain = { navigationActions.navigateToMain() }
        )
    }
}