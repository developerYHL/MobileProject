package com.example.mobileproject.DB;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.mobileproject.Adapter.CommentRecyclerAdapter;
import com.example.mobileproject.holder.CommentItemHolder;
import com.example.mobileproject.holder.HomeItemHolder;
import com.example.mobileproject.model.CommentItem;
import com.example.mobileproject.model.DetailItem;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class CommentDB {
    private static final CommentDB instance = new CommentDB();

    private CommentDB() {}

    public static CommentDB getInstance() {

        return instance;
    }


    public void CommentPost(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

        db.collection("post")
                .whereEqualTo("uid", mUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                        }
                    } else {
                    }
                });
    }



}
