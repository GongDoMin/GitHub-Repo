package com.prac.core.common.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput

private enum class ButtonState(val targetFloat: Float) {
    Pressed(0.95f),
    Idle(1f)
}

@Composable
fun Modifier.bounceClick() : Modifier {
    var buttonState by remember { mutableStateOf(ButtonState.Idle) }
    val scale by animateFloatAsState(
        targetValue = buttonState.targetFloat,
        label = ""
    )

    val bounceClickModifier =
            Modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .pointerInput(buttonState) {
                    awaitPointerEventScope {
                        buttonState = if (buttonState == ButtonState.Pressed) {
                            waitForUpOrCancellation()
                            ButtonState.Idle
                        } else {
                            awaitFirstDown(false)
                            ButtonState.Pressed
                        }
                    }
                }

    return this then bounceClickModifier
}