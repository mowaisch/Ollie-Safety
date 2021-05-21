package com.olliesafety2.simpleapplocker;

import android.Manifest;
import android.app.ActivityManager;

import android.app.NotificationManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.olliesafety2.simpleapplocker.DataBase.Password_Database;
import com.olliesafety2.simpleapplocker.services.BackgroundServices;
import com.google.firebase.auth.FirebaseAuth;


import java.util.ArrayList;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_DRAW_OVERLAY_PERMISSION = 5;
    Button update_pass, sign_out_Btn;
    TextView user_name_txt, user_email_txt;
    Password_Database db = new Password_Database(this);
    ImageView click_Image;
    String m_Text = "";
    Context cn;
    ComponentName compName;
    DevicePolicyManager devicePolicyManager;
    double i = 0;
    String pass, name, email, phoneNum;
    String user_id;
    String type = "User";
    List<String> checkPass = new ArrayList<String>();
    // intent is used to call background services
    Intent mServiceIntent;
    BackgroundServices mYourService = new BackgroundServices();

    // NotificationService is for background service class
    //  private BackgroundServices mYourService;
    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            type = extras.getString("type");
        }
        // update_pass = findViewById(R.id.update_pass_btn);
        user_name_txt = findViewById(R.id.name_txt);
        user_email_txt = findViewById(R.id.email_txt);
        sign_out_Btn = findViewById(R.id.sign_out_btn);
        //  progressBar.setVisibility(View.INVISIBLE);
        cn = this;
        click_Image = findViewById(R.id.click_image);
        devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        compName = new ComponentName(this, MyClass.class);
        if (type.equals("User")) {
            // OneSignal.setSubscription(false);
            sign_out_Btn.setVisibility(View.VISIBLE);
        }
        final FirebaseAuth nAuth;
        nAuth = FirebaseAuth.getInstance();
//        user_id = nAuth.getInstance().getCurrentUser().getUid();
        //  String user_id = nAuth.getInstance().getCurrentUser().getUid();

        Cursor res = db.getAllData();
        while (res.moveToNext()) {
            //checkPass.add(res.getString(0));
            pass = res.getString(0);
            name = res.getString(1);
            email = res.getString(2);
            phoneNum = res.getString(3);

        }
        user_name_txt.setText(name);
        user_email_txt.setText(email);
        click_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://www.google.com"));
                startActivity(intent);
            }
        });

        sign_out_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  Toast.makeText(MainActivity.this, name +" "+email+" "+pass, Toast.LENGTH_LONG).show();
                nAuth.signOut();
                db.updateStatus("signedOut");
                signOut(cn);
                startActivity(new Intent(MainActivity.this, SignIn_Activity.class));
                finish();
                db.deleteDataBase(cn);
                // System.exit(0);

            }
        });
    }

    // check your background services
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("Service status", "Running");
                return true;
            }
        }
        Log.i("Service status", "Not running");
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onStart() {
        super.onStart();

        NotificationManager notificationManager =
                (NotificationManager) cn.getSystemService(Context.NOTIFICATION_SERVICE);
        user_name_txt.setText(name);
        user_email_txt.setText(email);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && checkSelfPermission(Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ANSWER_PHONE_CALLS}, 1001);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && checkSelfPermission(Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CALL_LOG}, 1001);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 1001);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 1001);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                && !notificationManager.isNotificationPolicyAccessGranted()) {

            Intent intent = new Intent(
                    android.provider.Settings
                            .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

            startActivity(intent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            checkDrawOverlayPermission();
        } else if (!devicePolicyManager.isAdminActive(compName)) {
            //  boolean active = devicePolicyManager.isAdminActive(compName);
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Permission is required to lock screen");
            startActivity(intent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
        }

        else {

            BackgroundServices mYourService = new BackgroundServices();
            mServiceIntent = new Intent(cn, mYourService.getClass());
            if (!isMyServiceRunning(mYourService.getClass())) {
                startService(mServiceIntent);
            }

        }
        user_name_txt.setText(name);
        user_email_txt.setText(email);
    }

    public void signOut(Context context1) {
        Intent mServiceIntent1 = new Intent(context1, BackgroundServices.class);
        if (isMyServiceRunning(mYourService.getClass())) {
            context1.stopService(mServiceIntent1);
        }
    }

    private void thankyouDisplay() {
        final WindowManager mWindowManager;
        final View mView;
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mView = View.inflate(cn, R.layout.thank_you_layout, null);
        // mView.setTag(TAG);
        Button button = mView.findViewById(R.id.close_btn);

        int layout_parms;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layout_parms = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layout_parms = WindowManager.LayoutParams.TYPE_PHONE;
        }
        final WindowManager.LayoutParams mLayoutParams = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 0,
                layout_parms,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                PixelFormat.RGBA_8888);
        mView.setVisibility(View.VISIBLE);
        //  Looper.prepare();
        mWindowManager.addView(mView, mLayoutParams);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindowManager.removeView(mView);
            }
        });
    }

    private void checkDrawOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQUEST_CODE_DRAW_OVERLAY_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1000:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    BackgroundServices mYourService = new BackgroundServices();
                    mServiceIntent = new Intent(cn, mYourService.getClass());
                    // services not running already
                    // start services
                    if (!isMyServiceRunning(mYourService.getClass())) {
                        startService(mServiceIntent);
                    }
                    finish();
                    thankyouDisplay();
                } else {
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.putExtra("type", type);
                    startActivity(intent);
                    finish();
                }
                break;
            case 1001:
                if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.putExtra("type", type);
                    startActivity(intent);
                    finish();

                } else {
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.putExtra("type", type);
                    startActivity(intent);
                    finish();
                }
                break;
        }
    }
}

