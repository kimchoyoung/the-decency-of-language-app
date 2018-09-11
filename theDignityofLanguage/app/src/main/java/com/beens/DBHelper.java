package com.beens;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DBHelper extends SQLiteOpenHelper{
    private int dic_version;
    private Context context;
    private SQLiteDatabase db;

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
        // Set user dictionary
        String sql = "create table if not exists user_dic("
                + " word varchar(255) PRIMARY KEY );";

        db.execSQL(sql);
        // Set user Record
        sql = "create table if not exists user_rec("
                + " word varchar(255), "
                + " time datetime default (datetime('now', 'localtime'))"
                + " );";
        db.execSQL(sql);
        // Set Alarm List
        sql = "create table if not exists alarm_list("
                + " time_from varchar(255) PRIMARY KEY, "
                + " time_to varchar(255));";
        db.execSQL(sql);
        // Set dictionary
        sql = "create table if not exists dictionary("
                + " word varchar(255) PRIMARY KEY );";

        db.execSQL(sql);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "drop table if exists user_dic";
        db.execSQL(sql);
        sql = "drop table if exists user_rec";
        db.execSQL(sql);
        onCreate(db);
    }
}