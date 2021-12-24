package com.example.haushaltsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.annotation.NonNull;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.haushaltsapp.database.Category;
import com.example.haushaltsapp.database.Intake;
import com.example.haushaltsapp.database.MySQLite;
import com.example.haushaltsapp.database.Outgo;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.haushaltsapp.database.Category;
import com.example.haushaltsapp.database.MySQLite;

import java.util.ArrayList;
import java.util.List;

public class BudgetLimitActivity extends AppCompatActivity {

    ////Variabeln zur Menünavigation
    private MySQLite mySQLite;
    private final int REQUESTCODE_ADD = 12; //AddEntryActivity
    private final int REQUESTCODE_SHOW = 13; //ShowEntryActivity
    private final int REQUESTCODE_EDIT = 14; //EditEntryActivity
    private final int REQUESTCODE_ADD_CATEGORY = 15; //AddCategoryActivity


    private static boolean limitGesamt = false;
    private static boolean limitCategory = false;

    private LinearLayout linearLayout;
    private CheckBox checkBoxGesamt, checkBoxCategory;

    //gibt an, welche Limits betrachtet werden sollen?
    //die für die Kategorien (Datenbank) oder
    //das Gesamtlimit (Variable gesamtLimit)
    private boolean gesamtButton = false;
    private boolean categoryButton = false;

    //Variablen für Gesamtlimit
    private String gesamtString = "Gesamtbudget";
    private static int gesamtLimit = 0;
    private int gesamtColor = 10;

    //aktuelles Datum
    private int day;
    private int month;
    private int year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_limit);

        getDate();

        linearLayout = findViewById(R.id.container);
        checkBoxGesamt = findViewById(R.id.checkBox);
        checkBoxCategory = findViewById(R.id.checkBox2);

        //Infos aus der Datenbank
        mySQLite = new MySQLite(this);
        ArrayList<Category> list = mySQLite.getAllCategory();

        //Gesamtlimit
        addCategory(gesamtString, gesamtLimit, gesamtColor);
        //Darstellen der Kategorien
        for(int i = 0; i < list.size(); i++){
            Category category = list.get(i);
            addCategory(category.getName_PK(), category.getBorder(), category.getColor());
        }

  //      gesamtButton = mySQLite.getSateLimitState("Gesamtlimit").equals("true");
  //      categoryButton = mySQLite.getSateLimitState("Kategorielimit").equals("true");

        gesamtButton = false;
        categoryButton = false;

        //Alte Werte setzen bzg checkbox
        if(limitGesamt){
            checkBoxGesamt.setChecked(true);
        }
        if(limitCategory){
            checkBoxCategory.setChecked(true);
        }
    }

    public static boolean getCatButtonStatus(){
        return limitCategory;
    }

    public static boolean getTotalButtonStatus(){
        return limitGesamt;
    }

    public static Integer getPercentageLimit(){
        return gesamtLimit;
    }
    // Setzt die Variablen day, month, year
    private void getDate() {
        java.util.Calendar calender = java.util.Calendar.getInstance();
        SimpleDateFormat datumsformat = new SimpleDateFormat("dd.MM.yyyy");
        String dates = datumsformat.format(calender.getTime());
        day = Integer.parseInt(dates.substring(0, 2));
        month = Integer.parseInt(dates.substring(3, 5));
        year = Integer.parseInt(dates.substring(6, 10));
    }

    //layout aufbauen
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
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.checkBox:
                if (checked) {
                    if(categoryButton){
                        checkBoxGesamt.setChecked(false);
                        gesamtButton = false;
                    //    mySQLite.updateLimitSate("Gesamtlimit","false");
                     }else{
                        gesamtButton = true;
                      //  mySQLite.updateLimitSate("Gesamtlimit","true");
                    }
                }else {
                    gesamtButton = false;
                   // mySQLite.updateLimitSate("Gesamtlimit","false");
                }break;
            case R.id.checkBox2:
                if (checked) {
                    if(gesamtButton){
                        checkBoxCategory.setChecked(false);
                        categoryButton = false;
                     //   mySQLite.updateLimitSate("Kategorielimit","false");
                    }else{
                        categoryButton = true;
                       // mySQLite.updateLimitSate("Kategorielimit","true");
                    }
                }else {
                    categoryButton = false;
                   // mySQLite.updateLimitSate("Kategorielimit","false");
                }break;
        }
    }



    //Abbrechen
    public void clickCancel(View view){
        super.finish();
    }

    //Ok
    public void clickOk(View view){
        //Prüfen, ob die eingabe sinnvoll ist. -> sind alle werte zwischen 0 und 100?
        //und in Summe nur zwischen 0 und 100%
        boolean valideValues = checkValues();

        if(valideValues){
            limitGesamt = gesamtButton;
            limitCategory = categoryButton;

            writeValues();

            super.finish();
        }
    }

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
                Toast.makeText(BudgetLimitActivity.this, "Ihre Eingabe bei "+name.getText()+" ist fehlerhaft.",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        double gesamtbudget = mySQLite.getValueIntakesMonth(day,month,year);
        if(summe > gesamtbudget){
            Toast.makeText(BudgetLimitActivity.this, "Das Gesamtbudget des Montas von "+gesamtbudget+"€ wird mit diesen Angaben überschritten.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    private void writeValues(){

        //Gesamt
        View v = linearLayout.getChildAt(0);
        EditText valueLimit = v.findViewById(R.id.limit);
        gesamtLimit = Integer.parseInt(valueLimit.getText().toString());

        //Kategorien
        int childCount = linearLayout.getChildCount();
        for (int i = 1; i < childCount; i++) {
            v = linearLayout.getChildAt(i);
            valueLimit = v.findViewById(R.id.limit);
            TextView name = v.findViewById(R.id.name);

            Category category = mySQLite.getCategory(name.getText().toString());
            category.setBorder(Double.valueOf(valueLimit.getText().toString()));
            mySQLite.updateCategory(category);
        }
    }




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