package com.prac.githubrepo.main.setting

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.prac.githubrepo.R

@Preview(showBackground = true)
@Composable
fun SettingContentIsLoadingPreview() {
    SettingContent(
        isLoading = true,
        dialogMessage = "",
        onClickLogoutButton = { },
        onClickCheckButton = { },
        onDismissRequest = { }
    )
}


@Preview(showBackground = true)
@Composable
fun SettingContentPreview() {
    SettingContent(
        isLoading = false,
        dialogMessage = "",
        onClickLogoutButton = { },
        onClickCheckButton = { },
        onDismissRequest = { }
    )
}

@Preview(showBackground = true)
@Composable
fun SettingContentDialogPreview() {
    SettingContent(
        isLoading = false,
        dialogMessage = stringResource(id = R.string.logout_confirm),
        onClickLogoutButton = { },
        onClickCheckButton = { },
        onDismissRequest = { }
    )
}