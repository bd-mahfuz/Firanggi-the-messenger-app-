package com.everest.mahfuz.firanggi.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.everest.mahfuz.firanggi.ChatActivity;
import com.everest.mahfuz.firanggi.R;
import com.everest.mahfuz.firanggi.UserProfileActivity;
import com.everest.mahfuz.firanggi.model.Friends;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by mahfuz on 9/3/18.
 */

public class FriendsRvAdapter extends RecyclerView.Adapter<FriendsRvAdapter.ViewHolder> {




    List<Friends> friends;
    Context context;

    public FriendsRvAdapter(List<Friends> friends, Context context) {
        this.friends = friends;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View  view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.layout_friend_list,parent, false);



        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

       /* holder.dateEt.setText(friends.get(position).getDate());
        holder.nameTv.setText(friends.get(position).getUserName());

        if (!friends.get(position).getThumbImageUrl().equals("")) {
            Picasso.get().load(friends.get(position).getThumbImageUrl()).into(holder.circleImageView);
        }

        if (friends.get(position).isOnline()) {
            holder.onlineStausIv.setVisibility(View.VISIBLE);
        }*/

    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView circleImageView;
        ImageView onlineStausIv;
        TextView nameTv;
        TextView dateEt;

        public ViewHolder(View itemView) {
            super(itemView);

            circleImageView = itemView.findViewById(R.id.fUserIv);
            nameTv = itemView.findViewById(R.id.fUserName);
            dateEt = itemView.findViewById(R.id.fDate);
            onlineStausIv = itemView.findViewById(R.id.onlineStatus);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    CharSequence[] options = new CharSequence[] {"Send Message", "View Profile"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Select option");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int position) {
                            switch (position) {
                                case 0:
                                    /*Intent chatIntent = new Intent(context, ChatActivity.class);
                                    chatIntent.putExtra("userId", friends.get(getAdapterPosition()).getUserId());
                                    chatIntent.putExtra("userName", friends.get(getAdapterPosition()).getUserName());
                                    context.startActivity(chatIntent);
                                    break;*/
                                case 1:
                                   /* Intent profileIntent = new Intent(context, UserProfileActivity.class);
                                    profileIntent.putExtra("userId", friends.get(getAdapterPosition()).getUserId());
                                    context.startActivity(profileIntent);
                                    break;*/
                            }
                        }
                    });

                    builder.show();

                }
            });

        }
    }
}
