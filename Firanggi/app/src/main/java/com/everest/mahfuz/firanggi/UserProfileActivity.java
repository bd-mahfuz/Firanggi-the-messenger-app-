package com.everest.mahfuz.firanggi;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.opengl.Visibility;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.everest.mahfuz.firanggi.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    private TextView mUserNameTv, mUserStatusTv, mTotalFriendTv;
    private Button mSendRequestBt, mDeclineFrBt;
    private CircleImageView mCircleIv;
    private LinearLayout backgroundLayout;

    private ProgressDialog mProgressDialog;

    private DatabaseReference rootRef;
    private DatabaseReference userRef;
    private DatabaseReference mFriendRequestRef;
    private DatabaseReference mFriendsRef;
    private DatabaseReference mNotificationRef;
    
    private FirebaseAuth mAuth;

    private int mCurrent_state;

    private static final int NOT_FRIEND = 0;
    private static final int REQUEST_SENT = 1;
    private static final int REQUEST_RECEIVED = 2;
    private static final int FRIEND = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mCurrent_state = NOT_FRIEND;
        mAuth = FirebaseAuth.getInstance();

        Log.d("d:", mAuth.getUid());
        Log.d("e:", mAuth.getCurrentUser().getUid());


        mUserNameTv = findViewById(R.id.pUserName);;
        mUserStatusTv = findViewById(R.id.pUserStatus);
        mTotalFriendTv = findViewById(R.id.totalFriendTv);
        mSendRequestBt = findViewById(R.id.sendRequestBt);
        mDeclineFrBt = findViewById(R.id.declineFrBt);
        mCircleIv = findViewById(R.id.pUserIv);
        backgroundLayout = findViewById(R.id.pBackgroundLayout);

        final String userId = getIntent().getStringExtra("userId");

        rootRef= FirebaseDatabase.getInstance().getReference();
        userRef = rootRef.child("Users").child(userId);
        mFriendRequestRef = rootRef.child("Friend_Request");
        mFriendsRef = rootRef.child("Friends");
        mNotificationRef = rootRef.child("notification");

        userRef.keepSynced(true);
        //progress dialog
        mProgressDialog = new ProgressDialog(UserProfileActivity.this);
        mProgressDialog.setMessage("Getting user data...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();



        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final User user = dataSnapshot.getValue(User.class);

                mUserNameTv.setText(user.getUserName());
                if (!user.getStatus().equals("")) {
                    mUserStatusTv.setText(user.getStatus());
                }

                if (!user.getUserImage().equals("")) {
                    Picasso.get().load(user.getUserImage()).placeholder(R.drawable.avatar_placeholder).into(mCircleIv);
                }

                if (!user.getBackgroundImage().equals("")) {
                    Glide.with(UserProfileActivity.this).load(user.getBackgroundImage())
                            .into(new SimpleTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    backgroundLayout.setBackground(resource);
                                }
                            });
                }

                mFriendRequestRef.child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(userId)) {
                            String requestType = dataSnapshot.child(userId).child("request_type").getValue().toString();
                            if (requestType.equals("Received")) {
                                mCurrent_state = REQUEST_RECEIVED;
                                mSendRequestBt.setEnabled(true);
                                mSendRequestBt.setText("Accept Request");
                                mDeclineFrBt.setVisibility(View.VISIBLE);
                            } else if (requestType.equals("Sent")) {
                                mCurrent_state = REQUEST_SENT;
                                mSendRequestBt.setEnabled(true);
                                mSendRequestBt.setText("Cancel Request");
                            }
                            mProgressDialog.dismiss();
                        }else {
                            mFriendsRef.child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(userId)) {
                                        mCurrent_state = FRIEND;
                                        mSendRequestBt.setText("UnFriend");
                                    }
                                    mProgressDialog.dismiss();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    mProgressDialog.dismiss();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        mProgressDialog.dismiss();
                    }
                });





            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mSendRequestBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mSendRequestBt.setEnabled(false);

                // send request part

                if (mCurrent_state == NOT_FRIEND) {
                    mFriendRequestRef.child(mAuth.getUid()).child(userId).child("request_type")
                            .setValue("Sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mFriendRequestRef.child(userId).child(mAuth.getUid()).child("request_type")
                                        .setValue("Received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        HashMap<String, String> hashMap = new HashMap<>();
                                        hashMap.put("from", mAuth.getUid());
                                        hashMap.put("type", "Request");

                                        mNotificationRef.child(userId).push().setValue(hashMap)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    mCurrent_state = REQUEST_SENT;
                                                    mSendRequestBt.setEnabled(true);
                                                    mSendRequestBt.setText("Cancel Request");

                                                    Toast.makeText(UserProfileActivity.this, "Friend Request sent succesfully", Toast.LENGTH_SHORT).show();

                                                }
                                            }
                                        });

                                    }
                                });
                            }else {
                                Toast.makeText(UserProfileActivity.this, "Sending Request Failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                // cancel request part
                if (mCurrent_state == REQUEST_SENT) {
                    mFriendRequestRef.child(mAuth.getUid()).child(userId).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        mFriendRequestRef.child(userId).child(mAuth.getUid()).removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {

                                                            //updating button
                                                            mSendRequestBt.setEnabled(true);
                                                            mSendRequestBt.setText("Send Friend Request");
                                                            mCurrent_state = NOT_FRIEND;


                                                            Toast.makeText(UserProfileActivity.this,
                                                                    "Friend request canceled.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }else {
                                        Toast.makeText(UserProfileActivity.this, "Request Not canceled.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

                // accept request part
                if(mCurrent_state == REQUEST_RECEIVED) {
                    final String currentDate = DateFormat.getDateInstance().format(new Date());
                    //set the date to Friend of current user
                    mFriendsRef.child(mAuth.getUid()).child(userId).child("date").setValue(currentDate)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //also set the date to Friend of requested user 
                                mFriendsRef.child(userId).child(mAuth.getUid()).child("date").setValue(currentDate)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    // removing request info from current user
                                                    mFriendRequestRef.child(mAuth.getUid()).child(userId).removeValue()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        //removing the request info from requested user
                                                                        mFriendRequestRef.child(userId).child(mAuth.getUid()).removeValue()
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            mCurrent_state = FRIEND;
                                                                                            mSendRequestBt.setText("UnFriend");
                                                                                            mSendRequestBt.setEnabled(true);

                                                                                            //hide the decline frnd btn
                                                                                            mDeclineFrBt.setVisibility(View.GONE);
                                                                                            mDeclineFrBt.setEnabled(false);
                                                                                            Toast.makeText(UserProfileActivity.this, "Accepted", Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                        else {
                                                                                            Toast.makeText(UserProfileActivity.this, "Not accepted, there was a problem.", Toast.LENGTH_SHORT).show();

                                                                                        }
                                                                                    }
                                                                                });
                                                                    }
                                                                    else {
                                                                        Toast.makeText(UserProfileActivity.this, "Not accepted, there was a problem.", Toast.LENGTH_SHORT).show();

                                                                    }
                                                                }
                                                            });
                                                }else {
                                                    Toast.makeText(UserProfileActivity.this, "Not accepted, there was a problem.", Toast.LENGTH_SHORT).show();

                                                }
                                            }
                                        });
                            }else {
                                Toast.makeText(UserProfileActivity.this, "There was a problem.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                
                
                // unfriend part
                if (mCurrent_state == FRIEND) {
                    mFriendsRef.child(mAuth.getUid()).child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mFriendsRef.child(userId).child(mAuth.getUid()).removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    mCurrent_state = NOT_FRIEND;
                                                    mSendRequestBt.setEnabled(true);
                                                    mSendRequestBt.setText("Send Friend Request");
                                                    Toast.makeText(UserProfileActivity.this, "Unfriend Successfully", Toast.LENGTH_SHORT).show();
                                                }else {
                                                    Toast.makeText(UserProfileActivity.this, "Unfriend Failed", Toast.LENGTH_SHORT).show();

                                                }
                                            }
                                        });
                            }else {
                                Toast.makeText(UserProfileActivity.this, "Unfriend Failed", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }

            }
        });

    }
}
