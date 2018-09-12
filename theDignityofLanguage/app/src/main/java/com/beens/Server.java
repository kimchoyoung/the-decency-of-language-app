package com.beens;

import android.annotation.TargetApi;
import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private final String BASE_PATH = Environment.getExternalStorageDirectory() + "/myapp";
    private final String NORMAL_PATH = BASE_PATH + "/normal";
    private String each_response = "";
    private Context context;
//    public static final String url="http://172.20.10.5:3000/";
    public static final String url="http://192.168.25.10:3000/";

    public Server(Context context) { this.context = context; }
    public void signin_process(final Context context, String user_name, String user_email, String user_pwd) {
        String query = "?user_name="+user_name+"&user_email="+user_email+"&user_pwd="+user_pwd;
        send_toServer(context, query);
    }
    public void id_check(final Context context, String user_email) {
        String query = "id_check?u_id=\""+user_email;
        send_toServer(context, query);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public String send_toServer(final Context context, String query){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String getTime = sdf.format(date);

        StringRequest request = new StringRequest(Request.Method.GET, url+ query ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            each_response = response;
                            if(response.equals("True")) {
                            }else {
                                Toast.makeText(context,"이메일 혹은 비밀번호가 일치하지 않습니다.",Toast.LENGTH_LONG).show();
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
        Volley.newRequestQueue(context).add(request);
        return each_response;
    }
    public boolean getEach_response() {
        return each_response.equals("True");
    }public String getQueryMakeUserRecTable(String userID) {
        String myQuery = "CREATE TABLE IF NOT EXISTS " + userID + "user_rec( word varchar(255), time timestamp default current_timestamp );";
        return myQuery;
    }

    public String getQueryInsertWord(String table_name, String word) {
        // INSERT INTO dictionary (word) SELECT * FROM (SELECT '해삼') AS tmp WHERE NOT EXISTS ( SELECT word FROM dictionary WHERE word = '해삼') LIMIT 1;
        String myQuery = "INSERT INTO " + table_name + " (word) SELECT * FROM (SELECT '" + word + "') AS tmp"
                + " WHERE NOT EXISTS ( SELECT word FROM " + table_name + " WHERE word = '" + word + "') LIMIT 1;";
        return myQuery;
    }
}
