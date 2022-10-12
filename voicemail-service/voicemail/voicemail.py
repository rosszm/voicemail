
from dataclasses import dataclass
from signalwire.relay.consumer import Consumer
from signalwire.relay.calling import Call
from signalwire.relay.calling.constants import MediaType, CallRecordState
import logging, asyncio

import transcriber
#from voicemail.database.database import VoicemailDB

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
    def __init__(self, project_id: str, token: str):
        super().__init__()
        # Required by RELAY
        self.project = project_id
        self.token = token
        self.contexts = ["voicemail"]

        self.language = "en-CA"
        #self.database = VoicemailDB(db_url)

    async def on_incoming_call(self, call: Call):
        """
        Handles incoming calls the voicemail consumer.
        """
        logging.info("Call Received: {} to {}".format(call.from_number, call.to_number))
        #user = await self.database.get_user(call.to_number)
        #blockedList = await self.database.get_blocked_numbers
        # TODO: if call is not in blocked list
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
            await asyncio.sleep(0.5)
            recording = await call.record_async(
                beep=True, direction="speak", record_format="wav", initial_timeout=3)

            for _ in range(MAX_MSG_DURATION):
                if recording.completed:
                    if recording.result.successful and recording.state == CallRecordState.FINISHED:
                        logging.info("Call Recorded:{} to {}".format(call.from_number, call.to_number))
                        transcription = transcriber.transcribe(recording.result.url)
                        print(transcription)
                        # TODO: add message to database
                    break
                await asyncio.sleep(1)
            await recording.stop()
        await call.hangup()
        logging.info("Call Ended: {} to {}".format(call.from_number, call.to_number))


if __name__ =="__main__":
    import os

    project = os.environ.get("SIGNALWIRE_PROJECT")
    token = os.environ.get("SIGNALWIRE_API_TOKEN")

    voicemail_consumer = Voicemail(project, token)
    voicemail_consumer.run()
