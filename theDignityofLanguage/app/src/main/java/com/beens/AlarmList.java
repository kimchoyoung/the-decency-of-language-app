package com.beens;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AlarmList extends AppCompatActivity {
    private ListView listView;
    private final int REQUEST_CODE_TIMESET = 100;
    private ArrayAdapter<String> myadapter;
    private ArrayList<String> time_list;

    private DBHelper helper;
    private SQLiteDatabase db;
    private Cursor cursor;

    private Button add_btn;
    private Button del_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);
        helper = MainActivity.helper;
        db = helper.getWritableDatabase();
        time_list = new ArrayList<>();
        set_list();

        listView = findViewById(R.id.alarm_listview);
        myadapter = new ArrayAdapter<String>(getBaseContext(),
                android.R.layout.simple_list_item_single_choice, time_list);
        listView.setAdapter(myadapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE); // 하나의 항목만 선택할 수 있도록 설정

        add_btn = findViewById(R.id.add_time_btn);
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SetTime.class);
                startActivityForResult(intent, REQUEST_CODE_TIMESET);
            }
        });

        del_btn = findViewById(R.id.del_time_btn);
        del_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = listView.getCheckedItemPosition(); // 현재 선택된 항목의 첨자(위치값) 얻기
                if (pos != ListView.INVALID_POSITION) {      // 선택된 항목이 있으면
                    String timeline = time_list.get(pos);
                    timeline = timeline.split(" ~ ")[0];
                    time_list.remove(pos);                       // items 리스트에서 해당 위치의 요소 제거
                    db.delete(Queries.getUserAlarmListTableName(userID), "time_from=?", new String[] {timeline});
                    listView.clearChoices();                 // 선택 해제
                }
                myadapter.notifyDataSetChanged();
            }
        });
    }
    private void set_list() {
        TextView title = findViewById(R.id.userdic_title);
        cursor = db.query(Queries.getUserAlarmListTableName(userID), null, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String timeline = cursor.getString(0) ;
                timeline += cursor.getString(1) ;
                time_list.add(timeline);
            } while (cursor.moveToNext());
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        /** Result of Setting **/
        if(requestCode == REQUEST_CODE_TIMESET) {
            time_list.add(intent.getStringExtra("timeline"));
            myadapter.notifyDataSetChanged();
        }
    }
}
