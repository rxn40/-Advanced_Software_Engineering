package com.example.haushaltsapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.haushaltsapp.ChartPackage.RecyclerAdapter;
import com.example.haushaltsapp.DeleteCategoryPackage.deleteCategorieAdapter;
import com.example.haushaltsapp.ToDoListPackage.SwipeHandler;
import com.example.haushaltsapp.database.Category;
import com.example.haushaltsapp.database.Intake;
import com.example.haushaltsapp.database.MySQLite;
import com.example.haushaltsapp.database.Outgo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.ArrayList;

public class DeleteCategoryActivity extends AppCompatActivity {

    ////Variabeln zur Menünavigation
    private MySQLite mySQLite;

    private int day;
    private int month;
    private int year;
    ///////////////////////////////

    private RecyclerView recyclerView;
    private ArrayList<Category> CategorieList;
    private deleteCategorieAdapter.deleteCategorieClickListener listener;
    private deleteCategorieAdapter deleteAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_categorie);
        mySQLite = new MySQLite(this);
        CategorieList = mySQLite.getAllCategory();
        recyclerView = findViewById(R.id.deleteCategorieRecyclerView);

        setAdapter();
    }

    //Anzeige der Kategorien
    private void setAdapter()
    {
        setOnClickListner();

        deleteCategorieAdapter adapter = new deleteCategorieAdapter(CategorieList,listener);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator( new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        deleteAdapter = new deleteCategorieAdapter(CategorieList,listener);
        recyclerView.setAdapter(deleteAdapter);

    }

    //Auswahl einer Kategorie um diese zu löschen
    private void setOnClickListner(){

        listener = new deleteCategorieAdapter.deleteCategorieClickListener() {
            @Override
            public void onClick(View v, int position) {

                String Categorie = CategorieList.get(position).getName_PK();

                //Sonstiges kann nicht gelöscht werden
                //Melung bringen über AlertDiagog
                if (Categorie.equals("Sonstiges"))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(DeleteCategoryActivity.this );
                    builder.setTitle("Kategorie löschen");
                    builder.setMessage("Sonstiges kann nicht gelöscht werden");
                    builder.setNeutralButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

                //Kategorie löschen
                //Melung bringen über AlertDiagog
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(DeleteCategoryActivity.this );
                    builder.setTitle("Kategorie löschen");
                    builder.setMessage("Möchten Sie die Kategorie " +Categorie+ " löschen?");
                    builder.setPositiveButton("Ja",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    //suchen nach allen Einträgen mit Categorie und änderen der Categorie zu Sonstiges
                                    mySQLite.ChangeCategorietoSonstiges(Categorie);
                                    //Löschen der Kategorie
                                    mySQLite.deleteCategoryByName(Categorie);
                                    deleteAdapter.deleteCategorie(position);
                                    Toast toast = Toast.makeText(getApplicationContext(),Categorie+" wird gelöscht",Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            });
                    builder.setNegativeButton("Abbruch",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast toast = Toast.makeText(getApplicationContext(),Categorie+" wird nicht gelöscht",Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.navigation_menu, menu);
        MenuItem item = menu.findItem(R.id.itemDeleteCategory);
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