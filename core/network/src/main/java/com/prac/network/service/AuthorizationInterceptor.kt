package com.prac.network.service

import com.prac.local.TokenLocalDataSource
import com.prac.local.datastore.token.TokenLocalDto
import com.prac.network.AuthApiDataSource
import com.prac.network.service.authManager.AuthManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import java.time.ZonedDateTime
import javax.inject.Inject

internal class AuthorizationInterceptor @Inject constructor(
    private val authManager: AuthManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        authManager.checkTokenIsExpired()

        val accessToken = authManager.getAccessToken()

        val request = chain.request().newBuilder()
            .addHeader(AUTHORIZATION, "$AUTHORIZATION_TYPE $accessToken")
            .build()

        return chain.proceed(request)
    }

    companion object {
        private const val AUTHORIZATION = "Authorization"
        private const val AUTHORIZATION_TYPE = "Bearer"
    }
}