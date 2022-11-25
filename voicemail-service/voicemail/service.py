import logging, asyncio, uuid
from datetime import datetime, timezone

from signalwire.relay.consumer import Consumer
from signalwire.relay.calling import Call
from signalwire.relay.calling.constants import MediaType, CallRecordState
from kombu import Connection, Queue, Exchange

import voicemail.voicemail_pb2 as proto


MAX_MSG_DURATION = 120

logging.basicConfig(
    level=logging.INFO,
    datefmt="%Y-%m-%d %H:%M:%S",
    format="[{asctime}] {name} {threadName} {levelname}: {message}",
    style='{')


class VoicemailConsumer(Consumer):
    """
    The voicemail consumer. This service handles calls received to a voicemail number.
    """
    def __init__(self, **kwargs):
        super().__init__()
        from voicemail.asr import ASRModel

        # Required by RELAY
        self.project = kwargs["project"]
        self.token = kwargs["token"]
        self.contexts = ["voicemail"]

        self.language = "en-CA"
        self.asr = ASRModel()
        self.amqp_host = kwargs["amqp_host"]

    async def on_incoming_call(self, call: Call):
        """
        Handles incoming calls the voicemail consumer.
        """
        logging.info("Call Received: {} to {}".format(call.from_number, call.to_number))

        voicemail_queue = Queue(call.to_number, Exchange('voicemail'), routing_key=call.to_number)

        result = await call.answer()
        if result.successful:
            await call.play([
                {"type": MediaType.SILENCE, "duration": 1 },
                {
                    "type": MediaType.TTS,
                    "language": self.language,
                    "text": "The person you are calling is currently unavailable; please leave a message after the tone. " +
                            "To finish recording, you may hang up, or press pound."
                },
            ])
            recording = await call.record_async(beep=True, direction="speak", record_format="mp3")
            for _ in range(MAX_MSG_DURATION):
                if recording.completed:
                    await recording.stop()
                    if recording.result.successful and recording.state == CallRecordState.FINISHED:
                        logging.info("Call Recorded:{} to {}".format(call.from_number, call.to_number))

                        transcription = self.asr.transcribe("https://www2.cs.uic.edu/~i101/SoundFiles/gettysburg.wav")

                        msg = proto.Voicemail()
                        msg.to_number = call.to_number
                        msg.from_number = call.from_number
                        msg.audio_url = recording.url
                        msg.transcription = transcription
                        msg.timestamp.FromDatetime(datetime.now(timezone.utc))

                        with Connection(self.amqp_host) as connection:
                            producer = connection.Producer(connection)
                            producer.publish(msg.SerializeToString(),
                                exchange=voicemail_queue.exchange,
                                routing_key=call.to_number,
                                declare=[voicemail_queue],
                                retry=True)
                    break
                await asyncio.sleep(1)

            if not recording.completed:
                await recording.stop()
                await call.play_tts(
                    language=self.language,
                    text="Time limit reached. Your message has been recorded, please call again later.")

        await call.hangup()
        logging.info("Call Ended: {} to {}".format(call.from_number, call.to_number))


def main():
    import argparse

    parser = argparse.ArgumentParser(
        description="A voicemail service that records calls and forwards them to a message queue.")

    parser.add_argument("project", type=str, help="A SignalWire project ID")
    parser.add_argument("token", type=str, help="A SignalWire API token")
    parser.add_argument("--amqp_host",
        type=str,
        default="localhost",
        help="The connection string of an AMQP 0-9-1 server (default: 'localhost')")

    args = parser.parse_args()

    voicemail_consumer = VoicemailConsumer(**vars(args))
    voicemail_consumer.run()


if __name__ =="__main__":
    main()