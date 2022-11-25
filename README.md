# Voicemail

A voicemail system designed to provide standard and visual voicemail services for a low-cost.

The main motivation for this system was the limited availability of Google Voice in certain areas
and a lack of carrier voicemail functionality.

## Repository Contents
### [Android App](android)
This is the primary client for the voicemail services. It provides standard and visual voicemail
features, and push notifications for new voicemail messages.

### [Voicemail Service](voicemail-service)
The primary service for handling voicemail messages. This service acts as the controller for
incoming calls, ensuring they are handled as necessary.

### [Protobuf](protobuf)
The protocol buffer definitions for the voicemail project.


## System Architecture

This system follows a straightforward PUB/SUB pattern where the voicemail service acts as a
publisher and publishes message to its corresponding queue. The android app implements a background
service that consumes messages from this queue, updating its internal database, and notifying users
via notifications.
