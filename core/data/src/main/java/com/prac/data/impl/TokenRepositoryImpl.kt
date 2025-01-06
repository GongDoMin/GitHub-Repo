package com.prac.data.impl

import com.prac.data.exception.CommonException
import com.prac.data.repository.TokenRepository
import com.prac.local.TokenLocalDataSource
import com.prac.local.UserLocalDataSource
import com.prac.local.datastore.token.TokenLocalDto
import com.prac.network.AuthApiDataSource
import com.prac.network.UserApiDataSource
import com.prac.network.dto.TokenDto
import java.io.IOException
import java.time.ZonedDateTime
import javax.inject.Inject

internal class TokenRepositoryImpl @Inject constructor(
    private val tokenLocalDataSource: TokenLocalDataSource,
    private val authApiDataSource: AuthApiDataSource,
    private val userApiDataSource: UserApiDataSource,
    private val userLocalDataSource: UserLocalDataSource
) : TokenRepository {
    override suspend fun authorizeOAuth(code: String): String {
        val dto = authApiDataSource.authorizeOAuth(code)

        setToken(dto)

        return dto.accessToken
    }

    override suspend fun isLoggedIn(): Boolean {
        return tokenLocalDataSource.getToken().accessToken.isNotEmpty()
    }

    override suspend fun clearToken() {
        tokenLocalDataSource.clearToken()
        userLocalDataSource.clearUserName()
    }

    private suspend fun setToken(token: TokenDto) {
        tokenLocalDataSource.setToken(
            TokenLocalDto(
                token.accessToken,
                token.refreshToken,
                token.expiresIn,
                token.refreshTokenExpiresIn,
                ZonedDateTime.now()
            )
        )
    }
}