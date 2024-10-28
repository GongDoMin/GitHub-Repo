package com.prac.githubrepo.util

import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver

val DrawableID = SemanticsPropertyKey<Int>("drawableID")
var SemanticsPropertyReceiver.drawableID by DrawableID

val ButtonID = SemanticsPropertyKey<Int>("buttonID")
var SemanticsPropertyReceiver.buttonID by ButtonID