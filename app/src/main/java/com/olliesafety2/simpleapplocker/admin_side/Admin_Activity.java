package com.olliesafety2.simpleapplocker.admin_side;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.olliesafety2.simpleapplocker.services.BackgroundServices;
import com.olliesafety2.simpleapplocker.MainActivity;
import com.olliesafety2.simpleapplocker.DataBase.Password_Database;
import com.olliesafety2.simpleapplocker.R;
import com.olliesafety2.simpleapplocker.SignIn_Activity;
import com.olliesafety2.simpleapplocker.admin_side.admin.All_Admins_Fragment;
import com.olliesafety2.simpleapplocker.admin_side.user.All_Users_Fragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Admin_Activity extends AppCompatActivity {
    TabLayout MyTabs;
    ViewPager MyPage;
    FirebaseAuth nAuth;
    private PageViewerAdaptar pageViewerAdaptar;
    private Button btn_Switch;
    Password_Database db = new Password_Database(this);

    BackgroundServices mYourService = new BackgroundServices();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_);
        //GetUserData getUserData=new GetUserData();
        MyTabs = (TabLayout) findViewById(R.id.tabLayout);
        MyPage = (ViewPager) findViewById(R.id.pageViewer);
        btn_Switch = (Button) findViewById(R.id.btn_Switch);
        pageViewerAdaptar = new PageViewerAdaptar(getSupportFragmentManager());
        pageViewerAdaptar.AddFragmentPage(new All_Users_Fragment(), "");
        pageViewerAdaptar.AddFragmentPage(new All_Admins_Fragment(), "");
       // pageViewerAdaptar.AddFragmentPage(new Notification_Fragment(), "");
        MyPage.setAdapter(pageViewerAdaptar);
        MyTabs.setupWithViewPager(MyPage);
        MyTabs.getTabAt(0).setIcon(R.drawable.ic_users_black_24dp);
        MyTabs.getTabAt(1).setIcon(R.drawable.ic_admins_black_24dp);
       // MyTabs.getTabAt(2).setIcon(R.drawable.ic_notifications_active_black_24dp);
        nAuth = FirebaseAuth.getInstance();
        String user_id = nAuth.getInstance().getCurrentUser().getUid();
        btn_Switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Admin_Activity.this, MainActivity.class);
                intent.putExtra("type", "Admin");
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.new_admin:
//                startActivity(new Intent(this, Admin_SignUp_Activity.class));
//                finish();
//                return true;
//            case R.id.set_password:
//                startActivity(new Intent(this, ChangePssword_Activity.class));
//                finish();
//                return true;
            case R.id.sign_out:
                nAuth.signOut();
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user == null){
                    db.updateStatus("signedOut");
                   // OneSignal.setSubscription(false);
                    Intent mServiceIntent1 = new Intent(this, BackgroundServices.class);
                    if (isMyServiceRunning(mYourService.getClass())) {
                        this.stopService(mServiceIntent1);
                    }
                    startActivity(new Intent(this, SignIn_Activity.class));
                    // db.deleteData();
                    db.deleteDataBase(this);
                    finish();
                    //System.exit(0);
                }else{
                    Log.i("User status", "LOGGED IN");
                }

                return true;
        }
        return super.onOptionsItemSelected(item);
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
