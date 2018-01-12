package com.example.kangw.evolut;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class AddFriendActivity extends AppCompatActivity {

    private EditText friend_email;
    private TextView addFriend_feedback, friend_info;
    private Button addFriend_button, back_button;
    private ImageButton imgButton;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String friendUserId, friendName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("User");
        mAuth = FirebaseAuth.getInstance();
        friendUserId = "";
        friendName = "";
        friend_email = findViewById(R.id.txtFriendEmail);
        addFriend_feedback = findViewById(R.id.txtAddFriendFeedBack);
        friend_info = findViewById(R.id.txtFriendInfo);
        addFriend_button = findViewById(R.id.btnAddFriend);
        back_button = findViewById(R.id.btnAddFriendBack);
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
                    if (friendUserId.compareTo(mAuth.getCurrentUser().getUid()) == 0) {
                        friend_info.setText("This is your email address, you cannot add yourself as friend");
                    } else if (!Objects.equals(friendUserId, "")) {
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
