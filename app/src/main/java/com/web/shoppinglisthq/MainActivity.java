package com.web.shoppinglisthq;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.KeyEvent;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ShoppingMemoDataSource dataSource;
    private boolean isButtonClick = true;
    private ListView mShoppingMemosListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ShoppingMemo testMemo = new ShoppingMemo("Birnen",5,102);
        //Log.d(TAG, "Inhalt der Testmemo: " + testMemo.toString());

        Log.d(TAG, "Das Datenquellen-Objekt wird angelegt.");
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
    }

    private void initializeShoppingMemosList() {
        List<TblShoppingMemo> emptyListForInitialisation = new ArrayList<>();

        mShoppingMemosListView = findViewById(R.id.listview_shopping_memos);
        ArrayAdapter<TblShoppingMemo> shoppingMemoArrayAdapter = new ArrayAdapter<TblShoppingMemo>(this,
                android.R.layout.simple_list_item_multiple_choice, emptyListForInitialisation){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view =  super.getView(position, convertView, parent);
                TextView textView = (TextView) view;

                TblShoppingMemo memo = (TblShoppingMemo) mShoppingMemosListView.getItemAtPosition(position);
                if(memo.isChecked()){
                    textView.setPaintFlags(textView.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
                     textView.setTextColor(Color.GRAY);
                } else {
                    textView.setPaintFlags(textView.getPaintFlags() &(~Paint.STRIKE_THRU_TEXT_FLAG));
                    textView.setTextColor(Color.DKGRAY);
                }

                return view;
            }
        };

        mShoppingMemosListView.setAdapter(shoppingMemoArrayAdapter);

        mShoppingMemosListView.setOnItemClickListener((parent, view, position, id) -> {
            TblShoppingMemo memo =(TblShoppingMemo) parent.getItemAtPosition(position);
            TblShoppingMemo updateMemo = dataSource.updateShoppingMemo(memo.getId(),memo.getProduct(),
                    memo.getQuantity(),!memo.isChecked());
            showAllListEntries();
        });
    }

    private void activateAddButton() {
        final Button buttonAddProduct = (Button) findViewById(R.id.btn_add_product);
        final EditText editTextQuantity = (EditText) findViewById(R.id.editText_quantity);
        final EditText editTextProdukt = (EditText) findViewById(R.id.editText_product);

        buttonAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantityString = editTextQuantity.getText().toString();
                String product = editTextProdukt.getText().toString();

                if (TextUtils.isEmpty(quantityString)) {
                    editTextQuantity.setError(getString(R.string.editText_errorMessage));
                    return;
                }
                if (TextUtils.isEmpty(product)) {
                    editTextProdukt.setError(getString(R.string.editText_errorMessage));
                    return;
                }
                int quantity = Integer.parseInt(quantityString);
                editTextQuantity.setText("");
                editTextProdukt.setText("");

                dataSource.createShoppingMemo(product, quantity);

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

    private void showAllListEntries() {
        List<TblShoppingMemo> shoppingMemoList = dataSource.getAllShoppingMemos();
        ArrayAdapter<TblShoppingMemo> adapter = (ArrayAdapter<TblShoppingMemo>)mShoppingMemosListView.getAdapter();
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

        Log.d(TAG, "Die Datenquelle wird geöffnet.");
        dataSource.open();

        Log.d(TAG, "Folgende Einträge sind in der Datenbank vorhanden:");
        showAllListEntries();
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d(TAG, "Die Datenquelle wird geschlossen.");
        dataSource.close();
    }

    private AlertDialog createEditShoppingMemoDialog(@org.jetbrains.annotations.NotNull final TblShoppingMemo shoppingMemo) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        View dialogsView = inflater.inflate(R.layout.dialog_edit_shopping_memo, null);

        final EditText editTextNewQuantity = (EditText) dialogsView.findViewById(R.id.editText_new_quantity);
        editTextNewQuantity.setText(String.valueOf(shoppingMemo.getQuantity()));

        final EditText editTextNewProduct = (EditText) dialogsView.findViewById(R.id.editText_new_product);
        editTextNewProduct.setText(shoppingMemo.getProduct());

        builder.setView(dialogsView)
                .setTitle(R.string.dialog_title)
                .setPositiveButton(R.string.dialog_button_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String quantityString = editTextNewQuantity.getText().toString();
                        String product = editTextNewProduct.getText().toString();


                        if ((TextUtils.isEmpty(quantityString)) || (TextUtils.isEmpty(product))) {
                            Log.d(TAG, "Ein Eintrag enthielt keinen Text. Daher Abbruch der Änderung.");
                            Toast.makeText(MainActivity.this, "Felder dürfen nicht leere sein", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        int quantity = Integer.parseInt(quantityString);

                        // An dieser Stelle schreiben wir die geänderten Daten in die SQLite Datenbank
                        TblShoppingMemo updatedShoppingMemo = dataSource.updateShoppingMemo(shoppingMemo.getId(), product, quantity, shoppingMemo.isChecked());

                        Log.d(TAG, "Alter Eintrag - ID: " + shoppingMemo.getId() + " Inhalt: " + shoppingMemo.toString());
                        Log.d(TAG, "Neuer Eintrag - ID: " + updatedShoppingMemo.getId() + " Inhalt: " + updatedShoppingMemo.toString());

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
                                Log.d(TAG, "Position im ListView: " + postitionInListView + " Inhalt: " + shoppingMemo.toString());
                                dataSource.deleteShoppingMemo(shoppingMemo);
                            }
                        }
                        showAllListEntries();
                        mode.finish();
                        break;

                    case R.id.cab_change:
                        Log.d(TAG, "Eintrag ändern");
                        for (int i = 0; i < touchedShoppingMemosPositions.size(); i++) {
                            boolean isChecked = touchedShoppingMemosPositions.valueAt(i);
                            if (isChecked) {
                                int postitionInListView = touchedShoppingMemosPositions.keyAt(i);
                                TblShoppingMemo shoppingMemo = (TblShoppingMemo) shoppingMemosListView.getItemAtPosition(postitionInListView);
                                Log.d(TAG, "Position im ListView: " + postitionInListView + " Inhalt: " + shoppingMemo.toString());

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
}
