package com.prac.core.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.prac.core.designsystem.R

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun UserProfile(
    modifier: Modifier = Modifier,
    uri: String
) {
    GlideImage(
        modifier = modifier,
        model = uri,
        loading = placeholder(R.drawable.img_glide_profile),
        failure = placeholder(R.drawable.img_glide_error),
        contentDescription = null
    )
}