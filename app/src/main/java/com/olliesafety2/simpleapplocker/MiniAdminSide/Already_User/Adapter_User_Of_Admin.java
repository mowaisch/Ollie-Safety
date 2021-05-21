package com.olliesafety2.simpleapplocker.MiniAdminSide.Already_User;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.olliesafety2.simpleapplocker.R;
import com.olliesafety2.simpleapplocker.admin_side.user.Users;
import java.util.List;

public class Adapter_User_Of_Admin extends RecyclerView.Adapter<Adapter_User_Of_Admin.RecyclerViewHolder>{
    List<Users> usersList;
    private Context cn;
int p;
    public Adapter_User_Of_Admin(List<Users> usersList) {
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recycle_view_all_users, parent, false);
        cn = parent.getContext();
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_User_Of_Admin.RecyclerViewHolder holder, int position) {
        Users users = usersList.get(position);
        holder.name.setText(users.getName());
        holder.email.setText(users.getEmail());
        holder.number.setText(users.getPhoneNum());
        p=position;

    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView email;
        TextView number;

        public RecyclerViewHolder(@NonNull final View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            email = itemView.findViewById(R.id.email);
            number = itemView.findViewById(R.id.number);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Users users = usersList.get(getAdapterPosition());
                  //  Toast.makeText(cn, users.getLatitude()+" p "+users.getLongitude(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(cn, User_Details.class);
                    intent.putExtra("Name", users.getName());
                    intent.putExtra("Email", users.getEmail());
                    intent.putExtra("PhoneNum", users.getPhoneNum());
                    intent.putExtra("WeeklySpeed", users.getWeeklySpeed());
                    intent.putExtra("MonthlySpeed", users.getMonthlySpeed());
                    intent.putExtra("UID", users.getUID());

                    cn.startActivity(intent);
                }
            });
        }
    }
    public int getPosition(){
        return p;
    }
}

