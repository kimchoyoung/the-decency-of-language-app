package com.beens;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Queries {
    public void Insert(SQLiteDatabase db, DBHelper helper, String table, String word){
        // table names: dictionary, user_dic, user_rec

        db = helper.getWritableDatabase(); // sqLiteDatabase 객체를 얻어온다. 쓰기 가능
        ContentValues values = new ContentValues();

        values.put("words", word);
        db.insert("dictionary", null, values);
    }


    /*
    * select(): To figure out that Database contains a certain word. ( it's slang )
    * if exists, return 1.
    * else return 0
    * */

    public int select(SQLiteDatabase db, DBHelper helper, String table,String word) {
        db = helper.getReadableDatabase();

        //Cursor c = sqLiteDatabase.query(table,null,null,null,null,null,null,null);
        Cursor c = db.rawQuery("select exists (select 1 from dictionary where words=\""+word+"\")", null);
        int result=0;

        while (c.moveToNext()) {
            String data = c.getString(c.getColumnIndex("exists (select 1 from dictionary where words=\"" + word + "\")"));
            Log.i("sqLiteDatabase", "words: " + data);

            result=Integer.parseInt(data);
        }
        return result;
    }

    public String getQueryMakeUserRecTable(String userID) {
        String myQuery = "CREATE TABLE IF NOT EXISTS " + userID + "user_rec( word varchar(255), time timestamp default current_timestamp );";
        return myQuery;
    }

    public String getQueryInsertWord(String table_name, String word) {
        // INSERT INTO dictionary (word) SELECT * FROM (SELECT '해삼') AS tmp WHERE NOT EXISTS ( SELECT word FROM dictionary WHERE word = '해삼') LIMIT 1;
        String myQuery = "INSERT INTO " + table_name + " (word) SELECT * FROM (SELECT '" + word + "') AS tmp"
                + " WHERE NOT EXISTS ( SELECT word FROM " + table_name + " WHERE word = '" + word + "') LIMIT 1;";
        return myQuery;
    }

    private static final String USERDIC = "_user_dic";
    public static String getUserDicTableName(String userID) {
        String UserAlarmListTableName = userID + USERDIC;
        return UserAlarmListTableName;
    }
    public static String getQueryCreateUserDicTable(String userID) {
        String myQuery = "create table if not exists " + getUserDicTableName(userID) + "("
                + " word varchar(255) PRIMARY KEY );";
        return myQuery;
    }

    private static final String USERREC = "_user_rec";
    public static String getUserRecTableName(String userID) {
        String UserAlarmListTableName = userID + USERREC;
        return UserAlarmListTableName;
    }
    public static String getQueryCreateUserRecTable(String userID) {
        String myQuery = "create table if not exists " + getUserRecTableName(userID) + "("
                + " word varchar(255), "
                + " time datetime default (datetime('now', 'localtime'))"
                + " );";
        return myQuery;
    }

    private static final String ALARMLIST = "_alarm_list";
    public static String getUserAlarmListTableName(String userID) {
        String UserAlarmListTableName = userID + ALARMLIST;
        return UserAlarmListTableName;
    }

    public static String getQueryCreateUserAlarmListTable(String userID) {
        String myQuery = "create table if not exists " + getUserAlarmListTableName(userID) + "("
                + " time_from varchar(255) PRIMARY KEY, "
                + " time_to varchar(255));";
        return myQuery;
    }
}
