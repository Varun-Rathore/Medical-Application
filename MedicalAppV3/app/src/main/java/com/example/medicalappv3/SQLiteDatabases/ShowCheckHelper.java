package com.example.medicalappv3.SQLiteDatabases;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class ShowCheckHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "showcheck.db";
    private static final String CHECK_TABLE = "show_table";
    private static final String col0 = "ID";

    public ShowCheckHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table IF NOT EXISTS " + CHECK_TABLE + " (ID TEXT PRIMARY KEY)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CHECK_TABLE);
    }

    @SuppressLint("Recycle")
    public void addcheck(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col0, id);
        db.insert(CHECK_TABLE, null, contentValues);
    }

    public Cursor getcheck() {
        final SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("Select * from " + CHECK_TABLE, null);
    }
}

