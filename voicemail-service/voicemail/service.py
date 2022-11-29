import logging, asyncio
from datetime import datetime, timezone
from signalwire.relay import event
from signalwire.relay.consumer import Consumer
from signalwire.relay.calling import Call
from signalwire.relay.calling.call import RecordAction
from signalwire.relay.calling.constants import CallState
import nanoid
import firebase_admin
from firebase_admin import credentials, firestore, messaging


MAX_MSG_DURATION = 180

logging.basicConfig(
    level=logging.INFO,
    datefmt="%Y-%m-%d %H:%M:%S",
    format="[{asctime}] {name} {threadName} {levelname}: {message}",
    style='{')


class VoicemailService(Consumer):
    """
    The voicemail consumer. This service handles calls received to a voicemail number.
    """
    def __init__(self, **kwargs):
        super().__init__()
        # Required by RELAY
        self.project = kwargs["project"]
        self.token = kwargs["token"]
        self.contexts = ["voicemail"]
        # Other variables
        self.cms_url = kwargs["cms_url"]

        from voicemail.asr import ASRModel
        self.asr = ASRModel()
        logging.info(f"{self}: ASR Model initialized.")

        self.firebase = firebase_admin.initialize_app(credentials.Certificate(kwargs["firebase_cert"]))
        self.firestore = firestore.client(self.firebase)
        logging.info(f"{self}: Firebase initialized.")

    async def on_incoming_call(self, call: Call):
        """
        Handles incoming calls the voicemail consumer.
        """
        logging.info(f"[{call.id}] Receiving incoming call")

        users_ref = self.firestore.collection("users")
        user = (users_ref.where("voicemail_number", "==", call.to_number).get()[0:1] or [None])[0]

        result = await call.answer()
        if result.successful:
            logging.info(f"[{call.id}] Answered call")
            await call.play_silence(1)
            await call.play_audio(f"{self.cms_url}/leave_message.mp3", volume=16)

            recording = await call.record_async(beep=True, direction="speak", record_format="mp3")

            elapsed = 0
            while (elapsed < MAX_MSG_DURATION and not recording.completed):
                elapsed += 1
                await asyncio.sleep(1)

            await recording.stop()

            if recording.result.successful:
                logging.info(f"[{call.id}] Recorded voice message")
                if elapsed >= MAX_MSG_DURATION:
                    await call.play_audio(f"{self.cms_url}/time_limit.mp3", volume=16)
                await call.play_audio(f"{self.cms_url}/message_recorded.mp3", volume=16)
                await call.hangup()
                logging.info(f"[{call.id}] Ended call")
                self._on_successful_recording(user, call, recording)
            else:
                await call.hangup()
                logging.info(f"[{call.id}] Ended call")
        else:
            logging.error(f"[{call.id}] Could not answer incoming call")

    def _on_successful_recording(self, user, call: Call, recording: RecordAction):
        """
        Handles when a voicemail message is successfully recorded.
        """
        transcription = self.asr.transcribe(recording.url)

        msg_id = nanoid.generate()
        msg_data = {
            "from_number": call.from_number,
            "audio_url": recording.url,
            "transcription": transcription,
            "timestamp": datetime.now(timezone.utc)
        }
        try:
            messages_ref = self.firestore.collection("users").document(user.id).collection("messages")
            messages_ref.document(msg_id).set(msg_data)
        except Exception as err:
            logging.error(f"[{call.id}] Could not save message: {err}")

        msg = messaging.Message(
            data={ "message_id": msg_id },
            token=user.get("fcm_token"))

        try:
            messaging.send(msg, app=self.firebase)
        except Exception as err:
            logging.error(f"[{call.id}] Could not send message: {err}")


def main():
    import argparse

    parser = argparse.ArgumentParser(
        description="A voicemail service that records calls and forwards them to a message queue.")

    parser.add_argument("project", type=str, help="A SignalWire project ID")
    parser.add_argument("token", type=str, help="A SignalWire API token")
    parser.add_argument("cms_url",
        type=str,
        help="The content management system URL. This points to the location of the audio prompts, "
            "and must be accessible by SignalWire. i.e., not localhost")
    parser.add_argument("--firebase_cert",
        type=str,
        default="./service_cert.json",
        help="The Firebase service account key path (default: './service_cert.json')")

    args = parser.parse_args()

    voicemail_consumer = VoicemailService(**vars(args))
    voicemail_consumer.run()


if __name__ =="__main__":
    main()