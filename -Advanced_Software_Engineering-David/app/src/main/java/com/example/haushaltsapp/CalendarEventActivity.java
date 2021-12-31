package com.example.haushaltsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.CalendarContract;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.haushaltsapp.database.Category;
import com.example.haushaltsapp.database.Intake;
import com.example.haushaltsapp.database.MySQLite;
import com.example.haushaltsapp.database.Outgo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class CalendarEventActivity extends AppCompatActivity {



    ////Variabeln zur Menünavigation
    private MySQLite mySQLite;
    ///////////////////////////////

    private int Storage_Permission_Code = 1;
    private TextView dateSelect;
    private ImageView calenderView;
    private EditText titleSelect;
    private EditText locationSelect;
    private EditText descriptionSelect;
    private Button addEvent;
    private Switch dailySwitch;

    private String titleValue;
    private  int year;
    private  int month;
    private  int day;
    private  long startDateInMilliSec;
    private  long endDateInMilliSec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_calendar_event);
        dateSelect = findViewById(R.id.calendarDate);
        calenderView = findViewById(R.id.calenderView);
        titleSelect = findViewById(R.id.titleSelect);
        locationSelect = findViewById(R.id.locationSelect);
        descriptionSelect=findViewById(R.id.descriptionSelect);
        addEvent    = findViewById(R.id.createEvent);
        dailySwitch = findViewById(R.id.switchDaily);

        //Überprüfen ob Daten in savedInstanceState gespeichert wurden
        if(savedInstanceState != null){
            titleSelect.setText(savedInstanceState.getString("titleText"));
            locationSelect.setText(savedInstanceState.getString("locationText"));
            descriptionSelect.setText(savedInstanceState.getString("descriptionText") );
        }

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        dateSelect.setText(year + "/" + (month + 1) + "/" + day);

        //Übergabe der Daten an Kalender-Objekt und Setzen von Start und Endzeit)
        calendar.set(year,month+1,day,8,0,0);
        startDateInMilliSec = calendar.getTimeInMillis();
        calendar.set(year,month+1,day,9,0,0);
        endDateInMilliSec =calendar.getTimeInMillis();

        //Auf deutsche Kalenderanzeige umstellen
        Locale locale = new Locale("de");
        Locale.setDefault(locale);
        Resources res = this.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.locale = locale;
        res.updateConfiguration(config, res.getDisplayMetrics());

        //Setzen von Listener auf dem Kalender Symbol
        calenderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View dateView) {
                DatePickerDialog dateDialog = new DatePickerDialog(CalendarEventActivity.this,R.style.datePickerStyle, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        day = selectedDay;
                        month = selectedMonth;
                        year = selectedYear;

                        //Addition bei Monat von 1, Index beginnend bei 0
                        dateSelect.setText(selectedYear + "/" + (selectedMonth + 1) + "/" + selectedDay);

                        //Übergabe der Daten an Kalender-Objekt und Setzen von Start und Endzeit)
                        calendar.set(year,month,day,8,0,0);
                        startDateInMilliSec = calendar.getTimeInMillis();
                        calendar.set(year,month,day,9,0,0);
                        endDateInMilliSec =calendar.getTimeInMillis();
                    }
                }, year, month, day);
                dateDialog.show();
            }
        });


        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View insertButtonView) {

                if(!titleSelect.getText().toString().isEmpty()){
                    if (ContextCompat.checkSelfPermission(CalendarEventActivity.this, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
                    }else{
                        requestWritePermission();
                    }
                    insertEvent(titleSelect.getText().toString(), locationSelect.getText().toString(),descriptionSelect.getText().toString(), dailySwitch.isChecked(), startDateInMilliSec, endDateInMilliSec);
                }else{
                    Toast.makeText(CalendarEventActivity.this, "Bitte wählen Sie einen Titel",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Intent zum Einfügen von Events in einer Kalender Applikation
    public void insertEvent(String title, String location, String description, boolean isAllDay, long beginTime, long endTime) {
        Intent insertEvent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, title)
                .putExtra(CalendarContract.Events.EVENT_LOCATION, location)
                .putExtra(CalendarContract.Events.DESCRIPTION, description)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime)
                .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, isAllDay);


        try {
            startActivity(insertEvent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(CalendarEventActivity.this, "Keine App auf Ihrem Handy unterstützt dieses Feature",
                    Toast.LENGTH_SHORT).show();
        }
    }

    //Intent zur Ansicht einer Kalender Applikation
    public void viewEvent(View eventView) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
        }else{
            requestReadPermission();
        }
        //
        long timeInMilliSec = System.currentTimeMillis();
        Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
        builder.appendPath("time");
        ContentUris.appendId(builder, timeInMilliSec);
        Intent viewEvent = new Intent(Intent.ACTION_VIEW).setData(builder.build());
        try {
            startActivity(viewEvent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(CalendarEventActivity.this, "Keine App auf Ihrem Handy unterstützt dieses Feature",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.navigation_menu, menu);

        //Die aktuelle Activity im Menü ausblenden
        MenuItem item = menu.findItem(R.id.itemCalendar);
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
                int day = 0;  //Yvette
                int month = 0;  //Yvette
                int year = 0;  //Yvette
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


    public void requestWritePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_CALENDAR)){
            new AlertDialog.Builder(this)
                    .setTitle("Erlaubnis wird benötigt!")
                    .setMessage("Bestätigen Sie diese Erlaubnis um Einträge Ihrem Kalender hinzuzufügen")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(CalendarEventActivity.this, new String[]{Manifest.permission.WRITE_CALENDAR},Storage_Permission_Code);
                        }
                    })
                    .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALENDAR},Storage_Permission_Code);
        }
    }

    public void requestReadPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_CALENDAR)){
            new AlertDialog.Builder(this)
                    .setTitle("Erlaubnis benötigt!")
                    .setMessage("Bestätigen Sie diese Erlaubnis Ihrem Kalender anzusehen")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(CalendarEventActivity.this, new String[]{Manifest.permission.READ_CALENDAR},Storage_Permission_Code);
                        }
                    })
                    .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALENDAR},Storage_Permission_Code);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Storage_Permission_Code) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "Erlaubnis erteilt!", Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(this, "Erlaubnis verweigert!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putString("titleText", titleSelect.getText().toString());
        state.putString("descriptionText", descriptionSelect.getText().toString());
        state.putString("locationText", locationSelect.getText().toString());

    }

    @Override
    public void onRestoreInstanceState(Bundle state) {
          super.onRestoreInstanceState(state);
          state.putString("titleText", titleSelect.getText().toString());
          state.putString("descriptionText", descriptionSelect.getText().toString());
          state.putString("locationText", locationSelect.getText().toString());
    }
}