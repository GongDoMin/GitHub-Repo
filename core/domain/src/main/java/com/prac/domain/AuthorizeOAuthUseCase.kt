package com.prac.domain

interface AuthorizeOAuthUseCase {
    suspend operator fun invoke(code: String) : Result<Unit>
}