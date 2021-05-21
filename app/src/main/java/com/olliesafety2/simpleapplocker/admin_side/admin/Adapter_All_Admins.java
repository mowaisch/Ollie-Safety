package com.olliesafety2.simpleapplocker.admin_side.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;


import com.olliesafety2.simpleapplocker.R;
import com.olliesafety2.simpleapplocker.admin_side.user.Users;

import java.util.List;

public class Adapter_All_Admins extends RecyclerView.Adapter<Adapter_All_Admins.RecyclerViewHolder> {

    List<Users> adminsList;

    public Adapter_All_Admins(List<Users> usersList) {
        this.adminsList = usersList;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recycle_view_all_admin,parent,false);

        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        Users users = adminsList.get(position);

        holder.name.setText(users.getName());
        holder.email.setText(users.getEmail());
//        holder.location.setText(users.getLocation());
//        holder.sex.setText(users.getSex());
//        holder.age.setText(users.getAge()+" years");




    }



    @Override
    public int getItemCount() {
        return adminsList.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView email;
//        TextView location;
//        TextView sex;
//        TextView age;


        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            email = itemView.findViewById(R.id.email);
//            location = itemView.findViewById(R.id.location);
//            sex = itemView.findViewById(R.id.sex);
//            age = itemView.findViewById(R.id.age);




        }
    }
}

