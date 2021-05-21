package com.olliesafety2.simpleapplocker.admin_side.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.olliesafety2.simpleapplocker.DataBase.Password_Database;
import com.olliesafety2.simpleapplocker.R;
import com.olliesafety2.simpleapplocker.SignIn_Activity;
import com.olliesafety2.simpleapplocker.admin_side.Admin_Activity;
import com.olliesafety2.simpleapplocker.services.BackgroundServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePssword_Activity extends AppCompatActivity {

    private TextView forgot_password;
    private Button  change_pw_btn,verify_pw_btn;
    private EditText Email, old_Password,new_Password;
    Context cn;
    private FirebaseAuth nAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private LinearLayout change_pw_Layout, verify_pw_layout;
    FirebaseUser user;
    private ProgressBar progressBar;
    Password_Database db = new Password_Database(this);
    BackgroundServices mYourService = new BackgroundServices();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pssword_);

        cn = this;
        progressBar = findViewById(R.id.password_progress_Bar);
        ////////////////////////////////////Already user//////////////////////////////
        Email = findViewById(R.id.pw_email_address);
        old_Password = findViewById(R.id.old_ch_password);
        new_Password = findViewById(R.id.new_ch_password);
        change_pw_Layout = findViewById(R.id.change_pw_layout);
        verify_pw_layout = findViewById(R.id.verify_password_layout);
        forgot_password = findViewById(R.id.ch_forgot_password);
////////////////////new User.////////////////////////////////////////////////////////////
        change_pw_btn = findViewById(R.id.change_password);
        verify_pw_btn = findViewById(R.id.verify_password);
        nAuth = FirebaseAuth.getInstance();
        user=nAuth.getCurrentUser();
        verify_pw_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = Email.getText().toString().trim();
                final String password = old_Password.getText().toString().trim();
                verify_pw_layout.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                AuthCredential credential = EmailAuthProvider.getCredential(email, password);
                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            verify_pw_layout.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            change_pw_Layout.setVisibility(View.VISIBLE);
                        }else {
                            verify_pw_layout.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(ChangePssword_Activity.this, "Invalid Credentials", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });
        change_pw_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                change_pw_Layout.setVisibility(View.GONE);
                final String new_password = new_Password.getText().toString().trim();
                user.updatePassword(new_password).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(ChangePssword_Activity.this, "Password Updated", Toast.LENGTH_LONG).show();
                            nAuth.signOut();
                            db.updateStatus("signedOut");
                            //OneSignal.setSubscription(false);
                            Intent mServiceIntent1 = new Intent(cn, BackgroundServices.class);
                            if (isMyServiceRunning(mYourService.getClass())) {
                                cn.stopService(mServiceIntent1);
                            }
                            //db.deleteData();
                            db.deleteDataBase(cn);
                            startActivity(new Intent(cn, SignIn_Activity.class));
                            finish();
                            //System.exit(0);
                        }else{
                            verify_pw_layout.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(ChangePssword_Activity.this, "Error while updating password", Toast.LENGTH_LONG).show();

                        }

                    }
                });

            }
        });

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(cn);
                builder.setTitle("Enter Email Where Password Reset Method can be send");
                // Set up the input
                final EditText input = new EditText(cn);
                // Specify the type of input expected; this, for olliesafety, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                builder.setView(input);
                // Set up the buttons
                builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String m_Text = input.getText().toString();
                        if (m_Text.isEmpty()) {
                            Toast.makeText(ChangePssword_Activity.this, "Enter Email Address", Toast.LENGTH_LONG).show();
                        } else {
                            verify_pw_layout.setVisibility(View.GONE);
                            progressBar.setVisibility(View.VISIBLE);
                            nAuth.sendPasswordResetEmail(m_Text)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(ChangePssword_Activity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(ChangePssword_Activity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                            }
                                            progressBar.setVisibility(View.GONE);
                                            verify_pw_layout.setVisibility(View.VISIBLE);
                                        }
                                    });
                            dialog.cancel();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ChangePssword_Activity.this, Admin_Activity.class);
        startActivity(intent);
        finish();
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
}