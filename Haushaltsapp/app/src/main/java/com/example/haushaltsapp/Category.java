package com.example.haushaltsapp;

import java.io.Serializable;

public class Category implements Serializable {
    int id;
    String name_PK;
    int color;
    double border;

    public Category(){
        super();
    }

    public Category(String name_PK, int color, double border){
        super();
        this.name_PK = name_PK;
        this.color = color;
        this.border = border;
    }

    public String toString(){
        return name_PK;
       // return "'\n' "+"Kategorie "+name_PK+ " ,Farbe = "+color +" Limit:"+border;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName_PK() {
        return name_PK;
    }

    public void setName_PK(String name_PK) {
        this.name_PK = name_PK;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public double getBorder() {
        return border;
    }

    public void setBorder(double border) {
        this.border = border;
    }
}
