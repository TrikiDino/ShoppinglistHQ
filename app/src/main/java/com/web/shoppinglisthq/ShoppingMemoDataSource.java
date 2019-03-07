package com.web.shoppinglisthq;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ShoppingMemoDataSource {

    private static final String TAG = ShoppingMemoDataSource.class.getSimpleName();

    // Datenbank
    private SQLiteDatabase database;
    private ShoppingMemoDBHelper dbHelper;

    public ShoppingMemoDataSource(Context context) {
        Log.d(TAG, "##Unsere Datasource erzeugt jetzt den dbHelper");
        dbHelper = new ShoppingMemoDBHelper(context);
    }

    public void open() {
        Log.d(TAG, "##Eine Referenz auf die Datenbank wird jetzt angefragt.");
        database = dbHelper.getWritableDatabase();
        Log.d(TAG, "##Datenbankreferenz erhalten. Pfad zur Datenbank: " + database.getPath());
    }

    public void close() {
        dbHelper.close();
        Log.d(TAG, "##Datenbank mit Hilfe des DbHelpers geschlossen.");
    }

    // Tabelle Benutzer
    private String[] col_benutzer = {
            TblBenutzer.COLUMN._ID.bez(),
            TblBenutzer.COLUMN.NAME.bez(),
            TblBenutzer.COLUMN.VORNAME.bez(),
            TblBenutzer.COLUMN.STRASSE.bez(),
            TblBenutzer.COLUMN.PLZ.bez(),
            TblBenutzer.COLUMN.ORT.bez(),
            TblBenutzer.COLUMN.LATITUDE.bez(),
            TblBenutzer.COLUMN.LONGITUDE.bez()
    };
// ToDo Tabelle Benutzer erzeugen
// ToDo Benutzer einfügen
// ToDo Benutzer ändern
// ToDo Benutzer laden

    // Tabelle Einkaufsliste
    private String[] col_einkauf = {
            TblShoppingMemo.COLUMN._ID.bez(),
            TblShoppingMemo.COLUMN.PRODUCT.bez(),
            TblShoppingMemo.COLUMN.EINH.bez(),
            TblShoppingMemo.COLUMN.QUANTITY.bez(),
            TblShoppingMemo.COLUMN.CHECKED.bez(),
            TblShoppingMemo.COLUMN.PREIS.bez(),
            TblShoppingMemo.COLUMN.WARENGRUPPE.bez(),
            TblShoppingMemo.COLUMN.VONWO.bez()
    };
    // Tabelle Einkaufsliste erstellen
    public TblShoppingMemo createShoppingMemo(String product, int quantity,
                                              String einh, boolean checked, double preis,
                                              String wg, String vonwo) {
        ContentValues values = new ContentValues();
        values.put(TblShoppingMemo.COLUMN.PRODUCT.bez(), product);
        values.put(TblShoppingMemo.COLUMN.QUANTITY.bez(), quantity);
        values.put(TblShoppingMemo.COLUMN.EINH.bez(), einh);
        values.put(TblShoppingMemo.COLUMN.PREIS.bez(), preis);
        values.put(TblShoppingMemo.COLUMN.WARENGRUPPE.bez(), wg);
        values.put(TblShoppingMemo.COLUMN.VONWO.bez(), vonwo);

        Log.d(TAG, "##createShoppingMemo 72: "+values.toString());

        long insertId = database.insert(TblShoppingMemo.TABLE_NAME, null, values);

        Cursor cursor = database.query(TblShoppingMemo.TABLE_NAME,
                col_einkauf, TblShoppingMemo.COLUMN._ID + "=" + insertId,
                null, null, null, null);

        cursor.moveToFirst();
        TblShoppingMemo shoppingMemo = cursorToShoppingMemo(cursor);
        cursor.close();

        return shoppingMemo;
    }
    // Cursor für Einkaufsliste
    private TblShoppingMemo cursorToShoppingMemo(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(TblShoppingMemo.COLUMN._ID.bez());
        int idProduct = cursor.getColumnIndex(TblShoppingMemo.COLUMN.PRODUCT.bez());
        int idQuantity = cursor.getColumnIndex(TblShoppingMemo.COLUMN.QUANTITY.bez());
        int idEinh = cursor.getColumnIndex(TblShoppingMemo.COLUMN.EINH.bez());
        int idChecked = cursor.getColumnIndex(TblShoppingMemo.COLUMN.CHECKED.bez());
        int idPreis = cursor.getColumnIndex(TblShoppingMemo.COLUMN.PREIS.bez());
        int idWg = cursor.getColumnIndex(TblShoppingMemo.COLUMN.WARENGRUPPE.bez());
        int idVonWo = cursor.getColumnIndex(TblShoppingMemo.COLUMN.VONWO.bez());

        String product = cursor.getString(idProduct);
        int quantity = cursor.getInt(idQuantity);
        String einh = cursor.getString(idEinh);
        long id = cursor.getLong(idIndex);
        boolean checked = cursor.getInt(idChecked) == 0 ? false : true;
        double preis = cursor.getDouble(idPreis);
        String wg = cursor.getString(idWg);
        String vonwo = cursor.getString(idVonWo);

        TblShoppingMemo shoppingMemo = new TblShoppingMemo(product, quantity, einh, id, checked, preis, wg,vonwo);

        return shoppingMemo;
    }
    // Eintrag in Einkaufsliste einfügen
// Eintrag in Einkaufsliste ändern
// Eintrag aus Einkaufsliste laden
// alle Einträge aus Einkaufsliste laden
    public List<TblShoppingMemo> getAllShoppingMemos() {
        List<TblShoppingMemo> shoppingMemoList = new ArrayList<>();

        Cursor cursor = database.rawQuery("SELECT * FROM shopping_list ORDER BY vonWo",null);

        cursor.moveToFirst();
        TblShoppingMemo shoppingMemo;
        String wechselWg = "";

        while (!cursor.isAfterLast()) {
            shoppingMemo = cursorToShoppingMemo(cursor);
            Log.d(TAG, "##getAllShoppingMemos: Cursor " + cursor.toString());
            Log.d(TAG, "##getAllShoppingMemos: shoppingMemo " + shoppingMemo.toString());
            String fldWg = shoppingMemo.getVonWo() == null ? "" : shoppingMemo.getVonWo();
            Log.d(TAG, "##getAllShoppingMemos: " + wechselWg + " / " + fldWg);
            if (!wechselWg.equals(fldWg)) {
                shoppingMemoList.add(new TblShoppingMemo(fldWg.toUpperCase(),
                        0,0,false));
                wechselWg = fldWg;
                Log.d(TAG, "##getAllShoppingMemos: wechselWG:" + wechselWg + " Warengruppe:" + fldWg);
            }
            shoppingMemoList.add(shoppingMemo);
            Log.d(TAG, "##ID: " + shoppingMemo.getId() + ", Inhalt: " + shoppingMemo.toString());
            cursor.moveToNext();
        }

        cursor.close();

        return shoppingMemoList;
    }

// Eintrag aus Einkaufsliste löschen

    // Tabelle Artikel
    private String[] col_produkt = {
            TblProdukt.COLUMN._ID.bez(),
            TblProdukt.COLUMN.NAME.bez(),
            TblProdukt.COLUMN.EINH.bez(),
            TblProdukt.COLUMN.DURCH_PREIS.bez(),
            TblProdukt.COLUMN.WARENGRUPPE.bez(),
            TblProdukt.COLUMN.VONWO.bez()
    };
    // Cursor für Artikel
    private TblProdukt cursorToProdukt(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(TblProdukt.COLUMN._ID.bez());
        int idName = cursor.getColumnIndex(TblProdukt.COLUMN.NAME.bez());
        int idEinh = cursor.getColumnIndex(TblProdukt.COLUMN.EINH.bez());
        int idPreis = cursor.getColumnIndex(TblProdukt.COLUMN.DURCH_PREIS.bez());
        int idWg = cursor.getColumnIndex(TblProdukt.COLUMN.WARENGRUPPE.bez());
        int idVonWo = cursor.getColumnIndex(TblProdukt.COLUMN.VONWO.bez());
        Log.d(TAG, "##cursorToProdukt: Index:" + idIndex + ", Name:" + idName + ", Einheit:"
                + idEinh + ", Warengruppe:" + idWg + ", Geschäft:" + idVonWo);

        String product = cursor.getString(idName);
        String einh = cursor.getString(idEinh);
        long id = cursor.getLong(idIndex);
        double preis = cursor.getDouble(idPreis);
        String wg = cursor.getString(idWg);
        String vonwo = cursor.getString(idVonWo);

        TblProdukt produkt = new TblProdukt(product, einh, preis, vonwo, wg);

        return produkt;
    }

// Tabelle Artikel erzeugen
// Artikel in Tabelle einfügen
// Artikel ändern
// Artikel laden
// alle Artikel laden
// Artikel aus Tabelle löschen

    // Tabelle Geschäfte
    private String[] col_vonwo = {
            TblVonWo.COLUMN._ID.bez(),
            TblVonWo.COLUMN.NAME.bez(),
            TblVonWo.COLUMN.STRASSE.bez(),
            TblVonWo.COLUMN.PLZ.bez(),
            TblVonWo.COLUMN.ORT.bez(),
            TblVonWo.COLUMN.WWW.bez(),
            TblVonWo.COLUMN.LATITUDE.bez(),
            TblVonWo.COLUMN.LONGITUDE.bez(),
            TblVonWo.COLUMN.FAVORIT.bez()
    };
// ToDo Tabelle Geschäfte erzeugen
// ToDo Standardwerte in Tabelle einfügen
// ToDo Geschäft in Tabelle einfügen
// ToDo Geschäft ändern
// ToDo Geschäft laden
// ToDo alle Geschäft laden
// ToDo Geschäft aus Tabelle löschen

    // Tabelle Warengruppe
    private String[] col_wg = {
            TblWarengruppe.COLUMN._ID.bez(),
            TblWarengruppe.COLUMN.NAME.bez()
    };
// ToDo Tabelle Warengruppe erzeugen
// ToDo Standardwerte in Tabelle einfügen
// ToDo Warengruppe in Tabelle einfügen
// ToDo Warengruppe ändern
// ToDo Warengruppe laden
// ToDo alle Warengruppe laden
// ToDo Warengruppe aus Tabelle löschen

}
