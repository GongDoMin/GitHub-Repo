package com.prac.githubrepo.util

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.prac.githubrepo.R
import com.prac.githubrepo.ui.home.BOTTOM_HOME
import com.prac.githubrepo.ui.profile.BOTTOM_PROFILE

sealed class BottomNavItem(
    @StringRes val title: Int,
    val icon: ImageVector,
    val screenRoute: String
) {
    data object Home : BottomNavItem(R.string.bottom_home, Icons.Default.Home, BOTTOM_HOME)
    data object Profile : BottomNavItem(R.string.bottom_profile, Icons.Default.AccountCircle, BOTTOM_PROFILE)
}

@Composable
fun MyBottomNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Profile
    )

    NavigationBar(
        modifier = modifier
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentDestination?.hierarchy?.any { it.route == item.screenRoute } == true,
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
                    if (currentDestination?.hierarchy?.any { it.route == item.screenRoute } == false) {
                        navController.navigate(item.screenRoute) {
                            popUpTo(navController.graph.id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
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