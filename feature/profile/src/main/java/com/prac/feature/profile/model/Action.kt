package com.prac.feature.profile.model

sealed interface Action {
    data object UserAction {
        data object OnClickLogoutButton : Action
        data object DialogDismiss : Action
        data object OnClickNegativeButton : Action
        data object OnClickPositiveButton : Action
    }
}