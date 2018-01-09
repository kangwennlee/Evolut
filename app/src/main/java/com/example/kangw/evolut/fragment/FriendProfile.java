package com.example.kangw.evolut.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kangw.evolut.BitmapDownloaderTask;
import com.example.kangw.evolut.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FriendProfile.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FriendProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendProfile extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View mView;
    //view variables
    Button btnFriendProfileBack, btnRemoveFriend;
    TextView txtFriendName, txtFriendEmail, txtFriendPhoneNo;
    ImageView friendProfileImgView;
    //friend profile info
    String friendUID;
    String friendProfilePic;
    String friendName;
    String friendEmail;
    String friendPhoneNo;
    //database references
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    private OnFragmentInteractionListener mListener;

    public FriendProfile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendProfile.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendProfile newInstance(String param1, String param2) {
        FriendProfile fragment = new FriendProfile();
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
            //get values from FriendListFragment
            friendUID = getArguments().getString("UID");
            friendProfilePic = getArguments().getString("ProfilePic");
            friendName = getArguments().getString("Name");
            friendEmail = getArguments().getString("Email");
            friendPhoneNo = getArguments().getString("PhoneNo");
        }
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_friend_profile, container, false);
        btnFriendProfileBack = mView.findViewById(R.id.btnFriendProfileBack);
        btnRemoveFriend = mView.findViewById(R.id.btnRemoveFriend);
        txtFriendName = mView.findViewById(R.id.txtFriendProfileName);
        txtFriendEmail = mView.findViewById(R.id.txtFriendProfileEmail);
        txtFriendPhoneNo = mView.findViewById(R.id.txtFriendContactNo);
        friendProfileImgView = mView.findViewById(R.id.friendProfilePic);


        //set values into view
        if (friendProfilePic != "") {
            BitmapDownloaderTask task = new BitmapDownloaderTask(friendProfileImgView);
            task.execute(friendProfilePic);
        }
        txtFriendName.setText(txtFriendName.getText() + friendName);
        txtFriendEmail.setText(txtFriendEmail.getText() + friendEmail);
        if(friendPhoneNo != ""){
            txtFriendPhoneNo.setText(txtFriendPhoneNo.getText() + friendPhoneNo);
        }
        else{
            txtFriendPhoneNo.setText(txtFriendPhoneNo.getText() + " - ");
        }

        //on button click listener
        btnFriendProfileBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FriendListFragment fragment = new FriendListFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, fragment, "friend list").commit();
            }
        });
        btnRemoveFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_id = mAuth.getCurrentUser().getUid();
                mDatabase.child(user_id).child("UID").child(friendUID).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        Toast.makeText(getActivity(), "Friend successfully removed" , Toast.LENGTH_LONG).show();
                        FriendListFragment fragment = new FriendListFragment();
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.frame_container, fragment, "friend list").commit();
                    }
                });
            }
        });
        return mView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
