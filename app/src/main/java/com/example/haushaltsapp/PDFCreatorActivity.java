package com.example.haushaltsapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.fonts.Font;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.haushaltsapp.database.Category;
import com.example.haushaltsapp.database.Intake;
import com.example.haushaltsapp.database.MySQLite;
import com.example.haushaltsapp.database.Outgo;

import com.itextpdf.kernel.colors.CalGray;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.WebColors;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;


public class PDFCreatorActivity extends AppCompatActivity {

    ////Variabeln zur Menünavigation
    private MySQLite mySQLite;
    private final int REQUESTCODE_ADD = 12; //AddEntryActivity
    private final int REQUESTCODE_SHOW = 13; //ShowEntryActivity
    private final int REQUESTCODE_EDIT = 14; //EditEntryActivity
    private final int REQUESTCODE_ADD_CATEGORY = 15; //AddCategoryActivity
    ///////////////////////////////

    private MySQLite db;
    private Button createPdfButton;
    private Button viewPdfButton;
    private TextView dateSelect;
    private ImageView calenderView;

    String name;
    String cycle;
    String category;
    String dateString;

    double value;
    int day;
    int month;
    int year;
    int calendarDay;
    int calendarMonth;
    int calendarYear;
    int numberOfPages;

    long compareTime;

    long intakeTime;
    long outgoeTime;
    Calendar intakeCalendar = Calendar.getInstance();
    Calendar outgoeCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfcreator);
        createPdfButton    = findViewById(R.id.createPdfButton);
        viewPdfButton    = findViewById(R.id.viewPdfButton);
        dateSelect = findViewById(R.id.selectView);
        calenderView = findViewById(R.id.calenderView);

        db = new MySQLite(this);
        // db.openDatabase(); // nicht mehr notwendig // Auskommentiert von Yvette Groner

        Calendar calendar = Calendar.getInstance();
        calendarYear  = calendar.get(Calendar.YEAR);
        calendarMonth = calendar.get(Calendar.MONTH);
        calendarDay   = calendar.get(Calendar.DAY_OF_MONTH);

        dateSelect.setText(calendarDay + "/" + (calendarMonth+1) + "/" + calendarYear);

        calendar.set(calendarYear,(calendarMonth+1),calendarDay,0,0,0);
        compareTime = calendar.getTimeInMillis();

        calenderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View dateView) {
                DatePickerDialog dateDialog = new DatePickerDialog(PDFCreatorActivity.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int setYear, int setMonth, int setDay) {
                        calendarYear = setYear;
                        calendarMonth = setMonth;
                        calendarDay = setDay;
                        calendar.set(calendarYear,(calendarMonth+1),calendarDay,0,0,0);
                        compareTime = calendar.getTimeInMillis();

                        //Addition bei Monat von 1, Index beginnend bei 0
                        dateSelect.setText(setDay + "/" + (setMonth+1) + "/" + setYear);
                    }
                }, calendarYear, calendarMonth, calendarDay);
                dateDialog.show();
            }
        });


        createPdfButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    printPDF();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        viewPdfButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                viewPdf();
            }
        });
    }


    public void printPDF()throws FileNotFoundException {

        String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File file = new File(pdfPath, "Haushaltsapp.pdf");
        OutputStream outputStream = new FileOutputStream(file);
        PdfWriter pdfWriter = new PdfWriter(file);

        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        Document document = new Document(pdfDocument,PageSize.A4,false );

        Paragraph chapter = new Paragraph("Haushaltplaner").setFontSize(22).setUnderline().setFixedPosition(1,220,750,500);
        Paragraph space = new Paragraph("\n");
        document.add(chapter);
        document.add(space);
        document.add(space);
        document.add(space);

        Table tableOutgoes = new Table(UnitValue.createPercentArray(5)).useAllAvailableWidth();
        tableOutgoes.addHeaderCell(new Cell(1, 5).add(new Paragraph("Ausgaben").setFontSize(16)).setFontColor(ColorConstants.DARK_GRAY).setRelativePosition(220,0,250,0));
        tableOutgoes.addHeaderCell(new Cell().add(new Paragraph("Bezeichenung").setWidth(170).setFontSize(14).setBorder(Border.NO_BORDER)));
        tableOutgoes.addHeaderCell(new Cell().add(new Paragraph("Wert").setWidth(60).setFontSize(14).setBorder(Border.NO_BORDER)));
        tableOutgoes.addHeaderCell(new Cell().add(new Paragraph("Datum").setWidth(115).setFontSize(14).setBorder(Border.NO_BORDER)));
        tableOutgoes.addHeaderCell(new Cell().add(new Paragraph("Zyklus").setWidth(65).setFontSize(14).setBorder(Border.NO_BORDER)));
        tableOutgoes.addHeaderCell(new Cell().add(new Paragraph("Kategorie").setWidth(90).setFontSize(14).setBorder(Border.NO_BORDER)));

        Cursor curOutgo = db.getWritableDatabase().rawQuery("SELECT * FROM outgo ORDER BY year,month,day", null);
        int countOutgo = curOutgo.getCount();
        curOutgo.moveToFirst();
        for (int j = 0; j < countOutgo; j++){

            //ID = Integer.parseInt(cursor.getString(0));
            name = curOutgo.getString(1);
            value = Double.parseDouble(curOutgo.getString(2));
            day = Integer.parseInt(curOutgo.getString(3));
            month = Integer.parseInt(curOutgo.getString(4));
            year = Integer.parseInt(curOutgo.getString(5));
            cycle =  curOutgo.getString(6);
            category =  curOutgo.getString(7);

            dateString = day+"."+month+"."+year;

            outgoeCalendar.set(year,month,day,0,0,0);
            outgoeTime = outgoeCalendar.getTimeInMillis() + 999;

            if(outgoeTime>=compareTime){

                tableOutgoes.addCell(new Cell().add(new Paragraph(name + "").setWidth(170).setFontSize(12)));
                tableOutgoes.addCell(new Cell().add(new Paragraph(value + "").setWidth(60).setFontSize(12)));
                tableOutgoes.addCell(new Cell().add(new Paragraph(dateString + "").setWidth(115).setFontSize(12)));
                tableOutgoes.addCell(new Cell().add(new Paragraph(cycle + "").setWidth(65).setFontSize(12)));
                tableOutgoes.addCell(new Cell().add(new Paragraph(category + "").setWidth(90).setFontSize(12)));
            }
            curOutgo.moveToNext();
        }

        curOutgo.close();
        db.close();

        Table tableIntakes = new Table(UnitValue.createPercentArray(5)).useAllAvailableWidth();
        tableIntakes.addHeaderCell(new Cell(1, 5).add(new Paragraph("Einnahmen").setFontSize(16)).setFontColor(ColorConstants.DARK_GRAY).setRelativePosition(220,0,250,0));
        tableIntakes.addHeaderCell(new Cell().add(new Paragraph("Bezeichenung").setWidth(170).setFontSize(14).setBorder(Border.NO_BORDER)));
        tableIntakes.addHeaderCell(new Cell().add(new Paragraph("Wert").setWidth(60).setFontSize(14).setBorder(Border.NO_BORDER)));
        tableIntakes.addHeaderCell(new Cell().add(new Paragraph("Datum").setWidth(115).setFontSize(14).setBorder(Border.NO_BORDER)));
        tableIntakes.addHeaderCell(new Cell().add(new Paragraph("Zyklus").setWidth(65).setFontSize(14).setBorder(Border.NO_BORDER)));
        tableIntakes.addHeaderCell(new Cell().add(new Paragraph("Kategorie").setWidth(90).setFontSize(14).setBorder(Border.NO_BORDER)));

        //Informationen aus Datenbank lesen

        Cursor curIntake = db.getWritableDatabase().rawQuery("SELECT * FROM intake ORDER BY year,month,day", null);
        int countIntake = curIntake.getCount();
        curIntake.moveToFirst();

        for (int l = 0; l < countIntake; l++){

            //ID = Integer.parseInt(cursor.getString(0));
            name = curIntake.getString(1);
            value = Double.parseDouble(curIntake.getString(2));
            day = Integer.parseInt(curIntake.getString(3));
            month = Integer.parseInt(curIntake.getString(4));
            year = Integer.parseInt(curIntake.getString(5));
            cycle =  curIntake.getString(6);
            category =  "";

            intakeCalendar.set(year,month,day,0,0,0);
            intakeTime = intakeCalendar.getTimeInMillis() + 999;

            dateString = day+"."+month+"."+year;

            if(intakeTime>=compareTime){

                tableIntakes.addCell(new Cell().add(new Paragraph(name + "").setWidth(170).setFontSize(12)));
                tableIntakes.addCell(new Cell().add(new Paragraph(value + "").setWidth(60).setFontSize(12)));
                tableIntakes.addCell(new Cell().add(new Paragraph(dateString + "").setWidth(115).setFontSize(12)));
                tableIntakes.addCell(new Cell().add(new Paragraph(cycle + "").setWidth(65).setFontSize(12)));
                tableIntakes.addCell(new Cell().add(new Paragraph(category + "").setWidth(90).setFontSize(12)));
            }
            curIntake.moveToNext();
        }

        curIntake.close();
        db.close();

        document.add(tableOutgoes);
        document.add(new AreaBreak());
        document.add(tableIntakes);

        numberOfPages = pdfDocument.getNumberOfPages();
        for (int i = 1; i <= numberOfPages; i++) {
            // Write aligned text to the specified by parameters point
            document.showTextAligned(new Paragraph(String.format("Seite %s von %s", i, numberOfPages)),
                    560, 15, i, TextAlignment.RIGHT, VerticalAlignment.BOTTOM, 0);
        }

        document.close();

    }

    private void viewPdf(){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/Haushaltsapp.pdf"), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "There is no app that can support this action",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.navigation_menu, menu);

        //Die aktuelle Activity im Menü ausblenden
        MenuItem item = menu.findItem(R.id.itemPdfCreator);
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
////table1.addCell(new Cell().add(new Paragraph("ID").setWidth(30).setFontSize(14).setBorder(Border.NO_BORDER).setBackgroundColor(ColorConstants.CYAN)));
//Table tableOutgoes = new Table(UnitValue.createPercentArray(7)).useAllAvailableWidth();

