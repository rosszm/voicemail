
import os, logging
import nemo.collections.asr as nemo_asr
import nemo.collections.nlp as nemo_nlp
from urllib.request import urlretrieve


logging.getLogger('nemo_logger').setLevel(logging.ERROR)


class ASRModel:
    def __init__(self) -> None:
        self.asr_model = nemo_asr.models.EncDecCTCModelBPE.from_pretrained("stt_en_conformer_ctc_large")
        self.punctuation = nemo_nlp.models.PunctuationCapitalizationModel.from_pretrained(model_name='punctuation_en_distilbert')

    def transcribe(self, url: str) -> str:
        path, _ = urlretrieve(url)

        transcriptions = self.asr_model.transcribe([path])
        res = self.punctuation.add_punctuation_capitalization(queries=transcriptions)

        os.remove(path)
        return res[0]
