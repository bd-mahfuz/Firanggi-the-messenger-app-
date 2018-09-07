package com.everest.mahfuz.firanggi.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.everest.mahfuz.firanggi.ChatActivity;
import com.everest.mahfuz.firanggi.R;
import com.everest.mahfuz.firanggi.UserProfileActivity;
import com.everest.mahfuz.firanggi.adapter.FriendsRvAdapter;
import com.everest.mahfuz.firanggi.model.Friends;
import com.everest.mahfuz.firanggi.model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private List<Friends> friendsList = new ArrayList<>();
    private List<Friends> tempFriendsList = new ArrayList<>();

    private RecyclerView recyclerView;

    private DatabaseReference mRootRef;
    private DatabaseReference mFriendRef;
    private DatabaseReference mUserRef;

    private FirebaseAuth mAuth;

    private TextView noFriendMsg;

    private Friends friends;

    int count = 0;


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        mAuth = FirebaseAuth.getInstance();

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mFriendRef = mRootRef.child("Friends").child(mAuth.getCurrentUser().getUid());
        Query query = mFriendRef;
        mFriendRef.keepSynced(true);
        mUserRef = mRootRef.child("Users");
        mUserRef.keepSynced(true);

        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        noFriendMsg = view.findViewById(R.id.noFriendMsg);
        recyclerView = view.findViewById(R.id.friendRV);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        //recyclerView.setHasFixedSize(true);

        FirebaseRecyclerOptions<Friends> options =
                new FirebaseRecyclerOptions.Builder<Friends>()
                        .setQuery(query, Friends.class)
                        .build();


        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> recyclerAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FriendsViewHolder holder, int position, @NonNull Friends model) {
                holder.dateTv.setText(model.getDate());

                final String userListId = getRef(position).getKey();

                mUserRef.child(userListId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String userName = dataSnapshot.child("userName").getValue().toString();
                        String isOnline =  dataSnapshot.child("online").getValue().toString();
                        String userThumbImage = dataSnapshot.child("thumbnailImage").getValue().toString();

                        if (isOnline.equals("true")) {
                            holder.imageView.setVisibility(View.VISIBLE);
                        }else {
                            holder.imageView.setVisibility(View.GONE);
                        }

                        holder.userNameTv.setText(userName);
                        Picasso.get().load(userThumbImage).placeholder(R.drawable.avatar_placeholder).into(holder.userImageIv);

                        holder.view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CharSequence[] options = new CharSequence[] {"Send Message", "View Profile"};

                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Select option");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int position) {
                                        switch (position) {
                                            case 0:
                                                Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                                chatIntent.putExtra("userId", userListId);
                                                chatIntent.putExtra("userName", userName);
                                                getActivity().startActivity(chatIntent);
                                                break;
                                            case 1:
                                                Intent profileIntent = new Intent(getContext(), UserProfileActivity.class);
                                                profileIntent.putExtra("userId", userListId);
                                                getActivity().startActivity(profileIntent);
                                                break;
                                        }
                                    }
                                });

                                builder.show();
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_friend_list, parent, false);

                return new FriendsViewHolder(view);
            }


        };

        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.startListening();
        recyclerAdapter.notifyDataSetChanged();

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();



    }

    class FriendsViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView userNameTv;
        TextView dateTv;
        CircleImageView userImageIv;
        ImageView imageView;


        public FriendsViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            userNameTv = itemView.findViewById(R.id.fUserName);
            dateTv = itemView.findViewById(R.id.fDate);
            userImageIv = itemView.findViewById(R.id.fUserIv);
            imageView = itemView.findViewById(R.id.onlineStatus);
        }
    }
}
