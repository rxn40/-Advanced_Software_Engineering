package com.example.haushaltsapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.haushaltsapp.ToDoListPackage.TaskModel;
import java.util.ArrayList;
import java.util.List;

public class MySQLite extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "BudgetAppDB";

    public MySQLite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public MySQLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_Intake_TABLE = "CREATE TABLE intake ( " + "id INTEGER PRIMARY KEY AUTOINCREMENT, " +  "name TEXT, "+"value DOUBLE, "+"day INTEGER, "+"month INTEGER, "+"year INTEGER, " +"cycle TEXT)";
        db.execSQL(CREATE_Intake_TABLE);
        String CREATE_OUTGO_TABLE = "CREATE TABLE outgo ( " + "id INTEGER PRIMARY KEY AUTOINCREMENT, " +  "name TEXT, "+"value DOUBLE, "+"day INTEGER, "+"month INTEGER, "+"year INTEGER, " +"cycle TEXT, "+"categoryName TEXT)";
        db.execSQL(CREATE_OUTGO_TABLE);
        String CREATE_CATEGORY_TABLE = "CREATE TABLE category ( " + "id INTEGER PRIMARY KEY AUTOINCREMENT, " +  "name TEXT, "+"color INTEGER, "+"border DOUBLE)";
        db.execSQL(CREATE_CATEGORY_TABLE);
        String CREATE_TODO_TABLE = "CREATE TABLE todo ( " + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "task TEXT, "+"status INTEGER)";
        db.execSQL(CREATE_TODO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS intake");
        this.onCreate(db);
        db.execSQL("DROP TABLE IF EXISTS outgo");
        this.onCreate(db);
        db.execSQL("DROP TABLE IF EXISTS category");
        this.onCreate(db);
        db.execSQL("DROP TABLE IF EXISTS todo");
        this.onCreate(db);
    }


    ////////////////////////////////MySQLite//////////////////////////////////////////////////////////
    private static final String TABLE_INTAKE= "intake";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_VALUE = "value";
    private static final String KEY_DAY = "day";
    private static final String KEY_MONTH = "month";
    private static final String KEY_YEAR = "year";
    private static final String KEY_CYCLE = "cycle";
    //////////////////////////////////////////////////////////////////////////////////////////
    private static final String TABLE_OUTGO= "outgo";
    private static final String KEY_CATEGORY = "categoryName";
    //////////////////////////////////////////////////////////////////////////////////////////
    private static final String TABLE_CATEGORY = "category";
    private static final String KEY_COLOR = "color";
    private static final String KEY_BORDER = "border";
    //////////////////////////////////////////////////////////////////////////////////////////
    private static final String TABLE_TODO  = "todo";
    private static final String TASK = "task";
    private static final String STATUS = "status";
    private SQLiteDatabase db;

    public void openDatabase() {
        db = this.getWritableDatabase();
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    // Tabelle intake: id, name, calue, day, month, year, cycle
    //////////////////////////////////////////////////////////////////////////////////////////

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
       Funktion gibt eine ArrayList zurück, welche alle Einnahmen der Datenbank
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

                    intake.setId_PK(Integer.parseInt(cursor.getString(0)));
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
    Die Funktion liefert die Einnahme zurück welche die übergebene id besitzt
    Sollte diese Id nicht existieren so wird eine "leere" Ausgabe (ohne name, value ect) zurück gegeben
    */
    public Intake getIntakeById(int id){
        Intake intake = new Intake();

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+TABLE_INTAKE+" WHERE "+KEY_ID+" = "+id;

        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            intake.setId_PK(cursor.getInt(0));
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
    Funktion löscht die Einnahme welche die übergebne Id besitzt.
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
    Funktion dient dazu, die Zeile mit der übergebnen id mit den Informationen der
    übergebenen Einnahme zu überschreiben. Sollte eine solche Id nicht exestieren, wird
    ein neuer Eintrag mit den gewünschten Daten angelegt.
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
    Funktion gibt eine ArrayList zurück, welche alle Einnahmen der Datenbank
    beinhaltet, welche vom 1.month.year bis day.month.year getätigt wurden.
    Periodische Ausgaben wurden dabei berücksichtigt.
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
    Funktion gibt eine Float-Wert zurück, welche alle Einnahmen der Datenbank
    berücksichtigt, welche vom 1.month.year bis day.month.year getätigt wurden.
    Periodische Ausgaben wurden dabei berücksichtigt.
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

                        intake.setId_PK(Integer.parseInt(cursor.getString(0)));
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


    //////////////////////////////////////////////////////////////////////////////////////////

    /*
  Tabelle outgo: id, name, calue, day, month, year, cycle, category
   */
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
        value.put(KEY_CATEGORY, outgo.getCategory());

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

                    outgo.setId_PK(Integer.parseInt(cursor.getString(0)));
                    outgo.setName(cursor.getString(1));
                    outgo.setValue(Double.parseDouble(cursor.getString(2)));
                    outgo.setDay(Integer.parseInt(cursor.getString(3)));
                    outgo.setMonth(Integer.parseInt(cursor.getString(4)));
                    outgo.setYear(Integer.parseInt(cursor.getString(5)));
                    outgo.setCycle(cursor.getString(6));
                    outgo.setCategory(cursor.getString(7));

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

            outgo.setId_PK(cursor.getInt(0));
            outgo.setName(cursor.getString(1));
            outgo.setValue(cursor.getDouble(2));
            outgo.setDay(cursor.getInt(3));
            outgo.setMonth(cursor.getInt(4));
            outgo.setYear(cursor.getInt(5));
            outgo.setCycle(cursor.getString(6));
            outgo.setCategory(cursor.getString(7));
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

                        outgo.setId_PK(Integer.parseInt(cursor.getString(0)));
                        outgo.setName(cursor.getString(1));
                        outgo.setValue(Double.parseDouble(cursor.getString(2)));
                        outgo.setDay(Integer.parseInt(cursor.getString(3)));
                        outgo.setMonth(Integer.parseInt(cursor.getString(4)));
                        outgo.setYear(Integer.parseInt(cursor.getString(5)));
                        outgo.setCycle(cursor.getString(6));
                        outgo.setCategory(cursor.getString(7));

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

    //////////////////////////////////////////////////////////////////////////////////////////

      /*
    Tabelle category: id, name, color, border
     */

    /*
    Category hinzufügen
     */
    public void addCategory(Category category){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put(KEY_NAME, category.getName_PK());
        value.put(KEY_COLOR, category.getColor());
        value.put(KEY_BORDER, category.getBorder());

        db.insert(TABLE_CATEGORY, null, value);
        db.close();
        Log.d("addCategory", category.toString());
    }

    // Lösche Kategorie mit id
    public void deleteCategoryById(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+TABLE_CATEGORY+" WHERE "+KEY_ID+" = "+id;
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            String idcategory = cursor.getString(0);
            db.delete(TABLE_CATEGORY, KEY_ID+" = ?", new String[]{idcategory});
        }
        db.close();
    }

    // Lösche Kategorie mit namen
    public void deleteCategoryByName(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+TABLE_CATEGORY+" WHERE "+KEY_NAME+" = \""+name+"\"";
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            String idcategory = cursor.getString(0);
            db.delete(TABLE_CATEGORY, KEY_ID+" = ?", new String[]{idcategory});
        }
        db.close();
    }

    /*
    Alle Categorien bekommen
     */
    public ArrayList<Category> getAllCategory(){
        ArrayList<Category> categories = new ArrayList<Category>();

        String query = "SELECT * FROM "+TABLE_CATEGORY;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);


        if(cursor != null){
            Category category = null;
            if(cursor.moveToFirst()){
                do{
                    category = new Category();

                    category.setId(Integer.parseInt(cursor.getString(0)));
                    category.setName_PK(cursor.getString(1));
                    category.setColor(Integer.parseInt(cursor.getString(2)));
                    category.setBorder(Double.parseDouble(cursor.getString(3)));


                    categories.add(category);
                }while(cursor.moveToNext());
            }

        }
        db.close();
        Log.d("getAllCategories", categories.toString());
        return categories;
    }

    public Category getCategory(String name){
        Category category = new Category();

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+TABLE_CATEGORY+" WHERE "+KEY_NAME+" = "+name;

        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){

            category.setId(cursor.getInt(0));
            category.setName_PK(cursor.getString(1));
            category.setColor(cursor.getInt(2));
            category.setBorder(cursor.getDouble(3));
        }
        db.close();

        return category;
    }



////////////////////////////////////To Do Listen ////////////////////////////////////////////



    public void insertTask(TaskModel task){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put(TASK, task.getTask());
        value.put(STATUS, 0);
        db.insert(TABLE_TODO, null, value);
        db.close();
    }


    public List<TaskModel> getAllTasks(){
        List<TaskModel> taskList = new ArrayList<>();

        String query = "SELECT * FROM "+TABLE_TODO;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor != null){
            TaskModel task = null;
            if(cursor.moveToFirst()){
                do{
                    task = new TaskModel();
                    task.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
                    task.setTask(cursor.getString(cursor.getColumnIndexOrThrow(TASK)));
                    task.setStatus(cursor.getInt(cursor.getColumnIndexOrThrow(STATUS)));
                    taskList.add(task);
                }
                while(cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        return taskList;
    }

    public void updateTask(int id, String task){
        ContentValues cv = new ContentValues();
        cv.put(TASK, task);
        db.update(TABLE_TODO, cv, KEY_ID + "= ?", new String[] {String.valueOf(id)});
    }

    //Auffrischen des Task Status
    public void updateStatus(int id, int status){
        ContentValues cv = new ContentValues();
        cv.put(STATUS, status);
        db.update(TABLE_TODO, cv, KEY_ID + "= ?", new String[] {String.valueOf(id)});
    }


    //Löschen der Task
    public void deleteTask(int id){
        db.delete(TABLE_TODO, KEY_ID + "= ?", new String[] {String.valueOf(id)});
    }
}



