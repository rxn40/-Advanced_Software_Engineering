package com.example.haushaltsapp;

import java.io.Serializable;

/*
Repräsentation einer Ausgabe
id, name, value, day, month, year, cyclus, category
-- !! wird später noch um eine Kategorie erweitert !!--
 */
public class Outgo extends EntryFinance{
    String category;

    public Outgo(){
        super();
    }

    public Outgo(String name, double value, int day, int month, int year, String cycle, String category){
        super(name, value, day, month, year, cycle);
        this.category = category;
    }

    public String toString(){
        return "'\n' "+" id:"+id_PK+"Ausgabe "+name+ " ,Wert = "+category +" datum:"+day+"."+month+"."+year;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}