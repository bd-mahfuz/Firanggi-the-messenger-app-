package com.everest.mahfuz.firanggi;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.everest.mahfuz.firanggi.db.FiranggiDB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class StatusActivity extends AppCompatActivity {

    private Toolbar mStatusToolBar;

    private TextInputLayout mStatusUpdateEt;
    private Button mUpdateStatusButton;

    private FirebaseAuth mAuth;
    private FiranggiDB db;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mAuth = FirebaseAuth.getInstance();

        //Firebase database
        db = new FiranggiDB(this, mAuth.getCurrentUser());

        //progress dialog
        progressDialog = new ProgressDialog(this);

        //getting previous status
        String status = getIntent().getStringExtra("status");

        //setup toolbar with back button
        mStatusToolBar = findViewById(R.id.statusUpdateAppBar);
        setSupportActionBar(mStatusToolBar);
        getSupportActionBar().setTitle("Change Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mStatusUpdateEt = findViewById(R.id.statusUpdateEt);
        mUpdateStatusButton = findViewById(R.id.statusUpdateBt);

        // setting the status to the input field
        mStatusUpdateEt.getEditText().setText(status);

        mUpdateStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //setting up progress bar
                progressDialog.setTitle("Updating Status..");
                progressDialog.setMessage("Please wait while we update your status");
                progressDialog.show();

                // updating status
                String updatedStatus = mStatusUpdateEt.getEditText().getText().toString();
                db.updateUserStatus(updatedStatus).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                        }
                        else {
                            progressDialog.hide();
                            Toast.makeText(StatusActivity.this, "Status not Updated! Please Try Again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });


    }
}
