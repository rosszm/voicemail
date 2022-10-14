from vosk_recasepunc.recasepunc import CasePuncPredictor
from vosk import Model, KaldiRecognizer
import wave, json


class Transcriber(object):
    """
    Transcribers are objects that handle all processes related to the transcription of audio into
    text format.
    """
    def __init__(self, use_casepunc: bool=False) -> None:
        """
        Initialize an new transcriber. This loads ML models and may take some time.
        """
        self._model = Model("models/vosk-model-en-us-0.22")
        self._use_casepunc = use_casepunc
        if use_casepunc:
            self._cp_predictor = CasePuncPredictor(
                "transcriber/vosk_recasepunc/checkpoint",
                lang="en",
                device="cpu")

    def transcribe_file(self, audio_file) -> str:
        """
        Transcribes an audio file in .wav format to the audio in text.
        """
        wf = wave.open(audio_file, "rb")
        recognizer = KaldiRecognizer(self._model, wf.getframerate())
        while True:
            data = wf.readframes(4000)
            if len(data) == 0:
                break
            if recognizer.AcceptWaveform(data):
                res = json.loads(recognizer.Result())
        res = json.loads(recognizer.FinalResult())["text"]
        if self._use_casepunc:
            res = self.predict_casepunc(res)
        return res

    def predict_casepunc(self, string: str) -> str:
        """
        Predicts the case and punctuation of an input string and returns a new string with the
        predictions in it.
        """
        tokens = list(enumerate(self._cp_predictor.tokenize(string)))
        res = ""
        for token, case_label, punc_label in self._cp_predictor.predict(tokens, lambda x: x[1]):
            prediction = self._cp_predictor.map_punc_label(
                self._cp_predictor.map_case_label(token[1], case_label), punc_label)
            if token[1][0] != '#':
                res = res + ' ' + prediction
            else:
                res = res + prediction
        return res.strip()


if __name__ =="__main__":
    from urllib.request import urlopen
    from vosk_recasepunc.recasepunc import WordpieceTokenizer, Config

    t = Transcriber(use_casepunc=True)
    res = t.transcribe_file(urlopen("https://www2.cs.uic.edu/~i101/SoundFiles/gettysburg.wav"))
    print(res)
