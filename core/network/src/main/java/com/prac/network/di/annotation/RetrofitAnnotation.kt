package com.prac.network.di.annotation

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GitHubAuthRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GitHubRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GitHubUserRetrofit