package com.prac.githubrepo.util

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.test.SemanticsMatcher

fun hasDrawable(@DrawableRes id: Int): SemanticsMatcher =
    SemanticsMatcher.expectValue(DrawableID, id)

fun hasIcon(id: ImageVector): SemanticsMatcher =
    SemanticsMatcher.expectValue(IconID, id)