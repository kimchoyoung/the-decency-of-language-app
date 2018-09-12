package com.beens;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {
    private String new_name;
    private String new_email;
    private String new_pwd;
    private String new_pwdcheck;
    private Button new_signin_btn;
    private Button IDcheck_btn;
    private final String url=Server.url;
    private static final int ID_CHECK_MODE = 1000;
    private static final int SIGN_IN_MODE = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_preference);
        new_signin_btn = (Button) findViewById(R.id.new_signin_btn);
        IDcheck_btn = (Button) findViewById(R.id.ID_check_btn);

        new_signin_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                new_email = ((EditText) findViewById(R.id.new_email)).getText().toString();
                new_pwd = ((EditText) findViewById(R.id.new_pwd)).getText().toString();
                new_pwdcheck = ((EditText) findViewById(R.id.new_pwdcheck)).getText().toString();
                new_name = ((EditText) findViewById(R.id.new_name)).getText().toString();
                // if ( bool ID 중복확인 is true ) 추가..
                if(new_email.equals("") || new_pwd.equals("") || new_pwdcheck.equals("") || new_name.equals("")) {
                    Toast.makeText(SignUp.this, "입력정보가 부족합니다.", Toast.LENGTH_SHORT).show();
                }
                else if (!new_pwd.equals(new_pwdcheck)) {
                    Toast.makeText(SignUp.this,"비밀번호를 확인하세요.",Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent return_intent = new Intent();
                    return_intent.putExtra("name",new_name);
                    return_intent.putExtra("new_email", new_email);
                    return_intent.putExtra("new_pwd", new_pwd);
                    setResult(RESULT_OK, return_intent);
                    /**  send new data to server for registration **/
                    String query = "sign_up?name=\"" + new_name + "\"&u_id=\""+new_email+"\"&u_pwd="+new_pwd;
                    send_toServer(SIGN_IN_MODE, query);
                    finish();
                }
            }
        });
        IDcheck_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                new_name = ((EditText) findViewById(R.id.new_name)).getText().toString();
                new_email = ((EditText) findViewById(R.id.new_email)).getText().toString();
                if(new_name.length()<=0 || new_email.length()<=0)
                    Toast.makeText(getBaseContext(),"이름과 이메일 계정을 입력해 주세요.",Toast.LENGTH_LONG).show();
                else {
                    String query = "id_check?u_id=\""+new_email + "\"";

                    send_toServer(ID_CHECK_MODE, query);
                }
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void send_toServer(final int mode, String query){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd, hh:mm");
        String getTime = sdf.format(date);

        StringRequest request = new StringRequest(Request.Method.GET, url+ query+"&time="+getTime ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if(mode == ID_CHECK_MODE) {
                                if(response.equals("True"))
                                    Toast.makeText(getBaseContext(),"사용 가능한 아이디 입니다.",Toast.LENGTH_LONG).show();
                                else
                                    Toast.makeText(getBaseContext(),"이미 존재하는 계정입니다.",Toast.LENGTH_LONG).show();
                            }else {
                                Toast.makeText(getBaseContext(),"정상적으로 회원가입 되었습니다.",Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Log.d("SERVER_ERROR", "server is not connected");
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("time","11111");
                return params;
            }
        };
        request.setShouldCache(false);
        Volley.newRequestQueue(getBaseContext()).add(request);
    }
}
