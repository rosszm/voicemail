package dev.zacharyross.voicemail.domain.usecase.auth

import dev.zacharyross.voicemail.domain.model.VerifyPhoneRequest
import dev.zacharyross.voicemail.domain.repository.AuthRepository
import dev.zacharyross.voicemail.domain.usecase.UseCase
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class VerifyPhoneNumberUseCase @Inject constructor(
    private val authRepository: AuthRepository,
): UseCase<VerifyPhoneRequest, Unit>(Dispatchers.IO) {

    override suspend fun execute(parameters: VerifyPhoneRequest) {
        authRepository.verifyPhoneNumber(parameters)
    }
}