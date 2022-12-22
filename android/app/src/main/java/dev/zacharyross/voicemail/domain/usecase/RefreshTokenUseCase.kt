package dev.zacharyross.voicemail.domain.usecase

import dev.zacharyross.voicemail.domain.repository.AuthRepository
import dev.zacharyross.voicemail.domain.repository.ClientRepository
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class RefreshTokenUseCase @Inject constructor(
    private val clientRepository: ClientRepository,
    private val authRepository: AuthRepository,
): UseCase<Unit, Unit>(Dispatchers.IO) {

    override suspend fun execute(parameters: Unit) {
        clientRepository.refreshToken()
        val user = authRepository.getUser()
        if (user != null) {
            clientRepository.setClientUser(user.uid)
        }
    }
}