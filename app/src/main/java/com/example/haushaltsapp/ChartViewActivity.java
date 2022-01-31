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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.haushaltsapp.ChartPackage.RecyclerAdapterOut;
import com.example.haushaltsapp.ChartPackage.RecyclerAdapterIn;
import com.example.haushaltsapp.Database.Intake;
import com.example.haushaltsapp.Database.MySQLite;
import com.example.haushaltsapp.Database.Outgo;

public class ChartViewActivity extends  AppCompatActivity {

    private MySQLite mySQLite;

    private int day;
    private int month;
    private int year;

    private Spinner spinner;
    private TextView editTextDate;

    private ArrayList<Outgo> outgoList;
    private ArrayList<Intake> intakeList;
    private RecyclerView recyclerView;
    private RecyclerAdapterOut.RecyclerViewClickListenerOut listenerOut;
    private RecyclerAdapterIn.RecyclerViewClickListenerIn listenerIn;
    private String inOutSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_view);

        //Erstellung Datenbank-Objekt und Auslesen der Daten
        mySQLite = new MySQLite(this);
        outgoList = mySQLite.getAllOutgoes();
        intakeList = mySQLite.getAllIntakes();

        //Aktuelles Datum auslesen und anzeigen
        editTextDate = (TextView) findViewById(R.id.editTextDate);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        editTextDate.setText(dateFormat.format(calendar.getTime()));

        Calendar calender = Calendar.getInstance();
        year = calender.get(Calendar.YEAR);
        month = calender.get(Calendar.MONTH)+1; //month hier eins weniger, deshalb +1
        day = calender.get(Calendar.DAY_OF_MONTH);

        //Spinner zur Auswahl von Einnahmen oder Ausgaben
        spinner = findViewById(R.id.SpinnerInOut);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinner_TabelleInOut, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //Abfrage, welcher Spinner gesetzt ist
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i==0) {
                    inOutSpinner ="Outgo";
                    setAdapterOut();
                } else if (i == 1) {
                    inOutSpinner ="Intake";
                    setAdapterIn();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        recyclerView = findViewById((R.id.chartRecyclerView));
    }


    //Ausgaben anzeigen in Recyclerview mit RecyclerAdapterOut
    private void setAdapterOut() {
        setOnClickListener();
        RecyclerAdapterOut adapter = new RecyclerAdapterOut(outgoList, listenerOut);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator( new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    //Einnahmen anzeigen in Recyclerview mit RecyclerAdapterIn
    private void setAdapterIn() {
        setOnClickListener();
        RecyclerAdapterIn adapter = new RecyclerAdapterIn(intakeList, listenerIn);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator( new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }


    //Auswahl eines Eintrags in der Tabelle, um zu bearbeiten
    private void setOnClickListener() {
        //Einnahmen
        listenerIn = new RecyclerAdapterIn.RecyclerViewClickListenerIn() {
            @Override
            public void onClick(View v, int position) {
                //Activity Edit entry aufrufen
                String name = intakeList.get(position).getName();
                char[] checkTransfer = name.toCharArray();

                if (checkTransfer.length>12)
                {
                    String str= name.substring(0,12);
                    if (str.equals("Übertrag vom")) {
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
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ChartViewActivity.this );
                        builder.setTitle("Eintrag bearbeiten");
                        builder.setMessage("Möchten Sie den Eintrag " +name+ " bearbeiten?");
                        builder.setPositiveButton("Ja",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        //Activity Edit entry aufrufen
                                        Intent intentToEdit = new Intent(getApplicationContext(), EditEntryActivity.class);
                                        int id = intakeList.get(position).getId_PK();
                                        intentToEdit.putExtra("id", id);
                                        intentToEdit.putExtra("entry", inOutSpinner);
                                        setResult(RESULT_OK, intentToEdit);
                                        startActivity(intentToEdit);
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
                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(ChartViewActivity.this );
                    builder.setTitle("Eintrag bearbeiten");
                    builder.setMessage("Möchten Sie den Eintrag " +name+ " bearbeiten?");
                    builder.setPositiveButton("Ja",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    //Activity Edit entry aufrufen
                                    Intent intentToEdit = new Intent(getApplicationContext(), EditEntryActivity.class);
                                    int id = intakeList.get(position).getId_PK();
                                    intentToEdit.putExtra("id", id);
                                    intentToEdit.putExtra("entry", inOutSpinner);
                                    setResult(RESULT_OK, intentToEdit);
                                    startActivity(intentToEdit);
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
        listenerOut = new RecyclerAdapterOut.RecyclerViewClickListenerOut() {
            @Override
            public void onClick(View v, int position) {

                String name = outgoList.get(position).getName();
                char[] checkTransfer = name.toCharArray();

                if (checkTransfer.length>12)
                {
                    String str= name.substring(0,12);
                    if (str.equals("Übertrag vom")) {
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
                    } else {

                        AlertDialog.Builder builder = new AlertDialog.Builder(ChartViewActivity.this );
                        builder.setTitle("Eintrag bearbeiten");
                        builder.setMessage("Möchten Sie den Eintrag " +name+ " bearbeiten?");
                        builder.setPositiveButton("Ja",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        //Activity Edit entry aufrufen
                                        Intent intentToEdit = new Intent(getApplicationContext(), EditEntryActivity.class);
                                        int id = outgoList.get(position).getId_PK();
                                        intentToEdit.putExtra("id", id);
                                        intentToEdit.putExtra("entry", inOutSpinner);
                                        setResult(RESULT_OK, intentToEdit);
                                        startActivity(intentToEdit);
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
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChartViewActivity.this );
                    builder.setTitle("Eintrag bearbeiten");
                    builder.setMessage("Möchten Sie den Eintrag " +name+ " bearbeiten?");
                    builder.setPositiveButton("Ja",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    //Activity Edit entry aufrufen
                                    Intent intentToEdit = new Intent(getApplicationContext(), EditEntryActivity.class);
                                    int Id = outgoList.get(position).getId_PK();
                                    intentToEdit.putExtra("id", Id);
                                    intentToEdit.putExtra("entry", inOutSpinner);
                                    setResult(RESULT_OK, intentToEdit);
                                    startActivity(intentToEdit);
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

    //Button zum Aktualisieren des Monats und Laden der Tabelle
    public void changeMonth(View view)
    {
        if (inOutSpinner.equals("Outgo")) {
            outgoList = mySQLite.getMonthOutgoes(day,month,year);
            setAdapterOut();
        } else if( inOutSpinner.equals("Intake")) {
            intakeList = mySQLite.getMonthIntakes(day,month,year);
            setAdapterIn();
        }
    }

        //Kalender zur Auswahl des Monats, der angezeigt werden soll
        public  void openCalender(View dateView) {
        Calendar calender = Calendar.getInstance();
        year = calender.get(Calendar.YEAR);
        month = calender.get(Calendar.MONTH);
        day = calender.get(Calendar.DAY_OF_MONTH);
          //  calender.set(year,(month+1),day);

        //Kalenderanzeige auf Deutsch umstellen
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

    //Menüaufruf
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.navigation_menu, menu);
        MenuItem item = menu.findItem(R.id.itemTableView);
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