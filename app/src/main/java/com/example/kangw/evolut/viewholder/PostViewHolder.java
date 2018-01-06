package com.example.kangw.evolut.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.kangw.evolut.R;
import com.example.kangw.evolut.models.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * Created by kangw on 6/1/2018.
 */

public class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView titleView;

    public PostViewHolder(View itemView) {
        super(itemView);
        titleView = itemView.findViewById(R.id.textViewElement);
        itemView.setOnClickListener(this);
    }

    public void bindToPost(Post post){
        titleView.setText(post.title);
    }

    @Override
    public void onClick(View v) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
