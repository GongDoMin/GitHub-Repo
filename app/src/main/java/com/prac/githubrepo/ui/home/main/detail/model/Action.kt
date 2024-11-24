package com.prac.githubrepo.ui.home.main.detail.model

import com.prac.data.entity.RepoEntity

sealed interface Action {
    data object UserAction {
        data class OnClickUnStar(val repoEntity: RepoEntity) : Action
        data class OnClickStar(val repoEntity: RepoEntity) : Action
        data object DialogDismiss : Action
        data object LogoutDialogDismiss : Action
    }
    data object InternalAction {
        data class GetRepository(val userName: String, val repoName: String) : Action
    }
}