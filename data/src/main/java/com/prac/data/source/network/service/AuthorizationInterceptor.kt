package com.prac.data.source.network.service

import com.prac.data.repository.TokenRepository
import com.prac.data.source.local.datastore.TokenDataStoreManager
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
                        // Token 정보 초기화 및 return refreshTokenExpired Request
                    }

                    runBlocking {
                        tokenRepository.refreshToken(tokenRepository.getRefreshToken())
                            .onFailure {
                                // 잘못된 RefreshToken 또는 인터넷 연결 불안정
                                // Token 정보 초기화
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