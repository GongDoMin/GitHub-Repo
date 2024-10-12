package com.prac.data.source.network.service

import com.prac.data.repository.TokenRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

internal class AuthorizationInterceptor @Inject constructor(
    private val tokenRepository: TokenRepository
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (tokenRepository.getAccessTokenIsExpired()) {
            synchronized(this) {
                if (tokenRepository.getAccessTokenIsExpired()) {
                    if (tokenRepository.getRefreshTokenIsExpired()) {
                        runBlocking {
                            tokenRepository.clearToken()
                        }
                    }

                    runBlocking {
                        tokenRepository.refreshToken(tokenRepository.getRefreshToken())
                            .onFailure {
                                tokenRepository.clearToken()
                            }
                    }
                }
            }
        }

        val accessToken = tokenRepository.getAccessToken()

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