package com.example.haushaltsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ChartViewActivity extends  AppCompatActivity {

    private final static int[] COLUMN_WIDTHS = new int[]{40, 20, 40}; //Fensterbreite der einzelnen Spalten
    private final static int CONTENT_ROW_HEIGHT = 80;
    private final static int FIXED_HEADER_HEIGHT = 60;

    //Textgröße noch ändern und Button zum bearbeiten der Einträge anlegen

    private TableLayout fixedTableLayout;
    private TableLayout scrollableTableLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_view);

        Intent intent = getIntent();
        ArrayList<Outgo> ListeOut = (ArrayList<Outgo>) intent.getSerializableExtra("monthlist");


        final HorizontalScrollView tblHeaderhorzScrollView = (HorizontalScrollView) findViewById(R.id.tblHeaderhorzScrollView);
        tblHeaderhorzScrollView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        tblHeaderhorzScrollView.setHorizontalScrollBarEnabled(false);

        final HorizontalScrollView horizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);
        horizontalScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {

            @Override
            public void onScrollChanged() {
                tblHeaderhorzScrollView.setScrollX(horizontalScrollView.getScrollX());
            }
        });


        //zeile
        this.fixedTableLayout = (TableLayout) findViewById(R.id.fixed_column);
        //SPalten die zu jeweiligen Zeile gehören
        this.scrollableTableLayout = (TableLayout) findViewById(R.id.scrollable_part);
        //Setzt die Spaltenbreite für die Tabelle.
        setTableHeaderWidth();
        //Befüllt die Tabelle mit Beispieldaten.
        fillTable();
    }

    private void setTableHeaderWidth() {
        TextView textView;
        textView = (TextView) findViewById(R.id.Ausgabe);
        setHeaderWidth(textView, COLUMN_WIDTHS[0]);
        textView = (TextView) findViewById(R.id.Wert);
        setHeaderWidth(textView, COLUMN_WIDTHS[1]);
        textView = (TextView) findViewById(R.id.Datum);
        setHeaderWidth(textView, COLUMN_WIDTHS[2]);
            /*textView = (TextView) findViewById(R.id.Kategorie);
            setHeaderWidth(textView, COLUMN_WIDTHS[3]);
            textView = (TextView) findViewById(R.id.Sonstiges);
            setHeaderWidth(textView, COLUMN_WIDTHS[4]);*/
    }

    private void setHeaderWidth(TextView textView, int width) {
        textView.setWidth(width * getScreenWidth() / 100);
        textView.setHeight(FIXED_HEADER_HEIGHT);
    }

    private void fillTable() {

        Intent intent = getIntent();
        ArrayList<Outgo> ListeOut = (ArrayList<Outgo>) intent.getSerializableExtra("monthlist");

        Context ctx = getApplicationContext();
        int lenghtOutgos = ListeOut.size();
        //begrenzung der ABfrage durch lenghte von Datenbank noch einfügen.
        for (int position = 1; position < lenghtOutgos; position++) {
            //Daten aus Datenbank holen
            String outgo = ListeOut.get(position).getName();
            String value = Double.toString(ListeOut.get(position).getValue());
            Integer day = ListeOut.get(position).getDay();
            //hier sind die vergabe von monat und Jahr verdreht
            Integer month =ListeOut.get(position).getMonth();
            Integer year = ListeOut.get(position).getYear();
            String date = day+"."+month+"."+year;

            fixedTableLayout.addView(createTextView(outgo, COLUMN_WIDTHS[0], position));
            TableRow row = new TableRow(ctx);

            for (int col = 1; col < 2; col++) //1=Wert, 2=Datum , 3=Kategorie, 4=Sonstiges
            {
                //Wert
                row.addView(createTextView(value, COLUMN_WIDTHS[col], position));
                //Datum
                row.addView(createTextView(date, COLUMN_WIDTHS[col], position));
                //Kategorie
                //String Kategorie ="alles";
                //row.addView(createTextView(Kategorie, COLUMN_WIDTHS[col], position));
            }
            scrollableTableLayout.addView(row);
        }
    }

    private TextView createTextView(String text, int width, int index) {
        TextView textView = new TextView(getApplicationContext());
        textView.setText(text);
        textView.setWidth(width * getScreenWidth() / 100);
        textView.setHeight(CONTENT_ROW_HEIGHT);
        return textView;
    }

    private int getScreenWidth() {
        return getResources().getDisplayMetrics().widthPixels;
    }



/* @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_view);
        ListView mListView = (ListView) findViewById(R.id.listView);
        Intent intent = getIntent();
        ArrayList<Outgo> ListeOut = (ArrayList<Outgo>) intent.getSerializableExtra("list");
        //zum TEsten ohne Datenbankzugriff
        //Erzeugen die AUsgabe objects
        Expenditures T1 = new Expenditures("Tanken", "54", "11.11.2021");
        Expenditures T2 = new Expenditures("Einkaufen Lidl", "24", "13.11.2021");
        Expenditures T3 = new Expenditures("Handy", "3.99", "01.11.2021");
        Expenditures T4 = new Expenditures("Penny", "10", "20.11.2021");
        Expenditures T5 = new Expenditures("Penny EInk.", "34.87", "21.11.2021");
        //füllen der Array List
        ArrayList<Expenditures> AusgabeList = new ArrayList<>();
        AusgabeList.add(T1);
        AusgabeList.add(T2);
        AusgabeList.add(T3);
        AusgabeList.add(T4);
        AusgabeList.add(T5);
        AusgabeList.add(T1);
        AusgabeList.add(T2);
        AusgabeList.add(T4);
        AusgabeList.add(T3);
        AusgabeList.add(T5);
        AusgabeList.add(T1);
        AusgabeList.add(T1);
        AusgabeList.add(T3);
        AusgabeList.add(T4);
        AusgabeList.add(T2);
        //Zum Test ohne Datenbank
        ExpendituresListAdapter adapter = new ExpendituresListAdapter(this, R.layout.activity_adapter_list_view, AusgabeList);
        mListView.setAdapter(adapter);
        //Intent switchOutgoListAdapter =new Intent(this, OutgoListAdapter.A.class);
        //ArrayList<Outgo> outgoes1 = ListeOut;
        //switchOutgoListAdapter.putExtra("list",(Serializable) outgoes1);
        //OutgoListAdapter adapter = new OutgoListAdapter(this,R.layout.activity_adapter_list_view,ListeOut);
        //mListView.setAdapter(adapter);
    }*/


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chart_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.itemStartseite:
                Intent switchToMain = new Intent(this, MainActivity.class);
                startActivity(switchToMain);
                return true;

            case R.id.itemEinnahmenAusgaben:
                Intent switchToAddEntry = new Intent(this, AddEntryActivity.class);
                startActivity(switchToAddEntry);
                return true;

            case R.id.itemBudgetLimit:
                Intent switchToBudgetLimit = new Intent(this, BudgetLimitActivity.class);
                startActivity(switchToBudgetLimit);
                return true;

            case R.id.itemDiagrammansicht:
                Intent switchToDiagramView = new Intent(this, DiagramViewActivity.class);
                startActivity(switchToDiagramView);
                return true;

            case R.id.itemKalender:
                Intent switchToCalendar = new Intent(this, CalendarEventActivity.class);
                startActivity(switchToCalendar);
                return true;

            case R.id.itemTodoListe:
                Intent switchToToDoList = new Intent(this, ToDoListActivity.class);
                startActivity(switchToToDoList);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}