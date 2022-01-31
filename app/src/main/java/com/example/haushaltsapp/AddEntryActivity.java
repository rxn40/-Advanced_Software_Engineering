package com.example.haushaltsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.haushaltsapp.Database.Category;
import com.example.haushaltsapp.Database.Intake;
import com.example.haushaltsapp.Database.MySQLite;
import com.example.haushaltsapp.Database.Outgo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
Activity um eine Einnahme oder Ausgabe anzulegen
 */
public class AddEntryActivity extends AppCompatActivity {

    private MySQLite mySQLite;

    private String selected; //Einnahme oder Ausgabe je nach dem, was im Menü ausgewählt wurde

    private Spinner spinnerCycle, spinnerCategory; //Spinner für Zyklus, Kategorie
    private TextView editTextDate; //Datum
    private ImageView calenderView; //Kalender

    //Werte der Einnahme oder Ausgabe
    private String name;
    private double value;
    private String date;
    private int day;
    private int month;
    private int year;
    private String cycle;
    private String category;

    //Aktuelles Datum. Notwendig um Budget-Eintrag anzupassen
    private int dayCurrent, monthCurrent, yearCurrent;

    /*
    1: Gewähltes Datum liegt in der Zukunft
    2: Der Titel wurde nicht gesetzt
    3: Es wurde kein Wert gesetzt (value = 0.00)
    4: Der gesetzte Wert ist keine Valide eingabe (z.B. 3 Nachkommastellen)
     */
    private int errorValue; //bei entsprechendem Fehler wird ein Dialog geöffnet, um den Benutzer darauf hinzuweisen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);

        mySQLite = new MySQLite(this);

        errorValue = 0; //Default Wert

        getDate(); //Setzt monthCurrent und yearCurrent mit dem aktuellen Datum

        //Was soll angelegt werden? Eine Einnahme oder eine Ausgabe?
        Intent intent = getIntent();
        selected = intent.getStringExtra("Selected");

        //Layout erstellen
        setLayout();

        //Aktuelles Datum anzeigen
        editTextDate = (TextView) findViewById(R.id.editTextDate);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        editTextDate.setText(dateFormat.format(calendar.getTime()));

        //Aktuelles Datum von Kalendar holen, um im CalenderView einzubinden
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        //Auf deutsche Kalenderanzeige umstellen
        Locale locale = new Locale("de");
        Locale.setDefault(locale);
        Resources res = this.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.locale = locale;
        res.updateConfiguration(config, res.getDisplayMetrics());

        //Kalender
        calenderView = findViewById(R.id.calenderView);
        //Setzen von Listener auf dem Kalender Symbol
        calenderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View dateView) {
                DatePickerDialog dateDialog = new DatePickerDialog(AddEntryActivity.this,R.style.datePickerStyle, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        day = selectedDay;
                        month = selectedMonth;
                        year = selectedYear;

                        String dayString = String.valueOf(day);
                        String monthString = String.valueOf(month+1); //Monat beginnt bei index 0

                        if(day < 10){
                            dayString = "0"+dayString;
                        }
                        if(month < 9){ //Monat beginnt bei index 0
                            monthString = "0"+monthString;
                        }
                        editTextDate.setText(dayString+"."+monthString+"."+year);
                    }
                }, year, month, day);
                dateDialog.show();
            }
        });
    }


    /*
    Methode, um das Layout entsprechend aufzubauen
     */
    private void setLayout(){
        //Titel setzen -> Einname anlegen oder Ausgabe anlegen
        TextView title = (TextView) findViewById(R.id.entryTitle);
        title.setText(selected+" anlegen");

        //Spinner, um den Zyklus anzugeben
        spinnerCycle = (Spinner) findViewById(R.id.spinnerCycle);
        ArrayAdapter<CharSequence> adapterCycle = ArrayAdapter.createFromResource(this,  R.array.spinner_cycle, android.R.layout.simple_spinner_item);
        adapterCycle.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCycle.setAdapter(adapterCycle);

        //Spinner Kategorie - nur anzeigen, wenn es eine Ausgabe ist
        spinnerCategory = (Spinner) findViewById(R.id.spinnerCategory);
        if(selected.equals("Ausgabe")){
            ArrayList<Category> list = mySQLite.getAllCategories();
            ArrayAdapter<Category> adapterCategory = new ArrayAdapter<Category>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, list);
            adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCategory.setAdapter(adapterCategory);
        }else{
            //Einnahme - Spinner darf nicht angezeigt werden
            spinnerCategory.setVisibility(View.INVISIBLE);
            TextView categoryTitle = (TextView) findViewById(R.id.textViewCategory);
            categoryTitle.setVisibility(View.INVISIBLE);
        }
    }


    /*
        Methode, die beim Klicken des Ok-Buttons aufgerufen wird
     */
    public void onClickOk(View view){
        boolean valid = getValues(); // sind die Angaben valide? wenn nein -> errorValue != 0

        if(valid){ //Titel und Wert wurde gesetzt
            if(selected.equals("Einnahme")){
                //Einnahme
                Intake intake = new Intake(name, value, day, month, year, cycle);
                mySQLite.addIntake(intake);
            }else{
                //Ausgabe
                Outgo outgo = new Outgo(name, value, day, month, year, cycle, category);
                mySQLite.addOutgo(outgo);

                //Überprüfung, ob ein gesetztes Limit für Kategorie oder Gesamt überschritten wird
                      checkCatLimitReached(category);
                      checkPercentageLimitReached();
            }

            //Wenn der Eintrag in der Vergangenheit liegt, muss der Budget-Eintrag angepasst werden
            if((month < monthCurrent) || (year < yearCurrent)) {
                setBudgetEntry(month, year);
            }

            //Zurück zur Main
            Intent switchToMainActivity= new Intent(this, MainActivity.class);
            startActivity(switchToMainActivity);

        }else{ //Die Angaben waren nicht sinnvoll und der Benutzer muss darauf hingewiesen werden
            informUser(); //Was für ein Fehler ist aufgetreten?
            errorValue = 0; //Danach zurücksetzen
        }
    }


    /*
    Methode, die beim Klicken des Abbrechen-Buttons aufgerufen wird
     */
    public void onClickCancel(View view){
        //zurück zur Main-Seite
        Intent switchToMainActivity= new Intent(this, MainActivity.class);
        startActivity(switchToMainActivity);
    }


    /*
    Funktion um die eingegebenen Werte zu ermitteln
    Achtung - setzt bei einer nicht sinnvolle Eingabe errorValue != 0
     */
    private boolean getValues(){
        boolean retValue = true;

        //Datum:
        date = editTextDate.getText().toString();
        day = Integer.parseInt(date.substring(0,2));
        month = Integer.parseInt(date.substring(3,5));
        year = Integer.parseInt(date.substring(6,10));
        //Datum liegt in der Zukunft
        if((month > monthCurrent && year >= yearCurrent) || (year > yearCurrent) ||(day > dayCurrent && month == monthCurrent && year == yearCurrent)){ //Eintrag liegt in der Zukunft
            errorValue = 1;
            retValue = false;
        }

        //Wert anzeigen lassen:
        EditText editTextValue = (EditText) findViewById(R.id.editTextNumberDecimal);
        Pattern p = Pattern.compile("^\\d+([\\.,]\\d{2})?$"); // 10 oder 10,00 oder 10.00 erlaubt
        Matcher m = p.matcher(editTextValue.getText().toString());
        if(m.find()){ //Eintrag ist valide
            value = Double.parseDouble(editTextValue.getText().toString().replace(",",".")); //Eingabe mit Komma abfangen
            if(value == 0.0){ //Prüfen ob ein Wert gesetzt wurde
                errorValue = 3;
                retValue = false;
            }
        }else{ //es wurde z.B. 10.000 angegeben
            errorValue = 4;
            retValue = false;
        }

        //Name
        EditText editTextName = (EditText) findViewById(R.id.Bezeichnung);
        name = editTextName.getText().toString();
        if(name.equals("Titel") || name.trim().isEmpty()){
            errorValue = 2; //Es wurde kein Titel gesetzt
            retValue = false;
        }


        //Zyklus
        cycle = spinnerCycle.getSelectedItem().toString();

        //Kategorie
        if(selected.equals("Ausgabe")){
            category = spinnerCategory.getSelectedItem().toString();
        }

        return retValue;
    }


    /*
    Methode öffnet einen Dialog, um den Benutzer auf unterschiedliche Fehler hinzuweisen.
     */
    private void informUser(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Hinweis"); //Titel des Dialogs

        //Hinweistext des Dialogs
        if(errorValue == 1){
            builder.setMessage("Das gewählte Datum liegt in der Zukunft.");
        }else if(errorValue == 2){
            builder.setMessage("Bitte setzen Sie einen Titel.");
        }else if(errorValue == 3){
            builder.setMessage("Bitte geben Sie einen Wert an.");
        }else if(errorValue == 4){
            builder.setMessage("Ihre Eingabe bezüglich des Werts ist nicht valide.");
        }

        builder.setCancelable(true);
        builder.setNeutralButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show(); //Dialog anzeigen
        month--; //damit im Kalender der aktuelle Monat angezeigt wird.
    }

    /*
     Setzt die Variablen dayCurrent, monthCurrent und yearCurrent mit dem aktuellen Datum
     */
    private void getDate(){
        Calendar calender = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String dates = dateFormat.format(calender.getTime());
        dayCurrent = Integer.parseInt(dates.substring(0, 2));
        monthCurrent= Integer.parseInt(dates.substring(3,5));
        yearCurrent = Integer.parseInt(dates.substring(6,10));
    }

    /*
    Funktion geht von monthEntry +1 bis zum akuellen Monat/Jahr iterativ durch,
    löscht den Eintrag "Übertrag vom XX.YYYY" mit dem Budget und berechnet den neuen Wert.
    Ist der Wert positiv wird dieser in Einnahmen, ansonsten in Ausgaben, hinterlegt
     */
    private void setBudgetEntry(int monthEntry,int yearEntry){
        if((monthEntry < monthCurrent) || (yearEntry < yearCurrent)){

            do{
                // Erst hochzählen da man den nächsten Monat braucht
                if(monthEntry == 12){
                    monthEntry = 1;
                    yearEntry = yearEntry +1;
                }else{
                    monthEntry = monthEntry + 1;
                }

                //Titel des Eintrags ermitteln
                String titel = "Übertrag vom ";
                if(monthEntry > 1){
                    titel = titel+(monthEntry-1)+"."+yearEntry;
                }else{
                    titel = titel+12+"."+(yearEntry-1);
                }

                //ID des Eintrags ermitteln
                int idIntake = mySQLite.getIntakeIdByName(titel);
                int idOutgo = mySQLite.getOutgoIdByName(titel);
                if(idIntake > -1){
                    mySQLite.deleteIntakeById(idIntake); //Eintrag löschen
                }else if(idOutgo > -1){
                    mySQLite.deleteOutgoById(idOutgo); //Eintrag löschen
                }

                //Neuer Wert für den Eintrag ermitteln
                double value = 0.0;
                if (monthEntry > 1) {
                    value = mySQLite.getValueIntakesMonth(31, monthEntry - 1, yearEntry) - mySQLite.getValueOutgoesMonth(31, monthEntry - 1, yearEntry);
                } else { //1
                    value = mySQLite.getValueIntakesMonth(31, 12, yearEntry - 1) - mySQLite.getValueOutgoesMonth(31, 12, yearEntry - 1);
                }
                //Eintrag "Übertrag vom XX.YYYY" erstellen
                if(value >= 0) { //als Einnahme
                    Intake intake = new Intake(titel, value, 1, monthEntry, yearEntry, "einmalig");
                    mySQLite.addIntake(intake);
                }else{ //als Ausgabe
                    value = value * (-1); //Betrag bilden
                    Outgo outgo = new Outgo(titel, value, 1, monthEntry, yearEntry, "einmalig","Sonstiges");
                    mySQLite.addOutgo(outgo);
                }
            }while (!((monthEntry == monthCurrent) && (yearEntry == yearCurrent))); //bis zm aktuellen Monat
        }
    }

    //Aufruf Menü
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.navigation_menu, menu);

        //Die aktuelle Activity im Menü ausblenden
        MenuItem item = menu.findItem(R.id.subitemAddOutgoes);//Default Ausgaben
        if(selected.equals("Einnahme")){
            item = menu.findItem(R.id.subitemAddIntakes);
        }
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

    private void checkCatLimitReached(String categoryName){
        Double categoryLimit= 0.0;
        Boolean categoryLimitReached;
        boolean isCatButtonChecked = mySQLite.getStateLimitState("Kategorielimit").equals("true");
        if(isCatButtonChecked){
            Category category = mySQLite.getCategory(categoryName);
            categoryLimit = category.getBorder();
            categoryLimitReached=mySQLite.isCatBudgetLimitReached(monthCurrent,yearCurrent,categoryName,categoryLimit);
            if(categoryLimitReached && categoryLimit>0.0 ){
                addCategoryNotification(categoryName);
            }
        }
    }

    private void checkPercentageLimitReached(){
        Integer percentOfBudget=0;
        Boolean isPercentLimitReached;
        Boolean isPercentageButtonChecked = mySQLite.getStateLimitState("Gesamtlimit").equals("true");
        if(isPercentageButtonChecked){
            percentOfBudget =  (int) mySQLite.getStateLimitValue("Gesamtlimit");
            isPercentLimitReached =mySQLite.isPercentBudgetLimitReached(monthCurrent,yearCurrent, percentOfBudget);
            if(isPercentLimitReached && percentOfBudget>=0 ){
                addPercentageNotification();
            }
        }
    }

    private void addCategoryNotification(String category) {
        // Anlegen des Channels der Notifikation, Id muss eindeutig identifizierbar sein
        String NOTIFICATION_CHANNEL_ID = "channel_id";
        String CHANNEL_NAME = "Notification Channel";
        int NOTIFICATION_ID = 2;

        NotificationChannel notificationChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_house)
                .setContentTitle("Überschreitung des Budgets:")
                .setContentText("Betroffene Kategorie: "+category)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Sie haben das von Ihnen definierte Budget überschritten! Betroffene Kategorie: \n"+category))
                .setAutoCancel(true);

        //Intent, welcher aufgerufen wird, wenn Notifikation in der Statuszeile angeklickt wird
        Intent notificationIntent = new Intent(this,MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(notificationChannel);
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void addPercentageNotification() {
        // Anlegen des Channels der Notifikation, Id muss eindeutig identifizierbar sein
        String NOTIFICATION_CHANNEL_ID = "channel_id";
        String CHANNEL_NAME = "Notification Channel";
        int NOTIFICATION_ID = 3;

        NotificationChannel notificationChannel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_house)
                .setContentTitle("Überschreitung des Budgets:")
                .setContentText("Betroffenes Budget: Gesamtbudget")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Sie haben das von Ihnen definierte Gesamtbudget überschritten!"))
                .setAutoCancel(true);

        //Intent, welcher aufgerufen wird, wenn Notifikation in der Statuszeile angeklickt wird
        Intent notificationIntent = new Intent(this,MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(notificationChannel);
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}


