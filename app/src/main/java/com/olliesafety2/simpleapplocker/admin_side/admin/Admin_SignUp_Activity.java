package com.olliesafety2.simpleapplocker.admin_side.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.olliesafety2.simpleapplocker.DataBase.Password_Database;
import com.olliesafety2.simpleapplocker.R;
import com.olliesafety2.simpleapplocker.SignIn_Activity;
import com.olliesafety2.simpleapplocker.admin_side.Admin_Activity;
import com.olliesafety2.simpleapplocker.services.BackgroundServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Admin_SignUp_Activity extends AppCompatActivity {
    private Button  btn_sign_up;
    private EditText new_name, new_email, new_password,confirm_password;
//    private  EditText new_age, new_location;
    Context cn;
    private FirebaseAuth nAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private LinearLayout sign_up_Layout;
    private ProgressBar progressBar;
//    private RadioGroup radioSexGroup;
//    private RadioButton radioSexButton;
    Password_Database db = new Password_Database(this);
    BackgroundServices mYourService = new BackgroundServices();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin__sign_up_);
        cn = this;
        progressBar = findViewById(R.id.progress_Bar1);
////////////////////new User.////////////////////////////////////////////////////////////
        sign_up_Layout = findViewById(R.id.sign_up_layout1);
        new_name = findViewById(R.id.txt_name1);
        new_email = findViewById(R.id.txt_email1);
        new_password = findViewById(R.id.new_password1);
        confirm_password = findViewById(R.id.confirm_password1);
//        new_age = findViewById(R.id.txt_age1);
//        new_location = findViewById(R.id.txt_location1);
//        radioSexGroup = findViewById(R.id.radioSex1);
        btn_sign_up = findViewById(R.id.register1);
        nAuth = FirebaseAuth.getInstance();

        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // get selected radio button from radioGroup
//                int selectedId = radioSexGroup.getCheckedRadioButtonId();
//                // find the radiobutton by returned id
//                radioSexButton = (RadioButton) findViewById(selectedId);
                if(new_password.getText().toString().trim().length()<6){
                    Toast.makeText(Admin_SignUp_Activity.this, "Password must contain more than 6 digits ", Toast.LENGTH_SHORT).show();
                }else {
                    if (new_email.getText().toString().trim() != null && new_password.getText().toString().trim() != null && new_name.getText().toString().trim() != null) {
                        if(new_password.getText().toString().trim().equals(confirm_password.getText().toString().trim())) {
                            final String email = new_email.getText().toString().trim();
                            final String password = new_password.getText().toString().trim();
                            sign_up_Layout.setVisibility(View.GONE);
                            progressBar.setVisibility(View.VISIBLE);
                            nAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(Admin_SignUp_Activity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(Admin_SignUp_Activity.this, "Sign Up Error", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                        sign_up_Layout.setVisibility(View.VISIBLE);
                                    } else {
                                        String user_id = nAuth.getInstance().getCurrentUser().getUid();
                                        DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Connection").child("Admin").child(user_id);
                                        //  current_user_db.setValue(true);
                                        Map userInfo = new HashMap();
                                        userInfo.put("Name", new_name.getText().toString().trim());
                                        userInfo.put("Email", new_email.getText().toString().trim());
//                                    userInfo.put("Age", new_age.getText().toString().trim());
//                                    userInfo.put("Location", new_location.getText().toString().trim());
//                                    userInfo.put("Sex", radioSexButton.getText().toString().trim());
                                        userInfo.put("Create", currentDateTime());
                                        current_user_db.updateChildren(userInfo);
                                        Toast.makeText(Admin_SignUp_Activity.this, "Admin Added", Toast.LENGTH_SHORT).show();
                                        nAuth.signOut();
                                        db.updateStatus("signedOut");
                                        // OneSignal.setSubscription(false);
                                        Intent mServiceIntent1 = new Intent(cn, BackgroundServices.class);
                                        if (isMyServiceRunning(mYourService.getClass())) {
                                            cn.stopService(mServiceIntent1);
                                        }
                                        //db.deleteData();
                                        db.deleteDataBase(cn);
                                        startActivity(new Intent(cn, SignIn_Activity.class));
                                        finish();
                                        // System.exit(0);
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(Admin_SignUp_Activity.this, "Both passwords are not same", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Admin_SignUp_Activity.this, "Fill values first", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

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
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Admin_SignUp_Activity.this, Admin_Activity.class);
        startActivity(intent);
        finish();
    }

    public String currentDateTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;

    }
}