package com.example.haushaltsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import top.defaults.colorpicker.ColorPickerPopup;
import com.example.haushaltsapp.Database.Category;
import com.example.haushaltsapp.Database.MySQLite;

/*
Activity um eine Kategorie anzulegen
 */
public class AddCategoryActivity extends AppCompatActivity {

    //Maximale Anzahl der anlegbaren Kategorien
    private final int MAX_LIMIT = 9;

    private MySQLite mySQLite;
    private View mColorPreview; //Feld, welches gewählte Farbe anzeigt

    private int mDefaultColor; //gewählte Farbe
    private String name; // Name der Kategorie
    private double border; //Limit der Kategorie

    /*
    1: Titel wurde nicht gesetzt
    2: Titel gibt es schon
    3: Wert für boder nicht sinnvoll
    4: Können keine weiteren Kategorien angelegt werden
     */
    private int errorValue; //bei entsprechendem Fehler wird ein Dialog geöffnet, um den Benutzer darauf hinzuweisen


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        mySQLite = new MySQLite(this);
        mColorPreview = findViewById(R.id.preview_selected_color); //Kasten der später die Farbe anzeigt

        // Setzen von Default-Werten
        name = " ";
        border = 0.0;
        mDefaultColor = R.color.colorDefaultCategory;
        mColorPreview.setBackgroundColor(mDefaultColor);
        errorValue = 0;
    }



    /*
    Funktion um die Farbe auszuwählen. Color Picker
     */
    public void pickColor(View view){
        new ColorPickerPopup.Builder(AddCategoryActivity.this).initialColor(
                Color.RED).enableBrightness(true)//welche Farbe soll zu Beginn ausgewählt sein
                .enableAlpha(true) //Farbintensität einstellbar
                .okTitle( "Bestätigen") //Beschriftung des Bestätigung-Buttons
                .cancelTitle("Abbrechen") //Beschriftung des Abbruch-Buttons
                .showIndicator(true) //kleines Viereck, welches gewählte Farbe anzeigt
                .showValue(false) //Farbwert oben rechts anzeigen lassen
                .build()
                .show(view,
                        new ColorPickerPopup.ColorPickerObserver() {
                            @Override
                            public void
                            onColorPicked(int color) {
                                mDefaultColor = color;
                                //gewählte Farbe im viereckigen Feld anzeigen lassen
                                mColorPreview.setBackgroundColor(mDefaultColor);
                            }
                        });
    }



    /*
    Funktion die ausgeführt wird, nachdem der Ok-Button gedruckt wurde
     */
    public void onClickOk(View view){
        ArrayList<Category> categories = mySQLite.getAllCategories(); //Alle Kategorien aus der DB

        if(categories.size() < MAX_LIMIT){ //Können Kategorien noch angelegt werden?
            boolean valide = getValues(); //Achtung, wenn valide = false ist errorValue != 0
            if(valide){ //sind die Eingaben sinnvoll? wenn ja ->
                Category category = new Category(name, mDefaultColor, border); //Kategorie erzeugen
                mySQLite.addCategory(category); //Kategorie in DB hinzufügen
                //Zurück zur Startseite
                Intent switchToMain = new Intent(this, MainActivity.class);
                startActivity(switchToMain);
            } // else -> weiter unten wird der Benutzer auf den Fehler hingewiesen.
            //Denn: Der Fehler keine mehr anlegen zu können ist wichtiger als nicht setzten eines Titels
        }else{ // Limit von 9 bereits erreicht
            errorValue = 4; //Es gibt bereits "maxLimit" Kategorien
        }

        if(errorValue > 0){ //Fehler ist aufgetreten. Benutzer hinweisen
            informUser(); //Öffnet einen Dialog mit Fehlerhinweis
            errorValue = 0; //Zurücksetzen des errorValues
        }
    }

    /*
    Funktion nach drücken des Abbruch-Buttons
     */
    public void onClickBreak(View view){
        //Zurück zur Startseite
        Intent switchToMain = new Intent(this, MainActivity.class);
        startActivity(switchToMain);
    }


   /*
   Funktion setzt die Variablen name, border
   Achtung: Ändert bei einer nicht validen Eingabe den Wert von errorValue
    */
    private boolean getValues() {

        boolean retValue = true;

        //Bezeichnung
        EditText editTextName = (EditText) findViewById(R.id.Bezeichnung);
        name = editTextName.getText().toString(); //vom Benutzer gewünschter Name
        if(name.equals("Titel") || name.trim().isEmpty()){ //wurde ein Titel gesetzt?
            errorValue = 1; //Titel wurde nicht gesetzt
            retValue = false;
        }else { //Test - gibt es diesen Titel bereits?
            ArrayList<Category> categories = mySQLite.getAllCategories(); //Alle Kategorien aus der DB
            for( int i = 0; i < categories.size(); i++){ //diese iterativ durchgehen
                if(categories.get(i).getName_PK().equals(name)){ //Titel gibt es bereits
                    errorValue = 2;
                    retValue = false;
                    break; //Suche kann abgebrochen werden
                }
            }
        }

        //Limit
        EditText editTextValue = (EditText) findViewById(R.id.editTextLimit);
        Pattern p = Pattern.compile("^\\d+([\\.,]\\d{2})?$"); // 10 oder 10.00 oder 10,00 erlaubt
        Matcher m = p.matcher(editTextValue.getText().toString());
        if(!m.find()){ //Eintrag ist nicht Valide
            errorValue = 3;
            retValue = false;
        }else {
            String valueString = editTextValue.getText().toString();
            valueString = valueString.replace(",", "."); //Komma muss ggf mit einem Punkt ersetzt werden
            border = Double.parseDouble(valueString);
        }
        return retValue;
    }

    /*
    Methode öffnet einen Dialog, um den Benutzer auf den jeweiligen Eingabefehler hinzuweisen
     */
    private void informUser(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Hinweis"); //Titel des Dialogs
        //Hinweistext des Dialogs setzen
        if(errorValue == 1){
            builder.setMessage("Bitte setzen Sie einen Titel.");
        }else if(errorValue == 2) {
            builder.setMessage("Diese Kategorie gibt es bereits.");
        }else if(errorValue == 3){
            builder.setMessage("Die Eingabe für das Limit ist nicht valide.");
        }else if(errorValue == 4){
            builder.setMessage("Es können leider keine weiteren Kategorien angelegt werden.");
        }

        builder.setCancelable(true);
        //Beim drucken des Ok-Buttons Dialog schließen
        builder.setNeutralButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show(); //Dialog anzeigen
    }


    //Aufruf des Menüs
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.navigation_menu, menu);

        //Die aktuelle Activity im Menü ausblenden
        MenuItem item = menu.findItem(R.id.itemAddCategory);
        item.setEnabled(false);

        return true;
    }

    //Menüauswahl
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.itemMainPage:
                Intent switchToMain = new Intent(this, MainActivity.class);
                startActivity(switchToMain);
                return true;

            case R.id.subitemAddIntakes:
                mySQLite = new MySQLite(this);
                Intent switchToAddIntake = new Intent(this, AddEntryActivity.class);
                mySQLite.close();
                switchToAddIntake.putExtra("Selected","Einnahme");
                startActivity(switchToAddIntake);
                return true;

            case R.id.subitemAddOutgoes:
                mySQLite = new MySQLite(this);
                Intent switchToAddOutgo = new Intent(this, AddEntryActivity.class);
                mySQLite.close();
                switchToAddOutgo.putExtra("Selected","Ausgabe");
                startActivity(switchToAddOutgo);
                return true;

            case R.id.itemBudgetLimit:
                Intent switchToBudgetLimit = new Intent(this, BudgetLimitActivity.class);
                startActivity(switchToBudgetLimit);
                return true;

            case R.id.itemDiagramView:
                Intent switchToDiagramView = new Intent(this, DiagramViewActivity.class);
                startActivity(switchToDiagramView);
                return true;

            case R.id.itemTableView:
                Intent switchToChartView = new Intent(this, ChartViewActivity.class);
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
                Intent switchToAddCategory = new Intent(this, AddCategoryActivity.class);
                startActivity(switchToAddCategory);
                return true;

            case R.id.itemDeleteCategory:
                Intent switchToDeleteCategory = new Intent(this, DeleteCategoryActivity.class);
                startActivity(switchToDeleteCategory);
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