package com.web.shoppinglisthq;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ShoppingMemoDBHelper extends SQLiteOpenHelper {

    private static final String TAG = ShoppingMemoDBHelper.class.getSimpleName();

    public static final String DB_NAME = "Shopping_list.db";
    public static final  int DB_VERSION = 12;

    // Datenbank erstellen
    public ShoppingMemoDBHelper(Context context) {
        //super(context, "DATENBANKNAME", null, 1);
        super(context, DB_NAME, null, DB_VERSION);
        Log.d(TAG, "##DbHelper hat die Datenbank: " + getDatabaseName() + "erzeugt.");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Log.d(TAG, "##Die Tabelle Benutzereinstellungen wird mit dem SQL-Befehl: " + TblBenutzer.SQL_CREATE + " angelegt.");
            db.execSQL(TblBenutzer.SQL_CREATE);
        }
        catch (Exception e) {
            Log.e(TAG, "##Fehler beim Anlegen der Tabelle: ", e);
        }
        try {
            Log.d(TAG, "##Die Tabelle Einkaufsliste wird mit dem SQL-Befehl: " + TblShoppingMemo.SQL_CREATE + " angelegt.");
            db.execSQL(TblShoppingMemo.SQL_CREATE);
        }
        catch (Exception e) {
            Log.e(TAG, "##Fehler beim Anlegen der Tabelle: ", e);
        }
        try {
            Log.d(TAG, "##Die Tabelle Produkte wird mit dem SQL-Befehl: " + TblProdukt.SQL_CREATE + " angelegt.");
            db.execSQL(TblProdukt.SQL_CREATE);
        }
        catch (Exception e) {
            Log.e(TAG, "##Fehler beim Anlegen der Tabelle: ", e);
        }
        try {
            Log.d(TAG, "##Die Tabelle Warengruppe wird mit dem SQL-Befehl: " + TblWarengruppe.SQL_CREATE + " angelegt.");
            db.execSQL(TblWarengruppe.SQL_CREATE);
            Log.d(TAG, "## und mit den Standardwerten gefüllt");

        }
        catch (Exception e) {
            Log.e(TAG, "##Fehler beim Anlegen der Tabelle: ", e);
        }
        try {
            Log.d(TAG, "##Die Tabelle Geschäfte wird mit dem SQL-Befehl: " + TblVonWo.SQL_CREATE + " angelegt.");
            db.execSQL(TblVonWo.SQL_CREATE);
        }
        catch (Exception e) {
            Log.e(TAG, "##Fehler beim Anlegen der Tabelle: ", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try{
            db.execSQL(TblBenutzer.SQL_DROP);
            db.execSQL(TblShoppingMemo.SQL_DROP);
            db.execSQL(TblProdukt.SQL_DROP);
            db.execSQL(TblWarengruppe.SQL_DROP);
            db.execSQL(TblVonWo.SQL_DROP);
            onCreate(db);
        } catch(RuntimeException e){
            Log.e(TAG, "Fehler beim Upgrade der Datenbank",e);
        }
    }
}
