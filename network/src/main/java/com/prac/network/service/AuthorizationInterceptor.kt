package com.prac.network.service

import com.prac.local.TokenLocalDataSource
import com.prac.local.datastore.token.TokenLocalDto
import com.prac.network.AuthApiDataSource
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import java.time.ZonedDateTime
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
                            val response = authApiDataSource.refreshAccessToken(tokenLocalDataSource.getToken().refreshToken)
                            tokenLocalDataSource.setToken(
                                TokenLocalDto(
                                    accessToken = response.accessToken,
                                    refreshToken = response.refreshToken,
                                    expiresInSeconds = response.expiresIn,
                                    refreshTokenExpiresInSeconds = response.refreshTokenExpiresIn,
                                    updatedAt = ZonedDateTime.now()
                                )
                            )
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