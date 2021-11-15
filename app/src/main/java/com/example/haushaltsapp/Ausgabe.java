package com.example.haushaltsapp;

public class Ausgabe {
    private int id;
    private String name;
    private String date;
    private String cycle;
    private String kategorie;

    public Ausgabe(){}

    public Ausgabe(String name, String date, String cycle, String kategorie){
        super();
        this.name = name;
        this.date = date;
        this.cycle = cycle;
        this.kategorie = kategorie;
    }
    public String toString(){
        return "'\n' Ausgabe "+name+ " ,Datum = "+date + " ,Zyklus = "+cycle+" Kategorie "+kategorie;
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

    public String getKategorie() {
        return kategorie;
    }

    public void setKategorie(String kategorie) {
        this.kategorie = kategorie;
    }
}
