package com.prac.githubrepo.ui.home.main

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.prac.githubrepo.ui.Routes.HOME

fun NavGraphBuilder.mainScreen(
    onLogout: () -> Unit,
    onClickRepository: (String, String) -> Unit
) {
    composable<HOME.MAIN> {
        MainScreen(
            onLogout = onLogout,
            onClickRepository = onClickRepository
        )
    }
}