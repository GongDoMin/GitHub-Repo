package com.prac.data.exception

sealed class RepositoryException :Exception() {
    class NetworkError : RepositoryException()

    class AuthorizationError : RepositoryException()

    class NotFoundRepository : RepositoryException()

    class UnKnownError : RepositoryException()
}