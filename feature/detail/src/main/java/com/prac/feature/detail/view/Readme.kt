package com.prac.feature.detail.view

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.prac.core.designsystem.R
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun ReadMe(readme: String) {
    if (readme.isNotEmpty()) {
        Text(
            modifier = Modifier
                .padding(top = dimensionResource(id = R.dimen.padding_normal)),
            text = stringResource(id = R.string.readme)
        )

        HorizontalDivider(
            modifier = Modifier
                .padding(vertical = dimensionResource(id = R.dimen.padding_normal))
        )

        MarkdownText(markdown = readme)
    }
}