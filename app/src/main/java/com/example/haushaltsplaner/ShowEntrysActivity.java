package com.example.haushaltsplaner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

/*
Repräsentation von Einnahmen bzw Ausgaben einer übergebnen Array
 */
public class ShowEntrysActivity extends AppCompatActivity {

    private String entry; //Intake oder Outgo

    /*
   Wird am Anfang aufgerufen und stellt die übergebenen Daten dar
   -- !! Design wird ggf noch angepasst !!--
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_entrys);

        TextView tv = (TextView) findViewById(R.id.textViewEntry);

        //Informationen auslesen
        Intent intent = getIntent();
        entry = intent.getStringExtra("entry");

        //Ausgaben ohner Einnahmen
        String text = " ";
        if(entry.equals("Intake")){
            text = "Alle Einnahmen des aktuellen Monats:";
            List<Intake> list = (List<Intake>) intent.getSerializableExtra("list");
            for(int i = 0; i < list.size(); i++){
                text = text + " '\n' "+list.get(i).toString();
            }
        }else{
            text = "Alle Ausgaben des aktuellen Monats:";
            List<Outgo> list = (List<Outgo>) intent.getSerializableExtra("list");
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