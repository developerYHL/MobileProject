package com.example.mobileproject.Adapter;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.mobileproject.R;
import com.example.mobileproject.fragments.FragmentHome;
import com.example.mobileproject.holder.DetailItemHolder;
import com.example.mobileproject.model.DetailItem;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class asd extends FirestoreRecyclerAdapter<DetailItem, DetailItemHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public asd(@NonNull FirestoreRecyclerOptions<DetailItem> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull DetailItemHolder holder, int position, @NonNull DetailItem model) {
        // Bind the Chat object to the ChatHolder
        // ...
        holder.title.setText(model.getTitle());
        holder.contents.setText(model.getContents() + "");

        Glide.with(holder.itemView)
                .load(model.getDownloadUrl())
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .into(holder.imageView);
    }

    @NonNull
    @Override
    public DetailItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }
}
