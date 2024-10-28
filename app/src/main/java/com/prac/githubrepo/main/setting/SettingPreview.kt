package com.prac.githubrepo.main.setting

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

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
        dialogMessage = "정말 로그아웃하시겠습니까?",
        onClickLogoutButton = { },
        onClickCheckButton = { },
        onDismissRequest = { }
    )
}