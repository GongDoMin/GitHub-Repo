package com.prac.data.source.local

import com.prac.data.source.local.datastore.TokenLocalDto

internal interface TokenLocalDataSource {
    suspend fun setToken(token: TokenLocalDto)

    fun getToken(): TokenLocalDto
}