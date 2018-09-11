package com.beens;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Parcelable;
import android.speech.RecognizerIntent;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class SetTime extends AppCompatActivity {
    private DBHelper helper;
    private SQLiteDatabase db;
    private Cursor cursor;
    private Intent start_intent;
    private Intent end_intent;
    private PendingIntent startPendingIntent;
    private PendingIntent endPendingIntent;
    private TimePicker start_timePicker;
    private TimePicker end_timePicker;
    private Calendar Start_Calendar;
    private Calendar End_Calendar;
    private AlarmManager startAlarmManager;
    private AlarmManager endAlarmManager;
    private String start_time, end_time;
    private final int SET_TIME_RESULT = 500;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_time);
        helper = MainActivity.helper;
        db = helper.getWritableDatabase();
        start_intent = new Intent("com.beens.test.ALARM_START");
        start_intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        start_intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");

        end_intent = new Intent("com.beens.test.ALARM_STOP");
        end_intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        end_intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");

        startPendingIntent =
                PendingIntent.getBroadcast (
                        this,
                        0,
                        start_intent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );
        endPendingIntent =
                PendingIntent.getBroadcast (
                        this,
                        0,
                        end_intent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );

        Button button = findViewById(R.id.setTimeBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                Toast.makeText(getBaseContext(),"시간이 설정되었습니다.",Toast.LENGTH_LONG).show();
                start_timePicker = findViewById(R.id.Start_Time);
                end_timePicker = findViewById(R.id.End_Time);
                Start_Calendar = Calendar.getInstance();
                Start_Calendar.set(Calendar.HOUR_OF_DAY,start_timePicker.getHour());
                Start_Calendar.set(Calendar.MINUTE,start_timePicker.getMinute());
                Start_Calendar.set(Calendar.SECOND,00);
                start_time = start_timePicker.getHour() + ":" + start_timePicker.getMinute() + " ~ ";

                End_Calendar = Calendar.getInstance();
                End_Calendar.set(Calendar.HOUR_OF_DAY,end_timePicker.getHour());
                End_Calendar.set(Calendar.MINUTE,end_timePicker.getMinute());
                End_Calendar.set(Calendar.SECOND,00);
                end_time = end_timePicker.getHour() + ":" + end_timePicker.getMinute();

                startAlarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                startAlarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        Start_Calendar.getTimeInMillis(),
                        startPendingIntent
                );
                endAlarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                endAlarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        End_Calendar.getTimeInMillis(),
                        endPendingIntent
                );
                /** insert Alarm Data to DB **/
                ContentValues value = new ContentValues();
                value.put("time_from", start_time);
                value.put("time_to", end_time);
                db.insert("alarm_list", null, value);

                /** Set Result Intent **/
                Intent intent_result = getIntent();
                intent_result.putExtra("timeline", start_time + " ~ " + end_time);
                setResult(SET_TIME_RESULT,intent_result);
                finish();
            }
        });
    }
}
