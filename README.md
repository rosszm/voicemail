# Voicemail

A voicemail system designed to provide standard and visual voicemail services for a low-cost.

The main motivation for this system was the limited availability of Google Voice in certain areas
and a lack of carrier voicemail functionality.


## [Android App](android)
This is the primary client for the voicemail services. It provides standard and visual voicemail
features, and push notifications for new voicemail messages.

## [API Service](api-service)
This service acts as the interface between client applications and the voicemail system. It supports
requests through its REST API.

## [Voicemail Service](voicemail-service)
The primary service for handling voicemail messages. This service acts as the controller for
incoming calls, ensuring they are handled as necessary.

## [Transcriber Service](transcriber-service)
A gRPC based service to transcribe audio into text. This service is called by the voicemail system
to transcribe messages.
