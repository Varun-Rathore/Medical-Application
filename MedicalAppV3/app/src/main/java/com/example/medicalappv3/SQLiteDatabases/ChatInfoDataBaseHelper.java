package com.example.medicalappv3.SQLiteDatabases;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.medicalappv3.UserDefinedClasses.ChatInfo;


public class ChatInfoDataBaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "chatinfo.db";
    private static final String CHATINFO_TABLE = "chatinfo_table";
    private static final String col0 = "DOCTOR_NAME";
    private static final String col1 = "PATIENT_NAME";
    private static final String col2 = "DATE";
    private static final String col3 = "ID";

    public ChatInfoDataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table IF NOT EXISTS " + CHATINFO_TABLE + " (DOCTOR_NAME TEXT , PATIENT_NAME TEXT , DATE TEXT , ID TEXT PRIMARY KEY)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CHATINFO_TABLE);
    }

    @SuppressLint("Recycle")
    void addInfo(String doctor_name, String patient_name, String date, String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col0, doctor_name);
        contentValues.put(col1, patient_name);
        contentValues.put(col2, date);
        contentValues.put(col3, id);
        if (db.rawQuery("Select * from " + CHATINFO_TABLE + " WHERE " + col3 + " = ?", new String[]{id})
              .getCount() == 0) {
            db.insert(CHATINFO_TABLE, null, contentValues);
        } else {
            updateData(contentValues, id);
        }
    }


    @SuppressLint("Recycle")
    public void addInfo(ChatInfo info, String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col0, info.getDocuname());
        contentValues.put(col1, info.getPatuname());
        contentValues.put(col2, info.getDatecreated());
        contentValues.put(col3, id);
        if (db.rawQuery("Select * from " + CHATINFO_TABLE + " WHERE " + col3 + " = ?", new String[]{id})
              .getCount() == 0) {
            db.insert(CHATINFO_TABLE, null, contentValues);
        } else {
            updateData(contentValues, id);
        }
    }

    public Cursor getInfo(String id) {
        final SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("Select * from " + CHATINFO_TABLE + " WHERE " + col0 + " = ? OR " + col1 + " = ?",
                           new String[]{id, id});
    }

    void updateData(ContentValues contentValues, String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(CHATINFO_TABLE, contentValues, "ID = ?", new String[]{id});
    }
}

