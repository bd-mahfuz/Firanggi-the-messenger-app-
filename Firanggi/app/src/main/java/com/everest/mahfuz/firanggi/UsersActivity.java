package com.everest.mahfuz.firanggi;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.everest.mahfuz.firanggi.adapter.UsersRVAdapter;
import com.everest.mahfuz.firanggi.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UsersActivity extends AppCompatActivity {

    private Toolbar mUserToolBar;
    private ProgressDialog progressDialog;

    private RecyclerView userRv;
    private RecyclerView.Adapter  mUserListAdapter;

    private ArrayList<User> userList = new ArrayList<>();

    private DatabaseReference mDbRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        progressDialog = new ProgressDialog(this);

        mUserToolBar = findViewById(R.id.usersToolBar);
        setSupportActionBar(mUserToolBar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userRv = findViewById(R.id.userRV);
        userRv.setClickable(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        userRv.setLayoutManager(layoutManager);

        mDbRef = FirebaseDatabase.getInstance().getReference().child("Users");

        mDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.show();
                for (DataSnapshot data: dataSnapshot.getChildren()) {
                    User user = data.getValue(User.class);
                    userList.add(user);
                }
                Log.d("user list size:", userList.get(0).getUserImage()+"");
                mUserListAdapter = new UsersRVAdapter(UsersActivity.this, userList);
                userRv.setAdapter(mUserListAdapter);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(UsersActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



    }
}
