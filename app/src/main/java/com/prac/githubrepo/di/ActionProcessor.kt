package com.prac.githubrepo.di

import com.prac.data.repository.TokenRepository
import com.prac.githubrepo.common.ActionProcessor
import com.prac.githubrepo.ui.login.LoginActionProcessor
import com.prac.githubrepo.ui.login.UserActionProcessor
import com.prac.githubrepo.ui.login.model.Action
import com.prac.githubrepo.ui.login.model.Event
import com.prac.githubrepo.ui.login.model.UiState
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class LoginActionAnnotation

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class UserActionAnnotation

@Module
@InstallIn(SingletonComponent::class)
class ActionProcessorModule {
    @Provides
    @LoginActionAnnotation
    fun providesLoginActionProcessor(tokenRepository: TokenRepository) : ActionProcessor<Action, UiState, Event> {
        return LoginActionProcessor(tokenRepository)
    }

    @Provides
    @UserActionAnnotation
    fun providesUserActionProcessor() : ActionProcessor<Action, UiState, Event> {
        return UserActionProcessor()
    }
}