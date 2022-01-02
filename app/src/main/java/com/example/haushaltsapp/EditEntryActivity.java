package com.example.haushaltsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.haushaltsapp.database.Category;
import com.example.haushaltsapp.database.Intake;
import com.example.haushaltsapp.database.MySQLite;
import com.example.haushaltsapp.database.Outgo;


import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
Activity um eine Einnahme oder Ausgabe zu ändern oder zu löschen
 */
public class EditEntryActivity extends AppCompatActivity {

        private MySQLite mySQLite;

        private Spinner spinnerCyclus;
        private Spinner spinnerCategory;
        private EditText editTextName;
        private EditText editTextValue;
        private TextView editTextDate;
        private ImageView calenderView; //Kalender

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

        //Aktuelles Datum. Notwendig um Budget-Eintrag anzupassen
        private int dayCurrent, monthCurrent, yearCurrent;

        /*
    1: gewähltes Datum liegt in der Zukunft
    2: der Titel wurde nicht gesetzt
    3: es wurde kein Wert gesetzt
    */
     private int errorValue; //bei entsprechendem Fehler wird ein Dialog geöffnet, um den Benutzer darauf hinzuweisen



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_entry);

        mySQLite = new MySQLite(this); //Datenbank

        getDate(); //setze monthCurrent und yearCurrent mit dem aktuellen Datum

        //Werte aus der ermitteln, des zu ändernden Eintrags
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }

        entry = extras.getString("entry");
        id = extras.getInt("id");

        //die Werte name bis category setzen
        if (entry.equals("Intake")) {
            Intake intake = mySQLite.getIntakeById(id);
            name = intake.getName();
            value = intake.getValue();
            day = intake.getDay();
            month = intake.getMonth();
            year = intake.getYear();
            cyclus = intake.getCycle();
        } else { //Outgo Ausgabe
            Outgo outgo = mySQLite.getOutgoById(id);
            name = outgo.getName();
            value = outgo.getValue();
            day = outgo.getDay();
            month = outgo.getMonth();
            year = outgo.getYear();
            cyclus = outgo.getCycle();
            category = outgo.getCategory();
        }

        setValue(); //Werte in der Oberfläche setzen

        //Aktuelles Datum anzeigen
        //Auf deutsche Kalenderanzeige umstellen
        Locale locale = new Locale("de");
        Locale.setDefault(locale);
        Resources res = this.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.locale = locale;
        res.updateConfiguration(config, res.getDisplayMetrics());
        //Kalender
        calenderView = findViewById(R.id.calenderView);
        month = month - 1; // Januar ist 0, demnach monat um 1 minimieren
        //Setzen von Listener auf dem Kalender Symbol
        calenderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View dateView) {
                DatePickerDialog dateDialog = new DatePickerDialog(EditEntryActivity.this,R.style.datePickerStyle, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        int day = selectedDay;
                        int month = selectedMonth;
                        int year = selectedYear;

                        String dayString = String.valueOf(day);
                        String monthString = String.valueOf(month+1); //Monat beginnt bei index 0

                        if(day < 10){
                            dayString = "0"+dayString;
                        }
                        if(month < 9){ //Monat beginnt bei index 0
                            monthString = "0"+monthString;
                        }
                        editTextDate.setText(dayString+"."+monthString+"."+year);
                    }
                }, year, month, day);
                dateDialog.show();
            }
        });
    }

        // Setzt die Variablen monthCurrent und yearCurrent mit dem aktuellen datum
        private void getDate() {
            java.util.Calendar calender = java.util.Calendar.getInstance();
            SimpleDateFormat datumsformat = new SimpleDateFormat("dd.MM.yyyy");
            String dates = datumsformat.format(calender.getTime());
            dayCurrent = Integer.parseInt(dates.substring(0, 2));
            monthCurrent = Integer.parseInt(dates.substring(3, 5));
            yearCurrent = Integer.parseInt(dates.substring(6, 10));
        }


        // Werte des zu ändernden Eintrag setzen
        public void setValue() {
            //Überschrift setzten
            TextView textView = findViewById(R.id.Name);
            if (entry.equals("Outgo")) {
                textView.setText("Ausgabe ändern");
            } else {
                textView.setText("Einnahme ändern");
            }


            //Spinner Kategory
            spinnerCategory = (Spinner) findViewById(R.id.spinnerCategory);
            if (entry.equals("Outgo")) {
                ArrayList<Category> list = mySQLite.getAllCategory();
                ArrayList<String> listCategory = new ArrayList<String>();
                for (int i = 0; i < list.size(); i++) {
                    listCategory.add(list.get(i).getName_PK());
                }

                spinnerCategory = (Spinner) findViewById(R.id.spinnerCategory);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, listCategory);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategory.setAdapter(adapter);

                // String aktuellCatagory = outgo.getCategory();
                int spinnerPosition = adapter.getPosition(category);
                spinnerCategory.setSelection(spinnerPosition);
            } else {
                TextView textView1 = (TextView) findViewById(R.id.textView8);
                textView1.setVisibility(View.GONE);
                spinnerCategory.setVisibility(View.GONE);
            }


            //Spinner Zyklus
            spinnerCyclus = (Spinner) findViewById(R.id.spinnerCyclus);
            ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.spinner_cyclus, android.R.layout.simple_spinner_item);
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCyclus.setAdapter(adapter2);
            int index = adapter2.getPosition(cyclus);
            spinnerCyclus.setSelection(index);

            //name setzen
            editTextName = (EditText) findViewById(R.id.Bezeichnung);
            editTextName.setText(name);

            //value setzen
            editTextValue = (EditText) findViewById(R.id.editTextNumberDecimal);
            DecimalFormat df = new DecimalFormat("0.00");
            editTextValue.setText(df.format(value));

            //Datum setzen
            editTextDate = (TextView) findViewById(R.id.editTextDate);

            //Datum setzen
            editTextDate = (TextView) findViewById(R.id.editTextDate);
            String dateStr = "";
            if(day < 10){
                dateStr = dateStr + "0"+ String.valueOf(day)+".";
            }else{
                dateStr = dateStr + String.valueOf(day)+".";
            }

            if(month < 9){
                dateStr = dateStr + "0"+ String.valueOf(month)+".";
            }else{
                dateStr = dateStr + String.valueOf(month)+".";
            }

            dateStr = dateStr + String.valueOf(year);
            editTextDate.setText(dateStr);
        }

        //ändern
        public void onClickchange(View view) {

            boolean valide = getValues(); //erhalte die gewünschten Werte

            if (valide) {
                if (entry.equals("Intake")) {
                    Intake intake = new Intake(name, value, day, month, year, cyclus);
                    mySQLite.updateIntake(intake, id);
                } else {
                    Outgo outgo = new Outgo(name, value, day, month, year, cyclus, category);
                    mySQLite.updateOutgo(outgo, id);
                }
                if ((month < monthCurrent) || (year < yearCurrent)) {//Wenn der Eintrag in der Vergangenheit liegt muss das Budget angepasst werden
                    setBudgetEntry(month, year);
                }
                Intent switchToMainActivity= new Intent(this, MainActivity.class);
                startActivity(switchToMainActivity);
            }else{
                informUser();
            }
        }

        //Löschen
        public void onClickdeli(View view) {
            if (entry.equals("Outgo")) {
                mySQLite.deleteOutgoById(id);
            } else {
                mySQLite.deleteIntakeById(id);
            }
            if ((month < monthCurrent) || (year < yearCurrent)) {//Wenn der Eintrag in der Vergangenheit liegt muss das Budget angepasst werden
                setBudgetEntry(month, year);
            }
            Intent switchToMainActivity= new Intent(this, MainActivity.class);
            startActivity(switchToMainActivity);
        }

        private boolean getValues() {
            boolean retValue = true;

            //Datum:
            dates = editTextDate.getText().toString();
            day = Integer.parseInt(dates.substring(0, 2));
            month = Integer.parseInt(dates.substring(3, 5));
            year = Integer.parseInt(dates.substring(6, 10));

            if((month > monthCurrent && year >= yearCurrent) || (year > yearCurrent) ||(day > dayCurrent && month == monthCurrent && year == yearCurrent)){ //Eintrag liegt in der Zukunft
                errorValue = 1;
                retValue = false;
            }

            //Wert anzeigen lassen:
            EditText editTextValue = (EditText) findViewById(R.id.editTextNumberDecimal);
            Pattern p = Pattern.compile("^\\d+([\\.,]\\d{2})?$");
            Matcher m = p.matcher(editTextValue.getText().toString());
            if(m.find()){ //Eintrag ist valide
                value = Double.parseDouble(editTextValue.getText().toString().replace(",",".")); //Eingabe mit Komma abfangen
                if(value == 0.0){ //Prüfen ob ein Wert gesetzt wurde
                    errorValue = 3;
                    retValue = false;
                }
            }else{
                errorValue = 4;
                retValue = false;
            }

            //Name
            EditText editTextName = (EditText) findViewById(R.id.Bezeichnung);
            name = editTextName.getText().toString();

            //Zyklus
            cyclus = spinnerCyclus.getSelectedItem().toString();

            //Kategorie, wenn Outgo
            if (entry.equals("Outgo")) {
                category = spinnerCategory.getSelectedItem().toString();
            }

            return retValue;
        }

    /*
Abbrechen
*/
    public void onClickCancel(View view){
        Intent switchToMainActivity= new Intent(this, MainActivity.class);
        startActivity(switchToMainActivity);
    }

    //Methode öffnet ein Fenster um den Benutzer auf unterschiedliche Fehler hinzuweisen.
    private void informUser(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Hinweis");

        if(errorValue == 1){
            builder1.setMessage("Das gewählte Datum liegt in der Zukunft.");
        }else if(errorValue == 3){
            builder1.setMessage("Bitte geben Sie einen Wert an.");
        }else{ // errorValue 4
            builder1.setMessage("Ihre Eingabe bezüglich des Werts ist nicht valide.");
        }

        builder1.setCancelable(true);
        builder1.setNeutralButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();

        errorValue = 0; //danach zurücksetzen
        month--; //damit im Kalender der aktuelle Monat angezeigt wird.
    }

    /*
    Funktion geht von moonthEntry +1 bis zum akuellen Monat/Jahr iteraqtiv durch
    löscht den Eintrag mit dem Budget und berechnet den neuen Wert
    ist der Wert positiv wird dieser in Einnahmen, ansonsten in Ausgaben, hinterlegt
     */
    private void setBudgetEntry(int monthEntry,int yearEntry){
        if((monthEntry < monthCurrent) || (yearEntry < yearCurrent)){
            do{
                // Erst hochzählen da man den nächsten Monat braucht
                if(monthEntry == 12){
                    monthEntry = 1;
                    yearEntry = yearEntry +1;
                }else{
                    monthEntry = monthEntry + 1;
                }

                //Eintrag muss aus der Datenbank enfernt werden
                //Wie der Eintrag lautet
                String titel = "Übertrag vom ";
                if(monthEntry > 1){
                    titel = titel+(monthEntry-1)+"."+yearEntry;
                }else{
                    titel = titel+12+"."+(yearEntry-1);
                }

                //id des Eintrags ermitteln
                int idIntake = mySQLite.getIntakeIdbyName(titel);
                int idOutgo = mySQLite.getOutgoIdbyName(titel);
                if(idIntake > -1){
                    mySQLite.deleteIntakeById(idIntake); //Eintrag löschen
                }else if(idOutgo > -1){
                    mySQLite.deleteOutgoById(idOutgo); //Eintrag löschen
                }

                //Neuer Eintrag erstellen
                double value = 0.0;
                if (monthEntry > 1) {
                    value = mySQLite.getValueIntakesMonth(31, monthEntry - 1, yearEntry) - mySQLite.getValueOutgosMonth(31, monthEntry - 1, yearEntry);
                } else { //1
                    value = mySQLite.getValueIntakesMonth(31, 12, yearEntry - 1) - mySQLite.getValueOutgosMonth(31, 12, yearEntry - 1);
                }
                if(value >= 0) { //Einnahme
                    Intake intake = new Intake(titel, value, 1, monthEntry, yearEntry, "einmalig");
                    mySQLite.addIntake(intake);
                }else{ //Ausgabe
                    value = value * (-1);
                    Outgo outgo = new Outgo(titel, value, 1, monthEntry, yearEntry, "einmalig","Sonstiges");
                    mySQLite.addOutgo(outgo);
                }
            }while (!((monthEntry == monthCurrent) && (yearEntry == yearCurrent)));
        }
    }
}