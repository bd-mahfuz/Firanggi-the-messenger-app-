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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.everest.mahfuz.firanggi.Utility.FiranggiUtility;
import com.everest.mahfuz.firanggi.db.FiranggiDB;
import com.everest.mahfuz.firanggi.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Date;

public class SignUpActivity extends AppCompatActivity {

    private TextInputLayout userNameEt;
    private TextInputLayout emailEt;
    private TextInputLayout passwordEt;
    private Button signUpActionBt;

    private Toolbar mToolbar;
    private ProgressDialog mProgressDialog;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private String mGender = "Male";
    private RadioGroup mUserGender;

    private DatabaseReference mUserRef;

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



        //getting value from radio button for user gender
        mUserGender = findViewById(R.id.userGender);
        mUserGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = findViewById(i);
                mGender = radioButton.getText().toString();
                Toast.makeText(SignUpActivity.this, mGender, Toast.LENGTH_SHORT).show();
            }
        });


        //sign up button action
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
                    signUpUser(userName, email,mGender, password);
                } else {
                    Toast.makeText(SignUpActivity.this, "Above fields are should not be empty!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void signUpUser(final String userName, final String email, final String userGender, final String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("email:", email);
                        if (task.isSuccessful()) {

                            String deviceTokeniId = FirebaseInstanceId.getInstance().getToken();

                            User user = new User();
                            user.setUserName(userName);
                            user.setEmail(email);
                            user.setGender(userGender);
                            user.setRegisterDate(FiranggiUtility.getCurrentDate());
                            user.setPassword(password);
                            user.setDeviceToken(deviceTokeniId);

                            //getting current user
                            mUser = mAuth.getCurrentUser();
                            //String uId = mUser.getUid();

                            // save the user to database
                            FiranggiDB db = new FiranggiDB(SignUpActivity.this , mUser);
                            db.addUser(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        mProgressDialog.dismiss();
                                        Intent mainIntent = new Intent(SignUpActivity.this,
                                                MainActivity.class);
                                        // handling previous back for clear all activity
                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(mainIntent);
                                        finish();
                                    } else {
                                        mProgressDialog.hide();
                                        Toast.makeText(SignUpActivity.this, "Something went wrong! user not saved. Contact with developer", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        } else {
                            mProgressDialog.hide();
                            Log.w("SignUp failure message:", task.getException());
                            Toast.makeText(SignUpActivity.this, "Sign Up failed! Try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
