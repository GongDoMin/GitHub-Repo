package com.prac.githubrepo.ui.home

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.prac.githubrepo.ui.Routes.HOME
import com.prac.githubrepo.ui.home.main.detail.detailScreen
import com.prac.githubrepo.ui.home.main.detail.navigationToDetail
import com.prac.githubrepo.ui.home.main.mainScreen
import com.prac.githubrepo.ui.login.navigationToLogin

fun NavGraphBuilder.homeNavigation(
    navController: NavController
) {
    navigation<HOME>(startDestination = HOME.MAIN) {
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