package com.prac.feature.main.model

import com.prac.data.model.RepoModel

sealed interface Action {
    data object UserAction {
        data class OnClickRepository(val repoModel: RepoModel) : Action
        data class OnClickUnStar(val repoModel: RepoModel) : Action
        data class OnClickStar(val repoModel: RepoModel) : Action
        data object OnClickRetry : Action
        data object LogoutDialogDismiss : Action
        data object DialogDismiss : Action
    }
    data object InternalAction {
        data object Load : Action
        data class FetchStarState(val repoModel: RepoModel) : Action
        data object Logout : Action
    }
}