package com.prac.githubrepo.main.detail

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.prac.githubrepo.main.detail.DetailArgs.REPO_NAME_ARG
import com.prac.githubrepo.main.detail.DetailArgs.USER_NAME_ARG

object DetailArgs {
    const val USER_NAME_ARG = "userName"
    const val REPO_NAME_ARG = "repoName"
}

private const val DETAIL_SCREEN = "detail"
const val DETAIL_SCREEN_WITH_ARGS = "$DETAIL_SCREEN?${USER_NAME_ARG}={${USER_NAME_ARG}}&${REPO_NAME_ARG}={${REPO_NAME_ARG}}"

fun NavController.navigationToDetail(
    userName: String,
    repoName: String
) {
    val route = "$DETAIL_SCREEN?${USER_NAME_ARG}=$userName&${REPO_NAME_ARG}=$repoName"
    navigate(route)
}

fun NavGraphBuilder.detailScreen(
    onLogout: () -> Unit,
    onBack: () -> Unit,
) {
    composable(
        route = DETAIL_SCREEN_WITH_ARGS,
        arguments = listOf(
            navArgument(USER_NAME_ARG) { type = NavType.StringType },
            navArgument(REPO_NAME_ARG) { type = NavType.StringType },
        )
    ) { entry ->
        DetailScreen(
            onLogout = onLogout,
            onBack = onBack,
            userName = entry.arguments?.getString(USER_NAME_ARG),
            repoName = entry.arguments?.getString(REPO_NAME_ARG)
        )
    }
}