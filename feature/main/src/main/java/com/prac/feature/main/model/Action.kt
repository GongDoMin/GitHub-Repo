package com.prac.feature.main.model

import com.prac.data.entity.RepoEntity

sealed interface Action {
    data object UserAction {
        data class OnClickRepository(val repoEntity: RepoEntity) : Action
        data class OnClickUnStar(val repoEntity: RepoEntity) : Action
        data class OnClickStar(val repoEntity: RepoEntity) : Action
        data object OnClickRetry : Action
        data object LogoutDialogDismiss : Action
        data object DialogDismiss : Action
    }
    data object InternalAction {
        data object Load : Action
        data class FetchStarState(val repoEntity: RepoEntity) : Action
    }
}