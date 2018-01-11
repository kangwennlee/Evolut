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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.kangw.evolut.FriendTransactionActivity;
import com.example.kangw.evolut.NewTransactionActivity;
import com.example.kangw.evolut.R;
import com.example.kangw.evolut.RecyclerAdapter;
import com.example.kangw.evolut.models.Transactions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TransactionHistoryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TransactionHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TransactionHistoryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View mView;
    public Button mNewTransactionButton;
    Button mTransactionCancel;
    RecyclerView mRecycler;
    LinearLayoutManager mManager;
    RecyclerAdapter mAdapter;
    Transactions transaction;
    final ArrayList<Transactions> transactionArrayList = new ArrayList<>();

    private OnFragmentInteractionListener mListener;

    public TransactionHistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TransactionHistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TransactionHistoryFragment newInstance(String param1, String param2) {
        TransactionHistoryFragment fragment = new TransactionHistoryFragment();
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
        mView = inflater.inflate(R.layout.fragment_history_transaction, container, false);
        mRecycler = mView.findViewById(R.id.transactionRecycler);
        mNewTransactionButton = mView.findViewById(R.id.newTransactionButton);
        mNewTransactionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), NewTransactionActivity.class);
                startActivity(i);
            }
        });
        mTransactionCancel = mView.findViewById(R.id.newTransactionCancel);
        mTransactionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment fragment = new HomepageFragment();
                ft.replace(R.id.frame_container, fragment, "homepage").commit();
            }
        });
        Query queryPay = FirebaseDatabase.getInstance().getReference().child("Friend-Transactions").child("Pay").child(FirebaseAuth.getInstance().getUid());
        queryPay.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String timeStamp = userSnapshot.getKey().toString();
                    getPayTransactionByTimeStamp(timeStamp);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Query queryRequest = FirebaseDatabase.getInstance().getReference().child("Friend-Transactions").child("Request").child(FirebaseAuth.getInstance().getUid());
        queryRequest.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String timeStamp = userSnapshot.getKey().toString();
                    getRequestTransactionByTimeStamp(timeStamp);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Query queryMerchant = FirebaseDatabase.getInstance().getReference().child("Merchant-Transactions").child(FirebaseAuth.getInstance().getUid());
        queryMerchant.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String timeStamp = userSnapshot.getKey().toString();
                    getMerchantTransactionByTimeStamp(timeStamp);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return mView;
    }

    private void getPayTransactionByTimeStamp(String timeStamp) {
        Query query = FirebaseDatabase.getInstance().getReference().child("Friend-Transactions").child("Pay").child(FirebaseAuth.getInstance().getUid()).child(timeStamp).orderByValue();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String to = dataSnapshot.child("To").getValue().toString();
                String timeStamp = dataSnapshot.getKey().toString();
                Double amount = Double.parseDouble(dataSnapshot.child("Amount").getValue().toString());
                String comments = dataSnapshot.child("Comments").getValue().toString();
                transaction = new Transactions(to, timeStamp, amount, comments);
                transactionArrayList.add(transaction);
                initializeRVAdapter();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getRequestTransactionByTimeStamp(String timeStamp) {
        Query query = FirebaseDatabase.getInstance().getReference().child("Friend-Transactions").child("Request").child(FirebaseAuth.getInstance().getUid()).child(timeStamp).orderByValue();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String to = dataSnapshot.child("To").getValue().toString();
                String timeStamp = dataSnapshot.getKey().toString();
                Double amount = Double.parseDouble(dataSnapshot.child("Amount").getValue().toString());
                String comments = dataSnapshot.child("Comments").getValue().toString();
                transaction = new Transactions(to, timeStamp, amount, comments);
                transactionArrayList.add(transaction);
                initializeRVAdapter();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getMerchantTransactionByTimeStamp(String timeStamp) {
        Query query = FirebaseDatabase.getInstance().getReference().child("Merchant-Transactions").child(FirebaseAuth.getInstance().getUid()).child(timeStamp).orderByValue();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String to = dataSnapshot.child("To").getValue().toString();
                String timeStamp = dataSnapshot.getKey().toString();
                Double amount = Double.parseDouble(dataSnapshot.child("Amount").getValue().toString());
                String comments = dataSnapshot.child("Comments").getValue().toString();
                transaction = new Transactions(to, timeStamp, amount, comments);
                transactionArrayList.add(transaction);
                initializeRVAdapter();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initializeRVAdapter() {
        mRecycler.setRecyclerListener(new RecyclerView.RecyclerListener() {
            @Override
            public void onViewRecycled(RecyclerView.ViewHolder holder) {
                holder.setIsRecyclable(false);
            }
        });
        mManager = new LinearLayoutManager(getActivity());
        mRecycler.setLayoutManager(mManager);
        RVAdapter adapter = new RVAdapter(transactionArrayList);
        mRecycler.setAdapter(adapter);
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

    public class RVAdapter extends RecyclerView.Adapter<RVAdapter.TransactionViewHolder>{

        public class TransactionViewHolder extends RecyclerView.ViewHolder{
            CardView cardView;
           // TextView name;
            TextView time;
            TextView amount;
            TextView comment;

            TransactionViewHolder(View itemView){
                super(itemView);
                cardView = itemView.findViewById(R.id.cardView);
                time = itemView.findViewById(R.id.txtTime);
                amount = itemView.findViewById(R.id.txtAmt);
                comment = itemView.findViewById(R.id.txtComment);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = mRecycler.indexOfChild(view);
                        String time = transaction.get(position).getTime();
                        String year = time.substring(0,4).concat("-");
                        String month = time.substring(4,6).concat("-");
                        String hours = time.substring(6,11).concat(":");
                        String minutes = time.substring(11,13).concat(":");
                        String seconds = time.substring(13,15);
                        String date = " ";
                        date = date.concat(year.concat(month).concat(hours).concat(minutes).concat(seconds));
                        Bundle bundle = new Bundle();
                        bundle.putString("To", transaction.get(position).getTo());
                        bundle.putString("Time", date);
                        bundle.putString("Amount", transaction.get(position).getAmount().toString());
                        bundle.putString("Comments", transaction.get(position).getComments());

                        //set Fragmentclass Arguments
                        Fragment fragment = new TransactionDetails();
                        fragment.setArguments(bundle);
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.frame_container, fragment, "transactionDetails").commit();
                    }
                });
            }
        }

        List<Transactions> transaction;
        RVAdapter(List<Transactions> transaction){
            this.transaction = transaction;
        }

        @Override
        public TransactionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_transaction_view, parent, false);
            TransactionViewHolder transactionViewHolder = new TransactionViewHolder(view);
            return transactionViewHolder;
        }

        @Override
        public void onBindViewHolder(TransactionViewHolder holder, int position) {
            String time = transaction.get(position).getTime();
            String year = time.substring(0,4).concat("-");
            String month = time.substring(4,6).concat("-");
            String hours = time.substring(6,11).concat(":");
            String minutes = time.substring(11,13).concat(":");
            String seconds = time.substring(13,15);
            String date = " ";
            date = date.concat(year.concat(month).concat(hours).concat(minutes).concat(seconds));
           // holder.name.setText(transaction.get(position).getTo().toString());
            holder.time.setText("Date: "+date);
            holder.amount.setText("Total Amount: "+transaction.get(position).getAmount().toString());
            holder.comment.setText("Comment: "+transaction.get(position).getComments());
        }

        @Override
        public int getItemCount() {
            return transaction.size();
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        @Override
        public void onBindViewHolder(TransactionViewHolder holder, int position, List<Object> payloads) {
            super.onBindViewHolder(holder, position, payloads);
        }
    }
}
