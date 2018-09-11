package com.beens;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class checkVersion extends Activity{
    int version=1;
    private final String url = Server.url;
    String query="/version?version="+version;
    boolean result=false;

    public boolean send_toServer(Context context){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        Log.i("[url]",url);

        StringRequest request = new StringRequest(Request.Method.GET, url+ query ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            result= (response=="true"); // 업데이트가 필요하다
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
                return params;
            }
        };
        request.setShouldCache(false);
        Volley.newRequestQueue(this).add(request);
        return result;
    }
}
