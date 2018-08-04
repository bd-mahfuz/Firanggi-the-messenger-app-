package com.everest.mahfuz.firanggi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class StartActivity extends AppCompatActivity {
    Button signUpBt, signInBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //checking internet is either connected or not
        if (!isInternetConnected()) {
            //show the message on alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(StartActivity.this);
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
        }


        signUpBt = findViewById(R.id.signUpBt);
        signInBt = findViewById(R.id.signInBt);

        signUpBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signUpIntent = new Intent(StartActivity.this,
                        SignUpActivity.class);
                startActivity(signUpIntent);
            }
        });

        signInBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = new Intent(StartActivity.this,
                        SignInActivity.class);
                startActivity(signInIntent);
            }
        });

    }


    public boolean isInternetConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            return true;
        }
        return false;
    }
}
