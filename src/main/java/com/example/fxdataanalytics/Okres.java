package com.example.fxdataanalytics;

public class Okres {
    final private int id;
    final private int krajId;
    final private String nazev;
    final private int[] hodnoty; // Pole pro čísla (např. populace v různých letech)

    public Okres(int id, int krajId, String nazev, int... hodnoty) {
        this.id = id;
        this.krajId = krajId;
        this.nazev = nazev;
        this.hodnoty = hodnoty;
    }
    public int getId(){
        return id;
    }
    public int getKrajId(){
        return krajId;
    }
    public String getNazev(){
        return nazev;
    }
    public int[] getHodnoty(){
        return hodnoty;
    }
}
