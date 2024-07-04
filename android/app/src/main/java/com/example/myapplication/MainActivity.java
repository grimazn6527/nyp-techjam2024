package com.example.myapplication;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.security.PrivateKey;

public class MainActivity extends AppCompatActivity {

    private Button btnTest;
    private Button btnTest2;
    private DatabaseHelper myDB;


    //UI-RELATED
    private LinearLayout clientsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        myDB = new DatabaseHelper(MainActivity.this);
        clientsContainer = (LinearLayout) findViewById(R.id.clients_container);
        loadClients();


        btnTest = (Button) findViewById(R.id.button);
        btnTest.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                myDB.addClient("Wallace Kwek");
            }

        });

        btnTest2 = (Button) findViewById(R.id.button2);
        btnTest2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                myDB.addContact("Dumbass", 995, "Wallace Kwek");
            }

        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
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

   /* private void storeDataInArrays(){
        Cursor cursor = myDB.readAllData();
        if(cursor.getCount() == 0){

        }else{
            while (cursor.moveToNext()){

            }

        }
    }*/
}