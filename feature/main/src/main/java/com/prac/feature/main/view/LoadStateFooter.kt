package com.prac.feature.main.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.LoadState
import com.prac.core.common.constants.CONNECTION_FAIL
import com.prac.core.designsystem.R

@Composable
internal fun LoadStateFooter(
    loadState: LoadState?,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (loadState) {
        is LoadState.Loading -> LoadingFooter(modifier = modifier)

        is LoadState.Error -> ErrorFooter(
            onRetryClick = onRetryClick,
            modifier = modifier
        )

        else -> { }
    }
}

@Composable
internal fun LoadingFooter(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.progressbar)),
            trackColor = Color.Transparent,
            color = Color.Black
        )
    }
}

@Composable
internal fun ErrorFooter(
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = CONNECTION_FAIL,
            color = Color.Black,
            modifier = Modifier
                .padding(
                    bottom = dimensionResource(id = R.dimen.padding_small)
                )
        )

        Button(
            onClick = onRetryClick,
            colors = ButtonColors(
                containerColor = Color.Black,
                contentColor = Color.White,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color.White
            )
        ) {
            Text(text = stringResource(id = R.string.retry))
        }
    }
}

@Preview(showBackground = true)
@Composable
internal fun LoadingFooterPreview() {
    LoadingFooter(
        modifier = Modifier
            .padding(dimensionResource(R.dimen.padding_small))
    )
}

@Preview(showBackground = true)
@Composable
internal fun ErrorFooterPreview() {
    ErrorFooter(
        onRetryClick = {},
        modifier = Modifier
            .padding(dimensionResource(R.dimen.padding_small))
    )
}