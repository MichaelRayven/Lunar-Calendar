package com.michaelrayven.lunarcalendar.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullToRefreshLayout(
    modifier: Modifier = Modifier,
    onRefresh: suspend CoroutineScope.() -> Unit,
    content: @Composable () -> Unit
) {
    val state = rememberPullToRefreshState()
    Box(modifier.nestedScroll(state.nestedScrollConnection)
    ) {
        if (state.isRefreshing) {
            LaunchedEffect(true) {
                onRefresh()
                state.endRefresh()
            }
        }

        content()

        PullToRefreshContainer(
            modifier = Modifier.align(Alignment.TopCenter),
            state = state
        )
    }
}