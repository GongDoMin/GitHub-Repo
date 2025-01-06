package com.prac.network.service

import com.prac.auth.AuthManager
import com.prac.auth.model.TokenModel
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
                val response = gitHubAuthService.refreshAccessToken(refreshToken = it)
                TokenModel(
                    accessToken = response.accessToken,
                    refreshToken = response.refreshToken,
                    expiredIn = response.expiresIn,
                    refreshExpiredIn = response.refreshTokenExpiresIn
                )
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