package com.michaelrayven.lunarcalendar.ui.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.michaelrayven.lunarcalendar.R
import com.michaelrayven.lunarcalendar.navigation.NavigationItem


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior?
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
        ),
        title = {
            if (currentRoute == null || currentRoute == NavigationItem.Home.route) {
                Image(
                    painter = painterResource(id = R.drawable.ic_logo),
                    contentDescription = "App logo",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                )
            } else {
                Log.d("DEGUB", currentRoute)
                val text = when (currentRoute) {
                    NavigationItem.Settings.routeWithArgs -> {
                        NavigationItem.Settings.name
                    }
                    NavigationItem.Calendar.routeWithArgs -> {
                        NavigationItem.Calendar.name
                    }
                    else -> { "" }
                }

                Text(
                    text = text,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        },
        navigationIcon = {
            if (currentRoute != null && currentRoute != NavigationItem.Home.route) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                        contentDescription = "Вернуться назад"
                    )
                }
            }
        },
        actions = { AppTopBarDropdownMenu(navController) },
        scrollBehavior = scrollBehavior,
    )
}
data class TopBarItem(
    val name: String,
    val route: String,
    val icon: Painter
)

@Composable
fun AppTopBarDropdownMenu(
    navController: NavController
) {
    val items = listOf(
        TopBarItem(
            name = NavigationItem.Home.name,
            route = NavigationItem.Home.route,
            icon = painterResource(id = NavigationItem.Home.icon)
        ),
        TopBarItem(
            name = NavigationItem.Settings.name,
            route = NavigationItem.Settings.route,
            icon = painterResource(id = NavigationItem.Settings.icon)
        )
    )

    var expanded by remember { mutableStateOf(false) }

    ContextDropdownMenu(
        items = items,
        drawItem = { item, onClick ->
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            DropdownMenuItem(
                enabled = currentRoute != item.route,
                text = {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleSmall
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = item.icon,
                        contentDescription = item.name
                    )
                },
                onClick = onClick,
                colors = MenuItemColors(
                    textColor = MaterialTheme.colorScheme.onBackground,
                    leadingIconColor = MaterialTheme.colorScheme.onBackground,
                    trailingIconColor = MaterialTheme.colorScheme.onBackground,
                    disabledTextColor = MaterialTheme.colorScheme.primary,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.primary,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        onItemClick = { _, item ->
            navController.navigate(item.route)
        },
        actionIcon = {
            IconButton(onClick = { expanded = true }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_more_24),
                    contentDescription = "Меню",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        },
        onDismissRequest = { expanded = false },
        expanded = expanded
    )
}