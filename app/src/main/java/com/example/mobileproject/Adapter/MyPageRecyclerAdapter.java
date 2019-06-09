package com.example.mobileproject.Adapter;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.mobileproject.R;
import com.example.mobileproject.holder.DetailItemHolder;
import com.example.mobileproject.holder.SinglePictureHolder;
import com.example.mobileproject.model.DetailItem;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class MyPageRecyclerAdapter extends com.firebase.ui.firestore.FirestoreRecyclerAdapter<DetailItem, SinglePictureHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public MyPageRecyclerAdapter(@NonNull FirestoreRecyclerOptions<DetailItem> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull SinglePictureHolder holder, int position, DetailItem model) {
        // Bind the Chat object to the ChatHolder
        // ...
        Glide.with(holder.itemView)
                .load(model.getDownloadUrl())
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .into(holder.imageView);
    }

    @NonNull
    @Override
    public SinglePictureHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_picture, viewGroup, false);

        return new SinglePictureHolder(view);
    }
}