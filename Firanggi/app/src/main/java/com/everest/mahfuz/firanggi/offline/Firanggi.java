package com.everest.mahfuz.firanggi.offline;

import android.app.Application;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

/**
 * Created by mahfuz on 8/11/18.
 */

public class Firanggi extends Application {

    private DatabaseReference mUserRef;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);


        //offline capability for picasso
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this, Integer.MAX_VALUE));

        Picasso build = builder.build();
        build.setIndicatorsEnabled(true);
        build.setLoggingEnabled(true);
        build.setSingletonInstance(build);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getUid() != null) {
            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());



            mUserRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot != null) {
                        mUserRef.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);
                        //mUserRef.child("online").setValue(true);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


    }
}
