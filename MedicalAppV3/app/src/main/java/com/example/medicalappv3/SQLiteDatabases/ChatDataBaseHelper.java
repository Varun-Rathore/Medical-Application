package com.example.medicalappv3.SQLiteDatabases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.MediaPlayer;

import com.example.medicalappv3.R;
import com.example.medicalappv3.UserDefinedClasses.ActivatedUser;
import com.example.medicalappv3.UserDefinedClasses.MessageInfo;


public class ChatDataBaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "chat.db";
    private static final String CHAT_TABLE = "chat_table";
    private static final String col0 = "SENDER";
    private static final String col1 = "RECEIVER";
    private static final String col2 = "MESSAGE";
    private static final String col3 = "ID";
    private final MediaPlayer mp;

    public ChatDataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.mp = MediaPlayer.create(context, R.raw.sound);
        mp.setVolume(1.0f, 1.0f);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table IF NOT EXISTS " + CHAT_TABLE + " (SENDER TEXT ,RECEIVER TEXT,MESSAGE TEXT,ID TEXT PRIMARY KEY)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CHAT_TABLE);
    }

    void addMessage(String sender, String receiver, String message, String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col0, sender);
        contentValues.put(col1, receiver);
        contentValues.put(col2, message);
        contentValues.put(col3, id);
        if (ActivatedUser.getActivated() != null) { if (ActivatedUser.getActivated().equals(sender)) { mp.start(); } }
        db.insert(CHAT_TABLE, null, contentValues);
    }

    public void addMessage(MessageInfo info, String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col0, info.getSender());
        contentValues.put(col1, info.getReceiver());
        contentValues.put(col2, info.getMessage());
        contentValues.put(col3, id);
        if (ActivatedUser.getActivated() != null) {
            if (ActivatedUser.getActivated().equals(info.getSender())) { mp.start(); }
        }
        db.insert(CHAT_TABLE, null, contentValues);
    }

    public Cursor getMessage(String userId, String myid) {
        final SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery(
                "Select * from " + CHAT_TABLE + " WHERE (" + col0 + " = ? AND " + col1 + " = ? ) OR (" + col0 + " = ? AND " + col1 + " = ? )",
                new String[]{userId, myid, myid, userId});
    }
}

