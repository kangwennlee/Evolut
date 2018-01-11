package com.example.kangw.evolut;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MyProfileActivity extends AppCompatActivity {

    private Button back_button;
    private ImageButton myProfilePic;
    private TextView name, email;
    private FirebaseAuth mAuth;
    private String userName, userEmail, userProfilePic;

    private ProgressBar progressBar;
    private StorageReference mStorage;
    private static final int GALLERY_INTENT = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        back_button = findViewById(R.id.btnMyProfileBack);
        myProfilePic = findViewById(R.id.myProfilePic);
        name = findViewById(R.id.txtMyProfileName);
        email = findViewById(R.id.txtMyProfileEmail);
        progressBar = findViewById(R.id.myProfileProgressBar);

        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        userName = mAuth.getCurrentUser().getDisplayName();
        userEmail = mAuth.getCurrentUser().getEmail();
        name.setText(name.getText() + userName);
        email.setText(email.getText() + userEmail);
        setProfilePic();

        myProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final String user_id = mAuth.getCurrentUser().getUid();

        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK){
            //initialize progress bar
            progressBar.setVisibility(View.VISIBLE);
            Uri uri = data.getData();

            StorageReference filepath = mStorage.child("ProfilePic").child(user_id);

            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(MyProfileActivity.this, "Upload Successful", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);

                    String newProfilePic = taskSnapshot.getDownloadUrl().toString();
                    BitmapDownloaderTask task = new BitmapDownloaderTask(myProfilePic);
                    task.execute(newProfilePic);

                    //store into database
                    DatabaseReference fb = FirebaseDatabase.getInstance().getReference().child("User").child(user_id).child("ProfilePic");
                    fb.setValue(newProfilePic);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MyProfileActivity.this, "Upload Unsuccessful", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void setProfilePic(){
        final String user_id = mAuth.getCurrentUser().getUid();
        final DatabaseReference fb = FirebaseDatabase.getInstance().getReference().child("User").child(user_id);
        fb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("ProfilePic")) {
                    userProfilePic = dataSnapshot.child("ProfilePic").getValue().toString();
                    BitmapDownloaderTask task = new BitmapDownloaderTask(myProfilePic);
                    task.execute(userProfilePic);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
