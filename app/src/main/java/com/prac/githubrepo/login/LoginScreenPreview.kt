package com.prac.githubrepo.login

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.prac.githubrepo.constants.CONNECTION_FAIL
import com.prac.githubrepo.constants.LOGIN_FAIL

@Preview(showBackground = true)
@Composable
fun LoginContentIsLoadingPreview() {
    LoginContent(
        isLoading = true,
        errorMessage = "",
        onDismissRequest = { },
        onLoginButtonClick = { },
    )
}

@Preview(showBackground = true)
@Composable
fun LoginContentPreview() {
    LoginContent(
        isLoading = false,
        errorMessage = "",
        onDismissRequest = { },
        onLoginButtonClick = { },
    )
}

@Preview(showBackground = true)
@Composable
fun LoginContentNetworkErrorPreview() {
    LoginContent(
        isLoading = false,
        errorMessage = CONNECTION_FAIL,
        onDismissRequest = { },
        onLoginButtonClick = { },
    )
}
@Preview(showBackground = true)
@Composable
fun LoginContentLoginFailureErrorPreview() {
    LoginContent(
        isLoading = false,
        errorMessage = LOGIN_FAIL,
        onDismissRequest = { },
        onLoginButtonClick = { },
    )
}

@Preview(showBackground = true)
@Composable
fun LoginContentLoginButtonPreview() {
    LoginButton(
        onLoginButtonClick = { }
    )

}