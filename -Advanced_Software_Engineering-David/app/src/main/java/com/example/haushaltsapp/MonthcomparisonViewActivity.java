package com.example.haushaltsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import com.example.haushaltsapp.database.MySQLite;
import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.models.BarModel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MonthcomparisonViewActivity extends AppCompatActivity {
    ////Variabeln zur Menünavigation
    private MySQLite mySQLite;
    ///////////////////////////////

    private BarChart barChartInOutcomparison;

    private TextView tvM1o, tvM2o, tvM1i, tvM2i;
    private TextView tvM1out, tvM2out,tvM1in, tvM2in;

    //aktuelles Datum
    private int day1, day2;
    private int month1, month2;
    private int year1, year2;
    private String datesM1, datesM2;

    private TextView editTextDateM1; //Datum M1
    private TextView editTextDateM2; //Datum M2

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthcomparison_view);

        mySQLite = new MySQLite(this);

        //Aktuelles Datum anzeigen
        editTextDateM1 = (TextView) findViewById(R.id.editTextDateM1);
        editTextDateM2 = (TextView) findViewById(R.id.editTextDateM2);
        Calendar calender = Calendar.getInstance();
        SimpleDateFormat datumsformat = new SimpleDateFormat("dd.MM.yyyy");

        //Beide Datums auf aktuelles Datum setzen
        editTextDateM1.setText(datumsformat.format(calender.getTime()));
        editTextDateM2.setText(datumsformat.format(calender.getTime()));

        setData();
    }

    private void setData() {
        //Datum von Textfeld auslesen
        datesM1 = editTextDateM1.getText().toString();
        day1 = Integer.parseInt(datesM1.substring(0,2));
        month1 = Integer.parseInt(datesM1.substring(3,5));
        year1 = Integer.parseInt(datesM1.substring(6,10));

        datesM2 = editTextDateM2.getText().toString();
        day2 = Integer.parseInt(datesM2.substring(0,2));
        month2 = Integer.parseInt(datesM2.substring(3,5));
        year2 = Integer.parseInt(datesM2.substring(6,10));

        barChartInOutcomparison = findViewById(R.id.barchartinout);

        tvM1o = findViewById(R.id.tvMonth1);
        tvM2o = findViewById(R.id.tvMonth2);

        tvM1i = findViewById(R.id.tvMonth1in);
        tvM2i = findViewById(R.id.tvMonth2in);

        tvM1out = findViewById(R.id.tvout_Month1);
        tvM2out = findViewById(R.id.tvout_Month2);

        tvM1in =findViewById(R.id.tvin_Month1);
        tvM2in =findViewById(R.id.tvin_Month2);

        barChartInOutcomparison.clearChart();
        barGraphComparision(month1,year1,month2,year2);
        setTextInOut(month1,year1,month2,year2);
    }

    //Kalender für auswahl des ersten Monats der Anzeige
    public  void openCalenderM1(View dateview) {
        Calendar calender = Calendar.getInstance();
        year1 = calender.get(Calendar.YEAR);
        month1 = calender.get(Calendar.MONTH);
        day1 = calender.get(Calendar.DAY_OF_MONTH);

        //Kalender auf Deutsch umstellen
        Locale locale = new Locale("de");
        Locale.setDefault(locale);
        Resources res = this.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.locale = locale;
        res.updateConfiguration(config, res.getDisplayMetrics());

        DatePickerDialog dateDialog = new DatePickerDialog(MonthcomparisonViewActivity.this,R.style.datePickerStyle, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {

                day1 = selectedDay;
                month1 = selectedMonth+1;
                year1 = selectedYear;

                if (day1<10)
                {
                    if(month1<10)
                    {
                        editTextDateM1.setText("0"+ selectedDay+".0"+month1+"."+selectedYear);
                    }
                    else {
                        editTextDateM1.setText("0" + selectedDay + "." + month1 + "." + selectedYear);
                    }
                }
                else {
                    if(month1<10)
                    {
                        editTextDateM1.setText(selectedDay+".0"+month1+"."+selectedYear);
                    }
                    else {
                        editTextDateM1.setText(selectedDay + "." + month1 + "." + selectedYear);
                    }
                }
            }
        }, year1, month1, day1);
        dateDialog.show();
    }

    //Kalender für auswahl des zweiten Monats der Anzeige
    public  void openCalenderM2(View dateview) {
        java.util.Calendar calender = java.util.Calendar.getInstance();
        year2 = calender.get(Calendar.YEAR);
        month2 = calender.get(Calendar.MONTH);
        day2 = calender.get(Calendar.DAY_OF_MONTH);

        //Kalender auf Deutsch umstellen
        Locale locale = new Locale("de");
        Locale.setDefault(locale);
        Resources res = this.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.locale = locale;
        res.updateConfiguration(config, res.getDisplayMetrics());

        DatePickerDialog dateDialog = new DatePickerDialog(MonthcomparisonViewActivity.this,R.style.datePickerStyle, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {

                day2 = selectedDay;
                month2 = selectedMonth + 1; //richtige monatszahl
                year2 = selectedYear;

                if (day2<10)
                {
                    if(month2<10)
                    {
                        editTextDateM2.setText("0"+ selectedDay+".0"+month2+"."+selectedYear);
                    }
                    else {
                        editTextDateM2.setText("0" + selectedDay + "." + month2 + "." + selectedYear);
                    }
                }
                else {
                    if(month2<10)
                    {
                        editTextDateM2.setText(selectedDay+".0"+month2+"."+selectedYear);
                    }
                    else {
                        editTextDateM2.setText(selectedDay + "." + month2 + "." + selectedYear);
                    }
                }
            }
        }, year2, month2, day2);
        dateDialog.show();
    }

    //Runden auf zwei Nachkommazahlen
    public float roundf(float number, int position) {
        return (float) ((int)number + (Math.round(Math.pow(10,position)*(number-(int)number)))/(Math.pow(10,position)));
    }

    //Balkendiagramm, mit Anzeige von zwei ausgewählten Monaten mit Ausgaben und Einnahmen
    public void barGraphComparision(int month1, int year1, int month2, int year2) {
        String monthtext1 = monthtoSting(month1);
        String monthtext2 =monthtoSting(month2);

        //Monat 1
        float intakeMonth1 = roundf(mySQLite.getValueIntakesMonth(31,month1,year1),2);
        barChartInOutcomparison.addBar(new BarModel(
                "           "+monthtext1 + " "+ year1,
                intakeMonth1,
                Color.parseColor("#90BE6D")));
        float outgoMonth1 = roundf(mySQLite.getValueOutgosMonth(31,month1,year1),2);
        barChartInOutcomparison.addBar(new BarModel(
                "",//"Aus. "+monthtext1,
                outgoMonth1,
                Color.parseColor("#F94144")));

        //Monat 2
        float intakeMonth2 = roundf(mySQLite.getValueIntakesMonth(31,month2,year2),2);
        barChartInOutcomparison.addBar(new BarModel(
                "           "+monthtext2 + " "+ year2,
                intakeMonth2,
                Color.parseColor("#90BE6D")));
        float outgoMonth2 = roundf(mySQLite.getValueOutgosMonth(31,month2,year2),2);
         barChartInOutcomparison.addBar(new BarModel(
                "",//"Aus. "+monthtext2,
                outgoMonth2,
                Color.parseColor("#F94144")));
    }

    //Monat in String umwandeln zur Anzeige in Textview
    public String monthtoSting(int month) {
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

    //Anzeige von beiden Monaten mit Werten zu Einnahmen und Ausgaben
    public void setTextInOut(int month1, int year1, int month2, int year2) {
        String monthtext1 = monthtoSting(month1);
        String monthtext2 = monthtoSting(month2);
        float round = roundf(mySQLite.getValueOutgosMonth(31,month1,year1),2);
        tvM1out.setText(Float.toString(round)+" €");
        tvM1o.setText(monthtext1+"."+year1);
        round = roundf(mySQLite.getValueIntakesMonth(31,month1,year1),2);
        tvM1in.setText(Float.toString(round)+" €");
        tvM1i.setText(monthtext1+"."+year1);

        round = roundf(mySQLite.getValueOutgosMonth(31,month2,year2),2);
        tvM2out.setText(Float.toString(round)+" €");
        tvM2o.setText(monthtext2+"."+year2);
        round = roundf(mySQLite.getValueIntakesMonth(31,month2,year2),2);
        tvM2in.setText(Float.toString(round)+" €");
        tvM2i.setText(monthtext2+"."+year2);
    }

    //Button zum setzen der Daten zu aktualisierung des Monats
    public void changeMonth(View view) {
        setData();
    }


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