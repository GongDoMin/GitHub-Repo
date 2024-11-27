package com.prac.exception

sealed class RepositoryException :Exception() {
    class NotFoundRepository : RepositoryException()
}