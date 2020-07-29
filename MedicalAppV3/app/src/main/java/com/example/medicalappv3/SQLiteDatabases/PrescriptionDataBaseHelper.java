package com.example.medicalappv3.SQLiteDatabases;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.medicalappv3.UserDefinedClasses.PrescriptionData;


public class PrescriptionDataBaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "prescrip.db";
    private static final String PRESCRIP_TABLE = "prescription_table";
    private static final String col0 = "DOCTOR_NAME";
    private static final String col1 = "DATE";
    private static final String col2 = "DATA";
    private static final String col3 = "PATIENT_ID";
    private static final String col4 = "TOPIC";
    private static final String col5 = "ID";

    public PrescriptionDataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table IF NOT EXISTS " + PRESCRIP_TABLE + " (DOCTOR_NAME TEXT , DATE TEXT , DATA TEXT , PATIENT_ID TEXT , TOPIC TEXT, ID TEXT PRIMARY KEY)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PRESCRIP_TABLE);
    }

    @SuppressLint("Recycle")
    void addPrescription(String doctor_name, String date, String data, String patient_id, String topic, String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col0, doctor_name);
        contentValues.put(col1, date);
        contentValues.put(col2, data);
        contentValues.put(col3, patient_id);
        contentValues.put(col4, topic);
        contentValues.put(col5, id);
        if (db.rawQuery("Select * from " + PRESCRIP_TABLE + " WHERE " + col5 + " = ?", new String[]{id})
              .getCount() == 0) {
            db.insert(PRESCRIP_TABLE, null, contentValues);
        }
    }


    @SuppressLint("Recycle")
    public void addPrescription(PrescriptionData info, String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col0, info.getDOCTOR_NAME());
        contentValues.put(col1, info.getDATE());
        contentValues.put(col2, info.getDATA());
        contentValues.put(col3, info.getPATIENT_ID());
        contentValues.put(col4, info.getTOPIC());
        contentValues.put(col5, id);
        if (db.rawQuery("Select * from " + PRESCRIP_TABLE + " WHERE " + col5 + " = ?", new String[]{id})
              .getCount() == 0) {
            db.insert(PRESCRIP_TABLE, null, contentValues);
        }
    }

    public Cursor getPrescription(String myid) {
        final SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("Select * from " + PRESCRIP_TABLE + " WHERE " + col3 + " = ?", new String[]{myid});
    }
}

