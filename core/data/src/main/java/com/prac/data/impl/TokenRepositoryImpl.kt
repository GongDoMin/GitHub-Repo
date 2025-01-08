package com.prac.data.impl

import com.prac.data.model.Token
import com.prac.data.model.toLocalModel
import com.prac.data.model.toModel
import com.prac.data.repository.TokenRepository
import com.prac.local.TokenLocalDataSource
import com.prac.network.AuthApiDataSource
import javax.inject.Inject

internal class TokenRepositoryImpl @Inject constructor(
    private val tokenLocalDataSource: TokenLocalDataSource,
    private val authApiDataSource: AuthApiDataSource,
) : TokenRepository {
    override suspend fun authorizeOAuth(code: String): String {
        val model = authApiDataSource.authorizeOAuth(code).toModel()

        setToken(model)

        return model.accessToken
    }

    override suspend fun isLoggedIn(): Boolean =
        tokenLocalDataSource.getToken().accessToken.isNotEmpty()

    override suspend fun clearToken() {
        tokenLocalDataSource.clearToken()
    }

    private suspend fun setToken(token: Token) {
        tokenLocalDataSource.setToken(token.toLocalModel())
    }
}