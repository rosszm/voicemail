package dev.zacharyross.voicemail.domain.usecase.auth

import com.google.firebase.auth.FirebaseUser
import dev.zacharyross.voicemail.domain.repository.AuthRepository
import dev.zacharyross.voicemail.domain.usecase.UseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MonitorAuthUseCase @Inject constructor(
    private val authRepository: AuthRepository,
): UseCase<Unit, Flow<FirebaseUser?>>(Dispatchers.IO) {

    override suspend fun execute(parameters: Unit): Flow<FirebaseUser?> {
        return authRepository.getAuthStateFlow()
    }
}