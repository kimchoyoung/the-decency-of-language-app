package com.beens;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StartReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent mServiceintent = new Intent(context,TestService.class);
        context.startService(mServiceintent);
        Log.d("START", "The alarm is succesfully started");
    }
}
