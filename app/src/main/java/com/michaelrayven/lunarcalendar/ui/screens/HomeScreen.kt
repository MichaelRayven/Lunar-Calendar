package com.michaelrayven.lunarcalendar.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.michaelrayven.lunarcalendar.types.LunarCalendar
import com.michaelrayven.lunarcalendar.ui.components.AppScaffold
import com.michaelrayven.lunarcalendar.ui.components.LoadingFullscreen
import com.michaelrayven.lunarcalendar.ui.components.LunarCalendarView
import com.michaelrayven.lunarcalendar.ui.components.PullToRefreshLayout
import com.michaelrayven.lunarcalendar.util.getCurrentLunarCalendar
import com.michaelrayven.lunarcalendar.util.getSavedLocation

@Composable
fun HomeScreen(
    navController: NavController,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    val location = getSavedLocation(context)

    var lunarCalendar by remember { mutableStateOf<LunarCalendar?>(null) }

    LaunchedEffect(true) {
        lunarCalendar = getCurrentLunarCalendar(location)
    }

    AppScaffold(navController = navController, snackbarHostState = snackbarHostState) { innerPadding ->
        PullToRefreshLayout(
            modifier = Modifier.padding(innerPadding),
            onRefresh = {
                lunarCalendar = null
                lunarCalendar = getCurrentLunarCalendar(location)
            }
        ) {
            if (lunarCalendar != null) {
                LunarCalendarView(lunarCalendar!!)
            } else {
                LoadingFullscreen()
            }
        }
    }
}

