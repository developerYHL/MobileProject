package com.example.mobileproject.Adapter

import android.view.ViewGroup
import com.example.mobileproject.holder.HomeItemHolder
import com.example.mobileproject.model.DetailItem
import com.firebase.ui.firestore.FirestoreRecyclerOptions

abstract class FirestoreRecyclerAdapter
/**
 * Create a new RecyclerView adapter that listens to a Firestore Query.  See [ ] for configuration options.
 *
 * @param options
 */
(options: FirestoreRecyclerOptions<DetailItem>) : com.firebase.ui.firestore.FirestoreRecyclerAdapter<DetailItem, HomeItemHolder>(options) {

    abstract override fun onBindViewHolder(holder: HomeItemHolder, position: Int, model: DetailItem)

    abstract override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): HomeItemHolder
}