package com.prac.githubrepo.ui.home.main.view

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.prac.githubrepo.ui.NavigationDestinations.HOME

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