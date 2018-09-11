package com.beens;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class AlarmStyle extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_style);

        final CheckBox cb1 = (CheckBox)findViewById(R.id.checkBox1);
        final CheckBox cb2 = (CheckBox)findViewById(R.id.checkBox2);
        final CheckBox cb3 = (CheckBox)findViewById(R.id.checkBox3);
        final CheckBox cb4 = (CheckBox)findViewById(R.id.checkBox4);
        final CheckBox cb5 = (CheckBox)findViewById(R.id.checkBox5);

        Button button = findViewById(R.id.checkSet_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cb1.isChecked() == true) TestService.none_isChecked = true;
                else  TestService.none_isChecked = false;
                if(cb2.isChecked() == true) TestService.toast_isChecked = true;
                else TestService.toast_isChecked = false;
                if(cb3.isChecked() == true) TestService.sound_isChecked = true;
                else TestService.sound_isChecked = false;
                if(cb4.isChecked() == true) TestService.vibrate_isChecked = true;
                else TestService.vibrate_isChecked = false;
                if(cb5.isChecked() == true) TestService.notific_isChecked = true;
                else TestService.notific_isChecked = false;
                Toast.makeText(getBaseContext(), "알람방식이 설정되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }
}
