package com.example.kangw.evolut.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.kangw.evolut.AddFriendActivity;
import com.example.kangw.evolut.BitmapDownloaderTask;
import com.example.kangw.evolut.FriendTransactionActivity;
import com.example.kangw.evolut.MyProfileActivity;
import com.example.kangw.evolut.NewTransactionActivity;
import com.example.kangw.evolut.R;

import com.example.kangw.evolut.TopUpActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomepageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomepageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomepageFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    TextView mUserName;
    TextView mBalance;
    ImageButton mProfilePic;
    DatabaseReference mDatabase;
    FirebaseUser user;
    ImageButton mPayMerchant;
    ImageButton mTopUpButton;
    ImageButton mNewFriend;
    ImageButton mPayFriend;
    ImageButton mHistory;
    ImageButton mFriendList;
    String userProfilePic;

    public HomepageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomepageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomepageFragment newInstance(String param1, String param2) {
        HomepageFragment fragment = new HomepageFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_homepage, container, false);
        mUserName = v.findViewById(R.id.textViewName);
        mProfilePic = v.findViewById(R.id.imageViewProfile);
        mBalance = v.findViewById(R.id.textViewAmount);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        mPayMerchant = v.findViewById(R.id.imageButtonPayMerchant);
        mTopUpButton = v.findViewById(R.id.imageButtonTopUp);
        mNewFriend = v.findViewById(R.id.imageButtonNewFriend);
        mPayFriend = v.findViewById(R.id.imageButtonPayFriend);
        mHistory = v.findViewById(R.id.imageButtonHistory);
        mFriendList = v.findViewById(R.id.imageButtonFriendList);

        //Initialize name, email and profile picture and homepage fragment
        try {
            String userName = user.getDisplayName();
            if (userName == null) {
                userName = user.getPhoneNumber();
            }
            mUserName.setText(userName);

        } catch (NullPointerException e) {
            Log.e(TAG, "Error setting user's name", e);
        }
        //Insert your get Amount code here
        DatabaseReference balanceRef = mDatabase.child("User").child(user.getUid());
        balanceRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    if(dataSnapshot.hasChild("ProfilePic")) {
                        userProfilePic = dataSnapshot.child("ProfilePic").getValue().toString();
                        BitmapDownloaderTask task = new BitmapDownloaderTask(mProfilePic);
                        task.execute(userProfilePic);
                    }
                    mBalance.setText("RM " + dataSnapshot.child("Balance").getValue());
                }catch (NullPointerException e){
                    Log.e(TAG, "Error retrieving user's detail", e);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mPayMerchant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), NewTransactionActivity.class);
                startActivity(i);
            }
        });
        mTopUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), TopUpActivity.class);
                startActivity(i);
            }
        });
        mNewFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), AddFriendActivity.class);
                startActivity(i);
            }
        });
        mHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                TransactionHistoryFragment fragment = new TransactionHistoryFragment();
                ft.replace(R.id.frame_container, fragment, "TransactionHistory").commit();
            }
        });
        mPayFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), FriendTransactionActivity.class);
                startActivity(i);
            }
        });
        mProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyProfileActivity.class);
                startActivity(intent);
            }
        });

        mFriendList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                FriendListFragment fragment = new FriendListFragment();
                ft.replace(R.id.frame_container, fragment, "FriendList").commit();
            }
        });
        return v;
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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


