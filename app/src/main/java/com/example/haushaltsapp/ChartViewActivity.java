package com.example.haushaltsapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.haushaltsapp.ChartPackage.RecyclerAdapter;
import com.example.haushaltsapp.ChartPackage.RecyclerAdapterIn;
import com.example.haushaltsapp.database.Category;
import com.example.haushaltsapp.database.Intake;
import com.example.haushaltsapp.database.MySQLite;
import com.example.haushaltsapp.database.Outgo;


//https://www.youtube.com/watch?v=vBxNDtyE_Co
public class ChartViewActivity extends  AppCompatActivity {

    ////Variabeln zur Menünavigation
    private MySQLite mySQLite;

    private int day;
    private int month;
    private int year;
    ///////////////////////////////
    private Spinner spinner;
    private TextView editTextDate;

    private ArrayList<Outgo> Outgolist;
    private ArrayList<Intake> Intakelist;
    private RecyclerView recyclerView;
    private RecyclerAdapter.RecyclerViewClickListener listener;
    private RecyclerAdapterIn.RecyclerViewClickListenerIn listenerIn;
    private String InOutSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_view);
        mySQLite = new MySQLite(this);
        Outgolist = mySQLite.getAllOutgo();
        Intakelist = mySQLite.getAllIntakes();

        //Aktuelles Datum anzeigen
        editTextDate = (TextView) findViewById(R.id.editTextDate);
        java.util.Calendar calender = Calendar.getInstance();
        SimpleDateFormat datumsformat = new SimpleDateFormat("dd.MM.yyyy");
        editTextDate.setText(datumsformat.format(calender.getTime()));

        //Spinner zu auswahl von Einnahmen oder Ausgaben
        spinner = findViewById(R.id.SpinnerInOut);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinner_TabelleInOut, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //ausgelesen, welcher Spinner gesetzt ist
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i==0)
                {
                    InOutSpinner ="Outgo";
                    setAddapertOut();
                }
                else if (i == 1)
                {
                    InOutSpinner ="Intake";
                    setAddapertIn();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        recyclerView = findViewById((R.id.chartRecyclerView));
    }


    //Ausgaben anzeigen in Recyclerview mit RecyclerAdapter
    private void setAddapertOut() {

        setOnClickListner();
        RecyclerAdapter adapter = new RecyclerAdapter(Outgolist, listener);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator( new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    //Einnahmen anzeigen in Recyclerview mit RecyclerAdapterIn
    private void setAddapertIn() {

        setOnClickListner();
        RecyclerAdapterIn adapter = new RecyclerAdapterIn(Intakelist, listenerIn);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator( new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    //Auswahl eines Eintrags in der Tabelle um zu bearbeiten
    private void setOnClickListner() {
        //Einnahmen
        listenerIn = new RecyclerAdapterIn.RecyclerViewClickListenerIn() {
            @Override
            public void onClick(View v, int position) {
                //Activity Edit entry aufrufen
              /*  Intent intenttoedit = new Intent(getApplicationContext(), EditEntryActivity.class);
                int Id =Intakelist.get(position).getId_PK();
                intenttoedit.putExtra("id", Id);
                intenttoedit.putExtra("entry", InOutSpinner);
                setResult(RESULT_OK, intenttoedit);
                startActivity(intenttoedit);
*/


                String name =Intakelist.get(position).getName();
                char[] checkÜbertrag = name.toCharArray();
                char[] check = new char[12];

                if (checkÜbertrag.length>12)
                {
                    int charnum= 12;
                    int i=0;
                    while (i <charnum)
                    {
                        char letter = checkÜbertrag[i];
                        check[i] = letter;
                        i++;
                    }
                    String Übertrag = new String(check);

                    if (Übertrag.equals("Übertrag vom"))
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ChartViewActivity.this );
                        builder.setTitle("Eintrag bearbeiten");
                        builder.setMessage("Überträge vom Vormonat können nicht bearbeitet werden!");
                        builder.setNeutralButton(android.R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    else {

                        AlertDialog.Builder builder = new AlertDialog.Builder(ChartViewActivity.this );
                        builder.setTitle("Eintrag bearbeiten");
                        builder.setMessage("Möchtest du den Eintrag " +name+ " bearbeiten?");
                        builder.setPositiveButton("Ja",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        //Activity Edit entry aufrufen
                                        Intent intenttoedit = new Intent(getApplicationContext(), EditEntryActivity.class);
                                        int Id =Intakelist.get(position).getId_PK();
                                        intenttoedit.putExtra("id", Id);
                                        intenttoedit.putExtra("entry", InOutSpinner);
                                        setResult(RESULT_OK, intenttoedit);
                                        startActivity(intenttoedit);
                                    }
                                });
                        builder.setNegativeButton("Nein",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }

                else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(ChartViewActivity.this );
                    builder.setTitle("Eintrag bearbeiten");
                    builder.setMessage("Möchtest du den Eintrag " +name+ " bearbeiten?");
                    builder.setPositiveButton("Ja",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    //Activity Edit entry aufrufen
                                    Intent intenttoedit = new Intent(getApplicationContext(), EditEntryActivity.class);
                                    int Id =Intakelist.get(position).getId_PK();
                                    intenttoedit.putExtra("id", Id);
                                    intenttoedit.putExtra("entry", InOutSpinner);
                                    setResult(RESULT_OK, intenttoedit);
                                    startActivity(intenttoedit);
                                }
                            });
                    builder.setNegativeButton("Nein",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }


            }
        };

        //Ausgaben
        listener = new RecyclerAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {

                String name =Outgolist.get(position).getName();
                char[] checkÜbertrag = name.toCharArray();
                char[] check = new char[12];

                if (checkÜbertrag.length>12)
                {
                    int charnum= 12;
                    int i=0;
                    while (i <charnum)
                    {
                        char letter = checkÜbertrag[i];
                        check[i] = letter;
                        i++;
                    }
                    String Übertrag = new String(check);

                    if (Übertrag.equals("Übertrag vom"))
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ChartViewActivity.this );
                        builder.setTitle("Eintrag bearbeiten");
                        builder.setMessage("Überträge vom Vormonat können nicht bearbeitet werden!");
                        builder.setNeutralButton(android.R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    else {

                        AlertDialog.Builder builder = new AlertDialog.Builder(ChartViewActivity.this );
                        builder.setTitle("Eintrag bearbeiten");
                        builder.setMessage("Möchtest du den Eintrag " +name+ " bearbeiten?");
                        builder.setPositiveButton("Ja",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        //Activity Edit entry aufrufen
                                        Intent intenttoedit = new Intent(getApplicationContext(), EditEntryActivity.class);
                                        int Id =Outgolist.get(position).getId_PK();
                                        intenttoedit.putExtra("id", Id);
                                        intenttoedit.putExtra("entry", InOutSpinner);
                                        setResult(RESULT_OK, intenttoedit);
                                        startActivity(intenttoedit);
                                    }
                                });
                        builder.setNegativeButton("Nein",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }

                else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(ChartViewActivity.this );
                    builder.setTitle("Eintrag bearbeiten");
                    builder.setMessage("Möchtest du den Eintrag " +name+ " bearbeiten?");
                    builder.setPositiveButton("Ja",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    //Activity Edit entry aufrufen
                                    Intent intenttoedit = new Intent(getApplicationContext(), EditEntryActivity.class);
                                    int Id =Outgolist.get(position).getId_PK();
                                    intenttoedit.putExtra("id", Id);
                                    intenttoedit.putExtra("entry", InOutSpinner);
                                    setResult(RESULT_OK, intenttoedit);
                                    startActivity(intenttoedit);
                                }
                            });
                    builder.setNegativeButton("Nein",
                                new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        };
    }

    //Button zum aktualisieren des Monats und laden der Tabelle
    public void changeMonth(View view)
    {
        if (InOutSpinner.equals("Outgo"))
        {
            Outgolist = mySQLite.getMonthOutgos(day,month,year);
            setAddapertOut();
        }
        else if( InOutSpinner.equals("Intake"))
        {
            Intakelist= mySQLite.getMonthIntakes(day,month,year);
            setAddapertIn();
        }
    }

    //Kalender zu auswahl des Monats, der angezeigt werden soll
    public  void openCalender(View dateview) {
        java.util.Calendar calender = java.util.Calendar.getInstance();
        year = calender.get(Calendar.YEAR);
        month = calender.get(Calendar.MONTH);
        day = calender.get(Calendar.DAY_OF_MONTH);

        //Kalender auf Deutsch umstellen
        Locale locale = new Locale("de");
        Locale.setDefault(locale);
        Resources res = this.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.locale = locale;
        res.updateConfiguration(config, res.getDisplayMetrics());

        DatePickerDialog dateDialog = new DatePickerDialog(com.example.haushaltsapp.ChartViewActivity.this,R.style.datePickerStyle, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {

                day = selectedDay;
                month = selectedMonth+1;
                year = selectedYear;

                if (day<10)
                {
                    if(month<10)
                    {
                        editTextDate.setText("0"+ selectedDay+".0"+month+"."+selectedYear);
                    }
                    else {
                        editTextDate.setText("0" + selectedDay + "." + month + "." + selectedYear);
                    }
                }
                else {
                    if(month<10)
                    {
                        editTextDate.setText(selectedDay+".0"+month+"."+selectedYear);
                    }
                    else {
                        editTextDate.setText(selectedDay + "." + month + "." + selectedYear);
                    }
                }
            }
        }, year, month, day);
        dateDialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.navigation_menu, menu);
        MenuItem item = menu.findItem(R.id.itemTableView);
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