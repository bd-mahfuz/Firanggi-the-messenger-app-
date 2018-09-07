package com.everest.mahfuz.firanggi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class SignInActivity extends AppCompatActivity {

    private Button signInActionButton;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;

    private FirebaseAuth mAuth;
    private ProgressDialog mProgressDialog;

    private DatabaseReference mUserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();

        mProgressDialog = new ProgressDialog(this);

        signInActionButton = findViewById(R.id.signInActionBt);
        mEmail = findViewById(R.id.loginEmailEt);
        mPassword = findViewById(R.id.passwordLoginEt);

        //login event
        signInActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();

                if (!email.isEmpty() || !password.isEmpty()) {
                    mProgressDialog.show();
                    mProgressDialog.setTitle("Processing..");
                    mProgressDialog.setMessage("Signing in..Please wait!");
                    loginUser(email, password);
                } else {
                    Toast.makeText(SignInActivity.this, "Above fields are should not be empty!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mProgressDialog.dismiss();

                            String deviceTokenId = FirebaseInstanceId.getInstance().getToken();
                            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
                            mUserRef.child(mAuth.getUid()).child("deviceToken").setValue(deviceTokenId).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Intent signInIntent = new Intent(SignInActivity.this, MainActivity.class);
                                        signInIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(signInIntent);
                                        finish();
                                    }
                                }
                            });

                        } else{
                            mProgressDialog.hide();
                            Toast.makeText(SignInActivity.this, "Sign Up failed! Try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
