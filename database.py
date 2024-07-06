from datetime import datetime, timedelta
import sqlite3
import os.path

conn = None
cursor = None

def Connect():
    global conn

    package_dir = os.path.abspath(os.path.dirname(__file__))
    db_dir = os.path.join(package_dir, 'DATABASE.db')
    conn = sqlite3.connect(db_dir)

    #conn = sqlite3.connect('DATABASE.db')
    conn.execute('PRAGMA foreign_keys = ON')
    global cursor
    cursor = conn.cursor()

def CreateTables():
    cursor.execute("CREATE TABLE IF NOT EXISTS Clients (ClientID INTEGER PRIMARY KEY AUTOINCREMENT, ClientName TEXT, PhoneNumber INTEGER, OverallSentiment REAL)")
    # Create call record table if it doesnt exist
    cursor.execute("CREATE TABLE IF NOT EXISTS CallRecord (Sentiment REAL, CallDate DATE, ClientID INTEGER, SummarizedTranscription TEXT, FOREIGN KEY (ClientID) REFERENCES Clients(ClientID))")

def AddClient(phoneNumber, clientName, sentimentScore):
    # Check if the phone number already exists in the Clients table
    cursor.execute("SELECT ClientID FROM Clients WHERE PhoneNumber = ?", (phoneNumber,))
    result = cursor.fetchone()
    if result:
        # If the phone number exists, return the existing clientID
        clientID = result[0]
    else:
        # If the phone number does not exist, insert a new record
        cursor.execute("INSERT INTO Clients (ClientName, PhoneNumber, OverallSentiment) VALUES (?, ?, ?)", (clientName, phoneNumber, sentimentScore))
        # Get the last inserted clientID
        cursor.execute("SELECT last_insert_rowid()")
        clientID = cursor.fetchone()[0]
    
    message = "Client ID: " + str(clientID)
    print(message)
    return clientID

def AddCallRecord(sentimentScore, clientID, summarizedTranscription):
    today = datetime.today().date()
    cursor.execute("""
        INSERT INTO CallRecord (Sentiment, CallDate, ClientID, SummarizedTranscription) 
        VALUES (?, ?, ?, ?)
    """, (sentimentScore, today, clientID, summarizedTranscription))

def UpdateOverallSentiment(clientID):
    cursor.execute("SELECT AVG(Sentiment) FROM CallRecord WHERE ClientID = ?", (clientID,))
    overallSentiment = cursor.fetchone()[0]
    cursor.execute("UPDATE Clients SET OverallSentiment = ? WHERE ClientID = ?", (overallSentiment, clientID))

def ClearAllData():
    """
    This function clears all data from the database by dropping all tables.
    It then recreates the necessary tables to ensure the database schema is intact.
    """
    Connect()  # Ensure the database connection is established

    # Drop all tables
    cursor.execute("DROP TABLE IF EXISTS CallRecord")
    cursor.execute("DROP TABLE IF EXISTS Clients")

    # Recreate tables
    CreateTables()

    # Commit changes and close connection
    conn.commit()
    Disconnect()

    print("All data has been cleared and the database schema has been reset.")   

def Disconnect():
    conn.commit()
    conn.close()

def get_all_customers():
    Connect()
    cursor.execute("SELECT ClientID, ClientName, PhoneNumber, OverallSentiment FROM Clients")
    customers = [{"ClientID": row[0], "ClientName": row[1], "PhoneNumber": row[2], "OverallSentiment": row[3]} for row in cursor.fetchall()]
    Disconnect()
    return customers

def get_call_records(clientID):
    Connect()
    cursor.execute("SELECT CallDate, Sentiment, SummarizedTranscription FROM CallRecord WHERE ClientID = ?", (clientID,))
    call_records = [{"CallDate": row[0], "Sentiment": row[1], "SummarizedTranscription": row[2]} for row in cursor.fetchall()]
    Disconnect()
    return call_records

def get_client_info(clientID):
    Connect()
    cursor.execute("SELECT ClientID, ClientName, PhoneNumber, OverallSentiment FROM Clients WHERE ClientID = ?", (clientID,))
    client_info = cursor.fetchone()
    Disconnect()
    if client_info:
        return {"ClientID": client_info[0], "ClientName": client_info[1], "PhoneNumber": client_info[2], "OverallSentiment": client_info[3]}
    else:
        return None