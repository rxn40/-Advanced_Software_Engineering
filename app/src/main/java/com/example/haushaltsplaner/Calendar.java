package com.example.haushaltsplaner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
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

public class Calendar extends AppCompatActivity {
    private static final int MY_CAL_WRITE_REQ = 1; //Wird nie verwendet
    private int Storage_Permission_Code = 1;
    TextView dateSelect;
    ImageView calenderView;
    EditText titleSelect;
    EditText locationSelect;
    EditText descriptionSelect;
    int year;
    int month;
    int day;
    long startdateInMilliSec;
    long enddateInMilliSec;
    Button addEvent;
    Switch dailySwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        dateSelect = findViewById(R.id.dateSelect);
        calenderView = findViewById(R.id.calenderView);
        titleSelect = findViewById(R.id.titleSelect);
        locationSelect = findViewById(R.id.locationSelect);
        descriptionSelect=findViewById(R.id.descriptionSelect);
        addEvent    = findViewById(R.id.createEvent);
        dailySwitch = findViewById(R.id.switch1);

        //java.util notwendig, da Klasse selbst Calendar heißt und somit kein import java.util.Calendar angewendet werden kann
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        year = calendar.get(java.util.Calendar.YEAR);
        month = calendar.get(java.util.Calendar.MONTH);
        day = calendar.get(java.util.Calendar.DAY_OF_MONTH);
        dateSelect.setText(year + "/" + (month + 1) + "/" + day);

        calenderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dateDialog = new DatePickerDialog(Calendar.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        day = selectedDay;
                        month = selectedMonth;
                        year = selectedYear;
                        dateSelect.setText(selectedYear + "/" + (selectedMonth + 1) + "/" + selectedDay);   //month +1, starts with index 0
                        calendar.set(year,month,day,8,0,0);
                        startdateInMilliSec = calendar.getTimeInMillis();
                        calendar.set(year,month,day,9,0,0);
                        enddateInMilliSec =calendar.getTimeInMillis();
                    }
                }, year, month, day);
                dateDialog.show();
            }
        });

        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!titleSelect.getText().toString().isEmpty()&&!locationSelect.getText().toString().isEmpty()){
                    if (ContextCompat.checkSelfPermission(Calendar.this, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
                    }else{
                        requestWritePermission();
                    }
                    insertEvent(titleSelect.getText().toString(), locationSelect.getText().toString(),descriptionSelect.getText().toString(), dailySwitch.isChecked(), startdateInMilliSec, enddateInMilliSec);
                }else{
                    Toast.makeText(Calendar.this, "Please fill in all the fields",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Intent zum Einfügen von Events in eine Kalender Applikation
    public void insertEvent(String title, String location, String description, boolean value, long begin, long end) {
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, title)
                .putExtra(CalendarContract.Events._ID,2)
                .putExtra(CalendarContract.Events.EVENT_LOCATION, location)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, begin)
                .putExtra(CalendarContract.Events.DESCRIPTION, description)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end)
                .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, value);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(Calendar.this, "There is no app that can support this action",
                    Toast.LENGTH_SHORT).show();
        }
    }

    //Intent zur Ansicht von Events in eine Kalender Applikation
    public void viewEvent(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
             }else{
                   requestReadPermission();
             }

        long startMillis = System.currentTimeMillis();
        Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
        builder.appendPath("time");
        ContentUris.appendId(builder, startMillis);
        Intent intent = new Intent(Intent.ACTION_VIEW).setData(builder.build());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.calendar_menu, menu);
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

            case R.id.itemTabelle:
                Intent switchToChart = new Intent(this, Tabelle.class);
                startActivity(switchToChart);
                return true;

            case R.id.itemTodoListe:
                Intent switchToToDoList = new Intent(this, ToDoList.class);
                startActivity(switchToToDoList);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void requestWritePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_CALENDAR)){
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed to insert events to your calendar")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(Calendar.this, new String[]{Manifest.permission.WRITE_CALENDAR},Storage_Permission_Code);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed to view your calendar")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(Calendar.this, new String[]{Manifest.permission.READ_CALENDAR},Storage_Permission_Code);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

}