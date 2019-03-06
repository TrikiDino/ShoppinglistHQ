package com.web.shoppinglisthq;

public class TblWarengruppe {
    private int ID;
    private String warenGruppe;

    public TblWarengruppe(String warenGruppe) {
        this.warenGruppe = warenGruppe;
    }

    public int getID() {
        return ID;
    }

    public String getWarenGruppe() {
        return warenGruppe;
    }

    public void setWarenGruppe(String warenGruppe) {
        this.warenGruppe = warenGruppe;
    }

    // Tabellenname
    public static final String TABLE_WG = "warengruppe";

    // Spaltennamen
    enum COLUMN {
        _ID("_id"),
        NAME("warenGruppe");

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
            "CREATE TABLE " + TABLE_WG +
                    "(" + COLUMN._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN.NAME + " TEXT NOT NULL);";

    // Tabelle löschen
    public static final String SQL_DROP = "DROP TABLE IF EXISTS " + TABLE_WG;

    // Tabelle löschen
    public static final String SQL_DEL = "DELETE FROM " + TABLE_WG;

}
