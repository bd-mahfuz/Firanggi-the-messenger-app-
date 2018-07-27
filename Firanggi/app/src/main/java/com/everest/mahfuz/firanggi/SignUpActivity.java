package com.everest.mahfuz.firanggi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private TextInputLayout userNameEt;
    private TextInputLayout emailEt;
    private TextInputLayout passwordEt;
    private Button signUpActionBt;

    private Toolbar mToolbar;
    private ProgressDialog mProgressDialog;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //toolbar setting up
        mToolbar = findViewById(R.id.signUpToolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        userNameEt = findViewById(R.id.userNameEt);
        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);

        signUpActionBt = findViewById(R.id.SignUpActionBt);

        //progress bar
        mProgressDialog = new ProgressDialog(this);



        signUpActionBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = userNameEt.getEditText().getText().toString();
                String email = emailEt.getEditText().getText().toString();
                String password = passwordEt.getEditText().getText().toString();

                if (!userName.isEmpty() || !email.isEmpty() || !password.isEmpty()) {
                    //progress bar setting
                    mProgressDialog.show();
                    mProgressDialog.setTitle("Processing");
                    mProgressDialog.setMessage("Please wait while we create your account.");
                    signUpUser(userName, email, password);
                } else {
                    Toast.makeText(SignUpActivity.this, "Above fields are should not be empty!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void signUpUser(String userName, final String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("email:", email);
                        if (task.isSuccessful()) {
                            mProgressDialog.dismiss();
                            Intent mainIntent = new Intent(SignUpActivity.this,
                                    MainActivity.class);
                            startActivity(mainIntent);
                            finish();
                        } else {
                            mProgressDialog.hide();
                            Log.w("failure message:", task.getException());
                            Toast.makeText(SignUpActivity.this, "Sign Up failed! Try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
