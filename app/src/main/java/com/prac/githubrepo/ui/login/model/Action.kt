package com.prac.githubrepo.ui.login.model

sealed interface Action {
    data object UserAction {
        data object OnClickLoginButton : Action
        data object DialogDismiss : Action
    }
    data object InternalAction {
        data class AuthenticateOAuth(val code: String) : Action
        data object CheckAutoLogin : Action
    }
}