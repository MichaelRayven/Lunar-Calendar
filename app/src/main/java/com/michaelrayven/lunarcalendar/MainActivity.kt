package com.michaelrayven.lunarcalendar

import LunarCalendarScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.michaelrayven.lunarcalendar.navigation.NavigationItem
import com.michaelrayven.lunarcalendar.ui.screens.HomeScreen
import com.michaelrayven.lunarcalendar.ui.screens.SettingsScreen
import com.michaelrayven.lunarcalendar.ui.theme.LunarCalendarTheme
import com.michaelrayven.lunarcalendar.work.WidgetUpdateWorker.Companion.scheduleWidgetUpdates

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val snackbarHostState = remember { SnackbarHostState() }

            LaunchedEffect(true) {
                scheduleWidgetUpdates(applicationContext)
            }

            LunarCalendarTheme {
                NavHost(
                    modifier = Modifier.fillMaxSize(),
                    navController = navController,
                    startDestination = NavigationItem.Home.route
                ) {
                    composable(NavigationItem.Home.route) {
                        HomeScreen(navController, snackbarHostState)
                    }
                    composable(
                        NavigationItem.Calendar.routeWithArgs,
                    ) { backStackEntry ->
                        val (location, timestamp) = NavigationItem.Calendar.processDestinationArgs(
                            context = applicationContext,
                            args = backStackEntry.arguments
                        )

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
