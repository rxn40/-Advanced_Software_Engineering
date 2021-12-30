package com.example.haushaltsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.Locale;

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
        private Button changeToMonthcomparison;
        private ImageView calenderView;
        private long startDateInMilliSec;
        private long endDateInMilliSec;

        //noch erweitern um tv+, bei zufügen von weiteren Kategorien
        private LinearLayout lverkehrsmittel, lwohnen, llebensmittel, lgesundheit, lfreizeit, lsonstiges, letc1, letc2, letc3;
        private View wohnencolor, lebensmittelcolor, gesundheitcolor, verkehrsmittelcolor, freizeitcolor, sonstigescolor;
        private View etc1color, etc2color, etc3color;
        private TextView wohnentext, lebensmitteltext, verkehrsmitteltext, gesundheittext, freitzeittext,sonstigesttext, etc1text, etc2text, etc3text;

        private RelativeLayout rWohnen, rLebensmittel, rVerkehrsmittel, rGesundheit, rFreizeit, rSonstiges, retc1, retc2, retc3;
        private TextView tvWohnen, tvLebensmittel, tvGesundheit, tvVerkehrsmittel, tvFreizeit, tvSonstiges, tvetc1, tvetc2, tvetc3;
        private TextView verkehrsmittelname, wohnenname, lebensmittelname, gesundheitname, freizeitname, sonstigesname, etc1name, etc2name, etc3name;
        private View line0, line1, line2, line3, line4, line5, line6, line7, line8, line9;


        private PieChart pieChart;
        private BarChart BarChart;

        //aktuelles Datum
        private int day;
        private int month;
        private int year;

        private TextView editTextDate; //Datum
        private String dates;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_diagram_view);
            changeToAnnual = findViewById(R.id.changeViewAnnual);
            changeToMonthcomparison= findViewById(R.id.changeViewMonthcomparison);

            db = new MySQLite(this);
            // db.openDatabase(); // nicht mehr notwendig // Auskommentiert von Yvette Groner

            //Aktuelles Datum anzeigen
            editTextDate = (TextView) findViewById(R.id.editTextDate);
            java.util.Calendar calender = Calendar.getInstance();
            SimpleDateFormat datumsformat = new SimpleDateFormat("dd.MM.yyyy");
            editTextDate.setText(datumsformat.format(calender.getTime()));

            setData();
        }

        private void setData() {

            //Datum von Textfeld auslesen
            dates = editTextDate.getText().toString();
            day = Integer.parseInt(dates.substring(0,2));
            month = Integer.parseInt(dates.substring(3,5));
            year = Integer.parseInt(dates.substring(6,10));

            pieChart = findViewById(R.id.piechart);
            BarChart = findViewById(R.id.barchart);

            //linear Layouts
            lverkehrsmittel= findViewById(R.id.LinearLayoutVerkehrsmittel);
            lwohnen= findViewById(R.id.LinearLayoutWohnen);
            llebensmittel =findViewById(R.id.LinearLayoutLebensmittel);
            lgesundheit= findViewById(R.id.LinearLayoutGesundheit);
            lfreizeit = findViewById(R.id.LinearLayoutFreizeit);
            lsonstiges = findViewById(R.id.LinearLayoutSonstiges);
            letc1 = findViewById(R.id.LinearLayoutetc1);
            letc2 = findViewById(R.id.LinearLayoutetc2);
            letc3 = findViewById(R.id.LinearLayoutetc3);

            //farben
            verkehrsmittelcolor =findViewById(R.id.ColorVerkehrsmittel);
            wohnencolor =findViewById(R.id.ColorWohnen);
            lebensmittelcolor =findViewById(R.id.ColorLebensmittel);
            gesundheitcolor = findViewById(R.id.ColorGesundheit);
            freizeitcolor = findViewById(R.id.ColorFreizeit);
            sonstigescolor =findViewById(R.id.COlorSonstiges);
            etc1color = findViewById(R.id.etc1color);
            etc2color = findViewById(R.id.etc2color);
            etc3color = findViewById(R.id.etc3color);

            //namen unter Diagramm
            verkehrsmitteltext =findViewById(R.id.textVerkehrsmittel);
            wohnentext =findViewById(R.id.textWohnen);
            lebensmitteltext =findViewById(R.id.textLebensmittel);
            gesundheittext=findViewById(R.id.textGesundheit);
            freitzeittext=findViewById(R.id.textFreizeit);
            sonstigesttext=findViewById(R.id.textSonstiges);
            etc1text =findViewById(R.id.etc1);
            etc2text =findViewById(R.id.etc2);
            etc3text =findViewById(R.id.etc3);

            //Relativlayout
            rVerkehrsmittel = findViewById(R.id.RelativLayoutVerkehrsmittel);
            rWohnen =findViewById(R.id.RelativLayoutWohnen);
            rLebensmittel = findViewById(R.id.RelativLayoutLebensmittel);
            rGesundheit = findViewById(R.id.RelativLayoutGesundheit);
            rFreizeit= findViewById(R.id.RelativLayoutFreizeit);
            rSonstiges =findViewById(R.id.RelativLayoutSonstiges);
            retc1 =findViewById(R.id.RelativLayoutetc1);
            retc2 =findViewById(R.id.RelativLayoutetc2);
            retc3 =findViewById(R.id.RelativLayoutetc3);

            //Linien in Tabelle
            line0 = findViewById(R.id.line0);
            line1 = findViewById(R.id.line1);
            line2 = findViewById(R.id.line2);
            line3 = findViewById(R.id.line3);
            line4 = findViewById(R.id.line4);
            line5 = findViewById(R.id.line5);
            line6 = findViewById(R.id.line6);
            line7 = findViewById(R.id.line7);
            line8 = findViewById(R.id.line8);
            line9 = findViewById(R.id.line9);

            //Name in Tabelle
            verkehrsmittelname =findViewById(R.id.Verkehrmittelname);
            wohnenname =findViewById(R.id.Wohnenname);
            lebensmittelname =findViewById(R.id.Lebensmittelname);
            gesundheitname=findViewById(R.id.Gesundheitname);
            freizeitname =findViewById(R.id.Freizeitname);
            sonstigesname =findViewById(R.id.Sonstigesname);
            etc1name = findViewById(R.id.etc1name);
            etc2name = findViewById(R.id.etc2name);
            etc3name = findViewById(R.id.etc3name);

            //Anzeige für Werte in Tabelle
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

            ArrayList<Category> Categories =db.getAllCategory();
            //Textfelder und Farben setzen
            setTextandColor( Categories);

            //Diagramme zurücksetzten
            pieChart.clearChart();
            BarChart.clearChart();
            //Diagram Methoden aufrufen
            PieChartKat(Categories);
            BarGraphKat(Categories);
        }
    //runden auf zwei Nachkommazahlen
    public float roundf(float zahl, int stellen) {
        return (float) ((int)zahl + (Math.round(Math.pow(10,stellen)*(zahl-(int)zahl)))/(Math.pow(10,stellen)));
    }

        public  void setTextandColor ( ArrayList<Category> Categories)
        {

            int numCat= Categories.size();
            int n =0;
            String CatName;
            int CatColor;
            float round;

            //Ausgelegt auf max 3 extra Kategorien!!!
            while ( n < numCat)
            {
                CatName = Categories.get(n).getName_PK();
                CatColor =Categories.get(n).getColor();

                switch (n)
                { case 0:
                    verkehrsmittelcolor.setBackgroundColor(CatColor);
                    verkehrsmitteltext.setText(CatName);
                    lverkehrsmittel.setVisibility(View.VISIBLE);

                    verkehrsmittelname.setText(CatName);
                    round = db.getCategorieOutgosMonth(day,month,year,CatName);
                    tvVerkehrsmittel.setText(Float.toString(roundf(round,2))+" €");
                    rVerkehrsmittel.setVisibility(View.VISIBLE);
                    line1.setVisibility(View.VISIBLE);
                    break;
                    case 1:
                        wohnencolor.setBackgroundColor(CatColor);
                        wohnentext.setText(CatName);
                        lwohnen.setVisibility(View.VISIBLE);

                        wohnenname.setText(CatName);
                        round = db.getCategorieOutgosMonth(day,month,year,CatName);
                        tvWohnen.setText(Float.toString(roundf(round,2))+" €");
                        rWohnen.setVisibility(View.VISIBLE);
                        line2.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        lebensmittelcolor.setBackgroundColor(CatColor);
                        lebensmitteltext.setText(CatName);
                        llebensmittel.setVisibility(View.VISIBLE);

                        lebensmittelname.setText(CatName);
                        round = db.getCategorieOutgosMonth(day,month,year,CatName);
                        tvLebensmittel.setText(Float.toString(roundf(round,2))+" €");
                        rLebensmittel.setVisibility(View.VISIBLE);
                        line3.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        gesundheitcolor.setBackgroundColor(CatColor);
                        gesundheittext.setText(CatName);
                        lgesundheit.setVisibility(View.VISIBLE);

                        gesundheitname.setText(CatName);
                        round = db.getCategorieOutgosMonth(day,month,year,CatName);
                        tvGesundheit.setText(Float.toString(roundf(round,2))+" €");
                        rGesundheit.setVisibility(View.VISIBLE);
                        line4.setVisibility(View.VISIBLE);
                        break;
                    case 4:
                        freizeitcolor.setBackgroundColor(CatColor);
                        freitzeittext.setText(CatName);
                        lfreizeit.setVisibility(View.VISIBLE);

                        freizeitname.setText(CatName);
                        round = db.getCategorieOutgosMonth(day,month,year,CatName);
                        tvFreizeit.setText(Float.toString(roundf(round,2))+" €");
                        rFreizeit.setVisibility(View.VISIBLE);
                        line5.setVisibility(View.VISIBLE);
                        break;
                    case 5:
                        sonstigescolor.setBackgroundColor(CatColor);
                        sonstigesttext.setText(CatName);
                        lsonstiges.setVisibility(View.VISIBLE);

                        sonstigesname.setText(CatName);
                        round = db.getCategorieOutgosMonth(day,month,year,CatName);
                        tvSonstiges.setText(Float.toString(roundf(round,2))+" €");
                        rSonstiges.setVisibility(View.VISIBLE);
                        line6.setVisibility(View.VISIBLE);
                        break;


                    //Wenn weiter Kategorien eingetragen sind, weden diese angezeigt
                    //wenn nicht sind sie standardmäßig ausgeblendet
                    case 6:
                        etc1color.setBackgroundColor(CatColor);
                        etc1text.setText(CatName);
                        letc1.setVisibility(View.VISIBLE);

                        etc1name.setText(CatName);
                        round = db.getCategorieOutgosMonth(day,month,year,CatName);
                        tvetc1.setText(Float.toString(roundf(round,2))+" €");
                        retc1.setVisibility(View.VISIBLE);
                        line7.setVisibility(View.VISIBLE);
                        break;
                    case 7:
                        etc2color.setBackgroundColor(CatColor);
                        etc2text.setText(CatName);
                        letc2.setVisibility(View.VISIBLE);

                        etc2name.setText(CatName);
                        round = db.getCategorieOutgosMonth(day,month,year,CatName);
                        tvetc2.setText(Float.toString(roundf(round,2))+" €");
                        retc2.setVisibility(View.VISIBLE);
                        line8.setVisibility(View.VISIBLE);
                        break;
                    case 8:
                        etc3color.setBackgroundColor(CatColor);
                        etc3text.setText(CatName);
                        letc3.setVisibility(View.VISIBLE);

                        etc3name.setText(CatName);
                        round = db.getCategorieOutgosMonth(day,month,year,CatName);
                        tvetc3.setText(Float.toString(roundf(round,2))+" €");
                        retc3.setVisibility(View.VISIBLE);
                        line9.setVisibility(View.VISIBLE);
                        break;
                }
                n=n+1;
            }
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
                        //CatName,      //Anzeige von Kategorie als Achsenbeschriftung
                        Costs,
                        CatColor
                ));
                n++;
            }

            //mBarChart.callOnClick();
            BarChart.startAnimation();
            BarChart.setShowValues(false);  //Beschriftung auf Balken
            //BarChart.setAccessibilityHeading(true);
            BarChart.setActivated(false);
        }


        public void changeMonth(View view)
        {
            setData();
        }


        public  void openCalender(View dateview) {
            java.util.Calendar calender = java.util.Calendar.getInstance();
            year = calender.get(Calendar.YEAR);
            month = calender.get(Calendar.MONTH);
            day = calender.get(Calendar.DAY_OF_MONTH);

            //Kalender auf Deutsch umstellen
            Locale locale = new Locale("de");
            Locale.setDefault(locale);
            Resources res = this.getResources();
            Configuration config = new Configuration(res.getConfiguration());
            config.locale = locale;
            res.updateConfiguration(config, res.getDisplayMetrics());

            DatePickerDialog dateDialog = new DatePickerDialog(com.example.haushaltsapp.DiagramViewActivity.this,R.style.datePickerStyle, new DatePickerDialog.OnDateSetListener() {


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
                    //Übergabe der Daten an Kalender-Objekt und Setzen von Start und Endzeit
                    calender.set(year, month, day, 8, 0, 0);
                    startDateInMilliSec = calender.getTimeInMillis();
                    calender.set(year, month, day, 9, 0, 0);
                    endDateInMilliSec = calender.getTimeInMillis();
                }
            }, year, month, day);
            dateDialog.show();
        }

        //Link zu Jahresansicht
        public void changeToAnnual(View view) {

            Intent intent = getIntent();
            Intent switchToAnnualView= new Intent(this, AnnualViewActivity.class);
            startActivity(switchToAnnualView);
        }

        //Link zu Monatsvergleich
        public void changeToMonthcomparison(View view) {

            Intent intent = getIntent();
            Intent switchMonthcomparisonView= new Intent(this, MonthcomparisonViewActivity.class);
            startActivity(switchMonthcomparisonView);
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