package com.example.haushaltsappversion2;

public class Category {
    private int id;
    private String name;
    private int border;
    private String color1;
    private String color2;

    public Category(){ }

    public Category(String name, int border, String color1, String color2){
        super();
        this.name = name;
        this.border = border;
        this.color1 = color1;
        this.color2 = color2;
    }

    public String toString(){
        return "'\n' Kategorie "+name+ " ,Limit = "+border + " ,Farben = "+color1 +" und "+color2+" id:"+id;
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

    public int getBorder() {
        return border;
    }

    public void setBorder(int border) {
        this.border = border;
    }

    public String getColor1() {
        return color1;
    }

    public void setColor1(String color1) {
        this.color1 = color1;
    }

    public String getColor2() {
        return color2;
    }

    public void setColor2(String color2) {
        this.color2 = color2;
    }

}
