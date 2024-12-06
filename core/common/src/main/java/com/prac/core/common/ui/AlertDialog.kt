package com.prac.core.common.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun ConfirmationDialog(
    onDismissRequest: () -> Unit,
    message: String,
    negativeButtonText: String,
    onClickNegativeButton: () -> Unit,
    positiveButtonText: String,
    onClickPositiveButton: () -> Unit,
) {
    AlertDialog(
        modifier = Modifier
            .fillMaxWidth(),
        onDismissRequest = onDismissRequest,
        text = { Text(message) },
        confirmButton = {
            Button(
                onClick = onClickNegativeButton,
                colors = ButtonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White,
                    disabledContainerColor = Color.Red.copy(alpha = 0.3f),
                    disabledContentColor = Color.White
                )
            ) {
                Text(
                    text = negativeButtonText,
                    color = Color.White
                )
            }

            Button(
                onClick = onClickPositiveButton,
                colors = ButtonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White,
                    disabledContainerColor = Color.Gray,
                    disabledContentColor = Color.White
                )
            ) {
                Text(
                    text = positiveButtonText,
                    color = Color.White
                )
            }
        }
    )
}

@Composable
fun MessageDialog(
    onDismissRequest: (String) -> Unit,
    message: String,
    confirmButtonText: String,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = { onDismissRequest(message) },
        text = { Text(message) },
        confirmButton = {
            Button(
                onClick = { onDismissRequest(message) },
                colors = ButtonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White,
                    disabledContainerColor = Color.Gray,
                    disabledContentColor = Color.White
                )
            ) {
                Text(
                    text = confirmButtonText,
                    color = Color.White
                )
            }
        }
    )
}