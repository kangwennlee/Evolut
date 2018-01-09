package com.example.kangw.evolut;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ConfirmTransactionActivity extends AppCompatActivity {
    Button mAcceptButton;
    Button mRejectButton;
    TextView mTransactionFrom;
    Double amount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_transaction);
        mAcceptButton = findViewById(R.id.buttonAcceptTransaction);
        mRejectButton = findViewById(R.id.buttonRejectTransaction);
        mTransactionFrom = findViewById(R.id.textViewTransactionFrom);
        amount = Double.parseDouble(getIntent().getStringExtra("Amount"));
        String userName = getIntent().getStringExtra("Username");
        mTransactionFrom.setText(userName + " requested RM"+amount.toString()+" from you. Approve this transaction?");
        mAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getUid());
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Double balance = Double.parseDouble(dataSnapshot.child("Balance").getValue().toString());
                        Double newBalance = balance - amount;
                        FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getUid()).child("Balance").setValue(newBalance);
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
                finish();
            }
        });

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
