package com.example.kangw.evolut;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ConfirmTransactionActivity extends AppCompatActivity {
    Button mAcceptButton;
    Button mRejectButton;
    TextView mTransactionFrom;
    Double amount;
    String requestedUserName;
    String requestedUserUID;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private double currentUserBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_transaction);
        mAcceptButton = findViewById(R.id.buttonAcceptTransaction);
        mRejectButton = findViewById(R.id.buttonRejectTransaction);
        mTransactionFrom = findViewById(R.id.textViewTransactionFrom);
        amount = Double.parseDouble(getIntent().getStringExtra("Amount"));
        requestedUserName = getIntent().getStringExtra("Username");
        requestedUserUID = getIntent().getStringExtra("UID");
        mAuth = FirebaseAuth.getInstance();
        mTransactionFrom.setText(requestedUserName + " requested RM"+amount.toString()+" from you. Approve this transaction?");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("User");

        mAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_id = mAuth.getCurrentUser().getUid();
                //deduct from account
                final DatabaseReference mDeduct = mDatabase.child(user_id).child("Balance");
                mDeduct.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    //deduct from amount
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        currentUserBalance = Double.parseDouble(dataSnapshot.getValue().toString());
                        if(currentUserBalance >= amount){
                            Double newBalance = currentUserBalance - amount;
                            mDeduct.setValue(newBalance);
                            recordPayment();
                            Toast.makeText(ConfirmTransactionActivity.this,"Payment Successful",Toast.LENGTH_LONG).show();


                        }
                        else{
                            //PROMPT ERROR MESSAGE (BALANCE INSUFFICIENT)
                            Toast.makeText(ConfirmTransactionActivity.this,"Payment Unsuccessful, balance insufficient",Toast.LENGTH_LONG).show();
                        }
                        //add amount to user
                        final DatabaseReference mAdd = mDatabase.child(requestedUserUID).child("Balance");
                        mAdd.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(currentUserBalance > amount) {

                                    double requestedUserCurrentBalance = Double.parseDouble(dataSnapshot.getValue().toString());
                                    mAdd.setValue(requestedUserCurrentBalance + amount);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                finish();
            }

        });
        mRejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String user_name = mAuth.getCurrentUser().getDisplayName();
                //sendNotification(user_name, "Request Rejected",requestedUserUID ,amount);
                Toast.makeText(ConfirmTransactionActivity.this,"Payment Unsuccessful, Request Rejected",Toast.LENGTH_LONG).show();
                finish();
            }
        });

    }

    public void recordPayment(){
        String user_id = mAuth.getCurrentUser().getUid();
        final DatabaseReference dfTransaction = FirebaseDatabase.getInstance().getReference().child("Friend-Transactions").child("Pay").child(user_id);
        dfTransaction.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String currDateTime = getCurrentDataTime();
                if(!dataSnapshot.hasChild(currDateTime)){
                    dfTransaction.child(currDateTime).child("To").setValue(requestedUserUID);
                    dfTransaction.child(currDateTime).child("Amount").setValue(amount);
                    dfTransaction.child(currDateTime).child("Comments").setValue("Trasanction request from " + requestedUserName + " accepted");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private String getCurrentDataTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return sdf.format(new Date());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
