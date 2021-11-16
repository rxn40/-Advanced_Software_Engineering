package com.example.haushaltsappversion2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

public class MySQLiteCategory extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "CategoryDB";


    public MySQLiteCategory(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_Category_TABLE = "CREATE TABLE category ( " + "id INTEGER PRIMARY KEY AUTOINCREMENT, " +  "name TEXT, "+ "border INTEGER,"+"color1 TEXt,"+"color2 TEXT)";
        db.execSQL(CREATE_Category_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS category");
        this.onCreate(db);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private static final String TABLE_CATEGORY = "category";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_BORDER = "border";
    private static final String KEY_COLOR1 = "color1";
    private static final String KEY_COLOR2 = "color2";
    private static final String[] COLUMNS = {KEY_ID, KEY_NAME, KEY_BORDER,KEY_COLOR1,KEY_COLOR2};

    public void addCategory(Category category){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put(KEY_NAME, category.getName());
        value.put(KEY_BORDER, category.getBorder());
        value.put(KEY_COLOR1, category.getColor1());
        value.put(KEY_COLOR2, category.getColor2());

        db.insert(TABLE_CATEGORY, null, value);
        db.close();

        Log.d("addCategory", category.toString());
    }

    public void deleteCategory(Category category){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+TABLE_CATEGORY+" WHERE "+KEY_NAME+" = \""+category.getName()+"\"";
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            String idCategory = cursor.getString(0);
            db.delete(TABLE_CATEGORY, KEY_ID+" = ?", new String[]{idCategory});
            db.close();
        }
    }


    public List<Category> getAllCategory(){
        List<Category> categories = new LinkedList<Category>();

        String query = "SELECT * FROM "+TABLE_CATEGORY;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Category category = null;
        if(cursor.moveToFirst()){
            do{
                category = new Category();
                category.setId(Integer.parseInt(cursor.getString(0)));
                category.setName(cursor.getString(1));
                category.setBorder(Integer.parseInt(cursor.getString(2)));
                category.setColor1(cursor.getString(3));
                category.setColor2(cursor.getString(4));

                categories.add(category);
            }while(cursor.moveToNext());
        }
        db.close();
        Log.d("getAllCategory", categories.toString());
        return categories;
    }

    public int updateCategory(Category category){

        int i = -1;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, category.getName());
        values.put(KEY_BORDER, category.getBorder());
        values.put(KEY_COLOR1, category.getColor1());
        values.put(KEY_COLOR2, category.getColor2());

        String query = "SELECT * FROM "+TABLE_CATEGORY+" WHERE "+KEY_NAME+" = \""+category.getName()+"\"";
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            String idCategory = cursor.getString(0);
            i = db.update(TABLE_CATEGORY, values, KEY_ID+" = ?", new String[]{idCategory});
            db.close();
        }

        return i;
    }

}

