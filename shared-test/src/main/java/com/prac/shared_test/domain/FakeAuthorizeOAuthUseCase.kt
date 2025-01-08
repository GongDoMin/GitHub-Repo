package com.prac.shared_test.domain

import com.prac.data.exception.CommonException
import com.prac.domain.AuthorizeOAuthUseCase

class FakeAuthorizeOAuthUseCase : AuthorizeOAuthUseCase {
    /**
     * @param code 요청 코드.
     *
     * |       값       | 설명 |
     *
     * |   `success`   | 성공 |
     *
     * | `ioException` | IOException 발생 |
     *
     * |    `else`    | 기타 오류 |
     */
    override suspend fun invoke(code: String): Result<Unit> {
        if (code == "success") {
            return Result.success(Unit)
        }

        return when (code) {
            "ioException" -> Result.failure(CommonException.NetworkError())
            else -> Result.failure(CommonException.AuthorizationError())
        }
    }
}