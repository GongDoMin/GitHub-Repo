package com.prac.githubrepo.ui.login.view

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
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.prac.githubrepo.R
import com.prac.githubrepo.components.BounceButton
import com.prac.githubrepo.components.ErrorAlertDialog
import com.prac.githubrepo.components.LoadingContent
import com.prac.githubrepo.constants.CONNECTION_FAIL
import com.prac.githubrepo.util.drawableID

@Composable
fun LoginContent(
    isLoading: Boolean,
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
                    )
                    .semantics { drawableID = R.drawable.img_github_icon },
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

        if (errorMessage.isNotEmpty()) {
            ErrorAlertDialog(
                onDismissRequest = onDismissRequest,
                errorMessage = errorMessage
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginContentLoadingPreview() {
    LoginContent(
        isLoading = true,
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
        errorMessage = CONNECTION_FAIL,
        onClickLogin = {},
        onDismissRequest = {}
    )
}