package com.example.kangw.evolut;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kangw.evolut.fragment.FriendListFragment;
import com.example.kangw.evolut.fragment.HomepageFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class AddFriendActivity extends AppCompatActivity {

    private static EditText friend_email;
    private static TextView addFriend_feedback, friend_info;
    private static Button addFriend_button, back_button;
    private static Button imgButton;
    private static DatabaseReference mDatabase;
    private static FirebaseAuth mAuth;
    private static String friendUserId, friendName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("User");
        mAuth = FirebaseAuth.getInstance();
        friendUserId = "";
        friendName = "";
        friend_email = (EditText) findViewById(R.id.txtFriendEmail);
        addFriend_feedback = (TextView)findViewById(R.id.txtAddFriendFeedBack);
        friend_info = (TextView)findViewById(R.id.txtFriendInfo);
        addFriend_button = (Button)findViewById(R.id.btnAddFriend);
        back_button = (Button)findViewById(R.id.btnAddFriendBack);
        imgButton = findViewById(R.id.cancelImgButton);
        addFriend_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAddFriendClicked();
                friend_info.setText("");
                addFriend_feedback.setText("");
            }
        });
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnBackClicked();
            }
        });
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friend_email.setText("");
                addFriend_feedback.setText("");
                friendUserId = "";
                friendName = "";
            }
        });
        addFriend_button.setEnabled(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        friend_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                addFriend_feedback.setText("");
                friendUserId = "";
                friendName = "";
                String email = s.toString();
                checkFriendEmail(email.toLowerCase());
            }
        });
    }

    private void checkFriendEmail(final String email){

        if(isValidEmailAddress(email)) {
            Query query = mDatabase.orderByChild("Email").equalTo(email);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String userInfo = "";
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        userInfo += "Name: " + userSnapshot.child("Name").getValue() + "\nEmail :" + userSnapshot.child("Email").getValue();
                        friendUserId = userSnapshot.getKey();
                        friendName = userSnapshot.child("Name").getValue().toString();
                    }
                    friend_info.setText(userInfo);
                    if (friendUserId.toString().compareTo(mAuth.getCurrentUser().getUid().toString()) == 0) {
                        friend_info.setText("This is your email address, you cannot add yourself as friend");
                    } else if (friendUserId != "") {
                        addFriend_button.setEnabled(true);
                    } else {
                        friend_info.setText("Invalid User");
                        addFriend_button.setEnabled(false);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else {
            addFriend_button.setEnabled(false);
        }
    }

    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }


    public void btnAddFriendClicked(){

        final String user_id = mAuth.getCurrentUser().getUid();
        final DatabaseReference friend_reference = FirebaseDatabase.getInstance().getReference().child("Friends").child(user_id).child("UID");

        friend_reference.addValueEventListener(new ValueEventListener() {
            int counter = 0;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                counter++;
                if(dataSnapshot.hasChild(friendUserId) && counter==1){
                    addFriend_feedback.setText("Action cannot be done, " + friendName + " is already your friend");
                }
                if(!dataSnapshot.hasChild(friendUserId) && counter==1){
                    friend_reference.child(friendUserId).child("Name").setValue(friendName);
                    Toast.makeText(getApplicationContext(), "Successfully added " + friendName + " in your friend list", Toast.LENGTH_SHORT).show();
                    finish();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void btnBackClicked(){
        finish();
    }
}
