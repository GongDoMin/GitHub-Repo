package com.prac.feature.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.prac.core.navigation.NavigationActions
import com.prac.core.navigation.Routes.HOME
import com.prac.feature.detail.navigation.detailScreen
import com.prac.feature.main.navigation.mainScreen

fun NavGraphBuilder.homeNavigation(
    navigationActions: NavigationActions
) {
    navigation<HOME>(startDestination = HOME.MAIN) {
        mainScreen(
            onLogout = {
                navigationActions.navigateToLogin()
            },
            onClickRepository = { userName, repoName ->
                navigationActions.navigateToDetail(userName, repoName)
            }
        )

        detailScreen(
            onLogout = {
                navigationActions.navigateToLogin()
            },
            onBack = { navigationActions.popBackStack() }
        )
    }
}