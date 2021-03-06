package com.example.android.abndp9;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.abndp9.data.KitDbHelper;
import com.example.android.abndp9.data.KitContract.KitEntry;


public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private final int MINIMUM_QUANTITY_VALUE = 0;

    private final int MAXIMUM_QUANTITY_VALUE = 999;


    private boolean kitHasChanged = false;


    private String supplierContact;


    private static final int EXISTING_BOOK_LOADER = 1;


    private Uri currentKitUri;

    private EditText productNameEditText;

    private EditText productPriceEditText;

    private EditText productQuantityEditText;

    private EditText supplierNameEditText;

    private EditText supplierContactEditText;

    private Button subtractQuantityButton;

    private Button addQuantityButton;

    public KitDbHelper dbHelper;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);


        Intent intent = getIntent();
        currentKitUri = intent.getData();


        if(currentKitUri == null){

            setTitle(getString(R.string.add_a_Kit));

            invalidateOptionsMenu();
        }else{

            setTitle(getString(R.string.edit_kit));
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        productNameEditText = findViewById(R.id.product_name);
        productPriceEditText = findViewById(R.id.product_price);
        productQuantityEditText = findViewById(R.id.product_quantity);
        supplierNameEditText = findViewById(R.id.supplier_name);
        supplierContactEditText = findViewById(R.id.supplier_contact);
        subtractQuantityButton = findViewById(R.id.subtract_quantity);
        addQuantityButton = findViewById(R.id.add_quantity);
        subtractQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentQuantityString = productQuantityEditText.getText().toString();
                int currentQuantityInt;
                if(currentQuantityString.length() == 0){
                    currentQuantityInt = 0;
                    productQuantityEditText.setText(String.valueOf(currentQuantityInt));
                }else{
                    currentQuantityInt = Integer.parseInt(currentQuantityString) - 1;
                    if(currentQuantityInt >=MINIMUM_QUANTITY_VALUE) {
                        productQuantityEditText.setText(String.valueOf(currentQuantityInt));
                    }
                }

            }
        });
        addQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentQuantityString = productQuantityEditText.getText().toString();
                int currentQuantityInt;
                if(currentQuantityString.length() == 0){
                    currentQuantityInt = 1;
                    productQuantityEditText.setText(String.valueOf(currentQuantityInt));
                }else{
                    currentQuantityInt = Integer.parseInt(currentQuantityString) + 1;
                    if(currentQuantityInt<=MAXIMUM_QUANTITY_VALUE) {
                        productQuantityEditText.setText(String.valueOf(currentQuantityInt));
                    }
                }

            }
        });

        dbHelper = new KitDbHelper(this);


        productNameEditText.setOnTouchListener(mTouchListener);
        productPriceEditText.setOnTouchListener(mTouchListener);
        productQuantityEditText.setOnTouchListener(mTouchListener);
        subtractQuantityButton.setOnTouchListener(mTouchListener);
        addQuantityButton.setOnTouchListener(mTouchListener);
        supplierNameEditText.setOnTouchListener(mTouchListener);
        supplierContactEditText.setOnTouchListener(mTouchListener);

    }


    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            kitHasChanged = true;
            return false;
        }

    };

    @Override
    public void onBackPressed() {

        if (!kitHasChanged) {
            super.onBackPressed();
            return;
        }


        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        finish();
                    }
                };


        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void saveKit(){

        String productNameString = productNameEditText.getText().toString().trim();
        String productPriceString = productPriceEditText.getText().toString().trim();
        String productQuantityString = productQuantityEditText.getText().toString().trim();
        String supplierNameString = supplierNameEditText.getText().toString().trim();
        String supplierContactString = supplierContactEditText.getText().toString().trim();


        if (TextUtils.isEmpty(productNameString)) {
            productNameEditText.setError(getString(R.string.required));
            return;
        }

        if(TextUtils.isEmpty(productPriceString)){
            productPriceEditText.setError(getString(R.string.required));
            return;
        }
        if (TextUtils.isEmpty(productQuantityString)) {
            productQuantityEditText.setError(getString(R.string.required));
            return;
        }

        if(TextUtils.isEmpty(supplierNameString)){
            supplierNameEditText.setError(getString(R.string.required));
            return;
        }
        if(TextUtils.isEmpty(supplierContactString)){
            supplierContactEditText.setError(getString(R.string.required));
            return;
        }

        int productPriceInt = Integer.parseInt(productPriceString);
        int productQuantityInt = Integer.parseInt(productQuantityString);

        if(productPriceInt < 0){
            productPriceEditText.setError(getString(R.string.price_cannot_be_negative));
            return;
        }
        if(productQuantityInt < 0){
            productQuantityEditText.setError(getString(R.string.quantity_cannot_be_negative));
            return;
        }

        ContentValues values = new ContentValues();
        values.put(KitEntry.COLUMN_PRODUCT_NAME, productNameString);
        values.put(KitEntry.COLUMN_PRODUCT_PRICE, productPriceInt);
        values.put(KitEntry.COLUMN_PRODUCT_QUANTITY, productQuantityInt);
        values.put(KitEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
        values.put(KitEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierContactString);


        if(currentKitUri == null) {

            Uri newUri = getContentResolver().insert(KitEntry.CONTENT_URI, values);


            if (newUri == null) {

                Toast.makeText(this, getString(R.string.editor_insert_kit_failed), Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, getString(R.string.editor_insert_kit_successful), Toast.LENGTH_SHORT).show();
            }
        }else{

            int rowAffected = getContentResolver().update(currentKitUri, values, null, null);


            if (rowAffected == 0) {

                Toast.makeText(this, getString(R.string.editor_update_kit_failed),
                        Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, getString(R.string.editor_update_kit_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    private void deleteKit() {
        if (currentKitUri != null) {
            int rowsDeleted = 0;


            rowsDeleted = getContentResolver().delete(
                    currentKitUri,
                    null,
                    null
            );
            if (rowsDeleted == 0) {

                Toast.makeText(this, getString(R.string.error_deleting_kit),
                        Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, getString(R.string.kit_deleted),
                        Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.discard_changes_and_quit_editing));
        builder.setPositiveButton(getString(R.string.discard), discardButtonClickListener);
        builder.setNegativeButton(getString(R.string.keep_editing), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });


        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.delete_this_kit));
        builder.setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                deleteKit();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });


        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void callSupplier(){
        Intent supplierNumberIntent = new Intent(Intent.ACTION_DIAL);
        supplierNumberIntent.setData(Uri.parse("tel:" + supplierContact));
        startActivity(supplierNumberIntent);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (currentKitUri == null) {
            MenuItem menuItem;
            menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
            menuItem = menu.findItem(R.id.action_contact_supplier);
            menuItem.setVisible(false);
        }
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.action_save:

                saveKit();
                return true;

            case R.id.action_contact_supplier:

                callSupplier();
                break;

            case R.id.action_delete:

                showDeleteConfirmationDialog();
                break;

            case android.R.id.home:

                if (!kitHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };


                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
              KitEntry._ID,
                KitEntry.COLUMN_PRODUCT_NAME,
                KitEntry.COLUMN_PRODUCT_PRICE,
                KitEntry.COLUMN_PRODUCT_QUANTITY,
                KitEntry.COLUMN_SUPPLIER_NAME,
                KitEntry.COLUMN_SUPPLIER_PHONE_NUMBER,
        };

        return  new CursorLoader(this,
                currentKitUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if(cursor.moveToFirst()){


            int productNameColumnIndex = cursor.getColumnIndex(KitEntry.COLUMN_PRODUCT_NAME);
            int productPriceColumnIndex = cursor.getColumnIndex(KitEntry.COLUMN_PRODUCT_PRICE);
            int productQuantityColumnIndex = cursor.getColumnIndex(KitEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(KitEntry.COLUMN_SUPPLIER_NAME);
            int supplierContactColumnIndex = cursor.getColumnIndex(KitEntry.COLUMN_SUPPLIER_PHONE_NUMBER);


            String productName = cursor.getString(productNameColumnIndex);
            int productPrice = cursor.getInt(productPriceColumnIndex);
            int productQuantity = cursor.getInt(productQuantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            supplierContact = cursor.getString(supplierContactColumnIndex);


            productNameEditText.setText(productName);
            productPriceEditText.setText(String.valueOf(productPrice));
            productQuantityEditText.setText(String.valueOf(productQuantity));
            supplierNameEditText.setText(String.valueOf(supplierName));
            supplierContactEditText.setText(String.valueOf(supplierContact));


        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        productNameEditText.setText("");
        productPriceEditText.setText("");
        productQuantityEditText.setText("");
        supplierNameEditText.setText("");
        supplierContactEditText.setText("");
    }
}