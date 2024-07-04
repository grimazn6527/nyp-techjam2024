package com.example.myapplication;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private Button btnTest;
    private Button btnTest2;
    private DatabaseHelper myDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        myDB = new DatabaseHelper(MainActivity.this);

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

   /* private void storeDataInArrays(){
        Cursor cursor = myDB.readAllData();
        if(cursor.getCount() == 0){

        }else{
            while (cursor.moveToNext()){

            }

        }
    }*/
}