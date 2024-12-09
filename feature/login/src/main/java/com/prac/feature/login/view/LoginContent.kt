package com.prac.feature.login.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.prac.core.common.constants.CONNECTION_FAIL
import com.prac.core.common.ui.ContentWithLoadingIndicator
import com.prac.core.common.ui.MessageDialog
import com.prac.core.common.ui.bounceClick
import com.prac.core.designsystem.R

@Composable
fun LoginContent(
    isLoading: Boolean,
    isError: Boolean,
    errorMessage: String,
    onClickLoginButton: () -> Unit,
    onDismissRequest: (String) -> Unit,
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
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.login_icon)),
                painter = painterResource(id = R.drawable.img_github_icon),
                contentDescription = null
            )

            Spacer(modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.padding_normal)))

            Button(
                onClick = onClickLoginButton,
                modifier = Modifier
                    .fillMaxWidth()
                    .bounceClick(),
                colors = ButtonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White,
                    disabledContainerColor = Color.Gray,
                    disabledContentColor = Color.White
                ),
                contentPadding = PaddingValues(
                    dimensionResource(id = R.dimen.padding_normal)
                )
            ) {
                Text(
                    text = stringResource(id = R.string.login)
                )
            }

            Spacer(modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.padding_small)))

            Text(text = stringResource(id = R.string.login_description))
        }

        if (isError) {
            MessageDialog(
                onDismissRequest = onDismissRequest,
                message = errorMessage,
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
        onClickLoginButton = {},
        onDismissRequest = {},
        modifier = Modifier
            .padding(dimensionResource(id = R.dimen.padding_normal))
    )
}

@Preview(showBackground = true)
@Composable
fun LoginContentPreview() {
    LoginContent(
        isLoading = false,
        isError = false,
        errorMessage = "",
        onClickLoginButton = {},
        onDismissRequest = {},
        modifier = Modifier
            .padding(dimensionResource(id = R.dimen.padding_normal))
    )
}

@Preview(showBackground = true)
@Composable
fun LoginContentErrorMessagePreview() {
    LoginContent(
        isLoading = false,
        isError = true,
        errorMessage = CONNECTION_FAIL,
        onClickLoginButton = {},
        onDismissRequest = {},
        modifier = Modifier
            .padding(dimensionResource(id = R.dimen.padding_normal))
    )
}