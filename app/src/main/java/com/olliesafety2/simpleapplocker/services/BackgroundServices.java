package com.olliesafety2.simpleapplocker.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.admin.DevicePolicyManager;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import android.os.Looper;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import android.widget.Toast;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ncorti.slidetoact.SlideToActView;
import com.olliesafety2.simpleapplocker.CLocation;
import com.olliesafety2.simpleapplocker.Phone_Call_Files.CallRejection;

import com.olliesafety2.simpleapplocker.MyClass;
import com.olliesafety2.simpleapplocker.DataBase.Password_Database;
import com.olliesafety2.simpleapplocker.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class BackgroundServices extends Service implements LocationListener {

    private BroadcastReceiver receiver;
    // context is important
    // every gui or view or activity have context
    // i will use context of NotificationService class
    Context mContext;
    // database class
    Password_Database pass_db = new Password_Database(this);
    // flag is used for stopping or running loop check of
    // current app running
    public String strCurrentSpeed;
    public String status = "";
    static public Double speed = 0.0;
    static public String bluetooth_status = "NotConnected";
    private boolean isDeviceLocked = false;
    // when any app is lanuch and it have password set on it
    // that app name save in current_app varaible
    DevicePolicyManager devicePolicyManager;
    ComponentName compName;
    AudioManager audioManager;
    CallRejection callRejection;
    private String userId;
    private String weekEndDate = "", monthEndDate = "";
    String notificationStatus = "notSent";
    Vibrator vibrator;
    WindowManager mWindowManager;
    View mView;
    String emergencyStatus="not";
    private DatabaseReference databaseReference;

    private static final int ID_SERVICE = 101;

    public BackgroundServices() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // add context of NotificationService to mContext variable
        mContext = this;
        // oreo used different approach for background services
        // other use same approach
        audioManager =
                (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        compName = new ComponentName(this, MyClass.class);
        callRejection = new CallRejection();
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
        } else {
            doStuff();
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            startMyOwnForeground();
        } else {
            startForeground(1, new Notification());
        }
    }

    //  oreo api approach
    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        String NOTIFICATION_CHANNEL_ID = "example.permanence";
        String channelName = "Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());

        Intent intent1 = new Intent(mContext, callRejection.getClass());
        mContext.sendBroadcast(intent1);
        startTimer();
        //lockDevice();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Cursor res2 = pass_db.getAllData();
        while (res2.moveToNext()) {
            status = res2.getString(5);
        }

        stoptimertask();
        if (status.equals("signedOut")) {
        } else {
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("restartservice");
            broadcastIntent.setClass(this, RestartService.class);
            this.sendBroadcast(broadcastIntent);
            unregisterReceiver(callRejection);
        }
    }

    // set timer of one second repeat itself
    private Timer timer;
    private TimerTask timerTask;

    public void startTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {

                if (speed >= 20) {
                    if (isBluetoothHeadsetConnected()) {
                        bluetooth_status = "Connected";
                    } else {
                        bluetooth_status = "NotConnected";
                    }
                    try {
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (audioManager.getMode() == AudioManager.MODE_IN_CALL && bluetooth_status == "NotConnected") {
                        if (notificationStatus.equals("notSent")) {
                            notificationStatus = "Sent";
                            Log.i("MyTag", " Call Status  in progress");
                            NotificationManager notif = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            Notification notify = new Notification.Builder(getApplicationContext())
                                    .setContentTitle("Ollie Safety ")
                                    .setContentText("Please Hang Up The Phone")
                                    .setContentTitle("")
                                    .setSmallIcon(R.drawable.logo)
                                    .build();
                            notify.flags |= Notification.FLAG_AUTO_CANCEL;
                            notif.notify(0, notify);
                            if (Build.VERSION.SDK_INT >= 26) {
                                vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE));
                            } else {
                                vibrator.vibrate(300);
                            }
                        }
                    }
                    String pkg=printForegroundTask();
                    if (!(pkg.contains("com.android.contacts")||pkg.contains("com.olliesafety2.simpleapplocker")) && emergencyStatus=="on") {
                        isDeviceLocked = false;
                        emergencyStatus="not";
                     }
                    if (!isDeviceLocked && emergencyStatus=="not") {
                        isDeviceLocked = true;
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                boolean active = devicePolicyManager.isAdminActive(compName);
                                if (active) {
                                    devicePolicyManager.lockNow();
                                    mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
                                    mView = View.inflate(mContext, R.layout.device_lock, null);
                                    // mView.setTag(TAG);
                                    final SlideToActView emergency_slide = mView.findViewById(R.id.emergency_call_slide);
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
                                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                                                    | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                                                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                                                    | WindowManager.LayoutParams.LAST_SUB_WINDOW,
                                            PixelFormat.RGBA_8888);
                                    mView.setVisibility(View.VISIBLE);
                                    mWindowManager.addView(mView, mLayoutParams);
                                    emergency_slide.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
                                        @Override
                                        public void onSlideComplete(SlideToActView slideToActView) {
                                            Intent callIntent = new Intent("com.android.phone.EmergencyDialer.DIAL");
                                            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                                    Intent.FLAG_ACTIVITY_NO_USER_ACTION |
                                                    Intent.FLAG_ACTIVITY_SINGLE_TOP |
                                                    Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(callIntent);
                                            mWindowManager.removeView(mView);
                                            emergencyStatus="on";

                                        }
                                    });
                                }
                            }
                        });
                    }
                }
//                else if (isDeviceLocked) {
//                    isDeviceLocked = false;
//                    emergencyStatus="not";
//                    mWindowManager.removeView(mView);
//                }

                Log.i("MyTag", " " + speed);
            }
        };
        timer.schedule(timerTask, 0, 100); //
    }

    ///////////////////////////////////////////////////////////////////////Timer Ends
    public void stoptimertask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            CLocation myLocation = new CLocation(location, this.userMetricUnits());
            this.updateSpeed(myLocation, location);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
        String userNum = "", adminEmail = "", msg = "";
        Cursor res = pass_db.getAllData();
        while (res.moveToNext()) {
            status = res.getString(5);
        }
        if (status.equals("SignedIn")) {
            if (pass_db.getAdminEmail() == "noAdmin") {
            } else {
                adminEmail = pass_db.getAdminEmail();
                userNum = pass_db.getPhoneNum();
                msg = " turned their location off.";

                sendEmail(adminEmail, userNum, msg);
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void doStuff() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
    }

    @SuppressLint("MissingPermission")
    public static boolean isBluetoothHeadsetConnected() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()
                && mBluetoothAdapter.getProfileConnectionState(BluetoothProfile.STATE_CONNECTED) == BluetoothProfile.STATE_CONNECTED);
    }

    private String printForegroundTask() {
        String currentApp = "NULL";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
        } else {
            ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
            currentApp = tasks.get(0).processName;
        }
        Log.e("AppLockerService", "Current App in foreground is: " + currentApp);
        return currentApp;
    }

    private void updateSpeed(CLocation location, Location loc) {
        Cursor res2 = pass_db.getAllData();
        while (res2.moveToNext()) {
            status = res2.getString(5);
        }
        float nCurrentSpeed = 0;
        if (location != null) {
            location.setbUseMetricUnits(this.userMetricUnits());
            nCurrentSpeed = location.getSpeed();
        }
        Formatter fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.US, "%5.1f", nCurrentSpeed);
        strCurrentSpeed = fmt.toString();
        if (this.userMetricUnits()) {
            speed = Double.valueOf(strCurrentSpeed);
            if (status.equals("SignedIn")) {
                userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                databaseReference = FirebaseDatabase.getInstance().getReference().child("Connection").child("User").child(userId);
                speedSaver();
                Map locInfo = new HashMap();
                locInfo.put("Latitude", loc.getLatitude());
                locInfo.put("Longitude", loc.getLongitude());
                databaseReference.updateChildren(locInfo).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                    }
                });
            }
            Log.i("MyTag", "Speed,,,,,,,,,,,,,,,,,,,,,,,,, " + speed + "  " + strCurrentSpeed);
        } else {
            //  tv_Speed.setText(strCurrentSpeed+" miles/h");
        }
    }

    static public Double getSpeed() {
        return speed;
    }

    static public String getBluetooth_status() {
        return bluetooth_status;
    }

    private void speedSaver() {
        String weeklyDate = "", weeklySpeed = "", monthlyDate = "", monthlySpeed = "";
        Cursor res2 = pass_db.getAllData();
        while (res2.moveToNext()) {
            weeklyDate = res2.getString(7);
            weeklySpeed = res2.getString(8);
            monthlyDate = res2.getString(9);
            monthlySpeed = res2.getString(10);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd MM yyyy", Locale.getDefault());
        //   String currentDate = sdf.format(new Date());
        long d = weekDateChecker(weeklyDate);
        if (d > 0 && d < 7) {
            if (speed > Double.parseDouble(weeklySpeed)) {
                Toast.makeText(getApplicationContext(), "speed changed ", Toast.LENGTH_LONG).show();
                pass_db.updateWeeklySpeed(speed.toString());
                saveWeeklySpeed(speed);
            } else {
            }
        } else {
            pass_db.updateWeeklyDate(weekEndDate);
            pass_db.updateWeeklySpeed(speed.toString());
            saveWeeklySpeed(speed);
        }
        ///////////////////////////////////////////////////////
        long mdays = monthDateChecker(monthlyDate);
        if (mdays >= 0 && mdays <= 30) {
            if (speed > Double.parseDouble(monthlySpeed)) {
                pass_db.updateMonthlySpeed(speed.toString());
                saveMonthlySpeed(speed);
            }
        } else {
            pass_db.updateMonthlyDate(monthEndDate);
            pass_db.updateMonthlySpeed(speed.toString());
            saveMonthlySpeed(speed);
        }
    }

    private long monthDateChecker(String monthlyDate) {
        long days = 0;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
        SimpleDateFormat myFormat = new SimpleDateFormat("dd MM yyyy");
        String currentDate = myFormat.format(new Date());
        try {
            Date date1 = myFormat.parse(currentDate);
            Date date2 = myFormat.parse(monthlyDate);
            long diff = date2.getTime() - date1.getTime();
            days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            monthEndDate = myFormat.format(cal.getTime());
            System.out.println(myFormat.format(cal.getTime()) + currentDate + monthlyDate + "Days:  leftttt" + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return days;
    }

    private long weekDateChecker(String dateToCheck) {
        long days = 0;
        Calendar now = new GregorianCalendar();
        Calendar start = new GregorianCalendar(now.get(Calendar.YEAR),
                now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        while (start.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            start.add(Calendar.DAY_OF_WEEK, -1);
        }
        Calendar end = (Calendar) start.clone();
        end.add(Calendar.DAY_OF_MONTH, 7);
        SimpleDateFormat myFormat = new SimpleDateFormat("dd MM yyyy");
        String currentDate = myFormat.format(new Date());
        try {
            Date date1 = myFormat.parse(currentDate);
            Date date2 = myFormat.parse(dateToCheck);
            long diff = date2.getTime() - date1.getTime();
            days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            weekEndDate = myFormat.format(end.getTime());
            System.out.println(myFormat.format(end.getTime()) + currentDate + dateToCheck + "Days: " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return days;
    }

    private void sendEmail(String adminId, String userNum, String msg) {
        BackgroundMail.newBuilder(this)
                .withUsername("olliesafety@gmail.com")
                .withPassword("OllieSafety123")
                .withMailto(adminId)
                .withSubject("Permission Removal Notification of " + userNum)
                .withBody("Hi Admin \n \t\tUser " + userNum + msg + " \n\n Thanks! \n\nTeam Ollie Safety ")
                .withProcessVisibility(false)
                .send();
    }

    private void saveWeeklySpeed(double finalSpeed) {
        Map locInfo = new HashMap();
        locInfo.put("WeeklySpeed", finalSpeed);
        databaseReference.updateChildren(locInfo).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
            }
        });
    }

    private void saveMonthlySpeed(double finalSpeed) {
        Map locInfo = new HashMap();
        locInfo.put("MonthlySpeed", finalSpeed);
        databaseReference.updateChildren(locInfo).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
            }
        });
    }

    private boolean userMetricUnits() {
        return true;
    }
}
