package com.prac.feature.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.prac.core.navigation.NavigationActions
import com.prac.core.navigation.Routes.HOME
import com.prac.feature.detail.navigation.detailScreen
import com.prac.feature.main.navigation.mainScreen

fun NavGraphBuilder.homeNavigation(
    onNavigateToLogin: () -> Unit,
    onNavigateToDetail: (userName: String, repoName: String) -> Unit,
    onNavigateToBackStack: () -> Unit
) {
    navigation<HOME>(startDestination = HOME.MAIN) {
        mainScreen(
            onLogout = onNavigateToLogin,
            onClickRepository = onNavigateToDetail
        )

        detailScreen(
            onLogout = onNavigateToLogin,
            onBack = onNavigateToBackStack
        )
    }
}