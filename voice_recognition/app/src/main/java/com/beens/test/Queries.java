package com.beens;

/**
 * Created by ch02 on 2018. 8. 30..
 */

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class Queries {
    public void Insert(SQLiteDatabase db, DBHelper helper, String table, String word){
        // table names: dictionary, user_dic, user_rec

        db = helper.getWritableDatabase(); // db 객체를 얻어온다. 쓰기 가능
        ContentValues values = new ContentValues();

        values.put("words", word);
        db.insert("dictionary", null, values);
    }

    /*
    디비, 테이블, 칼럼, 데이터를 주면 거기에 레코드를 추가
     */
    public void Insert(SQLiteDatabase db, String table, String column, String data) {
        db = helper.getWritableDatabase(); // db 객체를 얻어온다. 쓰기 가능
        ContentValues values = new ContentValues();
        
        values.put(column, data);
        db.insert(table, null, values);
    }

    /*
     디비, 테이블, 조건, 데이터를 주면 그 데이터를 제거
     e.g. ) db.delete("student", "name=?", new String[]{"홍길동"});
            디비.delete(테이블, 조건, 데이터);
     */
    public void delete(SQLiteDatabase db, String table, String cond, String[] data) {
        db.delete(table, cond, data);
    }

    /*
    * select(): To figure out that Database contains a certain word. ( it's slang )
    * if exists, return 1.
    * else return 0
    * */
    public int select(SQLiteDatabase db, DBHelper helper, String table,String word) {
        db = helper.getReadableDatabase();

        //Cursor c = db.query(table,null,null,null,null,null,null,null);
        Cursor c = db.rawQuery("select exists (select 1 from dictionary where words=\""+word+"\")", null);
        int result=0;

        while (c.moveToNext()) {
            String data = c.getString(c.getColumnIndex("exists (select 1 from dictionary where words=\"" + word + "\")"));
            Log.i("db", "words: " + data);

            result=Integer.parseInt(data);
        }
        return result;
    }

    /*
    유저 욕설 기록에서 특정 시간 범위 안의 레코드를 모아주는 함수
    다만, having 부분을 저렇게 해야하는지는 테스트 안해봄.
     */
    public Cursor statisticsQuery(DBHelper helper, String begin, String end) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query("user_rec", null, null,
                null, "word", "time >= " + begin + " AND time <= " + end, null, null);
        return c;
    }
}
