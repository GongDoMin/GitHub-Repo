package com.prac.feature.login.di

import com.prac.core.common.mvi.reducer.Reducer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class LoginReducerAnnotation

@Module
@InstallIn(SingletonComponent::class)
class LoginReducerProcessorModule {
    @Provides
    @LoginReducerAnnotation
    fun providesLoginReducerProcessor(): Reducer<com.prac.feature.login.model.Mutation, com.prac.feature.login.view.UiState> {
        return com.prac.feature.login.LoginReducerProcessor()
    }
}