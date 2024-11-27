package com.prac.shared_test.data

import com.prac.exception.CommonException
import com.prac.data.repository.TokenRepository

class FakeTokenRepository(
    private var token: String = ""
): TokenRepository {

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
            "ioException" -> Result.failure(com.prac.exception.CommonException.NetworkError())
            else -> Result.failure(com.prac.exception.CommonException.AuthorizationError())
        }
    }

    override suspend fun isLoggedIn(): Boolean {
        return token.isNotEmpty()
    }

    override suspend fun clearToken() {
        token = ""
    }

    override suspend fun refreshToken(refreshToken: String): Result<Unit> {
        throw NotImplementedError("this method is not supported in fake repository")
    }
}