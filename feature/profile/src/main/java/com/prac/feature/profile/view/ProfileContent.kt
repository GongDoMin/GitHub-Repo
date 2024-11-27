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
import com.prac.core.designsystem.component.BasicAlertDialog
import com.prac.core.designsystem.component.BounceButton
import com.prac.core.designsystem.component.LoadingContent

@Composable
fun ProfileContent(
    isLoading: Boolean,
    isDialog: Boolean,
    onClickLogoutButton: () -> Unit,
    onClickCheckButton: () -> Unit,
    onDismissRequest: () -> Unit
) {
    LoadingContent(
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
            val dialogMessage = stringResource(id = R.string.logout_confirm)
            BasicAlertDialog(
                onDismissRequest = onDismissRequest,
                onClickCheckButton = onClickCheckButton,
                dialogMessage = dialogMessage,
                cancelButtonText = stringResource(id = R.string.cancel),
                confirmButtonText = stringResource(id = R.string.check)
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
        onClickCheckButton = {},
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
        onClickCheckButton = {},
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
        onClickCheckButton = {},
        onDismissRequest = {}
    )
}