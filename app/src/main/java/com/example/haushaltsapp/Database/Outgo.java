package com.example.haushaltsapp.Database;

/*
Repräsentation einer Ausgabe
 */

public class Outgo extends EntryFinance {
    String category;

    // Default Konstruktor
    public Outgo(){
        super();
    }

    // Konstruktor
    public Outgo(String name, double value, int day, int month, int year, String cycle, String category){
        super(name, value, day, month, year, cycle);
        this.category = category;
    }

    @Override
    public String toString(){
        return "'\n' "+" id:"+id_PK+"Ausgabe "+name+ " ,Wert = "+value+", Kategorie ="+category +" datum:"+day+"."+month+"."+year;
    }

    // Getter und Setter

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}