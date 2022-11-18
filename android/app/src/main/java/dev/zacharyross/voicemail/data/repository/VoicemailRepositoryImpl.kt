package dev.zacharyross.voicemail.data.repository


import dev.zacharyross.voicemail.data.source.local.VoicemailDatabase
import dev.zacharyross.voicemail.data.source.local.entity.VoicemailEntity
import dev.zacharyross.voicemail.domain.repository.VoicemailRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import dev.zacharyross.voicemail.domain.model.Voicemail
import kotlinx.coroutines.withContext
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject


class VoicemailRepositoryImpl @Inject constructor(
    private val localSource: VoicemailDatabase,
    //private val remoteSource: VoicemailApi
): VoicemailRepository {
    init {
        CoroutineScope(Dispatchers.IO).launch {
            localSource.voicemailDao().insertVoicemails(
                VoicemailEntity(
                    0,
                    "+13061234567",
                    "+13069943716",
                    "https://storage.googleapis.com/exoplayer-test-media-0/play.mp3",
                    "This is a test message transcription. This is a longer message transcription",
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    true
                ),
                VoicemailEntity(
                    1,
                    "+13061234567",
                    "+13068342266",
                    "https://storage.googleapis.com/exoplayer-test-media-0/play.mp3",
                    "This is a test message transcription.",
                    ZonedDateTime.now(ZoneId.of("UTC")).minusMinutes(5),
                    true
                ),
                VoicemailEntity(
                    2,
                    "+13061234567",
                    "+13068342266",
                    "https://storage.googleapis.com/exoplayer-test-media-0/play.mp3",
                    "This is a test message transcription.",
                    ZonedDateTime.now(ZoneId.of("UTC")).minusHours(1),
                    true
                ),
                VoicemailEntity(
                    3,
                    "+13061234567",
                    "+13068342266",
                    "https://storage.googleapis.com/exoplayer-test-media-0/play.mp3",
                    "This is a test message transcription.",
                    ZonedDateTime.now(ZoneId.of("UTC")).minusDays(1),
                    true
                ),
                VoicemailEntity(
                    4,
                    "+13061234567",
                    "+13068342266",
                    "https://storage.googleapis.com/exoplayer-test-media-0/play.mp3",
                    "This is a test message transcription.",
                    ZonedDateTime.now(ZoneId.of("UTC")).minusDays(4),
                    true
                ),
                VoicemailEntity(
                    5,
                    "+13061234567",
                    "+13069943716",
                    "https://storage.googleapis.com/exoplayer-test-media-0/play.mp3",
                    "This is a test message transcription.",
                    ZonedDateTime.now(ZoneId.of("UTC")).minusWeeks(1),
                    false
                ),
                VoicemailEntity(
                    6,
                    "+13061234567",
                    "+13069943716",
                    "https://storage.googleapis.com/exoplayer-test-media-0/play.mp3",
                    "This is a test message transcription.",
                    ZonedDateTime.now(ZoneId.of("UTC")).minusMonths(1),
                    false
                ),
                VoicemailEntity(
                    7,
                    "+13061234567",
                    "+11231231234",
                    "https://storage.googleapis.com/exoplayer-test-media-0/play.mp3",
                    "This is a test message transcription.",
                    ZonedDateTime.now(ZoneId.of("UTC")).minusMonths(2),
                    false
                ),
                VoicemailEntity(
                    8,
                    "+13061234567",
                    "+11231231234",
                    "https://storage.googleapis.com/exoplayer-test-media-0/play.mp3",
                    "This is a test message transcription.",
                    ZonedDateTime.now(ZoneId.of("UTC")).minusMonths(3),
                    false
                ),
                VoicemailEntity(
                    9,
                    "+13061234567",
                    "+11231231234",
                    "https://storage.googleapis.com/exoplayer-test-media-0/play.mp3",
                    "This is a test message transcription.",
                    ZonedDateTime.now(ZoneId.of("UTC")).minusYears(1),
                    false
                ),
                VoicemailEntity(
                    10,
                    "+13061234567",
                    "+11231231234",
                    "https://storage.googleapis.com/exoplayer-test-media-0/play.mp3",
                    "This is a test message transcription.",
                    ZonedDateTime.now(ZoneId.of("UTC")).minusYears(2),
                    false
                ),
                VoicemailEntity(
                    11,
                    "+13061234567",
                    "+11231231234",
                    "https://storage.googleapis.com/exoplayer-test-media-0/play.mp3",
                    "This is a test message transcription.",
                    ZonedDateTime.now(ZoneId.of("UTC")).minusYears(2),
                    false
                ),
                VoicemailEntity(
                    12,
                    "+13061234567",
                    "+11231231234",
                    "https://storage.googleapis.com/exoplayer-test-media-0/play.mp3",
                    "This is a test message transcription.",
                    ZonedDateTime.now(ZoneId.of("UTC")).minusYears(2),
                    false
                ),
                VoicemailEntity(
                    13,
                    "+13061234567",
                    "+11231231234",
                    "https://storage.googleapis.com/exoplayer-test-media-0/play.mp3",
                    "This is a test message transcription.",
                    ZonedDateTime.now(ZoneId.of("UTC")).minusYears(2),
                    false
                ),
                VoicemailEntity(
                    14,
                    "+13061234567",
                    "+11231231234",
                    "https://storage.googleapis.com/exoplayer-test-media-0/play.mp3",
                    "This is a test message transcription.",
                    ZonedDateTime.now(ZoneId.of("UTC")).minusYears(2),
                    false
                ),
                VoicemailEntity(
                    15,
                    "+13061234567",
                    "+11231231234",
                    "https://storage.googleapis.com/exoplayer-test-media-0/play.mp3",
                    "This is a test message transcription.",
                    ZonedDateTime.now(ZoneId.of("UTC")).minusYears(2),
                    false
                ),
            )
        }
    }

    override suspend fun getInbox(): Flow<List<Voicemail>> {
        return withContext(Dispatchers.IO) {
            return@withContext localSource.voicemailDao().getVoicemails()
        }
    }

    override suspend fun getVoicemail(id: Int): Voicemail {
        return withContext(Dispatchers.IO) {
            return@withContext localSource.voicemailDao().getVoicemailFromId(id)
        }
    }

    override suspend fun updateVoicemail(voicemail: Voicemail) {
        withContext(Dispatchers.IO) {
            localSource.voicemailDao().updateVoicemail(VoicemailEntity(voicemail))
        }
    }

    override suspend fun deleteVoicemail(voicemail: Voicemail) {
        withContext(Dispatchers.IO) {
            localSource.voicemailDao().deleteVoicemail(VoicemailEntity(voicemail))
        }
    }

    override suspend fun clearInbox() {
        withContext(Dispatchers.IO) {
            localSource.voicemailDao().deleteAllVoicemails()
        }
    }
}