package com.prac.feature.detail.di

import com.prac.core.common.backoff.BackOffWorkManager
import com.prac.core.common.mvi.action.ActionProcessor
import com.prac.core.common.mvi.reducer.Reducer
import com.prac.data.repository.RepoRepository
import com.prac.data.repository.TokenRepository
import com.prac.domain.ClearTokenUseCase
import com.prac.feature.detail.DetailActionProcessor
import com.prac.feature.detail.DetailReducerProcessor
import com.prac.feature.detail.model.Action
import com.prac.feature.detail.model.Event
import com.prac.feature.detail.model.Mutation
import com.prac.feature.detail.view.UiState
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
internal annotation class DetailReducerAnnotation

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
internal annotation class DetailActionAnnotation

@Module
@InstallIn(ActivityRetainedComponent::class)
internal class DetailModule {
    @Provides
    @DetailReducerAnnotation
    fun providesDetailReducerProcessor(): Reducer<Mutation, UiState> {
        return DetailReducerProcessor()
    }

    @Provides
    @DetailActionAnnotation
    fun providesDetailActionProcessor(
        repoRepository: RepoRepository,
        clearTokenUseCase: ClearTokenUseCase,
        backOffWorkManager: BackOffWorkManager
    ): ActionProcessor<Action, Mutation, Event> {
        return DetailActionProcessor(
            repoRepository = repoRepository,
            clearTokenUseCase = clearTokenUseCase,
            backOffWorkManager = backOffWorkManager
        )
    }
}