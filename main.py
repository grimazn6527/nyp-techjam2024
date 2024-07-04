import whisper_transcriber
import sentiment
import random
import database

database.Connect()
database.CreateTables()

#whisper_transcriber.transcribeAudio("numb.mp3")
sentiment_scores = sentiment.get_sentiment("audiotranscribed.txt")
# Generate an 8-digit number starting with 8 or 9
clientNumber = str(random.randint(80000000, 99999999))

clientID = database.AddClient(clientNumber, "<NAME>", sentiment_scores['compound'])
database.AddCallRecord(sentiment_scores['compound'], clientID)
database.UpdateOverallSentiment(clientID)

database.Disconnect()