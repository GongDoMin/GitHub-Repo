package com.prac.data.fake

import com.prac.data.exception.CommonException
import com.prac.data.repository.TokenRepository
import com.prac.local.datastore.token.TokenLocalDto
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class FakeTokenRepository: TokenRepository {

    private var token: TokenLocalDto = makeEmptyToken()

    fun setInitialToken() {
        token = makeNewToken()
    }

    /**
     * @param code 요청 코드.
     *
     * | 값 | 설명 |
     * |---|---|
     * | `success` | 성공 |
     * | `ioException` | IOException 발생 |
     * | `else` | 기타 오류 |
     */

    override suspend fun authorizeOAuth(code: String): Result<Unit> {
        if (code == "success") {
            return Result.success(Unit)
        }

        return when (code) {
            "ioException" -> Result.failure(CommonException.NetworkError())
            else -> Result.failure(CommonException.AuthorizationError())
        }
    }

    override suspend fun isLoggedIn(): Boolean {
        return token.accessToken.isNotEmpty()
    }

    override suspend fun clearToken() {
        token = makeEmptyToken()
    }

    override suspend fun refreshToken(refreshToken: String): Result<Unit> {
        throw NotImplementedError("this method is not supported in fake repository")
    }

    private fun makeEmptyToken() =
        TokenLocalDto(
            accessToken = "",
            refreshToken = "",
            expiresInSeconds = 0,
            refreshTokenExpiresInSeconds = 0,
            updatedAt = Instant.ofEpochMilli(0).atZone(ZoneId.systemDefault())
        )

    private fun makeNewToken() =
        TokenLocalDto(
            accessToken = "accessToken",
            refreshToken = "refreshToken",
            expiresInSeconds = 3600,
            refreshTokenExpiresInSeconds = 18000,
            updatedAt = Instant.ofEpochMilli(ZonedDateTime.now().toInstant().toEpochMilli()).atZone(ZoneId.systemDefault())
        )
}