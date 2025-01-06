package com.prac.domain.impl

import com.prac.data.repository.TokenRepository
import com.prac.data.repository.UserRepository
import com.prac.domain.ClearTokenUseCase
import javax.inject.Inject

class ClearTokenUseCaseImpl @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val userRepository: UserRepository
) : ClearTokenUseCase {
    override suspend fun invoke() {
        TODO("Not yet implemented")
    }
}