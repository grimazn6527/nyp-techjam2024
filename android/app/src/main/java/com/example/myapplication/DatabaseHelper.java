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
                "ClientName TEXT NOT NULL);";
        db.execSQL(createClientsTable);

        String createContactsTable = "CREATE TABLE Contacts (" +
                "ContactID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ClientID INTEGER NOT NULL, " +
                "ContactName TEXT NOT NULL, " +
                "PhoneNumber INTEGER NOT NULL, " +
                "FOREIGN KEY (ClientID) REFERENCES Clients(ClientID));";
        db.execSQL(createContactsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Clients");
        db.execSQL("DROP TABLE IF EXISTS Contacts");
        onCreate(db);
    }

    public void addClient(String clientName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("ClientName", clientName);
        long result = db.insert("Clients", null, cv);

        if(result == -1){
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "Added Successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    public void addContact(String contactName, int phoneNumber ,String clientName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("ContactName", contactName);
        cv.put("PhoneNumber", phoneNumber);

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

    /*public Cursor readAllData(){
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public void updateData(String row_id, String title, String author, String pages){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_AUTHOR, author);
        cv.put(COLUMN_PAGES, pages);

        long result = db.update(TABLE_NAME, cv, "_id=?", new String[]{row_id});
        if(result == -1){
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "Updated Successfully!", Toast.LENGTH_SHORT).show();
        }

    }

    public void deleteOneRow(String row_id){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME, "_id=?", new String[]{row_id});
        if(result == -1){
            Toast.makeText(context, "Failed to Delete.", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Successfully Deleted.", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
    }*/
}
