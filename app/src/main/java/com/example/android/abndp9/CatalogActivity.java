package com.example.android.abndp9;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.android.abndp9.data.KitContract.KitEntry;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final int KIT_LOADER = 0;

    RelativeLayout emptyView;


    KitCursorAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        FloatingActionButton fab = findViewById(R.id.insert_book_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });


        ListView booksListView = findViewById(R.id.list_view_books);


        emptyView = findViewById(R.id.empty_view);
        booksListView.setEmptyView(emptyView);



        adapter = new KitCursorAdapter(this, null);
        booksListView.setAdapter(adapter);


        booksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent i = new Intent(CatalogActivity.this, EditorActivity.class);

                Uri currentBookUri = ContentUris.withAppendedId(KitEntry.CONTENT_URI, id);
                i.setData(currentBookUri);
                startActivity(i);
            }
        });


        getLoaderManager().initLoader(KIT_LOADER, null, this);
    }

    private void deleteAllKits() {

        int rowsDeleted = 0;


        rowsDeleted = getContentResolver().delete(
                KitEntry.CONTENT_URI,
                null,
                null
        );
        if (rowsDeleted == 0) {

            Toast.makeText(this, R.string.error_while_deleting_kit,
                    Toast.LENGTH_SHORT).show();
        } else {

            Toast.makeText(this, R.string.all_kit_deleted,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteConfirmationDialog() {

        if(!(emptyView.getVisibility() == View.VISIBLE)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.delete_all_kit);
            builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    deleteAllKits();
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });


            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.action_delete_all_entries:
                showDeleteConfirmationDialog();
                return true;
            default:
                return false;
        }
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
                KitEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        adapter.swapCursor(null);
    }
}
