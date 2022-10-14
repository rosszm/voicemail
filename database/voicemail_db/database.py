from dataclasses import dataclass
import psycopg
from psycopg.rows import class_row, dict_row


@dataclass
class User:
    id: int
    phone_number: int
    voicemail_number: int

@dataclass
class BlockedNumber:
    blocker_id: int
    phone_number: int


class VoicemailDatabase(object):
    """
    A client interface to send and retrieve data from the voicemail database.
    """
    def __init__(self, url: str) -> None:
        self.url = url

    async def get_user(self, voicemail_number: str) -> User | None:
        """
        Returns the user from the voicemail box number.
        """
        async with await psycopg.AsyncConnection.connect(self.url) as conn:
            async with conn.cursor(row_factory=class_row(User)) as cur:
                await cur.execute(
                    "SELECT * FROM voicemail_user WHERE voicemail_number=%s",
                    [voicemail_number])
                return await cur.fetchone()

    async def get_blocked(self, user_id: int) -> set[BlockedNumber]:
        """
        Returns a set phone numbers that are blocked for a given voicemail number.
        """
        async with await psycopg.AsyncConnection.connect(self.url) as conn:
            async with conn.cursor(row_factory=class_row(User)) as cur:
                await cur.execute(
                    """SELECT (blocker_id, phone_number)
                        FROM blocked_number
                        WHERE blocker_id=%s""",
                    [user_id])
                return set(await cur.fetchall())

    async def insert_voicemail(self, user_id: int, from_number: str, audio_url: str, transcription: str | None=None) -> int:
        """
        Inserts a new voicemail into the database.
        """
        async with await psycopg.AsyncConnection.connect(self.url) as conn:
            async with conn.cursor(row_factory=dict_row) as cur:
                await cur.execute("""
                    INSERT INTO voicemail (user_id, from_number, recording_url, transcription)
                    VALUES (%s, %s, %s, %s)
                    RETURNING id
                    """,
                    [user_id, from_number, audio_url, transcription])
                return (await cur.fetchone())["id"]


    async def update_voicemail_transcription(self, voicemail_id: int, transcription: str):
        async with await psycopg.AsyncConnection.connect(self.url) as conn:
            async with conn.cursor() as cur:
                await cur.execute("""
                    UPDATE voicemail SET transcription = %s
                    WHERE id = %s
                    """,
                    [transcription, voicemail_id])