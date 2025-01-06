package com.prac.feature.detail.model

sealed interface Action {
    data object UserAction {
        data class OnClickUnStar(val repository: RepositoryDetail) : Action
        data class OnClickStar(val repository: RepositoryDetail) : Action
        data object DialogDismiss : Action
        data object LogoutDialogDismiss : Action
    }
    data object InternalAction {
        data class GetRepository(val userName: String?, val repoName: String?) : Action
    }
}