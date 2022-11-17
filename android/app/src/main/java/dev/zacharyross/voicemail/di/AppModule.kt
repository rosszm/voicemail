package dev.zacharyross.voicemail.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.zacharyross.voicemail.data.repository.VoicemailRepositoryImpl
import dev.zacharyross.voicemail.data.service.PlaybackService
import dev.zacharyross.voicemail.data.source.local.VoicemailDatabase
import dev.zacharyross.voicemail.domain.repository.VoicemailRepository
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
}