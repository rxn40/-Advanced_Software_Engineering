package com.example.haushaltsplaner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/*
Activity, um Einnahmen und Ausgaben hinzuzufügen
-- !! Um Kalenderbutton erweitern !! --
-- !! Erweiterung von Kategorien fehlt noch !! --
 */
    public class AddEntryActivity extends AppCompatActivity {

        private Spinner spinnerCyclus; //Zyklus
        private EditText editTextDate; //Datum

        //Werte der Einnahme oder Ausgabe
        private String name;
        private double value;
        private String dates;
        private int day;
        private int month;
        private int year;
        private String cyclus;
        private String choice = "";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_add_entry);

            //Spinner um den Zyklus anzugeben
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


        /*
        Eingabe soll eine Einnahme sein
         */
        public void onClickIntake(View view){
            choice = "Intake";
            finish();
        }

         /*
         Ausgabe soll eine Ausgabe sein
         */
        public void onClickOutgo(View view){
            choice = "Outgo";
            finish();
        }

        @Override
        public void finish(){
            getValues(); //Eingaben einlesen
          //  Intent i = getTheIntent(); //Eingaben "intent" geben
            Intent i = new Intent();

            if(choice.equals("Intake")){
                Intake intake = new Intake(name, value, day, month, year, cyclus);
                i.putExtra("object", intake);
            }else{
                Outgo outgo = new Outgo(name, value, day, month, year, cyclus);
                i.putExtra("object", outgo);
            }
            i.putExtra("entry",choice);

            setResult(RESULT_OK, i);
            super.finish();
        }

        /*
        Funktion um die eingegebenen Werte zu ermitteln
         */
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

            //Zyklus
            cyclus = spinnerCyclus.getSelectedItem().toString();
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.addentry_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.itemStartseite:
                Intent switchToMain = new Intent(this, MainActivity.class);
                startActivity(switchToMain);
                return true;

            case R.id.itemBudgetLimit:
                Intent switchToBudgetLimit = new Intent(this, BudgetLimitActivity.class);
                startActivity(switchToBudgetLimit);
                return true;

            case R.id.itemDiagrammansicht:
                Intent switchToDiagramView = new Intent(this, DiagramViewActivity.class);
                startActivity(switchToDiagramView);
                return true;

            case R.id.itemTabelle:
                Intent switchToChart = new Intent(this, ChartViewActivity.class);
                startActivity(switchToChart);
                return true;

            case R.id.itemKalender:
                Intent switchToCalendar = new Intent(this, CalendarEventActivity.class);
                startActivity(switchToCalendar);
                return true;

            case R.id.itemTodoListe:
                Intent switchToToDoList = new Intent(this, ToDoListActivity.class);
                startActivity(switchToToDoList);
                return true;

            default:
                return super.onOptionsItemSelected(item);
            }
        }
    }