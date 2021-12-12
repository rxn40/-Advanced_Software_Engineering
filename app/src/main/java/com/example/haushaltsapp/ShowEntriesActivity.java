package com.example.haushaltsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.haushaltsapp.database.Category;
import com.example.haushaltsapp.database.Intake;
import com.example.haushaltsapp.database.MySQLite;
import com.example.haushaltsapp.database.Outgo;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/*
Repräsentation von Einnahmen bzw Ausgaben eines übergebenen Array
 */
public class ShowEntriesActivity extends AppCompatActivity {

    ////Variabeln zur Menünavigation
    private MySQLite mySQLite;
    private final int REQUESTCODE_ADD = 12; //AddEntryActivity
    private final int REQUESTCODE_SHOW = 13; //ShowEntryActivity
    private final int REQUESTCODE_EDIT = 14; //EditEntryActivity
    private final int REQUESTCODE_ADD_CATEGORY = 15; //AddCategoryActivity

    private int day;
    private int month;
    private int year;
    ///////////////////////////////

    private String entry; //Intake oder Outgo

    /*
   Wird am Anfang aufgerufen und stellt die übergebenen Daten dar
   -- !! Design wird ggf noch angepasst !!--
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_entries);

        TextView tv = (TextView) findViewById(R.id.textViewEntry);

        //Informationen auslesen
        Intent intent = getIntent();
        entry = intent.getStringExtra("entry");

        //Ausgaben ohner Einnahmen
        String text = " ";
        if(entry.equals("Intake")){
            text = "Alle Einnahmen des aktuellen Monats:";
            List<Intake> list = (List<Intake>) intent.getSerializableExtra("list");
            //Liste Sortieren
            Collections.sort(list, new Comparator<Intake>() {
                @Override
                public int compare(Intake o1, Intake o2) {
                    return o1.compareTo(o2);
                }
            });

            for(int i = 0; i < list.size(); i++){
                text = text + " '\n' "+list.get(i).toString();
            }
        }else{
            text = "Alle Ausgaben des aktuellen Monats:";
            List<Outgo> list = (List<Outgo>) intent.getSerializableExtra("list");
            //Liste Sortieren
            Collections.sort(list, new Comparator<Outgo>() {
                @Override
                public int compare(Outgo o1, Outgo o2) {
                    return o1.compareTo(o2);
                }
            });

            for(int i = 0; i < list.size(); i++){
                text = text + " '\n' "+list.get(i).toString();
            }
        }

        //Text anzeigen lassen
        tv.setText(text);
    }

    public void changeEntry(View view){
        EditText editTextId = (EditText) findViewById(R.id.textViewEditText);
        int valueId = -1;
        if(!TextUtils.isEmpty(editTextId.getText())) { //teste, ob überhaupt eine eingabe getätigt wurde
            valueId = Integer.parseInt(editTextId.getText().toString());
        }

        Intent intent = new Intent();
        intent.putExtra("entry",entry);
        intent.putExtra("id",valueId);
        setResult(RESULT_OK, intent);
        super.finish();
    }


    /*
    Sorgt dafür, dass das Menü dargestellt wird.
    Ohne Funktionalität
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.navigation_menu, menu);
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