package com.prac.data.exception

sealed class CommonException : Exception(){
    class NetworkError : CommonException()

    class AuthorizationError : CommonException()

    class UnKnownError : CommonException()
}