package com.web.shoppinglisthq;

public class TblProdukt {
    private int ID;
    private String Name;
    private String Einheit;
    private double DurchPreis;
    private String VonWo;
    private String Wg;

    public TblProdukt(String name, String vonWo, String wg) {
        Name = name;
        VonWo = vonWo;
        Wg = wg;
    }

    public TblProdukt(String name) {
        Name = name;
    }

    public TblProdukt(String name, String einheit, double durchPreis, String vonWo, String wg) {
        Name = name;
        Einheit = einheit;
        DurchPreis = durchPreis;
        VonWo = vonWo;
        Wg = wg;
    }

    public String getVonWo() {
        return VonWo;
    }

    public void setVonWo(String vonWo) {
        VonWo = vonWo;
    }

    public String getWg() {
        return Wg;
    }

    public void setWg(String wg) {
        Wg = wg;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEinheit() {
        return Einheit;
    }

    public void setEinheit(String einheit) {
        Einheit = einheit;
    }

    public double getDurchPreis() {
        return DurchPreis;
    }

    public void setDurchPreis(double durchPreis) {
        DurchPreis = durchPreis;
    }

    @Override
    public String toString() {
        return "TblProdukt{" +
                "Name='" + Name + '\'' +
                ", Einheit='" + Einheit + '\'' +
                '}';
    }
}
