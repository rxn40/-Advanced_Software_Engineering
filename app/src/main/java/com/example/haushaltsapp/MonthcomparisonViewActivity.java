package com.example.haushaltsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.haushaltsapp.database.Category;
import com.example.haushaltsapp.database.Intake;
import com.example.haushaltsapp.database.MySQLite;
import com.example.haushaltsapp.database.Outgo;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.models.BarModel;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MonthcomparisonViewActivity extends AppCompatActivity {

    ////Variabeln zur Menünavigation
    private MySQLite mySQLite;
    private final int REQUESTCODE_ADD = 12; //AddEntryActivity
    private final int REQUESTCODE_SHOW = 13; //ShowEntryActivity
    private final int REQUESTCODE_EDIT = 14; //EditEntryActivity
    private final int REQUESTCODE_ADD_CATEGORY = 15; //AddCategoryActivity
    ///////////////////////////////
    private MySQLite db;

    //private ValueLineChart LineChartyear;
    private BarChart BarChartInOutcomparison;

    private TextView tvM1o, tvM2o, tvM1i, tvM2i;
    private TextView tvM1out, tvM2out,tvM1in, tvM2in;

    //aktuelles Datum
    private int day, day1, day2;
    private int month, month1, month2;
    private int year, year1, year2;
    private String datesM1, datesM2;

    private EditText editTextDateM1; //Datum M1
    private EditText editTextDateM2; //Datum M2
    private long startDateInMilliSec;
    private long endDateInMilliSec;

    // Setzt die Variablen day, month, year
    private void getDate(){
        java.util.Calendar calender = java.util.Calendar.getInstance();
        SimpleDateFormat datumsformat = new SimpleDateFormat("dd.MM.yyyy");
        String dates = datumsformat.format(calender.getTime());
        day = Integer.parseInt(dates.substring(0,2));
        month = Integer.parseInt(dates.substring(3,5));
        year = Integer.parseInt(dates.substring(6,10));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthcomparison_view);

        getDate();

        db = new MySQLite(this);
        db.openDatabase();

        //Aktuelles Datum anzeigen
        editTextDateM1 = (EditText) findViewById(R.id.editTextDateM1);
        editTextDateM2 = (EditText) findViewById(R.id.editTextDateM2);
        java.util.Calendar calender = Calendar.getInstance();
        SimpleDateFormat datumsformat = new SimpleDateFormat("dd.MM.yyyy");

        editTextDateM1.setText(datumsformat.format(calender.getTime()));
        editTextDateM2.setText(datumsformat.format(calender.getTime()));

        setData();
    }

    private void setData()
    {
        datesM1 = editTextDateM1.getText().toString();
        day1 = Integer.parseInt(datesM1.substring(0,2));
        month1 = Integer.parseInt(datesM1.substring(3,5));
        year1 = Integer.parseInt(datesM1.substring(6,10));

        datesM2 = editTextDateM2.getText().toString();
        day2 = Integer.parseInt(datesM2.substring(0,2));
        month2 = Integer.parseInt(datesM2.substring(3,5));
        year2 = Integer.parseInt(datesM2.substring(6,10));

        BarChartInOutcomparison= findViewById(R.id.barchartinout);

        tvM1o = findViewById(R.id.tvMonth1);
        tvM2o = findViewById(R.id.tvMonth2);

        tvM1i = findViewById(R.id.tvMonth1in);
        tvM2i = findViewById(R.id.tvMonth2in);

        tvM1out = findViewById(R.id.tvout_Month1);
        tvM2out = findViewById(R.id.tvout_Month2);

        tvM1in =findViewById(R.id.tvin_Month1);
        tvM2in =findViewById(R.id.tvin_Month2);

        BarChartInOutcomparison.clearChart();

        //In Balkendiagramm nur zwei bestimmte Werte übergaben
        //Date1 und Date1 müssen übergaben werden in Methode zur Bargraph
        //BarGraphMonthInOut();
        BarGraphComparision(month1,year1,month2,year2);

        setTextInOut(month1,year1,month2,year2);

    }

    public  void openCalenderM1(View dateview) {
        java.util.Calendar calender = java.util.Calendar.getInstance();
        DatePickerDialog dateDialog = new DatePickerDialog(MonthcomparisonViewActivity.this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {

                day = selectedDay;
                month = selectedMonth + 1; //richtige monatszahl
                year = selectedYear;

                //Addition bei Monat von 1, Index beginnend bei 0
                if (day<10)
                {
                    if(month<10)
                    {
                        editTextDateM1.setText("0"+ selectedDay+".0"+month+"."+selectedYear);
                    }
                    else {
                        editTextDateM1.setText("0" + selectedDay + "." + month + "." + selectedYear);
                    }
                }
                else {
                    if(month<10)
                    {
                        editTextDateM1.setText(selectedDay+".0"+month+"."+selectedYear);
                    }
                    else {
                        editTextDateM1.setText(selectedDay + "." + month + "." + selectedYear);
                    }
                }

                //editTextDate.setText(selectedYear + "/" + (selectedMonth + 1) + "/" + selectedDay);

                //Übergabe der Daten an Kalender-Objekt und Setzen von Start und Endzeit)
                calender.set(year, month, day, 8, 0, 0);
                startDateInMilliSec = calender.getTimeInMillis();
                calender.set(year, month, day, 9, 0, 0);
                endDateInMilliSec = calender.getTimeInMillis();
            }
        }, year, month, day);
        dateDialog.show();
    }

    public  void openCalenderM2(View dateview) {
        java.util.Calendar calender = java.util.Calendar.getInstance();
        DatePickerDialog dateDialog = new DatePickerDialog(MonthcomparisonViewActivity.this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {

                day = selectedDay;
                month = selectedMonth + 1; //richtige monatszahl
                year = selectedYear;

                //Addition bei Monat von 1, Index beginnend bei 0
                if (day<10)
                {
                    if(month<10)
                    {
                        editTextDateM2.setText("0"+ selectedDay+".0"+month+"."+selectedYear);
                    }
                    else {
                        editTextDateM2.setText("0" + selectedDay + "." + month + "." + selectedYear);
                    }
                }
                else {
                    if(month<10)
                    {
                        editTextDateM2.setText(selectedDay+".0"+month+"."+selectedYear);
                    }
                    else {
                        editTextDateM2.setText(selectedDay + "." + month + "." + selectedYear);
                    }
                }

                //editTextDate.setText(selectedYear + "/" + (selectedMonth + 1) + "/" + selectedDay);

                //Übergabe der Daten an Kalender-Objekt und Setzen von Start und Endzeit)
                calender.set(year, month, day, 8, 0, 0);
                startDateInMilliSec = calender.getTimeInMillis();
                calender.set(year, month, day, 9, 0, 0);
                endDateInMilliSec = calender.getTimeInMillis();
            }
        }, year, month, day);
        dateDialog.show();
    }

    /*public void LineGraphMonth() {

        //Ausgaben
        ValueLineSeries Outgoe = new ValueLineSeries();
        Outgoe.setColor(0xFFEF5350);

        //Einnahmen
        ValueLineSeries Input = new ValueLineSeries();
        Input.setColor(0xFF66BB6A);

        int mo = 1; //monate hochzählen
        int aktuellMonth = month; //1-12
        int monthrechne;
        //wegen Darstellung einen Monat mehr anzeigen, weil Achse bei erstem Monat nicht beschriftet wird
        if( month==1)
        {
            monthrechne =12;
        }
        else
        {
            monthrechne=month-1;
        }
        int vorYear = year-1; //2020

        //vorjahresanzeige
        while (monthrechne <= 12) {
            String monatJahresansicht = "leer";

            switch (monthrechne) {
                case 1:
                    monatJahresansicht = "Jan";
                    break;
                case 2:
                    monatJahresansicht = "Feb";
                    break;
                case 3:
                    monatJahresansicht = "Mar";
                    break;
                case 4:
                    monatJahresansicht = "Apr";
                    break;
                case 5:
                    monatJahresansicht = "Mai";
                    break;
                case 6:
                    monatJahresansicht = "Jun";
                    break;
                case 7:
                    monatJahresansicht = "Jul";
                    break;
                case 8:
                    monatJahresansicht = "Aug";
                    break;
                case 9:
                    monatJahresansicht = "Sep";
                    break;
                case 10:
                    monatJahresansicht = "Okt";
                    break;
                case 11:
                    monatJahresansicht = "Nov";
                    break;
                case 12:
                    monatJahresansicht = "Dez";
                    break;
            }
            //Datnbankzugriff: AUsgaben
            float AusgabeMonateX = db.getValueOutgosMonth(31,monthrechne,vorYear);
            Outgoe.addPoint(new ValueLinePoint(monatJahresansicht, AusgabeMonateX));

            //Datnbankzugriff: Einnahmen
            float IntakeMonateX = db.getValueIntakesMonth(31,monthrechne,vorYear);
            Input.addPoint(new ValueLinePoint(monatJahresansicht, IntakeMonateX));

            monthrechne++;
        }

        //dieses Jahr anzeigen
        //letzer Monata wird die Achse nicht beschriftet
        while (mo <= (month)) {
            String monatJahresansicht = "leer";

            switch (mo) {
                case 1:
                    monatJahresansicht = "Jan";
                    break;
                case 2:
                    monatJahresansicht = "Feb";
                    break;
                case 3:
                    monatJahresansicht = "Mar";
                    break;
                case 4:
                    monatJahresansicht = "Apr";
                    break;
                case 5:
                    monatJahresansicht = "Mai";
                    break;
                case 6:
                    monatJahresansicht = "Jun";
                    break;
                case 7:
                    monatJahresansicht = "Jul";
                    break;
                case 8:
                    monatJahresansicht = "Aug";
                    break;
                case 9:
                    monatJahresansicht = "Sep";
                    break;
                case 10:
                    monatJahresansicht = "Okt";
                    break;
                case 11:
                    monatJahresansicht = "Nov";
                    break;
                case 12:
                    monatJahresansicht = "Dez";
                    break;
            }
            //Datnbankzugriff:
            float AusgabeMonateX = db.getValueOutgosMonth(31,mo,year);
            Outgoe.addPoint(new ValueLinePoint(monatJahresansicht, AusgabeMonateX));

            //Datnbankzugriff: Ausgaben
            float IntakeMonateX = db.getValueIntakesMonth(31,mo,year);
            Input.addPoint(new ValueLinePoint(monatJahresansicht, IntakeMonateX));

            mo++;
        }
        //Darstellungsoptionen
        LineChartyear.addSeries(Outgoe);
        LineChartyear.addSeries(Input);
        LineChartyear.startAnimation();
    }*/

    /*public void BarGraphMonth() {

        int m=1; //für Textausgabe
        int mo = 1; //monate hochzählen
        int monthrechne=month;//1-12

        int vorYear = year-1; //2020

        //vorjahresanzeige
        while (monthrechne <= 12) {
            String monatJahresansicht = "leer";

            switch (monthrechne) {
                case 1:
                    monatJahresansicht = "Jan";
                    break;
                case 2:
                    monatJahresansicht = "Feb";
                    break;
                case 3:
                    monatJahresansicht = "Mar";
                    break;
                case 4:
                    monatJahresansicht = "Apr";
                    break;
                case 5:
                    monatJahresansicht = "Mai";
                    break;
                case 6:
                    monatJahresansicht = "Jun";
                    break;
                case 7:
                    monatJahresansicht = "Jul";
                    break;
                case 8:
                    monatJahresansicht = "Aug";
                    break;
                case 9:
                    monatJahresansicht = "Sep";
                    break;
                case 10:
                    monatJahresansicht = "Okt";
                    break;
                case 11:
                    monatJahresansicht = "Nov";
                    break;
                case 12:
                    monatJahresansicht = "Dez";
                    break;
            }
            //Datnbankzugriff: AUsgaben
            float AusgabeMonateX = db.getValueOutgosMonth(31,monthrechne,vorYear);
            BarChartyear.addBar(new BarModel(
                    monatJahresansicht,
                    AusgabeMonateX,
                    Color.parseColor("#EF5350")));
            //Datnbankzugriff: Einnahmen
            //noch schauen ob in extra Diagram darstellen
            //float IntakeMonateX = db.getValueIntakesMonth(31,monthrechne,vorYear);

            //ANzeige von Wert in Text unter Diagramm
            switch (m) {
                case 1:
                    tvM1out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM1.setText(monatJahresansicht+"."+vorYear);
                    break;
                case 2:
                    tvM2out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM2.setText(monatJahresansicht+"."+vorYear);
                    break;
                case 3:
                    tvM3out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM3.setText(monatJahresansicht+"."+vorYear);
                    break;
                case 4:
                    tvM4out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM4.setText(monatJahresansicht+"."+vorYear);
                    break;
                case 5:
                    tvM5out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM5.setText(monatJahresansicht+"."+vorYear);
                    break;
                case 6:
                    tvM6out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM6.setText(monatJahresansicht+"."+vorYear);
                    break;
                case 7:
                    tvM7out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM7.setText(monatJahresansicht+"."+vorYear);
                    break;
                case 8:
                    tvM8out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM8.setText(monatJahresansicht+"."+vorYear);
                    break;
                case 9:
                    tvM9out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM9.setText(monatJahresansicht+"."+vorYear);
                    break;
                case 10:
                    tvM10out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM10.setText(monatJahresansicht+"."+vorYear);
                    break;
                case 11:
                    tvM11out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM11.setText(monatJahresansicht+"."+vorYear);
                    break;
                case 12:
                    tvM12out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM12.setText(monatJahresansicht+"."+vorYear);
                    break;
                case 13:
                    tvM13out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM13.setText(monatJahresansicht+"."+vorYear);
                    break;
            }

            m++;
            monthrechne++;
        }

        //dieses Jahr anzeigen
        //letzer Monata wird die Achse nicht beschriftet
        while (mo <= (month)) {
            String monatJahresansicht = "leer";

            switch (mo) {
                case 1:
                    monatJahresansicht = "Jan";
                    break;
                case 2:
                    monatJahresansicht = "Feb";
                    break;
                case 3:
                    monatJahresansicht = "Mar";
                    break;
                case 4:
                    monatJahresansicht = "Apr";
                    break;
                case 5:
                    monatJahresansicht = "Mai";
                    break;
                case 6:
                    monatJahresansicht = "Jun";
                    break;
                case 7:
                    monatJahresansicht = "Jul";
                    break;
                case 8:
                    monatJahresansicht = "Aug";
                    break;
                case 9:
                    monatJahresansicht = "Sep";
                    break;
                case 10:
                    monatJahresansicht = "Okt";
                    break;
                case 11:
                    monatJahresansicht = "Nov";
                    break;
                case 12:
                    monatJahresansicht = "Dez";
                    break;
            }
            //Datnbankzugriff:
            float AusgabeMonateX = db.getValueOutgosMonth(31,mo,year);
            BarChartyear.addBar(new BarModel(
                    monatJahresansicht,
                    AusgabeMonateX,
                    Color.parseColor("#EF5350")));


            //Datnbankzugriff: Einnahmen
            //noch schauen ob in extra Diagram darstellen
            //float IntakeMonateX = db.getValueIntakesMonth(31,monthrechne,vorYear);

            //ANzeige von Wert in Text unter Diagramm
            switch (m) {
                case 1:
                    tvM1out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM1.setText(monatJahresansicht+"."+year);
                    break;
                case 2:
                    tvM2out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM2.setText(monatJahresansicht+"."+year);
                    break;
                case 3:
                    tvM3out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM3.setText(monatJahresansicht+"."+year);
                    break;
                case 4:
                    tvM4out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM4.setText(monatJahresansicht+"."+year);
                    break;
                case 5:
                    tvM5out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM5.setText(monatJahresansicht+"."+year);
                    break;
                case 6:
                    tvM6out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM6.setText(monatJahresansicht+"."+year);
                    break;
                case 7:
                    tvM7out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM7.setText(monatJahresansicht+"."+year);
                    break;
                case 8:
                    tvM8out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM8.setText(monatJahresansicht+"."+year);
                    break;
                case 9:
                    tvM9out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM9.setText(monatJahresansicht+"."+year);
                    break;
                case 10:
                    tvM10out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM10.setText(monatJahresansicht+"."+year);
                    break;
                case 11:
                    tvM11out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM11.setText(monatJahresansicht+"."+year);
                    break;
                case 12:
                    tvM12out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM12.setText(monatJahresansicht+"."+year);
                    break;
                case 13:
                    tvM13out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM13.setText(monatJahresansicht+"."+year);
                    break;
            }
            m++;
            mo++;
        }
        //Darstellungsoptionen
        BarChartyear.startAnimation();
        BarChartyear.setShowValues(true);
        BarChartyear.setActivated(false);

    }*/

    public void BarGraphComparision(int month1, int year1, int month2, int year2)
    {
        String monthtext1 = monthtoSting(month1);
        String monthtext2 =monthtoSting(month2);
        /*switch (month1) {
            case 1:
                monthtext1 = "Jan";
                break;
            case 2:
                monthtext1 = "Feb";
                break;
            case 3:
                monthtext1 = "Mar";
                break;
            case 4:
                monthtext1 = "Apr";
                break;
            case 5:
                monthtext1 = "Mai";
                break;
            case 6:
                monthtext1 = "Jun";
                break;
            case 7:
                monthtext1 = "Jul";
                break;
            case 8:
                monthtext1 = "Aug";
                break;
            case 9:
                monthtext1 = "Sep";
                break;
            case 10:
                monthtext1 = "Okt";
                break;
            case 11:
                monthtext1 = "Nov";
                break;
            case 12:
                monthtext1 = "Dez";
                break;
        }

        switch (month2) {
            case 1:
                monthtext2 = "Jan";
                break;
            case 2:
                monthtext2 = "Feb";
                break;
            case 3:
                monthtext2 = "Mar";
                break;
            case 4:
                monthtext2 = "Apr";
                break;
            case 5:
                monthtext2 = "Mai";
                break;
            case 6:
                monthtext2 = "Jun";
                break;
            case 7:
                monthtext2 = "Jul";
                break;
            case 8:
                monthtext2 = "Aug";
                break;
            case 9:
                monthtext2 = "Sep";
                break;
            case 10:
                monthtext2 = "Okt";
                break;
            case 11:
                monthtext2 = "Nov";
                break;
            case 12:
                monthtext2 = "Dez";
                break;
        }*/

        //Datnbankzugriff: Einnahmen
        float IntakeMonate1 = db.getValueIntakesMonth(31,month1,year1);
        BarChartInOutcomparison.addBar(new BarModel(
                "           "+monthtext1 + " "+ year1,
                IntakeMonate1,
                Color.parseColor("#66BB6A")));
        //Datnbankzugriff: AUsgaben
        float OutgoMonate1 = db.getValueOutgosMonth(31,month1,year1);
        BarChartInOutcomparison.addBar(new BarModel(
                "",//"Aus. "+monthtext1,
                OutgoMonate1,
                Color.parseColor("#EF5350")));

        //Monat 2
        float IntakeMonate2 = db.getValueIntakesMonth(31,month2,year2);
        BarChartInOutcomparison.addBar(new BarModel(
                "           "+monthtext2 + " "+ year2,
                IntakeMonate2,
                Color.parseColor("#66BB6A")));
        //Datnbankzugriff: AUsgaben
        float OutgoMonate2 = db.getValueOutgosMonth(31,month2,year2);
        BarChartInOutcomparison.addBar(new BarModel(
                "",//"Aus. "+monthtext2,
                OutgoMonate2,
                Color.parseColor("#EF5350")));

    }

    public String monthtoSting(int month)
    {
        String monthtext="";

        switch (month) {
            case 1:
                monthtext = "Jan";
                break;
            case 2:
                monthtext = "Feb";
                break;
            case 3:
                monthtext= "Mar";
                break;
            case 4:
                monthtext = "Apr";
                break;
            case 5:
                monthtext = "Mai";
                break;
            case 6:
                monthtext = "Jun";
                break;
            case 7:
                monthtext = "Jul";
                break;
            case 8:
                monthtext = "Aug";
                break;
            case 9:
                monthtext = "Sep";
                break;
            case 10:
                monthtext = "Okt";
                break;
            case 11:
                monthtext = "Nov";
                break;
            case 12:
                monthtext = "Dez";
                break;
        }
        return  monthtext;
    }

    public void setTextInOut(int month1, int year1, int month2, int year2)
    {
        String monthtext1= monthtoSting(month1);
        String monthtext2 = monthtoSting(month2);
        tvM1out.setText(Float.toString(db.getValueOutgosMonth(31,month1,year1))+" €");
        tvM1o.setText(monthtext1+"."+year1);

        tvM1in.setText(Float.toString(db.getValueIntakesMonth(31,month1,year1))+" €");
        tvM1i.setText(monthtext1+"."+year1);

        tvM2out.setText(Float.toString(db.getValueOutgosMonth(31,month2,year2))+" €");
        tvM2o.setText(monthtext2+"."+year2);

        tvM2in.setText(Float.toString(db.getValueIntakesMonth(31,month2,year2))+" €");
        tvM2i.setText(monthtext2+"."+year2);
    }

    public void BarGraphMonthInOut() {

        int m=1; //für Textausgabe
        int mo = 1; //monate hochzählen
        int monthrechne=month;//1-12

        //erster Monat wird in Balkendiagramm nicht beschriftet

        int vorYear = year-1; //2020

        //vorjahresanzeige
        while (monthrechne <= 12) {
            String monatJahresansicht = "leer";

            switch (monthrechne) {
                case 1:
                    monatJahresansicht = "Jan";
                    break;
                case 2:
                    monatJahresansicht = "Feb";
                    break;
                case 3:
                    monatJahresansicht = "Mar";
                    break;
                case 4:
                    monatJahresansicht = "Apr";
                    break;
                case 5:
                    monatJahresansicht = "Mai";
                    break;
                case 6:
                    monatJahresansicht = "Jun";
                    break;
                case 7:
                    monatJahresansicht = "Jul";
                    break;
                case 8:
                    monatJahresansicht = "Aug";
                    break;
                case 9:
                    monatJahresansicht = "Sep";
                    break;
                case 10:
                    monatJahresansicht = "Okt";
                    break;
                case 11:
                    monatJahresansicht = "Nov";
                    break;
                case 12:
                    monatJahresansicht = "Dez";
                    break;
            }
            //Datnbankzugriff: Einnahmen
            float IntakeMonateX = db.getValueIntakesMonth(31,monthrechne,vorYear);
            BarChartInOutcomparison.addBar(new BarModel(
                    "     "+monatJahresansicht,
                    IntakeMonateX,
                    Color.parseColor("#66BB6A")));
            //Datnbankzugriff: AUsgaben
            float AusgabeMonateX = db.getValueOutgosMonth(31,monthrechne,vorYear);
            BarChartInOutcomparison.addBar(new BarModel(
                    "",//monatJahresansicht,
                    AusgabeMonateX,
                    Color.parseColor("#EF5350")));


            //ANzeige von Wert in Text unter Diagramm
         /*   switch (m) {
                case 1:
                    tvM1out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM1.setText(monatJahresansicht+"."+vorYear);

                    tvM1in.setText(Float.toString(IntakeMonateX)+" €");
                    tvM1i.setText(monatJahresansicht+"."+vorYear);
                    break;
                case 2:
                    tvM2out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM2.setText(monatJahresansicht+"."+vorYear);
                    tvM2in.setText(Float.toString(IntakeMonateX)+" €");
                    tvM2i.setText(monatJahresansicht+"."+vorYear);
                    break;
                case 3:
                    tvM3out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM3.setText(monatJahresansicht+"."+vorYear);
                    tvM3in.setText(Float.toString(IntakeMonateX)+" €");
                    tvM3i.setText(monatJahresansicht+"."+vorYear);
                    break;
                case 4:
                    tvM4out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM4.setText(monatJahresansicht+"."+vorYear);
                    tvM4in.setText(Float.toString(IntakeMonateX)+" €");
                    tvM4i.setText(monatJahresansicht+"."+vorYear);
                    break;
                case 5:
                    tvM5out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM5.setText(monatJahresansicht+"."+vorYear);
                    tvM5in.setText(Float.toString(IntakeMonateX)+" €");
                    tvM5i.setText(monatJahresansicht+"."+vorYear);
                    break;
                case 6:
                    tvM6out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM6.setText(monatJahresansicht+"."+vorYear);
                    tvM6in.setText(Float.toString(IntakeMonateX)+" €");
                    tvM6i.setText(monatJahresansicht+"."+vorYear);
                    break;
                case 7:
                    tvM7out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM7.setText(monatJahresansicht+"."+vorYear);
                    tvM7in.setText(Float.toString(IntakeMonateX)+" €");
                    tvM7i.setText(monatJahresansicht+"."+vorYear);
                    break;
                case 8:
                    tvM8out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM8.setText(monatJahresansicht+"."+vorYear);
                    tvM8in.setText(Float.toString(IntakeMonateX)+" €");
                    tvM8i.setText(monatJahresansicht+"."+vorYear);
                    break;
                case 9:
                    tvM9out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM9.setText(monatJahresansicht+"."+vorYear);
                    tvM9in.setText(Float.toString(IntakeMonateX)+" €");
                    tvM9i.setText(monatJahresansicht+"."+vorYear);
                    break;
                case 10:
                    tvM10out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM10.setText(monatJahresansicht+"."+vorYear);
                    tvM10in.setText(Float.toString(IntakeMonateX)+" €");
                    tvM10i.setText(monatJahresansicht+"."+vorYear);
                    break;
                case 11:
                    tvM11out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM11.setText(monatJahresansicht+"."+vorYear);
                    tvM11in.setText(Float.toString(IntakeMonateX)+" €");
                    tvM11i.setText(monatJahresansicht+"."+vorYear);
                    break;
                case 12:
                    tvM12out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM12.setText(monatJahresansicht+"."+vorYear);
                    tvM12in.setText(Float.toString(IntakeMonateX)+" €");
                    tvM12i.setText(monatJahresansicht+"."+vorYear);
                    break;
                case 13:
                    tvM13out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM13.setText(monatJahresansicht+"."+vorYear);
                    tvM13in.setText(Float.toString(IntakeMonateX)+" €");
                    tvM13i.setText(monatJahresansicht+"."+vorYear);
                    break;
            }
*/
            m++;
            monthrechne++;
        }

        //dieses Jahr anzeigen
        //letzer Monata wird die Achse nicht beschriftet
        while (mo <= (month)) {
            String monatJahresansicht = "leer";

            switch (mo) {
                case 1:
                    monatJahresansicht = "Jan";
                    break;
                case 2:
                    monatJahresansicht = "Feb";
                    break;
                case 3:
                    monatJahresansicht = "Mar";
                    break;
                case 4:
                    monatJahresansicht = "Apr";
                    break;
                case 5:
                    monatJahresansicht = "Mai";
                    break;
                case 6:
                    monatJahresansicht = "Jun";
                    break;
                case 7:
                    monatJahresansicht = "Jul";
                    break;
                case 8:
                    monatJahresansicht = "Aug";
                    break;
                case 9:
                    monatJahresansicht = "Sep";
                    break;
                case 10:
                    monatJahresansicht = "Okt";
                    break;
                case 11:
                    monatJahresansicht = "Nov";
                    break;
                case 12:
                    monatJahresansicht = "Dez";
                    break;
            }
            //Datnbankzugriff: Einnahmen
            float IntakeMonateX = db.getValueIntakesMonth(31,mo,year);
            BarChartInOutcomparison.addBar(new BarModel(
                    "     "+ monatJahresansicht,
                    IntakeMonateX,
                    Color.parseColor("#66BB6A")));
            //Datnbankzugriff Ausgaben:
            float AusgabeMonateX = db.getValueOutgosMonth(31,mo,year);
            BarChartInOutcomparison.addBar(new BarModel(
                    "",//monatJahresansicht,
                    AusgabeMonateX,
                    Color.parseColor("#EF5350")));



            //ANzeige von Wert in Text unter Diagramm
         /*   switch (m) {
                case 1:
                    tvM1out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM1.setText(monatJahresansicht+"."+year);
                    tvM1in.setText(Float.toString(IntakeMonateX)+" €");
                    tvM1i.setText(monatJahresansicht+"."+year);
                    break;
                case 2:
                    tvM2out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM2.setText(monatJahresansicht+"."+year);
                    tvM2in.setText(Float.toString(IntakeMonateX)+" €");
                    tvM2i.setText(monatJahresansicht+"."+year);
                    break;
                case 3:
                    tvM3out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM3.setText(monatJahresansicht+"."+year);
                    tvM3in.setText(Float.toString(IntakeMonateX)+" €");
                    tvM3i.setText(monatJahresansicht+"."+year);
                    break;
                case 4:
                    tvM4out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM4.setText(monatJahresansicht+"."+year);
                    tvM4in.setText(Float.toString(IntakeMonateX)+" €");
                    tvM4i.setText(monatJahresansicht+"."+year);
                    break;
                case 5:
                    tvM5out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM5.setText(monatJahresansicht+"."+year);
                    tvM5in.setText(Float.toString(IntakeMonateX)+" €");
                    tvM5i.setText(monatJahresansicht+"."+year);
                    break;
                case 6:
                    tvM6out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM6.setText(monatJahresansicht+"."+year);
                    tvM6in.setText(Float.toString(IntakeMonateX)+" €");
                    tvM6i.setText(monatJahresansicht+"."+year);
                    break;
                case 7:
                    tvM7out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM7.setText(monatJahresansicht+"."+year);
                    tvM7in.setText(Float.toString(IntakeMonateX)+" €");
                    tvM7i.setText(monatJahresansicht+"."+year);
                    break;
                case 8:
                    tvM8out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM8.setText(monatJahresansicht+"."+year);
                    tvM8in.setText(Float.toString(IntakeMonateX)+" €");
                    tvM8i.setText(monatJahresansicht+"."+year);
                    break;
                case 9:
                    tvM9out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM9.setText(monatJahresansicht+"."+year);
                    tvM9in.setText(Float.toString(IntakeMonateX)+" €");
                    tvM9i.setText(monatJahresansicht+"."+year);
                    break;
                case 10:
                    tvM10out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM10.setText(monatJahresansicht+"."+year);
                    tvM10in.setText(Float.toString(IntakeMonateX)+" €");
                    tvM10i.setText(monatJahresansicht+"."+year);
                    break;
                case 11:
                    tvM11out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM11.setText(monatJahresansicht+"."+year);
                    tvM11in.setText(Float.toString(IntakeMonateX)+" €");
                    tvM11i.setText(monatJahresansicht+"."+year);
                    break;
                case 12:
                    tvM12out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM12.setText(monatJahresansicht+"."+year);
                    tvM12in.setText(Float.toString(IntakeMonateX)+" €");
                    tvM12i.setText(monatJahresansicht+"."+year);

                    break;
                case 13:
                    tvM13out.setText(Float.toString(AusgabeMonateX)+" €");
                    tvM13.setText(monatJahresansicht+"."+year);
                    tvM13in.setText(Float.toString(IntakeMonateX)+" €");
                    tvM13i.setText(monatJahresansicht+"."+year);
                    break;
            }*/
            m++;
            mo++;
        }
        //Darstellungsoptionen
        BarChartInOutcomparison.startAnimation();
        BarChartInOutcomparison.setShowValues(true);
        BarChartInOutcomparison.setActivated(false);

    }


  /*  public void StackedBarGraphMonth() {

        //Datenbankzugriff
        Intent intent = getIntent();
        ArrayList<Outgo> Data = (ArrayList<Outgo>) intent.getSerializableExtra("dataOut");
        ArrayList<Intake> DataIn = (ArrayList<Intake>) intent.getSerializableExtra("dataIn");

        StackedBarModel[] StackKat1 =new StackedBarModel[12];
        StackKat1[0] = M1;
        StackKat1[1] = M2;
        StackKat1[2] = M3;
        StackKat1[3] = M4;
        StackKat1[4] = M5;
        StackKat1[5] = M6;
        StackKat1[6] = M7;
        StackKat1[7] = M8;
        StackKat1[8] = M9;
        StackKat1[9] = M10;
        StackKat1[10] = M11;
        StackKat1[11] = M12;

        int mo = 1; //monate hochzählen
        int monthrechne=month;//1-12

        int vorYear = year-1; //2020

        int stack=0;

        //Für vorjahresanzeige
        while (monthrechne <= 12) {
            //Für Achsenbeschriftung
            String monatJahresansicht = "leer";

            switch (monthrechne) {
                case 1:
                    monatJahresansicht = "Jan";
                    break;
                case 2:
                    monatJahresansicht = "Feb";
                    break;
                case 3:
                    monatJahresansicht = "Mar";
                    break;
                case 4:
                    monatJahresansicht = "Apr";
                    break;
                case 5:
                    monatJahresansicht = "Mai";
                    break;
                case 6:
                    monatJahresansicht = "Jun";
                    break;
                case 7:
                    monatJahresansicht = "Jul";
                    break;
                case 8:
                    monatJahresansicht = "Aug";
                    break;
                case 9:
                    monatJahresansicht = "Sep";
                    break;
                case 10:
                    monatJahresansicht = "Okt";
                    break;
                case 11:
                    monatJahresansicht = "Nov";
                    break;
                case 12:
                    monatJahresansicht = "Dez";
                    break;
            }
            //Datnbankzugriff: Kategorien


            float AusgabeWohnen = (float) MonthOutgoCategorie(31,monthrechne,vorYear,Data,"Wohnen");
            float AusgabeLebensmittel =(float) MonthOutgoCategorie(31,monthrechne,vorYear,Data,"Lebensmittel");
            float AUsgabeVerkehrsmittel = (float) MonthOutgoCategorie(31,monthrechne,vorYear,Data,"Verkehrsmittel");
            float AusgabeGesundheit =(float) MonthOutgoCategorie(31,monthrechne,vorYear,Data,"Gesundheit");
            float AusgabeFreizeit =(float) MonthOutgoCategorie(31,monthrechne,vorYear,Data,"Freizeit");
            float AusgabeSonstiges = (float) MonthOutgoCategorie(31,monthrechne,vorYear,Data,"Sonstiges");

            //geht nicht!!!!
            M1.addBar(new BarModel(
                    //monatJahresansicht,
                    AusgabeWohnen,
                    Color.parseColor("#66BB6A")));

            StackKat1[stack].addBar(new BarModel(
                    //monatJahresansicht,
                    AusgabeLebensmittel,
                    Color.parseColor("#FFA726")));
            StackKat1[stack].addBar(new BarModel(
                    monatJahresansicht,
                    AUsgabeVerkehrsmittel,
                    Color.parseColor("#EF5350")));
            StackKat1[stack].addBar(new BarModel(
                    monatJahresansicht,
                    AusgabeGesundheit,
                    Color.parseColor("#29B6F6")));
            StackKat1[stack].addBar(new BarModel(
                    monatJahresansicht,
                    AusgabeFreizeit,
                    Color.parseColor("#A5B6DF")));
            StackKat1[stack].addBar(new BarModel(
                    monatJahresansicht,
                    AusgabeSonstiges,
                    Color.parseColor("#FF3AFA")));
            monthrechne++;
            stack++;
        }

        //dieses Jahr anzeigen
        //letzer Monata wird die Achse nicht beschriftet
        while (mo <= (month)) {
            //Für Achsenbeschriftung
            String monatJahresansicht = "leer";

            switch (mo) {
                case 1:
                    monatJahresansicht = "Jan";
                    break;
                case 2:
                    monatJahresansicht = "Feb";
                    break;
                case 3:
                    monatJahresansicht = "Mar";
                    break;
                case 4:
                    monatJahresansicht = "Apr";
                    break;
                case 5:
                    monatJahresansicht = "Mai";
                    break;
                case 6:
                    monatJahresansicht = "Jun";
                    break;
                case 7:
                    monatJahresansicht = "Jul";
                    break;
                case 8:
                    monatJahresansicht = "Aug";
                    break;
                case 9:
                    monatJahresansicht = "Sep";
                    break;
                case 10:
                    monatJahresansicht = "Okt";
                    break;
                case 11:
                    monatJahresansicht = "Nov";
                    break;
                case 12:
                    monatJahresansicht = "Dez";
                    break;
            }
            //Datnbankzugriff:

            float AusgabeWohnen = (float) MonthOutgoCategorie(31,mo,year,Data,"Wohnen");
            float AusgabeLebensmittel =(float) MonthOutgoCategorie(31,mo,year,Data,"Lebensmittel");
            float AUsgabeVerkehrsmittel = (float) MonthOutgoCategorie(31,mo,year,Data,"Verkehrsmittel");
            float AusgabeGesundheit =(float) MonthOutgoCategorie(31,mo,year,Data,"Gesundheit");
            float AusgabeFreizeit =(float) MonthOutgoCategorie(31,mo,year,Data,"Freizeit");
            float AusgabeSonstiges = (float) MonthOutgoCategorie(31,mo,year,Data,"Sonstiges");

            StackKat1[stack].addBar(new BarModel(
                    monatJahresansicht,
                    AusgabeWohnen,
                    Color.parseColor("#66BB6A")));
            StackKat1[stack].addBar(new BarModel(
                    monatJahresansicht,
                    AusgabeLebensmittel,
                    Color.parseColor("#FFA726")));
            StackKat1[stack].addBar(new BarModel(
                    monatJahresansicht,
                    AUsgabeVerkehrsmittel,
                    Color.parseColor("#EF5350")));
            StackKat1[stack].addBar(new BarModel(
                    monatJahresansicht,
                    AusgabeGesundheit,
                    Color.parseColor("#29B6F6")));
            StackKat1[stack].addBar(new BarModel(
                    monatJahresansicht,
                    AusgabeFreizeit,
                    Color.parseColor("#A5B6DF")));
            StackKat1[stack].addBar(new BarModel(
                    monatJahresansicht,
                    AusgabeSonstiges,
                    Color.parseColor("#FF3AFA")));


            mo++;
            stack++;
        }
        //Darstellungsoptionen
        BarChartyear.startAnimation();
        BarChartyear.setShowValues(true);
        BarChartyear.setActivated(false);

    }*/

    public void changeMonth1(View view)
    {
        setData();
    }

    public void changeMonth2(View view)
    {
        setData();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.navigation_menu, menu);
        MenuItem item2 = menu.findItem(R.id.itemPdfCreator);
        item2.setEnabled(false);
        MenuItem item3 = menu.findItem(R.id.itemBudgetLimit);
        item3.setEnabled(false);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.itemMainPage:
                Intent switchToMain = new Intent(this, MainActivity.class);
                startActivity(switchToMain);
                return true;

            case R.id.itemAddIntakesOutgoes:
                mySQLite = new MySQLite(this);
                ArrayList<Category> categories = mySQLite.getAllCategory();
                Intent switchToAddEntry = new Intent(this, AddEntryActivity.class);
                switchToAddEntry.putExtra("list",categories);
                mySQLite.close();
                startActivityForResult(switchToAddEntry,REQUESTCODE_ADD);
                return true;

            case R.id.subitemIntakes:
                mySQLite = new MySQLite(this);
                ArrayList<Intake> intakes = mySQLite.getMonthIntakes(day,month,year);
                Intent getIntakes = new Intent(this, ShowEntriesActivity.class);
                getIntakes.putExtra("list",(Serializable) intakes);
                getIntakes.putExtra("entry","Intake");
                mySQLite.close();
                startActivityForResult(getIntakes, REQUESTCODE_SHOW);
                return true;

            case R.id.subitemOutgoes:
                mySQLite = new MySQLite(this);
                ArrayList<Outgo> outgoes = mySQLite.getMonthOutgos(day, month, year);
                Intent getOutgoes = new Intent(this, ShowEntriesActivity.class);
                getOutgoes.putExtra("list",(Serializable) outgoes);
                getOutgoes.putExtra("entry","Outgo");
                mySQLite.close();
                startActivityForResult(getOutgoes, REQUESTCODE_SHOW);
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
                ArrayList<Category> categories1 = mySQLite.getAllCategory();
                switchToAddCategory.putExtra("list",(Serializable) categories1);
                mySQLite.close();
                startActivityForResult(switchToAddCategory, REQUESTCODE_ADD_CATEGORY);
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