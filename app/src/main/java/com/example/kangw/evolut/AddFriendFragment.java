package com.example.kangw.evolut;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.text.TextWatcher;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddFriendFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddFriendFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddFriendFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private View mView;

    private static EditText friend_email;
    private static TextView addFriend_feedback, friend_info;
    private static Button addFriend_button, cancel_button;
    private static DatabaseReference mDatabase;
    private static FirebaseAuth mAuth;
    private static String friendUserId, friendName;


    public AddFriendFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddFriendFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddFriendFragment newInstance(String param1, String param2) {
        AddFriendFragment fragment = new AddFriendFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        friendUserId = "";
        friendName = "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_add_friend, container, false);
        friend_email = (EditText) mView.findViewById(R.id.txtFriendEmail);
        addFriend_feedback = (TextView)mView.findViewById(R.id.txtAddFriendFeedBack);
        friend_info = (TextView)mView.findViewById(R.id.txtFriendInfo);
        addFriend_button = (Button)mView.findViewById(R.id.btnAddFriend);
        cancel_button = (Button)mView.findViewById(R.id.btnCancel);
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
        return mView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onStart() {
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
                checkFriendEmail(email);
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



    public void btnCancelClicked(){
        Fragment fragment = new HomepageFragment();
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.frame_container, fragment).commit();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
