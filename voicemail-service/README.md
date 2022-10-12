
# Voicemail Service

A simple voicemail service using Signalwire's RELAY SDK.


## Usage
Using poetry
```sh
# gRPC codegen
poetry run python -m grpc_tools.protoc -I../protos --python_out=. --grpc_python_out=. ../protos/voicemail.proto
# run voicemail service
poetry run python voicemail/voicemail.py
```
Using python and pip

```sh
# install dependencies
pip install -r requirements.txt
# gRPC codegen
python -m grpc_tools.protoc -I../protos --python_out=. --grpc_python_out=. ../protos/voicemail.proto
# run voicemail service
python voicemail/voicemail.py
```
