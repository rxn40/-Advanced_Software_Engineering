package com.example.haushaltsplaner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import java.io.Serializable;
import java.util.ArrayList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.PieModel;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;




public class MainActivity extends AppCompatActivity {

    // Create the object of TextView
    // and PieChart class
    TextView tvAusgaben, tvEinnahmen, tvRestbudget;
    PieChart pieChart;
    BarChart mBarChart;
    ValueLineChart LineChart;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Ermittle das aktuelle Datum:
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        year = calendar.get(java.util.Calendar.YEAR);
        month = calendar.get(java.util.Calendar.MONTH);
        day = calendar.get(java.util.Calendar.DAY_OF_MONTH);

        //Setzen der Texte im textview und pie Chart und Barchart
        tvEinnahmen=findViewById(R.id.tvEinnahmen);
        tvAusgaben=findViewById(R.id.tvAusgaben);
        tvRestbudget=findViewById(R.id.tvRestbudget);

        pieChart = findViewById(R.id.piechart);
        mBarChart = findViewById(R.id.barchart);
        LineChart= findViewById(R.id.linechart);

        // Creating a method setData()
        setData();
    }


    private void setData()
    {
        //Daten aus Datenbank:
        //Datum muss noch als zeitraum und nicht als Datum angegeben werden, sonst kann nur ein Tag geholt werden

        float Ausgaben = outgoDB.getValueOutgosMonth(30,11,2021);
        float Einnahmen = intakeDB.getValueIntakesMonth(30,11,2021);
        //Testdaten
        //float Ausgaben =888.0f;
        //float Einnahmen =1000.0f;

        //Eventuell noch differenz für Restbudget
        float Restbudget = Einnahmen-Ausgaben;

        //Setzen von EInnahmen und Ausgaben als Stirng in Textview
        tvEinnahmen.setText(Float.toString(Einnahmen));
        tvAusgaben.setText(Float.toString(Ausgaben));
        tvRestbudget.setText(Float.toString(Restbudget));
        //zum Testen bei direkter EIngabe von Geldwerten
        //tvEinnahmen.setText(Integer.toString(1000));
        //tvAusgaben.setText(Integer.toString(888));
        //tvRestbudget.setText(Integer.toString(112));

        //Daten und Farben dem PieChart zuordnen
        //es geht noch nicht die Farbe aus colors.xml zu übernehmen
        pieChart.addPieSlice(

                new PieModel(
                        "Einnahmen",
                        Float.parseFloat(tvEinnahmen.getText().toString()),
                        Color.parseColor("#66BB6A")));
        pieChart.addPieSlice(
                new PieModel(
                        "Ausgaben",
                        Float.parseFloat(tvAusgaben.getText().toString()),
                        Color.parseColor("#EF5350")));
        pieChart.addPieSlice(
                new PieModel(
                        "Restbudget",
                        Float.parseFloat(tvRestbudget.getText().toString()),
                        Color.parseColor("#FFA726")));

        pieChart.setInnerPaddingOutline(5);

        // To animate the pie chart
        pieChart.startAnimation();
        pieChart.setBackgroundColor(0);


        //BarGraph
        BarChart mBarChart = (BarChart) findViewById(R.id.barchart);

        mBarChart.addBar(new BarModel(Einnahmen,  Color.parseColor("#66BB6A")));
        mBarChart.addBar(new BarModel(Ausgaben, Color.parseColor("#EF5350")));
        mBarChart.addBar(new BarModel(Restbudget, Color.parseColor("#FFA726")));

        mBarChart.startAnimation();
        mBarChart.setShowValues(true);  //keine Kommazahl darzustellen auf Balken
        mBarChart.setActivated(false);

        //LineChart
        //für Monatsvergleich später verwenden
        //Benötigt Monat als Sting und Geldwert als Float

        ValueLineSeries series = new ValueLineSeries();
        series.setColor(0xFF56B7F1);

        //Beschreibung
        //series.addPoint(new ValueLinePoint("Achsenbeschriftung", floatwert Übergeben));

        //Hier kannst du in den Werten noch die einzelenen Monate im Vergleich zueinander darstellen.
        //als float wert den Monatswert übergeben

        series.addPoint(new ValueLinePoint("Jan", 2.0f));
        series.addPoint(new ValueLinePoint("Feb", 1.0f));
        series.addPoint(new ValueLinePoint("Mar", 1.5f));
        series.addPoint(new ValueLinePoint("Apr", 2.0f));
        series.addPoint(new ValueLinePoint("Mai", 0.5f));
        series.addPoint(new ValueLinePoint("Jun", 4.0f));
        series.addPoint(new ValueLinePoint("Jul", 3.5f));
        series.addPoint(new ValueLinePoint("Aug", 2.4f));
        series.addPoint(new ValueLinePoint("Sep", 2.4f));
        series.addPoint(new ValueLinePoint("Oct", 3.4f));
        series.addPoint(new ValueLinePoint("Nov", .4f));
        series.addPoint(new ValueLinePoint("Dec", 1.3f));

        LineChart.addSeries(series);
        LineChart.startAnimation();
    }



    /*
    Darstellung des Menus aber ohne Funktionalität
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.overview_menu, menu);
        return true;
    }



    // get a passed Item and check which one was clicked
    @Override
    //Methode zum Aufrufen des Overview-Menus
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.itemEinnahmenAusgaben:
                Intent switchToAddEntry = new Intent(this, AddEntryActivity.class);
                startActivityForResult(switchToAddEntry,REQUESTCODE_ADD);
                return true;
            case R.id.subitemEinnahmen:
                ArrayList<Intake> intakes = intakeDB.getMonthIntakes(day,month,year);
                Intent switchActivityIntent1 = new Intent(this, ShowEntrysActivity.class);
                switchActivityIntent1.putExtra("list",(Serializable) intakes);
                switchActivityIntent1.putExtra("entry","Intake");
                startActivityForResult(switchActivityIntent1, REQUESTCODE_SHOW);   return true;
            case R.id.subitemAusgaben:
                ArrayList<Outgo> outgoes = outgoDB.getMonthOutgos(day, month, year);
                Intent switchActivityIntent2 = new Intent(this, ShowEntrysActivity.class);
                switchActivityIntent2.putExtra("list",(Serializable) outgoes);
                switchActivityIntent2.putExtra("entry","Outgo");
                startActivityForResult(switchActivityIntent2, REQUESTCODE_SHOW);
                return true;
            case R.id.itemBudgetLimit:
                Intent switchToBudgetLimit = new Intent(this, BudgetLimit.class);
                startActivity(switchToBudgetLimit);
                return true;

            case R.id.itemDiagrammansicht:
                Intent switchToEditDiagramView = new Intent(this, EditDiagramView.class);
                startActivity(switchToEditDiagramView);
                return true;

            case R.id.itemKalender:
                Intent switchToCalander = new Intent(this, Calendar.class);
                startActivity(switchToCalander);
                return true;

            case R.id.itemTodoListe:
                Intent switchToDoList = new Intent(this, ToDoList.class);
                startActivity(switchToDoList);
                return true;

            case R.id.itemTabelle:
                Intent switchTabelle = new Intent(this, Tabelle.class);
                startActivity(switchTabelle);
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

        //Eintrag löschen oder ändern
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
       //setData();
    }

}