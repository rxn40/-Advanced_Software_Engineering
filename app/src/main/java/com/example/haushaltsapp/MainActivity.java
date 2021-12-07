package com.example.haushaltsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import android.graphics.Color;
import android.widget.TextView;
import android.widget.Toast;

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

    //REQUESTCODES
    private final int REQUESTCODE_ADD = 12; //AddEntryActivity
    private final int REQUESTCODE_SHOW = 13; //ShowEntryActivity
    private final int REQUESTCODE_EDIT = 14; //EditEntryActivity
    private final int REQUESTCODE_ADD_CATEGORY = 15; //AddCategoryActivity

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

        //Restbudget in den neuen Monat übernehmen
        setLastBudget();

        //Setzen der Texte im textview und pie Chart und Barchart
        tvIntake = findViewById(R.id.tvEinnahmen); //warum hier?
        tvOutgo = findViewById(R.id.tvAusgaben);
        tvResidualbudget = findViewById(R.id.tvRestbudget);

        pieChart = findViewById(R.id.piechart);
       mBarChart = findViewById(R.id.barchart);
        LineChart = findViewById(R.id.linechart);

        //Daten anzeigen
        setData();
    }

    // Setzt die Variablen day, month, year
    private void getDate(){
        java.util.Calendar calender = java.util.Calendar.getInstance();
        SimpleDateFormat datumsformat = new SimpleDateFormat("dd.MM.yyyy");
        String dates = datumsformat.format(calender.getTime());
        day = Integer.parseInt(dates.substring(0,2));
        month = Integer.parseInt(dates.substring(3,5));
        year = Integer.parseInt(dates.substring(6,10));
    }

    //Kategorien anlegen
    private void setCategories(){
        ArrayList<Category> categories = mySQLite.getAllCategory();
        if(categories.size() == 0){ //falls es noch keine Kategorien gibt, diese hier anlegen
            Category category = new Category("Verkehrsmittel", Color.parseColor("#EF5350"), 0.0);
            mySQLite.addCategory(category);
            category = new Category("Wohnen", Color.parseColor("#66BB6A"), 0.0);
            mySQLite.addCategory(category);
            category = new Category("Lebensmittel", Color.parseColor("#FFA726"), 0.0);
            mySQLite.addCategory(category);
            category = new Category("Gesundheit",Color.parseColor("#29B6F6"), 0.0);
            mySQLite.addCategory(category);
            category = new Category("Freizeit", Color.parseColor("#A5B6DF"), 0.0);
            mySQLite.addCategory(category);
            category = new Category("Sonstiges", Color.parseColor("#FF3AFA"), 0.0);
            mySQLite.addCategory(category);
        }
    }



    //Übertrage das Budget des letzten Monats
    private void setLastBudget(){

        //Prüfen ob es einen solchen Eintrag gibt
        //Dazu erst den gewünschten Titel generieren
        String titel = "Restbudget vom ";
        if(month > 1){
            titel = titel+(month-1)+"."+year;
        }else{
            titel = titel+12+"."+(year-1);
        }


        //Testen, ob es einen solchen Eintrag gibt. Später
        //mit Methode in der DB ersetzen -> Laufzeit
        ArrayList<Intake> intakes = mySQLite.getMonthIntakes(day, month, year);
        ArrayList<Outgo> outgoes = mySQLite.getMonthOutgos(day, month, year);

        boolean existsIntake = false;
        boolean existsOutgo = false;
        for (int i = 0; i < intakes.size(); i++){
            if(intakes.get(i).getName().equals(titel)){
                existsIntake = true;
            }
        }

        if(!existsIntake){ //Laufzeit
            for (int i = 0; i < outgoes.size(); i++){
                if(outgoes.get(i).getName().equals(titel)){
                    existsOutgo = true;
                }
            }
        }

        //falls nicht -> Eintrag erstellen
        //wenn value negaitv -> Outgo wenn positiv Intake
        if(!(existsIntake || existsOutgo)) {
            double value = 0.0;
            if (month > 1) {
                value = mySQLite.getValueIntakesMonth(31, month - 1, year) - mySQLite.getValueOutgosMonth(31, month - 1, year);
            } else {
                value = mySQLite.getValueIntakesMonth(31, 1, year - 1) - mySQLite.getValueOutgosMonth(31, 1, year - 1);
            }
            if(value >= 0) { //Einnahme
                Intake intake = new Intake(titel, value, 1, month, year, "einmalig");
                mySQLite.addIntake(intake);
            }else{ //Ausgabe
                value = value * (-1);
                Outgo outgo = new Outgo(titel, value, 1, month, year, "einmalig","Sonstiges");
                mySQLite.addOutgo(outgo);
            }
        }
    }

    //Werte aus der Datenbank
    private void setData()
    {
        //Daten aus Datenbank:
        //später noch Fester zur Datumsauswahl einfügen
        float outgo = mySQLite.getValueOutgosMonth(day,month,year);
        float intake = mySQLite.getValueIntakesMonth(day,month,year);
        float residualBudget = intake-outgo;

        //Setzen von Einnahmen und Ausgaben als Stirng in Textview
        tvIntake.setText(Float.toString(intake));
        tvOutgo.setText(Float.toString(outgo));
        tvResidualbudget.setText(Float.toString(residualBudget));

        //Diagramme zurücksetzten
        pieChart.clearChart();
        mBarChart.clearChart();
        //LineChart.clearChart();
        //Diagram Methoden aufrufen
        PieChart(intake,outgo,residualBudget);
        BarGraph(intake,outgo,residualBudget);
        //LineGraphMonth();

    }



    public void PieChart (float Einnahmen,float Ausgaben, float Restbudget)
    {
        //Daten und Farben zuordnen
        //es geht noch nicht die Farbe aus colors.xml zu übernehmen
       /* pieChart.addPieSlice(new PieModel(
                "Einnahmen",
                Einnahmen,
                Color.parseColor("#66BB6A")));

        */
        pieChart.addPieSlice(new PieModel(
                "Ausgaben",
                Ausgaben,
                Color.parseColor("#EF5350")));
        pieChart.addPieSlice(new PieModel(
                "Restbudget",
                Restbudget,
                Color.parseColor("#FFA726")));

        //Darstellungsoptionen
        pieChart.setInnerPaddingOutline(5);
        pieChart.startAnimation();
        pieChart.setBackgroundColor(0);
    }

    public void BarGraph(float Einnahmen,float Ausgaben, float Restbudget)
    {
        //Daten und Farben zuordnen
        mBarChart.addBar(new BarModel(
                Einnahmen,
                Color.parseColor("#66BB6A")));
        mBarChart.addBar(new BarModel(
                Ausgaben,
                Color.parseColor("#EF5350")));
        mBarChart.addBar(new BarModel(
                Restbudget,
                Color.parseColor("#FFA726")));
        //Darstellungsoptionen
        mBarChart.startAnimation();
        mBarChart.setShowValues(true);  //werte Aus Balken
        mBarChart.setActivated(false);

    }

    public void LineGraphMonth()
    {
        //Benötigt Monat als Sting und Geldwert als Float
        //für Monatsvergleich der Ausgaben
        ValueLineSeries series = new ValueLineSeries();
        series.setColor(0xFF56B7F1);
        int i =1; //monate hochzählen

        //aktuelles Datum abfragen über month
        //letzer Monata wird die Achse nicht beschriftet
        while (i<=(month+1))
        {
            //Für Achsenbeschriftung
            String monatJahresansicht ="leer";

            switch(i) {
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
            float AusgabeMonateX = mySQLite.getValueOutgosMonth(31, i, year);
            series.addPoint(new ValueLinePoint(monatJahresansicht, AusgabeMonateX));

            i++;
        }
        //Noch Jahresübergang einbringen
        //prüfen bis wann Ausgabe vorhaneden sind

        //Darstellungsoptionen
        LineChart.addSeries(series);
        LineChart.startAnimation();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.itemEinnahmenAusgaben:
                ArrayList<Category> categories = mySQLite.getAllCategory();
                Intent switchToAddEntry = new Intent(this, AddEntryActivity.class);
                switchToAddEntry.putExtra("list",categories);
                startActivityForResult(switchToAddEntry,REQUESTCODE_ADD);
                return true;

            case R.id.subitemEinnahmen:
                ArrayList<Intake> intakes = mySQLite.getMonthIntakes(day,month,year);
                Intent getIntakes = new Intent(this, ShowEntriesActivity.class);
                getIntakes.putExtra("list",(Serializable) intakes);
                getIntakes.putExtra("entry","Intake");
                startActivityForResult(getIntakes, REQUESTCODE_SHOW);
                return true;

            case R.id.subitemAusgaben:
                ArrayList<Outgo> outgoes = mySQLite.getMonthOutgos(day, month, year);
                Intent getOutgoes = new Intent(this, ShowEntriesActivity.class);
                getOutgoes.putExtra("list",(Serializable) outgoes);
                getOutgoes.putExtra("entry","Outgo");
                startActivityForResult(getOutgoes, REQUESTCODE_SHOW);
                return true;

            case R.id.itemBudgetLimit:
                Intent switchToBudgetLimit = new Intent(this, BudgetLimitActivity.class);
                startActivity(switchToBudgetLimit);
                return true;

            case R.id.itemDiagrammansicht:
                Intent switchToDiagramView = new Intent(this, DiagramViewActivity.class);
                //Alle Ausgaben in Datenbank
                ArrayList<Outgo> AlloutgoD =mySQLite.getAllOutgo();
                switchToDiagramView.putExtra("dataOut",AlloutgoD);
                //Alle Einnahmen in Datenbank
                ArrayList<Intake> AllIntakeD =mySQLite.getAllIntakes();
                switchToDiagramView.putExtra("dataIn",AllIntakeD);
                startActivity(switchToDiagramView);
                return true;

            case R.id.itemTabelle:
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
                startActivity(switchToChartView);
                return true;

            case R.id.itemKalender:
                Intent switchToCalender = new Intent(this, CalendarEventActivity.class);
                startActivity(switchToCalender);
                return true;

            case R.id.itemToDoListe:
                Intent switchToToDoList = new Intent(this, ToDoListActivity.class);
                startActivity(switchToToDoList);
                return true;

            case R.id.itemaddCategory:
                Intent switchToAddCategory = new Intent(this, AddCategoryActivity.class);

                ArrayList<Category> categories1 = mySQLite.getAllCategory();
                switchToAddCategory.putExtra("list",(Serializable) categories1);

                startActivityForResult(switchToAddCategory, REQUESTCODE_ADD_CATEGORY);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }



    //Funktion um die empfanenen Daten weiter zu verwerten
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Von AddEntryActivity
        if (resultCode == RESULT_OK && requestCode == REQUESTCODE_ADD) {
            String selection = data.getExtras().getString("selection");
            if(selection.equals("add")) {
                String entry = data.getExtras().getString("entry");
                if (entry.equals("Intake")) { //Eingabe
                    Intake intake = (Intake) data.getSerializableExtra("object");
                    mySQLite.addIntake(intake);
                } else { //Ausgabe
                    Outgo outgo = (Outgo) data.getSerializableExtra("object");
                    mySQLite.addOutgo(outgo);
                }
            }
        }

        // Von ShowEntryActivity
        if (resultCode == RESULT_OK && requestCode == REQUESTCODE_SHOW) {

            String entry = data.getExtras().getString("entry");
            int id = data.getExtras().getInt("id");

            Intent i = new Intent(this, EditEntryActivity.class);
            if(id > -1){
                if (entry.equals("Intake")) { //Einnahme
                    Intake intake = mySQLite.getIntakeById(id);
                    i.putExtra("object", (Serializable) intake);
                }else{
                    Outgo outgo = mySQLite.getOutgoById(id);
                    ArrayList<Category> categories = mySQLite.getAllCategory();
                    i.putExtra("object", (Serializable) outgo);
                    i.putExtra("list",categories);
                }
                i.putExtra("id", id);
                i.putExtra("entry", entry);
                startActivityForResult(i, REQUESTCODE_EDIT);
            }
        }

        //Eintrag löschen oder ändern von EditEntryActivity
        if(resultCode == RESULT_OK && requestCode == REQUESTCODE_EDIT){
            String selection = data.getExtras().getString("selection");
            String entry = data.getExtras().getString("entry");
            int id = data.getExtras().getInt("id");

            if(selection.equals("clear")){ //löschen
                if(entry.equals("Intake")){ //Intake
                    mySQLite.deleteIntakeById(id);
                }else{ //Outgo
                    mySQLite.deleteOutgoById(id);
                }
            }else{ //ändern
                if(entry.equals("Intake")){ //Intake
                    Intake intake = (Intake) data.getSerializableExtra("object");
                    mySQLite.updateIntake(intake, id);
                }else{ //Outgo
                    Outgo outgo = (Outgo) data.getSerializableExtra("object");
                    mySQLite.updateOutgo(outgo, id);
                }
            }
        }
        //Kategorie hinzufügen
        if(resultCode == RESULT_OK && requestCode == REQUESTCODE_ADD_CATEGORY){
            String selection = data.getExtras().getString("selection");
            if(selection.equals("ok")){
                Category category = (Category) data.getSerializableExtra("category");
               // mySQLite.addCategory(category);
                Toast.makeText(MainActivity.this, category.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        }
        setData();
    }
}