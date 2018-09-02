package com.beens;

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

        /*
        유저의 욕설 단어와 시간을 기록, 시간은 기본으로 현재시각으로 저장
         */
        sql = "create table if not exists user_rec(" +
                "word text primary key,"
        +   "time timestamp default current_timestamp);";
        db.execSQL(sql); //for creating private dictionary

        /*
        유저정보를 기록, 이름 아이디 비밀번호
         */
        sql = "create table if not exists user_info(" +
                "name text,"
        +   "id text NOT NULL PRIMARY KEY,"
        +   "pw text);";
        db.execSQL(sql); //for creating private dictionary
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "drop table if exists dictionary";
        db.execSQL(sql);

        onCreate(db);
    }
}
