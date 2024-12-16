package com.prac.feature.main.refresh

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.times
import kotlinx.coroutines.launch

@Composable
fun PullToRefreshProgressbar(
    refreshState: RefreshState,
    distanceFraction: Float,
    indicatorCount: Int,
    progressbarSizeDp: Dp,
    refreshProgressbarPaddingDp: Dp,
    thresholdDp: Dp,
    modifier: Modifier = Modifier
) {
    val indicatorAlphaValues = (0 until indicatorCount).map { remember { Animatable(0f) } }

    LaunchedEffect(distanceFraction, refreshState) {
        when (refreshState) {
            RefreshState.Default, RefreshState.CompleteRefreshing -> {
                (0 until indicatorCount).map { index ->
                    launch {
                        indicatorAlphaValues[index].animateTo(
                            targetValue = TRANSPARENT_ALPHA,
                            animationSpec = tween(durationMillis = TWEEN_DURATION)
                        )
                    }
                }
            }
            RefreshState.ReachedThreshold, RefreshState.Refreshing -> {
                (0 until indicatorCount).map { index ->
                    launch {
                        indicatorAlphaValues[index].animateTo(
                            targetValue = OPAQUE_ALPHA,
                            animationSpec = tween(durationMillis = TWEEN_DURATION)
                        )
                    }
                }
            }
            RefreshState.PullingDown -> {
                val indicatorIndex = calculateIndicatorIndex(distanceFraction, indicatorCount)
                val indicatorAlphaValue = calculateIndicatorValue(distanceFraction, indicatorCount, indicatorIndex)
                (0 until indicatorCount).map { index ->
                    launch {
                        indicatorAlphaValues[index].animateTo(
                            targetValue =
                                if (index == indicatorIndex) indicatorAlphaValue
                                else if (index < indicatorIndex) OPAQUE_ALPHA
                                else TRANSPARENT_ALPHA,
                            animationSpec = tween(durationMillis = TWEEN_DURATION)
                        )
                    }
                }
            }
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "wheel transition")

    val rotationAnim by infiniteTransition.animateFloat(
        initialValue = INITIAL_ROTATION_ANGLE,
        targetValue = TARGET_ROTATION_ANGLE,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = ROTATION_ANIMATION_DURATION, easing = LinearEasing),
        ),
        label = "wheel rotation animation",
    )

    Canvas(
        modifier = modifier
            .padding(
                vertical =
                    if (refreshState == RefreshState.Refreshing) refreshProgressbarPaddingDp
                    else calculateVerticalPaddingOnPulling(distanceFraction, (thresholdDp - progressbarSizeDp))
            )
            .size(progressbarSizeDp)
            .graphicsLayer {
                rotationZ =
                    if (refreshState == RefreshState.Refreshing || refreshState == RefreshState.ReachedThreshold) rotationAnim else 0f
            }
    ) {
        (0 until indicatorCount).forEach { index ->
            rotate(degrees = index * (ANGLE_OF_CIRCLE / indicatorCount)) {
                drawLine(
                    color = Color.Black,
                    alpha = indicatorAlphaValues[index].value,
                    strokeWidth = STROKE_WIDTH,
                    cap = StrokeCap.Round,
                    start = drawStarLineInCircle(size),
                    end = drawEndLineInCircle(size),
                )
            }
        }
    }
}

private const val ANGLE_OF_CIRCLE = 360f
private const val TWEEN_DURATION = 0
private const val TRANSPARENT_ALPHA = 0f
private const val OPAQUE_ALPHA = 1f
private const val INITIAL_ROTATION_ANGLE = 0f
private const val TARGET_ROTATION_ANGLE = 360f
private const val STROKE_WIDTH = 8f
private const val ROTATION_ANIMATION_DURATION = 1000

private fun calculateIndicatorIndex(distanceFraction: Float, indicatorCount: Int) : Int {
    // ex), indicatorCount 13 일 경우 scaledFraction [0f ~ 1.69f] 로 scale
    // ex), indicatorCount 14 일 경우 scaledFraction [0f ~ 1.96f] 로 scale
    val scaledFraction = distanceFraction * (indicatorCount * indicatorCount * 0.01f)

    // ex), indicatorCount 13 일 경우 scaledIndicatorCount 은 0.13f
    // ex), indicatorCount 14 일 경우 scaledIndicatorCount 은 0.14f
    val scaledIndicatorCount = indicatorCount * 0.01f

    // ex), indicatorCount 13 일 경우 return 은 [0 ~ 13]
    // ex), indicatorCount 14 일 경우 return 은 [0 ~ 14]
    return (scaledFraction / scaledIndicatorCount).toInt()
}

private fun calculateIndicatorValue(distanceFraction: Float, indicatorCount: Int, indicatorIndex: Int) : Float {
    // indicatorIndex 와 indicatorCount 가 같은 경우 indicatorAlphaValue 는 OPAQUE_ALPHA
    if (indicatorIndex == indicatorCount) return OPAQUE_ALPHA

    // ex), indicatorCount 13 일 경우 scaledFraction [0f ~ 1.3f] 로 scale
    // ex), indicatorCount 14 일 경우 scaledFraction [0f ~ 1.4f] 로 scale
    val scaledFraction = distanceFraction * (indicatorCount * 0.1f)

    // ex), indicatorIndex 5 일 경우 prevIndicatorValues 은 0.5f
    // ex), indicatorIndex 6 일 경우 prevIndicatorValues 은 0.6f
    val prevIndicatorValues = indicatorIndex * 0.1f

    // alpha 은 [0f ~ 1f]
    // 예를 들어, indicatorCount 는 13, distanceFraction 는 0.11f, indicatorIndex 는 1 일 때
    // scaledFraction 은 0.143f, prevIndicatorValues 는 0.1f 이므로 1번째 인덱스의 alpha 는 0.43f이다.
    return (scaledFraction - prevIndicatorValues) * 10
}

private fun calculateVerticalPaddingOnPulling(distanceFraction: Float, remainingSpace: Dp) =
    distanceFraction.coerceAtMost(1f) * remainingSpace / 2

/**
 * 이 메서드는 주어진 Size를 기반으로 원의 중심에서 수직으로 1/3 위치 위에 있는 지점의 좌표를 계산하고 반환한다.
 */
private fun drawStarLineInCircle(size: Size) =
    Offset(size.width / 2, size.height / 3)

/**
 * 이 메서드는 원의 중심에서 수직으로 위에 있는 지점의 좌표를 계산한다.
 * 이 지점은 수직 축을 따라 원 높이의 1/3 만큼 떨어져있다.
 */
private fun drawEndLineInCircle(size: Size) =
    Offset(size.width / 2, size.height / 2 - size.height / 3)