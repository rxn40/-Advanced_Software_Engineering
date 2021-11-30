package com.example.haushaltsplaner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import android.graphics.Color;
import android.widget.TextView;
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
    private MySQLiteIntake intakeDB = new MySQLiteIntake(this, null, null, 0);
    private MySQLiteOutgo outgoDB = new MySQLiteOutgo(this, null, null, 0);

    //REQUESTCODES
    private final int REQUESTCODE_ADD = 12; //AddEntryActivity
    private final int REQUESTCODE_SHOW = 13; //ShowEntryActivity
    private final int REQUESTCODE_EDIT = 14; //EditEntryActivity

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
        setContentView(R.layout.activity_main);

        //Erhalte das aktuelle Datum
        getDate();
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

    //Werte aus der Datenbank
    private void setData()
    {
        //Daten aus Datenbank:
        //später noch Fester zur Datumsauswahl einfügen
        float outgo = outgoDB.getValueOutgosMonth(day,month,year);
        float intake = intakeDB.getValueIntakesMonth(day,month,year);
        float residualBudget = intake-outgo;

        //Setzen von Einnahmen und Ausgaben als Stirng in Textview
        tvIntake.setText(Float.toString(intake));
        tvOutgo.setText(Float.toString(outgo));
        tvResidualbudget.setText(Float.toString(residualBudget));

        //Diagramme zurücksetzten
        pieChart.clearChart();
        mBarChart.clearChart();
        LineChart.clearChart();
        //Diagram Methoden aufrufen
        PieChart(intake,outgo,residualBudget);
        BarGraph(intake,outgo,residualBudget);
        LineGraphMonth();

    }


    public void PieChart (float Einnahmen,float Ausgaben, float Restbudget)
    {
        //Daten und Farben zuordnen
        //es geht noch nicht die Farbe aus colors.xml zu übernehmen
        pieChart.addPieSlice(new PieModel(
                "Einnahmen",
                Einnahmen,
                Color.parseColor("#66BB6A")));
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
            float AusgabeMonateX = outgoDB.getValueOutgosMonth(31, i, year);
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
                Intent switchToAddEntry = new Intent(this, AddEntryActivity.class);
                startActivityForResult(switchToAddEntry,REQUESTCODE_ADD);
                return true;

            case R.id.subitemEinnahmen:
                ArrayList<Intake> intakes = intakeDB.getMonthIntakes(day,month,year);
                Intent getIntakes = new Intent(this, ShowEntriesActivity.class);
                getIntakes.putExtra("list",(Serializable) intakes);
                getIntakes.putExtra("entry","Intake");
                startActivityForResult(getIntakes, REQUESTCODE_SHOW);
                return true;

            case R.id.subitemAusgaben:
                ArrayList<Outgo> outgoes = outgoDB.getMonthOutgos(day, month, year);
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
                startActivity(switchToDiagramView);
                return true;

            case R.id.itemTabelle:
                Intent switchToChartView = new Intent(this, ChartViewActivity.class);
                //Intent switchToAdapter = new Intent(this,OutgoListAdapter.A.class);
                ArrayList<Outgo> outgoesT = outgoDB.getMonthOutgos(day,month,year);
                switchToChartView.putExtra("list",(Serializable) outgoesT);
                //switchToAdapter.putExtra("list", (Serializable) outgoesT);
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
            String entry = data.getExtras().getString("entry");
            if(entry.equals("Intake")){ //Eingabe
                Intake intake = (Intake) data.getSerializableExtra("object");
                intakeDB.addIntake(intake);
            }else{ //Ausgabe
                Outgo outgo = (Outgo) data.getSerializableExtra("object");
                outgoDB.addOutgo(outgo);
            }
        }

        // Von ShoEntryActivity
        if (resultCode == RESULT_OK && requestCode == REQUESTCODE_SHOW) {
            String entry = data.getExtras().getString("entry");
            int id = data.getExtras().getInt("id");

            Intent i = new Intent(this, EditEntryActivity.class);

            if(id > -1) {
                if (entry.equals("Intake")) { //Einnahme
                    Intake intake = intakeDB.getIntakeById(id);
                    i.putExtra("object", (Serializable) intake);
                } else { //Ausgabe
                    Outgo outgo = outgoDB.getOutgoById(id);
                    i.putExtra("object", (Serializable) outgo);
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
                    intakeDB.deleteIntakeById(id);
                }else{ //Outgo
                    outgoDB.deleteOutgoById(id);
                }
            }else{ //ändern
                if(entry.equals("Intake")){ //Intake
                    Intake intake = (Intake) data.getSerializableExtra("object");
                    intakeDB.updateIntake(intake, id);
                }else{ //Outgo
                    Outgo outgo = (Outgo) data.getSerializableExtra("object");
                    outgoDB.updateOutgo(outgo, id);
                }
            }

        }
       setData();
    }

}