package com.prac.feature.main.di

import com.prac.core.common.backoff.BackOffWorkManager
import com.prac.core.common.mvi.action.ActionProcessor
import com.prac.core.common.mvi.reducer.Reducer
import com.prac.data.repository.RepoRepository
import com.prac.data.repository.TokenRepository
import com.prac.domain.ClearLocalDataUseCase
import com.prac.feature.main.MainActionProcessor
import com.prac.feature.main.MainReducerProcessor
import com.prac.feature.main.model.Action
import com.prac.feature.main.model.Event
import com.prac.feature.main.model.Mutation
import com.prac.feature.main.view.UiState
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
internal annotation class MainReducerAnnotation

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
internal annotation class MainActionAnnotation

@Module
@InstallIn(ActivityRetainedComponent::class)
internal class MainModule {
    @Provides
    @MainReducerAnnotation
    fun providesMainReducerProcessor(): Reducer<Mutation, UiState> {
        return MainReducerProcessor()
    }

    @Provides
    @MainActionAnnotation
    fun providesMainActionProcessor(
        repoRepository: RepoRepository,
        clearLocalDataUseCase: ClearLocalDataUseCase,
        backOffWorkManager: BackOffWorkManager
    ): ActionProcessor<Action, Mutation, Event> {
        return MainActionProcessor(
            repoRepository = repoRepository,
            clearLocalDataUseCase = clearLocalDataUseCase,
            backOffWorkManager = backOffWorkManager
        )
    }
}