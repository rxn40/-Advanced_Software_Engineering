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
import com.example.haushaltsapp.Database.Category;
import com.example.haushaltsapp.Database.MySQLite;
import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.PieModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class DiagramViewActivity extends AppCompatActivity {


        private MySQLite mySQLite;

        private LinearLayout lVerkehrsmittel, lWohnen, lLebensmittel, lGesundheit, lFreizeit, lSonstiges, lEtc1, lEtc2, lEtc3;
        private View wohnenColor, lebensmittelColor, gesundheitColor, verkehrsmittelColor, freizeitColor, sonstigesColor, etc1Color, etc2Color, etc3Color;
        private TextView wohnenText, lebensmittelText, verkehrsmittelText, gesundheitText, freitzeitText, sonstigesText, etc1Text, etc2Text, etc3Text;

        private RelativeLayout rWohnen, rLebensmittel, rVerkehrsmittel, rGesundheit, rFreizeit, rSonstiges, retc1, retc2, retc3;
        private TextView tvWohnen, tvLebensmittel, tvGesundheit, tvVerkehrsmittel, tvFreizeit, tvSonstiges, tvetc1, tvetc2, tvetc3;
        private TextView verkehrsmittelName, wohnenName, lebensmittelName, gesundheitName, freizeitName, sonstigesName, etc1Name, etc2Name, etc3Name;
        private View line0, line1, line2, line3, line4, line5, line6, line7, line8, line9;

        private PieChart pieChart;
        private BarChart barChart;

        private int day;
        private int month;
        private int year;

        private TextView editTextDate; //Datumsanzeige
        private String date;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_diagram_view);

            //Datenbank-Objekt
            mySQLite = new MySQLite(this);

            //Aktuelles Datum auslesen und anzeigen
            editTextDate = (TextView) findViewById(R.id.editTextDate);
            Calendar calender = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            editTextDate.setText(dateFormat.format(calender.getTime()));

            setData();
        }

        private void setData() {

            //Datum von Textview auslesen
            date = editTextDate.getText().toString();
            day = Integer.parseInt(date.substring(0,2));
            month = Integer.parseInt(date.substring(3,5));
            year = Integer.parseInt(date.substring(6,10));

            pieChart = findViewById(R.id.piechart);
            barChart = findViewById(R.id.barchart);

            //linear Layouts
            lVerkehrsmittel = findViewById(R.id.LinearLayoutVerkehrsmittel);
            lWohnen = findViewById(R.id.LinearLayoutWohnen);
            lLebensmittel =findViewById(R.id.LinearLayoutLebensmittel);
            lGesundheit = findViewById(R.id.LinearLayoutGesundheit);
            lFreizeit = findViewById(R.id.LinearLayoutFreizeit);
            lSonstiges = findViewById(R.id.LinearLayoutSonstiges);
            lEtc1 = findViewById(R.id.LinearLayoutetc1);
            lEtc2 = findViewById(R.id.LinearLayoutetc2);
            lEtc3 = findViewById(R.id.LinearLayoutetc3);

            //Farben
            verkehrsmittelColor =findViewById(R.id.ColorVerkehrsmittel);
            wohnenColor =findViewById(R.id.ColorWohnen);
            lebensmittelColor =findViewById(R.id.ColorLebensmittel);
            gesundheitColor = findViewById(R.id.ColorGesundheit);
            freizeitColor = findViewById(R.id.ColorFreizeit);
            sonstigesColor =findViewById(R.id.COlorSonstiges);
            etc1Color = findViewById(R.id.etc1color);
            etc2Color = findViewById(R.id.etc2color);
            etc3Color = findViewById(R.id.etc3color);

            //Kategorienamen unter Diagramm
            verkehrsmittelText =findViewById(R.id.textVerkehrsmittel);
            wohnenText =findViewById(R.id.textWohnen);
            lebensmittelText =findViewById(R.id.textLebensmittel);
            gesundheitText =findViewById(R.id.textGesundheit);
            freitzeitText =findViewById(R.id.textFreizeit);
            sonstigesText =findViewById(R.id.textSonstiges);
            etc1Text =findViewById(R.id.etc1);
            etc2Text =findViewById(R.id.etc2);
            etc3Text =findViewById(R.id.etc3);

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
            verkehrsmittelName = findViewById(R.id.Verkehrmittelname);
            wohnenName = findViewById(R.id.Wohnenname);
            lebensmittelName = findViewById(R.id.Lebensmittelname);
            gesundheitName = findViewById(R.id.Gesundheitname);
            freizeitName = findViewById(R.id.Freizeitname);
            sonstigesName = findViewById(R.id.Sonstigesname);
            etc1Name = findViewById(R.id.etc1name);
            etc2Name = findViewById(R.id.etc2name);
            etc3Name = findViewById(R.id.etc3name);

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

            ArrayList<Category> categories = mySQLite.getAllCategories();
            //Textfelder und Farben setzen
            setTextAndColor( categories);

            //Diagramme zurücksetzten
            pieChart.clearChart();
            barChart.clearChart();
            //Diagramm Methoden aufrufen
            pieChartCat(categories);
            barGraphCat(categories);
        }

        //Runden einer Float-Zahl auf zwei Nachkommazahlen
        public float roundF(float number, int positions) {
            return (float) ((int)number + (Math.round(Math.pow(10,positions)*(number-(int)number)))/(Math.pow(10,positions)));
        }


        //Setzen der Textfelder und Farben in der Anzeige
        public  void setTextAndColor(ArrayList<Category> categories) {
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
                    verkehrsmittelColor.setBackgroundColor(catColor);
                    verkehrsmittelText.setText(catName);
                    lVerkehrsmittel.setVisibility(View.VISIBLE);

                    verkehrsmittelName.setText(catName);
                    round = mySQLite.getCategoryOutgoesMonth(day,month,year,catName);
                    tvVerkehrsmittel.setText(Float.toString(roundF(round,2))+" €");
                    rVerkehrsmittel.setVisibility(View.VISIBLE);
                    line1.setVisibility(View.VISIBLE);
                    break;
                    case 1:
                        wohnenColor.setBackgroundColor(catColor);
                        wohnenText.setText(catName);
                        lWohnen.setVisibility(View.VISIBLE);

                        wohnenName.setText(catName);
                        round = mySQLite.getCategoryOutgoesMonth(day,month,year,catName);
                        tvWohnen.setText(Float.toString(roundF(round,2))+" €");
                        rWohnen.setVisibility(View.VISIBLE);
                        line2.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        lebensmittelColor.setBackgroundColor(catColor);
                        lebensmittelText.setText(catName);
                        lLebensmittel.setVisibility(View.VISIBLE);

                        lebensmittelName.setText(catName);
                        round = mySQLite.getCategoryOutgoesMonth(day,month,year,catName);
                        tvLebensmittel.setText(Float.toString(roundF(round,2))+" €");
                        rLebensmittel.setVisibility(View.VISIBLE);
                        line3.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        gesundheitColor.setBackgroundColor(catColor);
                        gesundheitText.setText(catName);
                        lGesundheit.setVisibility(View.VISIBLE);

                        gesundheitName.setText(catName);
                        round = mySQLite.getCategoryOutgoesMonth(day,month,year,catName);
                        tvGesundheit.setText(Float.toString(roundF(round,2))+" €");
                        rGesundheit.setVisibility(View.VISIBLE);
                        line4.setVisibility(View.VISIBLE);
                        break;
                    case 4:
                        freizeitColor.setBackgroundColor(catColor);
                        freitzeitText.setText(catName);
                        lFreizeit.setVisibility(View.VISIBLE);

                        freizeitName.setText(catName);
                        round = mySQLite.getCategoryOutgoesMonth(day,month,year,catName);
                        tvFreizeit.setText(Float.toString(roundF(round,2))+" €");
                        rFreizeit.setVisibility(View.VISIBLE);
                        line5.setVisibility(View.VISIBLE);
                        break;
                    case 5:
                        sonstigesColor.setBackgroundColor(catColor);
                        sonstigesText.setText(catName);
                        lSonstiges.setVisibility(View.VISIBLE);

                        sonstigesName.setText(catName);
                        round = mySQLite.getCategoryOutgoesMonth(day,month,year,catName);
                        tvSonstiges.setText(Float.toString(roundF(round,2))+" €");
                        rSonstiges.setVisibility(View.VISIBLE);
                        line6.setVisibility(View.VISIBLE);
                        break;
                    case 6:
                        etc1Color.setBackgroundColor(catColor);
                        etc1Text.setText(catName);
                        lEtc1.setVisibility(View.VISIBLE);

                        etc1Name.setText(catName);
                        round = mySQLite.getCategoryOutgoesMonth(day,month,year,catName);
                        tvetc1.setText(Float.toString(roundF(round,2))+" €");
                        retc1.setVisibility(View.VISIBLE);
                        line7.setVisibility(View.VISIBLE);
                        break;
                    case 7:
                        etc2Color.setBackgroundColor(catColor);
                        etc2Text.setText(catName);
                        lEtc2.setVisibility(View.VISIBLE);

                        etc2Name.setText(catName);
                        round = mySQLite.getCategoryOutgoesMonth(day,month,year,catName);
                        tvetc2.setText(Float.toString(roundF(round,2))+" €");
                        retc2.setVisibility(View.VISIBLE);
                        line8.setVisibility(View.VISIBLE);
                        break;
                    case 8:
                        etc3Color.setBackgroundColor(catColor);
                        etc3Text.setText(catName);
                        lEtc3.setVisibility(View.VISIBLE);

                        etc3Name.setText(catName);
                        round = mySQLite.getCategoryOutgoesMonth(day,month,year,catName);
                        tvetc3.setText(Float.toString(roundF(round,2))+" €");
                        retc3.setVisibility(View.VISIBLE);
                        line9.setVisibility(View.VISIBLE);
                        break;
                }
                num=num+1;
            }
        }

        //Kreisdiagramm mit Anzeige der vorhandenen Kategorien im ausgewählten Monat
        public void pieChartCat(ArrayList<Category> categories){
            int num = 0;
            int catNum = categories.size();
            String catName;
            float costs;
            float costsround;
            int catColor;

            while (num< catNum) {
                catName = categories.get(num).getName_PK();
                costs = mySQLite.getCategoryOutgoesMonth(day,month,year,catName );
                costsround =roundF(costs,2);
                catColor = categories.get(num).getColor();

                pieChart.addPieSlice(
                        new PieModel(
                                catName,
                                costsround,
                                catColor));
                num++;
            }

            pieChart.setInnerPaddingOutline(5);
            pieChart.startAnimation();
            pieChart.setBackgroundColor(0);
        }

        //Balkendiagramm mit Anzeige der vorhanden Kategorien im ausgewählten Monat
        public void barGraphCat(ArrayList<Category> categories) {
            int num = 0;
            int catNum = categories.size();
            String catName;
            float costs;
            float costsround;
            int catColor;

            while (num < catNum) {
                catName = categories.get(num).getName_PK();
                costs = mySQLite.getCategoryOutgoesMonth(day,month,year,catName );
                costsround =roundF(costs,2);
                catColor = categories.get(num).getColor();

                barChart.addBar(new BarModel(
                        //catName,      //Anzeige von Kategorie als Achsenbeschriftung
                        costsround,
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

        //Intent zur Jahresansicht
        public void changeToAnnual(View view) {
            Intent switchToAnnualView = new Intent(this, AnnualViewActivity.class);
            startActivity(switchToAnnualView);
        }

        //Intent zum Monatsvergleich
        public void changeToMonthComparison(View view) {
            Intent switchMonthComparisonView = new Intent(this, MonthComparisonViewActivity.class);
            startActivity(switchMonthComparisonView);
        }

    //Menüaufruf
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.navigation_menu, menu);

        //Die aktuelle Activity im Menü ausblenden
        MenuItem item = menu.findItem(R.id.itemDiagramView);
        item.setEnabled(false);

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