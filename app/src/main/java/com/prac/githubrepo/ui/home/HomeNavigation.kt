package com.prac.githubrepo.ui.home

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.prac.githubrepo.ui.home.main.MAIN_SCREEN
import com.prac.githubrepo.ui.home.main.detail.detailScreen
import com.prac.githubrepo.ui.home.main.detail.navigationToDetail
import com.prac.githubrepo.ui.home.main.mainScreen
import com.prac.githubrepo.ui.login.navigationToLogin

const val BOTTOM_HOME = "bottomHome"

fun NavGraphBuilder.homeNavigation(
    navController: NavController
) {
    navigation(startDestination = MAIN_SCREEN, route = BOTTOM_HOME) {
        mainScreen(
            onLogout = {
                navController.navigationToLogin()
            },
            onClickRepository = { userName, repoName ->
                navController.navigationToDetail(userName, repoName)
            }
        )

        detailScreen(
            onLogout = {
                navController.navigationToLogin()
            },
            onBack = { navController.popBackStack() }
        )
    }
}