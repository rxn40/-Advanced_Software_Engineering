package com.example.haushaltsapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


//nur zum Testen
public class Charttest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.charttest);
        TextView Outname = findViewById(R.id.testname);

        String name = "";

        Bundle extras = getIntent().getExtras();
        if (extras!= null)
        {
            name = extras.getString("name");
        }
        Outname.setText(name);
    }
}


