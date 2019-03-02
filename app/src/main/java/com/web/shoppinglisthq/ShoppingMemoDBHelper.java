package com.web.shoppinglisthq;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ShoppingMemoDBHelper extends SQLiteOpenHelper {

    private static final String TAG = ShoppingMemoDBHelper.class.getSimpleName();

    public static final String DB_NAME = "Shopping_list.db";
    public static final  int DB_VERSION = 7;

    // Tabellennamen
    public static final String TABLE_SHOPPING_LIST = "shopping_list";
    public static final String TABLE_PRODUCT = "produkte";


    // Tabelle Einkaufsliste
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PRODUCT = "product";
    public static final String COLUMN_EINH = "einh";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_CHECKED = "checked";
    public static final String COLUMN_PREIS = "preis";
    public static final String COLUMN_WARENGRUPPE = "warenGruppe";
    public static final String COLUMN_VONWO = "vonWo";

    // Tabelle Produkte
    public static final String COLUMN_ID_PRO = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_EINH_PRO = "einh";
    public static final String COLUMN_DURCH_PREIS = "durch_preis";
    public static final String COLUMN_WARENGRUPPE_PRO = "warenGruppe";
    public static final String COLUMN_VONWO_PRO = "vonWo";

    // Einkaufslisten table create
    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_SHOPPING_LIST +
                    "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_PRODUCT + " TEXT NOT NULL, " +
                    COLUMN_QUANTITY + " INTEGER NOT NULL, " +
                    COLUMN_EINH + " STRING NOT NULL DEFAULT 'Stck', " +
                    COLUMN_CHECKED + " BOOLEAN NOT NULL DEFAULT 0, " +
                    COLUMN_PREIS + " DOUBLE NOT NULL DEFAULT 0, " +
                    COLUMN_WARENGRUPPE + " TEXT NOT NULL DEFAULT 'Sonstiges', " +
                    COLUMN_VONWO + " TEXT NOT NULL DEFAULT 'Sonstige');";

    // Produkte table create
    public static final String SQL_CREATE_PRO =
            "CREATE TABLE " + TABLE_PRODUCT +
                    "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_EINH_PRO + " STRING NOT NULL DEFAULT 'Stck', " +
                    COLUMN_DURCH_PREIS + " DOUBLE NOT NULL DEFAULT 0, " +
                    COLUMN_WARENGRUPPE_PRO + " TEXT NOT NULL DEFAULT 'Sonstiges', " +
                    COLUMN_VONWO_PRO + " TEXT NOT NULL DEFAULT 'Sonstige');";

    // Einkaufsliste table löschen
    public static final String SQL_DROP = "DROP TABLE IF EXISTS " + TABLE_SHOPPING_LIST;

    // Produkte table löschen
    public static final String SQL_DROP_PRO = "DROP TABLE IF EXISTS " + TABLE_PRODUCT;

    // Einkaufsliste table leeren
    public static final String SQL_DEL = "DELETE FROM " + TABLE_SHOPPING_LIST;

    // Produkte table leeren
    public static final String SQL_DEL_PRO = "DELETE FROM " + TABLE_PRODUCT;

    // Datenbank erstellen
    public ShoppingMemoDBHelper(Context context) {
        //super(context, "DATENBANKNAME", null, 1);
        super(context, DB_NAME, null, DB_VERSION);
        Log.d(TAG, "DbHelper hat die Datenbank: " + getDatabaseName() + "erzeugt.");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Log.d(TAG, "Die Tabelle Einkaufsliste wird mit dem SQL-Befehl: " + SQL_CREATE + " angelegt.");
            db.execSQL(SQL_CREATE);
        }
        catch (Exception e) {
            Log.e(TAG, "Fehler beim Anlegen der Tabelle: ", e);
        }
        try {
            Log.d(TAG, "Die Tabelle Produkte wird mit dem SQL-Befehl: " + SQL_CREATE + " angelegt.");
            db.execSQL(SQL_CREATE_PRO);
        }
        catch (Exception e) {
            Log.e(TAG, "Fehler beim Anlegen der Tabelle: ", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try{
            db.execSQL(SQL_DROP);
            onCreate(db);
        } catch(RuntimeException e){
            Log.e(TAG, "Fehler beim Upgrade der Datenbank",e);
        }
    }
}
