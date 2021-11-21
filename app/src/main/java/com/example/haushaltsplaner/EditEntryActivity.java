package com.example.haushaltsplaner;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class EditEntryActivity extends AppCompatActivity {

        private Spinner spinnerCyclus;
        private EditText editTextName;
        private EditText editTextValue;
        private EditText editTextDate;

        private int id;
        private String bezeichnung;
        private String name;
        private double value;
        private String dates;
        private int day;
        private int month;
        private int year;
        private String cyclus;



        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_edit_entry);
            //Darstellung der Oberfläche

            Bundle extras = getIntent().getExtras();
            if(extras == null){
                return;
            }


            id = extras.getInt("id");
            name = extras.getString("name");
            value = extras.getDouble("value");
            day = extras.getInt("day");
            month = extras.getInt("month");
            year= extras.getInt("year");
            cyclus = extras.getString("cyclus");


            bezeichnung = extras.getString("Bezeichnung");
            TextView textView = findViewById(R.id.Name);
            if(bezeichnung.equals("Outgo")){
                textView.setText("Ausgabe");
            }else{
                textView.setText("Einnahme");
            }

            //Spinner "befüllen"
            spinnerCyclus = (Spinner) findViewById(R.id.spinnerCyclus);
            ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,  R.array.spinner_cyclus, android.R.layout.simple_spinner_item);
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCyclus.setAdapter(adapter2);
            int index = adapter2.getPosition(cyclus);
            spinnerCyclus.setSelection(index);

            editTextName = (EditText) findViewById(R.id.Bezeichnung);
            editTextName.setText(name);

            editTextValue = (EditText) findViewById(R.id.editTextNumberDecimal);
            editTextValue.setText(String.valueOf(value));

            editTextDate = (EditText) findViewById(R.id.editTextDate);
            String dateStr = String.valueOf(day)+"."+String.valueOf(month)+"."+String.valueOf(year);
            editTextDate.setText(dateStr);


        }

        public void onClickchange(View view){

            Intent data = new Intent();
            data.putExtra("selection","change");
            data.putExtra("Bezeichnung",bezeichnung);
            data.putExtra("id",id);
            getValues();


            data.putExtra("name",name);
            data.putExtra("value",value);
            data.putExtra("day",day);
            data.putExtra("month",month);
            data.putExtra("year",year);
            data.putExtra("cyclus",cyclus);

            setResult(RESULT_OK, data);
            super.finish();

        }

        public void onClickdeli(View view){
            Intent intent = new Intent();
            intent.putExtra("entry","intake");
            intent.putExtra("Bezeichnung",bezeichnung);
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
            dates = editTextDate.getText().toString();
            day = Integer.parseInt(dates.substring(0,2));
            month = Integer.parseInt(dates.substring(3,5));
            year = Integer.parseInt(dates.substring(6,10));
            //dates = editTextDate.getText().toString().substring(6,10);

            //Zyklus
            cyclus = spinnerCyclus.getSelectedItem().toString();

        }
    }