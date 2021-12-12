package com.example.haushaltsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.haushaltsapp.database.Category;
import com.example.haushaltsapp.database.Intake;
import com.example.haushaltsapp.database.MySQLite;
import com.example.haushaltsapp.database.Outgo;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.StackedBarChart;
import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.StackedBarModel;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AnnualViewActivity extends AppCompatActivity {

    ////Variabeln zur Menünavigation
    private MySQLite mySQLite;
    private final int REQUESTCODE_ADD = 12; //AddEntryActivity
    private final int REQUESTCODE_SHOW = 13; //ShowEntryActivity
    private final int REQUESTCODE_EDIT = 14; //EditEntryActivity
    private final int REQUESTCODE_ADD_CATEGORY = 15; //AddCategoryActivity
    ///////////////////////////////
    private MySQLite db;

    private ValueLineChart LineChartyear;
    private BarChart BarChartyear;

    private TextView tvM1, tvM2, tvM3, tvM4,tvM5,tvM6,tvM7,tvM8,tvM9,tvM10,tvM11,tvM12,tvM13;
    private TextView tvM1out, tvM2out, tvM3out, tvM4out,tvM5out,tvM6out,tvM7out,tvM8out,tvM9out,tvM10out,tvM11out,tvM12out,tvM13out;


    //aktuelles Datum
    private int day;
    private int month;
    private int year;

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
        setContentView(R.layout.activity_annual_view);

        getDate();
        LineChartyear = findViewById(R.id.linechart);

        db = new MySQLite(this);
        db.openDatabase();

        setData();
    }

    private void setData()
    {
        BarChartyear = findViewById(R.id.barchart);
        //StackedBarchartKat = findViewById(R.id.stackedbarchart);

        tvM1 = findViewById(R.id.tvMonth1);
        tvM2 = findViewById(R.id.tvMonth2);
        tvM3 = findViewById(R.id.tvMonth3);
        tvM4 = findViewById(R.id.tvMonth4);
        tvM5 = findViewById(R.id.tvMonth5);
        tvM6 = findViewById(R.id.tvMonth6);
        tvM7 = findViewById(R.id.tvMonth7);
        tvM8 = findViewById(R.id.tvMonth8);
        tvM9 = findViewById(R.id.tvMonth9);
        tvM10 = findViewById(R.id.tvMonth10);
        tvM11 = findViewById(R.id.tvMonth11);
        tvM12 = findViewById(R.id.tvMonth12);
        tvM13 = findViewById(R.id.tvMonth13);

        tvM1out = findViewById(R.id.tvout_Month1);
        tvM2out = findViewById(R.id.tvout_Month2);
        tvM3out = findViewById(R.id.tvout_Month3);
        tvM4out = findViewById(R.id.tvout_Month4);
        tvM5out = findViewById(R.id.tvout_Month5);
        tvM6out = findViewById(R.id.tvout_Month6);
        tvM7out = findViewById(R.id.tvout_Month7);
        tvM8out = findViewById(R.id.tvout_Month8);
        tvM9out = findViewById(R.id.tvout_Month9);
        tvM10out = findViewById(R.id.tvout_Month10);
        tvM11out = findViewById(R.id.tvout_Month11);
        tvM12out = findViewById(R.id.tvout_Month12);
        tvM13out = findViewById(R.id.tvout_Month13);

        LineChartyear.clearChart();
        BarChartyear.clearChart();
        LineGraphMonth();
        BarGraphMonth();

    }

    public void LineGraphMonth() {

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
    }

    public void BarGraphMonth() {

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





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.navigation_menu, menu);
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