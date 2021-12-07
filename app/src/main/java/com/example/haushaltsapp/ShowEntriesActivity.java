package com.example.haushaltsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.haushaltsapp.database.Intake;
import com.example.haushaltsapp.database.Outgo;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/*
Repräsentation von Einnahmen bzw Ausgaben eines übergebenen Array
 */
public class ShowEntriesActivity extends AppCompatActivity {

    private String entry; //Intake oder Outgo

    /*
   Wird am Anfang aufgerufen und stellt die übergebenen Daten dar
   -- !! Design wird ggf noch angepasst !!--
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_entries);

        TextView tv = (TextView) findViewById(R.id.textViewEntry);

        //Informationen auslesen
        Intent intent = getIntent();
        entry = intent.getStringExtra("entry");

        //Ausgaben ohner Einnahmen
        String text = " ";
        if(entry.equals("Intake")){
            text = "Alle Einnahmen des aktuellen Monats:";
            List<Intake> list = (List<Intake>) intent.getSerializableExtra("list");
            //Liste Sortieren
            Collections.sort(list, new Comparator<Intake>() {
                @Override
                public int compare(Intake o1, Intake o2) {
                    return o1.compareTo(o2);
                }
            });

            for(int i = 0; i < list.size(); i++){
                text = text + " '\n' "+list.get(i).toString();
            }
        }else{
            text = "Alle Ausgaben des aktuellen Monats:";
            List<Outgo> list = (List<Outgo>) intent.getSerializableExtra("list");
            //Liste Sortieren
            Collections.sort(list, new Comparator<Outgo>() {
                @Override
                public int compare(Outgo o1, Outgo o2) {
                    return o1.compareTo(o2);
                }
            });

            for(int i = 0; i < list.size(); i++){
                text = text + " '\n' "+list.get(i).toString();
            }
        }

        //Text anzeigen lassen
        tv.setText(text);
    }

    public void changeEntry(View view){
        EditText editTextId = (EditText) findViewById(R.id.textViewEditText);
        int valueId = -1;
        if(!TextUtils.isEmpty(editTextId.getText())) { //teste, ob überhaupt eine eingabe getätigt wurde
            valueId = Integer.parseInt(editTextId.getText().toString());
        }

        Intent intent = new Intent();
        intent.putExtra("entry",entry);
        intent.putExtra("id",valueId);
        setResult(RESULT_OK, intent);
        super.finish();
    }


    /*
    Sorgt dafür, dass das Menü dargestellt wird.
    Ohne Funktionalität
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.showintakes_menu, menu);
         */
        return true;
    }
}