package com.everest.mahfuz.firanggi;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.everest.mahfuz.firanggi.Utility.FiranggiUtility;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private TextView mUserNameTv;
    private TextView mLastSeenTv;
    private CircleImageView mUserImageIv;

    private Toolbar mChatToolBar;

    private DatabaseReference mRootRef;
    private DatabaseReference mUserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        String userId = getIntent().getStringExtra("userId");
        String userName = getIntent().getStringExtra("userName");

        mChatToolBar = findViewById(R.id.chatToolBar);
        setSupportActionBar(mChatToolBar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View charBarView = inflater.inflate(R.layout.layout_chat_bar, null);
        actionBar.setCustomView(charBarView);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mUserRef = mRootRef.child("Users").child(userId);

        mUserNameTv = findViewById(R.id.userNameTv);
        mLastSeenTv = findViewById(R.id.lastSeenTv);
        mUserImageIv = findViewById(R.id.userImageIv);

        mUserNameTv.setText(userName);

        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String online =  dataSnapshot.child("online").getValue().toString();

                if (online.equals("true")) {
                    mLastSeenTv.setText("Online");
                }else {
                    mLastSeenTv.setText(FiranggiUtility.getTimeAgo(Long.valueOf(online)));
                }

                String thumbImage = dataSnapshot.child("thumbnailImage").getValue().toString();
                Picasso.get().load(thumbImage).placeholder(R.drawable.avatar_placeholder).into(mUserImageIv);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





    }
}
