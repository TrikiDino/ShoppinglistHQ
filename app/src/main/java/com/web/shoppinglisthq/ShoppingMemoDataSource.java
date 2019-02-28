package com.web.shoppinglisthq;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ShoppingMemoDataSource {

    private static final String TAG = ShoppingMemoDataSource.class.getSimpleName();

    private SQLiteDatabase database;
    private ShoppingMemoDBHelper dbHelper;

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

    public ShoppingMemoDataSource(Context context) {
        Log.d(TAG, "Unsere Datasource erzeugt jetzt den dbHelper");
        dbHelper = new ShoppingMemoDBHelper(context);
    }

    public void open(){
        Log.d(TAG, "Eine Referenz auf die Datenbank wird jetzt angefragt.");
        database = dbHelper.getWritableDatabase();
        Log.d(TAG, "Datenbankreferenz erhalten. Pfad zur Datenbank: " + database.getPath());
    }

    public void close(){
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
        int einh = cursor.getInt(idEinh);
        long id = cursor.getLong(idIndex);
        boolean checked = cursor.getInt(idChecked)==0?false:true;
        double preis = cursor.getDouble(idPreis);
        String wg = cursor.getString(idWg);
        String vonwo = cursor.getString(idVonWo);

        TblShoppingMemo shoppingMemo = new TblShoppingMemo(product, quantity, id, checked);

        return shoppingMemo;
    }

    public List<TblShoppingMemo> getAllShoppingMemos() {
        List<TblShoppingMemo> shoppingMemoList = new ArrayList<>();

        Cursor cursor = database.query(ShoppingMemoDBHelper.TABLE_SHOPPING_LIST,
                columns, null, null, null, null, null);

        cursor.moveToFirst();
        TblShoppingMemo shoppingMemo;

        while(!cursor.isAfterLast()) {
            shoppingMemo = cursorToShoppingMemo(cursor);
            shoppingMemoList.add(shoppingMemo);
            Log.d(TAG, "ID: " + shoppingMemo.getId() + ", Inhalt: " + shoppingMemo.toString());
            cursor.moveToNext();
        }

        cursor.close();

        return shoppingMemoList;
    }

    public List<TblShoppingMemo> getAllShoppingMemos(String[][] params) {
        List<TblShoppingMemo> shoppingMemoList = new ArrayList<>();
        String filterBy = null;
        String orderBy = null;

        for(int i = 0;i<params.length;i++){
            switch (params[i][0]){
                case "S":
                    if(orderBy.length()>0) orderBy+=", ";
                    orderBy+=params[i][1];
                    break;
                case "F":
                    if(filterBy.length()>0) {
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

        Cursor cursor = database.query(ShoppingMemoDBHelper.TABLE_SHOPPING_LIST,
                columns, filterBy, null, null, null, orderBy);

        cursor.moveToFirst();
        TblShoppingMemo shoppingMemo;

        while(!cursor.isAfterLast()) {
            shoppingMemo = cursorToShoppingMemo(cursor);
            shoppingMemoList.add(shoppingMemo);
            Log.d(TAG, "ID: " + shoppingMemo.getId() + ", Inhalt: " + shoppingMemo.toString());
            cursor.moveToNext();
        }

        cursor.close();

        return shoppingMemoList;
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
        values.put(ShoppingMemoDBHelper.COLUMN_CHECKED, newChecked?1:0);

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
        values.put(ShoppingMemoDBHelper.COLUMN_CHECKED, newChecked?1:0);
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
}
