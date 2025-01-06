package com.prac.feature.login.di

import com.prac.core.common.mvi.action.ActionProcessor
import com.prac.core.common.mvi.reducer.Reducer
import com.prac.data.repository.TokenRepository
import com.prac.domain.AuthorizeOAuthUseCase
import com.prac.feature.login.LoginActionProcessor
import com.prac.feature.login.LoginReducerProcessor
import com.prac.feature.login.model.Action
import com.prac.feature.login.model.Event
import com.prac.feature.login.model.Mutation
import com.prac.feature.login.view.UiState
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
internal annotation class LoginReducerAnnotation

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
internal annotation class LoginActionAnnotation

@Module
@InstallIn(ActivityRetainedComponent::class)
internal class LoginModule {
    @Provides
    @LoginReducerAnnotation
    fun providesLoginReducerProcessor(): Reducer<Mutation, UiState> {
        return LoginReducerProcessor()
    }

    @Provides
    @LoginActionAnnotation
    fun providesLoginActionProcessor(
        tokenRepository: TokenRepository,
        authorizeOAuthUseCase: AuthorizeOAuthUseCase
    ): ActionProcessor<Action, Mutation, Event> {
        return LoginActionProcessor(tokenRepository, authorizeOAuthUseCase)
    }
}
