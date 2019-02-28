package com.web.shoppinglisthq;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ShoppingMemoDBHelper extends SQLiteOpenHelper {

    private static final String TAG = ShoppingMemoDBHelper.class.getSimpleName();

    public static final String DB_NAME = "Shopping_list.db";
    public static final  int DB_VERSION = 3;

    public static final String TABLE_SHOPPING_LIST = "shopping_list";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PRODUCT = "product";
    public static final String COLUMN_EINH = "einh";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_CHECKED = "checked";
    public static final String COLUMN_PREIS = "preis";
    public static final String COLUMN_WARENGRUPPE = "warenGruppe";
    public static final String COLUMN_VONWO = "vonWo";

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

    public static final String SQL_DROP = "DROP TABLE IF EXISTS " + TABLE_SHOPPING_LIST;

    public ShoppingMemoDBHelper(Context context) {
        //super(context, "DATENBANKNAME", null, 1);
        super(context, DB_NAME, null, DB_VERSION);
        Log.d(TAG, "DbHelper hat die Datenbank: " + getDatabaseName() + "erzeugt.");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Log.d(TAG, "Die Tabelle wird mit dem SQL-Befehl: " + SQL_CREATE + " angelegt.");
            db.execSQL(SQL_CREATE);
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
