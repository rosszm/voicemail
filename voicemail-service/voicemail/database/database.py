
from dataclasses import dataclass
from typing import Coroutine
import psycopg
from psycopg import sql
from psycopg.rows import class_row


@dataclass
class User:
    id: int
    phone_number: int
    voicemail_number: int

@dataclass
class BlockedNumber:
    blocker_id: int
    phone_number: int


class VoicemailDB(object):
    def __init__(self, url: str) -> None:
        self.url = url
        self.create_tables()

    def create_tables(self):
        """
        Creates the tables for the voicemail database.
        """
        table_queries = [
            sql.SQL("""
                CREATE TABLE IF NOT EXISTS user (
                    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                    phone_number TEXT NOT NULL UNIQUE,
                    voicemail_number TEXT NOT NULL UNIQUE,
                )"""
            ),
            sql.SQL("""
                CREATE TABLE IF NOT EXISTS blocked_number (
                    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                    blocker_id INTEGER NOT NULL,
                    phone_number TEXT NOT NULL,

                    FOREIGN KEY(user_id) REFERENCES user(id),
                )"""
            ),
            sql.SQL("""
                CREATE TABLE IF NOT EXISTS voicemail (
                    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                    user_id INTEGER NOT NULL,
                    from_number TEXT NOT NULL,
                    recording_url TEXT NOT NULL UNIQUE,
                    transcription TEXT,

                    FOREIGN KEY(user_id) REFERENCES user(id),
                )"""
            ),
        ]
        with psycopg.connect(self.url) as conn:
            with conn.cursor() as cur:
                for query in table_queries:
                    cur.execute(query)

    async def get_user(self, voicemail_number: str) -> User | None:
        """
        Returns the user from the voicemail box number.
        """
        async with psycopg.AsyncConnection.connect(self.url) as conn:
            async with conn.cursor(row_factory=class_row(User)) as cur:
                return await cur.execute(
                    "SELECT * FROM user WHERE voicemail_number=%s",
                    (voicemail_number)).fetchone()[0]

    async def insert_voicemail(self, user: int, from_number: str, recording_url: str):
        """
        Inserts a new voicemail into the database.
        """
        async with psycopg.AsyncConnection.connect(self.url) as conn:
            async with conn.cursor() as cur:
                await cur.execute("""
                    INSERT INTO voicemail (user_id, from_number, recording_url)
                    VALUES (%i, %s, %s);
                    """,
                    (user, from_number, recording_url))


