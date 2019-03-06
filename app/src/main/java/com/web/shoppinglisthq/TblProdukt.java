package com.web.shoppinglisthq;

public class TblProdukt {
    private int id;
    private String name;
    private String einheit;
    private double durchpreis;
    private String vonwo;
    private String wg;

    public TblProdukt(String name, String vonwo, String wg) {
        this.name = name;
        this.vonwo = vonwo;
        this.wg = wg;
    }

    public TblProdukt(String name) {
        this.name = name;
    }

    public TblProdukt(String name, String einheit, double durchpreis, String vonWo, String wg) {
        this.name = name;
        this.einheit = einheit;
        this.durchpreis = durchpreis;
        this.vonwo = vonwo;
        this.wg = wg;
    }

    public String getVonwo() {
        return vonwo;
    }

    public void setVonwo(String vonwo) {
        this.vonwo = vonwo;
    }

    public String getWg() {
        return wg;
    }

    public void setWg(String wg) {
        this.wg = wg;
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

    public String getEinheit() {
        return einheit;
    }

    public void setEinheit(String einheit) {
        this.einheit = einheit;
    }

    public double getDurchpreis() {
        return durchpreis;
    }

    public void setDurchpreis(double durchpreis) {
        this.durchpreis = durchpreis;
    }

    @Override
    public String toString() {
        return "Artikel=" + name +
                ", Warengruppe=" + wg +
                ", Geschäft=" + vonwo;
    }

    public static final String TABLE_NAME = "produkte";

    enum COLUMN {
        _ID("_id"),
        NAME("name"),
        EINH("einh"),
        DURCH_PREIS("durch_preis"),
        WARENGRUPPE("warenGruppe"),
        VONWO("vonWo");

        private String name;

        COLUMN(String name) { this.name = name; }

        public String bez(){ return name; }
    }

    // Produkte table create
    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_NAME +
                    "(" + COLUMN._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN.NAME + " TEXT NOT NULL, " +
                    COLUMN.EINH + " STRING DEFAULT 'Stck', " +
                    COLUMN.DURCH_PREIS + " DOUBLE DEFAULT 0, " +
                    COLUMN.WARENGRUPPE + " TEXT NOT NULL DEFAULT 'Sonstiges', " +
                    COLUMN.VONWO + " TEXT DEFAULT 'Sonstige');";

    // Produkte table löschen
    public static final String SQL_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;

    // Produkte table leeren
    public static final String SQL_DEL = "DELETE FROM " + TABLE_NAME;

}
