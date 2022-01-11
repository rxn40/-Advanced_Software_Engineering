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

import top.defaults.colorpicker.ColorPickerPopup;

import com.example.haushaltsapp.database.Category;
import com.example.haushaltsapp.database.Intake;
import com.example.haushaltsapp.database.MySQLite;
import com.example.haushaltsapp.database.Outgo;



public class AddCategoryActivity extends AppCompatActivity {

    //Wie viele Kategorien kann man maximal in Summe anlegen?
    private final int maxLimit = 9;



    private MySQLite mySQLite;

    //private Button pickColorButton;
    private View mColorPreview; //Feld, welches gewählte Farbe anzeigt

    private int mDefaultColor; //gewählte Farbe
    private String name = " "; // Name der Kategorie
    private double border = 0.0; //Limit der Kategorie

    private int day, month, year;

    /*
    1: der Titel wurde nicht gesetzt
    2: den Titel gibt es schon
    3: es können keine weiteren Kategorien angelegt werden
     */
    private int errorValue; //bei entsprechendem Fehler wird ein Dialog geöffnet, um den Benutzer darauf hinzuweisen


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);


        mySQLite = new MySQLite(this);

        mColorPreview = findViewById(R.id.preview_selected_color); //Kasten der später die Farbe anzeigt

        //default-Werte setzen
        mDefaultColor = R.color.defaultCategory;
        mColorPreview.setBackgroundColor(mDefaultColor);
        errorValue = 0;
    }



    //um die Farhe auszuwählen
    public void pickColor(View view){
        new ColorPickerPopup.Builder(AddCategoryActivity.this).initialColor(
                Color.RED).enableBrightness(true)
                .enableAlpha(true)
                .okTitle( "Bestätigen")
                .cancelTitle("Abbrechen")
                .showIndicator(true)
                .showValue(true)
                .build()
                .show(view,
                        new ColorPickerPopup.ColorPickerObserver() {
                            @Override
                            public void
                            onColorPicked(int color) {
                                mDefaultColor = color;
                                mColorPreview.setBackgroundColor(mDefaultColor);
                            }
                        });
    }


    public void onClickOk(View view){
        ArrayList<Category> categories = mySQLite.getAllCategory();
        if(categories.size() < maxLimit){ //können Kategorien noch angelegt werden?
            boolean valide = getValues(); //Achtung, wenn valide = false ist errorValue != 0
            if(valide){ //sind die Eingaben sinnvoll?
                Category category = new Category(name, mDefaultColor, border); //Kategorie hinzufügen
                mySQLite.addCategory(category);
            }
        }else{
            errorValue = 3; //Es gibt bereits "maxLimit" Kategorien
        }

        if(errorValue > 0){ //Fehler ist aufgetreten. Benutzer hinweisen
            informUser();
            errorValue = 0; //muss zurückgesetzt werdenmaxLimit
        }else{ //zurück zur Startseite. Kategorie wurde hierbei bereits angelegt
            Intent switchToMain = new Intent(this, MainActivity.class);
            startActivity(switchToMain);
        }
    }

    //bei Abbruch zurück zur Startseite
    public void onClickBreak(View view){
        Intent switchToMain = new Intent(this, MainActivity.class);
        startActivity(switchToMain);
    }


    //um die eingetragene Werte zu ermitteln
    private boolean getValues() {

        boolean retValue = true;

        //Bezeichnung
        EditText editTextName = (EditText) findViewById(R.id.Bezeichnung);
        name = editTextName.getText().toString();
        if(name.equals("Titel")){ //wurde ein Titel gesetzt?
            errorValue = 1;
            retValue = false;
        }else { //gibt es diesen Titel bereits?
            ArrayList<Category> categories = mySQLite.getAllCategory();
            for( int i = 0; i < categories.size(); i++){
                if(categories.get(i).getName_PK().equals(name)){
                    retValue = false;
                    errorValue = 2;
                    break;
                }
            }
        }

        //Limit
        EditText editTextValue = (EditText) findViewById(R.id.editTextLimit);
        String valueString = editTextValue.getText().toString();
        valueString = valueString.replace(",","."); //Komma muss mit einem Punkt ersetzt werden
        border = Double.parseDouble(valueString);

        return retValue;
    }


    //Methode öffnet ein Fenster um den Benutzer auf unterschiedliche Fehler hinzuweisen.
    private void informUser(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Hinweis");
        if(errorValue == 1){
            builder1.setMessage("Bitte setzen Sie einen Titel.");
        }else if(errorValue == 2){
            builder1.setMessage("Diese Kategorie gibt es bereits.");
        }else{ // errorValue == 3
            builder1.setMessage("Es können lieder keine weiteren Kategorien angelegt werden.");
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
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.navigation_menu, menu);

        //Die aktuelle Activity im Menü ausblenden
        MenuItem item = menu.findItem(R.id.itemAddCategory);
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
                mySQLite = new MySQLite(this);
                Intent switchToAddCategory = new Intent(this, AddCategoryActivity.class);
                mySQLite.close();
                startActivity(switchToAddCategory);
                return true;


            case R.id.itemDeleteCategory:
                mySQLite = new MySQLite(this);
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