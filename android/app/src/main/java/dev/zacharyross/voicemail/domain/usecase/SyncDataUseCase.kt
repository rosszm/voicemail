package dev.zacharyross.voicemail.domain.usecase

import dev.zacharyross.voicemail.domain.repository.AuthRepository
import dev.zacharyross.voicemail.domain.repository.VoicemailRepository
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class SyncDataUseCase @Inject constructor(
    private val voicemailRepository: VoicemailRepository,
    private val authRepository: AuthRepository,
): UseCase<Unit, Unit>(Dispatchers.IO) {

    override suspend fun execute(parameters: Unit) {
        val user = authRepository.getUser()
        if (user != null) {
            voicemailRepository.syncData(user.uid)
        }
    }

}