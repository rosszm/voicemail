/* This transcation creates all the required tables for the voicemail database. */
BEGIN;

/*
  User Table.
  For associating users with their voicemail number.
*/
CREATE TABLE IF NOT EXISTS voicemail_user (
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    phone_number TEXT NOT NULL UNIQUE,
    voicemail_number TEXT NOT NULL UNIQUE
);

/*
  Blocked Number Table.
  Stores a nubmer blocked by a user.
*/
CREATE TABLE IF NOT EXISTS blocked_number (
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    blocker_id INTEGER NOT NULL,
    phone_number TEXT NOT NULL,

    FOREIGN KEY(blocker_id) REFERENCES voicemail_user(id)
);

/*
  Voicemail Table.
  Stores the contents of a voicemail message.
*/
CREATE TABLE IF NOT EXISTS voicemail (
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id INTEGER NOT NULL,
    from_number TEXT NOT NULL,
    recording_url TEXT NOT NULL UNIQUE,
    transcription TEXT,

    FOREIGN KEY(user_id) REFERENCES voicemail_user(id)
);

COMMIT;