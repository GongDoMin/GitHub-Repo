package com.prac.feature.detail.di

import com.prac.core.common.mvi.reducer.Reducer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class DetailReducerAnnotation

@Module
@InstallIn(ActivityRetainedComponent::class)
class DetailReducerProcessorModule {
    @Provides
    @DetailReducerAnnotation
    fun providesDetailReducerProcessor(): Reducer<com.prac.feature.detail.model.Mutation, com.prac.feature.detail.view.UiState> {
        return com.prac.feature.detail.DetailReducerProcessor()
    }
}