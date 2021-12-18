package com.example.haushaltsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.haushaltsapp.database.Category;
import com.example.haushaltsapp.database.Intake;
import com.example.haushaltsapp.database.MySQLite;
import com.example.haushaltsapp.database.Outgo;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AddEntryActivity extends AppCompatActivity {

    private MySQLite mySQLite;

    //REQUESTCODES
    private final int REQUESTCODE_ADD = 12; //AddEntryActivity
    private final int REQUESTCODE_SHOW = 13; //ShowEntryActivity
    private final int REQUESTCODE_ADD_CATEGORY = 15; //AddCategoryActivity

    private Spinner spinnerCyclus, spinnerCategory; //Zyklus, Kategorie
    private EditText editTextDate; //Datum
    private ImageView calenderView; //Kalender

    //Werte der Einnahme oder Ausgabe
    private String name;
    private double value;
    private String dates;
    private int day;
    private int month;
    private int year;
    private String cyclus;
    private String category;

    //Aktuelles Datum. Notwendig um Budget-Eintrag anzupassen
    private int monthCurrent;
    private int yearCurrent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);

        mySQLite = new MySQLite(this);

        getDate(); //setze monthCurrent und yearCurrent mit dem aktuellen Datum


        //Spinner um den Zyklus anzugeben
        spinnerCyclus = (Spinner) findViewById(R.id.spinnerCyclus);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,  R.array.spinner_cyclus, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCyclus.setAdapter(adapter2);

        //Spinner Kategorie
        Intent intent = getIntent();
        ArrayList<Category> list = mySQLite.getAllCategory();
        spinnerCategory = (Spinner) findViewById(R.id.spinnerCategory);
        ArrayAdapter<Category> adapter3 = new ArrayAdapter<Category>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, list);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter3);

        //Aktuelles Datum anzeigen
        editTextDate = (EditText) findViewById(R.id.editTextDate);
        java.util.Calendar kalender = Calendar.getInstance();
        SimpleDateFormat datumsformat = new SimpleDateFormat("dd.MM.yyyy");
        editTextDate.setText(datumsformat.format(kalender.getTime()));

        //Aktuelles Datum von Kalendar holen, um im CalenderView einzubinden
        year = kalender.get(Calendar.YEAR);
        month = kalender.get(Calendar.MONTH);
        day = kalender.get(Calendar.DAY_OF_MONTH);


        //Kalender
        calenderView = findViewById(R.id.calenderView);
        //Setzen von Listener auf dem Kalender Symbol
        calenderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View dateView) {
                DatePickerDialog dateDialog = new DatePickerDialog(AddEntryActivity.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        day = selectedDay;
                        month = selectedMonth + 1;
                        year = selectedYear;

                        String dayString = String.valueOf(day);
                        String monthString = String.valueOf(month);
                        if(day < 10){
                            dayString = "0"+dayString;
                        }
                        if(month < 10){
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
    Eingabe soll eine Einnahme sein
     */
    public void onClickIntake(View view){
        boolean valide = getValues();
        if(valide){ //Titel und Wert wurde gesetzt
            Intake intake = new Intake(name, value, day, month, year, cyclus);
            mySQLite.addIntake(intake);
            if((month < monthCurrent) || (year < yearCurrent)) {//Wenn der Eintrag in der Vergangenheit liegt muss das Budget angepasst werden
                setBudgetEntry(month, year);
            }
            super.finish();
        }
    }

    /*
    Ausgabe soll eine Ausgabe sein
    */
    public void onClickOutgo(View view){
        boolean valide = getValues();
        if(valide){ ////Titel und Wert wurde gesetzt
            Outgo outgo = new Outgo(name, value, day, month, year, cyclus, category);
            mySQLite.addOutgo(outgo);
            if((month < monthCurrent) || (year < yearCurrent)) {//Wenn der Eintrag in der Vergangenheit liegt muss das Budget angepasst werden
                setBudgetEntry(month, year);
            }
            super.finish();
        }
    }


    /*
    Funktion um die eingegebenen Werte zu ermitteln
    Warnt, falls eine Eingabe nicht sinnvoll ist
     */
    private boolean getValues(){
        boolean retValue = true;

        //Datum:
        dates = editTextDate.getText().toString();
        day = Integer.parseInt(dates.substring(0,2));
        month = Integer.parseInt(dates.substring(3,5));
        year = Integer.parseInt(dates.substring(6,10));

        if((month > monthCurrent && year >= yearCurrent) || (year > yearCurrent)){ //Eintrag liegt in der Zukunft
            Toast.makeText(AddEntryActivity.this, "Ihr gewähltes Datum liegt in der Zukunft",
                    Toast.LENGTH_SHORT).show();
            retValue = false;
        }

        //Wert anzeigen lassen:
        EditText editTextValue = (EditText) findViewById(R.id.editTextNumberDecimal);
        String valueString = editTextValue.getText().toString().replace(',', '.');
        value = Double.parseDouble(valueString);
        if(value == 0.0){
            Toast.makeText(AddEntryActivity.this, "Bitte geben Sie einen Wert ein",
                    Toast.LENGTH_SHORT).show();
            retValue = false;
        }

        //Name
        EditText editTextName = (EditText) findViewById(R.id.Bezeichnung);
        name = editTextName.getText().toString();
        if(name.equals("Titel")){
            Toast.makeText(AddEntryActivity.this, "Bitte geben Sie einen Titel ein",
                    Toast.LENGTH_SHORT).show();
            retValue = false;
        }


        //Zyklus
        cyclus = spinnerCyclus.getSelectedItem().toString();

        //Kategorie
        category = spinnerCategory.getSelectedItem().toString();

        return retValue;
    }


    // Setzt die Variablen monthCurrent und yearCurrent mit dem aktuellen datum
    private void getDate(){
        java.util.Calendar calender = java.util.Calendar.getInstance();
        SimpleDateFormat datumsformat = new SimpleDateFormat("dd.MM.yyyy");
        String dates = datumsformat.format(calender.getTime());
        monthCurrent= Integer.parseInt(dates.substring(3,5));
        yearCurrent = Integer.parseInt(dates.substring(6,10));
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
                    yearEntry++;
                }else{
                    monthEntry++;
                }
                //Eintrag muss aus der Datenbank enfernt werden
                //Wie der Eintrag lautet
                String titel = "Restbudget vom ";
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
                } else {
                    value = mySQLite.getValueIntakesMonth(31, 1, yearEntry - 1) - mySQLite.getValueOutgosMonth(31, 1, yearEntry - 1);
                }
                if(value >= 0) { //Einnahme
                    Intake intake = new Intake(titel, value, 1, monthEntry, yearEntry, "einmalig");
                    mySQLite.addIntake(intake);
                }else{ //Ausgabe
                    value = value * (-1);
                    Outgo outgo = new Outgo(titel, value, 1, monthEntry, yearEntry, "einmalig","Sonstiges");
                    mySQLite.addOutgo(outgo);
                }
            }while ((monthEntry < monthCurrent) && (yearEntry <= yearCurrent));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.navigation_menu, menu);

        //Die aktuelle Activity im Menü ausblenden
        MenuItem item = menu.findItem(R.id.itemAddIntakesOutgoes);
        item.setEnabled(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.itemMainPage:
                Intent switchToMain = new Intent(this, MainActivity.class);
                startActivity(switchToMain);
                return true;

            case R.id.itemAddIntakesOutgoes:
                mySQLite = new MySQLite(this);
                ArrayList<Category> categories = mySQLite.getAllCategory();
                Intent switchToAddEntry = new Intent(this, AddEntryActivity.class);
                switchToAddEntry.putExtra("list",categories);
                mySQLite.close();
                startActivityForResult(switchToAddEntry,REQUESTCODE_ADD);
                return true;

            case R.id.subitemIntakes:
                mySQLite = new MySQLite(this);
                ArrayList<Intake> intakes = mySQLite.getMonthIntakes(day,month,year);
                Intent getIntakes = new Intent(this, ShowEntriesActivity.class);
                getIntakes.putExtra("list",(Serializable) intakes);
                getIntakes.putExtra("entry","Intake");
                mySQLite.close();
                startActivityForResult(getIntakes, REQUESTCODE_SHOW);
                return true;

            case R.id.subitemOutgoes:
                mySQLite = new MySQLite(this);
                ArrayList<Outgo> outgoes = mySQLite.getMonthOutgos(day, month, year);
                Intent getOutgoes = new Intent(this, ShowEntriesActivity.class);
                getOutgoes.putExtra("list",(Serializable) outgoes);
                getOutgoes.putExtra("entry","Outgo");
                mySQLite.close();
                startActivityForResult(getOutgoes, REQUESTCODE_SHOW);
                return true;

            case R.id.itemBudgetLimit:
                Intent switchToBudgetLimit = new Intent(this, BudgetLimitActivity.class);
                startActivity(switchToBudgetLimit);
                return true;

            case R.id.itemDiagramView:
                mySQLite = new MySQLite(this);
                Intent switchToDiagramView = new Intent(this, DiagramViewActivity.class);
                //Alle Ausgaben in Datenbank
                ArrayList<Outgo> AlloutgoD =mySQLite.getAllOutgo();
                switchToDiagramView.putExtra("dataOut",AlloutgoD);
                //Alle Einnahmen in Datenbank
                ArrayList<Intake> AllIntakeD =mySQLite.getAllIntakes();
                switchToDiagramView.putExtra("dataIn",AllIntakeD);
                mySQLite.close();
                startActivity(switchToDiagramView);
                return true;

            case R.id.itemTableView:
                mySQLite = new MySQLite(this);
                Intent switchToChartView = new Intent(this, ChartViewActivity.class);
                //Alle Ausgaben in Datenbank
                ArrayList<Outgo> AlloutgoT =mySQLite.getAllOutgo();
                switchToChartView.putExtra("dataOut",AlloutgoT);
                //Ausgaben von aktuellem Monat
                ArrayList<Outgo> outgoesT = mySQLite.getMonthOutgos(day,month,year);
                switchToChartView.putExtra("monthlist",outgoesT);
                //Alle Einnahmen in Datenbank
                ArrayList<Outgo> AllintakeT =mySQLite.getAllOutgo();
                switchToChartView.putExtra("dataIn",AllintakeT);
                mySQLite.close();
                startActivity(switchToChartView);
                return true;

            case R.id.itemCalendar:
                Intent switchToCalender = new Intent(this, CalendarEventActivity.class);
                startActivity(switchToCalender);
                return true;

            case R.id.itemToDoListe:
                Intent switchToToDoList = new Intent(this, ToDoListActivity.class);
                startActivity(switchToToDoList);
                return true;

            case R.id.itemAddCategory:
                mySQLite = new MySQLite(this);
                Intent switchToAddCategory = new Intent(this, AddCategoryActivity.class);
                ArrayList<Category> categories1 = mySQLite.getAllCategory();
                switchToAddCategory.putExtra("list",(Serializable) categories1);
                mySQLite.close();
                startActivityForResult(switchToAddCategory, REQUESTCODE_ADD_CATEGORY);
                return true;

            case R.id.itemPdfCreator:
                Intent switchToPdfCreator = new Intent(this, PDFCreatorActivity.class);
                startActivity(switchToPdfCreator);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


}