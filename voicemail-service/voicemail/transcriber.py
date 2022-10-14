"""
transcriber.py

This module contains code for connecting to the transcriber gRPC service.
"""

import grpc
import transcribe_pb2_grpc
import transcribe_pb2


class Transcriber(object):
    """
    A client for the gRPC-based transcriber service.
    """
    def __init__(self, grpc_url) -> None:
        self.grpc_url =grpc_url

    async def transcribe(self, url: str) -> str:
        """
        Transcribes the audio at a given URL and returns the text of the transcribed audio.
        """
        with grpc.insecure_channel(self.grpc_url) as channel:
            stub = transcribe_pb2_grpc.TranscriberStub(channel)
            response = stub.transcribe(transcribe_pb2.TranscriptionRequest(url=url))
            return response.text
