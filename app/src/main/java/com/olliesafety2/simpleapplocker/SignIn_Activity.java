package com.olliesafety2.simpleapplocker;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.NotificationManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;
import com.olliesafety2.simpleapplocker.DataBase.LoggedUser;
import com.olliesafety2.simpleapplocker.DataBase.Password_Database;
import com.olliesafety2.simpleapplocker.MiniAdminSide.MiniAdmin_Activity;
import com.olliesafety2.simpleapplocker.admin_side.Admin_Activity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.olliesafety2.simpleapplocker.services.BackgroundServices;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SignIn_Activity extends AppCompatActivity {
    private TextView new_user, forgot_password, already_user;
    private Button btn_sign_up, phone_auth, phone_auth2;
    private EditText nEmail, nPassword, new_name, new_email, new_password, confirm_password;
    // private EditText new_age, new_location;
    Context cn;
    private static final int REQUEST_CODE_DRAW_OVERLAY_PERMISSION = 5;
    private FirebaseAuth nAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private LinearLayout sign_in_Layout, sign_up_Layout, code_Layout;
    private ProgressBar progressBar, auth_progress1, auth_progress2;
    private String number;
    CountryCodePicker ccpCountry;
    Password_Database db = new Password_Database(this);
    List<String> checkPass = new ArrayList<String>();
    String mVerificationId;
    AudioManager audioManager;
    NotificationManager notificationManager;
    Intent mServiceIntent;
    ComponentName compName;
    DevicePolicyManager devicePolicyManager;
    String permissionStatus = "notGranted";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_);

        cn = this;
        ////////////////////////////////////////////////////////////

        notificationManager =
                (NotificationManager) cn.getSystemService(Context.NOTIFICATION_SERVICE);
        audioManager =
                (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        ///////////////////////////////////////////////////////////////
        progressBar = findViewById(R.id.progress_Bar);
        auth_progress1 = findViewById(R.id.auth_progress1);
        auth_progress2 = findViewById(R.id.auth_progress2);
        ////////////////////////////////////Already user//////////////////////////////
        nEmail = findViewById(R.id.email_address);
        nPassword = findViewById(R.id.password);
        sign_in_Layout = findViewById(R.id.sign_in_layout);
////////////////////new User.////////////////////////////////////////////////////////////
        sign_up_Layout = findViewById(R.id.sign_up_layout);
        code_Layout = findViewById(R.id.code_layout);
        new_name = findViewById(R.id.txt_name);
        new_email = findViewById(R.id.txt_email);

        btn_sign_up = findViewById(R.id.register);
        phone_auth = findViewById(R.id.phone_auth);
        phone_auth2 = findViewById(R.id.phone_auth2);
        ccpCountry = findViewById(R.id.countryCodeHolder);
        ///////////////////////////////////////////////////////////////////////////////////////
        devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        compName = new ComponentName(this, MyClass.class);

        nAuth = FirebaseAuth.getInstance();

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    sign_in_Layout.setVisibility(View.GONE);
                    sign_up_Layout.setVisibility(View.GONE);
                    code_Layout.setVisibility((View.GONE));
                    progressBar.setVisibility(View.VISIBLE);
                    if(permissionStatus=="Granted"){
                        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                        if (netInfo == null) {
                            String status = "";
                            Cursor res = db.getAllData();
                            while (res.moveToNext()) {
                                status = res.getString(4);
                            }
                            if (!status.isEmpty()) {
                                if (status.equals("Admin")) {
                                    Intent intent = new Intent(SignIn_Activity.this, Admin_Activity.class);
                                    startActivity(intent);
                                    finish();
                                } else if (status.equals("MiniAdmin")) {
                                    Intent intent = new Intent(SignIn_Activity.this, MiniAdmin_Activity.class);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    Intent intent = new Intent(SignIn_Activity.this, MainActivity.class);
                                    intent.putExtra("type", "User");
                                    startActivity(intent);
                                    finish();

                                }
                            }

                        } else {

                            checkUser(user);
                        }
                    }


//                    checkAdmin(user);

                }
            }

        };

        phone_auth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone_auth.setEnabled(false);
                auth_progress1.setVisibility(View.VISIBLE);
                ccpCountry.getSelectedCountryCodeWithPlus();
                if (nEmail.getText().toString().trim().isEmpty()) {
                    Toast.makeText(SignIn_Activity.this, "Enter Phone Number", Toast.LENGTH_LONG).show();

                } else {
                    final String no = nEmail.getText().toString().trim();
                    number = ccpCountry.getSelectedCountryCodeWithPlus() + no;
                    sendVerificationCode(number);
                }

            }
            //   Toast.makeText(SignIn_Activity.this, code, Toast.LENGTH_SHORT).show();

        });

        phone_auth2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth_progress2.setVisibility(View.VISIBLE);
                if (nPassword.getText().toString().trim().isEmpty()) {
                    Toast.makeText(SignIn_Activity.this, "Enter OTP Please", Toast.LENGTH_LONG).show();

                } else {
                    final String password = nPassword.getText().toString().trim();
                    verifyCode(password);
                }

            }
        });

        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sign_in_Layout.setVisibility(View.GONE);
                code_Layout.setVisibility((View.GONE));
                progressBar.setVisibility(View.VISIBLE);
                sign_up_Layout.setVisibility(View.GONE);
                String user_id = nAuth.getInstance().getCurrentUser().getUid();
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Connection").child("User").child(user_id);
                //  current_user_db.setValue(true);
                Map userInfo = new HashMap();
                userInfo.put("Name", new_name.getText().toString().trim());
                userInfo.put("Email", new_email.getText().toString().trim());
                userInfo.put("PhoneNum", user.getPhoneNumber());
                userInfo.put("Status", "User");
                userInfo.put("Create", currentDateTime());
                current_user_db.updateChildren(userInfo).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        Intent intent = new Intent(SignIn_Activity.this, MainActivity.class);
                        intent.putExtra("type", "User");
                        startActivity(intent);
                        finish();
                    }
                });

            }
        });

    }

    private void sendVerificationCode(String number) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacksPhoneAuthActivity.java
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Log.d("", "onVerificationCompleted:" + credential);
            auth_progress1.setVisibility(View.GONE);
            auth_progress2.setVisibility(View.GONE);
//            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//            checkUser(user);
            // Toast.makeText(SignIn_Activity.this, "verified", Toast.LENGTH_LONG).show();
            //signInWithPhoneAuthCredential(credential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            phone_auth.setEnabled(true);
            Log.w("", "onVerificationFailed", e);
            auth_progress1.setVisibility(View.GONE);
            auth_progress2.setVisibility(View.GONE);
            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request

                // ...
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Toast.makeText(SignIn_Activity.this, "SMS quota has been exceeded. Please try later", Toast.LENGTH_LONG).show();
            }

            // Show a message and update the UI
            // ...
        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            auth_progress1.setVisibility(View.GONE);
            sign_in_Layout.setVisibility((View.GONE));
            code_Layout.setVisibility((View.VISIBLE));
            Log.d("", "onCodeSent:" + verificationId);
            mVerificationId = verificationId;
//                mResendToken = token;

            // ...
        }
    };

    private void verifyCode(String code) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        nAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            auth_progress2.setVisibility(View.GONE);
                            sign_in_Layout.setVisibility(View.GONE);
                            code_Layout.setVisibility((View.GONE));
                            progressBar.setVisibility(View.VISIBLE);
                            sign_up_Layout.setVisibility(View.GONE);
                            //     Toast.makeText(SignIn_Activity.this, "Succesfull", Toast.LENGTH_LONG).show();

                            // ...
                        } else {
                            auth_progress2.setVisibility(View.GONE);
                            sign_in_Layout.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            sign_up_Layout.setVisibility(View.GONE);
                            // Sign in failed, display a message and update the UI
                            Log.w("", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(SignIn_Activity.this, "Invalid Code", Toast.LENGTH_LONG).show();

                            }
                        }
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onStart() {
        super.onStart();
        Cursor res = db.getAllData();
        while (res.moveToNext()) {
            checkPass.add(res.getString(0));
        }
        if (checkPass.isEmpty()) {
            db.insertData("1");
            db.updateWeeklyDate("01 01 2020");
            db.updateWeeklySpeed("0");
            db.updateMonthlyDate("01 01 2020");
            db.updateMonthlySpeed("0");
            db.updateStatus("notSignedIn");
        }

        nAuth.addAuthStateListener(firebaseAuthListener);
        NotificationManager notificationManager =
                (NotificationManager) cn.getSystemService(Context.NOTIFICATION_SERVICE);

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
        }else if (!isAccessGranted()) {
            new AlertDialog.Builder(this)
                    .setTitle("USAGE_STATS Permission")
                    .setMessage("Allow USAGE_STATS Permission in Setting")
                    .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // action
                            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                        }
                    })
                    .setNegativeButton("Abort", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //
                        }
                    })
                    .show();
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
        } else {

            BackgroundServices mYourService = new BackgroundServices();
            permissionStatus="Granted";
            mServiceIntent = new Intent(cn, mYourService.getClass());
            if (!isMyServiceRunning(mYourService.getClass())) {
                startService(mServiceIntent);
            }

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
                    permissionStatus="Granted";
                    if (!isMyServiceRunning(mYourService.getClass())) {
                        startService(mServiceIntent);
                    }
                } else {
                    Intent intent = new Intent(SignIn_Activity.this, SignIn_Activity.class);
                    startActivity(intent);
                    finish();
                }
                break;
            case 1001:
                if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                    Intent intent = new Intent(SignIn_Activity.this, SignIn_Activity.class);
                    startActivity(intent);
                    finish();

                } else {
                    Intent intent = new Intent(SignIn_Activity.this, SignIn_Activity.class);
                    startActivity(intent);
                    finish();
                }
                break;
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean isAccessGranted() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = 0;
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.KITKAT) {
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        applicationInfo.uid, applicationInfo.packageName);
            }
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void checkDrawOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQUEST_CODE_DRAW_OVERLAY_PERMISSION);
        }
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

    @Override
    protected void onStop() {
        super.onStop();
        nAuth.removeAuthStateListener(firebaseAuthListener);
    }


    public void checkUser(FirebaseUser user) {
        DatabaseReference userref = FirebaseDatabase.getInstance().getReference().child("Connection").child("User");
        userref.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    String user_id = nAuth.getInstance().getCurrentUser().getUid();
                    String status;
                    status = map.get("Status").toString();
                    if (status.equals("Admin")) {
                        new LoggedUser(cn, user_id, "User");
                        Intent intent = new Intent(SignIn_Activity.this, Admin_Activity.class);
                        startActivity(intent);
                        finish();
                    } else if (status.equals("MiniAdmin")) {
                        new LoggedUser(cn, user_id, "User");
                        Intent intent = new Intent(SignIn_Activity.this, MiniAdmin_Activity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        new LoggedUser(cn, user_id, "User");
                        Intent intent = new Intent(SignIn_Activity.this, MainActivity.class);
                        intent.putExtra("type", "User");
                        startActivity(intent);
                        finish();

                    }
                } else {
                    sign_in_Layout.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    sign_up_Layout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public String currentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }
}
