package com.olliesafety2.simpleapplocker.MiniAdminSide.Already_User;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.olliesafety2.simpleapplocker.R;
import com.olliesafety2.simpleapplocker.admin_side.user.Users;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class User_Of_Admin_fragment extends Fragment {

    View v;
    RecyclerView mRecyclerView;
    Adapter_User_Of_Admin mAdapter;
    List<Users> usersList;
    ProgressBar progressBar;
FirebaseAuth nAuth;
TextView noUserFound;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.users_of_admin_layout, container, false);
        usersList = new ArrayList<>();
        progressBar = v.findViewById(R.id.user_of_admin_load);
        noUserFound = v.findViewById(R.id.no_user_text);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.user_of_admin_recycle_view);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        nAuth = FirebaseAuth.getInstance();
        String user_id = nAuth.getInstance().getCurrentUser().getUid();
        Query userref = FirebaseDatabase.getInstance().getReference().child("Connection").child("User").orderByChild("MiniAdminId").equalTo(user_id);
        progressBar.setVisibility(View.VISIBLE);
        userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    noUserFound.setVisibility(View.GONE);
                    usersList.clear();
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        Users users = userSnapshot.getValue(Users.class);
                        users.setUID(userSnapshot.getKey());
                        usersList.add(users);
                    }
                    Collections.reverse(usersList);
                    mAdapter = new Adapter_User_Of_Admin(usersList);
                    mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
                    progressBar.setVisibility(View.GONE);
                    mRecyclerView.setAdapter(mAdapter);
                }else{
                    progressBar.setVisibility(View.GONE);
                    noUserFound.setVisibility(View.VISIBLE);

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        return v;
    }
}

