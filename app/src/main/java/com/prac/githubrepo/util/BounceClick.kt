package com.prac.githubrepo.util

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput

enum class ButtonState(val targetFloat: Float) {
    Pressed(0.95f),
    Idle(1f)
}

fun Modifier.bounceClick() = composed {
    var buttonState by remember { mutableStateOf(ButtonState.Idle) }
    val scale by animateFloatAsState(
        targetValue = buttonState.targetFloat,
        label = ""
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        } // 부모와는 독립적으로 그리기 작업을 통해서 invalidate 를 최소화한다.
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null, // bounceClick에서 자체적으로 시각적인 피드백을 제공하므로, clickable의 indication은 null로 설정하여 Ripple 효과가 중복되지 않도록 합니다.
            onClick = {  }
        ) // clickable Modifier의 indication 매개변수는 클릭 시 시각적인 피드백을 제공하는 데 사용됩니다. interactionSource 는 indication 에 상태를 제공해주기 떄문에 remember 를 통해서 단 한번만 생성
        .pointerInput(buttonState) {
            awaitPointerEventScope {
                buttonState = if (buttonState == ButtonState.Pressed) {
                    waitForUpOrCancellation() // 포인터가 올라가거나 취소될 때까지 기다린다.
                    ButtonState.Idle
                } else {
                    awaitFirstDown(false) // If [requireUnconsumed] is `true` and the first down is already consumed in the pass, that gesture is ignored. true 로 지정할 경우 제스처는 무시되기 때문에 buttonState 는 바뀌지 않음
                    ButtonState.Pressed
                }
            }
        } // The pointer input handling [block] will be cancelled and **re-started** when [pointerInput] is recomposed with a different [key1].
}