package com.prac.feature.login.view

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.prac.core.common.ui.DrawableImage
import com.prac.core.common.ui.bounceClick
import com.prac.core.designsystem.R

@Composable
fun LoginContent(
    onClickLoginButton: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DrawableImage(
            res = R.drawable.img_github_icon,
            contentDescription = "",
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.login_icon)),
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
}

@Preview(showBackground = true)
@Composable
fun LoginContentPreview() {
    LoginContent(
        onClickLoginButton = {},
        modifier = Modifier
            .padding(dimensionResource(id = R.dimen.padding_normal))
    )
}