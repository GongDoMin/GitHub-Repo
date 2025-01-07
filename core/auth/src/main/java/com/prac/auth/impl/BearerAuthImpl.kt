package com.prac.auth.impl

import com.prac.auth.AuthManager
import com.prac.auth.model.TokenModel
import com.prac.auth.model.toTokenLocalDto
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
                                refreshAccessToken(tokenLocalDataSource.getToken().refreshToken).toTokenLocalDto()
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