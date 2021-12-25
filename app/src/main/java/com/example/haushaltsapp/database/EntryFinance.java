package com.example.haushaltsapp.database;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EntryFinance implements Serializable {
    int id_PK;
    String name;
    double value;
    int day, month, year;
    String cycle;

    public EntryFinance() {
        super();
    }

    public EntryFinance(String name, double value, int day, int month, int year, String cycle) {
        super();
        this.name = name;
        this.value = value;
        this.day = day;
        this.month = month;
        this.year = year;
        this.cycle = cycle;
    }

    //Damit man die Array-Listen später nach dem Datum sortieren kann
    /*
    public int compareTo(EntryFinance entryFinance) {
        String firstString = year + "/" + month + "/" + day;
        String secondString = entryFinance.year + "/" + entryFinance.month + "/" + entryFinance.day;
        try {
            Date firstDate = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH).parse(firstString);
            Date secondDate = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH).parse(secondString);
            return firstDate.compareTo(secondDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0; // default
    }
     */

    public int getId_PK() {
        return id_PK;
    }

    public void setId_PK(int id_PK) {
        this.id_PK = id_PK;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getCycle() {
        return cycle;
    }

    public void setCycle(String cycle) {
        this.cycle = cycle;
    }
}
