package com.example.haushaltsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    MySQLiteKategorie dbKat = new MySQLiteKategorie(this, null, null, 0);
    MySQLiteEinnahme dbEin = new MySQLiteEinnahme(this, null, null, 0);
    SQLiteAusgabe dbAus = new SQLiteAusgabe(this, null, null, 0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Anlegen der Kategorien
        Kategorie kat1 = new Kategorie("Kategorie 1", 90, "black", "white");
        dbKat.addKategorie(kat1);


    }
}