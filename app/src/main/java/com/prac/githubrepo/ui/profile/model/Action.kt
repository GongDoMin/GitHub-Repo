package com.prac.githubrepo.ui.profile.model

sealed interface Action {
    data object UserAction {
        data object OnClickLogoutButton : Action
        data object DialogDismiss : Action
        data object OnClickCheckButton : Action
    }
}