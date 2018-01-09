package com.example.kangw.evolut;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.*;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cunoraz.tagview.Tag;
import com.cunoraz.tagview.TagView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.*;

public class NewTransactionActivity extends AppCompatActivity {

    private static final String TAG = "NewTransactionActivity";
    String user_token;
    private Button confirm_button, cancel_button;
    private EditText txt_comments, txt_PaymentAmt, txt_tagName;
    private TextView txt_friendAmt;
    //private View mView;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference transactionDatabaseReference;
    private ArrayList<String> tagList;
    private TagView tagGroup;
    private ArrayList<Tag> selectedTags;
    private ArrayList<String> selectedUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_transaction);
        user_token = new String();
        mAuth = FirebaseAuth.getInstance();
        String user_id = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(user_id).child("UID");
        transactionDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Transactions").child(user_id);
        prepareTags();
        txt_comments = (EditText)findViewById(R.id.txtComments);
        txt_tagName = (EditText) findViewById(R.id.txtTagName);
        txt_PaymentAmt = (EditText) findViewById(R.id.txtPaymentAmt);
        txt_PaymentAmt.setText("");
        txt_friendAmt = findViewById(R.id.txtFriendAmt);
        confirm_button = (Button)findViewById(R.id.btnConfirm);
        cancel_button = findViewById(R.id.btnCancel);
        tagGroup = (TagView)findViewById(R.id.tag_group);

        selectedTags = new ArrayList<>();;


        txt_PaymentAmt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateSubtotal();
            }
        });

        txt_tagName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setTags(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        tagGroup.setOnTagDeleteListener(new TagView.OnTagDeleteListener() {
            @Override
            public void onTagDeleted(final TagView tagView, final Tag tag, final int i) {
                AlertDialog.Builder builder = new AlertDialog.Builder(NewTransactionActivity.this);
                builder.setMessage("\"" + tag.text + "\" will be delete. Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tagView.remove(i);
                        selectedTags.remove(i);
                        updateSubtotal();
                        Toast.makeText(NewTransactionActivity.this, "\"" + tag.text + "\" deleted", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("No", null);
                builder.show();
            }
        });
        tagGroup.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(Tag tag, int i) {
                if(!tagSelected(tag)){
                    txt_tagName.setText("");
                    txt_tagName.setSelection(0);
                    tag.layoutColor = Color.parseColor("#85C1E9");
                    tag.isDeletable = true;
                    selectedTags.add(tag);
                    tagGroup.addTags(selectedTags);
                    updateSubtotal();
                }
            }
        });
        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    btnConfirmClicked();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "New Transaction cancelled.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private String getCurrentDataTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }

    private void updateSubtotal(){
        txt_friendAmt.setText("Tag your friends here:");
        if(txt_PaymentAmt.getText().toString().compareTo("") != 0 && selectedTags.size()!=0){
            int totalShared = selectedTags.size();
            double totalAmount = Double.parseDouble(txt_PaymentAmt.getText().toString());
            double sharedAmt = ((double)Math.round(totalAmount/totalShared*100))/100;
            txt_friendAmt.setText("Tag your friends here:\n(RM " + sharedAmt + " for each person)");
        }
        selectedUID = new ArrayList<>();
        for(int i=0; i<selectedTags.size();i++){
           getUIDByName(selectedTags.get(i).text);
           //txt_comments.setText(txt_comments.getText() + selectedUID.get(i));
        }
    }

    private void setTags(CharSequence cs) {
        /**
         * for empty edittext
         */
        if (cs.toString().equals("")) {
            tagGroup.addTags(selectedTags);
            return;
        }

        String text = cs.toString();
        ArrayList<Tag> tags = new ArrayList<>();
        for (int i = 0; i < selectedTags.size(); i++) {
            tags.add(selectedTags.get(i));
        }
        Tag tag;


        for (int i = 0; i < tagList.size(); i++) {
            if (tagList.get(i).toLowerCase().startsWith(text.toLowerCase())) {
                tag = new Tag(tagList.get(i));
                tag.radius = 10f;
                tag.layoutColor = Color.parseColor("#FA8072");
                tag.isDeletable = false;
                if(!tagSelected(tag)){
                    tags.add(tag);
                }
            }
        }
        tagGroup.addTags(tags);
    }

    private boolean tagSelected(Tag tag) {
        boolean selected = false;
        for (int i = 0; i < selectedTags.size(); i++) {
            if (selectedTags.get(i).text.compareTo(tag.text) == 0) {
                selected = true;
            }
        }
        return selected;
    }

    public void getUIDByName(final String name){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        String user_id = mAuth.getCurrentUser().getUid();
        Query query = mDatabase.child(user_id).child("UID").orderByValue();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                    if(userSnapshot.child("Name").getValue().toString().compareTo(name)==0) {
                        //txt_comments.setText(txt_comments.getText() + userSnapshot.getKey().toString() + " " + userSnapshot.child("Name").getValue());
                        selectedUID.add(userSnapshot.getKey().toString());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void btnConfirmClicked() throws JSONException {
        final double sharedAmt = ((double)Math.round(Double.parseDouble(txt_PaymentAmt.getText().toString())/selectedUID.size()*100))/100;
        String user_name = mAuth.getCurrentUser().getDisplayName();
        String user_comment = "Request Amount : " + sharedAmt;
        if(!TextUtils.isEmpty(txt_comments.getText())){
            user_comment += "\n" + txt_comments.getText();
        }
        //send notifications
        for(int i=0; i<selectedUID.size();i++){
           //txt_comments.setText(txt_comments.getText() + selectedUID.get(i));
            sendNotification(user_name, user_comment, selectedUID.get(i),sharedAmt);
        }


       //record the transaction
        final String comments = user_comment;

        transactionDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(int i=0; i<selectedUID.size();i++){
                    String currDateTime = getCurrentDataTime();
                    if(!dataSnapshot.hasChild(currDateTime)){
                        transactionDatabaseReference.child(currDateTime).child("To").child(selectedUID.get(i)).setValue(sharedAmt);
                        transactionDatabaseReference.child(currDateTime).child("Amount").setValue(Double.parseDouble(txt_PaymentAmt.getText().toString()));
                        transactionDatabaseReference.child(currDateTime).child("Comments").setValue(comments);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void sendNotification(final String userName, final String body, String user_uid,Double amount){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("FCM").child(user_uid);
        Query query = databaseReference;
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user_token = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        JSONObject root = new JSONObject();
        try {
                    JSONObject notification = new JSONObject();
                    JSONObject data = new JSONObject();
                    notification.put("body", body);
                    notification.put("title", "Request from "+userName);
                    data.put("Amount",amount);
                    data.put("Username",userName);
                    root.put("notification",notification);
                    root.put("data",data);
                    root.put("to", user_token);
                } catch (Exception ex) {
                   ex.printStackTrace();
                }
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST,"https://fcm.googleapis.com/fcm/send", root, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getApplicationContext(), "Message Success" , Toast.LENGTH_SHORT).show();
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Message Failed" , Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "key=AIzaSyBfnbpacwG0MD8nVHB84I60DuomRSx4DbY");
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    private void prepareTags() {
        tagList = new ArrayList<>();
        Query query = mDatabase.orderByValue();
        query.addValueEventListener(new ValueEventListener() {
            String sample = "";
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot userSnapshot: dataSnapshot.getChildren()){
                    tagList.add(userSnapshot.child("Name").getValue().toString());
                }
                txt_tagName.setText(sample);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
