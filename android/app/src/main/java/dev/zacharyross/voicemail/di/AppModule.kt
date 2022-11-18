package dev.zacharyross.voicemail.di

import android.content.ComponentName
import android.content.Context
import androidx.concurrent.futures.await
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.room.Room
import com.google.common.util.concurrent.MoreExecutors
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.zacharyross.voicemail.data.repository.VoicemailRepositoryImpl
import dev.zacharyross.voicemail.data.service.PlaybackService
import dev.zacharyross.voicemail.data.service.PlaybackServiceConnection
import dev.zacharyross.voicemail.data.source.local.VoicemailDatabase
import dev.zacharyross.voicemail.domain.repository.VoicemailRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideVoicemailDatabase(@ApplicationContext appContext: Context): VoicemailDatabase {
        return Room.databaseBuilder(
            appContext,
            VoicemailDatabase::class.java,
            "Voicemail"
        ).build()
    }

    @Provides
    @Singleton
    fun provideVoicemailRepository(database: VoicemailDatabase): VoicemailRepository {
        return VoicemailRepositoryImpl(database)
    }

    @Provides
    @Singleton
    fun providePlaybackService(): PlaybackService {
        return PlaybackService()
    }

    @Provides
    @Singleton
    fun providePlaybackServiceConnection(@ApplicationContext appContext: Context): PlaybackServiceConnection {
        return PlaybackServiceConnection(appContext)
    }
}