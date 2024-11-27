package com.prac.core.designsystem.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun BasicAlertDialog(
    onDismissRequest: () -> Unit,
    onClickCheckButton: () -> Unit,
    dialogMessage: String,
    cancelButtonText: String,
    confirmButtonText: String
) {
    AlertDialog(
        modifier = Modifier
            .fillMaxWidth(),
        onDismissRequest = onDismissRequest,
        text = { Text(dialogMessage) },
        confirmButton = {
            Button(
                onClick = onDismissRequest,
                colors = ButtonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White,
                    disabledContainerColor = Color.Red.copy(alpha = 0.3f),
                    disabledContentColor = Color.White
                )
            ) {
                Text(
                    text = cancelButtonText,
                    color = Color.White
                )
            }

            Button(
                onClick = onClickCheckButton,
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

@Composable
fun ErrorAlertDialog(
    onDismissRequest: (String) -> Unit,
    errorMessage: String,
    confirmButtonText: String
) {
    AlertDialog(
        modifier = Modifier
            .fillMaxWidth(),
        onDismissRequest = { onDismissRequest(errorMessage) },
        text = { Text(errorMessage) },
        confirmButton = {
            Button(
                onClick = { onDismissRequest(errorMessage) },
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