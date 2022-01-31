package com.example.haushaltsapp.Database;

/*
Repräsentation einer Einnahmne
 */
public class Intake extends EntryFinance {

    // Default Konstruktor
    public Intake(){
        super();
    }

    // Konstruktor
    public Intake(String name, double value, int day, int month, int year, String cycle){
        super(name, value, day, month, year, cycle);
    }

    @Override
    public String toString(){
        return "'\n' "+" id:"+id_PK+"Einnahme "+name+ " ,Wert = "+value +" datum:"+day+"."+month+"."+year;
    }


}