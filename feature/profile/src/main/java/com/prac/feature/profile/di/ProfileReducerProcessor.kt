package com.prac.feature.profile.di

import com.prac.core.common.mvi.reducer.Reducer
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
    fun providesProfileReducerProcessor(): Reducer<com.prac.feature.profile.model.Mutation, com.prac.feature.profile.view.UiState> {
        return com.prac.feature.profile.ProfileReducerProcessor()
    }
}