package com.michaelrayven.lunarcalendar

import LunarCalendarScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
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
import com.michaelrayven.lunarcalendar.work.WidgetUpdateWorker.Companion.scheduleWidgetUpdates
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Base64

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
