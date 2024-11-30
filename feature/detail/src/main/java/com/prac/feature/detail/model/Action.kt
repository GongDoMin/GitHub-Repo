package com.prac.feature.detail.model

import com.prac.data.model.RepoDetailModel

sealed interface Action {
    data object UserAction {
        data class OnClickUnStar(val repoDetailModel: RepoDetailModel) : Action
        data class OnClickStar(val repoDetailModel: RepoDetailModel) : Action
        data object DialogDismiss : Action
        data object LogoutDialogDismiss : Action
    }
    data object InternalAction {
        data object GetRepository : Action
    }
}