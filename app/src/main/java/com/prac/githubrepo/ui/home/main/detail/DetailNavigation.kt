package com.prac.githubrepo.ui.home.main.detail

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.prac.githubrepo.ui.Routes.HOME

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