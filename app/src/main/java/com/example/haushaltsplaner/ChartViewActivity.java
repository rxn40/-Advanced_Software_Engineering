package com.example.haushaltsplaner;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ChartViewActivity extends  AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_view);

        ListView mListView = (ListView) findViewById(R.id.listView);

        Intent intent = getIntent();
        ArrayList<Outgo> ListeOut = (ArrayList<Outgo>) intent.getSerializableExtra("list");
        //zum TEsten ohne Datenbankzugriff
        //Erzeugen die AUsgabe objects
        Expenditures T1 = new Expenditures("Tanken", "54", "11.11.2021");
        Expenditures T2 = new Expenditures("Einkaufen Lidl", "24", "13.11.2021");
        Expenditures T3 = new Expenditures("Handy", "3.99", "01.11.2021");
        Expenditures T4 = new Expenditures("Penny", "10", "20.11.2021");
        Expenditures T5 = new Expenditures("Penny EInk.", "34.87", "21.11.2021");

        //füllen der Array List
        ArrayList<Expenditures> AusgabeList = new ArrayList<>();
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

        //Zum Test ohne Datenbank
        ExpendituresListAdapter adapter = new ExpendituresListAdapter(this, R.layout.activity_adapter_list_view, AusgabeList);
        mListView.setAdapter(adapter);

       /* Intent switchOutgoListAdapter =new Intent(this, OutgoListAdapter.A.class);
        ArrayList<Outgo> outgoes1 = ListeOut;
        switchOutgoListAdapter.putExtra("list",(Serializable) outgoes1);

        OutgoListAdapter adapter = new OutgoListAdapter(this,R.layout.activity_adapter_list_view,ListeOut);
        mListView.setAdapter(adapter);
*/

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chart_menu, menu);
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
                Intent switchToBudgetLimit = new Intent(this, BudgetLimitActivity.class);
                startActivity(switchToBudgetLimit);
                return true;

            case R.id.itemDiagrammansicht:
                Intent switchToDiagramView = new Intent(this, DiagramViewActivity.class);
                startActivity(switchToDiagramView);
                return true;

            case R.id.itemKalender:
                Intent switchToCalendar = new Intent(this, CalendarEventActivity.class);
                startActivity(switchToCalendar);
                return true;

            case R.id.itemTodoListe:
                Intent switchToToDoList = new Intent(this, ToDoListActivity.class);
                startActivity(switchToToDoList);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}