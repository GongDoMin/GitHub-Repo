package com.prac.data.impl

import com.prac.data.repository.TokenRepository
import com.prac.local.TokenLocalDataSource
import com.prac.local.datastore.token.TokenLocalDto
import com.prac.network.AuthApiDataSource
import com.prac.network.model.TokenDto
import java.time.ZonedDateTime
import javax.inject.Inject

internal class TokenRepositoryImpl @Inject constructor(
    private val tokenLocalDataSource: TokenLocalDataSource,
    private val authApiDataSource: AuthApiDataSource,
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