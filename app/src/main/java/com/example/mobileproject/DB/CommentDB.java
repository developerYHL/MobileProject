package com.example.mobileproject.DB;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
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
