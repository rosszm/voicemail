# Transcriber Service

This is a python service to transcribe voicemail messages.

Currently supports transcription of audio files via gRPC.

## Build & Usage
Using poetry
```sh
# gRPC codegen
poetry run python -m grpc_tools.protoc -I../protos --python_out=. --grpc_python_out=. ../protos/voicemail.proto
# run transcriber service
poetry run python src/service.py
```
Using python and pip

```sh
# install dependencies
pip install -r requirements.txt
# gRPC codegen
python -m grpc_tools.protoc -I../protos --python_out=. --grpc_python_out=. ../protos/voicemail.proto
# run transcriber service
python src/service.py
```