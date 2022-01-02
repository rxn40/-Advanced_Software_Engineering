package com.example.haushaltsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import android.graphics.Color;
import android.widget.TextView;

import com.example.haushaltsapp.database.Category;
import com.example.haushaltsapp.database.Intake;
import com.example.haushaltsapp.database.MySQLite;
import com.example.haushaltsapp.database.Outgo;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.PieModel;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;






public class MainActivity extends AppCompatActivity {

    //Textviews und Diagramme
    private TextView tvOutgo, tvIntake, tvResidualbudget;
    private PieChart pieChart;
    private BarChart mBarChart;
    private ValueLineChart LineChart;

    //beide Datanbanken anlegen für die Einnahmen und Ausgaben
    private MySQLite mySQLite = new MySQLite(this, null, null, 0);

    //aktuelles Datum
    private int day;
    private int month;
    private int year;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mySQLite.deleteCategoryById(7);

        //Erhalte das aktuelle Datum
        getDate();

        //Kategorien setzen
        setCategories();

        //LimitState Database füllen
        setLimitState();

        //Restbudget in den neuen Monat übernehmen
        setLastBudget();

        //Daten anzeigen
        setData();

    }

    // Setzt die Variablen day, month, year
    private void getDate() {
        Intent intent = getIntent();
        java.util.Calendar calender = java.util.Calendar.getInstance();
        SimpleDateFormat datumsformat = new SimpleDateFormat("dd.MM.yyyy");
        String dates = datumsformat.format(calender.getTime());
        day = Integer.parseInt(dates.substring(0, 2));
        month = Integer.parseInt(dates.substring(3, 5));
        year = Integer.parseInt(dates.substring(6, 10));
    }


    //LimitState dafault anlegen
    private void setLimitState() {
        String state = mySQLite.getSateLimitState("Gesamtlimit");
        if (state.equals("")) {
            mySQLite.addLimitState("Gesamtlimit", "false");
            mySQLite.addLimitState("Kategorielimit", "false");
        }
    }


    //Kategorien anlegen
    private void setCategories(){
        ArrayList<Category> categories = mySQLite.getAllCategory();
        if(categories.size() == 0){ //falls es noch keine Kategorien gibt, diese hier anlegen
            Category category = new Category("Verkehrsmittel", getResources().getColor(R.color.Verkehrsmittel), 0.0);
            mySQLite.addCategory(category);
            category = new Category("Wohnen",  getResources().getColor(R.color.Wohnen), 0.0);
            mySQLite.addCategory(category);
            category = new Category("Lebensmittel", getResources().getColor(R.color.Lebensmittel), 0.0);
            mySQLite.addCategory(category);
            category = new Category("Gesundheit",getResources().getColor(R.color.Gesundheit), 0.0);
            mySQLite.addCategory(category);
            category = new Category("Freizeit", getResources().getColor(R.color.Freizeit), 0.0);
            mySQLite.addCategory(category);
            category = new Category("Sonstiges", getResources().getColor(R.color.Sonstiges), 0.0);
            mySQLite.addCategory(category);
        }
    }


    //Übertrage das Budget des letzten Monats
    private void setLastBudget() {

        //Prüfen ob es einen solchen Eintrag gibt
        //Dazu erst den gewünschten Titel generieren
        String titel = "Übertrag vom ";
        if (month > 1) {
            titel = titel + (month - 1) + "." + year;
        } else {
            titel = titel + 12 + "." + (year - 1);
        }


        //Testen, ob es einen solchen Eintrag gibt. Später
        //mit Methode in der DB ersetzen -> Laufzeit
        ArrayList<Intake> intakes = mySQLite.getMonthIntakes(day, month, year);
        ArrayList<Outgo> outgoes = mySQLite.getMonthOutgos(day, month, year);

        boolean existsIntake = false;
        boolean existsOutgo = false;
        for (int i = 0; i < intakes.size(); i++) {
            if (intakes.get(i).getName().equals(titel)) {
                existsIntake = true;
            }
        }

        if (!existsIntake) { //Laufzeit
            for (int i = 0; i < outgoes.size(); i++) {
                if (outgoes.get(i).getName().equals(titel)) {
                    existsOutgo = true;
                }
            }
        }

        //falls nicht -> Eintrag erstellen
        //wenn value negaitv -> Outgo wenn positiv Intake
        if (!(existsIntake || existsOutgo)) {
            double value = 0.0;
            if (month > 1) {
                value = mySQLite.getValueIntakesMonth(31, month - 1, year) - mySQLite.getValueOutgosMonth(31, month - 1, year);
            } else {
                value = mySQLite.getValueIntakesMonth(31, 12, year - 1) - mySQLite.getValueOutgosMonth(31, 12, year - 1);
            }
            if (value >= 0) { //Einnahme
                Intake intake = new Intake(titel, value, 1, month, year, "einmalig");
                mySQLite.addIntake(intake);
            } else { //Ausgabe
                value = value * (-1);
                Outgo outgo = new Outgo(titel, value, 1, month, year, "einmalig", "Sonstiges");
                mySQLite.addOutgo(outgo);
            }
        }
    }


    //runden auf zwei Nachkommazahlen
    public float roundf(float zahl, int stellen) {
        return (float) ((int)zahl + (Math.round(Math.pow(10,stellen)*(zahl-(int)zahl)))/(Math.pow(10,stellen)));
    }

    //Werte aus der Datenbank
    private void setData() {
        //Setzen der textview
        tvIntake = findViewById(R.id.tvEinnahmen);
        tvOutgo = findViewById(R.id.tvAusgaben);
        tvResidualbudget = findViewById(R.id.tvRestbudget);

        pieChart = findViewById(R.id.piechart);
        mBarChart = findViewById(R.id.barchart);
        //Daten von Monat aus Datenbank:
        float outgo = roundf(mySQLite.getValueOutgosMonth(day,month,year),2);
        float intake = roundf( mySQLite.getValueIntakesMonth(day,month,year),2);
        float residualBudget = roundf(intake-outgo,2);

        //Setzen der Werte in Textview
        tvIntake.setText(Float.toString(intake) + " €");
        tvOutgo.setText(Float.toString(outgo) + " €");
        tvResidualbudget.setText(Float.toString(residualBudget) + " €");

        //Diagramme zurücksetzten
        pieChart.clearChart();
        mBarChart.clearChart();
        //Diagramme aufrufen
        PieChart(outgo, residualBudget);
        BarGraph(intake, outgo);
    }


    //Kreisdiagramm mit Ausgaben und Restbudget des aktuellen Monats
    public void PieChart(float Ausgaben, float Restbudget) {
        pieChart.addPieSlice(new PieModel(
                "Ausgaben",
                Ausgaben,
                Color.parseColor("#F94144")));

        //Restbudget wird nur angezeigt, wenn es positiv ist, sonst wird es nicht dagestellt
        if (Restbudget > 0) {
            pieChart.addPieSlice(new PieModel(
                    "Restbudget",
                    Restbudget,
                    Color.parseColor("#F9C74F")));
        }
        else {
            pieChart.addPieSlice(new PieModel(
                    "Restbudget",
                    0,
                    Color.parseColor("#F9C74F")));
        }

        //Darstellungsoptionen
        pieChart.setInnerPaddingOutline(5);
        pieChart.startAnimation();
        pieChart.setBackgroundColor(0);
    }

    //Balkendiagramm mit EInnahmen und Ausgaben des aktuellen Monats
    public void BarGraph(float Einnahmen, float Ausgaben) {
        mBarChart.addBar(new BarModel(
                Einnahmen,
                Color.parseColor("#90BE6D")));
        mBarChart.addBar(new BarModel(
                Ausgaben,
                Color.parseColor("#F94144")));
        //Darstellungsoptionen
        mBarChart.startAnimation();
        mBarChart.setShowValues(true);  //werte auf Balken
        mBarChart.setActivated(false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.navigation_menu, menu);

        //Die aktuelle Activity im Menü ausblenden
        MenuItem item = menu.findItem(R.id.itemMainPage);
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
}