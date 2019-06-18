package com.example.mobileproject.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.example.mobileproject.Adapter.CommentRecyclerAdapter;
import com.example.mobileproject.DB.Comment;
import com.example.mobileproject.R;
import com.example.mobileproject.holder.HomeItemHolder;
import com.example.mobileproject.model.DetailItem;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;

public class DetailActivity extends AppCompatActivity {
    Context context;

    private static final String TAG = MainActivity.class.getSimpleName();
    private FirestoreRecyclerAdapter mAdapter;
    //private FirestoreRecyclerAdapter commentAdapter;
    private CommentRecyclerAdapter commentAdapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private RecyclerView recyclerView;
    private RecyclerView commentRecyclerView;

    private ImageView mPreviewImageView;

    private ProgressBar mProgressBar;

    private FirebaseUser mUser;

    // Item의 클릭 상태를 저장할 array 객체
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    // 직전에 클릭됐던 Item의 position
    private int prePosition = -1;
    private LinearLayout commentLayout;

    private EditText commentEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        context = this;
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        queryData();
    }


    @Override
    public void onStart() {
        super.onStart();
        if(mAdapter != null){
            mAdapter.startListening();
            Log.e("starttest","start11");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();

        if(commentAdapter != null){
            commentAdapter.stopListening();
        }
    }


    private void queryData() {

        Query query = FirebaseFirestore.getInstance()
                .collection("post")
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<DetailItem> options = new FirestoreRecyclerOptions.Builder<DetailItem>()
                .setQuery(query, DetailItem.class)
                .build();

        mAdapter = new com.example.mobileproject.Adapter.FirestoreRecyclerAdapter(options) {
            @Override
            protected void onBindViewHolder(@NonNull HomeItemHolder holder, int position, DetailItem model) {
                // Bind the Chat object to the ChatHolder
                // ...
                holder.nickname.setText(model.getNickname());

                holder.contentsUserNickname.setText(model.getNickname());

                Glide.with(holder.itemView)
                        .load(model.getUserProfile())
                        .centerCrop()
                        .placeholder(R.mipmap.ic_launcher)
                        .into(holder.userProfile);

                Glide.with(holder.itemView)
                        .load(model.getDownloadUrl())
                        .centerCrop()
                        .placeholder(R.mipmap.ic_launcher)
                        .into(holder.imageView);

                LinearLayoutManager layoutManager = new LinearLayoutManager(context);

                //holder.contents.setText(model.getContents() + "");
                Comment.getInstance().setReadMore(holder.contents, model.getContents() + "", 1);

                holder.commentRecyclerView.setLayoutManager(layoutManager);

                holder.commentRecyclerView.setHasFixedSize(true);

                commentAdapter = new CommentRecyclerAdapter(Comment.getInstance().CommentQuery(model));

                holder.commentRecyclerView.setAdapter(commentAdapter);

                if(commentAdapter != null){
                    commentAdapter.startListening();
                }

                //댓글전송
                holder.commentPost.setOnClickListener(v -> {
                    Comment.getInstance().CommentPost(commentEditText, model, db, commentAdapter);
                });
            }

            @NonNull
            @Override
            public HomeItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item_detail, viewGroup, false);
                commentLayout = view.findViewById(R.id.comment_layout);

                commentEditText = view.findViewById(R.id.comment_edittext);

                return new HomeItemHolder(view);
            }
        };
        recyclerView.setAdapter(mAdapter);
    }


}
