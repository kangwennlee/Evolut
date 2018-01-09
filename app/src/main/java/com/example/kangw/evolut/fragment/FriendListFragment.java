package com.example.kangw.evolut.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.text.TextWatcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kangw.evolut.AddFriendActivity;
import com.example.kangw.evolut.BitmapDownloaderTask;
import com.example.kangw.evolut.R;
import com.example.kangw.evolut.RecyclerAdapter;
import com.example.kangw.evolut.models.Post;
import com.example.kangw.evolut.models.User;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FriendListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FriendListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private View mView;
    Button addFriendButton, backButton;
    private FirebaseAuth mAuth;
    RecyclerView mRecycler;
    LinearLayoutManager mManager;
    RecyclerAdapter mAdapter;
    DatabaseReference mDatabase;
    ArrayList<User> friendList;
    TextView textView;
    //User
    User user;
    String userName;
    String userEmail;
    String userProfilePic;

    public FriendListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendListFragment newInstance(String param1, String param2) {
        FriendListFragment fragment = new FriendListFragment();
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
        mAuth = FirebaseAuth.getInstance();
        String user_id = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(user_id);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_list_friend, container, false);
        addFriendButton = (Button)mView.findViewById(R.id.btnAddFriend);
        backButton = mView.findViewById(R.id.btnBack);
        textView = mView.findViewById(R.id.textView100);
        mRecycler = mView.findViewById(R.id.friendListRecycler);
        mRecycler.setHasFixedSize(true);

        return mView;
    }



    public void prepareDataset() {
        friendList = new ArrayList<>();
        Query query = mDatabase.child("UID").orderByValue();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                    String userUID = userSnapshot.getKey().toString();
                    getUserByUId(userUID);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    public void getUserByUId(final String uid){
        try {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("User");
            Query query = mDatabase.child(uid).orderByValue();
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    userName = dataSnapshot.child("Name").getValue().toString();
                    userEmail = dataSnapshot.child("Email").getValue().toString();
                    userProfilePic = dataSnapshot.child("ProfilePic").getValue().toString();
                    user = new User(uid, userName, userEmail, userProfilePic);

                    friendList.add(user);
                    initializeRVAdapter();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        catch (NullPointerException e){
            Toast.makeText(getActivity(), "Friend List cannot be retrieved" , Toast.LENGTH_LONG).show();
            HomepageFragment fragment = new HomepageFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(android.R.id.content, fragment);
            fragmentTransaction.commit();
        }

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
        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(),AddFriendActivity.class);
                startActivity(i);

            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               HomepageFragment fragment = new HomepageFragment();
               FragmentManager fragmentManager = getFragmentManager();
               FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
               fragmentTransaction.replace(R.id.frame_container, fragment, "homepage").commit();
            }
        });
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

    @Override
    public void onResume() {
        super.onResume();
        prepareDataset();
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

    public void initializeRVAdapter(){
        mRecycler.setRecyclerListener(new RecyclerView.RecyclerListener() {
            @Override
            public void onViewRecycled(RecyclerView.ViewHolder holder) {
                holder.setIsRecyclable(false);
            }
        });
        mManager = new LinearLayoutManager(getActivity());
        mRecycler.setLayoutManager(mManager);
        RVAdapter adapter = new RVAdapter(friendList);
        mRecycler.setAdapter(adapter);
    }


public class RVAdapter extends RecyclerView.Adapter<RVAdapter.FriendViewHolder>{

        public class FriendViewHolder extends RecyclerView.ViewHolder{
            CardView cardView;
            TextView friendName;
            TextView friendEmail;
            ImageView friendProfilePic;


            FriendViewHolder(View itemView){
                super(itemView);
                cardView = (CardView)itemView.findViewById(R.id.cv);
                friendName = (TextView)itemView.findViewById(R.id.person_name);
                friendEmail = (TextView)itemView.findViewById(R.id.person_email);
                friendProfilePic = (ImageView) itemView.findViewById(R.id.person_photo);

            }
        }

    List<User> friends;

    RVAdapter(List<User> friends){
        this.friends = friends;
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_friend_view, viewGroup, false);
        FriendViewHolder friendViewHolder = new FriendViewHolder(v);
        return friendViewHolder;
    }

    @Override
    public void onBindViewHolder(FriendViewHolder friendViewHolder, int i) {
            friendViewHolder.friendName.setText(friends.get(i).getName());
            friendViewHolder.friendEmail.setText(friends.get(i).getEmail());
            String profilePic = friends.get(i).getProfilePic().toString();
            if (profilePic.compareTo("@drawable/com_facebook_profile_picture_blank_square") != 0) {
                BitmapDownloaderTask task = new BitmapDownloaderTask(friendViewHolder.friendProfilePic);
                task.execute(profilePic);
            }

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

    }



}


}
