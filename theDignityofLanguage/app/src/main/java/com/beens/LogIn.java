package com.beens;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
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

public class LogIn extends Activity {
    public static final int SIGNIN_REQUEST_CODE = 99;
    public static final int LOGIN_RESULT_CODE = 10;
    public static final int GUEST_LOGIN_RESULT_CODE = 11;
    private Intent resultIntent;
    private final String url = Server.url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button login_btn = findViewById(R.id.login_btn);
        Button signin_btn = findViewById(R.id.signin_btn);
        Button guest_btn = findViewById(R.id.guest_btn);
        resultIntent = getIntent();

        /** set action when user long in **/
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_email = ((EditText)findViewById(R.id.emailInput)).getText().toString();
                String user_pwd = ((EditText)findViewById(R.id.passwordInput)).getText().toString();
                resultIntent.putExtra("user_email", user_email);
                resultIntent.putExtra("user_pwd", user_pwd);
                setResult(RESULT_OK, resultIntent);
                // Send data to server for checking identification
                String login_query = "login?u_id=\""+user_email+"\"&u_pwd="+user_pwd;
                send_toServer(login_query);
            }
        });
        /** set action when user sign in **/
        signin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signinIntent = new Intent(LogIn.this, SignUp.class);
                startActivityForResult(signinIntent,SIGNIN_REQUEST_CODE);
            }
        });
        /** set action when user is a guest **/
        guest_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LogIn.this );
                builder.setTitle("GUEST 로그인");
                builder.setMessage("GUEST모드 사용시 일부 기능 사용에 제한이 있습니다.");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(LogIn.this,"GUEST로그인 상태입니다.",Toast.LENGTH_SHORT).show();
                        resultIntent.putExtra("user_email","GUEST");
                        resultIntent.putExtra("user_pwd","");
                        resultIntent.putExtra("setting_state", false);
                        setResult(GUEST_LOGIN_RESULT_CODE,resultIntent);
                        finish();
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                builder.show();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(resultCode == RESULT_OK) {
            if (requestCode == SIGNIN_REQUEST_CODE) {
                String new_email = intent.getStringExtra("new_email");
                String new_pwd = intent.getStringExtra("new_pwd");
                ((EditText)findViewById(R.id.emailInput)).setText(new_email);
                ((EditText)findViewById(R.id.passwordInput)).setText(new_pwd);
                resultIntent.putExtra("setting_state", false);
            }
        }
    }
    @TargetApi(Build.VERSION_CODES.N)
    public void send_toServer(String query){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd, hh:mm");
        String getTime = sdf.format(date);
        Log.i("[url]",url);
        StringRequest request = new StringRequest(Request.Method.GET, url+ query ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if(response.equals("True")) {
                                finish();
                            }else {
                                Toast.makeText(getBaseContext(),"이메일 혹은 비밀번호가 일치하지 않습니다.",Toast.LENGTH_LONG).show();
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
