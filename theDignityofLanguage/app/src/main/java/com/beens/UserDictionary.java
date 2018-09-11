package com.beens;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class UserDictionary extends AppCompatActivity {
    private ListView listView;
    private Button add_btn;
    private Button del_btn;
    private EditText editText;
    private DBHelper helper;
    private SQLiteDatabase db;
    private Cursor cursor;
    private ArrayAdapter<String> myadapter;
    private ArrayList<String> word_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dic);
        helper = MainActivity.helper;
        db = helper.getWritableDatabase();
        word_list = new ArrayList<>();
        set_list();
        listView = (ListView) findViewById(R.id.userdic_listview);
        myadapter = new ArrayAdapter<String>(getBaseContext(),
                android.R.layout.simple_list_item_single_choice, word_list);
        listView.setAdapter(myadapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE); // 하나의 항목만 선택할 수 있도록 설정

        /** Delete Button Action **/
        del_btn = (Button) findViewById(R.id.del_time_btn);
        del_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                editText = (EditText) findViewById(R.id.voca_input_txt);
                String word = editText.getText().toString().trim();
                int pos = listView.getCheckedItemPosition(); // 현재 선택된 항목의 첨자(위치값) 얻기
                if (pos != ListView.INVALID_POSITION) {      // 선택된 항목이 있으면
                    word_list.remove(pos);                       // items 리스트에서 해당 위치의 요소 제거
                    listView.clearChoices();                 // 선택 해제
                }else if(word != "") {
                    editText.setText("");
                    db.delete(Queries.getUserDicTableName(userID), "word=?", new String[] {word});
                    word_list.remove(word);
                }
                myadapter.notifyDataSetChanged();
            }
        });

        /** Add Button Action **/
        add_btn = (Button) findViewById(R.id.add_voca_btn);
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText = (EditText) findViewById(R.id.voca_input_txt);
                String newWord = editText.getText().toString().trim();
                editText.setText("");
                ContentValues value = new ContentValues();
                value.put("word", newWord);
                db.insert(Queries.getUserDicTableName(userID), null, value);
                word_list.add(newWord);
                myadapter.notifyDataSetChanged();
            }
        });
    }
    private void set_list() {
        TextView title = findViewById(R.id.userdic_title);
        cursor = db.query(Queries.getUserDicTableName(userID), null, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            title.setText("등록된 단어");
            do {
                String word = cursor.getString(0) ;
                word_list.add(word);
            } while (cursor.moveToNext());
        }else {
            title.setText("등록된 단어가 없습니다.");
        }
    }
}
