package com.michaelrayven.lunarcalendar.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.michaelrayven.lunarcalendar.types.LunarCalendar
import com.michaelrayven.lunarcalendar.ui.components.LoadingFullscreen
import com.michaelrayven.lunarcalendar.ui.components.LunarCalendarView
import com.michaelrayven.lunarcalendar.util.getCurrentLunarCalendar
import com.michaelrayven.lunarcalendar.util.getSavedLocation

@OptIn(ExperimentalMaterial3Api::class)
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

    val state = rememberPullToRefreshState()
    Box(Modifier.nestedScroll(state.nestedScrollConnection)) {
        if (state.isRefreshing) {
            LaunchedEffect(true) {
                lunarCalendar = null
                lunarCalendar = getCurrentLunarCalendar(location)
                state.endRefresh()
            }
        }

        if (lunarCalendar != null) {
            LunarCalendarView(lunarCalendar!!)
        } else {
            LoadingFullscreen()
        }

        PullToRefreshContainer(
            modifier = Modifier.align(Alignment.TopCenter),
            state = state
        )
    }
}