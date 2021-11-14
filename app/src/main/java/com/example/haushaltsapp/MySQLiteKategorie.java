package com.example.haushaltsapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

public class MySQLiteKategorie extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "KategorieDB";

    //Konstruktor
    public MySQLiteKategorie(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_BOOK_TABLE = "CREATE TABLE kategorien ( " + "id INTEGER PRIMARY KEY AUTOINCREMENT, " +  "name TEXT, "+ "border DOUBLE,"+"color1 TEXt,"+"color2 TEXT)";
        db.execSQL(CREATE_BOOK_TABLE);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS kategorien");
        this.onCreate(db);
    }

    ////////////////////////////////////////////////////////
    private static final String TABLE_KATEGORIE = "kategorien";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_BORDER = "border";
    private static final String KEY_COLOR1 = "color1";
    private static final String KEY_COLOR2 = "color2";
    private static final String[] COLUMNS = {KEY_ID, KEY_NAME, KEY_BORDER,KEY_COLOR1,KEY_COLOR2};

    public void addKategorie(Kategorie kategorie){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put(KEY_NAME, kategorie.getName());
        value.put(KEY_BORDER, kategorie.getBorder());
        value.put(KEY_COLOR1, kategorie.getColor1());
        value.put(KEY_COLOR2, kategorie.getColor2());

        db.insert(TABLE_KATEGORIE, null, value);
        db.close();
        Log.d("addKategorie", kategorie.toString());
    }

    public Kategorie getKategorie(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_KATEGORIE, COLUMNS, "id = ?", new String[] {String.valueOf(id)},null,null,null,null);

        if (cursor != null){
            cursor.moveToFirst();
        }

        Kategorie kategorie = new Kategorie();
        kategorie.setId(Integer.parseInt(cursor.getString(0)));
        kategorie.setName(cursor.getString(1));
        kategorie.setBorder(Double.parseDouble(cursor.getString(2)));
        kategorie.setColor1(cursor.getString(3));
        kategorie.setColor2(cursor.getString(4));

        Log.d("getKategorie", kategorie.toString());
        return kategorie;
    }

    public List<Kategorie> getAllKategorien(){
        List<Kategorie> kategorien = new LinkedList<Kategorie>();

        String query = "SELECT * FROM "+TABLE_KATEGORIE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Kategorie kategorie = null;
        if(cursor.moveToFirst()){
            do{
                kategorie = new Kategorie();
                kategorie.setId(Integer.parseInt(cursor.getString(0)));
                kategorie.setName(cursor.getString(1));
                kategorie.setBorder(Double.parseDouble(cursor.getString(2)));
                kategorie.setColor1(cursor.getString(3));
                kategorie.setColor2(cursor.getString(4));

                kategorien.add(kategorie);
            }while(cursor.moveToNext());
        }

        Log.d("getAllKategorien", kategorien.toString());
        return kategorien;
    }


    public int updateKategorie(Kategorie kategorie){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, kategorie.getName());
        values.put(KEY_BORDER, kategorie.getBorder());
        values.put(KEY_COLOR1, kategorie.getColor1());
        values.put(KEY_COLOR2, kategorie.getColor2());

        int i = db.update(TABLE_KATEGORIE, values, KEY_ID+" = ?", new String[]{String.valueOf(kategorie.getId())});
        db.close();

        return i;
    }

    public void deleteKategorie(Kategorie kategorie){
        Log.d("deleteKategorie", "deleteKategorie");
        String number = String.valueOf(kategorie.getId());
        Log.d("deleteKategorie "+number, kategorie.toString());

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_KATEGORIE, KEY_ID+" = ?", new String[]{String.valueOf(kategorie.getId())});

        Log.d("deleteKategorie", kategorie.toString());
    }

}
