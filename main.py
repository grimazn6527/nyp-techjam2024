import whisper_transcriber
import sentiment
import database
import JSONHandler
import extractname

database.Connect()
database.CreateTables()

client_number = JSONHandler.ReadJSONFile("callData.json", "clientNumber")
file_path = JSONHandler.ReadJSONFile("callData.json", "audio_path")

whisper_transcriber.transcribeAudio(file_path)
sentiment_scores = sentiment.get_sentiment("audiotranscribed.txt")
client_name = extractname.GetName("audiotranscribed.txt")

client_id = database.AddClient(client_number, client_name, sentiment_scores['compound'])
database.AddCallRecord(sentiment_scores['compound'], client_id)
database.UpdateOverallSentiment(client_id)

send_data = {
    "audio_path": "",
    "name": client_name,
    "clientNumber": ""
}
JSONHandler.WriteJSONFile("callData.json", send_data)

database.Disconnect()