package com.olliesafety2.simpleapplocker.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

public class RestartService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Foreground", "restart" + "");
        try {
            if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

                Log.i("Broadcast Listened", "Service tried to stop");

                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
                    context.startService(new Intent(context, BackgroundServices.class));
                } else {
                    context.startForegroundService(new Intent(context, BackgroundServices.class));
                }

            }
        } catch (NullPointerException e) {
            Toast.makeText(context, "unexpected error occured please restart the app", Toast.LENGTH_LONG).show();
        }


        Log.i("Broadcast Listened", "Service tried to stop");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            Log.i("Foreground", "restarteddddddddddddddddddddddddddddddddddddddddddd" + "");

            context.startForegroundService(new Intent(context, BackgroundServices.class));
        } else {
            context.startService(new Intent(context, BackgroundServices.class));
        }


    }
}
