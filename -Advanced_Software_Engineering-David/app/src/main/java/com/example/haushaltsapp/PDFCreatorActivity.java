package com.example.haushaltsapp;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.fonts.Font;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;

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
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class PDFCreatorActivity extends AppCompatActivity {

    ////Variabeln zur Menünavigation
    private MySQLite mySQLite;
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
    private int Storage_Permission_Code = 1;
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

        //Auf deutsche Kalenderanzeige umstellen
        Locale locale = new Locale("de");
        Locale.setDefault(locale);
        Resources res = this.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.locale = locale;
        res.updateConfiguration(config, res.getDisplayMetrics());

        calenderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View dateView) {
                DatePickerDialog dateDialog = new DatePickerDialog(PDFCreatorActivity.this,R.style.datePickerStyle, new DatePickerDialog.OnDateSetListener() {

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

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Storage_Permission_Code);
                }
                else
                {
                    try {
                        printPDF();
                        Toast.makeText(getApplicationContext(), "PDF generiert",
                                Toast.LENGTH_SHORT).show();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
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

        File file=new File( Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"Haushaltsapp.pdf");
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

            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Haushaltsapp.pdf");
            Uri uri =          FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()),
                    BuildConfig.APPLICATION_ID + ".provider", file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.setDataAndType(uri, "application/pdf");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Keine App auf Ihrem Handy unterstützt dieses Feature",
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
                int day = 0;  //Yvette
                int month = 0;  //Yvette
                int year = 0;  //Yvette
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
                mySQLite.close();
                startActivity(switchToAddCategory);
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
