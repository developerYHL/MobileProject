package com.example.mobileproject.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mobileproject.R;
import com.example.mobileproject.holder.CommentItemHolder;
import com.example.mobileproject.model.CommentItem;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class CommentRecyclerAdapter extends com.firebase.ui.firestore.FirestoreRecyclerAdapter<CommentItem, CommentItemHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public CommentRecyclerAdapter(@NonNull FirestoreRecyclerOptions<CommentItem> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CommentItemHolder holder, int position, CommentItem model) {

        holder.nickname.setText(model.getNickname());
        holder.contents.setText(model.getContents());

        holder.itemView.requestLayout();
    }

    @NonNull
    @Override
    public CommentItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_comment, viewGroup, false);

        return new CommentItemHolder(view);
    }
}