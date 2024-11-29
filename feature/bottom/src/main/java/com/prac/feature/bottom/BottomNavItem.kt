package com.prac.feature.bottom

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import com.prac.core.designsystem.R
import com.prac.core.navigation.Routes

sealed class BottomNavItem(
    @StringRes val title: Int,
    val icon: ImageVector,
    val route: Routes
) {
    data object Home : BottomNavItem(R.string.bottom_home, Icons.Default.Home, Routes.HOME)
    data object Profile : BottomNavItem(
        R.string.bottom_profile, Icons.Default.AccountCircle,
        Routes.PROFILE
    )
}