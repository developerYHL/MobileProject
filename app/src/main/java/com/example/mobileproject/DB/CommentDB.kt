package com.example.mobileproject.DB

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CommentDB private constructor() {


    fun CommentPost() {
        val db = FirebaseFirestore.getInstance()
        val mUser = FirebaseAuth.getInstance().currentUser

        db.collection("post")
                .whereEqualTo("uid", mUser!!.uid)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result!!) {
                        }
                    } else {
                    }
                }
    }

    companion object {
        val instance = CommentDB()
    }


}
