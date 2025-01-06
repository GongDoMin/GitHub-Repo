package com.prac.network.service

import com.prac.auth.AuthManager
import com.prac.network.dto.toTokenModel
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

internal class AuthorizationInterceptor @Inject constructor(
    private val gitHubAuthService: GitHubAuthService,
    private val authManager: AuthManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val accessToken = authManager.getAccessToken(
            refreshAccessToken = {
                gitHubAuthService.refreshAccessToken(refreshToken = it).toTokenModel()
            }
        )

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