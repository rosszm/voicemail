from dataclasses import dataclass
from signalwire.relay.consumer import Consumer
from signalwire.relay.calling import Call
from signalwire.relay.calling.constants import MediaType, CallRecordState
import logging, asyncio

from voicemail_db.database import VoicemailDatabase
from transcriber import Transcriber


MAX_MSG_DURATION = 60

logging.basicConfig(
    level=logging.INFO,
    datefmt="%Y-%m-%d %H:%M:%S",
    format="[{asctime}] {name} {threadName} {levelname}: {message}",
    style='{')


class Voicemail(Consumer):
    """
    The voicemail consumer. This service handles calls received with a voicemail prompt,
    and records messages.
    """
    def __init__(self, project: str, token: str, db: str, transcriber: str | None=None):
        super().__init__()
        # Required by RELAY
        self.project = project
        self.token = token
        self.contexts = ["voicemail"]

        self.language = "en-CA"
        self.database = VoicemailDatabase(db)
        self.transcriber = Transcriber(transcriber)

    async def on_incoming_call(self, call: Call):
        """
        Handles incoming calls the voicemail consumer.
        """
        logging.info("Call Received: {} to {}".format(call.from_number, call.to_number))
        recipient = await self.database.get_user(call.to_number)
        blocked = await self.database.get_blocked(recipient.id)
        if call.from_number in blocked:
            await call.hangup()
            return

        result = await call.answer()
        if result.successful:
            await call.play([
                {"type": MediaType.SILENCE, "duration": 1 },
                {
                    "type": MediaType.TTS,
                    "language": self.language,
                    "text": "The person you are calling is currently unavailable; please leave a message after the tone. " +
                            "To finish recording, you may hang up, or press pound. "
                },
            ])
            recording = await call.record_async(beep=True, direction="speak", record_format="wav")
            for _ in range(MAX_MSG_DURATION):
                if recording.completed:
                    if recording.result.successful and recording.state == CallRecordState.FINISHED:
                        transcription = self.transcriber.transcribe(recording.result.url)

                        id = await self.database.insert_voicemail(
                            recipient.id,
                            call.from_number,
                            recording.result.url)

                        await self.database.update_voicemail_transcription(id, await transcription)

                        logging.info("Call Recorded:{} to {}".format(call.from_number, call.to_number))
                    break
                await asyncio.sleep(1)
            await recording.stop()
        await call.hangup()
        logging.info("Call Ended: {} to {}".format(call.from_number, call.to_number))


def run():
    import os

    project = os.environ.get("SIGNALWIRE_PROJECT")
    token = os.environ.get("SIGNALWIRE_API_TOKEN")
    transcriber_url = os.environ.get("TRANSCRIBER_GRPC_URL")
    db_url = os.environ.get("POSTGRES_URL")

    voicemail_consumer = Voicemail(project, token, db=db_url, transcriber=transcriber_url)
    voicemail_consumer.run()


if __name__ =="__main__":
    run()