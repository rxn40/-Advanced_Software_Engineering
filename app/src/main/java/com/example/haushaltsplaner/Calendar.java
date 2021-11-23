package com.example.haushaltsplaner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
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


    private static final int MY_CAL_WRITE_REQ = 1;
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
    long prev_id;
    long event_id;
    Button addEvent;
    Switch dailySwitch;
    ContentResolver contenRes;

    //@Override
    //public void onResume() {
    //    super.onResume();
    //
    //    long prev_id = getLastEventId(getContentResolver());
    //
    //    // if prev_id == mEventId, means there is new events created
    //    // and we need to insert new events into local sqlite database.
    //    if (prev_id == mEventID) {
    //        // do database insert
    //    }
    //}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.calendar_menu, menu);
        return true;
    }

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

        // Hier Code für CalenderView, kein erstellen von Events, nur Auslesen von Tag,Monat,Jahr
        java.util.Calendar calendar = java.util.Calendar.getInstance();

        //Standard: akutelles Datum übergeben
        year = calendar.get(java.util.Calendar.YEAR);
        month = calendar.get(java.util.Calendar.MONTH);
        day = calendar.get(java.util.Calendar.DAY_OF_MONTH);
        dateSelect.setText(year + "/" + (month + 1) + "/" + day);

        calenderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                year = calendar.get(java.util.Calendar.YEAR);
                month = calendar.get(java.util.Calendar.MONTH);
                day = calendar.get(java.util.Calendar.DAY_OF_MONTH);
                DatePickerDialog dateDialog = new DatePickerDialog(Calendar.this, new DatePickerDialog.OnDateSetListener() {
                    @Override

                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        day = selectedDay;
                        month = selectedMonth;
                        year = selectedYear;
                        dateSelect.setText(selectedYear + "/" + (selectedMonth + 1) + "/" + selectedDay);              //mont +1, starts with index 0
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

    //Ab hier: Erstellung, Editieren, Löschen... usw. von Events in Kalender
    public void insertEvent(String title, String location, String description, boolean value, long begin, long end) {
        event_id = getNewEventId(contenRes = getContentResolver());
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

    public void viewEvent(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
        }else{requestReadPermission();}

// View Events with particular date
        long startMillis = System.currentTimeMillis();
        Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
        builder.appendPath("time");
        ContentUris.appendId(builder, startMillis);
        Intent intent = new Intent(Intent.ACTION_VIEW)
                .setData(builder.build());
        startActivity(intent);

// Instead View Events by EventId
//        long eventID = 81;
//        Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
//        Intent intent = new Intent(Intent.ACTION_VIEW)
//                .setData(uri);
//        startActivity(intent);
    }

    public void editEvent(View view) {
//Use an intent to edit an event
        prev_id = getLastEventId(getContentResolver());
        if (prev_id != event_id) {
            Toast.makeText(Calendar.this, "No new event was created",
                    Toast.LENGTH_SHORT).show();
        } else {
            Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, event_id);
            Intent intent = new Intent(Intent.ACTION_EDIT)
                    .setData(uri)
                    .putExtra(CalendarContract.Events.TITLE, "My New Title");

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(Calendar.this, "There is no app that can support this action",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Methode zum Aufrufen des Calendar-Menus
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.item1:
                Intent switchActivityIntent = new Intent(this, MainActivity.class);
                // If data is need to be sent between activities
                //      EditText editText = (EditText) findViewById(R.id.editTextTextPersonName);
                //      String message = editText.getText().toString();
                //      intent.putExtra(EXTRA_MESSAGE, message);
                //Rufe Activity "Kalender" auf
                startActivity(switchActivityIntent);
                return true;

            case R.id.item2:
                Toast.makeText(this,"Transaktionen ausgewählt",Toast.LENGTH_SHORT).show();
                //Erstellung Intent mit Empfänger, hier BudgetLimit Klasse
                //Intent switchActivityIntent = new Intent(this, BudgetLimit.class);
                // If data is need to be sent between activities
                //      EditText editText = (EditText) findViewById(R.id.editTextTextPersonName);
                //      String message = editText.getText().toString();
                //      intent.putExtra(EXTRA_MESSAGE, message);
                //Rufe Activity "Budget Limit" auf
                //startActivity(switchActivityIntent);
                // return true;


            case R.id.subitem1:
                Toast.makeText(this,"Einnahmen ausgewählt",Toast.LENGTH_SHORT).show();
                //Intent switchActivityIntent = new Intent(this, Einnahmen.class);
                // If data is need to be sent between activities
                //      EditText editText = (EditText) findViewById(R.id.editTextTextPersonName);
                //      String message = editText.getText().toString();
                //      intent.putExtra(EXTRA_MESSAGE, message);
                //startActivity(switchActivityIntent);
                return true;
            case R.id.subitem2:
                Toast.makeText(this,"Ausgaben ausgewählt",Toast.LENGTH_SHORT).show();
                //Intent switchActivityIntent = new Intent(this, Ausgaben.class);
                // If data is need to be sent between activities
                //      EditText editText = (EditText) findViewById(R.id.editTextTextPersonName);
                //      String message = editText.getText().toString();
                //      intent.putExtra(EXTRA_MESSAGE, message);
                //startActivity(switchActivityIntent);
                return true;

            case R.id.item3:
                Toast.makeText(this,"Budget Limit ausgewählt",Toast.LENGTH_SHORT).show();
                //Intent switchActivityIntent = new Intent(this, Diagramm.class);
                // If data is need to be sent between activities
                //      EditText editText = (EditText) findViewById(R.id.editTextTextPersonName);
                //      String message = editText.getText().toString();
                //      intent.putExtra(EXTRA_MESSAGE, message);
                //Rufe Activity "Diagramm" auf
                //startActivity(switchActivityIntent);
                return true;

            case R.id.item4:
                Toast.makeText(this,"Diagramm ausgewählt",Toast.LENGTH_SHORT).show();
                //Intent switchActivityIntent = new Intent(this, Diagramm.class);
                // If data is need to be sent between activities
                //      EditText editText = (EditText) findViewById(R.id.editTextTextPersonName);
                //      String message = editText.getText().toString();
                //      intent.putExtra(EXTRA_MESSAGE, message);
                //Rufe Activity "Diagramm" auf
                //startActivity(switchActivityIntent);
                return true;
            case R.id.item5:
                //Erstellung Intent mit Empfänger, hier To-Do Liste Klasse
                Toast.makeText(this,"To-Do Liste ausgewählt",Toast.LENGTH_SHORT).show();
                //Rufe Klasse "To-Do Liste" auf
                //Intent switchActivityIntent = new Intent(this, ToDoList.class);
                // If data is need to be sent between activities
                //      EditText editText = (EditText) findViewById(R.id.editTextTextPersonName);
                //      String message = editText.getText().toString();
                //      intent.putExtra(EXTRA_MESSAGE, message);
                //Rufe Activity "To-Do Liste" auf
                //startActivity(switchActivityIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public static long getNewEventId(ContentResolver cr) {
        Cursor cursor = cr.query(CalendarContract.Events.CONTENT_URI, new String [] {"MAX(_id) as max_id"}, null, null, "_id");
        cursor.moveToFirst();
        long max_val = cursor.getLong(cursor.getColumnIndexOrThrow("max_id"));
        return max_val+1;
    }

    public static long getLastEventId(ContentResolver cr) {
        Cursor cursor = cr.query(CalendarContract.Events.CONTENT_URI, new String [] {"MAX(_id) as max_id"}, null, null, "_id");
        cursor.moveToFirst();
        long max_val = cursor.getLong(cursor.getColumnIndexOrThrow("max_id"));
        return max_val;
    }

    public void requestWritePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_CALENDAR)){
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
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
                    .setMessage("This permission is needed because of this and that")
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

//    public static final String[] EVENT_PROJECTION = new String[]{
//            CalendarContract.Calendars._ID,                           // 0
//            CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
//            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
//            CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
//    };
//
//    private static final int PROJECTION_ID_INDEX = 0;
//    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
//    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
//    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

//    public void queryCalendar(View view) {
//        Cursor cursor = getContentResolver().query(Uri.parse("content://com.android.calendar/calendars"),
//                new String[]{"_id", "calendar_displayName"}, null, null, null);
//        // Get calendar names
//        Log.i("@calendar","Cursor count " + cursor.getCount());
//        if (cursor.getCount() > 0) {
//            cursor.moveToFirst();
//            String[] calendarNames = new String[cursor.getCount()];
//            // Get calendars id
//            int calendarIds[] = new int[cursor.getCount()];
//            for (int i = 0; i < cursor.getCount(); i++) {
//                calendarIds[i] = cursor.getInt(0);
//                calendarNames[i] = cursor.getString(1);
//                Log.i("@calendar","Calendar Name : " + calendarNames[i]);
//                cursor.moveToNext(); }
//        } else {
//            Log.e("@calendar","No calendar found in the device");
//        }
//    }

//    public void modifyCalendar(View view) {
//        //Modify a calendar
//        final String DEBUG_TAG = "MyActivity";
//        long calID = 2;
//        ContentValues values = new ContentValues();
//        // The new display name for the calendar
//        values.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, "Trevor's Calendar");
//        Uri updateUri = ContentUris.withAppendedId(CalendarContract.Calendars.CONTENT_URI, calID);
//        int rows = getContentResolver().update(updateUri, values, null, null);
//        Log.i(DEBUG_TAG, "Rows updated: " + rows);
//    }

//    public void addReminders(View view, ContentResolver cr) {
//        long eventID = 221;
//        cr = getContentResolver();
//        ContentValues values = new ContentValues();
//        values.put(CalendarContract.Reminders.MINUTES, 15);
//        values.put(CalendarContract.Reminders.EVENT_ID, eventID);
//        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
//        Uri uri = cr.insert(CalendarContract.Reminders.CONTENT_URI, values);
//    }

//   public void deleteEvent(View view,ContentResolver cr) {
//        final String DEBUG_TAG = "MyActivity";
//        long eventID = 81;
//        cr = getContentResolver();
//        Uri deleteUri = null;
//        deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
//        int rows = cr.delete(deleteUri, null, null);
//        Log.i(DEBUG_TAG, "Rows deleted: " + rows);
//    }