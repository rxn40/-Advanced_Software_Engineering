package com.example.haushaltsapp;

public class Einnahme {
    private int id;
    private String name;
    private String date;
    private String cycle;

    public Einnahme(){ }

    public Einnahme(String name, String date, String cycle){
        super();
        this.name = name;
        this.date = date;
        this.cycle = cycle;
    }
    public String toString(){
        return "'\n' Einnahme "+name+ " ,Datum = "+date + " ,Zyklus = "+cycle;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCycle() {
        return cycle;
    }

    public void setCycle(String cycle) {
        this.cycle = cycle;
    }
}
