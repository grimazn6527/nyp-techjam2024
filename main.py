import whisper_transcriber
import sentiment
import database
import JSONHandler

database.Connect()
database.CreateTables()

clientNumber = JSONHandler.ReadJSONFile("callData.json", "clientNumber")
file_path = JSONHandler.ReadJSONFile("callData.json", "audio_path")

whisper_transcriber.transcribeAudio(file_path)
sentiment_scores = sentiment.get_sentiment("audiotranscribed.txt")

clientID = database.AddClient(clientNumber, "<NAME>", sentiment_scores['compound'])
database.AddCallRecord(sentiment_scores['compound'], clientID)
database.UpdateOverallSentiment(clientID)

send_data = {
    "audio_path": "",
    "name": "<NAME>",
    "clientNumber": clientNumber
}
JSONHandler.WriteJSONFile("callData.json", send_data)

database.Disconnect()