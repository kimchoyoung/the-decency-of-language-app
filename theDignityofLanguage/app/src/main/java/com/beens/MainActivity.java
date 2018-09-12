package com.beens;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences  myPreferences;
    private SharedPreferences.Editor editor;
    public static final int REQUEST_CODE_LOGIN = 1000;
    public static final int REQUEST_CODE_ALARMLIST = 2000;
    public static final int REQUEST_CODE_ALARMSTYLE = 3000;
    public static final int REQUEST_DB_VERSION = 100;
    public static final int REQUEST_DB_UPDATE = 200;

    private int requestCode = 0;
    private int resultCode = 0;

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ImageView imgshownav;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    public static DBHelper helper;
    public static SQLiteDatabase sqLiteDatabase;
    private Queries query;
    private final String url = Server.url;

    public static ViewPager viewPager;
    public static ViewPagerAdapter viewPagerAdapter;
    public static Spinner spinner;
    private TextView nav_head_useremail;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = myPreferences.edit();
        query=new Queries();
        helper = new DBHelper(MainActivity.this, "lang.sqLiteDatabase", null, 1);
        sqLiteDatabase = helper.getWritableDatabase();
        helper.onCreate(sqLiteDatabase);

        /** Set Permission **/
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},5);
            Toast.makeText(this,"순순히 권한을 넘기지 않으면, 음성 인식 기능을 사용할 수 없다.", Toast.LENGTH_LONG).show();
            send_toServer(REQUEST_DB_VERSION, "version_check");
            Log.d("DB_SERVER", "최초: 서버로 버전확인 커리를 보냅니다.");
        }
        editor.putString("user_email", null);

//      if(!myPreferences.getBoolean("setting",false) || myPreferences.getString("user_email", null)==null) {
        Intent intent_login = new Intent(getApplicationContext(), LogIn.class);
        startActivityForResult(intent_login, REQUEST_CODE_LOGIN);
//      }
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbarinclude);
        setSupportActionBar(toolbar);

        imgshownav = (ImageView) toolbar.findViewById(R.id.imgShowNavigationDrawer);
        imgshownav.setOnClickListener(onClickListener);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.drawable.ic_menu, R.drawable.ic_menu);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        navigationView = (NavigationView) findViewById(R.id.navigationView);
        setUpDrawerContent(navigationView);
    }
    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState,persistentState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newconfig) {
        super.onConfigurationChanged(newconfig);
        actionBarDrawerToggle.onConfigurationChanged(newconfig);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imgShowNavigationDrawer:
                    drawerLayout.openDrawer(GravityCompat.START);
                    break;
            }
        }
    };

    private void setUpDrawerContent(NavigationView navigationView) {
        nav_head_useremail = navigationView.getHeaderView(0).findViewById(R.id.nav_head_useremail);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_1st:
                        Intent intent_setTime = new Intent(getApplicationContext(), AlarmList.class);
                        startActivityForResult(intent_setTime, REQUEST_CODE_ALARMLIST);
                        break;
                    case R.id.nav_2nd:
                        Intent intent_alarmstyle = new Intent(getApplicationContext(), AlarmStyle.class);
                        startActivityForResult(intent_alarmstyle, REQUEST_CODE_ALARMSTYLE);
                        break;
                    case R.id.nav_3rd:
                        Intent intent = new Intent(MainActivity.this,UserDictionary.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_4th:
                        Intent intent2 = new Intent(getApplicationContext(), SplashActivity.class);
                        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent2);
                        break;
                }
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        this.requestCode = requestCode;
        this.resultCode = resultCode;
        /** Result of Setting **/
        if(requestCode == REQUEST_CODE_LOGIN) {
            Log.d("SETTING_ERROR", "내용" + intent.getBooleanExtra("setting_state", true));
            if (intent.getBooleanExtra("setting_state", true) == false) {
                Intent intent_setTime = new Intent(getApplicationContext(), SetTime.class);
                startActivityForResult(intent_setTime, REQUEST_CODE_ALARMLIST);
            }
            Toast.makeText(getBaseContext(), "User setting is done", Toast.LENGTH_LONG).show();
            editor.putBoolean("setting", true);
            editor.commit();

            viewPager = (ViewPager) findViewById(R.id.ViewPager);
            spinner = (Spinner) findViewById(R.id.spinner);
            viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

            /** Check the DB version **/
            send_toServer(REQUEST_DB_VERSION, "version_check");
            Log.d("DB_SERVER", "서버로 버전확인 커리를 보냅니다.");
            setspinner();
        }
        String userinfo = intent.getStringExtra("user_email");
        nav_head_useremail.setText(userinfo);
    }
    public void send_toServer(final int mode, String query){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        Log.i("[url]",url);
        StringRequest request = new StringRequest(Request.Method.GET, url+ query ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if(mode == REQUEST_DB_VERSION) {
                                Log.d("DB_SERVER", "데이터베이스 버전을 확인합니다. : " + response);
                                if(!response.equals(myPreferences.getString("DB_version","0"))){
                                    editor.putString("DB_version", response);
                                    send_toServer(REQUEST_DB_UPDATE, "update");
                                }
                            }
                            else if(mode == REQUEST_DB_UPDATE) {
                                Log.d("DB_SERVER", "데이터베이스를 업데이트 합니다.");
                                setSQLite(response);
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
    private void setSQLite(String response) {
        String[] results = response.split(",");
        for(String part: results) {
            String word = (part.split(":")[1]).split("\"")[1];

            ContentValues value = new ContentValues();
            value.put("word", word);
            sqLiteDatabase.insertWithOnConflict("dictionary", null, value, SQLiteDatabase.CONFLICT_IGNORE);
            editor.putBoolean("DB_setting", true);
        }
    }
    public void setspinner() {
        ArrayList<String> spinnerlist;
        spinnerlist = new ArrayList<String>();

        spinnerlist.add("일주");
        spinnerlist.add("한달");
        spinnerlist.add("석달");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,spinnerlist);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                viewPager.setAdapter(viewPagerAdapter);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
}
