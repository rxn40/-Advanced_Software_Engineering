package com.example.haushaltsplaner;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MySQLiteIntake extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "IntakeDB";

    public MySQLiteIntake(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_Intake_TABLE = "CREATE TABLE intake ( " + "id INTEGER PRIMARY KEY AUTOINCREMENT, " +  "name TEXT, "+"value DOUBLE, "+"day INTEGER, "+"month INTEGER, "+"year INTEGER, " +"cycle TEXT)";
        db.execSQL(CREATE_Intake_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS intake");
        this.onCreate(db);
    }

    ///////////////////////////////////////////////////////
    private static final String TABLE_INTAKE= "intake";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_VALUE = "value";
    private static final String KEY_DAY = "day";
    private static final String KEY_MONTH = "month";
    private static final String KEY_YEAR = "year";
    private static final String KEY_CYCLE = "cycle";

    /*
        Funktion dient dazu, die ├╝bergebene Einnahme in die Datenbank einzutragen
         */
    public void addIntake(Intake intake){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put(KEY_NAME, intake.getName());
        value.put(KEY_VALUE, intake.getValue());
        value.put(KEY_DAY, intake.getDay());
        value.put(KEY_MONTH, intake.getMonth());
        value.put(KEY_YEAR, intake.getYear());
        value.put(KEY_CYCLE, intake.getCycle());

        db.insert(TABLE_INTAKE, null, value);
        db.close();
    }

    /*
       Funktion gibt eine ArrayList zur├╝ck, welche alle Einnahmen der Datenbank
       beinhaltet
        */
    public ArrayList<Intake> getAllIntakes(){
        ArrayList<Intake> intakes = new ArrayList<Intake>();

        String query = "SELECT * FROM "+TABLE_INTAKE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);


        if(cursor != null){
            Intake intake = null;
            if(cursor.moveToFirst()){
                do{
                    intake = new Intake();

                    intake.setId(Integer.parseInt(cursor.getString(0)));
                    intake.setName(cursor.getString(1));
                    intake.setValue(Double.parseDouble(cursor.getString(2)));
                    intake.setDay(Integer.parseInt(cursor.getString(3)));
                    intake.setMonth(Integer.parseInt(cursor.getString(4)));
                    intake.setYear(Integer.parseInt(cursor.getString(5)));
                    intake.setCycle(cursor.getString(6));

                    intakes.add(intake);
                }while(cursor.moveToNext());
            }

        }

        db.close();
        Log.d("getAllIntakes", intakes.toString());
        return intakes;
    }

    /*
    Die Funktion liefert die Einnahme zur├╝ck welche die ├╝bergebene id besitzt
    Sollte diese Id nicht existieren so wird eine "leere" Ausgabe (ohne name, value ect) zur├╝ck gegeben
    */
    public Intake getIntakeById(int id){
        Intake intake = new Intake();

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+TABLE_INTAKE+" WHERE "+KEY_ID+" = "+id;

        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){

            intake.setId(cursor.getInt(0));
            intake.setName(cursor.getString(1));
            intake.setValue(cursor.getDouble(2));
            intake.setDay(cursor.getInt(3));
            intake.setMonth(cursor.getInt(4));
            intake.setYear(cursor.getInt(5));
            intake.setCycle(cursor.getString(6));
        }
        db.close();

        return intake;
    }

    /*
    Funktion l├Âscht die Einnahme welche die ├╝bergebne Id besitzt.
    Sollte ein solcher Eintrag nicht exestieren wird die Datenbank ohne
    einen weiteren Vorgang geschlossen
    */
    public void deleteIntakeById(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+TABLE_INTAKE+" WHERE "+KEY_ID+" = "+id;
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            String idOutgo = cursor.getString(0);
            db.delete(TABLE_INTAKE, KEY_ID+" = ?", new String[]{idOutgo});

        }
        db.close();
    }

    /*
    Funktion dient dazu, die Zeile mit der ├╝bergebnen id mit den Informationen der
    ├╝bergebenen Einnahme zu ├╝berschreiben. Sollte eine solche Id nicht exestieren, wird
    ein neuer Eintrag mit den gew├╝nschten Daten angelegt.
     */
    public int updateIntake(Intake intake, int id){
        int i = -1;
        try {
            deleteIntakeById(id);
            addIntake(intake);
            i = 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i;
    }

    /*
    Funktion gibt eine ArrayList zur├╝ck, welche alle Einnahmen der Datenbank
    beinhaltet, welche vom 1.month.year bis day.month.year get├Ątigt wurden.
    Periodische Ausgaben wurden dabei ber├╝cksichtigt.
     */
    public float getValueIntakesMonth(int day, int month, int year) {
        List<Intake> intakes = getMonthIntakes( day, month, year);

        float value = 0;
        for(int i = 0; i < intakes.size(); i++){
            value = value + (float) intakes.get(i).getValue();
        }
        return value;
    }

    /*
    Funktion gibt eine Float-Wert zur├╝ck, welche alle Einnahmen der Datenbank
    ber├╝cksichtigt, welche vom 1.month.year bis day.month.year get├Ątigt wurden.
    Periodische Ausgaben wurden dabei ber├╝cksichtigt.
    */
    public ArrayList<Intake> getMonthIntakes(int day, int month, int year) {
        ArrayList<Intake> intakes = new ArrayList<Intake>();

        String query = "SELECT * FROM " + TABLE_INTAKE + " WHERE (" + KEY_CYCLE + " = \"monatlich\") OR (" + KEY_YEAR + " = \"" + String.valueOf(year) + "\" AND " + KEY_MONTH + " = \"" + String.valueOf(month) + "\")";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);


        if (cursor != null) {
            Intake intake = null;
            if (cursor.moveToFirst()) {

                do {

                    intake = new Intake();
                    String cycle = cursor.getString(6);

                    if (( "monatlich".equals(cycle) && (Integer.parseInt(cursor.getString(4)) < month) && (Integer.parseInt(cursor.getString(5)) <= year)) || (Integer.parseInt(cursor.getString(3)) <= day)) {

                        intake.setId(Integer.parseInt(cursor.getString(0)));
                        intake.setName(cursor.getString(1));
                        intake.setValue(Double.parseDouble(cursor.getString(2)));
                        intake.setDay(Integer.parseInt(cursor.getString(3)));
                        intake.setMonth(Integer.parseInt(cursor.getString(4)));
                        intake.setYear(Integer.parseInt(cursor.getString(5)));
                        intake.setCycle(cursor.getString(6));
                        intakes.add(intake);
                    }
                }while (cursor.moveToNext()) ;
            }
        }


        db.close();

        return intakes;
    }
}