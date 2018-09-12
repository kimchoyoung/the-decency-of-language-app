package com.beens;

import android.Manifest;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences  myPreferences;
    private SharedPreferences.Editor editor;
    public static final int REQUEST_CODE_LOGIN = 1000;
    public static final int REQUEST_CODE_ALARMLIST = 2000;
    public static final int REQUEST_CODE_ALARMSTYLE = 3000;
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
    private checkVersion checkVersion;

    public static ViewPager viewPager;
    public static ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = myPreferences.edit();
        query=new Queries();
        helper = new DBHelper(MainActivity.this, "lang.sqLiteDatabase", null, 1);
        sqLiteDatabase = helper.getWritableDatabase();
        helper.onCreate(sqLiteDatabase);
//        if(!myPreferences.getBoolean("DB_setting",false)) {
            String query = "update";
            send_toServer(query);
//        }

        /** Set Permission **/
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},5);
            Toast.makeText(this,"순순히 권한을 넘기지 않으면, 음성 인식 기능을 사용할 수 없다.", Toast.LENGTH_LONG).show();
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
        //actionBarDrawerToggle = setUpActionBarToggle();
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
                        Toast.makeText(MainActivity.this,"3rd nav",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_4th:
                        Intent intent = new Intent(MainActivity.this,UserDictionary.class);
                        startActivity(intent);
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
            if (intent.getBooleanExtra("setting_state", true) == false) {
                Intent intent_setTime = new Intent(getApplicationContext(), SetTime.class);
                startActivityForResult(intent_setTime, REQUEST_CODE_ALARMLIST);
            }
            Toast.makeText(getBaseContext(), "User setting is done", Toast.LENGTH_LONG).show();
            editor.putBoolean("setting", true);
            editor.commit();
            String userinfo = intent.getStringExtra("user_email").toString() + "\n" + intent.getStringExtra("user_pwd").toString();
            ((TextView) findViewById(R.id.user_info)).setText(userinfo);

            viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
            viewPager = (ViewPager) findViewById(R.id.ViewPager);
            viewPager.setAdapter(viewPagerAdapter);
        }
    }
    public void send_toServer(String query){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        Log.i("[url]",url);
        StringRequest request = new StringRequest(Request.Method.GET, url+ query ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if(checkVersion.send_toServer(getBaseContext()))
                                setSQLite(response);
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
}
