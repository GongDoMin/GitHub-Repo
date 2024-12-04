package com.prac.feature.profile.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.prac.core.designsystem.R
import com.prac.core.designsystem.component.BounceButton
import com.prac.core.designsystem.component.ConfirmationDialog
import com.prac.core.designsystem.component.ContentWithLoadingIndicator

@Composable
fun ProfileContent(
    isLoading: Boolean,
    isDialog: Boolean,
    onClickLogoutButton: () -> Unit,
    onClickNegativeButton: () -> Unit,
    onClickPositiveButton: () -> Unit,
    onDismissRequest: () -> Unit
) {
    ContentWithLoadingIndicator(
        isLoading = isLoading
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = dimensionResource(id = R.dimen.padding_normal),
                    end = dimensionResource(id = R.dimen.padding_normal)
                ),
            verticalArrangement = Arrangement.Center
        ) {
            BounceButton(
                text = stringResource(id = R.string.logout),
                onClickButton = onClickLogoutButton
            )
        }

        if (isDialog) {
            ConfirmationDialog(
                onDismissRequest = onDismissRequest,
                message = stringResource(id = R.string.logout_confirm),
                negativeButtonText = stringResource(id = R.string.cancel),
                onClickNegativeButton = onClickNegativeButton,
                positiveButtonText = stringResource(id = R.string.check),
                onClickPositiveButton = onClickPositiveButton
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileContentPreview() {
    ProfileContent(
        isLoading = false,
        isDialog = false,
        onClickLogoutButton = {},
        onClickNegativeButton = {},
        onClickPositiveButton = {},
        onDismissRequest = {}
    )
}

@Preview(showBackground = true)
@Composable
fun ProfileContentLoadingPreview() {
    ProfileContent(
        isLoading = true,
        isDialog = false,
        onClickLogoutButton = {},
        onClickNegativeButton = {},
        onClickPositiveButton = {},
        onDismissRequest = {}
    )
}

@Preview(showBackground = true)
@Composable
fun ProfileContentDialogPreview() {
    ProfileContent(
        isLoading = true,
        isDialog = true,
        onClickLogoutButton = {},
        onClickNegativeButton = {},
        onClickPositiveButton = {},
        onDismissRequest = {}
    )
}