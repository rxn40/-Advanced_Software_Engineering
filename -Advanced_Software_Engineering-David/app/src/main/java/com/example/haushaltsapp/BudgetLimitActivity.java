package com.example.haushaltsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.haushaltsapp.database.Category;
import com.example.haushaltsapp.database.Intake;
import com.example.haushaltsapp.database.MySQLite;
import com.example.haushaltsapp.database.Outgo;

import java.util.ArrayList;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;


public class BudgetLimitActivity extends AppCompatActivity {

    ////Variabeln zur Menünavigation
    private MySQLite mySQLite;

    private LinearLayout linearLayout;
    private CheckBox checkBoxGesamt, checkBoxCategory;


    //Variablen für Gesamtlimit
    private String gesamtString = "Gesamtbudget";
    private double gesamtLimit = 0; //Dafaultvalue
    private int gesamtColor = 0; //Dafaultvalue

    //aktuelles Datum
    private int day;
    private int month;
    private int year;

    /*
    1: Wert von Gesamtlimit liegt nicht zwischen 0 und 100
    2: Gesamtbudget einer Kategorie wird überschritten
     */
    private int errorValue; //bei entsprechendem Fehler wird ein Dialog geöffnet, um den Benutzer darauf hinzuweisen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_limit);

        //Datenbank
        mySQLite = new MySQLite(this);

        //Layout aufbauen
        setLayout();

        getDate(); //aktueler Tag, Monat, Jahr wichtig für Eingabeprüfung
    }


    //Methode um das Layout aufzubauen
    private void setLayout(){
        //checkBox
        checkBoxGesamt = findViewById(R.id.checkBox);
        checkBoxCategory = findViewById(R.id.checkBox2);

        //Ggf Hacken setzen
        if(mySQLite.getSateLimitState("Gesamtlimit").equals("true")){
            checkBoxGesamt.setChecked(true);
        }

        if(mySQLite.getSateLimitState("Kategorielimit").equals("true")){
            checkBoxCategory.setChecked(true);
        }

        //Kategorien in der View darstellen
        linearLayout = findViewById(R.id.container);
        //Gesamtlimit
        ArrayList<Category> list = mySQLite.getAllCategory();
        gesamtLimit = mySQLite.getSateLimitValue("Gesamtlimit");
        addCategory(gesamtString, gesamtLimit, gesamtColor);
        //Darstellen der Kategorien
        for(int i = 0; i < list.size(); i++){
            Category category = list.get(i);
            addCategory(category.getName_PK(), category.getBorder(), category.getColor());
        }
    }




    // Setzt die Variablen day, month, year
    private void getDate() {
        java.util.Calendar calender = java.util.Calendar.getInstance();
        year = calender.get(Calendar.YEAR);
        month = calender.get(Calendar.MONTH);
        day = calender.get(Calendar.DAY_OF_MONTH);
    }


    //Layout aufbauen
    //fügt die einzelnen Zeilen hinzu mit den Kategorien und ihren Limits
    private void addCategory(String str, double value,int color){
        View view  = getLayoutInflater().inflate(R.layout.category_limit, null);

        View viewColor = view.findViewById(R.id.preview_selected_color);
        viewColor.setBackgroundColor(color);

        TextView textView = view.findViewById(R.id.name);
        textView.setText(str);

        EditText editText = view.findViewById(R.id.limit);
        editText.setText(String.valueOf((int) value));

        if(str.equals("Gesamtbudget")){
            TextView prozent = view.findViewById(R.id.prozent);
            prozent.setText("%");
        }

        linearLayout.addView(view);
    }


    //Vermeiden, dass man zwei gleichzeitig setzen kann
    //Variablen gesamtButton und categoryButton setzen
    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();

        switch(view.getId()) {
            case R.id.checkBox: //Gesamtlimit
                if (checked) {
                    if (checkBoxCategory.isChecked()) {
                        Toast.makeText(BudgetLimitActivity.this, "Es kann nur ein Limit betrachtet werden",
                                Toast.LENGTH_SHORT).show();
                        checkBoxGesamt.setChecked(false);
                    }
                }
                break;
            case R.id.checkBox2: //Kategorie
                if (checked) {
                    if (checkBoxGesamt.isChecked()) {
                        Toast.makeText(BudgetLimitActivity.this, "Es kann nur ein Limit betrachtet werden",
                                Toast.LENGTH_SHORT).show();
                        checkBoxCategory.setChecked(false);
                    }
                }
                break;
        }
    }


    //Abbrechen
    public void clickCancel(View view){
        //Zurück zur Main
        Intent switchToMainActivity= new Intent(this, MainActivity.class);
        startActivity(switchToMainActivity);
    }

    //Ok
    public void clickOk(View view){
        //Prüfen, ob die eingabe sinnvoll ist. -> sind alle werte zwischen 0 und 100?
        //und in Summe nur zwischen 0 und 100%
        boolean valideValues = checkValues();
        if(valideValues){
            writeValues(); //Werte in die Datenbank schreiben
            //Zurück zur Main
            Intent switchToMainActivity= new Intent(this, MainActivity.class);
            startActivity(switchToMainActivity);
        }
        informUser();
    }

    //Prüfe ob die Eingaben Sinn machen. Setzt ggf errorValue
    private boolean checkValues(){
        double summe = 0;

        int childCount = linearLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View v = linearLayout.getChildAt(i);
            EditText valueLimit = v.findViewById(R.id.limit);
            double valueInt = Double.valueOf(valueLimit.getText().toString());

            if(i > 0) { //Erster Eintrag ist Gesamtbudget
                summe = summe + valueInt;
            }else if(valueInt > 100 || valueInt < 0){
                TextView name = v.findViewById(R.id.name);
                errorValue = 1;
                return false;
            }
        }

        double gesamtbudget = mySQLite.getValueIntakesMonth(day,month,year);
        if(summe > gesamtbudget){
            errorValue = 2;
            return false;
        }

        return true;
    }

    //Methode öffnet ein Fenster um den Benutzer auf unterschiedliche Fehler hinzuweisen.
    private void informUser(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Hinweis");

        if(errorValue == 1){
            builder1.setMessage("Das Eintrag bezüglich Gesamtbudget liegt nicht zwischen 0% und 100%.");
        }else if(errorValue == 2){
            builder1.setMessage("Mit den gewählten Grenzen wird das Gesamtbudget überschritten");
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
    }


    //Methode um die eingabe in die Datenbank zu speichern
    private void writeValues(){
        //Gesamt
        View v = linearLayout.getChildAt(0);
        EditText valueLimit = v.findViewById(R.id.limit);
        String entryString = valueLimit.getText().toString().replace(",","."); //Eingabe mit Komma abfangen
        gesamtLimit = Double.parseDouble(entryString);
        //Wert in die Datanbank
        if(checkBoxGesamt.isChecked()){
            mySQLite.updateStateLimit("Gesamtlimit", gesamtLimit, "true");
        }else{
            mySQLite.updateStateLimit("Gesamtlimit", gesamtLimit, "false");
        }

        //Kategorie-Limit in DB
        if(checkBoxCategory.isChecked()){
            mySQLite.updateLimitSate("Kategorielimit","true");
        }else{
            mySQLite.updateLimitSate("Kategorielimit","false");
        }

        //Kategorien
        int childCount = linearLayout.getChildCount();
        for (int i = 1; i < childCount; i++) { //bei 1 anfangen um "Gesamtbudget" zu überspringen
            v = linearLayout.getChildAt(i);
            valueLimit = v.findViewById(R.id.limit);
            TextView name = v.findViewById(R.id.name);
            Category category = mySQLite.getCategory(name.getText().toString());
            entryString = valueLimit.getText().toString().replace(",","."); //Eingabe mit Komma abfangen
            category.setBorder(Double.valueOf(entryString));
            mySQLite.updateCategory(category);
        }
    }



    //Menü

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.navigation_menu, menu);

        //Die aktuelle Activity im Menü ausblenden
        MenuItem item = menu.findItem(R.id.itemBudgetLimit);
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
                int day = 0;  //Yvette
                int month = 0;  //Yvette
                int year = 0;  //Yvette
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