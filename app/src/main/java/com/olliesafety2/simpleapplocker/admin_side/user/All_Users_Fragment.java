package com.olliesafety2.simpleapplocker.admin_side.user;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class All_Users_Fragment extends Fragment {
    View v;
    RecyclerView mRecyclerView;
    Adapter_All_Users mAdapter;
    List<Users> usersList;
    ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.all_users_fragment, container, false);
        usersList = new ArrayList<>();
        progressBar = v.findViewById(R.id.user_load);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.all_user_recycle_view);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        Query userref = FirebaseDatabase.getInstance().getReference().child("Connection").child("User").orderByChild("Status").equalTo("User");
        progressBar.setVisibility(View.VISIBLE);
        userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    usersList.clear();
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        Users users = userSnapshot.getValue(Users.class);
                        users.setUID(userSnapshot.getKey());
                        usersList.add(users);

                    }

                    Collections.reverse(usersList);
                    mAdapter = new Adapter_All_Users(usersList);
                    mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
                    progressBar.setVisibility(View.GONE);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return v;


    }

}
