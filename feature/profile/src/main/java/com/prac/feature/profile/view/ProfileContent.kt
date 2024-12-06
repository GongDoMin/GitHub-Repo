package com.prac.feature.profile.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.prac.core.designsystem.R
import com.prac.core.designsystem.component.BounceButton
import com.prac.core.common.ui.ConfirmationDialog
import com.prac.core.designsystem.component.ContentWithLoadingIndicator

@Composable
fun ProfileContent(
    isLoading: Boolean,
    isDialog: Boolean,
    onClickLogoutButton: () -> Unit,
    onClickNegativeButton: () -> Unit,
    onClickPositiveButton: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    loadingModifier: Modifier = Modifier
) {
    ContentWithLoadingIndicator(
        isLoading = isLoading,
        modifier = loadingModifier
    ) {
        Column(
            modifier = modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            BounceButton(
                text = stringResource(id = R.string.logout),
                onClickButton = onClickLogoutButton,
                modifier = Modifier
                    .fillMaxWidth()
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
        onDismissRequest = {},
        modifier = Modifier.padding(horizontal = 16.dp)
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
        onDismissRequest = {},
        modifier = Modifier.padding(horizontal = 16.dp)
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
        onDismissRequest = {},
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}