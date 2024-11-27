package com.prac.githubrepo.di

import com.prac.core.common.mvi.reducer.Reducer
import com.prac.githubrepo.ui.profile.ProfileReducerProcessor
import com.prac.githubrepo.ui.profile.model.Mutation
import com.prac.githubrepo.ui.profile.view.UiState
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ProfileReducerAnnotation

@Module
@InstallIn(SingletonComponent::class)
class ProfileReducerProcessorModule {
    @Provides
    @ProfileReducerAnnotation
    fun providesProfileReducerProcessor(): Reducer<Mutation, UiState> {
        return ProfileReducerProcessor()
    }
}