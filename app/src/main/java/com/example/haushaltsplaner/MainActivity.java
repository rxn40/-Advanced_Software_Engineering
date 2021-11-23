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

    private MySQLiteIntake intakeDB = new MySQLiteIntake(this, null, null, 0);
    private MySQLiteOutgo outgoDB = new MySQLiteOutgo(this, null, null, 0);

    private final int REQUESTCODE_ADD = 12;
    private final int REQUESTCODE_SHOW = 13;
    private final int REQUESTCODE_EDIT = 14;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        //MEthode noch alte Methode von Kategorie, neue noch ergänzen
        //float Ausgaben =dboutgo.getValuesOutgosCategory(1,11,2021,  "Sonstiges");
        //float Einnahmen =dboutgo.getValuesOutgosCategory(1,11,2021,  "Sonstiges");
        //Testdaten
        //float Ausgaben =888.0f;
        float Ausgaben = outgoDB.getValueOutgosMonth(22,11,2021);
        float Einnahmen = intakeDB.getValueIntakesMonth(22,11,2021);

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

        pieChart.setInnerPaddingOutline(5);
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
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        int year = calendar.get(java.util.Calendar.YEAR);
        int month = calendar.get(java.util.Calendar.MONTH);
        int day = calendar.get(java.util.Calendar.DAY_OF_MONTH);

        switch (item.getItemId()){

            case R.id.item1:
                Toast.makeText(this,"Transaktionen ausgewählt",Toast.LENGTH_SHORT).show();
                //Erstellung Intent mit Empfänger, hier Transaktion Klasse
                //Intent switchActivityIntent = new Intent(this, Transaktion.class);
                // If data is need to be sent between activities
                //      EditText editText = (EditText) findViewById(R.id.editTextTextPersonName);
                //      String message = editText.getText().toString();
                //      intent.putExtra(EXTRA_MESSAGE, message);
                //Rufe Activity "Transaktion" auf
                //startActivity(switchActivityIntent);
                return true;
            case R.id.subitem1:
                Toast.makeText(this,"Einnahmen ausgewählt",Toast.LENGTH_SHORT).show();
                //Intent switchActivityIntent = new Intent(this, Einnahmen.class);
                // If data is need to be sent between activities
                //      EditText editText = (EditText) findViewById(R.id.editTextTextPersonName);
                //      String message = editText.getText().toString();
                //      intent.putExtra(EXTRA_MESSAGE, message);
                //startActivity(switchActivityIntent);


                ArrayList<Intake> intakes = intakeDB.getAllIntakes();
                Intent switchActivityIntent1 = new Intent(this, ShowIntakesActivity.class);
                switchActivityIntent1.putExtra("list",(Serializable) intakes);
                startActivityForResult(switchActivityIntent1, REQUESTCODE_SHOW);
                // startActivityForResult(switchActivityIntent1, 10);
                return true;
            case R.id.subitem2:
                Toast.makeText(this,"Ausgaben ausgewählt",Toast.LENGTH_SHORT).show();
                //Intent switchActivityIntent = new Intent(this, Ausgaben.class);
                // If data is need to be sent between activities
                //      EditText editText = (EditText) findViewById(R.id.editTextTextPersonName);
                //      String message = editText.getText().toString();
                //      intent.putExtra(EXTRA_MESSAGE, message);
                //startActivity(switchActivityIntent);
                /////////////////////////////////////////////////////
                ArrayList<Outgo> outgoes = outgoDB.getAllOutgo();
                Intent switchActivityIntent2 = new Intent(this, ShowOutgosActivity.class);
                switchActivityIntent2.putExtra("list",(Serializable) outgoes);
                startActivityForResult(switchActivityIntent2, REQUESTCODE_SHOW);
                return true;
            case R.id.item2:
                Toast.makeText(this,"Budget Limit ausgewählt",Toast.LENGTH_SHORT).show();
                //Erstellung Intent mit Empfänger, hier BudgetLimit Klasse
                //Intent switchActivityIntent = new Intent(this, BudgetLimit.class);
                // If data is need to be sent between activities
                //      EditText editText = (EditText) findViewById(R.id.editTextTextPersonName);
                //      String message = editText.getText().toString();
                //      intent.putExtra(EXTRA_MESSAGE, message);
                //Rufe Activity "Budget Limit" auf
                //startActivity(switchActivityIntent);
                // return true;
                Intent switchActivityIntent3 = new Intent(this, AddEntryActivity.class);
                startActivityForResult(switchActivityIntent3, REQUESTCODE_ADD);

            case R.id.item3:
                Toast.makeText(this,"Diagrammansicht ausgewählt",Toast.LENGTH_SHORT).show();
                //Intent switchActivityIntent = new Intent(this, Diagramm.class);
                // If data is need to be sent between activities
                //      EditText editText = (EditText) findViewById(R.id.editTextTextPersonName);
                //      String message = editText.getText().toString();
                //      intent.putExtra(EXTRA_MESSAGE, message);
                //Rufe Activity "Diagramm" auf
                //startActivity(switchActivityIntent);
                return true;

            case R.id.item4:
                //Erstellung Intent mit Empfänger, hier Calender Klasse
                Intent switchActivityIntent = new Intent(this, Calendar.class);
                // If data is need to be sent between activities
                //      EditText editText = (EditText) findViewById(R.id.editTextTextPersonName);
                //      String message = editText.getText().toString();
                //      intent.putExtra(EXTRA_MESSAGE, message);
                //Rufe Activity "Kalender" auf
                startActivity(switchActivityIntent);
                return true;
            case R.id.item5:
                //Erstellung Intent mit Empfänger, hier To-Do Liste Klasse
                Toast.makeText(this,"To-Do Liste ausgewählt",Toast.LENGTH_SHORT).show();
                //Rufe Klasse "To-Do Liste" auf
                //Intent switchActivityIntent = new Intent(this, ToDoList.class);
                // If data is need to be sent between activities
                //      EditText editText = (EditText) findViewById(R.id.editTextTextPersonName);
                //      String message = editText.getText().toString();
                //      intent.putExtra(EXTRA_MESSAGE, message);
                //Rufe Activity "To-Do Liste" auf
                //startActivity(switchActivityIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    //Trägt die gewünschten Daten in die Datenbank ein
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ///////////////////////////////////////////////////////////////
        //Daten hinzufügen
        //////////////////////////////////////////////////////////////
        if (resultCode == RESULT_OK && requestCode == REQUESTCODE_ADD) {
            //Später abfragen ob Einnahme oder Ausgabe
            String entry = data.getExtras().getString("entry");

            String name = data.getExtras().getString("name");
            double value = data.getExtras().getDouble("value");
            int day = data.getExtras().getInt("day");
            int month = data.getExtras().getInt("month");
            int year = data.getExtras().getInt("year");
            String cycle = data.getExtras().getString("cycle");

            if(entry.equals("Intake")){ //Eingabe
                Intake intake = new Intake( name,  value,  day,  month,  year, cycle);
                intakeDB.addIntake(intake);
            }else{ //Ausgabe
                Outgo outgo = new Outgo( name,  value,  day,  month,  year, cycle);
                outgoDB.addOutgo(outgo);
            }

        }

        ///////////////////////////////////////////////////////////////
        //Daten ausgeben
        //////////////////////////////////////////////////////////////
        if (resultCode == RESULT_OK && requestCode == REQUESTCODE_SHOW) {
            String entry = data.getExtras().getString("entry");
            int id = data.getExtras().getInt("id");

            if(entry.equals("Intake") && (id > -1)){
                Intake intake = intakeDB.getIntakeById(id);
                Intent switchActivityIntent1 = new Intent(this, EditEntryActivity.class);
                switchActivityIntent1.putExtra("Bezeichnung","Intake");
                switchActivityIntent1.putExtra("id",id);
                switchActivityIntent1.putExtra("name",intake.getName());
                switchActivityIntent1.putExtra("value",intake.getValue());
                switchActivityIntent1.putExtra("day",intake.getDay());
                switchActivityIntent1.putExtra("month",intake.getMonth());
                switchActivityIntent1.putExtra("year",intake.getYear());
                switchActivityIntent1.putExtra("cyclus",intake.getCycle());
                startActivityForResult(switchActivityIntent1, REQUESTCODE_EDIT);
            }

            /*
            int id = data.getExtras().getInt("id");
            if(data.getExtras().getString("entry").equals("intake") && (id > -1)){ //Intake
                Intake intake = intakeDB.getIntakeById(id);

                Intent switchActivityIntent = new Intent(this, EditEntryActivity.class);
                switchActivityIntent.putExtra("Bezeichnung","Intake");
                switchActivityIntent.putExtra("id",intake.getId());
                switchActivityIntent.putExtra("name",intake.getName());
                switchActivityIntent.putExtra("value",intake.getValue());
                switchActivityIntent.putExtra("day",intake.getDay());
                switchActivityIntent.putExtra("month",intake.getMonth());
                switchActivityIntent.putExtra("year",intake.getYear());
                switchActivityIntent.putExtra("cyclus",intake.getCycle());

                startActivityForResult(switchActivityIntent,REQUESTCODE_EDIT);



            }else if(id > -1){ //Outgo
                Outgo outgo = outgoDB.getOutgoById(id);

                Intent switchActivityIntent = new Intent(this, EditEntryActivity.class);
                switchActivityIntent.putExtra("Bezeichnung","Outgo");
                switchActivityIntent.putExtra("id",outgo.getId());
                switchActivityIntent.putExtra("name",outgo.getName());
                switchActivityIntent.putExtra("value",outgo.getValue());
                switchActivityIntent.putExtra("day",outgo.getDay());
                switchActivityIntent.putExtra("month",outgo.getMonth());
                switchActivityIntent.putExtra("year",outgo.getYear());
                switchActivityIntent.putExtra("cyclus",outgo.getCycle());

                startActivityForResult(switchActivityIntent,REQUESTCODE_EDIT);
            }

             */
        }

        ///////////////////////////////////////////////////////////////
        //Daten löschen oder ändern
        //////////////////////////////////////////////////////////////
        if (resultCode == RESULT_OK && requestCode == REQUESTCODE_EDIT){
            String selection = data.getExtras().getString("selection");
            int id = data.getExtras().getInt("id");
            String entry = data.getExtras().getString("entry");
            if(selection.equals("clear") && entry.equals("Intake")){
                intakeDB.deleteIntakeById(id);
            }else if(selection.equals("update") && entry.equals("Intake")){
                String name = data.getExtras().getString("name");
                double value = data.getExtras().getDouble("value");
                int day = data.getExtras().getInt("day");
                int month = data.getExtras().getInt("month");
                int year = data.getExtras().getInt("year");
                String cycle = data.getExtras().getString("cycle");

                Intake intake = new Intake(name, value, day, month, year, cycle);
                intakeDB.updateIntake(intake, id);
            }


            /*
            String selection = data.getExtras().getString("selection");
            if(selection.equals("loeschen")){
                int id = data.getExtras().getInt("id");
            }
            /*
            String selction = data.getExtras().getString("selection");
            if(selction.equals("clear")){
                String entry = data.getExtras().getString("Bezeichnung");
                if(entry.equals("Outgo")){
                    outgoDB.deleteOutgoById(data.getExtras().getInt("id"));
                }else{
                    intakeDB.deleteIntakeById(data.getExtras().getInt("id"));
                }



            }else{
                String entry = data.getExtras().getString("Bezeichnung");
                String name = data.getExtras().getString("name");
                double value = data.getExtras().getDouble("value");
                int day = data.getExtras().getInt("day");
                int month = data.getExtras().getInt("month");
                int year = data.getExtras().getInt("year");
                String cycle = data.getExtras().getString("cycle");
                if(entry.equals("Outgo")){
                    Outgo outgo = new Outgo( name,  value,  day,  month,  year, cycle);
                    outgoDB.updateOutgo(outgo,data.getExtras().getInt("id"));
                }else{
                    Intake intake = new Intake( name,  value,  day,  month,  year, cycle);
                    intakeDB.updateIntake(intake,data.getExtras().getInt("id"));
                }
            }

             */


        }
        setData();
    }

}