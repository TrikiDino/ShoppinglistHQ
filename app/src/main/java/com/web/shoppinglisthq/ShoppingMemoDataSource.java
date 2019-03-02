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

    // Tabelle Einkaufsliste
    private String[] columns = {
            ShoppingMemoDBHelper.COLUMN_ID,
            ShoppingMemoDBHelper.COLUMN_PRODUCT,
            ShoppingMemoDBHelper.COLUMN_EINH,
            ShoppingMemoDBHelper.COLUMN_QUANTITY,
            ShoppingMemoDBHelper.COLUMN_CHECKED,
            ShoppingMemoDBHelper.COLUMN_PREIS,
            ShoppingMemoDBHelper.COLUMN_WARENGRUPPE,
            ShoppingMemoDBHelper.COLUMN_VONWO
    };
    // Tabelle Produkte
    private String[] columns_pro = {
            ShoppingMemoDBHelper.COLUMN_ID_PRO,
            ShoppingMemoDBHelper.COLUMN_NAME,
            ShoppingMemoDBHelper.COLUMN_EINH_PRO,
            ShoppingMemoDBHelper.COLUMN_DURCH_PREIS,
            ShoppingMemoDBHelper.COLUMN_WARENGRUPPE_PRO,
            ShoppingMemoDBHelper.COLUMN_VONWO_PRO
    };

    public ShoppingMemoDataSource(Context context) {
        Log.d(TAG, "Unsere Datasource erzeugt jetzt den dbHelper");
        dbHelper = new ShoppingMemoDBHelper(context);
    }

    public void open() {
        Log.d(TAG, "Eine Referenz auf die Datenbank wird jetzt angefragt.");
        database = dbHelper.getWritableDatabase();
        Log.d(TAG, "Datenbankreferenz erhalten. Pfad zur Datenbank: " + database.getPath());
    }

    public void close() {
        dbHelper.close();
        Log.d(TAG, "Datenbank mit Hilfe des DbHelpers geschlossen.");
    }

    public TblShoppingMemo createShoppingMemo(String product, int quantity) {
        ContentValues values = new ContentValues();
        values.put(ShoppingMemoDBHelper.COLUMN_PRODUCT, product);
        values.put(ShoppingMemoDBHelper.COLUMN_QUANTITY, quantity);

        long insertId = database.insert(ShoppingMemoDBHelper.TABLE_SHOPPING_LIST, null, values);

        Cursor cursor = database.query(ShoppingMemoDBHelper.TABLE_SHOPPING_LIST,
                columns, ShoppingMemoDBHelper.COLUMN_ID + "=" + insertId,
                null, null, null, null);

        cursor.moveToFirst();
        TblShoppingMemo shoppingMemo = cursorToShoppingMemo(cursor);
        cursor.close();

        return shoppingMemo;
    }

    // Cursor für Einkaufsliste
    private TblShoppingMemo cursorToShoppingMemo(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(ShoppingMemoDBHelper.COLUMN_ID);
        int idProduct = cursor.getColumnIndex(ShoppingMemoDBHelper.COLUMN_PRODUCT);
        int idQuantity = cursor.getColumnIndex(ShoppingMemoDBHelper.COLUMN_QUANTITY);
        int idEinh = cursor.getColumnIndex(ShoppingMemoDBHelper.COLUMN_EINH);
        int idChecked = cursor.getColumnIndex(ShoppingMemoDBHelper.COLUMN_CHECKED);
        int idPreis = cursor.getColumnIndex(ShoppingMemoDBHelper.COLUMN_PREIS);
        int idWg = cursor.getColumnIndex(ShoppingMemoDBHelper.COLUMN_WARENGRUPPE);
        int idVonWo = cursor.getColumnIndex(ShoppingMemoDBHelper.COLUMN_VONWO);

        String product = cursor.getString(idProduct);
        int quantity = cursor.getInt(idQuantity);
        String einh = cursor.getString(idEinh);
        long id = cursor.getLong(idIndex);
        boolean checked = cursor.getInt(idChecked) == 0 ? false : true;
        double preis = cursor.getDouble(idPreis);
        String wg = cursor.getString(idWg);
        String vonwo = cursor.getString(idVonWo);

        TblShoppingMemo shoppingMemo = new TblShoppingMemo(product, quantity, id, checked);

        return shoppingMemo;
    }

    // Cursor für Produkte
    private TblProdukt cursorToProdukt(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(ShoppingMemoDBHelper.COLUMN_ID_PRO);
        int idProduct = cursor.getColumnIndex(ShoppingMemoDBHelper.COLUMN_NAME);
        int idEinh = cursor.getColumnIndex(ShoppingMemoDBHelper.COLUMN_EINH_PRO);
        int idPreis = cursor.getColumnIndex(ShoppingMemoDBHelper.COLUMN_DURCH_PREIS);
        int idWg = cursor.getColumnIndex(ShoppingMemoDBHelper.COLUMN_WARENGRUPPE_PRO);
        int idVonWo = cursor.getColumnIndex(ShoppingMemoDBHelper.COLUMN_VONWO_PRO);

        String product = cursor.getString(idProduct);
        String einh = cursor.getString(idEinh);
        long id = cursor.getLong(idIndex);
        double preis = cursor.getDouble(idPreis);
        String wg = cursor.getString(idWg);
        String vonwo = cursor.getString(idVonWo);

        TblProdukt produkt = new TblProdukt(product, einh, preis, vonwo, wg);

        return produkt;
    }

    // Lese gesamte Einkaufsliste
    public List<TblShoppingMemo> getAllShoppingMemos() {
        List<TblShoppingMemo> shoppingMemoList = new ArrayList<>();

        Cursor cursor = database.query(ShoppingMemoDBHelper.TABLE_SHOPPING_LIST,
                columns, null, null, null, null, null);

        cursor.moveToFirst();
        TblShoppingMemo shoppingMemo;
        String wechselWg = "";

        while (!cursor.isAfterLast()) {
            shoppingMemo = cursorToShoppingMemo(cursor);
            String fldWg = shoppingMemo.getWarenGruppe() == null ? "" : shoppingMemo.getWarenGruppe();
            if (wechselWg != fldWg) {
//                shoppingMemoList.add(new TblShoppingMemo(fldWg.toUpperCase(),
//                        0,0,false));
//                wechselWg = fldWg;
                Log.d(TAG, "getAllShoppingMemos: wechselWG:" + wechselWg + " Warengruppe:" + fldWg);
            }
            shoppingMemoList.add(shoppingMemo);
            Log.d(TAG, "ID: " + shoppingMemo.getId() + ", Inhalt: " + shoppingMemo.toString());
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
        Log.d(TAG, "getAllShoppingMemos: " + orderBy + ", " + filterBy);

        Cursor cursor = database.query(ShoppingMemoDBHelper.TABLE_SHOPPING_LIST,
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
            Log.d(TAG, "ID: " + shoppingMemo.getId() + ", Inhalt: " + shoppingMemo.toString());
            cursor.moveToNext();
        }

        cursor.close();

        return shoppingMemoList;
    }

    // Lese aktuelle Artikel
    public List<TblProdukt> getAktProdukt(String aktArt) {
        Log.d(TAG, "getAktProdukt: " + aktArt);
        if((aktArt==null) || (aktArt=="")){
            aktArt = "*";
        }

        List<TblProdukt> produktList = new ArrayList<>();

        if(DatabaseUtils.queryNumEntries(database,ShoppingMemoDBHelper.TABLE_PRODUCT)>0) {
            Cursor cursor = database.query(ShoppingMemoDBHelper.TABLE_PRODUCT,
                    columns, "name like " + aktArt + "%", null, null, null, "name");

            cursor.moveToFirst();
            TblProdukt produkt;

            while (!cursor.isAfterLast()) {
                produkt = cursorToProdukt(cursor);
                produktList.add(produkt);
                Log.d(TAG, "ID: " + produkt.getID() + ", Inhalt: " + produkt.toString());
                cursor.moveToNext();
            }
            return produktList;
        }
        return null;
    }


    public void deleteShoppingMemo(TblShoppingMemo shoppingMemo) {
        long id = shoppingMemo.getId();

        database.delete(ShoppingMemoDBHelper.TABLE_SHOPPING_LIST,
                ShoppingMemoDBHelper.COLUMN_ID + "=" + id,
                null);

        Log.d(TAG, "Eintrag gelöscht! ID: " + id + " Inhalt: " + shoppingMemo.toString());
    }

    public TblShoppingMemo updateShoppingMemo(long id, String newProduct, int newQuantity, boolean newChecked) {
        ContentValues values = new ContentValues();
        values.put(ShoppingMemoDBHelper.COLUMN_PRODUCT, newProduct);
        values.put(ShoppingMemoDBHelper.COLUMN_QUANTITY, newQuantity);
        values.put(ShoppingMemoDBHelper.COLUMN_CHECKED, newChecked ? 1 : 0);

        database.update(ShoppingMemoDBHelper.TABLE_SHOPPING_LIST,
                values,
                ShoppingMemoDBHelper.COLUMN_ID + "=" + id,
                null);

        Cursor cursor = database.query(ShoppingMemoDBHelper.TABLE_SHOPPING_LIST,
                columns, ShoppingMemoDBHelper.COLUMN_ID + "=" + id,
                null, null, null, null);

        cursor.moveToFirst();
        TblShoppingMemo shoppingMemo = cursorToShoppingMemo(cursor);
        cursor.close();

        return shoppingMemo;
    }

    public TblShoppingMemo updateShoppingMemo(long id, String newProduct, int newQuantity,
                                              String newEinh, boolean newChecked, double newPreis, String newWg, String newVonwo) {
        ContentValues values = new ContentValues();
        values.put(ShoppingMemoDBHelper.COLUMN_PRODUCT, newProduct);
        values.put(ShoppingMemoDBHelper.COLUMN_QUANTITY, newQuantity);
        values.put(ShoppingMemoDBHelper.COLUMN_EINH, newEinh);
        values.put(ShoppingMemoDBHelper.COLUMN_CHECKED, newChecked ? 1 : 0);
        values.put(ShoppingMemoDBHelper.COLUMN_PREIS, newPreis);
        values.put(ShoppingMemoDBHelper.COLUMN_WARENGRUPPE, newWg);
        values.put(ShoppingMemoDBHelper.COLUMN_VONWO, newVonwo);

        database.update(ShoppingMemoDBHelper.TABLE_SHOPPING_LIST,
                values,
                ShoppingMemoDBHelper.COLUMN_ID + "=" + id,
                null);

        Cursor cursor = database.query(ShoppingMemoDBHelper.TABLE_SHOPPING_LIST,
                columns, ShoppingMemoDBHelper.COLUMN_ID + "=" + id,
                null, null, null, null);

        cursor.moveToFirst();
        TblShoppingMemo shoppingMemo = cursorToShoppingMemo(cursor);
        cursor.close();

        return shoppingMemo;
    }

    public TblProdukt insertProdukt(String name, String warengruppe, String vonWo) {
        TblProdukt insProdukt = new TblProdukt(name, vonWo, warengruppe);
        return insProdukt;
    }
}
