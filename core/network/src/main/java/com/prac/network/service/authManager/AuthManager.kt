package com.prac.network.service.authManager

interface AuthManager {
    fun checkTokenIsExpired()

    fun getAccessToken() : String
}