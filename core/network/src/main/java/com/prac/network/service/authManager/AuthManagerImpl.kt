package com.prac.network.service.authManager

import com.prac.local.TokenLocalDataSource
import com.prac.local.datastore.token.TokenLocalDto
import com.prac.network.AuthApiDataSource
import kotlinx.coroutines.runBlocking
import java.time.ZonedDateTime
import javax.inject.Inject

class AuthManagerImpl @Inject constructor(
    private val authApiDataSource: AuthApiDataSource,
    private val tokenLocalDataSource: TokenLocalDataSource
) : AuthManager {
    override fun checkTokenIsExpired() {
        /**
         * [isAccessTokenIsExpired] 을 통해서 accessToken 이 만료되었는지 확인
         */
        if (isAccessTokenIsExpired()) {
            synchronized(this) {
                /**
                 * [refreshAccessToken] 을 통해서 accessToken 을 refresh 했을 수 있기 때문에
                 * accessToken 이 만료되었는지 재확인
                 */
                if (isAccessTokenIsExpired()) {
                    /**
                     * [isRefreshTokenIsExpired] 을 통해서 refreshToken 이 만료되었는지 확인
                     */
                    if (isRefreshTokenIsExpired()) {
                        clearToken()
                        return@synchronized
                    }

                    refreshAccessToken()
                }
            }
        }
    }

    override fun getAccessToken(): String = tokenLocalDataSource.getToken().accessToken

    private fun isAccessTokenIsExpired() : Boolean = tokenLocalDataSource.getToken().isExpired

    private fun isRefreshTokenIsExpired() : Boolean = tokenLocalDataSource.getToken().isRefreshTokenExpired

    private fun refreshAccessToken() {
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
                clearToken()
            }
        }
    }

    private fun clearToken() {
        runBlocking {
            tokenLocalDataSource.clearToken()
        }
    }
}