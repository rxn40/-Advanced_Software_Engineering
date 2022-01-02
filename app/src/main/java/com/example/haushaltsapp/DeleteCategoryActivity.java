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
                //Toast mit Meldung

                if (Categorie.equals("Sonstiges"))
                {
                    Toast toast = Toast.makeText(getApplicationContext(),"Sonstiges kann nicht gelöscht werden",Toast.LENGTH_SHORT);
                    toast.show();
                }

                //Kategorie löschen
                //Melung bringen um zu bestätigen, das gelöscht werden soll
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(DeleteCategoryActivity.this );
                    builder.setTitle("Kategorie Löschen");
                    builder.setMessage("Möchten Sie diese Kategorie löschen?");
                    builder.setPositiveButton("Ja",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    //suchen nach allen einträgen mit Categorie änderen der Categorie zu sonstiges
                                    mySQLite.ChangeCategorietoSonstiges(Categorie);
                                    //Löschen der Kategorie
                                    mySQLite.deleteCategoryByName(Categorie);
                                    deleteAdapter.deleteCategorie(position);
                                    Toast toast = Toast.makeText(getApplicationContext(),Categorie+" wurde gelöscht",Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            });
                    builder.setNegativeButton("Abbruch",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast toast = Toast.makeText(getApplicationContext(),Categorie+" Wird nicht gelöscht",Toast.LENGTH_SHORT);
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