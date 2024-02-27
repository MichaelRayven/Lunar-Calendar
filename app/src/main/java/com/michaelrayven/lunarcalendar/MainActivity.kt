package com.michaelrayven.lunarcalendar

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.await
import com.michaelrayven.lunarcalendar.MainActivity.Companion.UPDATE_WIDGET_TAG
import com.michaelrayven.lunarcalendar.ui.components.ContextDropdownMenu
import com.michaelrayven.lunarcalendar.ui.screens.HomeScreen
import com.michaelrayven.lunarcalendar.ui.screens.SettingsScreen
import com.michaelrayven.lunarcalendar.ui.theme.LunarCalendarTheme
import com.michaelrayven.lunarcalendar.work.WidgetUpdateWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var widgetScheduledUpdateWorkInfo by remember { mutableStateOf<WorkInfo?>(null) }

            val navController = rememberNavController()
            val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
            val snackbarHostState = remember { SnackbarHostState() }

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
                            actions = {TopBarDropdownMenu(navController)},
                            scrollBehavior = scrollBehavior,
                        )
                    },
                ) { innerPadding ->
                    NavHost(
                        modifier = Modifier.fillMaxSize().padding(innerPadding),
                        navController = navController,
                        startDestination = "home"
                    ) {
                        composable("home") {
                            HomeScreen()
                        }
                        composable("settings") {
                            SettingsScreen(snackbarHostState)
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

    companion object {
        const val UPDATE_WIDGET_TAG = "update-widget"
    }
}

suspend fun updateWidget(context: Context) {
    val widgetSingleUpdateRequest = OneTimeWorkRequestBuilder<WidgetUpdateWorker>().build()
    val wordManager = WorkManager
        .getInstance(context)

    wordManager
        .enqueue(widgetSingleUpdateRequest)
        .await()
}

suspend fun scheduleWidgetUpdates(context: Context) {
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.UNMETERED)
        .build()
    val widgetUpdateRequest = PeriodicWorkRequestBuilder<WidgetUpdateWorker>(1, TimeUnit.DAYS)
        .setConstraints(constraints)
        .addTag(UPDATE_WIDGET_TAG)
        .build()
    val workManger = WorkManager
        .getInstance(context)

    workManger
        .cancelAllWorkByTag(UPDATE_WIDGET_TAG)
        .await()

    workManger
        .enqueue(widgetUpdateRequest)
        .await()
}

@SuppressLint("RestrictedApi")
suspend fun getScheduledWorkInfo(context: Context): WorkInfo? {
    val workManger = WorkManager
        .getInstance(context)

    val infoList = workManger
        .getWorkInfosByTag(UPDATE_WIDGET_TAG)
        .await()


    return infoList.getOrNull(0)
}

