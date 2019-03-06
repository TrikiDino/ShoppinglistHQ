package com.web.shoppinglisthq;

public class TblBenutzer {
    private int id;
    private String name;
    private String vorname;
    private String strasse;
    private String plz;
    private String ort;
    private String web;
    private String latitude;
    private String longitude;

    // ToDo Konstruktoren


    public void setName(String name) {
        this.name = name;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public void setStrasse(String strasse) {
        this.strasse = strasse;
    }

    public void setPlz(String plz) {
        this.plz = plz;
    }

    public void setOrt(String ort) {
        this.ort = ort;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getVorname() {
        return vorname;
    }

    public String getStrasse() {
        return strasse;
    }

    public String getPlz() {
        return plz;
    }

    public String getOrt() {
        return ort;
    }

    public String getWeb() {
        return web;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    // Tabellenname
    public static final String TABLE_BENUTZER = "benutzer";

    // Spaltennamen
    enum COLUMN {
        _ID("_id"),
        NAME("name"),
        VORNAME("vorname"),
        STRASSE("strasse"),
        PLZ("plz"),
        ORT("ort"),
        LATITUDE("latitude"),
        LONGITUDE("longitude");

        private String name;

        COLUMN(String name) { this.name = name; }

        public String bez(){ return name; }
        }

    // Create Table
    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_BENUTZER +
                    "(" + COLUMN._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN.NAME + " TEXT," +
                    COLUMN.VORNAME + " TEXT," +
                    COLUMN.STRASSE + " TEXT," +
                    COLUMN.PLZ + " TEXT," +
                    COLUMN.ORT + " TEXT," +
                    COLUMN.LATITUDE + " TEXT," +
                    COLUMN.LONGITUDE + " TEXT);";

    // Tabelle löschen
    public static final String SQL_DROP = "DROP TABLE IF EXISTS " + TABLE_BENUTZER;

    // Tabelle leeren
    public static final String SQL_DEL = "DELETE FROM " + TABLE_BENUTZER;

}
