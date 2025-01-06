package com.prac.auth.impl

import com.prac.auth.AuthManager
import com.prac.auth.model.TokenModel
import com.prac.local.TokenLocalDataSource
import com.prac.local.datastore.token.TokenLocalDto
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

internal class BearerAuthImpl @Inject constructor(
    private val tokenLocalDataSource: TokenLocalDataSource
) : AuthManager {
    override fun getAccessToken(refreshAccessToken: suspend (refreshToken: String) -> TokenModel): String {
        if (tokenLocalDataSource.getToken().isExpired) {
            synchronized(this) {
                if (tokenLocalDataSource.getToken().isRefreshTokenExpired) {
                    runBlocking {
                        tokenLocalDataSource.clearToken()
                    }
                    return@synchronized
                }

                runBlocking {
                    try {
                        val response = refreshAccessToken(tokenLocalDataSource.getToken().refreshToken)
                        tokenLocalDataSource.setToken(
                            TokenLocalDto(
                                accessToken = response.accessToken,
                                refreshToken = response.refreshToken,
                                expiresInSeconds = response.expiredIn,
                                refreshTokenExpiresInSeconds = response.refreshExpiredIn,
                                updatedAt = response.updatedAt
                            )
                        )
                    } catch (e: Exception) {
                        tokenLocalDataSource.clearToken()
                    }
                }
            }
        }

        return tokenLocalDataSource.getToken().accessToken
    }
}