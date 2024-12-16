package com.prac.feature.main.refresh

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceAtMost
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times

@Composable
fun PullToRefreshSpacer(
    state: RefreshState,
    distanceFraction: Float,
    refreshSizeDp: Dp,
    thresholdDp: Dp,
    modifier: Modifier = Modifier
) {
    Spacer(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .heightModifier(
                state = state,
                distanceFraction = distanceFraction,
                refreshSizeDp = refreshSizeDp,
                thresholdDp = thresholdDp
            )
    )
}

private fun Modifier.heightModifier(
    state: RefreshState,
    distanceFraction: Float,
    refreshSizeDp: Dp,
    thresholdDp: Dp
) : Modifier {
    val modifier = when (state) {
        RefreshState.PullingDown -> {
            Modifier.height(
                (distanceFraction * thresholdDp)
                    .coerceAtMost(thresholdDp)
            )
        }
        RefreshState.ReachedThreshold -> Modifier.height(thresholdDp)
        RefreshState.Refreshing -> Modifier.height(refreshSizeDp)
        RefreshState.Default, RefreshState.CompleteRefreshing -> Modifier.height(0.dp)
    }

    return this.then(modifier)
}