package com.example.mobileproject.Adapter;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.example.mobileproject.Activity.PostActivity;
import com.example.mobileproject.holder.HomeItemHolder;
import com.example.mobileproject.model.DetailItem;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public abstract class FirestoreRecyclerAdapter extends com.firebase.ui.firestore.FirestoreRecyclerAdapter<DetailItem, HomeItemHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public FirestoreRecyclerAdapter(@NonNull FirestoreRecyclerOptions<DetailItem> options) {
        super(options);

    }

    @Override
    protected abstract void onBindViewHolder(@NonNull HomeItemHolder holder, int position, DetailItem model);

    @NonNull
    @Override
    public abstract HomeItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i);
}