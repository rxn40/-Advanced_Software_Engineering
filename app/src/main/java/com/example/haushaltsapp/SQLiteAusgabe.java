package com.example.haushaltsapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

public class SQLiteAusgabe extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "AusgabeDB";

    public SQLiteAusgabe(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_AUSGABE_TABLE = "CREATE TABLE ausgabe ("+"id INTEGER PRIMARY KEY AUTOINCREMENT, "+"name TEXT, "+"date TEXT, "+"cycle TEXT, "+"kategorie TEXT)";
        db.execSQL(CREATE_AUSGABE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS ausgaben");
        this.onCreate(db);
    }

    ///////////////////////////////////////////////////////////////////////
    private static final String TABLE_AUSGABE = "ausgaben";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_DATE = "date";
    private static final String KEY_CYCLE = "cycle";
    private static final String KEY_KATEGORIE = "kategorie";
    private static final String[] COLUMNS = {KEY_ID, KEY_NAME, KEY_DATE, KEY_CYCLE, KEY_KATEGORIE};

    public void addAusgabe(Ausgabe ausgabe){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, ausgabe.getName());
        values.put(KEY_DATE, ausgabe.getDate());
        values.put(KEY_CYCLE, ausgabe.getCycle());
        values.put(KEY_KATEGORIE, ausgabe.getKategorie());

        db.insert(TABLE_AUSGABE, null, values);
        db.close();
        Log.d("addAusgabe",ausgabe.toString());
    }

    public Ausgabe getAusgabe(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_AUSGABE, COLUMNS, " id = ?", new String[]{String.valueOf(id)},null, null, null,null);

        if(cursor != null){
            cursor.moveToFirst();
        }
        Ausgabe ausgabe = new Ausgabe();
        ausgabe.setId(Integer.parseInt(cursor.getString(0)));
        ausgabe.setName(cursor.getString(1));
        ausgabe.setDate(cursor.getString(2));
        ausgabe.setCycle(cursor.getString(3));
        ausgabe.setKategorie(cursor.getString(4));

        Log.d("getEinnahme", ausgabe.toString());
        db.close();
        return ausgabe;
    }

    public List<Ausgabe> getAllAusgaben(){
        List<Ausgabe> ausgaben = new LinkedList<Ausgabe>();
        String query = "SELECT * FROM "+TABLE_AUSGABE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Ausgabe ausgabe = null;
        if(cursor.moveToFirst()){
            do{
                ausgabe = new Ausgabe();
                ausgabe.setId(Integer.parseInt(cursor.getString(0)));
                ausgabe.setName(cursor.getString(1));
                ausgabe.setDate(cursor.getString(2));
                ausgabe.setCycle(cursor.getString(3));
                ausgabe.setKategorie(cursor.getString(4));

                ausgaben.add(ausgabe);
            }while(cursor.moveToNext());
        }

        Log.d("getAllAusgaben", ausgaben.toString());
        db.close();
        return ausgaben;
    }

    public int updateAusgabe(Ausgabe ausgabe){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, ausgabe.getName());
        values.put(KEY_DATE, ausgabe.getDate());
        values.put(KEY_CYCLE, ausgabe.getCycle());
        values.put(KEY_KATEGORIE, ausgabe.getKategorie());

        int i = db.update(TABLE_AUSGABE, values, KEY_ID+" = ?", new String[] {String.valueOf(ausgabe.getId())});
        db.close();
        return i;
    }

    public void deletAusgabe(Ausgabe ausgabe){
        Log.d("deleteAusgabe","deleteAusgabe");
        String number = String.valueOf(ausgabe.getId());
        Log.d("deleteAusgabe"+number, ausgabe.toString());
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_AUSGABE, KEY_ID+" = ?", new String[] {String.valueOf(ausgabe.getId())});
        db.close();
        Log.d("delteEinnahme",ausgabe.toString());
    }




}
