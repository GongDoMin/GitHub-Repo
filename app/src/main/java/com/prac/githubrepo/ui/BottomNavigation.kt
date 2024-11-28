package com.prac.githubrepo.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import com.prac.core.designsystem.R
import com.prac.core.navigation.NavigationActions
import com.prac.core.navigation.Routes
import com.prac.core.navigation.Routes.HOME
import com.prac.core.navigation.Routes.PROFILE

sealed class BottomNavItem(
    @StringRes val title: Int,
    val icon: ImageVector,
    val route: Routes
) {
    data object Home : BottomNavItem(R.string.bottom_home, Icons.Default.Home, HOME)
    data object Profile : BottomNavItem(R.string.bottom_profile, Icons.Default.AccountCircle, PROFILE)
}

@Composable
fun GitHubBottomNavigation(
    modifier: Modifier = Modifier,
    onNavigationToBottom: (Routes) -> Unit,
    currentDestination: NavDestination?
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Profile
    )

    NavigationBar(
        modifier = modifier
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentDestination?.hierarchyHasRoute(item.route) == true,
                label = {
                    Text(
                        text = stringResource(id = item.title),
                        style = TextStyle(
                            fontSize = 12.sp
                        )
                    )
                },
                icon = {
                    Icon(
                        painter = rememberVectorPainter(item.icon),
                        modifier = Modifier
                            .size(dimensionResource(id = R.dimen.bottom_icon)),
                        contentDescription = stringResource(id = item.title)
                    )
                },
                onClick = {
                    if (currentDestination?.hierarchyHasRoute(item.route) == false) {
                        onNavigationToBottom(item.route)
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    selectedTextColor = Color.Black,
                    unselectedIconColor = Color.LightGray,
                    unselectedTextColor = Color.LightGray,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

private fun NavDestination?.hierarchyHasRoute(route: Routes) =
    this?.hierarchy?.any { it.hasRoute(route::class) }