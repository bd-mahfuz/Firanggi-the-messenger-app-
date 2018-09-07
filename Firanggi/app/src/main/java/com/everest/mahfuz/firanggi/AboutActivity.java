package com.everest.mahfuz.firanggi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class AboutActivity extends AppCompatActivity {

    private Toolbar mAboutToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        mAboutToolBar = findViewById(R.id.aboutToolbar);
        setSupportActionBar(mAboutToolBar);
        getSupportActionBar().setTitle("About");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
