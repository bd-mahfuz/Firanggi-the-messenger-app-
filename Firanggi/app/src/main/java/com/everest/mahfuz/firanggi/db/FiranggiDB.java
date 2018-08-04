package com.everest.mahfuz.firanggi.db;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.everest.mahfuz.firanggi.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FiranggiDB {

    private Context context;
    private FirebaseUser firebaseUser;

    private DatabaseReference rootRef;
    private DatabaseReference userRef;

    private User user;



    public FiranggiDB(Context context, FirebaseUser user) {
        this.context = context;
        this.firebaseUser = user;

        rootRef = FirebaseDatabase.getInstance().getReference();
    }

    public Task<Void> addUser(User newUser) {
        newUser.setId(firebaseUser.getUid());
        try {
            userRef = rootRef.child("Users").child(firebaseUser.getUid());
            return userRef.setValue(newUser);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Task<Void> updateUserStatus(String status) {
        try {
            userRef = rootRef.child("Users").child(firebaseUser.getUid());
            return userRef.child("status").setValue(status);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public User getCurrentUserInfo() {
        String userId = firebaseUser.getUid();

        userRef = rootRef.child("Users").child(firebaseUser.getUid());
        userRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = (User) dataSnapshot.getValue(User.class);

                Toast.makeText(context, "data snapsot: ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return user;
    }
}
