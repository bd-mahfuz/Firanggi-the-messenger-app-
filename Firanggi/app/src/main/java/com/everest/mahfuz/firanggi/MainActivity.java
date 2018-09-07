package com.everest.mahfuz.firanggi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.everest.mahfuz.firanggi.adapter.SectionsPageAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;

    private static final String TAG = "MainActivity";

    private Toolbar mToolbar;

    private ViewPager mViewPager;
    private SectionsPageAdapter mPageAdapter;

    private TabLayout tabLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();


        if (mAuth.getUid() != null) {
            mUserRef = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(mAuth.getCurrentUser().getUid());
        }

        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "token id:"+ token);

        //checking internet is either connected or not
        /*if (!isInternetConnected()) {
            //show the message on alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("No Internet Connection");
            builder.setMessage("Please check your internet connection & try again!")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            finish();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }*/

        // working with tab
        mViewPager = findViewById(R.id.viewPager);
        mPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPageAdapter);

        //setting viewPager with tab layout
        tabLayout = findViewById(R.id.mainTab);
        tabLayout.setupWithViewPager(mViewPager);



        mToolbar = findViewById(R.id.mainPageToolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Firanggi");


    }

    public boolean isInternetConnected() {
        ConnectivityManager connectivityManager =(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            return true;
        }
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            //sending to start activity if user not signed in
            sendToStartActivity();
        } else {
            mUserRef.child("online").setValue("true");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mUserRef != null) {
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
        }

    }

    private void sendToStartActivity() {
        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //inflate the menu
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.logoutMenu) {
            //logout user
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
            FirebaseAuth.getInstance().signOut();
            //sending to start activity if user logged out
            sendToStartActivity();
        } else if (item.getItemId() == R.id.accountMenu) {
            Intent accountIntent = new Intent(MainActivity.this, AccountActivity.class);
            startActivity(accountIntent);
        }
        else if (item.getItemId() == R.id.usersMenu){
            Intent userIntent = new Intent(MainActivity.this, UsersActivity.class);
            startActivity(userIntent);
        }else if (item.getItemId() == R.id.aboutMenu){
            Intent aboutIntent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(aboutIntent);
        }

        return true;
    }
}
