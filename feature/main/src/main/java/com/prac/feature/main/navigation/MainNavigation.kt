package com.prac.feature.main.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.prac.core.navigation.Routes.HOME
import com.prac.feature.main.view.MainScreen

fun NavGraphBuilder.mainScreen(
    onLogout: () -> Unit,
    onClickRepository: (String, String) -> Unit
) {
    composable<HOME.MAIN> {
        MainScreen(
            onNavigateToLogin = onLogout,
            onClickRepository = onClickRepository
        )
    }
}