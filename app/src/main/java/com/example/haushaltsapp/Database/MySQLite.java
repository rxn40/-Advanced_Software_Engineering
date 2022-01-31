package com.example.haushaltsapp.Database;

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

    // Datenbankversion
    private static final int DATABASE_VERSION = 1;
    // Name der Datenbank
    private static final String DATABASE_NAME = "BudgetAppDB";

    public MySQLite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public MySQLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Erstellen der Tabellen
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_Intake_TABLE = "CREATE TABLE intake ( " + "id INTEGER PRIMARY KEY AUTOINCREMENT, " +  "name TEXT, "+"value DOUBLE, "+"day INTEGER, "+"month INTEGER, "+"year INTEGER, " +"cycle TEXT)";
        db.execSQL(CREATE_Intake_TABLE);

        String CREATE_OUTGO_TABLE = "CREATE TABLE outgo ( " + "id INTEGER PRIMARY KEY AUTOINCREMENT, " +  "name TEXT, "+"value DOUBLE, "+"day INTEGER, "+"month INTEGER, "+"year INTEGER, " +"cycle TEXT, "+"categoryName TEXT)";
        db.execSQL(CREATE_OUTGO_TABLE);

        String CREATE_CATEGORY_TABLE = "CREATE TABLE category ( " + "id INTEGER PRIMARY KEY AUTOINCREMENT, " +  "name TEXT, "+"color INTEGER, "+"border DOUBLE)";
        db.execSQL(CREATE_CATEGORY_TABLE);

        String CREATE_TODO_TABLE = "CREATE TABLE todo ( " + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "task TEXT, "+"status INTEGER, "+ "type TEXT)";
        db.execSQL(CREATE_TODO_TABLE);

        String CREATE_Limit_TABLE = "CREATE TABLE limitState ( " + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "name TEXT, "+"value DOUBLE, "+"state TEXT)";
        db.execSQL(CREATE_Limit_TABLE);
    }

    //Tabellen aktuallisieren
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
        db.execSQL("DROP TABLE IF EXISTS limitState");
        this.onCreate(db);
    }


    ////////////////////////////////MySQLite//////////////////////////////////////////////////
    private static final String TABLE_INTAKE= "intake"; //id, name, value, day, month, year, cycle
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_VALUE = "value";
    private static final String KEY_DAY = "day";
    private static final String KEY_MONTH = "month";
    private static final String KEY_YEAR = "year";
    private static final String KEY_CYCLE = "cycle";
    //////////////////////////////////////////////////////////////////////////////////////////
    private static final String TABLE_OUTGO= "outgo"; //id, name, value, day, month, year, cycle, categoryName
    private static final String KEY_CATEGORY = "categoryName";
    //////////////////////////////////////////////////////////////////////////////////////////
    private static final String TABLE_CATEGORY = "category"; //id, name, color, border
    private static final String KEY_COLOR = "color";
    private static final String KEY_BORDER = "border";
    //////////////////////////////////////////////////////////////////////////////////////////
    private static final String TABLE_TODO  = "todo";
    private static final String KEY_TASK = "task";
    private static final String KEY_STATUS = "status";
    private static final String KEY_TYPE = "type";
    //////////////////////////////////////////////////////////////////////////////////////////
    private static final String TABLE_LIMIT_STATE = "limitState"; // id, name, border, state
    private static final String KEY_STATE = "state";


    //////////////////////////////////////////////////////////////////////////////////////////
    // Tabelle intake: id, name, value, day, month, year, cycle
    //////////////////////////////////////////////////////////////////////////////////////////

    /*
    Funtkion, um eine Einnahme der Tabelle TABLE_INTAKE hinzuzufügen
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

        db.insert(TABLE_INTAKE, null, value); //neue Zeile in der Tabelle
        db.close(); // Datenbank schließen
    }


    /*
    Die Funktion liefert Rückgabe einer ArrayList, welche alle Einnahmen der Datenbank beinhaltet.
    Sortiert nach dem Datum. Aufsteigend
    */
    public ArrayList<Intake> getAllIntakes(){
        ArrayList<Intake> intakes = new ArrayList<Intake>(); //Rückgabewert

        String query = "SELECT * FROM "+TABLE_INTAKE +" ORDER BY "+KEY_YEAR+", "+KEY_MONTH+", "+KEY_DAY;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor != null){ //Tabelle enthaelt mindestens eine Zeile
            Intake intake = null;
            if(cursor.moveToFirst()){
                do{
                    intake = new Intake();
                    intake.setId_PK(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
                    intake.setName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)));
                    intake.setValue(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_VALUE)));
                    intake.setDay(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_DAY)));
                    intake.setMonth(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_MONTH)));
                    intake.setYear(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_YEAR)));
                    intake.setCycle(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CYCLE)));

                    intakes.add(intake);
                }while(cursor.moveToNext());
            }
        }

        db.close(); //Datenbank schließen
        return intakes; // alle Einnahmen sortiert nach Datum
    }


    /*
    Die Funktion liefert die Einnahme zurück, welche die übergebene ID besitzt.
    Sollte diese ID nicht existieren, eine Einnahme mit default-Werten fuer die jeweiligen
    Attribute zurück gegeben
    */
    public Intake getIntakeById(int id){
        Intake intake = new Intake(); //Rückgabewert

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+TABLE_INTAKE+" WHERE "+KEY_ID+" = "+id;

        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){ //Ein solcher Eintrag existiert
            intake.setId_PK(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
            intake.setName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)));
            intake.setValue(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_VALUE)));
            intake.setDay(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_DAY)));
            intake.setMonth(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_MONTH)));
            intake.setYear(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_YEAR)));
            intake.setCycle(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CYCLE)));
        }
        db.close(); //Datenbank schließen

        return intake; //Einname zurueck geben
    }


    /*
    Funktion liefert eine ID einer Einname, welche den übergebenen Namen aufweist.
    Wenn es eine solche ID nicht gibt, wird -1 zurück gegeben.
     */
    public int getIntakeIdByName(String name){
        int result = -1; //default Rückgabewert

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+TABLE_INTAKE+" WHERE "+KEY_NAME+" = \""+name+"\"";
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){ //Ein solcher Eintrag mit dem Namen existiert
            result = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)); //result überschreiben
        }

        db.close(); //Datenbank schließen
        return result;
    }


    /*
    Funktion löscht die Einnahme, welche die übergebne ID besitzt.
    Sollte ein solcher Eintrag nicht existieren, wird die Datenbank ohne
    einen weiteren Vorgang geschlossen.
    */
    public void deleteIntakeById(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+TABLE_INTAKE+" WHERE "+KEY_ID+" = "+id;
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){ //Ein solcher Eintrag exisitert
            String idIntake = cursor.getString(0);
            db.delete(TABLE_INTAKE, KEY_ID+" = ?", new String[]{idIntake}); //lösche Eintrag
        }
        db.close(); //
    }


    /*
    Funktion dient dazu, die Zeile der Tabelle mit der übergebenen ID mit den Informationen der
    übergebenen Einnahme zu überschreiben. Sollte eine solche ID nicht existieren, wird
    ein neuer Eintrag mit den gewünschten Daten angelegt.
     */
    public void updateIntake(Intake intake, int id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put(KEY_NAME, intake.getName());
        value.put(KEY_VALUE, intake.getValue());
        value.put(KEY_DAY, intake.getDay());
        value.put(KEY_MONTH, intake.getMonth());
        value.put(KEY_YEAR, intake.getYear());
        value.put(KEY_CYCLE, intake.getCycle());
        db.update(TABLE_INTAKE, value, KEY_ID+" = ?", new String[] { String.valueOf(id) });
        db.close(); //Datenbank schließen
    }


    /*
    Funktion gibt eine ArrayList zurück, welche alle Einnahmen der Datenbank
    beinhaltet, welche vom 1.month.year bis day.month.year getätigt wurden.
    Periodische Ausgaben aus vergangenen Monaten sind hierbei berücksichtigt.
    Sortiert nach dem Datum aufsteigend
    */
    public ArrayList<Intake> getMonthIntakes(int day, int month, int year) {
        ArrayList<Intake> intakes = new ArrayList<Intake>(); //Rückgabewert

        // Einträge der vergangenen Monate mit dem Zyklus monatlich
        String condition1 = "(" + KEY_CYCLE + " = \"monatlich\" AND "+KEY_YEAR + " <= \"" + String.valueOf(year)+"\" AND "+KEY_MONTH+"< \""+ String.valueOf(month) +"\" )";
        // Einträge des aktuellen Monats vom 1.month.year bis day.month.year
        String condition2 = "("+ KEY_YEAR + " = \"" + String.valueOf(year) + "\" AND " + KEY_MONTH + " = \"" + String.valueOf(month) +"\" AND "+KEY_DAY+" <= \""+String.valueOf(day) +"\")";
        String query = "SELECT * FROM " + TABLE_INTAKE + " WHERE "+condition1+" OR " +condition2+" ORDER BY "+KEY_YEAR+", "+KEY_MONTH+", "+KEY_DAY;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {
            Intake intake = null;
            if (cursor.moveToFirst()) {
                do {
                    intake = new Intake();

                    intake.setId_PK(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
                    intake.setName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)));
                    intake.setValue(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_VALUE)));
                    intake.setDay(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_DAY)));
                    intake.setMonth(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_MONTH)));
                    intake.setYear(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_YEAR)));
                    intake.setCycle(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CYCLE)));
                    intakes.add(intake);
                }while (cursor.moveToNext()) ;
            }
        }

        db.close();
        return intakes;
    }

    /*
    Funktion gibt eine Float-Wert zurück, welcher alle Einnahmen der Datenbank
    berücksichtigt, welche vom 1.month.year bis day.month.year getätigt wurden.
    Periodische Ausgaben sind hierbei berücksichtigt.
    */
    public float getValueIntakesMonth(int day, int month, int year) {
        float value = 0; //Rückgabewert

        ArrayList<Intake> intakes = getMonthIntakes(day, month, year); // Alle relevanten Einträge

        //Summe aller Werte bestimmen
        for(int i = 0; i < intakes.size(); i++){
            value = value + (float) (intakes.get(i).getValue());
        }
        return value;
    }




    //////////////////////////////////////////////////////////////////////////////////////////
    // Tabelle outgo: id, name, calue, day, month, year, cycle, category
    //////////////////////////////////////////////////////////////////////////////////////////

    /*
     Funktion dient dazu, eine Ausgabe in der Tabelle TABLE_OUTGO anzulegen
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
        db.close(); // Datenbank schließen
    }


    /*
    Funktion gibt eine ArrayList zurück, welche alle Ausgaben der Datenbank beinhaltet.
    Sortiert nach dem Datum. Aufsteigend
    */
    public ArrayList<Outgo> getAllOutgoes(){
        ArrayList<Outgo> outgoes = new ArrayList<Outgo>(); //Rückgabewert

        String query = "SELECT * FROM "+TABLE_OUTGO+" ORDER BY "+KEY_YEAR+", "+KEY_MONTH+", "+KEY_DAY;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor != null){
            Outgo outgo = null;
            if(cursor.moveToFirst()){
                do{
                    outgo = new Outgo();
                    outgo.setId_PK(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
                    outgo.setName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)));
                    outgo.setValue(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_VALUE)));
                    outgo.setDay(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_DAY)));
                    outgo.setMonth(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_MONTH)));
                    outgo.setYear(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_YEAR)));
                    outgo.setCycle(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CYCLE)));
                    outgo.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CATEGORY)));
                    outgoes.add(outgo);
                }while(cursor.moveToNext());
            }
        }
        db.close(); //Datenbank schließen

        return outgoes;
    }


    /*
    Die Funktion liefert die Ausgabe zurück, welche die übergebene ID besitzt.
    Sollte diese ID nicht existieren, wird eine Ausgabe mit default-Werten fuer die jeweiligen
    Attribute zurück gegeben
     */
    public Outgo getOutgoById(int id){
        Outgo outgo = new Outgo(); // Rückgabewert

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+TABLE_OUTGO+" WHERE "+KEY_ID+" = "+id;

        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){ // Ein solcher Eintrag mit der ID existiert
            outgo.setId_PK(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
            outgo.setName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)));
            outgo.setValue(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_VALUE)));
            outgo.setDay(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_DAY)));
            outgo.setMonth(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_MONTH)));
            outgo.setYear(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_YEAR)));
            outgo.setCycle(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CYCLE)));
            outgo.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CATEGORY)));
        }
        db.close(); // Datenbank schließen

        return outgo;
    }

    /*
    Funktion löscht die Ausgabe, welche die übergebene ID besitzt.
    Sollte ein solcher Eintrag nicht existieren wird die Datenbank ohne
    einen weiteren Vorgang geschlossen.
     */
    public void deleteOutgoById(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+TABLE_OUTGO+" WHERE "+KEY_ID+" = "+id;
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()){ //Ein solcher Eintrag exisitert
            String idOutgo = cursor.getString(0);
            db.delete(TABLE_OUTGO, KEY_ID+" = ?", new String[]{idOutgo}); //Eintrag löschen
        }

        db.close(); //Datenbank schließen
    }


    /*
    Funktion dient dazu, die Zeile der Datenbank mit der übergebenen ID mit den Informationen der
    übergebenen Ausgabe zu überschreiben. Sollte eine solche ID nicht existieren, wird
    ein neuer Eintrag mit den gewünschten Daten angelegt.
     */
    public void updateOutgo(Outgo outgo, int id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put(KEY_NAME, outgo.getName());
        value.put(KEY_VALUE, outgo.getValue());
        value.put(KEY_DAY, outgo.getDay());
        value.put(KEY_MONTH, outgo.getMonth());
        value.put(KEY_YEAR, outgo.getYear());
        value.put(KEY_CYCLE, outgo.getCycle());
        value.put(KEY_CATEGORY, outgo.getCategory());
        //In die Datenbank schreiben
        db.update(TABLE_OUTGO, value, KEY_ID+" = ?", new String[] { String.valueOf(id) });
        db.close(); //Datenbank schließen
    }


    /*
   Funktion gibt eine ArrayList zurück, welche alle Ausgaben der Datenbank
   beinhaltet, welche vom 1.month.year bis day.month.year getätigt wurden.
   Periodische Ausgaben sind dabei berücksichtigt.
   Sortiert nach dem Datum - aufsteigend
    */
    public ArrayList<Outgo> getMonthOutgoes(int day, int month, int year) {
        ArrayList<Outgo> outgos = new ArrayList<Outgo>(); //Rückgabewert

        // Einträge der vergangenen Monate mit dem Zyklus monatlich
        String condition1 = "(" + KEY_CYCLE + " = \"monatlich\" AND "+KEY_YEAR + " <= \"" + String.valueOf(year)+"\" AND "+KEY_MONTH+"< \""+ String.valueOf(month) +"\" )";
        // Einträge des aktuellen Monats vom 1.month.year bis day.month.year
        String condition2 = "("+ KEY_YEAR + " = \"" + String.valueOf(year) + "\" AND " + KEY_MONTH + " = \"" + String.valueOf(month) +"\" AND "+KEY_DAY+" <= \""+String.valueOf(day) +"\")";
        String query = "SELECT * FROM " + TABLE_OUTGO + " WHERE "+condition1+" OR " +condition2+" ORDER BY "+KEY_YEAR+", "+KEY_MONTH+", "+KEY_DAY;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {
            Outgo outgo = null;
            if (cursor.moveToFirst()) {
                do {
                    outgo = new Outgo();

                    outgo.setId_PK(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
                    outgo.setName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)));
                    outgo.setValue(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_VALUE)));
                    outgo.setDay(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_DAY)));
                    outgo.setMonth(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_MONTH)));
                    outgo.setYear(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_YEAR)));
                    outgo.setCycle(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CYCLE)));
                    outgo.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CATEGORY)));

                    outgos.add(outgo);
                }
                while (cursor.moveToNext()) ;
            }
        }
        db.close(); //Datenbank schließen

        return outgos;
    }


    /*
   Funktion gibt eine Float-Wert zurück, welche alle Ausgaben der Datenbank
   berücksichtigt, die vom 1.month.year bis day.month.year getätigt wurden.
   Periodische Ausgaben sind dabei berücksichtigt.
    */
    public float getValueOutgoesMonth(int day, int month, int year) {
        float value = 0; //Rückgabewert

        ArrayList<Outgo> outgos = getMonthOutgoes(day, month, year); // Alle relevanten Einträge

        //Summe aller Werte bestimmen
        for(int i = 0; i < outgos.size(); i++){
            value = value + (float) (outgos.get(i).getValue());
        }
        return value;
    }

    /*
    Funktion gibt einen Float-Wert zurück, welcher alle Ausgabe der Datenbank
    berücksichtigt, welche in einem bestimmtem Monat und in einer bestimmten Kategorie getätigt wurden.
    Periodische Ausgaben sind babei berücksichtigt
    Sortiert nach dem Datum - aufsteigend
     */
    public  float getCategoryOutgoesMonth(int day, int month, int year, String category) {
        float value = 0;

        // Einträge der vergangenen Monate mit dem zyklus monatlich
        String condition1 = "(" + KEY_CYCLE + " = \"monatlich\" AND "+KEY_YEAR + " <= \"" + String.valueOf(year)+"\" AND "+KEY_MONTH+"< \""+ String.valueOf(month) +"\" AND "+KEY_CATEGORY+"= \""+ String.valueOf(category)+"\")";
        // Einträge des ausgewählten Monats bis day 31
        String condition2 = "("+ KEY_YEAR + " = \"" + String.valueOf(year) + "\" AND " + KEY_MONTH + " = \"" + String.valueOf(month) +"\" AND "+KEY_DAY+" <= \""+String.valueOf(31) + "\" AND "+KEY_CATEGORY+"= \""+ String.valueOf(category)+"\")";
        String query = "SELECT SUM("+KEY_VALUE+") FROM " + TABLE_OUTGO + " WHERE "+condition1+" OR " +condition2;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            value = (float) cursor.getDouble(0);
        }

        db.close();

        return value;
    }


    /*
    Funktion gibt die ID mit der Ausgabe zurück, welche den übergebenen Namen hat.
    Gibt es diesen Eintrag nicht, wird -1 zurückgegeben.
     */
    public int getOutgoIdByName(String name){
        int result = -1; //Rückgabewert mit Default Wert
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+TABLE_OUTGO+" WHERE "+KEY_NAME+" = \""+name+"\"";

        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){ //Ein solcher Eintrag exisitert
            result = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID));
        }
        db.close(); //Datenabnk schließen
        return result;
    }

    /*
    Methode zum Setzen der Kategorie eines Outgoes
    */
    public void setOutgoCategory(int id, String category){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put(KEY_CATEGORY, category);
        db.update(TABLE_OUTGO, value, KEY_ID+" = ?", new String[] { String.valueOf(id) });
        db.close();
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    // Tabelle category: id, name, color, border
    //////////////////////////////////////////////////////////////////////////////////////////

    /*
    Hinzufügen einer Kategorie Category
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

    /*
    Löschen einer Kategorie mit übergebenen ID
     */

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

    /*
    Löschen einer Kategorie mit übergebenen Namen
     */
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
    Auslesen aller angelegten Kategorien
     */
    public ArrayList<Category> getAllCategories(){
        ArrayList<Category> categories = new ArrayList<Category>();

        String query = "SELECT * FROM "+TABLE_CATEGORY;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor != null){
            Category category = null;
            if(cursor.moveToFirst()){
                do{
                    category = new Category();

                    category.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
                    category.setName_PK(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)));
                    category.setColor(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_COLOR)));
                    category.setBorder(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_BORDER)));

                    categories.add(category);
                }while(cursor.moveToNext());
            }

        }
        db.close();
        return categories;
    }

    /*
    Erhalte das Object Category mit dem übergebenen Namen
    */

    public Category getCategory(String name){
        Category category = new Category();

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+TABLE_CATEGORY+" WHERE "+KEY_NAME+" = \""+name+"\"";

        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){

            category.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
            category.setName_PK(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)));
            category.setColor(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_COLOR)));
            category.setBorder(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_BORDER)));
        }
        db.close();
        return category;
    }

    /*
    Aktualisieren einer übergebenen Kategorie
     */

    public int updateCategory(Category category){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put(KEY_NAME, category.getName_PK());
        value.put(KEY_COLOR, category.getColor());
        value.put(KEY_BORDER, category.getBorder());
        value.put(KEY_ID, category.getId());
        int i = db.update(TABLE_CATEGORY, value, KEY_ID+" = ?", new String[] { String.valueOf(category.getId()) });
        db.close();
        return i;
    }


    /*
    Sucht alle mit bestimmter Kategorie assoziierten Ausgaben und ändert dessen Kategorie zu Sonstiges
    */

    public void changeCategoryToSonstiges(String categoryName){
        SQLiteDatabase db = this.getWritableDatabase();
        int ID;
        //Filterung nach gesuchter Kategorie
        String query = "SELECT * FROM "+TABLE_OUTGO+" WHERE "+KEY_CATEGORY+" = \""+ categoryName +"\"";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst())
        {
            do{
                ID = Integer.parseInt(cursor.getString(0));
                setOutgoCategory(ID, "Sonstiges");
            }
            while (cursor.moveToNext());
        }
        db.close();
    }
    //////////////////////////////////////////////////////////////////////////////////////////
    // To-do-listen: Task, Status, Type
    //////////////////////////////////////////////////////////////////////////////////////////

    /*
    Einfügen einer Task in die To-do Tabelle
    */

    public void insertTask(TaskModel task){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put(KEY_TASK, task.getTask());
        value.put(KEY_STATUS, 0);
        value.put(KEY_TYPE, task.getType());
        db.insert(TABLE_TODO, null, value);
        db.close();
    }

    /*
    Ausgabe aller verfügbaren Tasks
    */

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
                    task.setTask(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TASK)));
                    task.setStatus(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_STATUS)));
                    task.setType(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TYPE)));
                    taskList.add(task);
                }
                while(cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        return taskList;
    }

    /*
    Aktualisierung einer Task
    */

    public void updateTask(int id, String task){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_TASK, task);
        db.update(TABLE_TODO, cv, KEY_ID + "= ?", new String[] {String.valueOf(id)});
        db.close();

    }


    /*
    Überschreibung des Status einer Task über ID
    */

    public void updateTaskStatus(int id, int status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_STATUS, status);
        db.update(TABLE_TODO, cv, KEY_ID + "= ?", new String[] {String.valueOf(id)});
        db.close();
    }

    /*
    Löschen einer Task über ID
    */

    public void deleteTask(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TODO, KEY_ID + "= ?", new String[] {String.valueOf(id)});
        db.close();

    }

    /*
    Filterung aller Task nach Type
    */

    public List<TaskModel> getTaskByType(String type){
        List<TaskModel> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+TABLE_TODO+" WHERE "+KEY_TYPE+" = \""+type+"\"";
        Cursor cursor = db.rawQuery(query, null);
        if(cursor != null){
            TaskModel task = null;
            if(cursor.moveToFirst()){
                do{
                    task = new TaskModel();
                    task.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
                    task.setTask(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TASK)));
                    task.setStatus(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_STATUS)));
                    taskList.add(task);
                }
                while(cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        return taskList;
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    // Tabelle limitState: id, name, value, state
    //////////////////////////////////////////////////////////////////////////////////////////

    /*
    Methode zur Prüfung, ob ein gesetztes Budgetlimit einer Kategorie (wenn angelegt) erreicht wurde
    */
    public boolean isCatBudgetLimitReached(int month, int year, String category, double categoryLimit) {
        Double monthCatValue = 0.0;
        boolean isLimitReached = false;

        Cursor curCatOutgo = getWritableDatabase().rawQuery("SELECT SUM("+KEY_VALUE+") FROM " + TABLE_OUTGO + " WHERE "+ KEY_MONTH + " = \"" + month +"\" AND "+KEY_YEAR+"= \""+ year+"\" AND "+KEY_CATEGORY+"= \""+ category+"\"", null);

        if (curCatOutgo.moveToFirst()) {
            monthCatValue =  curCatOutgo.getDouble(0);
        }
        curCatOutgo.close();
        isLimitReached = (monthCatValue >= categoryLimit);

        return isLimitReached;

    }

    /*
    Methode zur Prüfung, ob ein gesetztes prozentuales Gesamtbudget erreicht wurde
    */
    public boolean isPercentBudgetLimitReached(int month, int year, Integer percentOfBudget){
        Double monthOutgoValue = 0.0 ;
        Double monthIntakeValue = 0.0;
        boolean isLimitReached = false;

        //Berechnung der Einnahmen des Monats
        Cursor curMonthIntakes = getWritableDatabase().rawQuery("SELECT SUM("+KEY_VALUE+") FROM "+TABLE_INTAKE+" WHERE "+KEY_MONTH+" = \""+month+"\" AND "+KEY_YEAR+"= \""+ year+"\"", null);
        if (curMonthIntakes.moveToFirst()) {
            monthIntakeValue =  curMonthIntakes.getDouble(0);
        }
        curMonthIntakes.close();

        //Berechnung der Ausgaben des Monats
        Cursor curMonthOutgoes = getWritableDatabase().rawQuery("SELECT SUM("+KEY_VALUE+") FROM "+TABLE_OUTGO+" WHERE "+KEY_MONTH+" = \""+month+"\" AND "+KEY_YEAR+"= \""+ year+"\"", null);

        if (curMonthOutgoes.moveToFirst()) {
            monthOutgoValue =  curMonthOutgoes.getDouble(0);
        }
        curMonthOutgoes.close();

        //Vergleich der Einnahmen und Ausgaben
        isLimitReached = (monthOutgoValue>=((percentOfBudget*monthIntakeValue)/100));

        return isLimitReached;
    }

    /*
    Hinzufügen eines Limits
    */
    public void addLimitState(String name, String state){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put(KEY_NAME, name);
        value.put(KEY_STATE, state);
        value.put(KEY_VALUE, 0);
        db.insert(TABLE_LIMIT_STATE, null, value);
        db.close();
    }

    /*
    Aktualisieren eines Limits
    */
    public int updateLimitState(String name, String state){
        int id = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put(KEY_NAME, name);
        value.put(KEY_STATE, state);

        //Ermittlung der ID
        String query = "SELECT * FROM "+ TABLE_LIMIT_STATE +" WHERE "+KEY_NAME+" = \""+name+"\"";
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID));
        }

        int i = db.update(TABLE_LIMIT_STATE, value, KEY_ID+" = ?", new String[] { String.valueOf(id) });
        db.close();
        return i;
    }

    /*
    Limit ersetzen. Primär für Gesamtlimit
    */
    public int updateStateLimit(String name, double valueLimit, String state){
        int id = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put(KEY_NAME, name);
        value.put(KEY_VALUE, valueLimit);
        value.put(KEY_STATE, state);

        //Ermittlung der ID
        String query = "SELECT * FROM "+ TABLE_LIMIT_STATE +" WHERE "+KEY_NAME+" = \""+name+"\"";
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID));
        }

        int i = db.update(TABLE_LIMIT_STATE, value, KEY_ID+" = ?", new String[] { String.valueOf(id) });
        db.close();
        return i;
    }

    /*
    Ermittlung des Status des gesetzten Limits
    */
    public String getStateLimitState(String name){
        String state = "";

        String query = "SELECT * FROM "+ TABLE_LIMIT_STATE +" WHERE "+KEY_NAME+" = \""+name+"\"";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor != null) {
            if (cursor.moveToFirst()) {
                state = cursor.getString(cursor.getColumnIndexOrThrow(KEY_STATE));
            }
        }

        db.close();
        return state;
    }

    /*
   Ermittlung des Wert des gesetzten Limits
    */
    public double getStateLimitValue(String name){
        double value = 0.0;


        String query = "SELECT * FROM "+ TABLE_LIMIT_STATE +" WHERE "+KEY_NAME+" = \""+name+"\"";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor != null) {
            if (cursor.moveToFirst()) {
                value = cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_VALUE));
            }
        }

        db.close();
        return value;
    }

}