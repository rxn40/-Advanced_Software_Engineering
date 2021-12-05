package com.example.haushaltsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;

import android.os.Bundle;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.PieModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DiagramViewActivity extends AppCompatActivity {

    private Button changeToAnnual;

    private TextView tvWohnen, tvLebensmittel, tvGesundheit, tvVerkehrsmittel, tvFreizeit, tvSonstiges;
    private PieChart pieChart;
    private BarChart mBarChart;

    //aktuelles Datum
    private int day;
    private int month;
    private int year;

    private EditText editTextDate; //Datum
    private String dates;

    //zur Jahresansicht
    private static final int REQUESTCODE = 30;


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
        setContentView(R.layout.activity_diagram_view);
        changeToAnnual = findViewById(R.id.changeView);

        //Erhalte das aktuelle Datum
        getDate();

        //Aktuelles Datum anzeigen
        editTextDate = (EditText) findViewById(R.id.editTextDate);
        java.util.Calendar calender = Calendar.getInstance();
        SimpleDateFormat datumsformat = new SimpleDateFormat("dd.MM.yyyy");
        editTextDate.setText(datumsformat.format(calender.getTime()));

        setData();
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

    private void setData() {

        Intent intent = getIntent();
        ArrayList<Outgo> Data = (ArrayList<Outgo>) intent.getSerializableExtra("dataOut");
        //Datum von Textfeld
        dates = editTextDate.getText().toString();
        day = Integer.parseInt(dates.substring(0,2));
        month = Integer.parseInt(dates.substring(3,5));
        year = Integer.parseInt(dates.substring(6,10));

        //Setzen der Texte im textview und pie Chart und Barchart
        tvWohnen =findViewById(R.id.tvWohnen);
        tvLebensmittel = findViewById(R.id.tvLebensmittel);
        tvVerkehrsmittel = findViewById(R.id.tvVerkehrsmittel);
        tvGesundheit = findViewById(R.id.tvGesundheit);
        tvFreizeit = findViewById(R.id.tvFreizeit);
        tvSonstiges = findViewById(R.id.tvSonstiges);
        pieChart = findViewById(R.id.piechart);
        mBarChart = findViewById(R.id.barchart);

        float Kosten_Wohnen = (float) MonthOutgoCategorie(day,month,year,Data,"Wohnen");
        float Kosten_Lebensmittel = (float) MonthOutgoCategorie(day,month,year,Data,"Lebensmittel");
        float Kosten_Verkehrsmittel = (float) MonthOutgoCategorie(day,month,year,Data,"Verkehrsmittel");
        float Kosten_Gesundheit = (float) MonthOutgoCategorie(day,month,year,Data,"Gesundheit");
        float Kosten_Freizeit = (float) MonthOutgoCategorie(day,month,year,Data,"Freitzeit");
        float Kosten_Sonstiges = (float) MonthOutgoCategorie(day,month,year,Data,"Sonstiges");

        //Direktes Einbinden von Geldwert in TV
        tvWohnen.setText(Float.toString(Kosten_Wohnen));
        tvLebensmittel.setText(Float.toString(Kosten_Lebensmittel));
        tvVerkehrsmittel.setText(Float.toString(Kosten_Verkehrsmittel));
        tvGesundheit.setText(Float.toString(Kosten_Gesundheit));
        tvFreizeit.setText(Float.toString(Kosten_Freizeit));
        tvSonstiges.setText(Float.toString(Kosten_Sonstiges));

        //Diagramme zurücksetzten
        pieChart.clearChart();
        mBarChart.clearChart();
        //Diagram Methoden aufrufen
        PieChartKat(Kosten_Wohnen, Kosten_Lebensmittel, Kosten_Verkehrsmittel, Kosten_Gesundheit, Kosten_Freizeit, Kosten_Sonstiges);//hier noch float von Kosten übergeben
        BarGraphKat(Kosten_Wohnen, Kosten_Lebensmittel, Kosten_Verkehrsmittel, Kosten_Gesundheit, Kosten_Freizeit, Kosten_Sonstiges);//hier noch float von Kosten übergeben

    }
    public void PieChartKat (float Wohnen,float Lebensmittel, float Verkehrsmittel, float Gesundheit, float Freizeit,float Sonstiges)
    {
        //Daten und Farben dem PieChart zuordnen
        //es geht noch nicht die Farbe aus colors.xml zu übernehmen
        pieChart.addPieSlice(
                new PieModel(
                        "Wohnen",
                        Wohnen,
                        Color.parseColor("#66BB6A")));
        pieChart.addPieSlice(
                new PieModel(
                        "Lebensmittel",
                        Lebensmittel,
                        Color.parseColor("#FFA726")));
        pieChart.addPieSlice(
                new PieModel(
                        "Verkehrsmittel",
                        Verkehrsmittel,
                        Color.parseColor("#EF5350")));
        pieChart.addPieSlice(
                new PieModel(
                        "Gesundheit",
                        Gesundheit,
                        Color.parseColor("#29B6F6")));
        pieChart.addPieSlice(
                new PieModel(
                        "Freizeit",
                        Freizeit,
                        Color.parseColor("#A5B6DF")));
        pieChart.addPieSlice(
                new PieModel(
                        "Sonstiges",
                        Sonstiges,
                        Color.parseColor("#FF3AFA")));
        pieChart.setInnerPaddingOutline(5);
        pieChart.setInnerPaddingOutline(5);

        // To animate the pie chart
        pieChart.startAnimation();
        pieChart.setBackgroundColor(0);
    }

    public void BarGraphKat(float Wohnen,float Lebensmittel, float Verkehrsmittel, float Gesundheit, float Freizeit,float Sonstiges)
    {
        mBarChart.addBar(new BarModel(
                "Wohnen",//Über weite in activity einstellen, welcher Text lesbar ist
                Wohnen,
                Color.parseColor("#66BB6A")));
        mBarChart.addBar(new BarModel(
                "Lebensmittel",
                Lebensmittel,
                Color.parseColor("#FFA726")));
        mBarChart.addBar(new BarModel(
                "Verkehrsmittel",
                Verkehrsmittel,
                Color.parseColor("#EF5350")));
        mBarChart.addBar(new BarModel(
                "Gesundheit",
                Gesundheit,
                Color.parseColor("#29B6F6")));
        mBarChart.addBar(new BarModel(
                "Freizeit",
                Freizeit,
                Color.parseColor("#A5B6DF")));
        mBarChart.addBar(new BarModel(
                "Sonstiges",
                Sonstiges,
                Color.parseColor("#FF3AFA")));
        //letzter Balken hat keine Beschriftung??? Breite von Darstellung in XML
        //EIn Balken dann ohne Wert und Farbe einfügen?

        //mBarChart.callOnClick();
        mBarChart.startAnimation();
        mBarChart.setShowValues(true);  //keine Kommazahl darzustellen
        //mBarChart.setAccessibilityHeading(true);
        mBarChart.setActivated(false);
    }


    public void changeMonth(View view)
    {
        setData();
    }
    //Link zu Jahresansicht
    //Platzierung noch ändern
    public void changeToAnnual(View view) {

        Intent intent = getIntent();
        ArrayList<Outgo> Data = (ArrayList<Outgo>) intent.getSerializableExtra("dataOut");
        ArrayList<Intake> DataIn = (ArrayList<Intake>) intent.getSerializableExtra("dataIn");

        Intent switchToAnnualView= new Intent(this, AnnualViewActivity.class);
        ArrayList<Outgo> AlloutgoD =Data;
        switchToAnnualView.putExtra("dataOut",AlloutgoD);
        ArrayList<Intake> AllIntakes =DataIn;
        switchToAnnualView.putExtra("dataIn",AllIntakes);
        startActivity(switchToAnnualView);
        //noch Datenbank mitgeben
        }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.diagramview_menu, menu);
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