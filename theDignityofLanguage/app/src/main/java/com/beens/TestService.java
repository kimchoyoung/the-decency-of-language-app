package com.beens;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class TestService extends Service {
    final SpeechRecognizer stt = SpeechRecognizer.createSpeechRecognizer(this);
    private Intent myintent;
    private DBHelper helper;
    private SQLiteDatabase db;
    private Cursor cursor1, cursor2;
    private Intent intent;
    public static boolean none_isChecked;
    public static boolean toast_isChecked;
    public static boolean sound_isChecked;
    public static boolean vibrate_isChecked;
    public static boolean notific_isChecked;

    public TestService() {
        helper = MainActivity.helper;
        db = helper.getWritableDatabase();
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onDestroy() {
        stt.stopListening();
        stt.destroy();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.intent = intent;
        Toast.makeText(this, "음성인식을 시작합니다.", Toast.LENGTH_SHORT).show();
        inputVoice(intent);
        return START_NOT_STICKY;
    }
    public void inputVoice(Intent intent) {
        myintent = intent;
        try {
            stt.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle bundle) {
//                    toast("음성 입력 준비완료!" );
                }
                @Override
                public void onBeginningOfSpeech() { }
                @Override
                public void onRmsChanged(float v) { }
                @Override
                public void onBufferReceived(byte[] bytes) { }
                @Override
                public void onEndOfSpeech() {
//                    toast("음성 입력 종료!");
                }
                @Override
                public void onError(int error) {
//                    toast("오류 발생 : " + error);
                    stt.destroy();
                    inputVoice(myintent);
                }

                @Override
                public void onResults(Bundle results) {
                    ArrayList<String> result = (ArrayList<String>)results.get(SpeechRecognizer.RESULTS_RECOGNITION);
                    search_slang(result.get(0));

                    stt.stopListening();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            // yourMethod();
                        }
                    }, 5000);   //5 seconds
                    stt.startListening(myintent);
                }

                @Override
                public void onPartialResults(Bundle bundle) {
                    showLog("partial result");
                }

                @Override
                public void onEvent(int i, Bundle bundle) {
                    toast("이벤트");
                }
            });
            stt.startListening(intent);

        }catch (Exception e) {
            toast(e.toString());
        }
    }
    public void showLog(String log) {
        Log.d("[myLOG]", log);
    }
    public void toast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
    private void search_slang(String sentence) {
        try {
            String[] words = sentence.split(" ");
            for(String word : words) {
                for(int i=0; i<3 && word.length() > 0; ++i) {
//                    cursor1 = db.query("user_dic", null, null, null, null, null, null, null);
//                    cursor2 = db.query("dictionary", null, null, null, null, null, null, null);
                    cursor1 = db.query("user_dic", null, "word=?", new String[] {word}, null, null, null, null);
                    cursor2 = db.query("dictionary", null, "word=?", new String[] {word}, null, null, null, null);
                    if(cursor1.moveToFirst()) {
                        ContentValues record = new ContentValues();
                        record.put("word", cursor1.getString(0));
                        db.insert("user_rec", null, record);
                        MainActivity.viewPager.setAdapter(MainActivity.viewPagerAdapter);
                        makeAlarm();
                        break;
                    }else if(cursor2.moveToFirst()) {
                        ContentValues record = new ContentValues();
                        record.put("word", cursor2.getString(0));
                        db.insert("user_rec", null, record);
                        makeAlarm();
                        break;
                    }else {
                    }
                    word = word.substring(0, word.length()-1);
                }
            }
        }catch (Exception e) {
            toast(e.toString());
        }
    }
    public void makeAlarm() {
        Log.d("ALARM_CHECK", Boolean.toString(none_isChecked) );
        if(none_isChecked == false) {
            if(toast_isChecked){
                Toast.makeText(getApplicationContext(), "비속어를 사용하셨습니다.", Toast.LENGTH_SHORT).show();
            } if(sound_isChecked) {
                Uri ringtoneUri =  RingtoneManager.getActualDefaultRingtoneUri(getApplicationContext(),RingtoneManager.TYPE_NOTIFICATION);
                Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), ringtoneUri);
                ringtone.play();
            } if(vibrate_isChecked) {
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(1500);
            } if(notific_isChecked) {
                // TODO : 여기 채워라..
            }
        }
    }
}
