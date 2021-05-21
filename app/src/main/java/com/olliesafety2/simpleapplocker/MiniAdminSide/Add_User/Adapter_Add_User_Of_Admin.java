package com.olliesafety2.simpleapplocker.MiniAdminSide.Add_User;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.olliesafety2.simpleapplocker.R;
import com.olliesafety2.simpleapplocker.admin_side.user.Users;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Adapter_Add_User_Of_Admin extends RecyclerView.Adapter<Adapter_Add_User_Of_Admin.RecyclerViewHolder>{
    List<Users> usersList;
    private Context cn;
    private FirebaseAuth nAuth;
    public Adapter_Add_User_Of_Admin(List<Users> usersList) {
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
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        Users users = usersList.get(position);
        holder.name.setText(users.getName());
        holder.email.setText(users.getEmail());
        holder.number.setText(users.getPhoneNum());
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView email;
        TextView number;
        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            email = itemView.findViewById(R.id.email);
            number = itemView.findViewById(R.id.number);
            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    final Users users = usersList.get(position);
                    Log.e("Clicked user ", "                     " + users.getName());
                    AlertDialog.Builder builder = new AlertDialog.Builder(cn);
                    final String user_id = nAuth.getInstance().getCurrentUser().getUid();
                    builder.setTitle("Do you want to add "+users.getName()+" to your users!");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Connection").child("User").child(users.getUID());
                            //  current_user_db.setValue(true);
                            Map userInfo = new HashMap();
                            userInfo.put("MiniAdminId", user_id );
                            current_user_db.updateChildren(userInfo).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    Toast.makeText(cn, "Done", Toast.LENGTH_LONG).show();
                                }

                            });
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }
            });
        }
    }
}
