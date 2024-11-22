package com.prac.githubrepo.ui.profile.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.prac.githubrepo.R
import com.prac.githubrepo.components.BasicAlertDialog
import com.prac.githubrepo.components.BounceButton
import com.prac.githubrepo.components.LoadingContent

@Composable
fun ProfileContent(
    isLoading: Boolean,
    dialogMessage: String,
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

        if (dialogMessage.isNotEmpty()) {
            BasicAlertDialog(
                onDismissRequest = onDismissRequest,
                onClickCheckButton = onClickCheckButton,
                dialogMessage = dialogMessage
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileContentPreview() {
    ProfileContent(
        isLoading = false,
        dialogMessage = "",
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
        dialogMessage = "",
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
        dialogMessage = stringResource(id = R.string.logout_confirm),
        onClickLogoutButton = {},
        onClickCheckButton = {},
        onDismissRequest = {}
    )
}