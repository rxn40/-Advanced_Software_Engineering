package com.example.haushaltsapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

public class MySQLiteEinnahme extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "EinnahmeDB";

    public MySQLiteEinnahme(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_EINNAHME_TABLE = "CREATE TABLE einnahmen ("+"id INTEGER PRIMARY KEY AUTOINCREMENT, "+"name TEXT, "+"date TEXT, "+"cycle TEXT )";
        db.execSQL(CREATE_EINNAHME_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS einnahmen");
        this.onCreate(db);
    }

    ///////////////////////////////////////////////////////////////////////
    private static final String TABLE_EINNAHME = "einnahmen";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_DATE = "date";
    private static final String KEY_CYCLE = "cycle";
    private static final String[] COLUMNS = {KEY_ID, KEY_NAME, KEY_DATE, KEY_CYCLE};

    public void addEintrag(Einnahme einnahme){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, einnahme.getName());
        values.put(KEY_DATE, einnahme.getDate());
        values.put(KEY_CYCLE, einnahme.getCycle());

        db.insert(TABLE_EINNAHME, null, values);
        db.close();
        Log.d("addEintrag",einnahme.toString());
    }

    public Einnahme getEinnahme (int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_EINNAHME, COLUMNS, " id = ?", new String[]{String.valueOf(id)},null, null, null,null);

        if(cursor != null){
            cursor.moveToFirst();
        }
        Einnahme einnahme = new Einnahme();
        einnahme.setId(Integer.parseInt(cursor.getString(0)));
        einnahme.setName(cursor.getString(1));
        einnahme.setDate(cursor.getString(2));
        einnahme.setCycle(cursor.getString(3));

        Log.d("getEinnahme", einnahme.toString());
        db.close();
        return einnahme;
    }

    public List<Einnahme> getAllEinnahmen(){
        List<Einnahme> einnahmen = new LinkedList<Einnahme>();
        String query = "SELECT * FROM "+TABLE_EINNAHME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Einnahme einnahme = null;
        if(cursor.moveToFirst()){
            do{
                einnahme = new Einnahme();
                einnahme.setId(Integer.parseInt(cursor.getString(0)));
                einnahme.setName(cursor.getString(1));
                einnahme.setDate(cursor.getString(2));
                einnahme.setCycle(cursor.getString(3));

                einnahmen.add(einnahme);
            }while(cursor.moveToNext());
        }

        Log.d("getAllEinnahmen", einnahmen.toString());
        db.close();
        return einnahmen;
    }

    public int updateBook(Einnahme einnahme){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, einnahme.getName());
        values.put(KEY_DATE, einnahme.getDate());
        values.put(KEY_CYCLE, einnahme.getCycle());

        int i = db.update(TABLE_EINNAHME, values, KEY_ID+" = ?", new String[] {String.valueOf(einnahme.getId())});
        db.close();
        return i;
    }

    public void deletEinnahme(Einnahme einnahme){
        Log.d("deleteEinnahme","deleteEinnahme");
        String number = String.valueOf(einnahme.getId());
        Log.d("deleteEinnahme"+number, einnahme.toString());
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EINNAHME, KEY_ID+" = ?", new String[] {String.valueOf(einnahme.getId())});
        db.close();
        Log.d("delteEinnahme",einnahme.toString());
    }


}
