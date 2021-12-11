package com.example.haushaltsapp;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.haushaltsapp.database.Category;
import com.example.haushaltsapp.database.Intake;
import com.example.haushaltsapp.database.MySQLite;
import com.example.haushaltsapp.database.Outgo;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;

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

    int ID;
    String name;
    double value;
    int day;
    int month;
    int year;
    String cycle;
    String category;
    Paragraph paragraphText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfcreator);
        createPdfButton    = findViewById(R.id.createPdfButton);
        viewPdfButton    = findViewById(R.id.viewPdfButton);
        db = new MySQLite(this);
        db.openDatabase();



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
            Document document = new Document(pdfDocument);
            pdfDocument.setDefaultPageSize(PageSize.A4);

            //boltText.setBold()
            //sizedText.setFontSize(20.0f)
            //coloredText.setFontColor(ColorConstants.RED)
            //alignedText.setTextAlignment(TextAlignment.CENTER)
            //textWithSpace.setMargins(10f, 10f, 10f, 10f)
            //Table table = new Table(UnitValue.createPercentArray(16)).useAllAvailableWidth();

            paragraphText = new Paragraph("My Text");
            document.add(paragraphText).setFontColor(ColorConstants.RED).setBold().setFontSize(20.0f).setTextAlignment(TextAlignment.CENTER);

            Table table1 = new Table(new float[]{1, 1, 1, 1, 1, 1, 1, 1});
            table1.setWidth(500).useAllAvailableWidth();
            table1.addCell(new Cell().add(new Paragraph("ID").setWidth(30).setFontSize(14).setBorder(Border.NO_BORDER).setBackgroundColor(ColorConstants.CYAN)));
            table1.addCell(new Cell().add(new Paragraph("Name").setWidth(160).setFontSize(14).setBorder(Border.NO_BORDER)));
            table1.addCell(new Cell().add(new Paragraph("Value").setWidth(50).setFontSize(14).setBorder(Border.NO_BORDER)));
            table1.addCell(new Cell().add(new Paragraph("Day").setWidth(40).setFontSize(14).setBorder(Border.NO_BORDER)));
            table1.addCell(new Cell().add(new Paragraph("Month").setWidth(40).setFontSize(14).setBorder(Border.NO_BORDER)));
            table1.addCell(new Cell().add(new Paragraph("Year").setWidth(40).setFontSize(14).setBorder(Border.NO_BORDER)));
            table1.addCell(new Cell().add(new Paragraph("Cycle").setWidth(70).setFontSize(14).setBorder(Border.NO_BORDER)));
            table1.addCell(new Cell().add(new Paragraph("Category").setWidth(70).setFontSize(14).setBorder(Border.NO_BORDER)));

            Table table2 = new Table(new float[]{1, 1, 1, 1, 1, 1, 1, 1});
            table2.setWidth(500);

            Cursor cursor = db.getWritableDatabase().rawQuery("SELECT * FROM intake", null);

            if (cursor != null) {
                while (cursor.moveToNext()){

                    ID = Integer.parseInt(cursor.getString(0));
                    name = cursor.getString(1);
                    value = Double.parseDouble(cursor.getString(2));
                    day = Integer.parseInt(cursor.getString(3));
                    month = Integer.parseInt(cursor.getString(4));
                    year = Integer.parseInt(cursor.getString(5));
                    cycle =  cursor.getString(6);

                    table2.addCell(new Cell().add(new Paragraph(ID + "").setWidth(30).setFontSize(14).setBorder(Border.NO_BORDER)));
                    table2.addCell(new Cell().add(new Paragraph(name + "").setWidth(160).setFontSize(14).setBorder(Border.NO_BORDER)));
                    table2.addCell(new Cell().add(new Paragraph(value + "").setWidth(50).setFontSize(14).setBorder(Border.NO_BORDER)));
                    table2.addCell(new Cell().add(new Paragraph(day + "").setWidth(40).setFontSize(14).setBorder(Border.NO_BORDER)));
                    table2.addCell(new Cell().add(new Paragraph(month + "").setWidth(40).setFontSize(14).setBorder(Border.NO_BORDER)));
                    table2.addCell(new Cell().add(new Paragraph(year + "").setWidth(40).setFontSize(14).setBorder(Border.NO_BORDER)));
                    table2.addCell(new Cell().add(new Paragraph(cycle + "").setWidth(70).setFontSize(14).setBorder(Border.NO_BORDER)));
                    table2.addCell(new Cell().add(new Paragraph(category + "").setWidth(70).setFontSize(14).setBorder(Border.NO_BORDER)));

                }
            }

            cursor.close();
            db.close();

            document.add(table1);
            document.add(table2);
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


    //private void emailNote()
    //    {
    //        Intent email = new Intent(Intent.ACTION_SEND);
    //        email.putExtra(Intent.EXTRA_SUBJECT,mSubjectEditText.getText().toString());
    //        email.putExtra(Intent.EXTRA_TEXT, mBodyEditText.getText().toString());
    //        Uri uri = Uri.parse(myFile.getAbsolutePath());
    //        email.putExtra(Intent.EXTRA_STREAM, uri);
    //        email.setType("message/rfc822");
    //        startActivity(email);
    //    }

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
