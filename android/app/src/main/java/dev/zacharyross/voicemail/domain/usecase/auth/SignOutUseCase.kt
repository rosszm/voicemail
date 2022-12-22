package dev.zacharyross.voicemail.domain.usecase.auth

import dev.zacharyross.voicemail.domain.repository.AuthRepository
import dev.zacharyross.voicemail.domain.repository.ClientRepository
import dev.zacharyross.voicemail.domain.usecase.UseCase
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val clientRepository: ClientRepository,
    private val authRepository: AuthRepository,
): UseCase<Unit, Unit>(Dispatchers.IO) {

    override suspend fun execute(parameters: Unit) {
        clientRepository.removeClientUser()
        authRepository.signOut()
    }
}