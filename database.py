import datetime
import sqlite3

conn = None
cursor = None

def Connect():
    global conn
    conn = sqlite3.connect('DATABASE.db')
    conn.execute('PRAGMA foreign_keys = ON')
    global cursor
    cursor = conn.cursor()

def CreateTables():
    cursor.execute("CREATE TABLE IF NOT EXISTS Clients (ClientID INTEGER PRIMARY KEY AUTOINCREMENT, ClientName TEXT, PhoneNumber INTEGER, OverallSentiment REAL)")
    # Create call record table if it doesnt exist
    cursor.execute("CREATE TABLE IF NOT EXISTS CallRecord (Sentiment REAL, CallDate DATE, ClientID INTEGER, FOREIGN KEY (ClientID) REFERENCES Clients(ClientID))")

def AddClient(phoneNumber, clientName, sentimentScore):
    # Check if the phone number already exists in the Clients table
    cursor.execute("SELECT COUNT(*) FROM Clients WHERE PhoneNumber = ?", (phoneNumber,))
    clientID = cursor.fetchone()[0]
    # Insert data into the database only if the phone number does not exist
    if clientID == 0:
        cursor.execute("INSERT INTO Clients (ClientName, PhoneNumber, OverallSentiment) VALUES (?, ?, ?)", (clientName, phoneNumber, sentimentScore))
        # Get the last inserted clientID
        cursor.execute("SELECT last_insert_rowid()")
        clientID = cursor.fetchone()[0]

    return clientID

def AddCallRecord(sentimentScore, clientID):
    today = datetime.date.today()
    cursor.execute("INSERT INTO CallRecord (Sentiment, CallDate, ClientID) VALUES (?, ?, ?)", (sentimentScore, today, clientID))

def UpdateOverallSentiment(clientID):
    cursor.execute("SELECT AVG(Sentiment) FROM CallRecord WHERE ClientID = ?", (clientID,))
    overallSentiment = cursor.fetchone()[0]
    cursor.execute("UPDATE Clients SET OverallSentiment = ? WHERE ClientID = ?", (overallSentiment, clientID))
    
def Disconnect():
    conn.commit()
    conn.close()