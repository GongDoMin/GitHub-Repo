package com.prac.githubrepo.di

import com.prac.core.common.mvi.reducer.Reducer
import com.prac.githubrepo.ui.login.LoginReducerProcessor
import com.prac.githubrepo.ui.login.model.Mutation
import com.prac.githubrepo.ui.login.view.UiState
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
    fun providesLoginReducerProcessor(): Reducer<Mutation, UiState> {
        return LoginReducerProcessor()
    }
}