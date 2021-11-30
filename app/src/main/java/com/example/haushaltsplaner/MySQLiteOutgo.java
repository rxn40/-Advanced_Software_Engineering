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

public class MySQLiteOutgo extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "OutgoDB";

    public MySQLiteOutgo(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_OUTGO_TABLE = "CREATE TABLE outgo ( " + "id INTEGER PRIMARY KEY AUTOINCREMENT, " +  "name TEXT, "+"value DOUBLE, "+"day INTEGER, "+"month INTEGER, "+"year INTEGER, " +"cycle TEXT)";
        db.execSQL(CREATE_OUTGO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS outgo");
        this.onCreate(db);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    private static final String TABLE_OUTGO= "outgo";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_VALUE = "value";
    private static final String KEY_DAY = "day";
    private static final String KEY_MONTH = "month";
    private static final String KEY_YEAR = "year";
    private static final String KEY_CYCLE = "cycle";


    /*
    Funktion dient dazu, die übergebene Ausgabe in die Datenbank einzutragen
     */
    public void addOutgo(Outgo outgo){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put(KEY_NAME, outgo.getName());
        value.put(KEY_VALUE, outgo.getValue());
        value.put(KEY_DAY, outgo.getDay());
        value.put(KEY_MONTH, outgo.getMonth());
        value.put(KEY_YEAR, outgo.getYear());
        value.put(KEY_CYCLE, outgo.getCycle());

        db.insert(TABLE_OUTGO, null, value);
        db.close();
        Log.d("addOutgo", outgo.toString());
    }


   /*
   Funktion gibt eine ArrayList zurück, welche alle Ausgaben der Datenbank
   beinhaltet
    */
    public ArrayList<Outgo> getAllOutgo(){
        ArrayList<Outgo> outgos = new ArrayList<Outgo>();

        String query = "SELECT * FROM "+TABLE_OUTGO;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);


        if(cursor != null){
            Outgo outgo = null;
            if(cursor.moveToFirst()){
                do{
                    outgo = new Outgo();

                    outgo.setId(Integer.parseInt(cursor.getString(0)));
                    outgo.setName(cursor.getString(1));
                    outgo.setValue(Double.parseDouble(cursor.getString(2)));
                    outgo.setDay(Integer.parseInt(cursor.getString(3)));
                    outgo.setMonth(Integer.parseInt(cursor.getString(4)));
                    outgo.setYear(Integer.parseInt(cursor.getString(5)));
                    outgo.setCycle(cursor.getString(6));

                    outgos.add(outgo);
                }while(cursor.moveToNext());
            }

        }
        db.close();
        Log.d("getAllOutgos", outgos.toString());
        return outgos;
    }


    /*
    Die Funktion liefert die Ausgabe zurück welche die übergebene id besitzt
    Sollte diese Id nicht existieren so wird eine "leere" Ausgabe (ohne name, value ect) zurück gegeben
     */
    public Outgo getOutgoById(int id){
        Outgo outgo = new Outgo();

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+TABLE_OUTGO+" WHERE "+KEY_ID+" = "+id;

        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){

            outgo.setId(cursor.getInt(0));
            outgo.setName(cursor.getString(1));
            outgo.setValue(cursor.getDouble(2));
            outgo.setDay(cursor.getInt(3));
            outgo.setMonth(cursor.getInt(4));
            outgo.setYear(cursor.getInt(5));
            outgo.setCycle(cursor.getString(6));
        }
        db.close();

        return outgo;
    }

    /*
    Funktion löscht die Ausgabe welche die übergebne Id besitzt.
    Sollte ein solcher Eintrag nicht exestieren wird die Datenbank ohne
    einen weiteren Vorgang geschlossen
     */
    public void deleteOutgoById(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+TABLE_OUTGO+" WHERE "+KEY_ID+" = "+id;
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            String idOutgo = cursor.getString(0);
            db.delete(TABLE_OUTGO, KEY_ID+" = ?", new String[]{idOutgo});

        }
        db.close();
    }


    /*
    Funktion dient dazu, die Zeile mit der übergebnen id mit den Informationen der
    übergebenen Ausgabe zu überschreiben. Sollte eine solche Id nicht exestieren, wird
    ein neuer Eintrag mit den gewünschten Daten angelegt.
     */
    public int updateOutgo(Outgo outgo, int id){
        int i = -1;
        try {
            deleteOutgoById(id);
            addOutgo(outgo);
            i = 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i;
    }

    /*
   Funktion gibt eine ArrayList zurück, welche alle Ausgaben der Datenbank
   beinhaltet, welche vom 1.month.year bis day.month.year getätigt wurden.
   Periodische Ausgaben wurden dabei berücksichtigt.
    */
    public ArrayList<Outgo> getMonthOutgos(int day, int month, int year) {
        ArrayList<Outgo> outgos = new ArrayList<Outgo>();

        String query = "SELECT * FROM " + TABLE_OUTGO + " WHERE (" + KEY_CYCLE + " = \"monatlich\") OR (" + KEY_YEAR + " = \"" + String.valueOf(year) + "\" AND " + KEY_MONTH + " = \"" + String.valueOf(month) + "\")";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);


        if (cursor != null) {
            Outgo outgo = null;
            if (cursor.moveToFirst()) {
                do {
                    outgo = new Outgo();
                    String cycle = cursor.getString(6);

                    if (( "monatlich".equals(cycle) && (Integer.parseInt(cursor.getString(4)) < month) && (Integer.parseInt(cursor.getString(5)) <= year)) || Integer.parseInt(cursor.getString(3)) <= day) {

                        outgo.setId(Integer.parseInt(cursor.getString(0)));
                        outgo.setName(cursor.getString(1));
                        outgo.setValue(Double.parseDouble(cursor.getString(2)));
                        outgo.setDay(Integer.parseInt(cursor.getString(3)));
                        outgo.setMonth(Integer.parseInt(cursor.getString(4)));
                        outgo.setYear(Integer.parseInt(cursor.getString(5)));

                        outgos.add(outgo);
                    }
                }
                while (cursor.moveToNext()) ;
            }
        }
        db.close();

        return outgos;
    }

    /*
   Funktion gibt eine Float-Wert zurück, welche alle Ausgaben der Datenbank
   berücksichtigt, welche vom 1.month.year bis day.month.year getätigt wurden.
   Periodische Ausgaben wurden dabei berücksichtigt.
    */
    public float getValueOutgosMonth(int day, int month, int year) {
        List<Outgo> outgos = getMonthOutgos( day, month, year);

        float value = 0;
        for(int i = 0; i < outgos.size(); i++){
            value = value + (float) outgos.get(i).getValue();
        }
        return value;
    }
}