package com.olliesafety2.simpleapplocker;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.Toast;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.olliesafety2.simpleapplocker.DataBase.Password_Database;

public class MyClass extends DeviceAdminReceiver {
    Password_Database db;
    String phoneNum,adminEmail;
    @Override
    public void onEnabled(Context context, Intent intent) {
        Toast.makeText(context, "Device Admin : enabled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        db = new Password_Database(context);
        String status ="";
        Cursor res = db.getAllData();



        while (res.moveToNext()) {
            status = res.getString(5);
        }
        if (status.equals("SignedIn")) {
            if(db.getAdminEmail()=="noAdmin") {

            }else{
                adminEmail=db.getAdminEmail();
                 phoneNum=db.getPhoneNum();
                BackgroundMail.newBuilder(context)
                        .withUsername("olliesafety@gmail.com")
                        .withPassword("OllieSafety123")
                        .withMailto(adminEmail)
                        .withSubject("Permission Removal Notification of "+phoneNum)
                        .withBody("Hi Admin \n \t\tUser " + phoneNum + " turned their Device Administration off that can lead to deletion of app. \n\n Thanks! \n\nTeam Ollie Safety ")
                        .withProcessVisibility(false)
                        .send();

            }
        }




    }
}
