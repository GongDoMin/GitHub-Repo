package com.prac.data.repository

interface TokenRepository {
    suspend fun getTokenApi(
        code: String
    ) : Result<Unit>

    suspend fun isLoggedIn() : Boolean

    /**
     * This methods is intended for internal use by the interceptor and should not be called from the ViewModel.
     */

    fun getAccessToken() : String

    fun getRefreshToken() : String

    fun getAccessTokenIsExpired() : Boolean

    fun getRefreshTokenIsExpired() : Boolean
}