package com.prac.feature.profile

import com.prac.core.common.backoff.BackOffWorkManager
import com.prac.core.common.mvi.action.ActionProcessor
import com.prac.data.repository.RepoRepository
import com.prac.data.repository.TokenRepository
import com.prac.domain.ClearTokenUseCase
import com.prac.feature.profile.model.Action
import com.prac.feature.profile.model.Event
import com.prac.feature.profile.model.Mutation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow

internal class ProfileActionProcessor(
    private val tokenRepository: TokenRepository,
    private val repoRepository: RepoRepository,
    private val clearTokenUseCase: ClearTokenUseCase,
    private val backOffWorkManager: BackOffWorkManager
) : ActionProcessor<Action, Mutation, Event> {
    override fun invoke(action: Action): Flow<Pair<Mutation?, Event?>> =
        flow {
            when (action) {
                is Action.UserAction.OnClickLogoutButton -> handleClickLogoutButton()
                is Action.UserAction.DialogDismiss -> handleDialogDismiss()
                is Action.UserAction.OnClickNegativeButton -> handleClickNegativeButton()
                is Action.UserAction.OnClickPositiveButton -> handleClickPositiveButton()
            }
        }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.handleClickLogoutButton() {
        emit(Mutation.ShowDialog to null)
    }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.handleDialogDismiss() {
        emit(Mutation.ShowIdle to null)
    }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.handleClickNegativeButton() {
        emit(Mutation.ShowIdle to null)
    }

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.handleClickPositiveButton() {
        emit(Mutation.ShowLoading to null)

        clearTokenUseCase.invoke()
        backOffWorkManager.clearWork()
        repoRepository.clearRepositories()

        emit(null to Event.Logout)
    }
}