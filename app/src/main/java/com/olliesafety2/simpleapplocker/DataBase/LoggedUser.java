package com.olliesafety2.simpleapplocker.DataBase;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class LoggedUser {
    private String Name;
    private String Email;
    private String Status;
    private String phoneNum;
    private String adminEmail;
    Context cn;

    public LoggedUser(final Context context, String userId, String type) {
        cn = context;

        final Password_Database db = new Password_Database(cn);
        final DatabaseReference userref = FirebaseDatabase.getInstance().getReference().child("Connection").child("User").child(userId);
        final DatabaseReference userref1 = FirebaseDatabase.getInstance().getReference().child("Connection").child("User");
        userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("Name") != null) {
                        Name = map.get("Name").toString();
                    }
                    if (map.get("Email") != null) {
                        Email = map.get("Email").toString();
                    }
                    if (map.get("Email") != null) {
                        phoneNum = map.get("PhoneNum").toString();
                    }
                    if (map.get("Status") != null) {
                        Status= map.get("Status").toString();
                        if(Status.equals("User")){
                            if(map.get("MiniAdminId") !=null){
                                userref1.child(map.get("MiniAdminId").toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                                        if (map.get("Email") != null) {
                                            adminEmail = map.get("Email").toString();
                                            db.updateAdminEmail(adminEmail);
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    }
//                    Toast.makeText(context, Name+" "+Email+" "+phoneNum+" "+Status+"  "+adminEmail, Toast.LENGTH_LONG).show();
                    db.updateUserData(Name, Email,phoneNum,Status);
                    db.updateStatus("SignedIn");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public String getName() {
        return Name;
    }

    public String getEmail() {
        return Email;
    }
}


