package com.prac.githubrepo.ui.home.main.detail.model

import com.prac.data.entity.RepoDetailEntity

sealed interface Action {
    data object UserAction {
        data class OnClickUnStar(val repoDetailEntity: RepoDetailEntity) : Action
        data class OnClickStar(val repoDetailEntity: RepoDetailEntity) : Action
        data object DialogDismiss : Action
        data object LogoutDialogDismiss : Action
    }
    data object InternalAction {
        data object GetRepository : Action
    }
}