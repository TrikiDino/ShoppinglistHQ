package com.web.shoppinglisthq;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ShoppingMemoDataSource dataSource;
    private boolean isButtonClick = true;
    private ListView mShoppingMemosListView;
    private AutoCompleteTextView actvEinh;
    private AutoCompleteTextView actvVonWo;
    private AutoCompleteTextView actvArtikel;

    public String strProduct = "";
    public boolean productNeu = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ShoppingMemo testMemo = new ShoppingMemo("Birnen",5,102);
        //Log.d(TAG, "Inhalt der Testmemo: " + testMemo.toString());

        Log.d(TAG, "##Das Datenquellen-Objekt wird angelegt.");
        dataSource = new ShoppingMemoDataSource(this);

//        Log.d(TAG, "Die Datenquelle wird geöffnet.");
//        dataSource.open();
//
//        ShoppingMemo shoppingMemo = dataSource.createShoppingMemo("Testprodukt", 2);
//        Log.d(TAG, "Es wurde folgender Eintrag in die Datenbank geschrieben:");
//        Log.d(TAG, "ID: " + shoppingMemo.getId() + ", Inhalt: " + shoppingMemo.toString());
//
//        Log.d(TAG, "Folgende Einträge sind in der Datenbank vorhanden:");
//        showAllListEntries();
//
//        Log.d(TAG, "Die Datenquelle wird geschlossen.");
//        dataSource.close();

        initializeShoppingMemosList();

        activateAddButton();
        initializeContextualActionBar();


        // ToDo fülle MultiAutoComplete in activity_main mit Werten - OK
        actvArtikel = (AutoCompleteTextView) findViewById(R.id.editText_product);
        actvEinh = (AutoCompleteTextView) findViewById(R.id.actv_Einh);
        actvVonWo = (AutoCompleteTextView) findViewById(R.id.actv_VonWo);

        ArrayAdapter adapterEinh = new ArrayAdapter(this, android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.einh_array));
        ArrayAdapter adapterVonWo = new ArrayAdapter(this, android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.geschaeft_array));
        // ToDo Artikeladapter aus Datenbank füllen
        ArrayAdapter adapterArtikel = new ArrayAdapter(this, android.R.layout.simple_list_item_1,
                dataSource.getAlleArtikel());

        actvArtikel.setAdapter(adapterArtikel);
        actvArtikel.setThreshold(1);
        actvEinh.setAdapter(adapterEinh);
        actvEinh.setThreshold(1);
        actvVonWo.setAdapter(adapterVonWo);
        actvVonWo.setThreshold(1);

    }

    private void initializeShoppingMemosList() {
        List<TblShoppingMemo> emptyListForInitialisation = new ArrayList<>();

        mShoppingMemosListView = findViewById(R.id.listview_shopping_memos);
        ArrayAdapter<TblShoppingMemo> shoppingMemoArrayAdapter = new ArrayAdapter<TblShoppingMemo>(this,
                android.R.layout.simple_list_item_multiple_choice, emptyListForInitialisation) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;

                TblShoppingMemo memo = (TblShoppingMemo) mShoppingMemosListView.getItemAtPosition(position);
                if (memo.isChecked()) {
                    textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    textView.setTextColor(Color.GRAY);
                } else {
                    textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    textView.setTextColor(Color.DKGRAY);
                }

                return view;
            }
        };

        mShoppingMemosListView.setAdapter(shoppingMemoArrayAdapter);

        mShoppingMemosListView.setOnItemClickListener((parent, view, position, id) -> {
            TblShoppingMemo memo = (TblShoppingMemo) parent.getItemAtPosition(position);
//            TblShoppingMemo updateMemo = dataSource.updateShoppingMemo(memo.getId(),memo.getProduct(),
//                    memo.getQuantity(),!memo.isChecked());
            TblShoppingMemo updateMemo = dataSource.updateShoppingMemo(memo.getId(), memo.getProduct(),
                    memo.getQuantity(), memo.getEinh(), !memo.isChecked(), memo.getPreis(),
                    memo.getWarenGruppe(), memo.getVonWo());
            showAllListEntries();
        });
    }

    private void activateAddButton() {
        final Button buttonAddProduct = (Button) findViewById(R.id.btn_add_product);
        final EditText editTextQuantity = (EditText) findViewById(R.id.editText_quantity);
        final EditText editTextProdukt = (EditText) findViewById(R.id.editText_product);
        final AutoCompleteTextView actvEinh = (AutoCompleteTextView) findViewById(R.id.actv_Einh);
        final EditText editTextPreis = (EditText) findViewById(R.id.editText_Preis);
        final AutoCompleteTextView actvVonWo = (AutoCompleteTextView) findViewById(R.id.actv_VonWo);

        editTextProdukt.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d(TAG, "##beforeTextChanged: s:" + s + " start:" + start + " count:" + count + " after:" + after);
                strProduct = s.toString();
                List<TblProdukt> produktList = dataSource.getAktProdukt(strProduct);
                if (produktList != null) {
                    Log.d(TAG, "##beforeTextChanged: " + produktList.toString());
                    if (produktList.size() > 0) {

                    }
                } else {
                    Log.d(TAG, "##beforeTextChanged: Tabelle ist leer");
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ToDo String[] für AutoCompleteTextView füllen - ?? - evtl Tabellen anlegen
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editTextProdukt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d(TAG, "##onFocusChange: " + strProduct);
                if (!((strProduct == null) || (strProduct.length() == 0))) {
                    List<TblProdukt> produktList = dataSource.getAktProdukt(strProduct);
                    productNeu = ((produktList == null) || (produktList.size() == 0));
                    Log.d(TAG, "##onFocusChange: " + productNeu);
                    if (productNeu) {
                        // neuen Artikel erfassen
                        Log.d(TAG, "##onFocusChange: Artikel erfassen");
                        AlertDialog editProduktDialog = createEditProduktDialog();
                        editProduktDialog.show();
                    }
                    strProduct = "";
                }
            }
        });

        buttonAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantityString = editTextQuantity.getText().toString();
                String product = editTextProdukt.getText().toString();
                String einh = actvEinh.getText().toString();
                boolean checked = false;
                String preisString = editTextPreis.getText().toString();
                preisString = (preisString==null)|(preisString.equals(""))?"0":preisString;
                String vonWo = actvVonWo.getText().toString();
                String wg = "";

                if (TextUtils.isEmpty(quantityString)) {
                    editTextQuantity.setError(getString(R.string.editText_errorMessage));
                    return;
                }
                if (TextUtils.isEmpty(product)) {
                    editTextProdukt.setError(getString(R.string.editText_errorMessage));
                    return;
                }
                int quantity = Integer.parseInt(quantityString);
                double preis = Double.parseDouble(preisString);
                editTextQuantity.setText("");
                editTextProdukt.setText("");
                actvEinh.setText("");
                editTextPreis.setText("");
                actvVonWo.setText("");

                dataSource.createShoppingMemo(product, quantity, einh, checked, preis, wg, vonWo);

                InputMethodManager inputMethodManager;
                inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (getCurrentFocus() != null && isButtonClick) {
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }

                showAllListEntries();
            }
        });
        editTextProdukt.setOnEditorActionListener((textView, pos, KeyEvent) -> {
            isButtonClick = false;
            buttonAddProduct.performClick();
            editTextQuantity.requestFocus();
            return isButtonClick = true;
        });
    }

    // ToDo  neuen Artikel anlegen
    private AlertDialog createEditProduktDialog() {
        Log.d(TAG, "##createEditProduktDialog: ");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        View dialogsView = inflater.inflate(R.layout.dialog_artikel_neu, null);

        final EditText editArtName = (EditText) dialogsView.findViewById(R.id.artName);
        EditText editText_produkt = (EditText) dialogsView.findViewById(R.id.editText_product);
        editArtName.setText(strProduct);

        final AutoCompleteTextView editEinh = (AutoCompleteTextView) dialogsView.findViewById(R.id.actv_newEinh_a);
        final AutoCompleteTextView editWarengruppe = (AutoCompleteTextView) dialogsView.findViewById(R.id.actv_newWg_a);
        final AutoCompleteTextView editVonWo = (AutoCompleteTextView) dialogsView.findViewById(R.id.actv_newVonWo_a);

        // ToDo MultiAutoText füllen für neuen Artikel
        ArrayAdapter adapterEinh = new ArrayAdapter(this, android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.einh_array));
        ArrayAdapter adapterVonWo = new ArrayAdapter(this, android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.geschaeft_array));
        ArrayAdapter adapterWg = new ArrayAdapter(this, android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.wg_array));

        editEinh.setAdapter(adapterEinh);
        editEinh.setThreshold(1);
        editWarengruppe.setAdapter(adapterWg);
        editWarengruppe.setThreshold(1);
        editVonWo.setAdapter(adapterVonWo);
        editVonWo.setThreshold(1);

//        editEinh.setText(((EditText)dialogsView.findViewById(R.id.actv_Einh)).getText());
//        editVonWo.setText(((EditText)dialogsView.findViewById(R.id.actv_VonWo)).getText());

        builder.setView(dialogsView)
                .setTitle(R.string.dialog_title_pro)
                .setPositiveButton(R.string.dialog_button_speichern, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String name = editArtName.getText().toString();
                        // ToDo Wert aus Warengruppe auslesen - OK
                        String warengruppe = editWarengruppe.getText().toString();
                         // ToDo Wert aus Geschäft auslesen - OK
                        String vonWo = editVonWo.getText().toString();
                        String einh = editEinh.getText().toString();

                        if ((TextUtils.isEmpty(name))) {
                            Log.d(TAG, "Ein Eintrag enthielt keinen Text. Daher Abbruch der Änderung.");
                            Toast.makeText(MainActivity.this, "Artikelbezeichnung darf nicht leer sein", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // An dieser Stelle schreiben wir den neuen Artikel in die SQLite Datenbank
                        TblProdukt insertTblProdukt = dataSource.insertProdukt(name, warengruppe, vonWo, einh);

                        Log.d(TAG, "##Neuer Eintrag - ID: " + insertTblProdukt.getId() + " Inhalt: " + insertTblProdukt.toString());

                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.dialog_button_negative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        return builder.create();
    }

    private void showAllListEntries() {
        List<TblShoppingMemo> shoppingMemoList = dataSource.getAllShoppingMemos();
        ArrayAdapter<TblShoppingMemo> adapter = (ArrayAdapter<TblShoppingMemo>) mShoppingMemosListView.getAdapter();
        adapter.clear();
        adapter.addAll(shoppingMemoList);
        adapter.notifyDataSetChanged();

//        List<TblShoppingMemo> shoppingMemoList = dataSource.getAllShoppingMemos();
//
//        ArrayAdapter<TblShoppingMemo> shoppingMemoArrayAdapter = new ArrayAdapter<>(
//                this,
//                android.R.layout.simple_list_item_multiple_choice,
//                shoppingMemoList);
//
//        ListView shoppingMemosListView = (ListView) findViewById(R.id.listview_shopping_memos);
//        shoppingMemosListView.setAdapter(shoppingMemoArrayAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Hinzufügen des Menüs, wenn eines angelegt ist
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Toast.makeText(this, "Einstellungen wurde gedrückt", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "##Die Datenquelle wird geöffnet.");
        dataSource.open();

        Log.d(TAG, "##Folgende Einträge sind in der Datenbank vorhanden:");
        showAllListEntries();
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d(TAG, "##Die Datenquelle wird geschlossen.");
        dataSource.close();
    }

    // Einkaufsliste ändern
    private AlertDialog createEditShoppingMemoDialog(@org.jetbrains.annotations.NotNull final TblShoppingMemo shoppingMemo) {

        // ToDo zusätzliche Felder im Änderungsdialog einfügen - OK
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        View dialogsView = inflater.inflate(R.layout.dialog_edit_shopping_memo, null);

        final EditText editTextNewQuantity = (EditText) dialogsView.findViewById(R.id.editText_new_quantity);
        editTextNewQuantity.setText(String.valueOf(shoppingMemo.getQuantity()));

        final EditText editTextNewProduct = (EditText) dialogsView.findViewById(R.id.editText_new_product);
        editTextNewProduct.setText(shoppingMemo.getProduct());

        final EditText editTextNewPreis = (EditText) dialogsView.findViewById(R.id.editText_newPreis);
        editTextNewPreis.setText(String.format("%.2f €", shoppingMemo.getPreis()));

        final AutoCompleteTextView actvNewEinh = (AutoCompleteTextView) dialogsView.findViewById(R.id.actv_newEinh_sm);
        final AutoCompleteTextView actvNewWg = (AutoCompleteTextView) dialogsView.findViewById((R.id.actv_newWg_sm));
        final AutoCompleteTextView actvNewVonWo = (AutoCompleteTextView) dialogsView.findViewById(R.id.actv_newVonWo_sm);

        // ToDo MultiAutoText füllen für edit_Shoppinglist - OK
        Log.d(TAG, "createEditShoppingMemoDialog: AutoCompleteTextView füllen");
        ArrayAdapter adapterEinh = new ArrayAdapter(this, android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.einh_array));
        ArrayAdapter adapterVonWo = new ArrayAdapter(this, android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.geschaeft_array));
        ArrayAdapter adapterWg = new ArrayAdapter(this, android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.wg_array));

        actvNewEinh.setAdapter(adapterEinh);
        actvNewEinh.setThreshold(1);
        actvNewWg.setAdapter(adapterWg);
        actvNewWg.setThreshold(1);
        actvNewVonWo.setAdapter(adapterVonWo);
        actvNewVonWo.setThreshold(1);

        Log.d(TAG, "##createEditShoppingMemoDialog: AutoCompleteTextView gefüllt");

        builder.setView(dialogsView)
                .setTitle(R.string.dialog_title)
                .setPositiveButton(R.string.dialog_button_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String quantityString = editTextNewQuantity.getText().toString();
                        String product = editTextNewProduct.getText().toString();
                        String einh = actvNewEinh.getText().toString();
                        String preisString = editTextNewPreis.getText().toString();
                        String wg = actvNewWg.getText().toString();
                        String vonWo = actvNewVonWo.getText().toString();


                        if ((TextUtils.isEmpty(quantityString)) || (TextUtils.isEmpty(product))) {
                            Log.d(TAG, "##Ein Eintrag enthielt keinen Text. Daher Abbruch der Änderung.");
                            Toast.makeText(MainActivity.this, "Felder dürfen nicht leere sein", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        int quantity = Integer.parseInt(quantityString);
                        double preis = Double.parseDouble(preisString.substring(0, preisString.length() - 2));

                        // An dieser Stelle schreiben wir die geänderten Daten in die SQLite Datenbank
                        //TblShoppingMemo updatedShoppingMemo = dataSource.updateShoppingMemo(shoppingMemo.getId(), product, quantity, shoppingMemo.isChecked());
                        //ToDo auch Warengruppe, Geschäft, Preis und Einheit speichern
                        Log.d(TAG, "##onClick: " + shoppingMemo.getId() + product + quantity + einh + shoppingMemo.isChecked() + preis + wg + vonWo);

                        TblShoppingMemo updatedShoppingMemo = dataSource.updateShoppingMemo(shoppingMemo.getId(),
                                product, quantity, einh, shoppingMemo.isChecked(), preis, wg, vonWo);

                        Log.d(TAG, "##Alter Eintrag - ID: " + shoppingMemo.getId() + " Inhalt: " + shoppingMemo.toString());
                        Log.d(TAG, "##Neuer Eintrag - ID: " + updatedShoppingMemo.getId() + " Inhalt: " + updatedShoppingMemo.toString());

                        showAllListEntries();
                        dialog.dismiss();
//                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                    }
                })
                .setNegativeButton(R.string.dialog_button_negative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        return builder.create();
    }

    private void initializeContextualActionBar() {

        final ListView shoppingMemosListView = (ListView) findViewById(R.id.listview_shopping_memos);
        shoppingMemosListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        shoppingMemosListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            int selCount = 0;

            // In dieser Callback-Methode zählen wir die ausgewählen Listeneinträge mit
            // und fordern ein Aktualisieren der Contextual Action Bar mit invalidate() an
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                if (checked) {
                    selCount++;
                } else {
                    selCount--;
                }
                String cabTitle = selCount + " " + getString(R.string.cab_checked_string);
                mode.setTitle(cabTitle);
                mode.invalidate();
            }

            // In dieser Callback-Methode legen wir die CAB-Menüeinträge an
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                getMenuInflater().inflate(R.menu.menu_contextual_action_bar, menu);
                return true;
            }

            // In dieser Callback-Methode reagieren wir auf den invalidate() Aufruf
            // Wir lassen das Edit-Symbol verschwinden, wenn mehr als 1 Eintrag ausgewählt ist
            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                MenuItem item = menu.findItem(R.id.cab_change);
                if (selCount == 1) {
                    item.setVisible(true);
                } else {
                    item.setVisible(false);
                }

                return true;
            }

            // In dieser Callback-Methode reagieren wir auf Action Item-Klicks
            // Je nachdem ob das Löschen- oder Ändern-Symbol angeklickt wurde
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                boolean returnValue = true;
                SparseBooleanArray touchedShoppingMemosPositions = shoppingMemosListView.getCheckedItemPositions();

                switch (item.getItemId()) {
                    case R.id.cab_delete:
                        for (int i = 0; i < touchedShoppingMemosPositions.size(); i++) {
                            boolean isChecked = touchedShoppingMemosPositions.valueAt(i);
                            if (isChecked) {
                                int postitionInListView = touchedShoppingMemosPositions.keyAt(i);
                                TblShoppingMemo shoppingMemo = (TblShoppingMemo) shoppingMemosListView.getItemAtPosition(postitionInListView);
                                Log.d(TAG, "##Position im ListView: " + postitionInListView + " Inhalt: " + shoppingMemo.toString());
                                dataSource.deleteShoppingMemo(shoppingMemo);
                            }
                        }
                        showAllListEntries();
                        mode.finish();
                        break;

                    case R.id.cab_change:
                        Log.d(TAG, "##Eintrag ändern");
                        for (int i = 0; i < touchedShoppingMemosPositions.size(); i++) {
                            boolean isChecked = touchedShoppingMemosPositions.valueAt(i);
                            if (isChecked) {
                                int postitionInListView = touchedShoppingMemosPositions.keyAt(i);
                                TblShoppingMemo shoppingMemo = (TblShoppingMemo) shoppingMemosListView.getItemAtPosition(postitionInListView);
                                Log.d(TAG, "##Position im ListView: " + postitionInListView + " Inhalt: " + shoppingMemo.toString());

                                AlertDialog editShoppingMemoDialog = createEditShoppingMemoDialog(shoppingMemo);
                                editShoppingMemoDialog.show();
//                                View forKeyboard = editShoppingMemoDialog.getCurrentFocus();
//                                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//                                imm.showSoftInput(getCurrentFocus(),InputMethodManager.RESULT_SHOWN);
                            }
                        }

                        mode.finish();
                        break;

                    default:
                        returnValue = false;
                        break;
                }
                return returnValue;
            }

            // In dieser Callback-Methode reagieren wir auf das Schließen der CAB
            // Wir setzen den Zähler auf 0 zurück
            @Override
            public void onDestroyActionMode(ActionMode mode) {
                selCount = 0;
//                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,1);
            }
        });
    }

    public void startScan(View view) {
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");

        intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
        try {
            startActivityForResult(intent, 1);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Scanner nicht installiert", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            TextView tvProduct = findViewById(R.id.editText_product);
            tvProduct.setText(getProductName(data.getStringExtra("SCAN_RESULT")));
            List<TblProdukt> produktList = dataSource.getAktProdukt(tvProduct.getText().toString());
            if (produktList.size() > 0) {
                // ToDo Artikelinformationen übertragen, wenn Artikel vorhanden
                TextView tvPreis = findViewById(R.id.editText_Preis);
                AutoCompleteTextView actvEinh = findViewById(R.id.actv_newEinh_sm);
                AutoCompleteTextView actvVonWo = findViewById(R.id.actv_newVonWo_sm);
                tvPreis.setText(String.format("%.2f €", produktList.get(0).getDurchpreis()));
                // ToDo Wert in Einheit eintragen
                // ToDo Wert in Geschäft eintragen
            }
            Log.d(TAG, "##onActivityResult: " + data.getStringExtra("SCAN_RESULT"));
        }
    }

    private String getProductName(String scanResult) {
        HoleDatenTask task = new HoleDatenTask();
        String result = null;
        try {
            result = task.execute(scanResult).get();
            JSONObject rootObject = new JSONObject(result);
            Log.d(TAG, "##getProductName: " + rootObject.toString(2));
            if (rootObject.has("product")) {
                JSONObject productObject = rootObject.getJSONObject("product");
                if (productObject.has("product_name")) {
                    return productObject.getString("product_name");
                }
            }
        } catch (InterruptedException e) {
            Log.e(TAG, "", e);
        } catch (ExecutionException e) {
            Log.e(TAG, "", e);
        } catch (JSONException e) {
            Log.e(TAG, "", e);
        }
        return "Artikel nicht gefunden";
    }

    public class HoleDatenTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {
            final String baseUrl = "https://world.openfoodfacts.org/api/v0/product/";
            final String requestUrl = baseUrl + strings[0] + ".json";
            Log.d(TAG, "##doInBackground: " + requestUrl);
            StringBuilder result = new StringBuilder();
            URL url = null;

            try {
                url = new URL(requestUrl);
            } catch (MalformedURLException e) {
                Log.e(TAG, "", e);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

            } catch (IOException e) {

            }
            Log.d(TAG, "##doInBackground: " + result.toString());
            return result.toString();
        }
    }
}
