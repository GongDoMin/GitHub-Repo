package com.prac.githubrepo.util

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.test.SemanticsMatcher

fun hasDrawable(@DrawableRes id: Int): SemanticsMatcher =
    SemanticsMatcher.expectValue(DrawableID, id)

fun hasButton(@StringRes id: Int): SemanticsMatcher =
    SemanticsMatcher.expectValue(ButtonID, id)