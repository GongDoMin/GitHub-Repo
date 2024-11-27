package com.prac.feature.login.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.prac.core.common.constants.CONNECTION_FAIL
import com.prac.core.designsystem.R
import com.prac.core.designsystem.component.BounceButton
import com.prac.core.designsystem.component.ErrorAlertDialog
import com.prac.core.designsystem.component.LoadingContent

@Composable
fun LoginContent(
    isLoading: Boolean,
    isError: Boolean,
    errorMessage: String,
    onClickLogin: () -> Unit,
    onDismissRequest: (String) -> Unit
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
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.login_icon))
                    .padding(
                        bottom = dimensionResource(id = R.dimen.padding_normal)
                    ),
                painter = painterResource(id = R.drawable.img_github_icon),
                contentDescription = null
            )

            BounceButton(
                text = stringResource(id = R.string.login),
                onClickButton = onClickLogin
            )

            Text(
                modifier = Modifier
                    .padding(
                        top = dimensionResource(id = R.dimen.padding_small)
                    ),
                text = stringResource(id = R.string.login_description)
            )
        }

        if (isError) {
            ErrorAlertDialog(
                onDismissRequest = onDismissRequest,
                errorMessage = errorMessage,
                confirmButtonText = stringResource(id = R.string.check)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginContentLoadingPreview() {
    LoginContent(
        isLoading = true,
        isError = false,
        errorMessage = "",
        onClickLogin = {},
        onDismissRequest = {}
    )
}

@Preview(showBackground = true)
@Composable
fun LoginContentPreview() {
    LoginContent(
        isLoading = false,
        isError = false,
        errorMessage = "",
        onClickLogin = {},
        onDismissRequest = {}
    )
}

@Preview(showBackground = true)
@Composable
fun LoginContentErrorMessagePreview() {
    LoginContent(
        isLoading = false,
        isError = true,
        errorMessage = CONNECTION_FAIL,
        onClickLogin = {},
        onDismissRequest = {}
    )
}