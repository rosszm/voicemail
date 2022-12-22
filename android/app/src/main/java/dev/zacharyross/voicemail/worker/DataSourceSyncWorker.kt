package dev.zacharyross.voicemail.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.hilt.work.HiltWorker
import androidx.work.Data
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.zacharyross.voicemail.domain.usecase.SyncDataUseCase


@HiltWorker
class DataSourceSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted parameters: WorkerParameters,
    private val syncDataUseCase: SyncDataUseCase
): CoroutineWorker(appContext, parameters) {

    override suspend fun doWork(): Result {
        return syncDataUseCase.invoke(Unit)
            .fold(
                { Result.success() },
                {
                    Result.failure(Data.Builder()
                        .putString("message", it.message)
                        .build())
                }
            )
    }
}