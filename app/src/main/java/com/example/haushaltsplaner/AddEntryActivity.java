package com.example.haushaltsplaner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.Calendar;

    public class AddEntryActivity extends AppCompatActivity {

        private Spinner spinnerCyclus;
        private EditText editTextDate;

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
            setContentView(R.layout.activity_add_entry);

            //Spinner "befüllen"
            spinnerCyclus = (Spinner) findViewById(R.id.spinnerCyclus);
            ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,  R.array.spinner_cyclus, android.R.layout.simple_spinner_item);
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCyclus.setAdapter(adapter2);



            //Aktuelles Datum anzeigen
            editTextDate = (EditText) findViewById(R.id.editTextDate);
            java.util.Calendar kalender = Calendar.getInstance();
            SimpleDateFormat datumsformat = new SimpleDateFormat("dd.MM.yyyy");
            editTextDate.setText(datumsformat.format(kalender.getTime()));
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.addentry_menu, menu);
            return true;
        }

        public void onClickIntake(View view){
            finishIntake();
        }

        public void onClickOutgo(View view){
            finishOutgo();
        }

        public void finishOutgo() {
            getValues();

            Intent i = new Intent();

            i.putExtra("entry","Outgo");
            i.putExtra("name",name);
            i.putExtra("value",value);
            i.putExtra("day",day);
            i.putExtra("month",month);
            i.putExtra("year",year);
            i.putExtra("cyclus",cyclus);
            setResult(RESULT_OK, i);
            super.finish();

        }

        public void finishIntake(){
            getValues();

            Intent i = new Intent();

            i.putExtra("entry","Intake");
            i.putExtra("name",name);
            i.putExtra("value",value);
            i.putExtra("day",day);
            i.putExtra("month",month);
            i.putExtra("year",year);
            i.putExtra("cyclus",cyclus);

            setResult(RESULT_OK, i);
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



            dates = editTextDate.getText().toString().substring(6,10);

            //Zyklus
            cyclus = spinnerCyclus.getSelectedItem().toString();

        }
    }