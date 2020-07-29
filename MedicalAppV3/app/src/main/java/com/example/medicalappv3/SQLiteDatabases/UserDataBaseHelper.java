package com.example.medicalappv3.SQLiteDatabases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.medicalappv3.UserDefinedClasses.UserData;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class UserDataBaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Medical.db";
    private static final String USER_TABLE = "user_table";
    private static final String col0 = "USERNAME";
    private static final String col1 = "PASSWORD";
    private static final String col2 = "NAME";
    private static final String col3 = "OCCUPATION";
    private static final String col4 = "MOBILE";
    private static final String col5 = "AGE";
    private static final String col6 = "GENDER";
    private static final String col7 = "EMAIL";

    public UserDataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table IF NOT EXISTS " + USER_TABLE + " (USERNAME TEXT PRIMARY KEY,PASSWORD TEXT,NAME TEXT,OCCUPATION TEXT,MOBILE TEXT," +
                "AGE TEXT,GENDER TEXT, EMAIL TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
    }

    public void addUser(String username, String password, String fullname, String occupation, String mob, String Age,
                        String gender, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        ContentValues contentValues = new ContentValues();
        contentValues.put(col0, username);
        contentValues.put(col1, password);
        contentValues.put(col2, fullname);
        contentValues.put(col3, occupation);
        contentValues.put(col4, mob);
        contentValues.put(col5, Age);
        contentValues.put(col6, gender);
        contentValues.put(col7, email);
        db.insert(USER_TABLE, null, contentValues);
        UserData data = new UserData(username, null, fullname, occupation, mob, Age, gender, email);
        db.execSQL("delete from " + USER_TABLE);
        db.insert(USER_TABLE, null, contentValues);
        databaseReference.child(username).setValue(data);
    }

    public void addUser(UserData userData) {
        SQLiteDatabase db = this.getWritableDatabase();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        ContentValues contentValues = new ContentValues();
        contentValues.put(col0, userData.getUSERNAME());
        contentValues.put(col1, userData.getPASSWORD());
        contentValues.put(col2, userData.getNAME());
        contentValues.put(col3, userData.getOCCUPATION());
        contentValues.put(col4, userData.getMOBILE());
        contentValues.put(col5, userData.getAGE());
        contentValues.put(col6, userData.getGENDER());
        contentValues.put(col7, userData.getEMAIL());
        UserData data = new UserData(userData);
        data.setPASSWORD(null);
        db.execSQL("delete from " + USER_TABLE);
        db.insert(USER_TABLE, null, contentValues);
        databaseReference.child(userData.getUSERNAME()).setValue(data);
    }

    public Cursor getUser() {
        final SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("Select * from " + USER_TABLE, null);
    }

    public void updateData(String username, String password, String mob, String Age, String gender, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col1, password);
        contentValues.put(col4, mob);
        contentValues.put(col5, Age);
        contentValues.put(col6, gender);
        contentValues.put(col7, email);
        db.update(USER_TABLE, contentValues, "USERNAME = ?", new String[]{username});
    }

    public void updateRegular(UserData userData) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col2, userData.getNAME());
        contentValues.put(col3, userData.getOCCUPATION());
        contentValues.put(col4, userData.getMOBILE());
        contentValues.put(col5, userData.getAGE());
        contentValues.put(col6, userData.getGENDER());
        contentValues.put(col7, userData.getEMAIL());
        db.update(USER_TABLE, contentValues, "USERNAME = ?", new String[]{userData.getUSERNAME()});
    }

    void updateAccount(UserData userData) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col1, userData.getPASSWORD());
        contentValues.put(col2, userData.getNAME());
        contentValues.put(col3, userData.getOCCUPATION());
        contentValues.put(col4, userData.getMOBILE());
        contentValues.put(col5, userData.getAGE());
        contentValues.put(col6, userData.getGENDER());
        contentValues.put(col7, userData.getEMAIL());
        db.update(USER_TABLE, contentValues, "USERNAME = ?", new String[]{userData.getUSERNAME()});
    }

    public void logout() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + USER_TABLE);
    }
}
