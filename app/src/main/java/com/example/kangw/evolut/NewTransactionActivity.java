package com.example.kangw.evolut;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
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
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NewTransactionActivity extends AppCompatActivity {

    private Button friendTransactionButton, confirm_button, cancel_button;
    private EditText txt_beneficiaryName, txt_payAmount, txt_comments;
    private TextView txt_feedback;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Drawable backgroundDefault;
    ImageButton mScanQR;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_transaction);

        friendTransactionButton = findViewById(R.id.btnFriendTransaction);
        confirm_button = findViewById(R.id.btnSingleTransactConfirm);
        cancel_button = findViewById(R.id.btnSingleTransactCancel);
        txt_beneficiaryName = findViewById(R.id.txtBeneficiaryName);
        txt_payAmount = findViewById(R.id.txtSingleTransactPaymentAmt);
        txt_comments = findViewById(R.id.txtSingleTransactComments);
        txt_feedback = findViewById(R.id.txtSingleTransactFeedback);
        mScanQR = findViewById(R.id.scanQR);
        activity = this;
        backgroundDefault = txt_beneficiaryName.getBackground();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Merchant-Transactions");

        friendTransactionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewTransactionActivity.this, FriendTransactionActivity.class);
                startActivity(intent);
            }
        });
        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnConfirmClicked();
            }
        });
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mScanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setOrientationLocked(false);
                integrator.initiateScan();

            }
        });
        txt_beneficiaryName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txt_beneficiaryName.setBackgroundDrawable(backgroundDefault);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        txt_payAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txt_payAmount.setBackgroundDrawable(backgroundDefault);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                txt_beneficiaryName.setText(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void btnConfirmClicked(){
        txt_feedback.setText("");
        String user_id = mAuth.getCurrentUser().getUid();
        String user_name = mAuth.getCurrentUser().getDisplayName();
        if(TextUtils.isEmpty(txt_payAmount.getText())){
            txt_payAmount.setBackgroundResource(R.drawable.border);
            txt_feedback.setText("*Payment Amount cannot be empty");
            txt_payAmount.requestFocus();
        }

        if(TextUtils.isEmpty(txt_beneficiaryName.getText())){
            txt_beneficiaryName.setBackgroundResource(R.drawable.border);
            txt_feedback.setText("*Beneficiary Name cannot be empty\n" + txt_feedback.getText());
            txt_beneficiaryName.requestFocus();
        }
        if(!TextUtils.isEmpty(txt_feedback.getText())){
            return;
        }

        //if all information gathered, begin transaction
        if(!TextUtils.isEmpty(txt_beneficiaryName.getText()) && !TextUtils.isEmpty(txt_payAmount.getText())){
            String comments;
            if(TextUtils.isEmpty(txt_comments.getText())){
                comments = "Payment from " + user_name + " to " + txt_beneficiaryName.getText();
            }
            else{
                comments = txt_comments.getText().toString();
            }

            final String finalComments = comments;
            final DatabaseReference dfTransaction = mDatabase.child(user_id);
            dfTransaction.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                        String currDateTime = getCurrentDataTime();
                        if(!dataSnapshot.hasChild(currDateTime)){
                            dfTransaction.child(currDateTime).child("To").setValue(txt_beneficiaryName.getText().toString());
                            dfTransaction.child(currDateTime).child("Amount").setValue(Double.parseDouble(txt_payAmount.getText().toString()));
                            dfTransaction.child(currDateTime).child("Comments").setValue(finalComments);
                        }
                    }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        //deduct from own account
        //check own acc balance
        final DatabaseReference dfAccount = FirebaseDatabase.getInstance().getReference().child("User").child(user_id).child("Balance");
        dfAccount.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                double current_balance = Double.parseDouble(dataSnapshot.getValue().toString());
                double paymentAmt  = Double.parseDouble(txt_payAmount.getText().toString());
                if (current_balance < paymentAmt){
                    txt_feedback.setText("Payment Not successful.\nYour current balance " + current_balance + " is insufficient to pay the amount of " + paymentAmt);
                }
                else{
                    dfAccount.setValue(current_balance - paymentAmt);
                    Toast.makeText(getApplicationContext(), "Payment successful" , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void resetView() {
        txt_beneficiaryName.setText("");
        txt_payAmount.setText("");
        txt_comments.setText("");
        txt_feedback.setText("");

        txt_beneficiaryName.setBackgroundDrawable(backgroundDefault);
        txt_payAmount.setBackgroundDrawable(backgroundDefault);
    }

    private String getCurrentDataTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }
}

