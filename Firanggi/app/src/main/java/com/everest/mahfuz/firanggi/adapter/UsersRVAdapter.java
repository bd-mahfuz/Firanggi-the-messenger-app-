package com.everest.mahfuz.firanggi.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.everest.mahfuz.firanggi.R;
import com.everest.mahfuz.firanggi.UserProfileActivity;
import com.everest.mahfuz.firanggi.model.User;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by mahfuz on 8/2/18.
 */

public class UsersRVAdapter extends RecyclerView.Adapter<UsersRVAdapter.ViewHolder> {

    Context contex;
    ArrayList<User> userArrayList;


    public UsersRVAdapter(Context contex, ArrayList<User> userArrayList) {
        this.contex = contex;
        this.userArrayList = userArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_layout,
                parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //binding
        holder.userName.setText(userArrayList.get(position).getUserName());
        if(userArrayList.get(position).getStatus().equals("")) {
            holder.userStatus.setText(R.string.default_status_Text);
        } else {
            holder.userStatus.setText(userArrayList.get(position).getStatus());
        }
        if (!userArrayList.get(position).getThumbnailImage().equals("")) {
            Picasso.get().load(userArrayList.get(position).getThumbnailImage())
                    .placeholder(R.drawable.avatar_placeholder).into(holder.circleImageView);
        }
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView userName;
        TextView userStatus;
        CircleImageView circleImageView;

        public ViewHolder(final View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.sUserName);
            userStatus = itemView.findViewById(R.id.sUserStatus);
            circleImageView = itemView.findViewById(R.id.sUserIv);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(view.getContext(), getAdapterPosition()+"", Toast.LENGTH_SHORT).show();
                    User user = userArrayList.get(getAdapterPosition());
                   // Log.d("userId in adapter:", user.getId()+"");
                    Intent profileIntent = new Intent(view.getContext(), UserProfileActivity.class);
                    profileIntent.putExtra("userId", user.getId());
                    view.getContext().startActivity(profileIntent);
                }
            });

        }
    }
}
