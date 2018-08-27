package com.beens.test;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    TextToSpeech tts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},5);
            toast("순순히 권한을 넘기지 않으면, 음성 인식 기능을 사용할 수 없다.");
        }
        final TextView txt = findViewById(R.id.content);
        Button button = findViewById(R.id.recordBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputVoice(txt);
            }
        });
//        ScrollView scroll = new ScrollView(this);
//        setContentView(scroll);
//        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
//            @Override
//            public void onInit(int i) {
//                tts.setLanguage(Locale.KOREAN);
//            }
//        });
    }
    public void inputVoice(final TextView txt) {
        try {
            final Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
            final SpeechRecognizer stt = SpeechRecognizer.createSpeechRecognizer(this);
            stt.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle bundle) {
                    toast("음성 입력 준비완료!");
                }

                @Override
                public void onBeginningOfSpeech() {
                }

                @Override
                public void onRmsChanged(float v) {
                }

                @Override
                public void onBufferReceived(byte[] bytes) {
                    showLog("check");
                }

                @Override
                public void onEndOfSpeech() {
                    toast("음성 입력 종료!");
                }

                @Override
                public void onError(int error) {
                    toast("오류 발생 : " + error);
                    stt.destroy();
                }

                @Override
                public void onResults(Bundle results) {
                    ArrayList<String> result = (ArrayList<String>)results.get(SpeechRecognizer.RESULTS_RECOGNITION);
                    txt.append("[나] "+result.get(0)+"\n");
                    showLog(result.get(0));
                    replyAnswer(result.get(0), txt);
                    stt.destroy();
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
    private void replyAnswer(String input, TextView txt) {
        try {
            if(input.contains("강아지")) {
                txt.append("[어플] 멍멍멍~!!\n");
            }else if(input.contains("고양이")) {
                txt.append("[어플] 야아아아아아옹~!!\n");
            }else {
                txt.append("[어플] 뭐라는 거야....\n");
            }
        }catch (Exception e) {
            toast(e.toString());
        }
    }
}
