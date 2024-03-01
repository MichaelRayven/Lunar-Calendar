import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.michaelrayven.lunarcalendar.types.Location
import com.michaelrayven.lunarcalendar.types.LunarCalendar
import com.michaelrayven.lunarcalendar.ui.components.AppScaffold
import com.michaelrayven.lunarcalendar.ui.components.LoadingFullscreen
import com.michaelrayven.lunarcalendar.ui.components.LunarCalendarView
import com.michaelrayven.lunarcalendar.util.getCurrentLunarCalendar

@Composable
fun LunarCalendarScreen(
    location: Location,
    timestamp: Long,
    navController: NavController,
    snackbarHostState: SnackbarHostState
) {
    var lunarCalendar by remember { mutableStateOf<LunarCalendar?>(null) }

    LaunchedEffect(true) {
        lunarCalendar = getCurrentLunarCalendar(location, timestamp)
    }

    AppScaffold(navController = navController, snackbarHostState = snackbarHostState) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            if (lunarCalendar != null) {
                LunarCalendarView(lunarCalendar!!)
            } else {
                LoadingFullscreen()
            }
        }
    }
}
