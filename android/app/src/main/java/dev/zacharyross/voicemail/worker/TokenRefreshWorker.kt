package dev.zacharyross.voicemail.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.zacharyross.voicemail.domain.repository.ClientRepository
import dev.zacharyross.voicemail.domain.usecase.RefreshTokenUseCase

@HiltWorker
class TokenRefreshWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted parameters: WorkerParameters,
    private val refreshTokenUseCase: RefreshTokenUseCase,
): CoroutineWorker(appContext, parameters) {

    override suspend fun doWork(): Result {
        return refreshTokenUseCase.invoke(Unit).fold(
            { Result.success() },
            { Result.retry() }
        )
    }
}