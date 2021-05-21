package com.olliesafety2.simpleapplocker.Phone_Call_Files;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;

import com.android.internal.telephony.ITelephony;
import com.olliesafety2.simpleapplocker.services.BackgroundServices;

public class CallRejection extends BroadcastReceiver {
    private String number, number2;
    BackgroundServices backgroundServices = new BackgroundServices();
phoneState pstate;
    @Override
    public void onReceive(Context context, final Intent intent) {

        number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

        if (backgroundServices.getSpeed() >= 20 && backgroundServices.getBluetooth_status() == "NotConnected") {
            disconnectPhoneItelephony(context);
//            if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
//                Toast.makeText(context, "In Process ", Toast.LENGTH_LONG).show();
//            }
        }
    }

    public void sendSMS(Context context) {

        try {
//            SmsManager smgr = SmsManager.getDefault();
//            smgr.sendTextMessage(number, null, "Sorry I'm driving at the moment and Ollie Safety is preventing me from answering my phone.", null, null);
            SmsManager sms = SmsManager.getDefault();
            ArrayList<String> parts = sms.divideMessage("Sorry I'm driving at the moment and Ollie Safety is preventing me from answering my phone. I’ll call you as soon as I get to my destination.");
            sms.sendMultipartTextMessage(number, null, parts, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to disconnect phone automatically and programmatically
    // Keep this method as it is
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void disconnectPhoneItelephony(Context context) {
        ITelephony telephonyService;
        TelephonyManager telephony = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            TelecomManager tm = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
            if (tm != null) {
                boolean success = tm.endCall();
                if (success) {
                    sendSMS(context);
                }
                // Toast.makeText(context,   backgroundServices.gettest()+"     test    ", Toast.LENGTH_LONG).show();
            }
        } else {
            try {
                Class c = Class.forName(telephony.getClass().getName());
                Method m = c.getDeclaredMethod("getITelephony");
                m.setAccessible(true);
                telephonyService = (ITelephony) m.invoke(telephony);
                boolean success = telephonyService.endCall();
                if (success) {
                    sendSMS(context);
                }
//                Toast.makeText(context, "Declined", Toast.LENGTH_LONG).show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class phoneState extends PhoneStateListener {

        //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
        //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);

            switch (state) {
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //Transition of ringing->offhook are pickups of incoming calls.  Nothing donw on them
                //Toast.makeText(, "Declined", Toast.LENGTH_LONG).show();

                    break;
            }

        }
    }
}
