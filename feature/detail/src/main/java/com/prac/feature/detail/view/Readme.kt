package com.prac.feature.detail.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.prac.core.designsystem.R
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun ReadMe(
    readme: String,
    modifier: Modifier = Modifier
) {
    if (readme.isNotEmpty()) {
        Column(
            modifier = modifier
        ) {
            Text(
                text = stringResource(id = R.string.readme)
            )

            HorizontalDivider(
                modifier = Modifier
                    .padding(vertical = dimensionResource(id = R.dimen.padding_normal))
            )

            MarkdownText(markdown = readme)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReadmePreview() {
    ReadMe(
        modifier = Modifier
            .padding(16.dp),
        readme = "hi!!!!"
    )
}