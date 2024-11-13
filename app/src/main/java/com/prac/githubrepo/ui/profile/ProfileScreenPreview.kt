package com.prac.githubrepo.ui.profile

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.prac.githubrepo.R

@Preview(showBackground = true)
@Composable
fun ProfileContentIsLoadingPreview() {
    ProfileContent(
        isLoading = true,
        dialogMessage = "",
        onClickLogoutButton = { },
        onClickCheckButton = { },
        onDismissRequest = { }
    )
}


@Preview(showBackground = true)
@Composable
fun ProfileContentPreview() {
    ProfileContent(
        isLoading = false,
        dialogMessage = "",
        onClickLogoutButton = { },
        onClickCheckButton = { },
        onDismissRequest = { }
    )
}

@Preview(showBackground = true)
@Composable
fun ProfileContentDialogPreview() {
    ProfileContent(
        isLoading = false,
        dialogMessage = stringResource(id = R.string.logout_confirm),
        onClickLogoutButton = { },
        onClickCheckButton = { },
        onDismissRequest = { }
    )
}