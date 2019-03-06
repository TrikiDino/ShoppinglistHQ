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

    private SQLiteDatabase database;
    private ShoppingMemoDBHelper dbHelper;

    // Tabelle Benutzer

    // Tabelle Einkaufsliste
    private String[] columns = {
            TblShoppingMemo.COLUMN._ID.bez(),
            TblShoppingMemo.COLUMN.PRODUCT.bez(),
            TblShoppingMemo.COLUMN.EINH.bez(),
            TblShoppingMemo.COLUMN.QUANTITY.bez(),
            TblShoppingMemo.COLUMN.CHECKED.bez(),
            TblShoppingMemo.COLUMN.PREIS.bez(),
            TblShoppingMemo.COLUMN.WARENGRUPPE.bez(),
            TblShoppingMemo.COLUMN.VONWO.bez()
    };
    // Tabelle Produkte
    private String[] columns_pro = {
            TblProdukt.COLUMN._ID.bez(),             //0
            TblProdukt.COLUMN.NAME.bez(),               //1
            TblProdukt.COLUMN.EINH.bez(),           //2
            TblProdukt.COLUMN.DURCH_PREIS.bez(),        //3
            TblProdukt.COLUMN.WARENGRUPPE.bez(),    //4
            TblProdukt.COLUMN.VONWO.bez()           //5
    };
    // Tabelle Geschäft

    // Tabelle Warengruppe

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
                columns, TblShoppingMemo.COLUMN._ID + "=" + insertId,
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

    // Cursor für Produkte
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

    // Cursor für nur Artikelbezeichnung
    private TblProdukt cursorToArtikel(Cursor cursor) {
        int idName = cursor.getColumnIndex(TblProdukt.COLUMN.NAME.bez());
        String product = cursor.getString(idName);
        Log.d(TAG, "##cursorToArtikel: " + product);

        TblProdukt produkt = new TblProdukt(product);

        return produkt;
    }

    // Lese gesamte Einkaufsliste
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

    // Lese sortierte und/oder gefilterte Einkaufsliste
    public List<TblShoppingMemo> getAllShoppingMemos(String[][] params) {
        List<TblShoppingMemo> shoppingMemoList = new ArrayList<>();
        String filterBy = null;
        String orderBy = null;

        for (int i = 0; i < params.length; i++) {
            switch (params[i][0]) {
                case "S":
                    if (orderBy.length() > 0) orderBy += ", ";
                    orderBy += params[i][1];
                    break;
                case "F":
                    if (filterBy.length() > 0) {
                        if (params[i][1].contains("|")) params[i][1].replace("|", "OR ");
                        if (params[i][1].contains("&")) params[i][1].replace("&", " AND ");
                    } else {
                        if (params[i][1].contains("|")) params[i][1].replace("|", "");
                        if (params[i][1].contains("&")) params[i][1].replace("&", "");
                    }
                    filterBy += params[i][1];
                    break;
                default:
                    Log.d(TAG, params[i][0] + " ist kein gültiger Parameter.");
            }
        }
        Log.d(TAG, "##getAllShoppingMemos: " + orderBy + ", " + filterBy);

        Cursor cursor = database.query(TblShoppingMemo.TABLE_NAME,
                columns, filterBy, null, null, null, orderBy);

        cursor.moveToFirst();
        TblShoppingMemo shoppingMemo;
        String wechselWg = "";

        while (!cursor.isAfterLast()) {
            shoppingMemo = cursorToShoppingMemo(cursor);
            if (wechselWg != shoppingMemo.getWarenGruppe()) {
                shoppingMemoList.add(new TblShoppingMemo(shoppingMemo.getWarenGruppe().toUpperCase(),
                        0, 0, false));
                wechselWg = shoppingMemo.getWarenGruppe();
            }
            shoppingMemoList.add(shoppingMemo);
            Log.d(TAG, "##ID: " + shoppingMemo.getId() + ", Inhalt: " + shoppingMemo.toString());
            cursor.moveToNext();
        }

        cursor.close();

        return shoppingMemoList;
    }

    // ToDo alle Artikel laden für AutoCompleteTextView
    public List<TblProdukt> getAlleArtikel() {
        List<TblProdukt> produktList = new ArrayList<>();

//        Cursor cursor = database.rawQuery("SELECT name FROM produkte ORDER BY name", null);
//
//        if (cursor.getCount() > 0) {
//            cursor.moveToFirst();
//            TblProdukt produkt;
//
//            while (!cursor.isAfterLast()) {
//                produkt = cursorToArtikel(cursor);
//                produktList.add(produkt);
//                Log.d(TAG, "##getAlleArtikel: " + produkt.toString());
//                cursor.moveToNext();
//            }
//        } else {
            produktList.add(new TblProdukt("Banane"));
            produktList.add(new TblProdukt("Milch"));
            produktList.add(new TblProdukt( "Brot"));
            produktList.add(new TblProdukt("Rouladen"));
            produktList.add(new TblProdukt("Aufschnitt"));

//        }
//
//        cursor.close();

        return produktList;
    }

    // ToDo einen Artikel laden
    public List<TblProdukt> getAktProdukt(String aktArt) {
        Log.d(TAG, "##getAktProdukt: " + aktArt);
        if ((aktArt == null) || (aktArt == "")) {
            aktArt = "*";
        }

        List<TblProdukt> produktList = new ArrayList<>();

        if (DatabaseUtils.queryNumEntries(database, TblProdukt.TABLE_NAME) > 0) {
            Cursor cursor = database.query(TblProdukt.TABLE_NAME,
                    columns_pro, "name like '" + aktArt + "%'", null, null, null, "name");

            cursor.moveToFirst();
            TblProdukt produkt;

            while (!cursor.isAfterLast()) {
                produkt = cursorToProdukt(cursor);
                produktList.add(produkt);
                Log.d(TAG, "##ID: " + produkt.getId() + ", Inhalt: " + produkt.toString());
                cursor.moveToNext();
            }
            return produktList;
        }
        return null;
    }


    public void deleteShoppingMemo(TblShoppingMemo shoppingMemo) {
        long id = shoppingMemo.getId();

        database.delete(TblShoppingMemo.TABLE_NAME,
                TblShoppingMemo.COLUMN._ID.bez() + "=" + id,
                null);

        Log.d(TAG, "##Eintrag gelöscht! ID: " + id + " Inhalt: " + shoppingMemo.toString());
    }

//    public TblShoppingMemo updateShoppingMemo(long id, String newProduct, int newQuantity, boolean newChecked) {
//        ContentValues values = new ContentValues();
//        values.put(TblShoppingMemo.COLUMN.PRODUCT, newProduct);
//        values.put(TblShoppingMemo.COLUMN.QUANTITY, newQuantity);
//        values.put(TblShoppingMemo.COLUMN.CHECKED, newChecked ? 1 : 0);
//
//        database.update(ShoppingMemoDBHelper.TABLE_SHOPPING_LIST,
//                values,
//                TblShoppingMemo.COLUMN.ID + "=" + id,
//                null);
//
//        Cursor cursor = database.query(ShoppingMemoDBHelper.TABLE_SHOPPING_LIST,
//                columns, TblShoppingMemo.COLUMN.ID + "=" + id,
//                null, null, null, null);
//
//        cursor.moveToFirst();
//        TblShoppingMemo shoppingMemo = cursorToShoppingMemo(cursor);
//        cursor.close();
//
//        return shoppingMemo;
//    }

    public TblShoppingMemo updateShoppingMemo(long id, String newProduct, int newQuantity,
                                              String newEinh, boolean newChecked, double newPreis,
                                              String newWg, String newVonwo) {

        //ToDo ergänze fehlende Werte aus Artikel

        //newQuantity = newQuantity==null?0:newQuantity;
        newEinh = newEinh == null ? "" : newEinh;
        //newChecked = newChecked==null?false:newChecked;
        //newPreis = newPreis==null?0:newPreis;
        newWg = newWg == null ? "" : newWg;
        newVonwo = newVonwo == null ? "" : newVonwo;

        ContentValues values = new ContentValues();
        values.put(TblShoppingMemo.COLUMN.PRODUCT.bez(), newProduct);
        values.put(TblShoppingMemo.COLUMN.QUANTITY.bez(), newQuantity);
        values.put(TblShoppingMemo.COLUMN.EINH.bez(), newEinh);
        values.put(TblShoppingMemo.COLUMN.CHECKED.bez(), newChecked ? 1 : 0);
        values.put(TblShoppingMemo.COLUMN.PREIS.bez(), newPreis);
        values.put(TblShoppingMemo.COLUMN.WARENGRUPPE.bez(), newWg);
        values.put(TblShoppingMemo.COLUMN.VONWO.bez(), newVonwo);

        Log.d(TAG, "##updateShoppingMemo 339: "+values.toString());
        Log.d(TAG, "##übergebene Werte 340: " + id + ", " + newProduct + ", " + newQuantity + ", "
                + newEinh + ", " + newChecked + ", " + newPreis + ", " + newWg + ", " + newVonwo + ", ");
        Log.d(TAG, "##in values: " + values.toString());

        database.update(TblShoppingMemo.TABLE_NAME,
                values,
                TblShoppingMemo.COLUMN._ID.bez() + "=" + id,
                null);

        Cursor cursor = database.query(TblShoppingMemo.TABLE_NAME,
                columns, TblShoppingMemo.COLUMN._ID.bez() + "=" + id,
                null, null, null, null);

        cursor.moveToFirst();

        // ToDo speichere Durchschnittspreis

        TblShoppingMemo shoppingMemo = cursorToShoppingMemo(cursor);
        cursor.close();

        return shoppingMemo;
    }

    // ToDo Artikel einfügen
    public TblProdukt insertProdukt(String newName, String newWarengruppe, String newVonWo, String newEinh) {
        ContentValues values = new ContentValues();
        values.put(TblProdukt.COLUMN.NAME.bez(), newName);
        values.put(TblProdukt.COLUMN.WARENGRUPPE.bez(), newWarengruppe);
        values.put(TblProdukt.COLUMN.VONWO.bez(), newVonWo);
        values.put(TblProdukt.COLUMN.EINH.bez(), newEinh);

        Log.d(TAG, "##insertProdukt 370: "+values.toString());

        long id = database.insert(TblProdukt.TABLE_NAME, null, values);
        Cursor cursor = database.query(TblProdukt.TABLE_NAME,
                columns_pro, TblProdukt.COLUMN._ID.bez() + "=" + id,
                null, null, null, null);
        cursor.moveToFirst();
        TblProdukt insProdukt = cursorToProdukt(cursor);
        cursor.close();

        return insProdukt;
    }

    // ToDo Warengruppenliste laden

}
