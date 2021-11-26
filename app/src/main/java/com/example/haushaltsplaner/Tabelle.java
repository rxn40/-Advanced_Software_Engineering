package com.example.haushaltsplaner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class Tabelle extends  AppCompatActivity {

    private static final String TAG = "ActivityTabelle";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabelle);

        Log.d(TAG, "onCreate: Started.");
        ListView mListView = (ListView) findViewById(R.id.listView);

        //Erzeugen die AUsgabe objects
        //Hier die Daten später aus Datenbank holen
        //hier alle Daten als String
        class_Ausgabe T1 = new class_Ausgabe("Tanken","54","11.11.2021");
        class_Ausgabe T2 = new class_Ausgabe("Einkaufen Lidl","24","13.11.2021");
        class_Ausgabe T3 = new class_Ausgabe("Handy","3.99","01.11.2021");
        class_Ausgabe T4 = new class_Ausgabe("Penny","10","20.11.2021");
        class_Ausgabe T5 = new class_Ausgabe("Penny EInk.","34.87","21.11.2021");


        //Add the Person objects to an ArrayList
        //kann hier die Liste vielleicht gleich aus der Datenbank geholt werden???
        ArrayList<class_Ausgabe> AusgabeList = new ArrayList<>();
        AusgabeList.add(T1);
        AusgabeList.add(T2);
        AusgabeList.add(T3);
        AusgabeList.add(T4);
        AusgabeList.add(T5);
        AusgabeList.add(T1);
        AusgabeList.add(T2);
        AusgabeList.add(T4);
        AusgabeList.add(T3);
        AusgabeList.add(T5);
        AusgabeList.add(T1);
        AusgabeList.add(T1);
        AusgabeList.add(T3);
        AusgabeList.add(T4);
        AusgabeList.add(T2);

        AusgabeListAdapter adapter = new AusgabeListAdapter(this, R.layout.activity_adapter_list_view, AusgabeList);
        //mListView.setAdapter((ListAdapter) adapter);
        mListView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tabelle_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.itemStartseite:
                Intent switchToMain = new Intent(this, MainActivity.class);
                startActivity(switchToMain);
                return true;

            case R.id.itemEinnahmenAusgaben:
                Intent switchToAddEntry = new Intent(this, AddEntryActivity.class);
                startActivity(switchToAddEntry);
                return true;

            case R.id.itemBudgetLimit:
                Intent switchToBudgetLimit = new Intent(this, BudgetLimit.class);
                startActivity(switchToBudgetLimit);
                return true;

            case R.id.itemDiagrammansicht:
                Intent switchToEditDiagramView = new Intent(this, EditDiagramView.class);
                startActivity(switchToEditDiagramView);
                return true;

            case R.id.itemKalender:
                Intent switchToCalendar = new Intent(this, Calendar.class);
                startActivity(switchToCalendar);
                return true;

            case R.id.itemTodoListe:
                Intent switchToDoList = new Intent(this, ToDoList.class);
                startActivity(switchToDoList);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}