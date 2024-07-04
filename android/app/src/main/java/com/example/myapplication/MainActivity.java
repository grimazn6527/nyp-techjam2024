package com.example.myapplication;

import android.content.res.AssetManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.security.PrivateKey;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper myDB;
    private static final int PICK_AUDIO_REQUEST = 1;

    //UI-RELATED
    private LinearLayout clientsContainer;
    private Button btnUploadAudioFile;
    private Button btnCompile;
    private EditText etPhoneNumber;
    private TextView tvTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        //Initialize database
        myDB = new DatabaseHelper(MainActivity.this);
        clientsContainer = (LinearLayout) findViewById(R.id.clients_container);
        loadClients();
        //Set up python
        if(!Python.isStarted()){
            Python.start(new AndroidPlatform(this));
        }

        tvTest = (TextView) findViewById(R.id.textView);
        etPhoneNumber = (EditText) findViewById(R.id.editTextPhone);
        btnCompile = (Button) findViewById(R.id.btnCompile);
        btnCompile.setOnClickListener(v -> runPythonScript());
        btnUploadAudioFile = (Button) findViewById(R.id.btnUploadFile);
        btnUploadAudioFile.setOnClickListener(v -> openAudioPicker());

    }
    
    private void loadClients(){
        Cursor cursor = myDB.getAllClients();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No clients found", Toast.LENGTH_SHORT).show();
            return;
        }

        while(cursor.moveToNext()){
            int clientID = cursor.getInt(cursor.getColumnIndexOrThrow("ClientID"));
            String clientName = cursor.getString(cursor.getColumnIndexOrThrow("ClientName"));
            Button clientButton = new Button(this);
            clientButton.setText(clientName);
            clientButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openContact(clientID);
                }
            });
            clientsContainer.addView(clientButton);
        }
        cursor.close();
    }

    private void openContact(int clientID) {
        //Intent intent = new Intent(MainActivity.this, ContactActivity.class);
        //intent.putExtra("ClientID", clientID);
        //startActivity(intent);
    }

    private void openAudioPicker(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("audio/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_AUDIO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_AUDIO_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri audioUri = data.getData();
            if (audioUri != null) {
                String filePath = getFileNameFromUri(audioUri);
                if (filePath != null) {
                    //sendFilePathToServer(filePath);
                    writeToJsonFile(filePath, etPhoneNumber.getText().toString());
                } else {
                    Toast.makeText(this, "Failed to get file path", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public String getFileNameFromUri(Uri uri) {
        String displayName = null;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            if (columnIndex != -1) {
                displayName = cursor.getString(columnIndex);
            }
            cursor.close();
        }
        return displayName;
    }

    private void writeToJsonFile(String audioPath, String clientNumber){
        try{
            JSONObject send_data = new JSONObject();
            send_data.put("audio_path", audioPath);
            send_data.put("name", "<NAME>");  // Replace <NAME> with the actual name if needed
            send_data.put("clientNumber", clientNumber);

            File file = new File(getFilesDir(), "callData.json");
            FileWriter writer = new FileWriter(file);
            writer.write(send_data.toString());
            writer.close();

            // Display a toast or log to indicate success
            Toast.makeText(this, "Data written to JSON file", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void runPythonScript(){
        //Create python instance
        Python py = Python.getInstance();

        //Run python file and function main()
        PyObject pyobj = py.getModule("main");
        PyObject testobj = pyobj.callAttr("main");
        String result = testobj.toString();
        tvTest.setText(result);
    }

   /* private void storeDataInArrays(){
        Cursor cursor = myDB.readAllData();
        if(cursor.getCount() == 0){

        }else{
            while (cursor.moveToNext()){

            }

        }
    }*/
}