package com.prac.feature.main.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.prac.core.navigation.Routes.HOME
import com.prac.feature.main.view.MainScreen

fun NavGraphBuilder.mainScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToDetail: (userName: String, repoName: String) -> Unit
) {
    composable<HOME.MAIN> {
        MainScreen(
            onNavigateToLogin = onNavigateToLogin,
            onClickRepository = onNavigateToDetail
        )
    }
}