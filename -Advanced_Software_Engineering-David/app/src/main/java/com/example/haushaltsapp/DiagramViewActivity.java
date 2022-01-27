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
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.haushaltsapp.database.Category;
import com.example.haushaltsapp.database.MySQLite;
import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.PieModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class DiagramViewActivity extends AppCompatActivity {

        ////Variabeln zur Menünavigation
        private MySQLite mySQLite;
        ///////////////////////////////

        private LinearLayout lverkehrsmittel, lwohnen, llebensmittel, lgesundheit, lfreizeit, lsonstiges, letc1, letc2, letc3;
        private View wohnencolor, lebensmittelcolor, gesundheitcolor, verkehrsmittelcolor, freizeitcolor, sonstigescolor,etc1color, etc2color, etc3color;
        private TextView wohnentext, lebensmitteltext, verkehrsmitteltext, gesundheittext, freitzeittext,sonstigesttext, etc1text, etc2text, etc3text;

        private RelativeLayout rWohnen, rLebensmittel, rVerkehrsmittel, rGesundheit, rFreizeit, rSonstiges, retc1, retc2, retc3;
        private TextView tvWohnen, tvLebensmittel, tvGesundheit, tvVerkehrsmittel, tvFreizeit, tvSonstiges, tvetc1, tvetc2, tvetc3;
        private TextView verkehrsmittelname, wohnenname, lebensmittelname, gesundheitname, freizeitname, sonstigesname, etc1name, etc2name, etc3name;
        private View line0, line1, line2, line3, line4, line5, line6, line7, line8, line9;

        private PieChart pieChart;
        private BarChart barChart;

        //aktuelles Datum
        private int day;
        private int month;
        private int year;

        private TextView editTextDate; //Datumsanzeige
        private String dates;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_diagram_view);

            mySQLite = new MySQLite(this);

            //Aktuelles Datum anzeigen
            editTextDate = (TextView) findViewById(R.id.editTextDate);
            java.util.Calendar calender = Calendar.getInstance();
            SimpleDateFormat datumsformat = new SimpleDateFormat("dd.MM.yyyy");
            editTextDate.setText(datumsformat.format(calender.getTime()));

            setData();
        }

        private void setData() {

            //Datum von Textview auslesen
            dates = editTextDate.getText().toString();
            day = Integer.parseInt(dates.substring(0,2));
            month = Integer.parseInt(dates.substring(3,5));
            year = Integer.parseInt(dates.substring(6,10));

            pieChart = findViewById(R.id.piechart);
            barChart = findViewById(R.id.barchart);

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

            //Farben
            verkehrsmittelcolor =findViewById(R.id.ColorVerkehrsmittel);
            wohnencolor =findViewById(R.id.ColorWohnen);
            lebensmittelcolor =findViewById(R.id.ColorLebensmittel);
            gesundheitcolor = findViewById(R.id.ColorGesundheit);
            freizeitcolor = findViewById(R.id.ColorFreizeit);
            sonstigescolor =findViewById(R.id.COlorSonstiges);
            etc1color = findViewById(R.id.etc1color);
            etc2color = findViewById(R.id.etc2color);
            etc3color = findViewById(R.id.etc3color);

            //Kategorienamen unter Diagramm
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

            //Kategoriename in Tabelle
            verkehrsmittelname = findViewById(R.id.Verkehrmittelname);
            wohnenname = findViewById(R.id.Wohnenname);
            lebensmittelname = findViewById(R.id.Lebensmittelname);
            gesundheitname = findViewById(R.id.Gesundheitname);
            freizeitname = findViewById(R.id.Freizeitname);
            sonstigesname = findViewById(R.id.Sonstigesname);
            etc1name = findViewById(R.id.etc1name);
            etc2name = findViewById(R.id.etc2name);
            etc3name = findViewById(R.id.etc3name);

            //Anzeige für Werte in Tabelle
            tvWohnen = findViewById(R.id.tvWohnen);
            tvLebensmittel = findViewById(R.id.tvLebensmittel);
            tvVerkehrsmittel = findViewById(R.id.tvVerkehrsmittel);
            tvGesundheit = findViewById(R.id.tvGesundheit);
            tvFreizeit = findViewById(R.id.tvFreizeit);
            tvSonstiges = findViewById(R.id.tvSonstiges);
            tvetc1 = findViewById(R.id.tvetc1);
            tvetc2 = findViewById(R.id.tvect2);
            tvetc3 = findViewById(R.id.tvetc3);

            ArrayList<Category> categories = mySQLite.getAllCategory();
            //Textfelder und Farben setzen
            setTextandColor( categories);

            //Diagramme zurücksetzten
            pieChart.clearChart();
            barChart.clearChart();
            //Diagram Methoden aufrufen
            pieChartKat(categories);
            barGraphKat(categories);
        }

        //Runden auf zwei Nachkommazahlen
        public float roundf(float number, int positions) {
            return (float) ((int)number + (Math.round(Math.pow(10,positions)*(number-(int)number)))/(Math.pow(10,positions)));
        }


        //Setzen der Textfelder und Farben in der Anzeige
        public  void setTextandColor ( ArrayList<Category> categories) {
            int numCat = categories.size();
            int num =0;
            String catName;
            int catColor;
            float round;

            //Ausgelegt auf max 9 Kategorien
            while ( num < numCat) {
                catName = categories.get(num).getName_PK();
                catColor = categories.get(num).getColor();

                switch (num) {
                    case 0:
                    verkehrsmittelcolor.setBackgroundColor(catColor);
                    verkehrsmitteltext.setText(catName);
                    lverkehrsmittel.setVisibility(View.VISIBLE);

                    verkehrsmittelname.setText(catName);
                    round = mySQLite.getCategorieOutgosMonth(day,month,year,catName);
                    tvVerkehrsmittel.setText(Float.toString(roundf(round,2))+" €");
                    rVerkehrsmittel.setVisibility(View.VISIBLE);
                    line1.setVisibility(View.VISIBLE);
                    break;
                    case 1:
                        wohnencolor.setBackgroundColor(catColor);
                        wohnentext.setText(catName);
                        lwohnen.setVisibility(View.VISIBLE);

                        wohnenname.setText(catName);
                        round = mySQLite.getCategorieOutgosMonth(day,month,year,catName);
                        tvWohnen.setText(Float.toString(roundf(round,2))+" €");
                        rWohnen.setVisibility(View.VISIBLE);
                        line2.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        lebensmittelcolor.setBackgroundColor(catColor);
                        lebensmitteltext.setText(catName);
                        llebensmittel.setVisibility(View.VISIBLE);

                        lebensmittelname.setText(catName);
                        round = mySQLite.getCategorieOutgosMonth(day,month,year,catName);
                        tvLebensmittel.setText(Float.toString(roundf(round,2))+" €");
                        rLebensmittel.setVisibility(View.VISIBLE);
                        line3.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        gesundheitcolor.setBackgroundColor(catColor);
                        gesundheittext.setText(catName);
                        lgesundheit.setVisibility(View.VISIBLE);

                        gesundheitname.setText(catName);
                        round = mySQLite.getCategorieOutgosMonth(day,month,year,catName);
                        tvGesundheit.setText(Float.toString(roundf(round,2))+" €");
                        rGesundheit.setVisibility(View.VISIBLE);
                        line4.setVisibility(View.VISIBLE);
                        break;
                    case 4:
                        freizeitcolor.setBackgroundColor(catColor);
                        freitzeittext.setText(catName);
                        lfreizeit.setVisibility(View.VISIBLE);

                        freizeitname.setText(catName);
                        round = mySQLite.getCategorieOutgosMonth(day,month,year,catName);
                        tvFreizeit.setText(Float.toString(roundf(round,2))+" €");
                        rFreizeit.setVisibility(View.VISIBLE);
                        line5.setVisibility(View.VISIBLE);
                        break;
                    case 5:
                        sonstigescolor.setBackgroundColor(catColor);
                        sonstigesttext.setText(catName);
                        lsonstiges.setVisibility(View.VISIBLE);

                        sonstigesname.setText(catName);
                        round = mySQLite.getCategorieOutgosMonth(day,month,year,catName);
                        tvSonstiges.setText(Float.toString(roundf(round,2))+" €");
                        rSonstiges.setVisibility(View.VISIBLE);
                        line6.setVisibility(View.VISIBLE);
                        break;
                    case 6:
                        etc1color.setBackgroundColor(catColor);
                        etc1text.setText(catName);
                        letc1.setVisibility(View.VISIBLE);

                        etc1name.setText(catName);
                        round = mySQLite.getCategorieOutgosMonth(day,month,year,catName);
                        tvetc1.setText(Float.toString(roundf(round,2))+" €");
                        retc1.setVisibility(View.VISIBLE);
                        line7.setVisibility(View.VISIBLE);
                        break;
                    case 7:
                        etc2color.setBackgroundColor(catColor);
                        etc2text.setText(catName);
                        letc2.setVisibility(View.VISIBLE);

                        etc2name.setText(catName);
                        round = mySQLite.getCategorieOutgosMonth(day,month,year,catName);
                        tvetc2.setText(Float.toString(roundf(round,2))+" €");
                        retc2.setVisibility(View.VISIBLE);
                        line8.setVisibility(View.VISIBLE);
                        break;
                    case 8:
                        etc3color.setBackgroundColor(catColor);
                        etc3text.setText(catName);
                        letc3.setVisibility(View.VISIBLE);

                        etc3name.setText(catName);
                        round = mySQLite.getCategorieOutgosMonth(day,month,year,catName);
                        tvetc3.setText(Float.toString(roundf(round,2))+" €");
                        retc3.setVisibility(View.VISIBLE);
                        line9.setVisibility(View.VISIBLE);
                        break;
                }
                num=num+1;
            }
        }

        //Kreisdiagramm mit anzeige der vorhandenen Kategorien im Ausgewählten Monat
        public void pieChartKat (ArrayList<Category> categories){
            int num = 0;
            int catnum = categories.size();
            String catName;
            float costs;
            int catColor;

            while (num<catnum) {
                catName = categories.get(num).getName_PK();
                costs = mySQLite.getCategorieOutgosMonth(day,month,year,catName );
                catColor = categories.get(num).getColor();

                pieChart.addPieSlice(
                        new PieModel(
                                catName,
                                costs,
                                catColor));
                num++;
            }

            pieChart.setInnerPaddingOutline(5);
            pieChart.startAnimation();
            pieChart.setBackgroundColor(0);
        }

        //Balkendiagramm mit Anzeige der vorhanden Kategorien im ausgewählten Monat
        public void barGraphKat(ArrayList<Category> categories) {
            int num = 0;
            int catnum = categories.size();
            String catName;
            float costs;
            int catColor;

            while (num < catnum) {
                catName = categories.get(num).getName_PK();
                costs = mySQLite.getCategorieOutgosMonth(day,month,year,catName );
                catColor = categories.get(num).getColor();

                barChart.addBar(new BarModel(
                        //catName,      //Anzeige von Kategorie als Achsenbeschriftung
                        costs,
                        catColor
                ));
                num++;
            }

            barChart.startAnimation();
            barChart.setShowValues(false);  //Beschriftung auf Balken
            barChart.setActivated(false);
        }

        //Button zum Aktualisieren des Monats
        public void changeMonth(View view) {
            setData();
        }

        //Kalender zur Auswahl des Monats, der angezeigt werden soll
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
                }
            }, year, month, day);
            dateDialog.show();
        }

        //Link zur Jahresansicht
        public void changeToAnnual(View view) {
            Intent switchToAnnualView = new Intent(this, AnnualViewActivity.class);
            startActivity(switchToAnnualView);
        }

        //Link zum Monatsvergleich
        public void changeToMonthcomparison(View view) {
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