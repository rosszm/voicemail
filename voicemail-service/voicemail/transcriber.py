"""
transcriber.py

This module contains code for connecting to the transcriber gRPC service.
"""

import grpc
import transcribe_pb2_grpc
import transcribe_pb2
import os

TRANSCRIBER_URL = os.environ.get("TRANSCRIBER_GRPC_URL")

assert TRANSCRIBER_URL != None, """Error: transcriber.py module requires the "TRANSCRIBER_GRPC_URL" environment variable to be set."""


def transcribe(url: str) -> str:
    """
    Transcribes the audio at a given URL and returns the text of the transcribed audio.
    """
    with grpc.insecure_channel(TRANSCRIBER_URL) as channel:
        stub = transcribe_pb2_grpc.TranscriberStub(channel)
        response = stub.transcribe(transcribe_pb2.TranscriptionRequest(url=url))
        return response.text
