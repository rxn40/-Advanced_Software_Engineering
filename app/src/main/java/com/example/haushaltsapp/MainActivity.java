package com.example.haushaltsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.graphics.Color;
import android.widget.TextView;
import com.example.haushaltsapp.Database.Category;
import com.example.haushaltsapp.Database.Intake;
import com.example.haushaltsapp.Database.MySQLite;
import com.example.haushaltsapp.Database.Outgo;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.PieModel;


public class MainActivity extends AppCompatActivity {

    //Textviews und Diagramme
    private TextView tvOutgo, tvIntake, tvResidualBudget;
    private PieChart pieChart;
    private BarChart mBarChart;

    private MySQLite mySQLite = new MySQLite(this, null, null, 0);

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

    /*
    Methode setzt die Variablen day, month, year mit aktuellem Datum
     */
    private void getDate() {
        Calendar calender = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String dates = dateFormat.format(calender.getTime());
        day = Integer.parseInt(dates.substring(0, 2));
        month = Integer.parseInt(dates.substring(3, 5));
        year = Integer.parseInt(dates.substring(6, 10));
    }

    /*
    Methode setzt beim aller ersten Ausführen der App beide Limits als nicht gesetzt
     */
    private void setLimitState() {
        String state = mySQLite.getStateLimitState("Gesamtlimit");
        if (state.equals("")) {
            mySQLite.addLimitState("Gesamtlimit", "false");
            mySQLite.addLimitState("Kategorielimit", "false");
        }
    }


    /*
    Methode legt beim aller ersten Ausführen der App die default Kategorien an
    */
    private void setCategories(){
        ArrayList<Category> categories = mySQLite.getAllCategories();
        if(categories.size() == 0){ //falls es noch keine Kategorien gibt, diese hier anlegen
            Category category = new Category("Verkehrsmittel", getResources().getColor(R.color.colorVerkehrsmittel), 0.0);
            mySQLite.addCategory(category);
            category = new Category("Wohnen",  getResources().getColor(R.color.colorWohnen), 0.0);
            mySQLite.addCategory(category);
            category = new Category("Lebensmittel", getResources().getColor(R.color.colorLebensmittel), 0.0);
            mySQLite.addCategory(category);
            category = new Category("Gesundheit",getResources().getColor(R.color.colorGesundheit), 0.0);
            mySQLite.addCategory(category);
            category = new Category("Freizeit", getResources().getColor(R.color.colorFreizeit), 0.0);
            mySQLite.addCategory(category);
            category = new Category("Sonstiges", getResources().getColor(R.color.colorSonstiges), 0.0);
            mySQLite.addCategory(category);
        }
    }


    /*
    Methode erstellt den Eintrag "Übertrag vom XX.YYYY" falls dieser noch nicht existiert
     */
    private void setLastBudget() {
        //Prüfen ob es einen solchen Eintrag gibt
        //Dazu erst den gewünschten Titel generieren
        String titel = "Übertrag vom ";
        if (month > 1) {
            titel = titel + (month - 1) + "." + year;
        } else {
            titel = titel + 12 + "." + (year - 1);
        }


        //Testen, ob es einen solchen Eintrag gibt.
        ArrayList<Intake> intakes = mySQLite.getMonthIntakes(day, month, year);
        ArrayList<Outgo> outgoes = mySQLite.getMonthOutgoes(day, month, year);

        boolean existsIntake = false;
        boolean existsOutgo = false;
        for (int i = 0; i < intakes.size(); i++) {
            if (intakes.get(i).getName().equals(titel)) {
                existsIntake = true;
            }
        }

        if (!existsIntake) { //Laufzeit -> Wenn ein Eintrag bereits in Einnahmen bestimmt
            for (int i = 0; i < outgoes.size(); i++) {
                if (outgoes.get(i).getName().equals(titel)) {
                    existsOutgo = true;
                }
            }
        }

        //Falls nicht -> Eintrag erstellen
        //Wenn Value negativ -> Outgo. Wenn positiv -> Intake
        if (!(existsIntake || existsOutgo)) {
            double value = 0.0;
            if (month > 1) {
                value = mySQLite.getValueIntakesMonth(31, month - 1, year) - mySQLite.getValueOutgoesMonth(31, month - 1, year);
            } else {
                value = mySQLite.getValueIntakesMonth(31, 12, year - 1) - mySQLite.getValueOutgoesMonth(31, 12, year - 1);
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


    //Runden eines Float-Wertes auf zwei Nachkommazahlen
    public float roundF(float number, int positions) {
        return (float) ((int)number + (Math.round(Math.pow(10,positions)*(number-(int)number)))/(Math.pow(10,positions)));
    }

    private void setData() {
        //Setzen der Textview
        tvIntake = findViewById(R.id.tvEinnahmen);
        tvOutgo = findViewById(R.id.tvAusgaben);
        tvResidualBudget = findViewById(R.id.tvRestbudget);

        pieChart = findViewById(R.id.piechart);
        mBarChart = findViewById(R.id.barchart);
        //Daten von Monat aus Datenbank:
        float outgo = roundF(mySQLite.getValueOutgoesMonth(day,month,year),2);
        float intake = roundF( mySQLite.getValueIntakesMonth(day,month,year),2);
        float residualBudget = roundF(intake-outgo,2);

        //Setzen der Werte in Textview
        tvIntake.setText(Float.toString(intake) + " €");
        tvOutgo.setText(Float.toString(outgo) + " €");
        tvResidualBudget.setText(Float.toString(residualBudget) + " €");

        //Diagramme zurücksetzten
        pieChart.clearChart();
        mBarChart.clearChart();
        //Diagramme aufrufen
        pieChart(outgo, residualBudget);
        barGraph(intake, outgo);
    }


    //Kreisdiagramm mit Ausgaben und Restbudget des aktuellen Monats füllen
    public void pieChart(float outgoValue, float budget) {
        pieChart.addPieSlice(new PieModel(
                "Ausgaben",
                outgoValue,
                Color.parseColor("#F94144")));

        //Restbudget wird nur angezeigt, wenn es positiv ist, sonst wird es nicht dagestellt
        if (budget > 0) {
            pieChart.addPieSlice(new PieModel(
                    "Restbudget",
                    budget,
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
    public void barGraph(float intakeValue, float outgoValue) {
        mBarChart.addBar(new BarModel(
                intakeValue,
                Color.parseColor("#90BE6D")));
        mBarChart.addBar(new BarModel(
                outgoValue,
                Color.parseColor("#F94144")));
        //Darstellungsoptionen
        mBarChart.startAnimation();
        mBarChart.setShowValues(false);  //werte auf Balken
        mBarChart.setActivated(false);

    }

    //Menüaufruf
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.navigation_menu, menu);

        //Die aktuelle Activity im Menü ausblenden
        MenuItem item = menu.findItem(R.id.itemMainPage);
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
}