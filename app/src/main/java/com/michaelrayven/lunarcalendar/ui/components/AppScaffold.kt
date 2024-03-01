package com.michaelrayven.lunarcalendar.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.michaelrayven.lunarcalendar.R
import com.michaelrayven.lunarcalendar.navigation.NavigationItem
import com.michaelrayven.lunarcalendar.types.Location
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Base64

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    modifier: Modifier = Modifier,
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    content: @Composable (PaddingValues) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            AppTopBar(
                navController = navController,
                scrollBehavior = scrollBehavior
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

                    val locationEncoded = Base64.getUrlEncoder().encodeToString(Json.encodeToString(location).toByteArray())
                    navController.navigate(NavigationItem.Calendar.route + "/$locationEncoded/$timestamp")
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
        },
        content = content
    )
}