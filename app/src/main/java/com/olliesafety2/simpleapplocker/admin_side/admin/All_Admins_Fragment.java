package com.olliesafety2.simpleapplocker.admin_side.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.olliesafety2.simpleapplocker.R;
import com.olliesafety2.simpleapplocker.admin_side.user.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class All_Admins_Fragment extends Fragment {

    View v;
    RecyclerView mRecyclerView;
    Adapter_All_Admins mAdapter;
    List<Users> adminsList;
    ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.all_admins_fragment, container, false);
        adminsList = new ArrayList<>();
        mRecyclerView = (RecyclerView) v.findViewById(R.id.all_admin_recycle_view);
        progressBar = v.findViewById(R.id.admin_load);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        Query userref = FirebaseDatabase.getInstance().getReference().child("Connection").child("User").orderByChild("Status").equalTo("MiniAdmin");
        progressBar.setVisibility(View.VISIBLE);
        userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    adminsList.clear();
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {

                        Users users = userSnapshot.getValue(Users.class);
                        adminsList.add(users);

                    }
                    Collections.reverse(adminsList);
                    mAdapter = new Adapter_All_Admins(adminsList);
                    progressBar.setVisibility(View.GONE);
                    mRecyclerView.setAdapter(mAdapter);
                    mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return v;
    }
}
