package com.prac.githubrepo.util

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.prac.githubrepo.R

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun UserProfile(
    uri: String
) {
    GlideImage(
        modifier = Modifier
            .size(dimensionResource(id = R.dimen.user_profile))
            .clip(CircleShape),
        model = uri,
        loading = placeholder(R.drawable.img_glide_profile),
        failure = placeholder(R.drawable.img_glide_error),
        contentDescription = null
    )
}