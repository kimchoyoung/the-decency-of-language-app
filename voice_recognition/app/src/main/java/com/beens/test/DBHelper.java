package org.androidtown.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{
    public DBHelper(Context context, String name,
                              SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table if not exists dictionary (" +
                "words text primary key);";
        db.execSQL(sql); //for creating public dictionary

        sql = "create table if not exists user_dic(" +
                "word text primary key;";
        db.execSQL(sql); //for creating private dictionary

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "drop table if exists dictionary";
        db.execSQL(sql);

        onCreate(db);
    }
}
