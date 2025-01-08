package com.prac.auth.impl

import com.prac.auth.AuthManager
import com.prac.auth.model.TokenModel
import com.prac.auth.model.toLocalModel
import com.prac.local.TokenLocalDataSource
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

internal class AuthManagerImpl @Inject constructor(
    private val tokenLocalDataSource: TokenLocalDataSource
) : AuthManager {
    override fun getAccessToken(refreshAccessToken: suspend (refreshToken: String) -> TokenModel): String {
        if (tokenLocalDataSource.getToken().isExpired) {
            synchronized(this) {
                if (tokenLocalDataSource.getToken().isExpired) {
                    if (tokenLocalDataSource.getToken().isRefreshTokenExpired) {
                        runBlocking {
                            tokenLocalDataSource.clearToken()
                        }
                        return@synchronized
                    }

                    runBlocking {
                        try {
                            tokenLocalDataSource.setToken(
                                refreshAccessToken(tokenLocalDataSource.getToken().refreshToken).toLocalModel()
                            )
                        } catch (e: Exception) {
                            tokenLocalDataSource.clearToken()
                        }
                    }
                }
            }
        }

        return tokenLocalDataSource.getToken().accessToken
    }
}