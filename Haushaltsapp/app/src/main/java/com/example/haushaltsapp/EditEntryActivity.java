package com.example.haushaltsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

/*
Activity um eine Einnahme oder Ausgabe zu ändern oder zu löschen
 */
public class EditEntryActivity extends AppCompatActivity {

    private Spinner spinnerCyclus;
    private Spinner spinnerCategory;
    private EditText editTextName;
    private EditText editTextValue;
    private EditText editTextDate;

    private String entry;
    private int id;
    private String name;
    private double value;
    private String dates;
    private int day;
    private int month;
    private int year;
    private String cyclus;
    private String category = " ";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_entry);
        //Darstellung der Oberfläche

        Bundle extras = getIntent().getExtras();
        if(extras == null){
            return;
        }

        entry = extras.getString("entry");
        id = extras.getInt("id");
        if(entry.equals("Intake")){
            Intake intake = (Intake) extras.getSerializable("object");
            //  id = intake.getId();
            name = intake.getName();
            value = intake.getValue();
            day = intake.getDay();
            month = intake.getMonth();
            year = intake.getYear();
            cyclus = intake.getCycle();
        }else{ //Ausgabe
            Outgo outgo = (Outgo) extras.getSerializable("object");
            name = outgo.getName();
            value = outgo.getValue();
            day = outgo.getDay();
            month = outgo.getMonth();
            year = outgo.getYear();
            cyclus = outgo.getCycle();

            //Spinner Kategorie

            ArrayList<Category> list = (ArrayList<Category>) extras.getSerializable("list");
            ArrayList<String> listCategory = new ArrayList<String>();
            for(int i = 0; i < list.size(); i++){
                listCategory.add(list.get(i).getName_PK());
            }

            spinnerCategory = (Spinner) findViewById(R.id.spinnerCategory);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, listCategory);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCategory.setAdapter(adapter);

            String aktuellCatagory = outgo.getCategory();
            int spinnerPosition = adapter.getPosition(aktuellCatagory);
            spinnerCategory.setSelection(spinnerPosition);
        }
        setValue();
    }

    public void setValue(){
        //Überschrift setzten
        TextView textView = findViewById(R.id.Name);
        if(entry.equals("Outgo")){
            textView.setText("Ausgabe");
        }else{
            textView.setText("Einnahme");
        }

        //Spinner Zyklus
        spinnerCyclus = (Spinner) findViewById(R.id.spinnerCyclus);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,  R.array.spinner_cyclus, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCyclus.setAdapter(adapter2);
        int index = adapter2.getPosition(cyclus);
        spinnerCyclus.setSelection(index);

        //name setzen
        editTextName = (EditText) findViewById(R.id.Bezeichnung);
        editTextName.setText(name);

        //value setzen
        editTextValue = (EditText) findViewById(R.id.editTextNumberDecimal);
        editTextValue.setText(String.valueOf(value));

        //Datum setzen
        editTextDate = (EditText) findViewById(R.id.editTextDate);
        String dateStr = String.valueOf(day)+"."+String.valueOf(month)+"."+String.valueOf(year);
        editTextDate.setText(dateStr);
    }

    public void onClickchange(View view){

        getValues(); //erhalte die gewünschten Werte

        Intent intent = new Intent();
        intent.putExtra("selection","update");
        intent.putExtra("entry",entry);
        intent.putExtra("id",id);

        if(entry.equals("Intake")){
            Intake intake = new Intake(name, value, day, month, year, cyclus);
            intent.putExtra("object", (Serializable) intake);
        }else{
          Outgo outgo = new Outgo(name, value, day, month, year, cyclus, category);
          intent.putExtra("object", (Serializable) outgo);
        }

        setResult(RESULT_OK, intent);
        super.finish();
    }

    public void onClickdeli(View view){
        Intent intent = new Intent();
        intent.putExtra("entry",entry); //bezeichnung ändern
        intent.putExtra("selection","clear");
        intent.putExtra("id",id);
        setResult(RESULT_OK, intent);
        super.finish();
    }

    private void getValues(){

        //Name
        EditText editTextName = (EditText) findViewById(R.id.Bezeichnung);
        name = editTextName.getText().toString();

        //Wert anzeigen lassen:
        EditText editTextValue = (EditText) findViewById(R.id.editTextNumberDecimal);
        String valueString = editTextValue.getText().toString().replace(',', '.');
        value = Double.parseDouble(valueString);

        //Datum:
         /*
        dates = editTextDate.getText().toString();
        day = Integer.parseInt(dates.substring(0,2));
        month = Integer.parseInt(dates.substring(3,5));
        year = Integer.parseInt(dates.substring(6,10));
        */

        //Zyklus
        cyclus = spinnerCyclus.getSelectedItem().toString();

        //Kategorie, wenn Outgo
        if(entry.equals("Outgo")){
            category = spinnerCategory.getSelectedItem().toString();
        }
    }
}