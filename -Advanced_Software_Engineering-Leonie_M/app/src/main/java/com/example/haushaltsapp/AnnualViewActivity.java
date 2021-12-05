package com.example.haushaltsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.renderscript.Matrix3f;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.StackedBarChart;
import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.StackedBarModel;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AnnualViewActivity extends AppCompatActivity {

    private ValueLineChart LineChartyear;
    private BarChart BarChartyear;
    private StackedBarChart StackedBarchartKat;
    private StackedBarModel M1, M2, M3, M4, M5, M6, M7, M8, M9, M10, M11, M12;


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
        //Daten anzeigen
        setData();
    }

    private void setData()
    {
        BarChartyear = findViewById(R.id.barchart);
        StackedBarchartKat = findViewById(R.id.stackedbarchart);

        LineChartyear.clearChart();
        BarChartyear.clearChart();
        //StackedBarchartKat.clearChart();
        LineGraphMonth();
        BarGraphMonth();
        //StackedBarGraphMonth();

    }
    //Monatliche Ausgaben
    public double MonthOutgos(int day, int month, int year, ArrayList<Outgo> Data) {
        int lenght = Data.size();
        int i = 0;
        double monthOutgo = 0;
        double outvalue = 0;
        while (i < lenght) {
            if(Data.get(i).getYear()==year) {
                if (Data.get(i).getMonth() == month) {
                    outvalue = Data.get(i).getValue();
                    monthOutgo = monthOutgo + outvalue;
                }
            }
            i++;
        }
        return monthOutgo;
    }

    //Monatliche Einnahmen
    public double MonthIntakes(int day, int month, int year, ArrayList<Intake> DataIn) {
        int lenght = DataIn.size();
        int i = 0;
        double monthIntakes = 0;
        double invalue = 0;
        while (i < lenght) {
            if(DataIn.get(i).getYear()==year) {
                if (DataIn.get(i).getMonth() == month) {
                    invalue = DataIn.get(i).getValue();
                    monthIntakes = monthIntakes + invalue;
                }
            }
            i++;
        }
        return monthIntakes;
    }
    //Monatliche AUsgaben nach Kategorie
    public double MonthOutgoCategorie (int day, int month, int year, ArrayList<Outgo> Data, String categorie)
    {
        int lenght = Data.size();
        int i = 0;
        double monthOutgo = 0;
        double outvalue = 0;

        while (i < lenght) {
            if(Data.get(i).getYear()==year) {
                if (Data.get(i).getMonth() == month) {
                    String cat = Data.get(i).getCategory();
                    if (categorie.equals(cat)) {
                        outvalue = Data.get(i).getValue();
                        monthOutgo = monthOutgo + outvalue;
                    }
                }
            }
            i++;
        }
        return monthOutgo;
    }
    public void LineGraphMonth() {
        //Datenbankzugriff
        Intent intent = getIntent();
        ArrayList<Outgo> Data = (ArrayList<Outgo>) intent.getSerializableExtra("dataOut");
        //hier werden keine Daten übergeben!!! Null
        ArrayList<Intake> DataIn = (ArrayList<Intake>) intent.getSerializableExtra("dataIn");

        ValueLineSeries Outgoe = new ValueLineSeries();
        Outgoe.setColor(0xFFEF5350);

        //Einnahmen
        ValueLineSeries Input = new ValueLineSeries();
        Input.setColor(0xFF66BB6A);

        int mo = 1; //monate hochzählen
        int aktuellMonth = month; //1-12
        int monthrechne;
        if( month==1)
        {
            monthrechne =12;
        }
        else
        {
            monthrechne=month-1;
        }
        int vorYear = year-1; //2020

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
            //Datnbankzugriff: AUsgaben
            //hier Werte aus Dtenbank von einzelnen Monaten übergeben,-
            float AusgabeMonateX = (float) MonthOutgos(31,monthrechne,vorYear,Data);
            Outgoe.addPoint(new ValueLinePoint(monatJahresansicht, AusgabeMonateX));

            //Datnbankzugriff: Einnahmen
            float IntakeMonateX = (float) MonthIntakes(31,monthrechne,vorYear,DataIn);
            Input.addPoint(new ValueLinePoint(monatJahresansicht, IntakeMonateX));

            monthrechne++;
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
            float AusgabeMonateX = (float) MonthOutgos(31,mo,year,Data);
            Outgoe.addPoint(new ValueLinePoint(monatJahresansicht, AusgabeMonateX));

            //Datnbankzugriff: AUsgaben
            float IntakeMonateX = (float) MonthIntakes(31,mo,year,DataIn);
            Input.addPoint(new ValueLinePoint(monatJahresansicht, IntakeMonateX));

            mo++;
        }
        //Darstellungsoptionen
        LineChartyear.addSeries(Outgoe);
        LineChartyear.addSeries(Input);
        LineChartyear.startAnimation();
    }

    public void BarGraphMonth() {

        //Datenbankzugriff
        Intent intent = getIntent();
        ArrayList<Outgo> Data = (ArrayList<Outgo>) intent.getSerializableExtra("dataOut");
        ArrayList<Intake> DataIn = (ArrayList<Intake>) intent.getSerializableExtra("dataIn");


        int mo = 1; //monate hochzählen
        int monthrechne=month;//1-12

        int vorYear = year-1; //2020

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
            //Datnbankzugriff: AUsgaben
            //hier Werte aus Dtenbank von einzelnen Monaten übergeben,-
            float AusgabeMonateX = (float) MonthOutgos(31,monthrechne,vorYear,Data);
            BarChartyear.addBar(new BarModel(
                    monatJahresansicht,
                    AusgabeMonateX,
                    Color.parseColor("#EF5350")));
            //Datnbankzugriff: Einnahmen
            //float IntakeMonateX = (float) MonthIntakes(31,monthrechne,vorYear,DataIn);
            //Input.addPoint(new ValueLinePoint(monatJahresansicht, IntakeMonateX));

            monthrechne++;
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
            float AusgabeMonateX = (float) MonthOutgos(31,mo,year,Data);
            BarChartyear.addBar(new BarModel(
                    monatJahresansicht,
                    AusgabeMonateX,
                    Color.parseColor("#EF5350")));


            //Datnbankzugriff: AUsgaben
            //float IntakeMonateX = (float) MonthIntakes(31,mo,year,DataIn);
            //Input.addPoint(new ValueLinePoint(monatJahresansicht, IntakeMonateX));

            mo++;
        }
        //Darstellungsoptionen
        BarChartyear.startAnimation();
        BarChartyear.setShowValues(true);
        BarChartyear.setActivated(false);

    }

    public void StackedBarGraphMonth() {

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

    }




    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.annualview_menu, menu);
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
                Intent switchToBudgetLimit = new Intent(this, BudgetLimitActivity.class);
                startActivity(switchToBudgetLimit);
                return true;

            case R.id.itemTabelle:
                Intent switchToChart = new Intent(this, ChartViewActivity.class);
                startActivity(switchToChart);
                return true;

            case R.id.itemKalender:
                Intent switchToCalendar = new Intent(this, CalendarEventActivity.class);
                startActivity(switchToCalendar);
                return true;

            case R.id.itemTodoListe:
                Intent switchToToDoList = new Intent(this, ToDoListActivity.class);
                startActivity(switchToToDoList);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}