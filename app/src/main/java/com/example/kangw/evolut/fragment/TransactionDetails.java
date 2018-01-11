package com.example.kangw.evolut.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.kangw.evolut.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TransactionDetails.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TransactionDetails#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TransactionDetails extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    View mView;
    String sTime, sAmount, sComment, sTo;
    TextView txtTDTime, txtTDAmount, txtTDComment, txtTDTo;
    Button mBackTransaction;

    public TransactionDetails() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TransactionDetails.
     */
    // TODO: Rename and change types and number of parameters
    public static TransactionDetails newInstance(String param1, String param2) {
        TransactionDetails fragment = new TransactionDetails();
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
            sTime = getArguments().getString("Time");
            sAmount = getArguments().getString("Amount");
            sComment = getArguments().getString("Comments");
            sTo = getArguments().getString("To");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_transaction_details, container, false);
        txtTDAmount = mView.findViewById(R.id.txtTDAmount);
        txtTDComment = mView.findViewById(R.id.txtTDComment);
        txtTDTime = mView.findViewById(R.id.txtTDTime);
        txtTDTo = mView.findViewById(R.id.txtTDTo);
        mBackTransaction = mView.findViewById(R.id.backTransaction);
        txtTDComment.setText(sComment);
        txtTDTo.setText(sTo);
        txtTDTime.setText(sTime);
        txtTDAmount.setText(sAmount);
        mBackTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                TransactionHistoryFragment fragment = new TransactionHistoryFragment();
                ft.replace(R.id.frame_container, fragment, "TransactionHistory").commit();
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

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

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
