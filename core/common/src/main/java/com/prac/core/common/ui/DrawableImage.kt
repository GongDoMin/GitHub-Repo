package com.prac.core.common.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource

@Composable
fun DrawableImage(
    @DrawableRes res: Int,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    Image(
        modifier = modifier,
        painter = painterResource(id = res), contentDescription = contentDescription
    )
}