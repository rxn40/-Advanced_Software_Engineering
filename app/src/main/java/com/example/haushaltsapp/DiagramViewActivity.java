package com.example.haushaltsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.haushaltsapp.database.Category;
import com.example.haushaltsapp.database.Intake;
import com.example.haushaltsapp.database.MySQLite;
import com.example.haushaltsapp.database.Outgo;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.PieModel;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class DiagramViewActivity extends AppCompatActivity {

    ////Variabeln zur Menünavigation
    private MySQLite mySQLite;
    private final int REQUESTCODE_ADD = 12; //AddEntryActivity
    private final int REQUESTCODE_SHOW = 13; //ShowEntryActivity
    private final int REQUESTCODE_EDIT = 14; //EditEntryActivity
    private final int REQUESTCODE_ADD_CATEGORY = 15; //AddCategoryActivity
    ///////////////////////////////

    private MySQLite db;

    private Button changeToAnnual;

    //noch erweitern um tv+, bei zufügen von weiteren Kategorien

    private View wohnencolor,lebensmittelcolor,gesundheitcolor,verkehrsmittelcolor,freizeitcolor, sonstigescolor;
    private LinearLayout letc1, letc2,letc3;
    private View etc1color, etc2color, etc3color;
    private TextView etc1n, etc2n, etc3n;

    private RelativeLayout retc1, retc2, retc3;
    private TextView tvWohnen, tvLebensmittel, tvGesundheit, tvVerkehrsmittel, tvFreizeit, tvSonstiges,tvetc1, tvetc2, tvetc3;
    private TextView etc1name, etc2name, etc3name;
    private View lineetc1, lineetc2, lineetc3;


    private PieChart pieChart;
    private BarChart BarChart;

    //aktuelles Datum
    private int day;
    private int month;
    private int year;

    private EditText editTextDate; //Datum
    private String dates;

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

        db = new MySQLite(this);
        db.openDatabase();
        //Erhalte das aktuelle Datum
        getDate();

        //Aktuelles Datum anzeigen
        editTextDate = (EditText) findViewById(R.id.editTextDate);
        java.util.Calendar calender = Calendar.getInstance();
        SimpleDateFormat datumsformat = new SimpleDateFormat("dd.MM.yyyy");
        editTextDate.setText(datumsformat.format(calender.getTime()));

        setData();
    }

    private void setData() {

        //Datum von Textfeld
        dates = editTextDate.getText().toString();
        day = Integer.parseInt(dates.substring(0,2));
        month = Integer.parseInt(dates.substring(3,5));
        year = Integer.parseInt(dates.substring(6,10));

        pieChart = findViewById(R.id.piechart);
        BarChart = findViewById(R.id.barchart);

        tvWohnen =findViewById(R.id.tvWohnen);
        tvLebensmittel = findViewById(R.id.tvLebensmittel);
        tvVerkehrsmittel = findViewById(R.id.tvVerkehrsmittel);
        tvGesundheit = findViewById(R.id.tvGesundheit);
        tvFreizeit = findViewById(R.id.tvFreizeit);
        tvSonstiges = findViewById(R.id.tvSonstiges);

        //Anzeige von weitern Kategorien in verschiedenen Textfeldern und Views
        tvetc1 = findViewById(R.id.tvetc1);
        tvetc2 = findViewById(R.id.tvect2);
        tvetc3 = findViewById(R.id.tvetc3);

        letc1 = findViewById(R.id.LinearLayoutetc1);
        letc2 = findViewById(R.id.LinearLayoutetc2);
        letc3 = findViewById(R.id.LinearLayoutetc3);

        wohnencolor =findViewById(R.id.ColorWohnen);
        lebensmittelcolor =findViewById(R.id.ColorLebensmittel);
        verkehrsmittelcolor =findViewById(R.id.ColorVerkehrsmittel);
        gesundheitcolor = findViewById(R.id.ColorGesundheit);
        freizeitcolor = findViewById(R.id.ColorFreizeit);
        sonstigescolor =findViewById(R.id.COlorSonstiges);

        etc1color = findViewById(R.id.etc1color);
        etc2color = findViewById(R.id.etc2color);
        etc3color = findViewById(R.id.etc3color);

        etc1n =findViewById(R.id.etc1);
        etc2n =findViewById(R.id.etc2);
        etc3n =findViewById(R.id.etc3);

        retc1 =findViewById(R.id.RelativLayoutetc1);
        retc2 =findViewById(R.id.RelativLayoutetc2);
        retc3 =findViewById(R.id.RelativLayoutetc3);

        etc1name = findViewById(R.id.etc1name);
        etc2name = findViewById(R.id.etc2name);
        etc3name = findViewById(R.id.etc3name);

        lineetc1 = findViewById(R.id.Lineetc1);
        lineetc2 = findViewById(R.id.Lineetc2);
        lineetc3 = findViewById(R.id.Lineetc3);

        ArrayList<Category> Categories =db.getAllCategory();
        int numCat= Categories.size();
        int n =0;
        String CatName;
        int CatColor;

        //Ausgelegt auf max 3 extra Kategorien!!!
        while ( n < numCat)
        {
            CatName = Categories.get(n).getName_PK();
            CatColor =Categories.get(n).getColor();

            switch (n)
            {
                case 0:
                    tvVerkehrsmittel.setText(Float.toString(db.getCategorieOutgosMonth(day,month,year,CatName))+" €");
                    verkehrsmittelcolor.setBackgroundColor(CatColor);
                    break;
                case 1:
                    tvWohnen.setText(Float.toString(db.getCategorieOutgosMonth(day,month,year,CatName))+" €");
                    wohnencolor.setBackgroundColor(CatColor);
                    break;
                case 2:
                    tvLebensmittel.setText(Float.toString(db.getCategorieOutgosMonth(day,month,year,CatName))+" €");
                    lebensmittelcolor.setBackgroundColor(CatColor);
                    break;
                case 3:
                    tvGesundheit.setText(Float.toString(db.getCategorieOutgosMonth(day,month,year,CatName))+" €");
                    gesundheitcolor.setBackgroundColor(CatColor);
                    break;
                case 4:
                    tvFreizeit.setText(Float.toString(db.getCategorieOutgosMonth(day,month,year,CatName))+" €");
                    freizeitcolor.setBackgroundColor(CatColor);
                    break;
                case 5:
                    tvSonstiges.setText(Float.toString(db.getCategorieOutgosMonth(day,month,year,CatName))+" €");
                    sonstigescolor.setBackgroundColor(CatColor);

                //Wenn weiter Kategorien eingetragen sind, weden diese angezeigt
                //wenn nicht sind sie standardmäßig ausgeblendet
                case 6:
                    etc1color.setBackgroundColor(CatColor);
                    etc1n.setText(CatName);
                    letc1.setVisibility(View.VISIBLE);

                    etc1name.setText(CatName);
                    tvetc1.setText(Float.toString(db.getCategorieOutgosMonth(day,month,year,CatName))+" €");
                    retc1.setVisibility(View.VISIBLE);
                    break;
                case 7:
                    etc2color.setBackgroundColor(CatColor);
                    etc2n.setText(CatName);
                    letc2.setVisibility(View.VISIBLE);

                    etc2name.setText(CatName);
                    tvetc2.setText(Float.toString(db.getCategorieOutgosMonth(day,month,year,CatName))+" €");
                    retc2.setVisibility(View.VISIBLE);
                    break;
                case 8:
                    etc3color.setBackgroundColor(CatColor);
                    etc3n.setText(CatName);
                    letc3.setVisibility(View.VISIBLE);

                    etc3name.setText(CatName);
                    tvetc3.setText(Float.toString(db.getCategorieOutgosMonth(day,month,year,CatName))+" €");
                    retc3.setVisibility(View.VISIBLE);
                    break;
            }
            n=n+1;
        }

        //Diagramme zurücksetzten
        pieChart.clearChart();
        BarChart.clearChart();
        //Diagram Methoden aufrufen
        PieChartKat(Categories);
        BarGraphKat(Categories);
    }

    public void PieChartKat (ArrayList<Category> Categories){
        int n=0;
        int Catnum =Categories.size();
        String CatName;
        float Costs;
        int CatColor;
        //Diagram werden so viele Kategorien zugeordnet, wie in der Datenbank vorhanden sind

        while (n<Catnum)
        {
            CatName = Categories.get(n).getName_PK();
            Costs = db.getCategorieOutgosMonth(day,month,year,CatName );
            CatColor =Categories.get(n).getColor();

            pieChart.addPieSlice(
                    new PieModel(
                            CatName,
                            Costs,
                            CatColor));

            n++;
        }

        pieChart.setInnerPaddingOutline(5);
        pieChart.setInnerPaddingOutline(5);

        pieChart.startAnimation();
        pieChart.setBackgroundColor(0);
    }

    public void BarGraphKat(ArrayList<Category> Categories)
    {
        int n=0;
        int Catnum =Categories.size();
        String CatName;
        float Costs;
        int CatColor;
        //Diagram werden so viele Kategorien zugeordnet, wie in der Datenbank vorhanden sind

        while (n<Catnum)
        {
            CatName = Categories.get(n).getName_PK();
            Costs = db.getCategorieOutgosMonth(day,month,year,CatName );
            CatColor =Categories.get(n).getColor();

            BarChart.addBar(new BarModel(
                    //CatName,
                    Costs,
                    CatColor
            ));
            n++;
        }

        //mBarChart.callOnClick();
        BarChart.startAnimation();
        BarChart.setShowValues(false);  //keine Kommazahl darzustellen
        //BarChart.setAccessibilityHeading(true);
        BarChart.setActivated(false);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.navigation_menu, menu);

        //Die aktuelle Activity im Menü ausblenden
        MenuItem item = menu.findItem(R.id.itemDiagramView);
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