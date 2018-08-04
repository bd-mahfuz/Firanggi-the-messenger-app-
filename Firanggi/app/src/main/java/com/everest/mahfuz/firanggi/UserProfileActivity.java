package com.everest.mahfuz.firanggi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class UserProfileActivity extends AppCompatActivity {

    private TextView userIdTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        String userId = getIntent().getStringExtra("userId");

        userIdTv = findViewById(R.id.user_idTv);

        userIdTv.setText(userId+"");
    }
}
