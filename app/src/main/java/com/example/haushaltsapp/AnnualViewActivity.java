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
import com.example.haushaltsapp.Database.MySQLite;
import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.models.BarModel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AnnualViewActivity extends AppCompatActivity {

    private MySQLite mySQLite;
    ///////////////////////////////

    private BarChart barChartInOut;

    private TextView tvM1o, tvM2o, tvM3o, tvM4o,tvM5o,tvM6o,tvM7o,tvM8o,tvM9o,tvM10o,tvM11o,tvM12o,tvM13o;
    private TextView tvM1out, tvM2out, tvM3out, tvM4out,tvM5out,tvM6out,tvM7out,tvM8out,tvM9out,tvM10out,tvM11out,tvM12out,tvM13out;
    private TextView tvM1i, tvM2i, tvM3i, tvM4i,tvM5i,tvM6i,tvM7i,tvM8i,tvM9i,tvM10i,tvM11i,tvM12i,tvM13i;
    private  TextView tvM1in, tvM2in,tvM3in,tvM4in,tvM5in,tvM6in,tvM7in,tvM8in,tvM9in,tvM10in,tvM11in,tvM12in,tvM13in;

    private int day;
    private int month;
    private int year;

    private TextView editTextDate; //Datum
    private String date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annual_view);

        mySQLite = new MySQLite(this);

        //Aktuelles Datum auslesen und anzeigen
        editTextDate = (TextView) findViewById(R.id.editTextDate);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        editTextDate.setText(dateFormat.format(calendar.getTime()));

        setData();
    }

    private void setData() {
        //Datum vom Textfeld auslesen
        date = editTextDate.getText().toString();
        day = Integer.parseInt(date.substring(0,2));
        month = Integer.parseInt(date.substring(3,5));
        year = Integer.parseInt(date.substring(6,10));

        barChartInOut = findViewById(R.id.barChartInOut);

        //Anzeige Monat Ausgaben
        tvM1o = findViewById(R.id.tvMonth1);
        tvM2o = findViewById(R.id.tvMonth2);
        tvM3o = findViewById(R.id.tvMonth3);
        tvM4o = findViewById(R.id.tvMonth4);
        tvM5o = findViewById(R.id.tvMonth5);
        tvM6o = findViewById(R.id.tvMonth6);
        tvM7o = findViewById(R.id.tvMonth7);
        tvM8o = findViewById(R.id.tvMonth8);
        tvM9o = findViewById(R.id.tvMonth9);
        tvM10o = findViewById(R.id.tvMonth10);
        tvM11o = findViewById(R.id.tvMonth11);
        tvM12o = findViewById(R.id.tvMonth12);
        tvM13o = findViewById(R.id.tvMonth13);
        //Anzeige Monat Einnahmen
        tvM1i = findViewById(R.id.tvMonth1in);
        tvM2i = findViewById(R.id.tvMonth2in);
        tvM3i = findViewById(R.id.tvMonth3in);
        tvM4i = findViewById(R.id.tvMonth4in);
        tvM5i = findViewById(R.id.tvMonth5in);
        tvM6i = findViewById(R.id.tvMonth6in);
        tvM7i = findViewById(R.id.tvMonth7in);
        tvM8i = findViewById(R.id.tvMonth8in);
        tvM9i = findViewById(R.id.tvMonth9in);
        tvM10i = findViewById(R.id.tvMonth10in);
        tvM11i = findViewById(R.id.tvMonth11in);
        tvM12i = findViewById(R.id.tvMonth12in);
        tvM13i = findViewById(R.id.tvMonth13in);
        //Anzeige Wert Ausgaben
        tvM1out = findViewById(R.id.tvout_Month1);
        tvM2out = findViewById(R.id.tvout_Month2);
        tvM3out = findViewById(R.id.tvout_Month3);
        tvM4out = findViewById(R.id.tvout_Month4);
        tvM5out = findViewById(R.id.tvout_Month5);
        tvM6out = findViewById(R.id.tvout_Month6);
        tvM7out = findViewById(R.id.tvout_Month7);
        tvM8out = findViewById(R.id.tvout_Month8);
        tvM9out = findViewById(R.id.tvout_Month9);
        tvM10out = findViewById(R.id.tvout_Month10);
        tvM11out = findViewById(R.id.tvout_Month11);
        tvM12out = findViewById(R.id.tvout_Month12);
        tvM13out = findViewById(R.id.tvout_Month13);
        //Anzeige Wert Einnahmen
        tvM1in =findViewById(R.id.tvin_Month1);
        tvM2in =findViewById(R.id.tvin_Month2);
        tvM3in =findViewById(R.id.tvin_Month3);
        tvM4in =findViewById(R.id.tvin_Month4);
        tvM5in =findViewById(R.id.tvin_Month5);
        tvM6in =findViewById(R.id.tvin_Month6);
        tvM7in =findViewById(R.id.tvin_Month7);
        tvM8in =findViewById(R.id.tvin_Month8);
        tvM9in =findViewById(R.id.tvin_Month9);
        tvM10in =findViewById(R.id.tvin_Month10);
        tvM11in =findViewById(R.id.tvin_Month11);
        tvM12in =findViewById(R.id.tvin_Month12);
        tvM13in =findViewById(R.id.tvin_Month13);

        //Diagramm zurücksetzen und aufrufen
        barChartInOut.clearChart();
        barGraphMonthInOut();
    }

    //Balkendiagramm mit Einnahmen und Ausgaben der letzten 12 Monate
    public void barGraphMonthInOut() {

        int mTextOut = 1; //für Textausgabe
        int monthThisYear = 1; //monate hochzählen
        int monthPreYear = month;
        int preYear = year-1; //Vorjahr

        //Vorjahresanzeige
        while (monthPreYear <= 12) {
            String monthname = "leer";

            switch (monthPreYear) {
                case 1:
                    monthname = "Jan";
                    break;
                case 2:
                    monthname = "Feb";
                    break;
                case 3:
                    monthname = "Mar";
                    break;
                case 4:
                    monthname = "Apr";
                    break;
                case 5:
                    monthname = "Mai";
                    break;
                case 6:
                    monthname = "Jun";
                    break;
                case 7:
                    monthname = "Jul";
                    break;
                case 8:
                    monthname = "Aug";
                    break;
                case 9:
                    monthname = "Sep";
                    break;
                case 10:
                    monthname = "Okt";
                    break;
                case 11:
                    monthname = "Nov";
                    break;
                case 12:
                    monthname = "Dez";
                    break;
            }

            //Balkendiagramm füllen mit Einnahmen des Monats
            float intakeMonthX = roundF(mySQLite.getValueIntakesMonth(31,monthPreYear,preYear),2);
            barChartInOut.addBar(new BarModel(
                    "     "+monthname+"."+ preYear,
                    intakeMonthX,
                    Color.parseColor("#90BE6D")));
            //Balkendiagramm füllen mit Ausgaben des Monats
            float outgosMonthX = roundF( mySQLite.getValueOutgoesMonth(31,monthPreYear,preYear),2);
            barChartInOut.addBar(new BarModel(
                    "",
                    outgosMonthX,
                    Color.parseColor("#F94144")));

            //Anzeige von Werten in Text unter Diagramm
            switch (mTextOut) {
                case 1:
                    tvM1out.setText(Float.toString(outgosMonthX)+" €");
                    tvM1o.setText(monthname+"."+preYear);
                    tvM1in.setText(Float.toString(intakeMonthX)+" €");
                    tvM1i.setText(monthname+"."+preYear);
                    break;
                case 2:
                    tvM2out.setText(Float.toString(outgosMonthX)+" €");
                    tvM2o.setText(monthname+"."+preYear);
                    tvM2in.setText(Float.toString(intakeMonthX)+" €");
                    tvM2i.setText(monthname+"."+preYear);
                    break;
                case 3:
                    tvM3out.setText(Float.toString(outgosMonthX)+" €");
                    tvM3o.setText(monthname+"."+preYear);
                    tvM3in.setText(Float.toString(intakeMonthX)+" €");
                    tvM3i.setText(monthname+"."+preYear);
                    break;
                case 4:
                    tvM4out.setText(Float.toString(outgosMonthX)+" €");
                    tvM4o.setText(monthname+"."+preYear);
                    tvM4in.setText(Float.toString(intakeMonthX)+" €");
                    tvM4i.setText(monthname+"."+preYear);
                    break;
                case 5:
                    tvM5out.setText(Float.toString(outgosMonthX)+" €");
                    tvM5o.setText(monthname+"."+preYear);
                    tvM5in.setText(Float.toString(intakeMonthX)+" €");
                    tvM5i.setText(monthname+"."+preYear);
                    break;
                case 6:
                    tvM6out.setText(Float.toString(outgosMonthX)+" €");
                    tvM6o.setText(monthname+"."+preYear);
                    tvM6in.setText(Float.toString(intakeMonthX)+" €");
                    tvM6i.setText(monthname+"."+preYear);
                    break;
                case 7:
                    tvM7out.setText(Float.toString(outgosMonthX)+" €");
                    tvM7o.setText(monthname+"."+preYear);
                    tvM7in.setText(Float.toString(intakeMonthX)+" €");
                    tvM7i.setText(monthname+"."+preYear);
                    break;
                case 8:
                    tvM8out.setText(Float.toString(outgosMonthX)+" €");
                    tvM8o.setText(monthname+"."+preYear);
                    tvM8in.setText(Float.toString(intakeMonthX)+" €");
                    tvM8i.setText(monthname+"."+preYear);
                    break;
                case 9:
                    tvM9out.setText(Float.toString(outgosMonthX)+" €");
                    tvM9o.setText(monthname+"."+preYear);
                    tvM9in.setText(Float.toString(intakeMonthX)+" €");
                    tvM9i.setText(monthname+"."+preYear);
                    break;
                case 10:
                    tvM10out.setText(Float.toString(outgosMonthX)+" €");
                    tvM10o.setText(monthname+"."+preYear);
                    tvM10in.setText(Float.toString(intakeMonthX)+" €");
                    tvM10i.setText(monthname+"."+preYear);
                    break;
                case 11:
                    tvM11out.setText(Float.toString(outgosMonthX)+" €");
                    tvM11o.setText(monthname+"."+preYear);
                    tvM11in.setText(Float.toString(intakeMonthX)+" €");
                    tvM11i.setText(monthname+"."+preYear);
                    break;
                case 12:
                    tvM12out.setText(Float.toString(outgosMonthX)+" €");
                    tvM12o.setText(monthname+"."+preYear);
                    tvM12in.setText(Float.toString(intakeMonthX)+" €");
                    tvM12i.setText(monthname+"."+preYear);
                    break;
                case 13:
                    tvM13out.setText(Float.toString(outgosMonthX)+" €");
                    tvM13o.setText(monthname+"."+preYear);
                    tvM13in.setText(Float.toString(intakeMonthX)+" €");
                    tvM13i.setText(monthname+"."+preYear);
                    break;
            }
            mTextOut++;
            monthPreYear++;
        }

        //Aktuelles Jahr anzeigen
        while (monthThisYear <= (month)) {
            String monthname = "leer";
            switch (monthThisYear) {
                case 1:
                    monthname = "Jan";
                    break;
                case 2:
                    monthname = "Feb";
                    break;
                case 3:
                    monthname = "Mar";
                    break;
                case 4:
                    monthname = "Apr";
                    break;
                case 5:
                    monthname = "Mai";
                    break;
                case 6:
                    monthname = "Jun";
                    break;
                case 7:
                    monthname = "Jul";
                    break;
                case 8:
                    monthname = "Aug";
                    break;
                case 9:
                    monthname = "Sep";
                    break;
                case 10:
                    monthname = "Okt";
                    break;
                case 11:
                    monthname = "Nov";
                    break;
                case 12:
                    monthname = "Dez";
                    break;
            }

            //Balkendiagramm füllen mit Einnahmen des Monats
            float intakeMonthX = roundF( mySQLite.getValueIntakesMonth(31,monthThisYear,year),2);
            barChartInOut.addBar(new BarModel(
                    "     "+ monthname +"."+ year,
                    intakeMonthX,
                    Color.parseColor("#90BE6D")));
            //Balkendiagramm füllen mit Ausgaben des Monats
            float outgoMonthX = roundF( mySQLite.getValueOutgoesMonth(31,monthThisYear,year),2);
            barChartInOut.addBar(new BarModel(
                    "",
                    outgoMonthX,
                    Color.parseColor("#F94144")));

            //Anzeige von Werten in Text unter Diagramm
            switch (mTextOut) {
                case 1:
                    tvM1out.setText(Float.toString(outgoMonthX)+" €");
                    tvM1o.setText(monthname+"."+year);
                    tvM1in.setText(Float.toString(intakeMonthX)+" €");
                    tvM1i.setText(monthname+"."+year);
                    break;
                case 2:
                    tvM2out.setText(Float.toString(outgoMonthX)+" €");
                    tvM2o.setText(monthname+"."+year);
                    tvM2in.setText(Float.toString(intakeMonthX)+" €");
                    tvM2i.setText(monthname+"."+year);
                    break;
                case 3:
                    tvM3out.setText(Float.toString(outgoMonthX)+" €");
                    tvM3o.setText(monthname+"."+year);
                    tvM3in.setText(Float.toString(intakeMonthX)+" €");
                    tvM3i.setText(monthname+"."+year);
                    break;
                case 4:
                    tvM4out.setText(Float.toString(outgoMonthX)+" €");
                    tvM4o.setText(monthname+"."+year);
                    tvM4in.setText(Float.toString(intakeMonthX)+" €");
                    tvM4i.setText(monthname+"."+year);
                    break;
                case 5:
                    tvM5out.setText(Float.toString(outgoMonthX)+" €");
                    tvM5o.setText(monthname+"."+year);
                    tvM5in.setText(Float.toString(intakeMonthX)+" €");
                    tvM5i.setText(monthname+"."+year);
                    break;
                case 6:
                    tvM6out.setText(Float.toString(outgoMonthX)+" €");
                    tvM6o.setText(monthname+"."+year);
                    tvM6in.setText(Float.toString(intakeMonthX)+" €");
                    tvM6i.setText(monthname+"."+year);
                    break;
                case 7:
                    tvM7out.setText(Float.toString(outgoMonthX)+" €");
                    tvM7o.setText(monthname+"."+year);
                    tvM7in.setText(Float.toString(intakeMonthX)+" €");
                    tvM7i.setText(monthname+"."+year);
                    break;
                case 8:
                    tvM8out.setText(Float.toString(outgoMonthX)+" €");
                    tvM8o.setText(monthname+"."+year);
                    tvM8in.setText(Float.toString(intakeMonthX)+" €");
                    tvM8i.setText(monthname+"."+year);
                    break;
                case 9:
                    tvM9out.setText(Float.toString(outgoMonthX)+" €");
                    tvM9o.setText(monthname+"."+year);
                    tvM9in.setText(Float.toString(intakeMonthX)+" €");
                    tvM9i.setText(monthname+"."+year);
                    break;
                case 10:
                    tvM10out.setText(Float.toString(outgoMonthX)+" €");
                    tvM10o.setText(monthname+"."+year);
                    tvM10in.setText(Float.toString(intakeMonthX)+" €");
                    tvM10i.setText(monthname+"."+year);
                    break;
                case 11:
                    tvM11out.setText(Float.toString(outgoMonthX)+" €");
                    tvM11o.setText(monthname+"."+year);
                    tvM11in.setText(Float.toString(intakeMonthX)+" €");
                    tvM11i.setText(monthname+"."+year);
                    break;
                case 12:
                    tvM12out.setText(Float.toString(outgoMonthX)+" €");
                    tvM12o.setText(monthname+"."+year);
                    tvM12in.setText(Float.toString(intakeMonthX)+" €");
                    tvM12i.setText(monthname+"."+year);
                    break;
                case 13:
                    tvM13out.setText(Float.toString(outgoMonthX)+" €");
                    tvM13o.setText(monthname+"."+year);
                    tvM13in.setText(Float.toString(intakeMonthX)+" €");
                    tvM13i.setText(monthname+"."+year);
                    break;
            }
            mTextOut++;
            monthThisYear++;
        }
        //Darstellungsoptionen
        barChartInOut.startAnimation();
        barChartInOut.setShowValues(true);
        barChartInOut.setActivated(false);
    }

    //Rundung eines Wertes auf zwei Nachkommazahlen
    public float roundF(float number, int positions) {
        return (float) ((int)number + (Math.round(Math.pow(10,positions)*(number-(int)number)))/(Math.pow(10,positions)));
    }

    //Ändern des letzen Anzuzeigenden Monats
    public void changeLastMonth(View view) {
        setData();
    }

    //Kalender zu Auswahl des Monats
    public  void openCalender(View dateview) {
        Calendar calender = Calendar.getInstance();
        year = calender.get(Calendar.YEAR);
        month = calender.get(Calendar.MONTH);
        day = calender.get(Calendar.DAY_OF_MONTH);

        //Kalenderanzeige auf Deutsch umstellen
        Locale locale = new Locale("de");
        Locale.setDefault(locale);
        Resources res = this.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.locale = locale;
        res.updateConfiguration(config, res.getDisplayMetrics());

        DatePickerDialog dateDialog = new DatePickerDialog(AnnualViewActivity.this, R.style.datePickerStyle, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {

                day = selectedDay;
                month = selectedMonth+1;
                year = selectedYear;

                if (day<10)
                {
                    if(month<10)
                    {
                        editTextDate.setText("0"+ selectedDay+".0"+month+"."+selectedYear);
                    }
                    else {
                        editTextDate.setText("0" + selectedDay + "." + month + "." + selectedYear);
                    }
                }
                else {
                    if(month<10)
                    {
                        editTextDate.setText(selectedDay+".0"+month+"."+selectedYear);
                    }
                    else {
                        editTextDate.setText(selectedDay + "." + month + "." + selectedYear);
                    }
                }
            }
        }, year, month, day);
        dateDialog.show();
    }

    //Aufruf Menü
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.navigation_menu, menu);
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