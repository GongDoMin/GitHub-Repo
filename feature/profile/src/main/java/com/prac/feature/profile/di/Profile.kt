package com.prac.feature.profile.di

import com.prac.core.common.backoff.BackOffWorkManager
import com.prac.core.common.mvi.action.ActionProcessor
import com.prac.core.common.mvi.reducer.Reducer
import com.prac.data.repository.RepoRepository
import com.prac.data.repository.TokenRepository
import com.prac.feature.profile.ProfileActionProcessor
import com.prac.feature.profile.ProfileReducerProcessor
import com.prac.feature.profile.model.Action
import com.prac.feature.profile.model.Event
import com.prac.feature.profile.model.Mutation
import com.prac.feature.profile.view.UiState
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ProfileReducerAnnotation

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ProfileActionAnnotation

@Module
@InstallIn(ActivityRetainedComponent::class)
class ProfileModule {
    @Provides
    @ProfileReducerAnnotation
    fun providesProfileReducerProcessor() : Reducer<Mutation, UiState> {
        return ProfileReducerProcessor()
    }

    @Provides
    @ProfileActionAnnotation
    fun providesProfileActionProcessor(
        tokenRepository: TokenRepository,
        repoRepository: RepoRepository,
        backOffWorkManager: BackOffWorkManager
    ) : ActionProcessor<Action, Mutation, Event> {
        return ProfileActionProcessor(
            tokenRepository = tokenRepository,
            repoRepository = repoRepository,
            backOffWorkManager = backOffWorkManager
        )
    }
}