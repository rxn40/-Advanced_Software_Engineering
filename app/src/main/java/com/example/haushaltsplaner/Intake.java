package com.example.haushaltsplaner;


import java.io.Serializable;

/*
Repräsentation einer Einnahme
id, name, value, day, month, ywar, cyclus
 */
public class Intake implements Serializable {
    private int id;
    private String name;
    private double value;
    private int day;
    private int month;
    private int year;
    private String cycle;

    public Intake(){}

    public Intake(String name, double value, int day, int month, int year, String cycle){
        super();
        this.name = name;
        this.value = value;
        this.day = day;
        this.month = month;
        this.year = year;
        this.cycle = cycle;
    }

    public String toString(){
        return "'\n' "+" id:"+id+"Einnahme "+name+ " ,Wert = "+value +" datum:"+day+"."+month+"."+year;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
