package com.prac.feature.main.refresh

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullToRefreshLayout(
    refreshState: RefreshState,
    onUpdateRefreshState: (RefreshState) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    indicatorCount: Int = 10,
    thresholdDp: Dp = 100.dp,
    progressbarSizeDp: Dp = 48.dp,
    refreshProgressbarPaddingDp: Dp = 16.dp,
    content: @Composable () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    val pullToRefreshState = rememberPullToRefreshState()

    // progressbarSize, refreshProgressbarPadding paramter 는 바뀌지 않는다고 가정
    val refreshSize = remember {
        progressbarSizeDp + refreshProgressbarPaddingDp
    }

    LaunchedEffect(pullToRefreshState.distanceFraction, refreshState) {
        when {
            refreshState == RefreshState.Refreshing -> Unit
            refreshState == RefreshState.CompleteRefreshing -> {
                onUpdateRefreshState(RefreshState.Default)
                pullToRefreshState.snapTo(MIN_DISTANCE_FRACTION)
            }
            pullToRefreshState.distanceFraction >= MAX_DISTANCE_FRACTION -> onUpdateRefreshState(RefreshState.ReachedThreshold)
            pullToRefreshState.distanceFraction > MIN_DISTANCE_FRACTION -> onUpdateRefreshState(RefreshState.PullingDown)
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter
    ) {
        PullToRefreshProgressbar(
            distanceFraction = pullToRefreshState.distanceFraction,
            refreshState = refreshState,
            indicatorCount = indicatorCount,
            progressbarSizeDp = progressbarSizeDp,
            refreshProgressbarPaddingDp = refreshProgressbarPaddingDp,
            thresholdDp = thresholdDp
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .pullToRefresh(
                    isRefreshing = refreshState == RefreshState.Refreshing,
                    state = pullToRefreshState,
                    threshold = thresholdDp,
                    onRefresh = {
                        coroutineScope.launch {
                            onUpdateRefreshState(RefreshState.Refreshing)
                            delay(REFRESH_DELAY)
                            onRefresh()
                        }
                    }
                )
        ) {
            PullToRefreshSpacer(
                state = refreshState,
                distanceFraction = pullToRefreshState.distanceFraction,
                refreshSizeDp = refreshSize,
                thresholdDp = thresholdDp
            )

            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                content()
            }
        }
    }
}

private const val MIN_DISTANCE_FRACTION = 0f
private const val MAX_DISTANCE_FRACTION = 1f
private const val REFRESH_DELAY = 1_000L