package dev.zacharyross.voicemail.domain.usecase.auth

import dev.zacharyross.voicemail.domain.model.SignInRequest
import dev.zacharyross.voicemail.domain.repository.AuthRepository
import dev.zacharyross.voicemail.domain.repository.ClientRepository
import dev.zacharyross.voicemail.domain.usecase.UseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class SignInWithCodeUseCase @Inject constructor(
    private val clientRepository: ClientRepository,
    private val authRepository: AuthRepository,
): UseCase<SignInRequest, Unit>(Dispatchers.IO) {

    override suspend fun execute(parameters: SignInRequest) {
        val request = parameters.copy(onSuccess = {
            CoroutineScope(Dispatchers.IO).launch { clientRepository.setClientUser(it.uid) }
            parameters.onSuccess(it)
        })
        authRepository.signInWithCode(request)
    }
}