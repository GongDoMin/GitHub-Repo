package com.prac.data.exception

sealed class AuthException : Exception() {
    class NetworkError : AuthException()

    class AuthorizationError : AuthException()

    class UnKnownError : AuthException()
}