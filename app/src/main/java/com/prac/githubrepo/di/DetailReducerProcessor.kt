package com.prac.githubrepo.di

import com.prac.githubrepo.common.Reducer
import com.prac.githubrepo.ui.home.main.detail.DetailReducerProcessor
import com.prac.githubrepo.ui.home.main.detail.model.Mutation
import com.prac.githubrepo.ui.home.main.detail.view.UiState
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class DetailReducerAnnotation

@Module
@InstallIn(SingletonComponent::class)
class DetailReducerProcessorModule {
    @Provides
    @DetailReducerAnnotation
    fun providesDetailReducerProcessor(): Reducer<Mutation, UiState> {
        return DetailReducerProcessor()
    }
}