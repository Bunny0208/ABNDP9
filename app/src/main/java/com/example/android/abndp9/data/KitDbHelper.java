package com.example.android.abndp9.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.abndp9.data.KitContract.KitEntry;

public class KitDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "kit.db";

    public KitDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_BOOKS_TABLE = "CREATE TABLE " + KitEntry.TABLE_NAME + " ("
                + KitEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KitEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + KitEntry.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL, "
                + KitEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL, "
                + KitEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
                + KitEntry.COLUMN_SUPPLIER_PHONE_NUMBER + " TEXT NOT NULL );";
        sqLiteDatabase.execSQL(SQL_CREATE_BOOKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}
