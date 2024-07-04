package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "DATABASE.db";
    private static final int DATABASE_VERSION = 1;


    public DatabaseHelper(@Nullable Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createClientsTable = "CREATE TABLE Clients (" +
                "ClientID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ClientName TEXT NOT NULL, " +
                "PhoneNumber INTEGER NOT NULL, " +
                "OverallSentiment REAL NOT NULL);";
        db.execSQL(createClientsTable);

        String createCallRecordTable = "CREATE TABLE CallRecord (" +
                "Sentiment REAL NOT NULL, " +
                "CallDate DATE NOT NULL, " +
                "ClientID INTEGER NOT NULL, " +
                "FOREIGN KEY (ClientID) REFERENCES Clients(ClientID));";
        db.execSQL(createCallRecordTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Clients");
        db.execSQL("DROP TABLE IF EXISTS CallRecord");
        onCreate(db);
    }

    public void addClient(String clientName, int phoneNumber, double overallSentiment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("ClientName", clientName);
        cv.put("PhoneNumber", phoneNumber);
        cv.put("OverallSentiment", overallSentiment);

        long result = db.insert("Clients", null, cv);

        if(result == -1){
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "Added Successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    public void addCallRecord(double sentiment, String callDate ,String clientName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("Sentiment", sentiment);
        cv.put("CallDate", callDate);
        int clientID = getClientID(clientName);
        cv.put("ClientID", clientID);

        long result = db.insert("Contacts", null, cv);

        if(result == -1){
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "Added Successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    public int getClientID(String clientName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT ClientID FROM Clients WHERE ClientName = ?";
        Cursor cursor = db.rawQuery(query, new String[]{clientName});

        int clientID = -1;
        if (cursor.moveToFirst()) {
            clientID = cursor.getInt(0);
        }
        cursor.close();
        return clientID;
    }

    public Cursor getAllClients(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM Clients", null);
    }

    public Cursor getCallRecordByClientID(int clientID){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM CallRecord WHERE ClientID = ?", new String[]{String.valueOf(clientID)});
    }
}
