package com.example.haushaltsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.haushaltsapp.database.Category;
import com.example.haushaltsapp.database.Intake;
import com.example.haushaltsapp.database.MySQLite;
import com.example.haushaltsapp.database.Outgo;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.models.BarModel;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MonthcomparisonViewActivity extends AppCompatActivity {
    ////Variabeln zur Menünavigation
    private MySQLite mySQLite;
    private final int REQUESTCODE_ADD = 12; //AddEntryActivity
    private final int REQUESTCODE_SHOW = 13; //ShowEntryActivity
    private final int REQUESTCODE_EDIT = 14; //EditEntryActivity
    private final int REQUESTCODE_ADD_CATEGORY = 15; //AddCategoryActivity
    ///////////////////////////////
    private MySQLite db;

    private BarChart BarChartInOutcomparison;

    private TextView tvM1o, tvM2o, tvM1i, tvM2i;
    private TextView tvM1out, tvM2out,tvM1in, tvM2in;

    //aktuelles Datum
    private int day, day1, day2;
    private int month, month1, month2;
    private int year, year1, year2;
    private String datesM1, datesM2;

    private TextView editTextDateM1; //Datum M1
    private TextView editTextDateM2; //Datum M2
    private long startDateInMilliSec;
    private long endDateInMilliSec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthcomparison_view);

        db = new MySQLite(this);
        //db.openDatabase();

        //Aktuelles Datum anzeigen
        editTextDateM1 = (TextView) findViewById(R.id.editTextDateM1);
        editTextDateM2 = (TextView) findViewById(R.id.editTextDateM2);
        java.util.Calendar calender = Calendar.getInstance();
        SimpleDateFormat datumsformat = new SimpleDateFormat("dd.MM.yyyy");

        //Beide Tage auf aktuelles Datum setzen
        editTextDateM1.setText(datumsformat.format(calender.getTime()));
        editTextDateM2.setText(datumsformat.format(calender.getTime()));


        setData();
    }

    private void setData()
    {
        //Datum von Textfeld auslesen
        datesM1 = editTextDateM1.getText().toString();
        day1 = Integer.parseInt(datesM1.substring(0,2));
        month1 = Integer.parseInt(datesM1.substring(3,5));
        year1 = Integer.parseInt(datesM1.substring(6,10));

        datesM2 = editTextDateM2.getText().toString();
        day2 = Integer.parseInt(datesM2.substring(0,2));
        month2 = Integer.parseInt(datesM2.substring(3,5));
        year2 = Integer.parseInt(datesM2.substring(6,10));

        BarChartInOutcomparison= findViewById(R.id.barchartinout);

        tvM1o = findViewById(R.id.tvMonth1);
        tvM2o = findViewById(R.id.tvMonth2);

        tvM1i = findViewById(R.id.tvMonth1in);
        tvM2i = findViewById(R.id.tvMonth2in);

        tvM1out = findViewById(R.id.tvout_Month1);
        tvM2out = findViewById(R.id.tvout_Month2);

        tvM1in =findViewById(R.id.tvin_Month1);
        tvM2in =findViewById(R.id.tvin_Month2);

        BarChartInOutcomparison.clearChart();

        //BarGraphMonthInOut();
        BarGraphComparision(month1,year1,month2,year2);
        setTextInOut(month1,year1,month2,year2);

    }

    public  void openCalenderM1(View dateview) {
        java.util.Calendar calender = java.util.Calendar.getInstance();
        year1 = calender.get(Calendar.YEAR);
        month1 = calender.get(Calendar.MONTH);
        day1 = calender.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dateDialog = new DatePickerDialog(MonthcomparisonViewActivity.this, new DatePickerDialog.OnDateSetListener() {

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

                //editTextDate.setText(selectedYear + "/" + (selectedMonth + 1) + "/" + selectedDay);

                //Übergabe der Daten an Kalender-Objekt und Setzen von Start und Endzeit)
                calender.set(year1, month1, day1, 8, 0, 0);
                startDateInMilliSec = calender.getTimeInMillis();
                calender.set(year1, month1, day1, 9, 0, 0);
                endDateInMilliSec = calender.getTimeInMillis();
            }
        }, year1, month1, day1);
        dateDialog.show();
    }

    public  void openCalenderM2(View dateview) {
        java.util.Calendar calender = java.util.Calendar.getInstance();
        year2 = calender.get(Calendar.YEAR);
        month2 = calender.get(Calendar.MONTH);
        day2 = calender.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dateDialog = new DatePickerDialog(MonthcomparisonViewActivity.this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {

                day2 = selectedDay;
                month2 = selectedMonth + 1; //richtige monatszahl
                year2 = selectedYear;

                //Addition bei Monat von 1, Index beginnend bei 0
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

                //editTextDate.setText(selectedYear + "/" + (selectedMonth + 1) + "/" + selectedDay);

                //Übergabe der Daten an Kalender-Objekt und Setzen von Start und Endzeit)
                calender.set(year2, month2, day2, 8, 0, 0);
                startDateInMilliSec = calender.getTimeInMillis();
                calender.set(year2, month2, day2, 9, 0, 0);
                endDateInMilliSec = calender.getTimeInMillis();
            }
        }, year2, month2, day2);
        dateDialog.show();
    }


    public void BarGraphComparision(int month1, int year1, int month2, int year2)
    {
        String monthtext1 = monthtoSting(month1);
        String monthtext2 =monthtoSting(month2);

        //Monat 1
        float IntakeMonth1 = db.getValueIntakesMonth(31,month1,year1);
        BarChartInOutcomparison.addBar(new BarModel(
                "           "+monthtext1 + " "+ year1,
                IntakeMonth1,
                Color.parseColor("#66BB6A")));
        float OutgoMonth1 = db.getValueOutgosMonth(31,month1,year1);
        BarChartInOutcomparison.addBar(new BarModel(
                "",//"Aus. "+monthtext1,
                OutgoMonth1,
                Color.parseColor("#EF5350")));

        //Monat 2
        float IntakeMonth2 = db.getValueIntakesMonth(31,month2,year2);
        BarChartInOutcomparison.addBar(new BarModel(
                "           "+monthtext2 + " "+ year2,
                IntakeMonth2,
                Color.parseColor("#66BB6A")));
        float OutgoMonth2 = db.getValueOutgosMonth(31,month2,year2);
        BarChartInOutcomparison.addBar(new BarModel(
                "",//"Aus. "+monthtext2,
                OutgoMonth2,
                Color.parseColor("#EF5350")));

    }

    public String monthtoSting(int month)
    {
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

    public void setTextInOut(int month1, int year1, int month2, int year2)
    {
        String monthtext1= monthtoSting(month1);
        String monthtext2 = monthtoSting(month2);
        tvM1out.setText(Float.toString(db.getValueOutgosMonth(31,month1,year1))+" €");
        tvM1o.setText(monthtext1+"."+year1);

        tvM1in.setText(Float.toString(db.getValueIntakesMonth(31,month1,year1))+" €");
        tvM1i.setText(monthtext1+"."+year1);

        tvM2out.setText(Float.toString(db.getValueOutgosMonth(31,month2,year2))+" €");
        tvM2o.setText(monthtext2+"."+year2);

        tvM2in.setText(Float.toString(db.getValueIntakesMonth(31,month2,year2))+" €");
        tvM2i.setText(monthtext2+"."+year2);
    }


    public void changeMonth1(View view)
    {
        setData();
    }

    public void changeMonth2(View view)
    {
        setData();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.navigation_menu, menu);
        MenuItem item2 = menu.findItem(R.id.itemPdfCreator);
        item2.setEnabled(false);
        MenuItem item3 = menu.findItem(R.id.itemBudgetLimit);
        item3.setEnabled(false);
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