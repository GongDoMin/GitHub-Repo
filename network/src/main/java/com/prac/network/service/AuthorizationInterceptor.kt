package com.prac.network.service

import com.prac.local.TokenLocalDataSource
import com.prac.network.AuthApiDataSource
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

internal class AuthorizationInterceptor @Inject constructor(
    private val authApiDataSource: AuthApiDataSource,
    private val tokenLocalDataSource: TokenLocalDataSource
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (tokenLocalDataSource.getToken().isExpired) {
            synchronized(this) {
                if (tokenLocalDataSource.getToken().isExpired) {
                    if (tokenLocalDataSource.getToken().isRefreshTokenExpired) {
                        runBlocking {
                            tokenLocalDataSource.clearToken()
                        }
                    }

                    runBlocking {
                        try {
                            authApiDataSource.refreshAccessToken(tokenLocalDataSource.getToken().refreshToken)
                        } catch (e: Exception) {
                            tokenLocalDataSource.clearToken()
                        }
                    }
                }
            }
        }

        val accessToken = tokenLocalDataSource.getToken().accessToken

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