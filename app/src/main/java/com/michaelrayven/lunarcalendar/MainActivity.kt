package com.michaelrayven.lunarcalendar

import LunarCalendarScreen
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.michaelrayven.lunarcalendar.navigation.NavigationItem
import com.michaelrayven.lunarcalendar.types.Location
import com.michaelrayven.lunarcalendar.ui.components.ComboPicker
import com.michaelrayven.lunarcalendar.ui.components.ContextDropdownMenu
import com.michaelrayven.lunarcalendar.ui.screens.HomeScreen
import com.michaelrayven.lunarcalendar.ui.screens.SettingsScreen
import com.michaelrayven.lunarcalendar.ui.theme.LunarCalendarTheme
import com.michaelrayven.lunarcalendar.util.parseLocationOrSaved
import com.michaelrayven.lunarcalendar.work.WidgetUpdateWorker.Companion.scheduleWidgetUpdates
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Instant

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val scrollBehavior =
                TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
            val snackbarHostState = remember { SnackbarHostState() }

            val context = applicationContext
            val preferencesFile = context.getString(R.string.preference_file)
            val preferences = context.getSharedPreferences(preferencesFile, Context.MODE_PRIVATE)

            LaunchedEffect(true) {
                scheduleWidgetUpdates(applicationContext)
            }

            LunarCalendarTheme {
                Scaffold(
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                    snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState)
                    },
                    topBar = {
                        TopAppBar(
                            colors = topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.primary,
                            ),
                            title = {
                                Text(
                                    text = "Lunar Calendar",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            navigationIcon = { },
                            actions = { TopBarDropdownMenu(navController) },
                            scrollBehavior = scrollBehavior,
                        )
                    },
                    floatingActionButton = {
                        var showDateTimePicker by remember {
                            mutableStateOf(false)
                        }

                        ComboPicker(
                            onDismissRequest = {
                                showDateTimePicker = false
                            },
                            onSelected = { location: Location, timestamp: Long ->
                                showDateTimePicker = false
                                with(preferences.edit()) {
                                    putString(
                                        context.getString(R.string.nav_location),
                                        Json.encodeToString(location)
                                    )
                                    apply()
                                }
                                navController.navigate(
                                    NavigationItem.Calendar.route + "/${
                                        context.getString(
                                            R.string.nav_location
                                        )
                                    }/${timestamp}"
                                )
                            },
                            expanded = showDateTimePicker
                        )

                        val navBackStackEntry by navController.currentBackStackEntryAsState()

                        if (navBackStackEntry?.destination?.route != NavigationItem.Settings.route) {
                            FloatingActionButton(onClick = { showDateTimePicker = true }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_edit_calendar_24),
                                    contentDescription = ""
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        navController = navController,
                        startDestination = NavigationItem.Home.route
                    ) {
                        composable(NavigationItem.Home.route) {
                            HomeScreen(navController, snackbarHostState)
                        }
                        composable(
                            NavigationItem.Calendar.route + "/{key}/{timestamp}",
                        ) { backStackEntry ->
                            val location = parseLocationOrSaved(
                                applicationContext,
                                backStackEntry.arguments?.getString("key")
                            )
                            val timestamp =
                                backStackEntry.arguments?.getString("timestamp")?.toLong()
                                    ?: Instant.now().toEpochMilli()

                            LunarCalendarScreen(
                                location,
                                timestamp,
                                navController,
                                snackbarHostState
                            )
                        }
                        composable(NavigationItem.Settings.route) {
                            SettingsScreen(navController, snackbarHostState)
                        }
                    }
                }
            }
        }
    }

    data class TopBarItem(
        val name: String,
        val route: String,
        val icon: ImageVector
    )

    @Composable
    fun TopBarDropdownMenu(
        navController: NavController
    ) {
        val items = listOf(
            TopBarItem(
                name = "Главная",
                route = "home",
                icon = Icons.Filled.Home
            ),
            TopBarItem(
                name = "Настройки",
                route = "settings",
                icon = Icons.Filled.Settings
            )
        )

        ContextDropdownMenu(
            items = items,
            drawItem = { _, item, onClick ->
                val currentRoute = navController.currentBackStackEntry?.destination?.route
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
                            imageVector = item.icon,
                            contentDescription = "Item's Icon"
                        )
                    },
                    onClick = onClick
                )
            },
            onItemClick = { _, item ->
                navController.navigate(item.route)
            }
        )
    }
}
