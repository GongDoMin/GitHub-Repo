package com.prac.auth

import com.prac.auth.model.TokenModel

interface AuthManager {
    fun getAccessToken(refreshAccessToken: suspend (refreshToken: String) -> TokenModel) : String
}