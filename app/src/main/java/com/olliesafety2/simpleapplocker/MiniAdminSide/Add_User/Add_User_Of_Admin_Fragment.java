package com.olliesafety2.simpleapplocker.MiniAdminSide.Add_User;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
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
import com.hbb20.CountryCodePicker;
import com.olliesafety2.simpleapplocker.R;
import com.olliesafety2.simpleapplocker.admin_side.user.Users;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class Add_User_Of_Admin_Fragment extends Fragment {
    EditText search_field;
    RecyclerView mRecyclerView;
    Adapter_Add_User_Of_Admin mAdapter;
    List<Users> usersList;
    CountryCodePicker ccpCountry;
    LinearLayout searchdetails, no_Search;
    View v;
    private FirebaseAuth nAuth;
    AppCompatImageButton searchButton;
    AppCompatImageButton btn;
    String user_id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.add_user_of_admin_layout, container, false);
        usersList = new ArrayList<>();
        ccpCountry = v.findViewById(R.id.countryCode);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.add_user_of_admin_recycle_view);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        user_id = nAuth.getInstance().getCurrentUser().getUid();
        searchdetails = v.findViewById(R.id.search_details);
        no_Search = v.findViewById(R.id.no_serach);
        search_field = v.findViewById(R.id.search_number);
        search_field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                searchResult();


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return v;
    }

    void searchResult() {
        String number = ccpCountry.getSelectedCountryCodeWithPlus() + search_field.getText().toString().trim();
        Query userref = FirebaseDatabase.getInstance().getReference().child("Connection").child("User").orderByChild("PhoneNum").startAt(number).endAt(number + "\uf8ff");
        userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                searchdetails.setVisibility(View.GONE);
                no_Search.setVisibility(View.GONE);
                if (dataSnapshot.exists()) {
                    usersList.clear();
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {

                        Map<String, Object> map = (Map<String, Object>) userSnapshot.getValue();

                        if (map.get("Status").equals("User")) {
                            if (map.get("MiniAdminId") != null) {
                                if (map.get("MiniAdminId").equals(user_id)) {

                                } else {
                                    Users users = userSnapshot.getValue(Users.class);
                                    users.setUID(userSnapshot.getKey());
                                    usersList.add(users);
                                }

                            } else {
                                Users users = userSnapshot.getValue(Users.class);
                                users.setUID(userSnapshot.getKey());
                                usersList.add(users);
                            }

                        }

                    }
                    if (usersList.isEmpty()) {
                        no_Search.setVisibility(View.VISIBLE);
                    }
                    Collections.reverse(usersList);
                    mAdapter = new Adapter_Add_User_Of_Admin(usersList);
                    mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
                    mRecyclerView.setAdapter(mAdapter);
                } else {
                    usersList.clear();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

}

