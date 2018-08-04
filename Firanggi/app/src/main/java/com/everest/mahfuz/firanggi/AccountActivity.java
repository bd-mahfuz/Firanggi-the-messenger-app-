package com.everest.mahfuz.firanggi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.everest.mahfuz.firanggi.db.FiranggiDB;
import com.everest.mahfuz.firanggi.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class AccountActivity extends AppCompatActivity {

    private CircleImageView mCircleIv;
    private TextView mUserNameTv;
    private TextView mUserStatusTv;
    private LinearLayout backgroundLayout;

    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDbRef;

    private Button mStatusBt;
    private Button mChangeImageBt;
    private Button mBackgroundChangeButton;

    private static int GALLERY_REQUEST_CODE = 1;

    private StorageReference rootStorageRef;

    private ProgressDialog progressDialog;

    private boolean isClickBackgroundButton = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        mAuth = FirebaseAuth.getInstance();
        rootStorageRef = FirebaseStorage.getInstance().getReference();

        mCircleIv = findViewById(R.id.userIv);
        mUserNameTv = findViewById(R.id.userNameTv);
        mUserStatusTv = findViewById(R.id.userStatusTv);

        backgroundLayout = findViewById(R.id.backgroundLayout);

        mStatusBt = findViewById(R.id.changeStatusBt);
        mChangeImageBt = findViewById(R.id.changeImageBt);
        mBackgroundChangeButton = findViewById(R.id.changeBackgroundBt);


        //retrieving user data from firebase database
        mFirebaseUser = mAuth.getCurrentUser();

        mDbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mFirebaseUser.getUid());

        mDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final User user = dataSnapshot.getValue(User.class);
                mUserNameTv.setText(user.getUserName());
                if (!user.getBackgroundImage().equals("")) {

                    //load and set background image with glide library
                    Glide.with(AccountActivity.this).load(user.getBackgroundImage())
                            .into(new SimpleTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    backgroundLayout.setBackground(resource);
                                }
                            });
                }
                if (!user.getUserImage().equals("")) {
                    Picasso.get().load(user.getUserImage()).placeholder(R.drawable.avatar_placeholder).into(mCircleIv);
                }
                if (!user.getStatus().equals("")) {
                    mUserStatusTv.setText(user.getStatus());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AccountActivity.this, "Error: "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        mStatusBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //sending status with intent to the status activity
                String status = mUserStatusTv.getText().toString();
                Intent statusIntent = new Intent(AccountActivity.this, StatusActivity.class);
                statusIntent.putExtra("status", status);
                startActivity(statusIntent);
            }
        });

        mChangeImageBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isClickBackgroundButton = false;
                openGallery();

            }
        });

        mBackgroundChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isClickBackgroundButton = true;
                openGallery();
            }
        });


    }

    public void openGallery() {
        //open photo from the gallery
        Intent galleryIntent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);


        // picking image from gallery using image cropper library
                /*CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(AccountActivity.this);*/
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (isClickBackgroundButton) {

            if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
                Uri bImageUri = data.getData();

                //cropping the selected image
                CropImage.activity(bImageUri)
                        .setAspectRatio(2,1)
                        .start(this);
            }

            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {

                    //progress dialog
                    progressDialog = new ProgressDialog(AccountActivity.this);
                    progressDialog.setTitle("Uploading background Image..");
                    progressDialog.setMessage("Please wait while we upload your image.");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    Uri uri = result.getUri();

                    //storage
                    StorageReference filePathRef = rootStorageRef.child("user_background_images")
                            .child(mFirebaseUser.getUid()+"_bg.jpg");
                    filePathRef.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                try{
                                    //download the image
                                    String bImageUrl = task.getResult().getDownloadUrl().toString();

                                    //upload the image url to database
                                    mDbRef.child("backgroundImage").setValue(bImageUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                progressDialog.dismiss();
                                                Toast.makeText(AccountActivity.this, "Image Uploaded Successfully!",
                                                        Toast.LENGTH_SHORT).show();
                                                isClickBackgroundButton = false;
                                            }
                                        }
                                    });

                                } catch (Exception e) {
                                    progressDialog.dismiss();
                                    e.printStackTrace();
                                    isClickBackgroundButton = false;
                                    Toast.makeText(AccountActivity.this, "No Image found to upload!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                progressDialog.dismiss();
                                isClickBackgroundButton = false;
                                Toast.makeText(AccountActivity.this, "Image is not uploaded, Please contact with developer." , Toast.LENGTH_SHORT).show();

                            }
                        }

                    });
                }
            }

        }
        else {
            //            For user profile image upload
            if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();

                //croping the image
                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(this);


            }


            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {

                    //progress bar
                    progressDialog = new ProgressDialog(AccountActivity.this);
                    progressDialog.setTitle("Image uploading...");
                    progressDialog.setMessage("Please wait while we upload you image.");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    Uri resultUri = result.getUri();

                    // ------------- image compressing for thumb image
                    File thumbFilePath = new File(resultUri.getPath());
                    Bitmap thumbBitMap = null;
                    try {
                        thumbBitMap = new Compressor(this)
                                .setMaxHeight(200)
                                .setMaxWidth(200)
                                .setQuality(60)
                                .compressToBitmap(thumbFilePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumbBitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    final byte[] thumbByte = baos.toByteArray();
                    //-------------------------------------

                    //for main image
                    StorageReference filePathRef = rootStorageRef.child("user_profile_images")
                            .child(mFirebaseUser.getUid()+ ".jpg");
                    //for thumb image
                    final StorageReference thumbFilePathRef = rootStorageRef.child("user_profile_images")
                            .child("thumb_images").child(mFirebaseUser.getUid()+".jpg");

                    filePathRef.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {

                                try {
                                    //getting the download uri
                                    final String userImageUrl = task.getResult().getDownloadUrl().toString();

                                    UploadTask uploadTask = thumbFilePathRef.putBytes(thumbByte);
                                    uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if (task.isSuccessful()) {

                                                //download the thumbimage
                                                String thumImageUrl = task.getResult().getDownloadUrl().toString();

                                                Map map = new HashMap();
                                                map.put("userImage", userImageUrl);
                                                map.put("thumbnailImage", thumImageUrl);

                                                //upload the map url to the database
                                                mDbRef.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(AccountActivity.this, "Image Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                            }else {
                                                progressDialog.dismiss();
                                                Toast.makeText(AccountActivity.this, "Image is not uploaded, Please contact with developer." , Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });


                                }catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(AccountActivity.this, "No Image Found to Upload!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                progressDialog.dismiss();
                                Toast.makeText(AccountActivity.this, "Image is not uploaded, Please contact with developer." , Toast.LENGTH_SHORT).show();

                            }
                        }
                    });


                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    Toast.makeText(this, "Error in user Account:"+error.getMessage(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, "Cropping error! Please contact with developer.", Toast.LENGTH_SHORT).show();
                }
            }

        }



    }
}
