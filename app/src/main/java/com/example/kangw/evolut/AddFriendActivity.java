package com.example.kangw.evolut;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
    private static Button addFriend_button, cancel_button;
    private static DatabaseReference mDatabase;
    private static FirebaseAuth mAuth;
    private static String friendUserId, friendName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        friendUserId = "";
        friendName = "";
        friend_email = (EditText) findViewById(R.id.txtFriendEmail);
        addFriend_feedback = (TextView)findViewById(R.id.txtAddFriendFeedBack);
        friend_info = (TextView)findViewById(R.id.txtFriendInfo);
        addFriend_button = (Button)findViewById(R.id.btnAddFriend);
        cancel_button = (Button)findViewById(R.id.btnCancel);
        addFriend_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAddFriendClicked();
            }
        });
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCancelClicked();
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
                friendUserId = "";
                friendName = "";
                String email = s.toString();
                checkFriendEmail(email.toLowerCase());
            }
        });
    }

    private void checkFriendEmail(String email){
        Query query = mDatabase.orderByChild("Email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userInfo = "";
                for(DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                    userInfo += "Name: " + userSnapshot.child("Name").getValue() + "\nEmail :" + userSnapshot.child("Email").getValue();
                    friendUserId = userSnapshot.getKey();
                    friendName = userSnapshot.child("Name").getValue().toString();
                }
                friend_info.setText(userInfo);
                if(friendUserId.toString().compareTo(mAuth.getCurrentUser().getUid().toString()) == 0){
                    friend_info.setText("This is your email address, you cannot add yourself as friend");
                }
                else if(friendUserId != ""){
                    addFriend_button.setEnabled(true);
                }
                else{
                    addFriend_button.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
                    addFriend_feedback.setText("Successfully added " + friendName + " in your friend list");
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

    public void btnCancelClicked(){
        finish();
    }
}
