package com.prac.githubrepo.di

import com.prac.githubrepo.common.Reducer
import com.prac.githubrepo.ui.login.LoginReducer
import com.prac.githubrepo.ui.login.model.Mutation
import com.prac.githubrepo.ui.login.model.UiState
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class LoginReducerProcessor

@Module
@InstallIn(SingletonComponent::class)
class ReducerProcessorModule {
    @Provides
    @LoginActionAnnotation
    fun providesLoginReducerProcessor(): Reducer<Mutation, UiState> {
        return LoginReducer()
    }
}