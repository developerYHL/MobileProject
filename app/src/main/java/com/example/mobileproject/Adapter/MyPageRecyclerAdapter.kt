package com.example.mobileproject.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.mobileproject.R
import com.example.mobileproject.holder.SinglePictureHolder
import com.example.mobileproject.model.DetailItem
import com.firebase.ui.firestore.FirestoreRecyclerOptions

open class MyPageRecyclerAdapter
/**
 * Create a new RecyclerView adapter that listens to a Firestore Query.  See [ ] for configuration options.
 *
 * @param options
 */
(options: FirestoreRecyclerOptions<DetailItem>) : com.firebase.ui.firestore.FirestoreRecyclerAdapter<DetailItem, SinglePictureHolder>(options) {

    override fun onBindViewHolder(holder: SinglePictureHolder, position: Int, model: DetailItem) {
        // Bind the Chat object to the ChatHolder
        // ...
        Glide.with(holder.itemView)
                .load(model.downloadUrl)
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .into(holder.imageView)
        holder.itemView.requestLayout()


    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): SinglePictureHolder {
        val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.item_picture, viewGroup, false)

        return SinglePictureHolder(view)
    }
}