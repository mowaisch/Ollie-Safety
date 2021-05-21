package com.olliesafety2.simpleapplocker.MiniAdminSide.Already_User;

import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.olliesafety2.simpleapplocker.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class User_Details extends FragmentActivity implements OnMapReadyCallback {
    //TextView date;
    TextView name;
    TextView email;
    TextView phoneNum;
    TextView weeklySpeed;
    TextView monthlySpeed;
    String uID;
    double lat=0.0, lng=0.0;
    private GoogleMap mMap;
    Context context;
    ImageView backButton;
    LatLng userLoc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__details);
        Bundle extras = getIntent().getExtras();
//        date= findViewById(R.id.date);
        backButton=findViewById(R.id.back_btn);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phoneNum = findViewById(R.id.phone_num);
        weeklySpeed = findViewById(R.id.weekly_speed);
        monthlySpeed = findViewById(R.id.monthly_speed);
        context = getApplicationContext();
        if (extras != null) {
            name.setText(extras.getString("Name"));
            email.setText(extras.getString("Email"));
            phoneNum.setText(extras.getString("PhoneNum"));
            weeklySpeed.setText(extras.getDouble("WeeklySpeed") + " KMPH");
            monthlySpeed.setText(extras.getDouble("MonthlySpeed") + " KMPH");
            uID = extras.getString("UID");
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        DatabaseReference userref = FirebaseDatabase.getInstance().getReference().child("Connection").child("User");
        userref.child(uID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("Latitude") != null) {
                        lat = Double.parseDouble(map.get("Latitude").toString());
                    }
                    if (map.get("Longitude") != null) {
                        lng = Double.parseDouble(map.get("Longitude").toString());
                    }

                    userLoc = new LatLng(lat, lng);
                    mMap.addMarker(new MarkerOptions().position(userLoc).title("Last Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(userLoc));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(14));

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private CharSequence timeElapse() {
        final SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateStr = "20200310233405";
        CharSequence niceDateStr = null;
        try {
            Date date = inputFormat.parse(dateStr);
            niceDateStr = DateUtils.getRelativeDateTimeString(this,
                    date.getTime(),
                    DateUtils.MINUTE_IN_MILLIS,
                    DateUtils.WEEK_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_RELATIVE);
            //.getRelativeTimeSpanString(date.getTime() , Calendar.getInstance().getTimeInMillis(), DateUtils.MINUTE_IN_MILLIS);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return niceDateStr;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
}
