package com.web.shoppinglisthq;

public class TblVonWo {

    private int id;
    private String name;
    private String strasse;
    private String plz;
    private String ort;
    private String web;
    private String latitude;
    private String longitude;
    private String distance;
    private int favorite;

    public TblVonWo(String name) {
        this.name = name;
    }

    public TblVonWo(String name, int favorite) {
        this.name = name;
        this.favorite = favorite;
    }

    public TblVonWo(int id, String name, String strasse, String plz, String ort, String web, String latitude, String longitude, String distance, int favorite) {
        this.id = id;
        this.name = name;
        this.strasse = strasse;
        this.plz = plz;
        this.ort = ort;
        this.web = web;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        this.favorite = favorite;
    }

    // Tabellenname
    public static final String TABLE_VONWO = "vonwo";

    // Spaltennamen
    public enum COLUMN {
        _ID("_id"),
        NAME("name"),
        STRASSE("strasse"),
        PLZ("plz"),
        ORT("ort"),
        WWW("web"),
        LATITUDE("latitude"),
        LONGITUDE("longitude"),
        DISTANCE("distance"),
        FAVORIT("favorite");

        private String name;

        COLUMN(String name) {
            this.name = name;
        }

        public String bez() {
            return name;
        }
    }

    // Create Table
    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_VONWO +
                    "(" + COLUMN._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN.NAME + " TEXT NOT NULL, " +
                    COLUMN.STRASSE + " TEXT, " +
                    COLUMN.ORT + " TEXT, " +
                    COLUMN.WWW + " TEXT, " +
                    COLUMN.LATITUDE + " TEXT, " +
                    COLUMN.LONGITUDE + " TEXT, " +
                    COLUMN.DISTANCE + " TEXT, " +
                    COLUMN.FAVORIT + " TEXT);";

    // Tabelle löschen
    public static final String SQL_DROP = "DROP TABLE IF EXISTS " + TABLE_VONWO;

    // Tabelle löschen
    public static final String SQL_DEL = "DELETE FROM " + TABLE_VONWO;

}
