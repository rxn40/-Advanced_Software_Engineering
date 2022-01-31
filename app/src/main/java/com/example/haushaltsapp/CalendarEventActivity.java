package com.example.haushaltsapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ActivityNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.app.DatePickerDialog;
import android.content.ContentUris;
import android.content.Intent;
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
import com.example.haushaltsapp.Database.MySQLite;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CalendarEventActivity extends AppCompatActivity {

    private MySQLite mySQLite;
    private TextView dateSelect;
    private ImageView calenderView;
    private EditText titleSelect;
    private EditText locationSelect;
    private EditText descriptionSelect;
    private Button addEvent;
    private Switch dailySwitch;

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

        //aktuelles Datum auslesen und an Textview übergeben
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        dateSelect.setText(dateFormat.format(calendar.getTime()));

        //Daten zur Übergaben an DatepickerDialog,übergeben. Ansonsten Start im Jahr 1970
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        //Übergabe der Daten an Kalender-Objekt und Setzen von Start und Endzeit
        calendar.set(year,month,day,8,0,0);
        startDateInMilliSec = calendar.getTimeInMillis();
        calendar.set(year,month,day,9,0,0);
        endDateInMilliSec =calendar.getTimeInMillis();

        //Auf deutsche Kalenderanzeige umstellen
        Locale locale = new Locale("de");
        Locale.setDefault(locale);
        Resources res = this.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.locale = locale;
        res.updateConfiguration(config, res.getDisplayMetrics());

        calenderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View dateView) {
                DatePickerDialog dateDialog = new DatePickerDialog(CalendarEventActivity.this,R.style.datePickerStyle, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        day = selectedDay;
                        month = selectedMonth;
                        year = selectedYear;

                        if (day<10)
                        {
                            if(month<9)
                            {
                                dateSelect.setText("0"+ selectedDay+".0"+(month + 1) +"."+selectedYear);
                            }
                            else {
                                dateSelect.setText("0" + selectedDay + "." + (month + 1)  + "." + selectedYear);
                            }
                        }
                        else {
                            if(month<9)
                            {
                                dateSelect.setText(selectedDay+".0"+(month + 1) +"."+selectedYear);
                            }
                            else {
                                dateSelect.setText(selectedDay + "." + (month + 1) + "." + selectedYear);
                            }
                        }

                        //Übergabe der Daten an Kalender-Objekt und Setzen von Start- und Endzeit
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
                    insertEvent(titleSelect.getText().toString(), locationSelect.getText().toString(),descriptionSelect.getText().toString(), dailySwitch.isChecked(), startDateInMilliSec, endDateInMilliSec);
                }else{
                    Toast.makeText(CalendarEventActivity.this, "Bitte wählen Sie einen Titel",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Intent zum Einfügen von Events in eine Kalender-Applikation
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

    //Intent zur Ansicht einer Kalender-Applikation
    public void viewEvent(View eventView) {
        Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
        builder.appendPath("time");
        ContentUris.appendId(builder, startDateInMilliSec);
        Intent viewEvent = new Intent(Intent.ACTION_VIEW).setData(builder.build());
        try {
            startActivity(viewEvent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(CalendarEventActivity.this, "Keine App auf Ihrem Handy unterstützt dieses Feature",
                    Toast.LENGTH_SHORT).show();
        }
    }

    //Menüaufruf
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.navigation_menu, menu);

        //Die aktuelle Activity im Menü ausblenden
        MenuItem item = menu.findItem(R.id.itemCalendar);
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