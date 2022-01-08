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

import com.example.haushaltsapp.database.Category;
import com.example.haushaltsapp.database.Intake;
import com.example.haushaltsapp.database.MySQLite;
import com.example.haushaltsapp.database.Outgo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddEntryActivity extends AppCompatActivity {

    private MySQLite mySQLite;

    private String selected; //Einnahe oder Ausgabe

    private Spinner spinnerCyclus, spinnerCategory; //Zyklus, Kategorie
    private TextView editTextDate; //Datum
    private ImageView calenderView; //Kalender

    //Werte der Einnahme oder Ausgabe
    private String name;
    private double value;
    private String dates;
    private int day;
    private int month;
    private int year;
    private String cyclus;
    private String category;

    //Aktuelles Datum. Notwendig um Budget-Eintrag anzupassen
    private int dayCurrent, monthCurrent, yearCurrent;

    /*
    1: gewähltes Datum liegt in der Zukunft
    2: der Titel wurde nicht gesetzt
    3: es wurde kein Wert gesetzt
    4: der gesetzte Wert ist keine Valide eingabe (z.B. 3 Nachkommastellen)
     */
    private int errorValue; //bei entsprechendem Fehler wird ein Dialog geöffnet, um den Benutzer darauf hinzuweisen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);

        mySQLite = new MySQLite(this);

        errorValue = 0; //default

        getDate(); //setze monthCurrent und yearCurrent mit dem aktuellen Datum


        //Was soll angelegt werden? Eine Einnahme oder eine Ausgabe?
        Intent intent = getIntent();
        selected = intent.getStringExtra("Selected");


        //Layout erstellen
        setLayout();

        //Aktuelles Datum anzeigen
        editTextDate = (TextView) findViewById(R.id.editTextDate);
        java.util.Calendar kalender = Calendar.getInstance();
        SimpleDateFormat datumsformat = new SimpleDateFormat("dd.MM.yyyy");
        editTextDate.setText(datumsformat.format(kalender.getTime()));

        //Aktuelles Datum von Kalendar holen, um im CalenderView einzubinden
        year = kalender.get(Calendar.YEAR);
        month = kalender.get(Calendar.MONTH);
        day = kalender.get(Calendar.DAY_OF_MONTH);

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


    //Methode um das Layout aufzubauen
    private void setLayout(){
        TextView titel = (TextView) findViewById(R.id.titel);
        titel.setText(selected+" anlegen");

        //Spinner um den Zyklus anzugeben
        spinnerCyclus = (Spinner) findViewById(R.id.spinnerCyclus);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,  R.array.spinner_cyclus, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCyclus.setAdapter(adapter2);

        //Spinner Kategorie - nur anzeigen, wenn es eine Ausgabe ist
        spinnerCategory = (Spinner) findViewById(R.id.spinnerCategory);
        if(selected.equals("Ausgabe")){
            ArrayList<Category> list = mySQLite.getAllCategory();
            ArrayAdapter<Category> adapter3 = new ArrayAdapter<Category>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, list);
            adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCategory.setAdapter(adapter3);
        }else{ //Einnahme - muss nicht angezeigt werden
            spinnerCategory.setVisibility(View.INVISIBLE);
            TextView categoryTitel = (TextView) findViewById(R.id.textView8);
            categoryTitel.setVisibility(View.INVISIBLE);
        }
    }



    /*
    Eingabe Anlegen
     */
    public void onClickOk(View view){
        boolean valide = getValues();
        if(valide){ //Titel und Wert wurde gesetzt

            if(selected.equals("Einnahme")){ //Einnahme
                Intake intake = new Intake(name, value, day, month, year, cyclus);
                mySQLite.addIntake(intake);
            }else{ //Ausgabe
                Outgo outgo = new Outgo(name, value, day, month, year, cyclus, category);
                mySQLite.addOutgo(outgo);
                //Prüfen, ob ein gesetztes Limit für Kategorie oder Gesamt überschritten wird
                      checkCatLimitReached();
                      checkPercentageLimitReached();
            }

            //Wenn der Eintrag in der Vergangenheit liegt muss der Budget-Eintrag angepasst werden
            if((month < monthCurrent) || (year < yearCurrent)) {
                setBudgetEntry(month, year);
            }

            //Zurück zur Main
            Intent switchToMainActivity= new Intent(this, MainActivity.class);
            startActivity(switchToMainActivity);
        }else{
            informUser(); //Was für ein Fehler ist aufgetreten?
        }
    }

    /*
    Abbrechne
    */
    public void onClickCancel(View view){
        Intent switchToMainActivity= new Intent(this, MainActivity.class);
        startActivity(switchToMainActivity);
    }


    /*
    Funktion um die eingegebenen Werte zu ermitteln
    Warnt, falls eine Eingabe nicht sinnvoll ist
     */
    private boolean getValues(){
        boolean retValue = true;

        //Datum:
        dates = editTextDate.getText().toString();
        day = Integer.parseInt(dates.substring(0,2));
        month = Integer.parseInt(dates.substring(3,5));
        year = Integer.parseInt(dates.substring(6,10));

        if((month > monthCurrent && year >= yearCurrent) || (year > yearCurrent) ||(day > dayCurrent && month == monthCurrent && year == yearCurrent)){ //Eintrag liegt in der Zukunft
            errorValue = 1;
            retValue = false;
        }

        //Wert anzeigen lassen:
        EditText editTextValue = (EditText) findViewById(R.id.editTextNumberDecimal);
        Pattern p = Pattern.compile("^\\d+([\\.,]\\d{2})?$");
        Matcher m = p.matcher(editTextValue.getText().toString());
        if(m.find()){ //Eintrag ist valide
            value = Double.parseDouble(editTextValue.getText().toString().replace(",",".")); //Eingabe mit Komma abfangen
            if(value == 0.0){ //Prüfen ob ein Wert gesetzt wurde
                errorValue = 3;
                retValue = false;
            }
        }else{
            errorValue = 4;
            retValue = false;
        }

        //Name
        EditText editTextName = (EditText) findViewById(R.id.Bezeichnung);
        name = editTextName.getText().toString();
        if(name.equals("Titel")){
            errorValue = 2;
            retValue = false;
        }


        //Zyklus
        cyclus = spinnerCyclus.getSelectedItem().toString();

        //Kategorie
        if(selected.equals("Ausgabe")){
            category = spinnerCategory.getSelectedItem().toString();
        }

        return retValue;
    }


    //Methode öffnet ein Fenster um den Benutzer auf unterschiedliche Fehler hinzuweisen.
    private void informUser(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Hinweis");

        if(errorValue == 1){
            builder1.setMessage("Das gewählte Datum liegt in der Zukunft.");
        }else if(errorValue == 2){
            builder1.setMessage("Bitte setzen Sie einen Titel.");
        }else if(errorValue == 3){
            builder1.setMessage("Bitte geben Sie einen Wert an.");
        }else{ // errorValue 4
            builder1.setMessage("Ihre Eingabe bezüglich des Werts ist nicht valide.");
        }

        builder1.setCancelable(true);
        builder1.setNeutralButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();

        errorValue = 0; //danach zurücksetzen
        month--; //damit im Kalender der aktuelle Monat angezeigt wird.
    }

    // Setzt die Variablen monthCurrent und yearCurrent mit dem aktuellen datum
    private void getDate(){
        java.util.Calendar calender = java.util.Calendar.getInstance();
        SimpleDateFormat datumsformat = new SimpleDateFormat("dd.MM.yyyy");
        String dates = datumsformat.format(calender.getTime());
        dayCurrent = Integer.parseInt(dates.substring(0, 2));
        monthCurrent= Integer.parseInt(dates.substring(3,5));
        yearCurrent = Integer.parseInt(dates.substring(6,10));
    }

    /*
    Funktion geht von moonthEntry +1 bis zum akuellen Monat/Jahr iteraqtiv durch
    löscht den Eintrag mit dem Budget und berechnet den neuen Wert
    ist der Wert positiv wird dieser in Einnahmen, ansonsten in Ausgaben, hinterlegt
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

                //Eintrag muss aus der Datenbank enfernt werden
                //Wie der Eintrag lautet
                String titel = "Übertrag vom ";
                if(monthEntry > 1){
                    titel = titel+(monthEntry-1)+"."+yearEntry;
                }else{
                    titel = titel+12+"."+(yearEntry-1);
                }

                //id des Eintrags ermitteln
                int idIntake = mySQLite.getIntakeIdbyName(titel);
                int idOutgo = mySQLite.getOutgoIdbyName(titel);
                if(idIntake > -1){
                    mySQLite.deleteIntakeById(idIntake); //Eintrag löschen
                }else if(idOutgo > -1){
                    mySQLite.deleteOutgoById(idOutgo); //Eintrag löschen
                }

                //Neuer Eintrag erstellen
                double value = 0.0;
                if (monthEntry > 1) {
                    value = mySQLite.getValueIntakesMonth(31, monthEntry - 1, yearEntry) - mySQLite.getValueOutgosMonth(31, monthEntry - 1, yearEntry);
                } else { //1
                    value = mySQLite.getValueIntakesMonth(31, 12, yearEntry - 1) - mySQLite.getValueOutgosMonth(31, 12, yearEntry - 1);
                }
                if(value >= 0) { //Einnahme
                    Intake intake = new Intake(titel, value, 1, monthEntry, yearEntry, "einmalig");
                    mySQLite.addIntake(intake);
                }else{ //Ausgabe
                    value = value * (-1);
                    Outgo outgo = new Outgo(titel, value, 1, monthEntry, yearEntry, "einmalig","Sonstiges");
                    mySQLite.addOutgo(outgo);
                }
            }while (!((monthEntry == monthCurrent) && (yearEntry == yearCurrent)));
        }
    }

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

    private void checkCatLimitReached(){
        ArrayList<Category> categorieList = mySQLite.getAllCategory();
        String categoryName = "";
        Double categoryLimit= 0.0;
        Boolean categoryLimitReached;
        int notificationId;
        boolean isCatButtonChecked = mySQLite.getSateLimitState("Kategorielimit").equals("true"); //später aus der Datenbank - Yvette
        if(isCatButtonChecked){
            for(int i = 0; i < categorieList.size(); i++){
                Category category = categorieList.get(i);
                categoryName = category.getName_PK();
                categoryLimit = category.getBorder();
                notificationId = i;
                categoryLimitReached=mySQLite.isCatBudgetLimitReached(monthCurrent,yearCurrent,categoryName,categoryLimit);
                if(categoryLimitReached && categoryLimit>0.0 ){
                    addCategoryNotification(notificationId,categoryName);
                }
            }
        }
    }

    private void checkPercentageLimitReached(){

        Integer percentOfBudget=0; //double?
        Boolean isPerecentLimitReached;
        Boolean isPercentageButtonChecked = mySQLite.getSateLimitState("Gesamtlimit").equals("true"); ; //später aus der Dantebank. Yvette
        if(isPercentageButtonChecked){
            percentOfBudget =  (int) mySQLite.getSateLimitValue("Gesamtlimit"); //Später aus der Datenbank. Yvette
            isPerecentLimitReached=mySQLite.isPercentBudgetLimitReached(monthCurrent,yearCurrent, percentOfBudget);
            if(isPerecentLimitReached && percentOfBudget>=0 ){
                addPercentageNotification();
            }
        }

    }

    private void addCategoryNotification(int id, String category) {
        // Anlegen des Channels der Notifikation
        String NOTIFICATION_CHANNEL_ID = "channel_id";
        String CHANNEL_NAME = "Notification Channel";
        // eindeutige ID für jede Notifikation
        int NOTIFICATION_ID = id;

        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Überschreitung des definierten Budget Limits:")
                .setContentText("Betroffene Kategorie: "+category)
                .setAutoCancel(true);
        //notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        //Intent welcher aufgerufen wird, wenn er in der Statuszeile angeklickt wird
        Intent notificationIntent = new Intent(this,MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(notificationChannel);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void addPercentageNotification() {
        // Anlegen des Channels der Notifikation
        String NOTIFICATION_CHANNEL_ID = "channel_id";
        String CHANNEL_NAME = "Notification Channel";
        int NOTIFICATION_ID = 10;

        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, CHANNEL_NAME,NotificationManager.IMPORTANCE_DEFAULT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Überschreitung des definierten Budget Limits:")
                .setContentText("Sie haben das von Ihnen gesetzte Budget überschritten!")
                .setAutoCancel(true);
        //notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        //Intent welcher aufgerufen wird, wenn er in der Statuszeile angeklickt wird
        Intent notificationIntent = new Intent(this,MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(notificationChannel);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}


