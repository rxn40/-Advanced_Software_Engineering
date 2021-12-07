package com.example.haushaltsapp.database;


import com.example.haushaltsapp.database.EntryFinance;

/*
Repräsentation einer Einnahme
id, name, value, day, month, ywar, cyclus
 */
public class Intake extends EntryFinance {

    public Intake(){
        super();
    }

    public Intake(String name, double value, int day, int month, int year, String cycle){
        super(name, value, day, month, year, cycle);
    }

    public String toString(){
        return "'\n' "+" id:"+id_PK+"Einnahme "+name+ " ,Wert = "+value +" datum:"+day+"."+month+"."+year;
    }


}