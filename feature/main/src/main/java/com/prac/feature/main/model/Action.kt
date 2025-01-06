package com.prac.feature.main.model

import com.prac.feature.main.refresh.RefreshState

sealed interface Action {
    data object UserAction {
        data class OnClickRepository(val repository: Repository) : Action
        data class OnClickUnStar(val repository: Repository) : Action
        data class OnClickStar(val repository: Repository) : Action
        data object OnClickRetry : Action
        data object LogoutDialogDismiss : Action
        data object DialogDismiss : Action
    }
    data object InternalAction {
        data object Load : Action
        data class FetchStarState(val repository: Repository) : Action
        data object Logout : Action
        data class UpdateRefreshState(val refreshState: RefreshState) : Action
    }
}