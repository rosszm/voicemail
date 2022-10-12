from vosk_recasepunc.recasepunc import WordpieceTokenizer, Config
from concurrent.futures import ThreadPoolExecutor
from urllib.request import urlopen
import grpc
import logging

import transcribe_pb2_grpc
import transcribe_pb2

from transcription import Transcriber


logging.basicConfig(
    level=logging.INFO,
    datefmt="%Y-%m-%d %H:%M:%S",
    format="[{asctime}] {name} {threadName} {levelname}: {message}",
    style='{')


class TranscriberServicer(transcribe_pb2_grpc.TranscriberServicer):
    """
    The gRPC transcriber servicer.
    """
    def __init__(self) -> None:
        logging.info("Initializing transcriber servicer...")
        super().__init__()
        self.transcriber = Transcriber(use_casepunc=True)
        logging.info("Transcriber servicer initialized.")

    def transcribe(self, request, context):
        recording_id = request.url.split('/')[-1]
        logging.info("Starting transcription of {}".format(recording_id))
        text = self.transcriber.transcribe_file(urlopen(request.url))
        logging.info("Finished transcription of {}".format(recording_id))
        return transcribe_pb2.TranscriptionResult(text=text)


if __name__ == "__main__":
    server = grpc.server(ThreadPoolExecutor())
    transcribe_pb2_grpc.add_TranscriberServicer_to_server(TranscriberServicer(), server)
    server_address = "[::]:50051"
    server.add_insecure_port(server_address)
    logging.info("Starting gRPC service on {}".format(server_address))
    server.start()
    server.wait_for_termination()
    logging.info("Service Terminated")