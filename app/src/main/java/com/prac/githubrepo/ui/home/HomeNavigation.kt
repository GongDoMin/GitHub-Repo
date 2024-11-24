package com.prac.githubrepo.ui.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.prac.githubrepo.ui.NavigationActions
import com.prac.githubrepo.ui.NavigationDestinations.HOME
import com.prac.githubrepo.ui.home.main.detail.view.detailScreen
import com.prac.githubrepo.ui.home.main.view.mainScreen

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