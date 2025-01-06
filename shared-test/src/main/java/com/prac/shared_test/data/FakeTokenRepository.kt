package com.prac.shared_test.data

import com.prac.data.repository.TokenRepository
import com.prac.data.exception.CommonException

class FakeTokenRepository(
    private var token: String = ""
): TokenRepository {
    /**
     * @param code 요청 코드.
     *
     * |       값       | 설명 |
     * |   `success`   | 성공 |
     * | `ioException` | IOException 발생 |
     * |    `else`    | 기타 오류 |
     */
    override suspend fun authorizeOAuth(code: String): String {
        if (code == "success") {
            return token
        }

        when (code) {
            "ioException" -> throw CommonException.NetworkError()
            else -> throw CommonException.AuthorizationError()
        }
    }

    override suspend fun isLoggedIn(): Boolean {
        return token.isNotEmpty()
    }

    override suspend fun clearToken() {
        token = ""
    }
}