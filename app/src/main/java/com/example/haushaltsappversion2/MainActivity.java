package com.example.haushaltsappversion2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    MySQLiteCategory dbcategory = new MySQLiteCategory(this, null, null, 0);
    MySQLiteIntake dbintake = new MySQLiteIntake(this, null, null, 0);
    MySQLiteOutgo dboutgo = new MySQLiteOutgo(this, null, null, 0);

    List<String> categoryList = Arrays.asList("Wohnen","Verkehrsmittel","Lebensmittel","Gesundheit", "Freizeit", "Sonstiges");

    private static final int REQUESTCODE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(dbcategory.getAllCategory().size() == 0) {
            setCategory();
        }
    }



    //Ruft anderen View auf
    public void addButton(View view){
        Intent i = new Intent(this, IntakeAndOutgoActivity.class);
        startActivityForResult(i,REQUESTCODE);
    }

    //Trägt die gewünschten Daten in die Datenbank ein
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUESTCODE) {
            //Später abfragen ob Einnahme oder Ausgabe
            String entry = data.getExtras().getString("entry");

            String name = data.getExtras().getString("name");
            double value = data.getExtras().getDouble("value");
            int day = data.getExtras().getInt("day");
            int month = data.getExtras().getInt("month");
            int year = data.getExtras().getInt("year");
            String cycle = data.getExtras().getString("cycle");

            if(entry.equals("Intake")){ //Eingabe

               Intake intake = new Intake( name,  value,  day,  month,  year, cycle);
               //dbintake.addIntake(intake);
               showIntakes();
            }else{ //Ausgabe

               String category = data.getExtras().getString("category");
               Outgo outgo = new Outgo( name,  value,  day,  month,  year, cycle, category);
                //dboutgo.addOutgo(outgo);
                showOutgos();
            }
        }
    }

    //wie kommt man an die Werte der Intakes:
    private void showIntakes(){
        float value = dbintake.getValueIntakesMonth(11,8,2021);
        TextView ansicht = (TextView) findViewById(R.id.textViewKat);
        String text = "Einnahmen des 8 Monats : "+String.valueOf(value);
        ansicht.setText(text.toCharArray(), 0, text.length());
    }


    //wie kommt man an die Werte der Outgos:
    private void showOutgos(){
        float value = dboutgo.getValueOutgosMonth(11,12,2021);
        TextView ansicht = (TextView) findViewById(R.id.textViewKat);
        String text = "Alle Ausgaben des 8 Monats : "+String.valueOf(value);

        for(int i = 0; i < categoryList.size(); i++){
            text = text + "'\n'"+categoryList.get(i)+" = "+dboutgo.getValuesOutgosCategory(11,12,2021, categoryList.get(i));
        }

        ansicht.setText(text.toCharArray(), 0, text.length());
    }
    /////////////////////////////////////////////////////////////////////////////

    //Falls die Datenbank leer ist, werden die Kategorien gesetzt
    private void setCategory(){
        Category category = new Category("Default", 90, "black", "white");
        //Werte aus der oberen Liste für den Namen setzen
        for(int i = 0; i < categoryList.size(); i++){
            category.setName(categoryList.get(i));
            dbcategory.addCategory(category);
        }
    }

}