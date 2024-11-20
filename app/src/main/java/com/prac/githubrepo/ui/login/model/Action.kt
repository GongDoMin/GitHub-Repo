package com.prac.githubrepo.ui.login.model

sealed class Action {
    data object OnClickLoginButton : Action()
    data class OAuthAuthenticated(val code: String) : Action()
    data object DialogDismiss : Action()
    data object CheckAutoLogin : Action()
}