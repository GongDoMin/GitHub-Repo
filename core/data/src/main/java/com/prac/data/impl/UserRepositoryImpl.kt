package com.prac.data.impl

import com.prac.data.repository.UserRepository
import com.prac.local.UserLocalDataSource
import com.prac.network.UserApiDataSource
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApiDataSource: UserApiDataSource,
    private val userLocalDataSource: UserLocalDataSource
) : UserRepository {
    override suspend fun getApiUserName(accessToken: String): String =
        // 404 : Token is invalid
        // 422 : Client id or Client secret is invalid
        // authorizeOAuth 에서 에러 처리
        userApiDataSource.getUserName(accessToken)

    override suspend fun getLocalUserName(): String =
        userLocalDataSource.getUserName()

    override suspend fun setUserName(userName: String) {
        userLocalDataSource.setUserName(userName)
    }

    override suspend fun clearUserName() {
        userLocalDataSource.clearUserName()
    }
}