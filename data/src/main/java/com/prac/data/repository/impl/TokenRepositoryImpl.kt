package com.prac.data.repository.impl

import com.prac.data.exception.CommonException
import com.prac.data.repository.TokenRepository
import com.prac.data.repository.model.TokenModel
import com.prac.data.source.network.AuthApiDataSource
import com.prac.data.source.network.UserApiDataSource
import com.prac.local.TokenLocalDataSource
import com.prac.local.UserLocalDataSource
import com.prac.local.datastore.token.TokenLocalDto
import java.io.IOException
import javax.inject.Inject

internal class TokenRepositoryImpl @Inject constructor(
    private val tokenLocalDataSource: TokenLocalDataSource,
    private val authApiDataSource: AuthApiDataSource,
    private val userApiDataSource: UserApiDataSource,
    private val userLocalDataSource: UserLocalDataSource
) : TokenRepository {
    override suspend fun authorizeOAuth(code: String): Result<Unit> {
        return try {
            val model = authApiDataSource.authorizeOAuth(code)
            val userName = getUserName(model.accessToken)

            setToken(model)
            setUserName(userName)

            Result.success(Unit)
        } catch (e: Exception) {
            when (e) {
                is IOException -> {
                    Result.failure(CommonException.NetworkError())
                }
                else -> {
                    Result.failure(CommonException.AuthorizationError())
                }
            }
        }
    }

    override suspend fun isLoggedIn(): Boolean {
        return getAccessToken().isNotEmpty()
    }

    override suspend fun clearToken() {
        tokenLocalDataSource.clearToken()
    }

    override suspend fun refreshToken(refreshToken: String): Result<Unit> {
        return try {
            val model = authApiDataSource.refreshAccessToken(refreshToken)
            setToken(model)

            Result.success(Unit)
        } catch (e: Exception) {
            // 예외 발생 시 로그아웃 처리할 것이라서 따로 처리를 하지 않는다.
            Result.failure(e)
        }
    }

    override fun getAccessToken(): String {
        return tokenLocalDataSource.getToken().accessToken
    }

    override fun getRefreshToken(): String {
        return tokenLocalDataSource.getToken().refreshToken
    }

    override fun getAccessTokenIsExpired(): Boolean {
        return tokenLocalDataSource.getToken().isExpired
    }

    override fun getRefreshTokenIsExpired(): Boolean {
        return tokenLocalDataSource.getToken().isRefreshTokenExpired
    }

    private suspend fun setToken(token: TokenModel) {
        tokenLocalDataSource.setToken(
            TokenLocalDto(
                token.accessToken,
                token.refreshToken,
                token.expiresInSeconds,
                token.refreshTokenExpiresInSeconds,
                token.updatedAt
            )
        )
    }

    private suspend fun getUserName(accessToken: String) : String {
        // 404 : Token is invalid
        // 422 : Client id or Client secret is invalid
        // authorizeOAuth 에서 에러 처리
        return userApiDataSource.getUserName(accessToken)
    }

    private suspend fun setUserName(userName: String) {
        userLocalDataSource.setUserName(userName)
    }
}