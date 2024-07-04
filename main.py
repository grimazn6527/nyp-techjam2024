import whisper_transcriber
import sentiment
import sqlite3
import random
import datetime

# Create a connection to the database
conn = sqlite3.connect('DATABASE.db')
conn.execute('PRAGMA foreign_keys = ON')
c = conn.cursor()

# Create clients table if it doesnt exist
c.execute("CREATE TABLE IF NOT EXISTS Clients (ClientID INTEGER PRIMARY KEY AUTOINCREMENT, ClientName TEXT, PhoneNumber INTEGER, OverallSentiment REAL)")
# Create call record table if it doesnt exist
c.execute("CREATE TABLE IF NOT EXISTS CallRecord (Sentiment REAL, CallDate DATE, ClientID INTEGER, FOREIGN KEY (ClientID) REFERENCES Clients(ClientID))")

whisper_transcriber.transcribeAudio("numb.mp3")
sentiment_scores = sentiment.get_sentiment("audiotranscribed.txt")
# Generate an 8-digit number starting with 8 or 9
clientNumber = str(random.randint(80000000, 99999999))

# Check if the phone number already exists in the Clients table
c.execute("SELECT COUNT(*) FROM Clients WHERE PhoneNumber = ?", (clientNumber,))
clientID = c.fetchone()[0]
# Insert data into the database only if the phone number does not exist
if clientID == 0:
    c.execute("INSERT INTO Clients (ClientName, PhoneNumber, OverallSentiment) VALUES ('<NAME>', ?, ?)", (clientNumber, sentiment_scores['compound']))
    # Get the last inserted clientID
    c.execute("SELECT last_insert_rowid()")
    clientID = c.fetchone()[0]

# Get today's date
today = datetime.date.today()
# Insert data into the CallRecord table
c.execute("INSERT INTO CallRecord (Sentiment, CallDate, ClientID) VALUES (?, ?, ?)", (sentiment_scores['compound'], today, clientID))

conn.commit()
conn.close()