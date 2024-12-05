package com.prac.feature.main.di

import com.prac.core.common.mvi.reducer.Reducer
import com.prac.feature.main.MainReducerProcessor
import com.prac.feature.main.model.Mutation
import com.prac.feature.main.view.UiState
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class MainReducerAnnotation

@Module
@InstallIn(ActivityRetainedComponent::class)
class MainReducerProcessorModule {
    @Provides
    @MainReducerAnnotation
    fun providesMainReducerProcessor(): Reducer<Mutation, UiState> {
        return MainReducerProcessor()
    }
}