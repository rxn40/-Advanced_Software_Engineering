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

import com.example.haushaltsapp.Database.Category;
import com.example.haushaltsapp.Database.Intake;
import com.example.haushaltsapp.Database.MySQLite;
import com.example.haushaltsapp.Database.Outgo;
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

    //Fuer das Layout
    private Spinner spinnerCycle;
    private Spinner spinnerCategory;
    private EditText editTextName;
    private EditText editTextValue;
    private TextView editTextDate;
    private ImageView calenderView;

    //Parameter der Einname oder Ausgabe
    private String entry;
    private int id;
    private String name;
    private double value;
    private String date;
    private int day;
    private int month;
    private int year;
    private String cycle;
    private String category = " ";

    //Aktuelles Datum. Notwendig um Budget-Eintrag anzupassen
    private int dayCurrent, monthCurrent, yearCurrent;

    /*
    1: gewähltes Datum liegt in der Zukunft
    2: der Titel wurde nicht gesetzt
    3: es wurde kein Wert gesetzt
    4: Wert ist nicht sinnvoll
    */
    private int errorValue; //bei entsprechendem Fehler wird ein Dialog geöffnet, um den Benutzer darauf hinzuweisen


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_entry);

        mySQLite = new MySQLite(this); //Datenbank

        getDate(); //Setzt monthCurrent und yearCurrent mit dem aktuellen Datum

        //Werte ermitteln, des zu ändernden Eintrags
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }

        //globale Variablen der Einname oder Ausgabe setzen
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
            cycle = intake.getCycle();
        } else { //Outgo Ausgabe
            Outgo outgo = mySQLite.getOutgoById(id);
            name = outgo.getName();
            value = outgo.getValue();
            day = outgo.getDay();
            month = outgo.getMonth();
            year = outgo.getYear();
            cycle = outgo.getCycle();
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
        month = month - 1; // Januar ist 0, demnach Monat um 1 verringern
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

    /*
    Methode setzt die Variablen dayCurrent, monthCurrent und yearCurrent mit dem aktuellen Datum
     */
    private void getDate() {
        Calendar calender =Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String dates = dateFormat.format(calender.getTime());
        dayCurrent = Integer.parseInt(dates.substring(0, 2));
        monthCurrent = Integer.parseInt(dates.substring(3, 5));
        yearCurrent = Integer.parseInt(dates.substring(6, 10));
    }


    /*
    Methode setzt die entsprechenden Werte der Einnahme/Ausgabe auf der
    Benutzeroberfläche
     */
    public void setValue() {
        //Überschrift setzten
        TextView textView = findViewById(R.id.Name);
        if (entry.equals("Outgo")) {
            textView.setText("Ausgabe ändern");
        } else {
            textView.setText("Einnahme ändern");
        }


        //Spinner Kategorie
        spinnerCategory = (Spinner) findViewById(R.id.spinnerCategory);
        if (entry.equals("Outgo")) {
            ArrayList<Category> list = mySQLite.getAllCategories();
            ArrayList<String> listCategory = new ArrayList<String>();
            for (int i = 0; i < list.size(); i++) {
                listCategory.add(list.get(i).getName_PK());
            }

            spinnerCategory = (Spinner) findViewById(R.id.spinnerCategory);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, listCategory);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCategory.setAdapter(adapter);

            int spinnerPosition = adapter.getPosition(category);
            spinnerCategory.setSelection(spinnerPosition);
        } else { //Bei einer Einname darf dieser Spinner nicht angezeigt werden
            TextView textViewCat = (TextView) findViewById(R.id.textViewCategory);
            textViewCat.setVisibility(View.GONE);
            spinnerCategory.setVisibility(View.GONE);
        }


        //Spinner Zyklus
        spinnerCycle = (Spinner) findViewById(R.id.spinnerCycle);
        ArrayAdapter<CharSequence> adapterCycle = ArrayAdapter.createFromResource(this, R.array.spinner_cycle, android.R.layout.simple_spinner_item);
        adapterCycle.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCycle.setAdapter(adapterCycle);
        int index = adapterCycle.getPosition(cycle);
        spinnerCycle.setSelection(index);

        //Name setzen
        editTextName = (EditText) findViewById(R.id.Bezeichnung);
        editTextName.setText(name);

        //Wert setzen
        editTextValue = (EditText) findViewById(R.id.editTextNumberDecimal);
        DecimalFormat df = new DecimalFormat("0.00"); //Darstellung anpassen
        editTextValue.setText(df.format(value));

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


    /*
    Methode wird aufgerufen wenn Ändern-Button gedruckt wurde
     */
    public void onClickChange(View view) {

        boolean valid = getValues(); //sind die Angaben valide, wenn nein errorValue != 0

        if (valid) { // Wenn ja
            if (entry.equals("Intake")) { //Einname in der DB aktuallisieren
                Intake intake = new Intake(name, value, day, month, year, cycle);
                mySQLite.updateIntake(intake, id);
            } else { // oder Ausgabe in der DB aktuallisieren
                Outgo outgo = new Outgo(name, value, day, month, year, cycle, category);
                mySQLite.updateOutgo(outgo, id);
            }

            //Wenn der Eintrag in der Vergangenheit liegt muss das Budget "Übertrag vcm XX.YYYY" angepasst werden
            if ((month < monthCurrent) || (year < yearCurrent)) {
                setBudgetEntry(month, year);
            }

            //zurück zur Tabellendarstellung
            Intent switchToChartActivity= new Intent(this, ChartViewActivity.class);
            startActivity(switchToChartActivity);
        }else{ //Wenn Eingabe nicht valide sind
                informUser(); //Benutzer auf den Fehler hinweisen
                errorValue = 0; //danach zurücksetzen
            }
        }


    /*
    Methode wird aufgerufen wenn Löschen-Button gedruckt wurde
     */
    public void onClickDelete(View view) {

        if (entry.equals("Outgo")) { //Ausgabe löschen
            mySQLite.deleteOutgoById(id);
        } else { //oder Einnahme löschen
            mySQLite.deleteIntakeById(id);
        }

        //Wenn der gelöschte Eintrag in der Vergangenheit liegt muss "Übertrag vom XX.YYYY" angepasst werden
        if ((month < monthCurrent) || (year < yearCurrent)) {
            setBudgetEntry(month, year);
        }

        //Zurück zur Tabellenansicht
        Intent switchToChartActivity= new Intent(this, ChartViewActivity.class);
        startActivity(switchToChartActivity);
    }


    /*
    Methode wird aufgerufen wenn Abbrechen-Button gedruckt wurde
    */
    public void onClickCancel(View view){
        //Zurück zur Tabellenansicht
        Intent switchToChartActivity= new Intent(this, ChartViewActivity.class);
        startActivity(switchToChartActivity);
    }


    /*
   Methode überschreibt die Werte name, value, day, month, year, cyclus, category
   Wenn die Eingabe nicht valide ist, ist errorValue != 0
   */
    private boolean getValues() {
        boolean retValue = true; //Rückgabewert

        //Datum:
        date = editTextDate.getText().toString();
        day = Integer.parseInt(date.substring(0, 2));
        month = Integer.parseInt(date.substring(3, 5));
        year = Integer.parseInt(date.substring(6, 10));
        //Datum liegt in der Zukunft
        if((month > monthCurrent && year >= yearCurrent) || (year > yearCurrent) ||(day > dayCurrent && month == monthCurrent && year == yearCurrent)){ //Eintrag liegt in der Zukunft
            errorValue = 1;
            retValue = false;
        }

        //Wert
        EditText editTextValue = (EditText) findViewById(R.id.editTextNumberDecimal);
        Pattern p = Pattern.compile("^\\d+([\\.,]\\d{2})?$"); //10 oder 10.00 oder 10,00
        Matcher m = p.matcher(editTextValue.getText().toString());
        if(m.find()){ //Eintrag ist valide
            value = Double.parseDouble(editTextValue.getText().toString().replace(",",".")); //Eingabe mit Komma abfangen
            if(value == 0.0){ //Prüfen ob ein Wert gesetzt wurde
                errorValue = 3;
                retValue = false;
            }
        }else{ //z.B. 10.000 angegeben
            errorValue = 4;
            retValue = false;
        }

        //Name
        EditText editTextName = (EditText) findViewById(R.id.Bezeichnung);
        name = editTextName.getText().toString();
        if(name.equals("Titel") || name.trim().isEmpty()){
            errorValue = 2; // kein Titel gesetzt
            retValue = false;
        }

        //Zyklus
        cycle = spinnerCycle.getSelectedItem().toString();

        //Kategorie, wenn Outgo
        if (entry.equals("Outgo")) {
            category = spinnerCategory.getSelectedItem().toString();
        }

        return retValue;
    }


     /*
     Methode die den Benutzer auf einen Fehler mittels einem Dialog hinweist
      */
    private void informUser(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Hinweis"); //Titel des Dialogs

        //Hinweistext des Dialogs
        if(errorValue == 1){
            builder.setMessage("Das gewählte Datum liegt in der Zukunft.");
        }else if(errorValue == 2){
            builder.setMessage("Bitte setzen Sie einen Titel..");
        }else if(errorValue == 3){
            builder.setMessage("Bitte geben Sie einen Wert an.");
        }else if(errorValue == 4){
            builder.setMessage("Ihre Eingabe bezüglich des Werts ist nicht valide.");
        }

        builder.setCancelable(true);
        builder.setNeutralButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show(); //Dialog anzeigen
        month--; //damit im Kalender der aktuelle Monat angezeigt wird.
    }

    /*
    Funktion geht von monthEntry +1 bis zum akuellen Monat/Jahr iterativ durch,
    löscht den Eintrag mit dem Budget "Übertrag vom XX.YYYY" und berechnet den neuen Wert
    ist der Wert positiv wird dieser in Einnahmen, ansonsten in Ausgaben, hinterlegt
     */
    private void setBudgetEntry(int monthEntry,int yearEntry){
        if((monthEntry < monthCurrent) || (yearEntry < yearCurrent)){
            do{
                // Erst hochzählen, da man den nächsten Monat braucht
                if(monthEntry == 12){
                    monthEntry = 1;
                    yearEntry = yearEntry +1;
                }else{
                    monthEntry = monthEntry + 1;
                }

                //Eintrag muss aus der Datenbank enfernt werden
                //Wie der Eintrag lautet
                String title = "Übertrag vom ";
                if(monthEntry > 1){
                    title = title +(monthEntry-1)+"."+yearEntry;
                }else{
                    title = title +12+"."+(yearEntry-1);
                }

                //ID des Eintrags ermitteln
                int idIntake = mySQLite.getIntakeIdByName(title);
                int idOutgo = mySQLite.getOutgoIdByName(title);
                if(idIntake > -1){
                    mySQLite.deleteIntakeById(idIntake); //Eintrag löschen
                }else if(idOutgo > -1){
                    mySQLite.deleteOutgoById(idOutgo); //Eintrag löschen
                }

                //neuer Wert bestimmen
                double value = 0.0;
                if (monthEntry > 1) {
                    value = mySQLite.getValueIntakesMonth(31, monthEntry - 1, yearEntry) - mySQLite.getValueOutgoesMonth(31, monthEntry - 1, yearEntry);
                } else { //1
                    value = mySQLite.getValueIntakesMonth(31, 12, yearEntry - 1) - mySQLite.getValueOutgoesMonth(31, 12, yearEntry - 1);
                }

                //neuer Eintrag anlegen
                if(value >= 0) { //Einnahme
                    Intake intake = new Intake(title, value, 1, monthEntry, yearEntry, "einmalig");
                    mySQLite.addIntake(intake);
                }else{ //Ausgabe
                    value = value * (-1);
                    Outgo outgo = new Outgo(title, value, 1, monthEntry, yearEntry, "einmalig","Sonstiges");
                    mySQLite.addOutgo(outgo);
                }
            }while (!((monthEntry == monthCurrent) && (yearEntry == yearCurrent)));
        }
    }
}