package com.prac.githubrepo.di

import com.prac.core.common.mvi.reducer.Reducer
import com.prac.githubrepo.ui.home.main.MainReducerProcessor
import com.prac.githubrepo.ui.home.main.model.Mutation
import com.prac.githubrepo.ui.home.main.view.UiState
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class MainReducerAnnotation

@Module
@InstallIn(SingletonComponent::class)
class MainReducerProcessorModule {
    @Provides
    @MainReducerAnnotation
    fun providesMainReducerProcessor(): Reducer<Mutation, UiState> {
        return MainReducerProcessor()
    }
}