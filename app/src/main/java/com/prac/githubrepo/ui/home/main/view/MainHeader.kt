package com.prac.githubrepo.ui.home.main.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.prac.githubrepo.R

@Composable
fun MainHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier
                .padding(
                    top = dimensionResource(id = R.dimen.padding_normal),
                    bottom = dimensionResource(id = R.dimen.padding_normal)
                ),
            text = stringResource(id = R.string.repository)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainHeaderPreview() {
    MainHeader()
}