package com.prac.githubrepo.ui.login

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.prac.githubrepo.ui.NavigationActions
import com.prac.githubrepo.ui.Routes.LOGIN

fun NavController.navigationToLogin() {
    navigate(LOGIN) {
        popUpTo(graph.id) {
            inclusive = true
        }
    }
}

fun NavGraphBuilder.loginScreen(
    navigationActions: NavigationActions
) {
    composable<LOGIN> {
        LoginScreen(
            onLogin = { navigationActions.navigateToMain() }
        )
    }
}