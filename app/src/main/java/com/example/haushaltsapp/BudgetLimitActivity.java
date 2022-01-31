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
import com.example.haushaltsapp.Database.Category;
import com.example.haushaltsapp.Database.MySQLite;
import java.text.DecimalFormat;
import java.util.ArrayList;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
Activity um die Limits der Kategorien und des Gesamtbudgets zu setzen
 */
public class BudgetLimitActivity extends AppCompatActivity {

    private MySQLite mySQLite;
    private LinearLayout linearLayout;
    private CheckBox checkBoxTotal, checkBoxCategory;

    //Variablen für Gesamtlimit
    private String totalString = "Gesamtbudget";
    private double totalLimit = 0; //Dafaultvalue
    private int totalColor = 0; //Dafaultvalue

    //Variablen für das aktuelle Datum
    private int day;
    private int month;
    private int year;

    /*
    1: Wert von Gesamtlimit liegt nicht zwischen 0 und 100
    2: Gesamtbudget einer Kategorie wird überschritten
    4: Eingabe bezüglich des Wertes ist Fehlerhaft (3 Nachkommastellen)
     */
    private int errorValue; //bei entsprechendem Fehler wird ein Dialog geöffnet, um den Benutzer darauf hinzuweisen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_limit);

        //Aufruf der Datenbank
        mySQLite = new MySQLite(this);

        //Layout aufbauen
        setLayout();

        //Letzte Werte der Checkbox setzen
        setCheckBoxes();

        getDate(); //aktueler Tag, Monat, Jahr wichtig für Eingabeprüfung
    }

    /*
    Methode setzt die zu letzt gesetzten Werte der CheckBoxen
     */
    private void setCheckBoxes(){
        //Checkbox
        checkBoxTotal = findViewById(R.id.checkBoxTotal);
        checkBoxCategory = findViewById(R.id.checkBoxCategory);

        //Gesamltlimit
        if(mySQLite.getStateLimitState("Gesamtlimit").equals("true")){
            checkBoxTotal.setChecked(true); //Hacken setzen
            //Möglichkeit, um den Wert für das Gesamtlimit zu setzen
            showTotalBudget();
        }

        //Kategorie
        if(mySQLite.getStateLimitState("Kategorielimit").equals("true")){
            checkBoxCategory.setChecked(true); //Hacken setzen
            //Möglichkeit, um den Wert für die Kategorien zu setzen
            showCategories();
        }
    }

    /*
    Methode zeigt im Layout das Feld für das Gesamtlimit an
     */
    private void showTotalBudget(){
        View v = linearLayout.getChildAt(0); //immer an erster Stelle
        v.setVisibility(View.VISIBLE);
    }

    /*
   Methode verbirgt im Layout das Feld für das Gesamtlimit
    */
    private void showTotalBudgetNot(){
        View v = linearLayout.getChildAt(0); //immer an erster Stelle
        v.setVisibility(View.INVISIBLE);
    }

    /*
    Methode zeigt im Layout die Felder für die Kategorien an
    */
    private void showCategories(){
        int childCount = linearLayout.getChildCount();
        for (int i = 1; i < childCount; i++) { //0 überspringen
            View v = linearLayout.getChildAt(i);
            v.setVisibility(View.VISIBLE);
        }
    }

    /*
    Methode verbirgt im Layout die Felder für die Kategorien
    */
    private void showCategoriesNot(){
        int childCount = linearLayout.getChildCount();
        for (int i = 1; i < childCount; i++) { //0 überspringen
            View v = linearLayout.getChildAt(i);
            v.setVisibility(View.INVISIBLE);
        }
    }



    /*
    Methode, um das Layout aufzubauen
    */
    private void setLayout(){
        //Container für Gesamtbudget und die Kategorien
        linearLayout = findViewById(R.id.container);

        //Gesamtlimit
        totalLimit = mySQLite.getStateLimitValue("Gesamtlimit");
        addCategory(totalString, totalLimit, totalColor);

        //Alle Kategorien
        ArrayList<Category> list = mySQLite.getAllCategories();
        //Darstellen der Kategorien
        for(int i = 0; i < list.size(); i++){
            Category category = list.get(i);
            addCategory(category.getName_PK(), category.getBorder(), category.getColor());
        }

    }

    /*
    Layout aufbauen
    fügt die einzelnen Zeilen hinzu mit den Kategorien und ihren Limits
     */
    private void addCategory(String str, double value,int color){
        View view  = getLayoutInflater().inflate(R.layout.category_limit, null);

        //Farbe
        View viewColor = view.findViewById(R.id.preview_selected_color);
        viewColor.setBackgroundColor(color);  //Farbe setzen

        //Bezeichnung
        TextView textView = view.findViewById(R.id.name);
        textView.setText(str); //Beschreibung setzen

        //Limit
        EditText editText = view.findViewById(R.id.limit);
        // Wenn value keine zwei Nachkommastellen hat - welche einfügen
        DecimalFormat df = new DecimalFormat("0.00");
        editText.setText(df.format(value));

        if(str.equals("Gesamtbudget")){ //Gesamtbudget in % nicht in €
            TextView prozent = view.findViewById(R.id.prozent);
            prozent.setText("%");
        }

        view.setVisibility(View.INVISIBLE);
        linearLayout.addView(view); //in Container hinzufügen
    }


    /*
    Methode wird aufgerufen, wenn eine Checkbox gegklickt wird
     */
    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();

        switch(view.getId()) {
            case R.id.checkBoxTotal: //Gesamtlimit geklickt
                if (checked) {
                    if (checkBoxCategory.isChecked()) { //Nur ein Limit kann angeklickt sein
                        Toast.makeText(BudgetLimitActivity.this, "Es kann nur ein Limit betrachtet werden",
                                Toast.LENGTH_SHORT).show();
                        checkBoxTotal.setChecked(false); //wieder als nicht gesetzt setzen
                    }else{
                        showTotalBudget(); //Zeile mit Gesamtlimit anzeigen
                    }
                }else{
                    showTotalBudgetNot(); //Zeile mit Gesamtlimit nicht anzeigen
                }
                break;
            case R.id.checkBoxCategory: //Kategorie geklickt
                if (checked) {
                    if (checkBoxTotal.isChecked()) { //Nur ein Limit kann angeklickt sein
                        Toast.makeText(BudgetLimitActivity.this, "Es kann nur ein Limit betrachtet werden",
                                Toast.LENGTH_SHORT).show();
                        checkBoxCategory.setChecked(false);
                    }else{
                        showCategories();//Zeilen mit Kategorien anzeigen
                    }
                }else{
                    showCategoriesNot();//Zeilen mit Kategorien nicht anzeigen
                }
                break;
        }
    }


    /*
    Methode setzt die Variablen day, month, year mit dem aktuellen Datum
     */
    private void getDate() {
        Calendar calender = Calendar.getInstance();
        year = calender.get(Calendar.YEAR);
        month = calender.get(Calendar.MONTH)+1;//Fängt bei mit 0 an
        day = calender.get(Calendar.DAY_OF_MONTH);
    }

    /*
    Methode wird aufgerufen, wenn Abbruch-Button gedruckt
     */
    public void clickCancel(View view){
        //Zurück zur Main
        Intent switchToMainActivity= new Intent(this, MainActivity.class);
        startActivity(switchToMainActivity);
    }


    /*
   Methode wird aufgerufen, wenn Ok-Button gedruckt
   */
    public void clickOk(View view){
        boolean validValues = checkValues(); //Prüfe die eingegebenen Werte. Wenn nicht sinnvoll -> errorValue != 0
        if(validValues){
            writeValues(); //Werte in die Datenbank schreiben
            //Zurück zur Main
            Intent switchToMainActivity= new Intent(this, MainActivity.class);
            startActivity(switchToMainActivity);
        }else {
            informUser(); //Eingabe war nicht sinnvoll. Benutzer hinweisen
            errorValue = 0; //danach zurücksetzen
        }
    }

    /*
    Prüfe ob die Eingaben Sinn machen. Setzt ggf errorValue != 0 setzen
     */
    private boolean checkValues(){
        double sum = 0; //Summe aller gesetzten Limits der Kategorien

        int childCount = linearLayout.getChildCount();

        for (int i = 1; i < childCount; i++) {
            View v = linearLayout.getChildAt(i);
            EditText valueLimit = v.findViewById(R.id.limit);
            double valueInt = 0.0; //Limit der jeweiligen Kategorie

            Pattern p = Pattern.compile("^\\d+([\\.,]\\d{2})?$"); // 10 oder 10.00 oder 10,00 erlaubt
            Matcher m = p.matcher(valueLimit.getText().toString());
            if(m.find()){ //Eintrag ist valide
                valueInt = Double.parseDouble(valueLimit.getText().toString().replace(",",".")); //Eingabe mit Komma abfangen
            }else{ //Eingabe gibt keinen Sinn (3 Nachkommastellen ect.)
                errorValue = 4;
                return false;
            }

            if(i > 0) { //Erster Eintrag ist Gesamtbudget
                sum = sum + valueInt;
            }else if(valueInt > 100 || valueInt < 0){ //Limit von Gesamtbudget muss zwischen 0 und 100% liegen
                TextView name = v.findViewById(R.id.name);
                errorValue = 1;
                return false;
            }
        }

        double totalBudget = mySQLite.getValueIntakesMonth(day,month,year);
        if(sum > totalBudget){ //Summe aller gesetzten Limits der Kategorien überschreitet aktuelles Budget
            errorValue = 2;
            return false;
        }

        return true;
    }

    /*
    Methode öffnet ein Fenster um den Benutzer auf unterschiedliche Fehler hinzuweisen.
     */
    private void informUser(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Hinweis"); //Titel des Dialogs

        //Hinweistext setzen
        if(errorValue == 1){
            builder.setMessage("Der Eintrag bezüglich des Gesamtbudget liegt nicht zwischen 0% und 100%.");
        }else if(errorValue == 2){
            builder.setMessage("Mit den gewählten Grenzen wird das Gesamtbudget überschritten");
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
    }


    /*
    Methode um die Eingaben in der Datanbank zu speichern
     */
    private void writeValues(){
        //Gesamt
        View v = linearLayout.getChildAt(0);
        EditText valueLimit = v.findViewById(R.id.limit);
        String entryString = valueLimit.getText().toString().replace(",","."); //Eingabe mit Komma abfangen
        totalLimit = Double.parseDouble(entryString);

        //Gesamtlimit in DB (Checkbox Info)
        if(checkBoxTotal.isChecked()){
            mySQLite.updateStateLimit("Gesamtlimit", totalLimit, "true");
        }else{
            mySQLite.updateStateLimit("Gesamtlimit", totalLimit, "false");
        }

        //Kategorie-Limit in DB (Checkbox Info)
        if(checkBoxCategory.isChecked()){
            mySQLite.updateLimitState("Kategorielimit","true");
        }else{
            mySQLite.updateLimitState("Kategorielimit","false");
        }

        //Kategorien
        int childCount = linearLayout.getChildCount();
        for (int i = 1; i < childCount; i++) { //bei 1 anfangen um "Gesamtbudget" zu überspringen
            v = linearLayout.getChildAt(i);

            valueLimit = v.findViewById(R.id.limit);
            TextView name = v.findViewById(R.id.name);
            Category category = mySQLite.getCategory(name.getText().toString()); //Kategorie aus DB
            entryString = valueLimit.getText().toString().replace(",","."); //Eingabe mit Komma abfangen
            category.setBorder(Double.valueOf(entryString)); //neues Limit setzen
            mySQLite.updateCategory(category); //DB aktuallisieren
        }


    }

    //Menüaufruf
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.navigation_menu, menu);

        //Die aktuelle Activity im Menü ausblenden
        MenuItem item = menu.findItem(R.id.itemBudgetLimit);
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