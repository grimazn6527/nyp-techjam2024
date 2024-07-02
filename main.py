import whisper_transcriber
import sentiment

whisper_transcriber.transcribeAudio("numb.mp3")
sentiment.get_sentiment("audiotranscribed.txt")