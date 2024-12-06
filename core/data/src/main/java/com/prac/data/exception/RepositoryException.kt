package com.prac.data.exception

sealed class RepositoryException :Exception() {
    class NotFoundRepository : RepositoryException()
}