
===================
Voicemail Service
===================

A simple voicemail service using Signalwire's RELAY SDK.

This service handles calls to voicemail, transcribes recorded messages, saves the data to the
database and sends a notification via Firebase Cloud Messaging.


----------------------
Installation & Usage
----------------------

The use of a virtual environment is highly recommended.

.. code-block:: sh

    sudo apt-get update
    sudo apt-get install -y libsndfile1 ffmpeg

    # Install and build the project
    pip install Cython
    pip install ./

    # Run the voicemail service
    voicemail-service $SIGNALWIRE_PROJECT $SIGNALWIRE_API_TOKEN $CMS_URL --firebase_cert ./firebase_cert.json


`voicemail-service` requires that the following files are accessible at `$CMS_URL`:

* `/leave_message.mp3`
* `/message_recorded.mp3`
* `/time_limit.mp3`

These mp3 files are played to provide feedback to the caller, and prompt for actions.