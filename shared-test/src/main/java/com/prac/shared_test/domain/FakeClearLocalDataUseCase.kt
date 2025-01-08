package com.prac.shared_test.domain

import com.prac.domain.ClearLocalDataUseCase

class FakeClearLocalDataUseCase(
    private var userName: String,
    private var accessToken: String,
    private var list: List<String>
) : ClearLocalDataUseCase {

    override suspend fun invoke() {
        userName = ""
        accessToken = ""
        list = emptyList()
    }

    // this method is for test
    fun isCleared() =
        userName.isEmpty() && accessToken.isEmpty() && list.isEmpty()
}