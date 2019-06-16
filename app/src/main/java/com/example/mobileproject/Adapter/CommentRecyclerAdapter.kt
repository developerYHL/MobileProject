package com.example.mobileproject.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.mobileproject.R
import com.example.mobileproject.holder.CommentItemHolder
import com.example.mobileproject.model.CommentItem
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class CommentRecyclerAdapter
/**
 * Create a new RecyclerView adapter that listens to a Firestore Query.  See [ ] for configuration options.
 *
 * @param options
 */
(options: FirestoreRecyclerOptions<CommentItem>) : com.firebase.ui.firestore.FirestoreRecyclerAdapter<CommentItem, CommentItemHolder>(options) {

    override fun onBindViewHolder(holder: CommentItemHolder, position: Int, model: CommentItem) {

        holder.nickname.text = model.nickname
        holder.contents.text = model.comment

        holder.itemView.requestLayout()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): CommentItemHolder {
        val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.item_picture, viewGroup, false)


        return CommentItemHolder(view)
    }
}