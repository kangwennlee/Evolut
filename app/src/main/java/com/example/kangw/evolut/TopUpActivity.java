package com.example.kangw.evolut;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.kangw.evolut.viewholder.AddCard;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class TopUpActivity extends AppCompatActivity {
    Button btnNext, btnCancel;
    EditText txtAmount;
    Spinner spinBankType;
    double balance = 0, newBalance = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up);

        txtAmount = findViewById(R.id.txtAmount);
        spinBankType = findViewById(R.id.spinBankType);
        final ArrayList<String> cardArrayList = new ArrayList<>();
        cardArrayList.add("--Select a card--");
        Query query = FirebaseDatabase.getInstance().getReference().child("Card").child(FirebaseAuth.getInstance().getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String cardNumber = userSnapshot.getKey().toString();
                    if (!cardArrayList.contains(cardNumber)) {
                        cardArrayList.add(cardNumber);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        cardArrayList.add("Add New Card");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cardArrayList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinBankType.setAdapter(adapter);
        spinBankType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String str = spinBankType.getItemAtPosition(i).toString();
                if (Objects.equals(str, "Add New Card")) {
                    Intent intent = new Intent(getApplicationContext(), AddCard.class);
                    startActivity(intent);
                }
                if (Objects.equals(str, "--Select a card--")){
                    Toast.makeText(getApplicationContext(), "Please select a card.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnNext = findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnTopUpClicked();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void btnTopUpClicked() {
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getUid());
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    balance = Double.parseDouble(dataSnapshot.child("Balance").getValue().toString());
                    double amt = Double.parseDouble(txtAmount.getText().toString());
                    newBalance = balance + amt;
                FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getUid()).child("Balance").setValue(newBalance);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        finish();
    }


}
