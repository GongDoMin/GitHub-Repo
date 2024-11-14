package com.prac.githubrepo.ui.home.main.detail

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.prac.githubrepo.ui.Routes.HOME

fun NavController.navigationToDetail(
    userName: String,
    repoName: String
) {
    navigate(HOME.DETAIL(userName, repoName))
}

fun NavGraphBuilder.detailScreen(
    onLogout: () -> Unit,
    onBack: () -> Unit,
) {
    composable<HOME.DETAIL> {
        DetailScreen(
            onLogout = onLogout,
            onBack = onBack
        )
    }
}