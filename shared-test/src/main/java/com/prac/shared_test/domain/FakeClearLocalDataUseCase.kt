package com.prac.shared_test.domain

import com.prac.domain.ClearLocalDataUseCase

class FakeClearLocalDataUseCase : ClearLocalDataUseCase {
    private var userName = "test"
    private var accessToken = "accessToken"
    private var list = listOf("test1", "test2")

    override suspend fun invoke() {
        userName = ""
        accessToken = ""
        list = emptyList()
    }

    fun isCleared() =
        userName.isEmpty() && accessToken.isEmpty() && list.isEmpty()
}