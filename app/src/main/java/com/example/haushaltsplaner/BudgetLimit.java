package com.example.haushaltsplaner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


public class BudgetLimit extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_limit);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.budgetlimit_menu, menu);
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

            case R.id.itemDiagrammansicht:
                Intent switchToEditDiagramView = new Intent(this, EditDiagramView.class);
                startActivity(switchToEditDiagramView);
                return true;

            case R.id.itemTabelle:
                Intent switchToChart = new Intent(this, Tabelle.class);
                startActivity(switchToChart);
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