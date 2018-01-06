package com.example.kangw.evolut.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kangw.evolut.BitmapDownloaderTask;
import com.example.kangw.evolut.R;
import com.example.kangw.evolut.RecyclerAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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
    ImageView mProfilePic;
    RecyclerView mRecycler;
    LinearLayoutManager mManager;
    RecyclerAdapter mAdapter;
    DatabaseReference mDatabase;
    FirebaseUser user;

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
        mUserName = (TextView) v.findViewById(R.id.textViewName);
        mProfilePic = (ImageView) v.findViewById(R.id.imageViewProfile);
        mBalance = (TextView) v.findViewById(R.id.textViewAmount);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        mRecycler = v.findViewById(R.id.transactionHistoryRecycler);
        mRecycler.setHasFixedSize(true);
        //Initialize name, email and profile picture and homepage fragment
        try {
            String userName = user.getDisplayName();
            if (userName == null) {
                userName = user.getPhoneNumber();
            }
            mUserName.setText(userName);
            String profilePic = user.getPhotoUrl().toString();
            BitmapDownloaderTask task = new BitmapDownloaderTask(mProfilePic);
            task.execute(profilePic);
        } catch (NullPointerException e) {
            Log.e(TAG, "Error retrieving user's detail", e);
        }
        //Insert your get Amount code here
        DatabaseReference balanceRef = mDatabase.child("Users").child(user.getUid());
        balanceRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mBalance.setText("RM " + dataSnapshot.child("Balance").getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        initRecycler();
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void initRecycler() {
        //BEGIN initialize Recycler View
        //Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);
        Query postQuery = getQuery(mDatabase);
        String[] mDataset = new String[10];
        for (int i = 0; i < 10; i++) {
            mDataset[i] = "This is element #" + i;
        }
        mAdapter = new RecyclerAdapter(mDataset);
        mRecycler.setAdapter(mAdapter);
        //FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Post>().setQuery(postQuery,Post.class).build();

    }

    private Query getQuery(DatabaseReference databaseReference) {
        return databaseReference.child("Friends").child(user.getUid());
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


