package com.prac.githubrepo.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.prac.githubrepo.R

@Composable
fun ErrorAlertDialog(
    onDismissRequest: (String) -> Unit,
    errorMessage: String
) {
    AlertDialog(
        modifier = Modifier
            .fillMaxWidth(), // AlertDialog 내부적으로 최대, 최소 사이즈가 있기 때문에 화면 전체로 붙지 않는다.
        onDismissRequest = { onDismissRequest(errorMessage) },
        text = { Text(errorMessage) }, // 내용
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
                    text = stringResource(id = R.string.check),
                    color = Color.White
                )
            }
        }
    )
}

@Composable
fun BasicAlertDialog(
    onDismissRequest: () -> Unit,
    onClickCheckButton: () -> Unit,
    dialogMessage: String
) {
    AlertDialog(
        modifier = Modifier
            .fillMaxWidth(),
        onDismissRequest = onDismissRequest,
        text = { Text(dialogMessage) }, // 내용
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
                    text = stringResource(id = R.string.cancel),
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
                    text = stringResource(id = R.string.check),
                    color = Color.White
                )
            }
        }
    )
}