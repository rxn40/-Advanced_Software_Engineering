package com.example.haushaltsplaner;

//Name geändert, ursprünglich: class_Ausgabe
public class Expenditures {

    private String name;
    private String value;
    private String date;

    public Expenditures(String expenditureNamue, String expenditureValue, String expenditureDate) {
        this.name = expenditureNamue;
        this.value = expenditureValue;
        this.date = expenditureDate;
    }

    public String getWert() {
        return value;
    }

    public void setWert(String birthday) {
        this.value = birthday;
    }

    public String getAusgabe() {
        return name;
    }

    public void setAusgabe(String name) {
        this.name = name;
    }

    public String getDatum() {
        return date;
    }

    public void setDatum(String sex){
        this.date =sex;
    }
}