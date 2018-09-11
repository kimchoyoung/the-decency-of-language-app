package com.beens;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class EndReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent mServiceintent = new Intent(context,TestService.class);
        context.stopService(mServiceintent);
        Log.d("END", "The alarm is succesfully ended");
    }
}
