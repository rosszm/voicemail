package dev.zacharyross.voicemail.di

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.zacharyross.voicemail.R
import dev.zacharyross.voicemail.data.repository.AuthRepositoryImpl
import dev.zacharyross.voicemail.data.repository.ClientRepositoryImpl
import dev.zacharyross.voicemail.data.repository.VoicemailRepositoryImpl
import dev.zacharyross.voicemail.data.repository.NotificationRepositoryImpl
import dev.zacharyross.voicemail.service.PlaybackServiceConnection
import dev.zacharyross.voicemail.data.source.local.VoicemailDatabase
import dev.zacharyross.voicemail.domain.repository.AuthRepository
import dev.zacharyross.voicemail.domain.repository.ClientRepository
import dev.zacharyross.voicemail.domain.repository.NotificationRepository
import dev.zacharyross.voicemail.domain.repository.VoicemailRepository
import kotlinx.coroutines.Dispatchers
import java.util.concurrent.ConcurrentHashMap
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
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideRemoteDatabase(): FirebaseFirestore {
        Firebase.firestore.firestoreSettings = firestoreSettings { isPersistenceEnabled = false }
        return Firebase.firestore
    }

    @Provides
    @Singleton
    fun provideAuth() = Firebase.auth

    @Provides
    @Singleton
    fun provideMessaging() = Firebase.messaging

    @Provides
    @Singleton
    fun provideNotificationChannel(@ApplicationContext appContext: Context): NotificationChannel {
        val channel = NotificationChannel(
            appContext.getString(R.string.notification_channel_id),
            appContext.getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT)

        val notificationManager: NotificationManager =
            appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        return channel
    }

    @Provides
    @Singleton
    fun provideNotificationMap(): ConcurrentHashMap<String, Int> = ConcurrentHashMap()

    @Provides
    @Singleton
    fun provideNotificationRepository(map: ConcurrentHashMap<String, Int>): NotificationRepository {
        return NotificationRepositoryImpl(map)
    }

    @Provides
    @Singleton
    fun provideVoicemailRepository(
        @ApplicationContext appContext: Context,
        database: VoicemailDatabase,
        remoteDatastore: FirebaseFirestore
    ): VoicemailRepository {
        return VoicemailRepositoryImpl(
            appContext,
            database,
            remoteDatastore,
            defaultDispatcher = Dispatchers.IO
        )
    }

    @Provides
    @Singleton
    fun provideAuthRepository(auth: FirebaseAuth): AuthRepository = AuthRepositoryImpl(auth)

    @Provides
    @Singleton
    fun provideClientRepository(remoteDatabase: FirebaseFirestore, messaging: FirebaseMessaging): ClientRepository {
        return ClientRepositoryImpl(remoteDatabase, messaging)
    }

    @Provides
    @Singleton
    fun providePlaybackServiceConnection(@ApplicationContext appContext: Context): PlaybackServiceConnection {
        return PlaybackServiceConnection(appContext)
    }
}