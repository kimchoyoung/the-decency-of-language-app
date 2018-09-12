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
}
