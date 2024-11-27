package com.prac.feature.login.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.prac.core.navigation.NavigationActions
import com.prac.core.navigation.Routes.LOGIN
import com.prac.feature.login.view.LoginScreen

fun NavGraphBuilder.loginScreen(
    navigationActions: NavigationActions
) {
    composable<LOGIN> {
        LoginScreen(
            onNavigateToMain = { navigationActions.navigateToMain() }
        )
    }
}