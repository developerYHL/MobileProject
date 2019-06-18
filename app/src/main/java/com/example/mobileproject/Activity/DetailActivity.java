package com.example.mobileproject.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mobileproject.Adapter.CommentRecyclerAdapter;
import com.example.mobileproject.DB.Comment;
import com.example.mobileproject.R;
import com.example.mobileproject.model.CommentItem;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DetailActivity extends AppCompatActivity {
    Context context;

    private static final String TAG = MainActivity.class.getSimpleName();
    private FirestoreRecyclerAdapter mAdapter;
    //private FirestoreRecyclerAdapter commentAdapter;
    private CommentRecyclerAdapter commentAdapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private RecyclerView recyclerView;

    private ImageView mPreviewImageView;


    private FirebaseUser mUser;

    // Item의 클릭 상태를 저장할 array 객체
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    // 직전에 클릭됐던 Item의 position
    private int prePosition = -1;
    private LinearLayout commentLayout;

    private EditText commentEditText;

    String timeStamp;

    ImageView miniProfileImage;
    TextView userNickName;
    ImageView imageView;
    TextView userNickNameTitle;
    TextView userContents;
    RecyclerView commentRecyclerView;
    EditText editText;
    ImageButton comment_post_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_detail);

        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();



        int width = (int) (display.getWidth() * 0.9); //Display 사이즈의 70%

        int height = (int) (display.getHeight() * 0.9);  //Display 사이즈의 90%

        getWindow().getAttributes().width = width;

        getWindow().getAttributes().height = height;

        context = this;

        miniProfileImage = findViewById(R.id.miniprofil_imageview);
        userNickNameTitle = findViewById(R.id.title_text);
        imageView = findViewById(R.id.imageView);
        userNickName = findViewById(R.id.contents_user_text);
        userContents = findViewById(R.id.contents_text);
        commentRecyclerView = findViewById(R.id.comment_recycler_view);
        editText = findViewById(R.id.comment_edittext);
        comment_post_button = findViewById(R.id.comment_post_button);
        commentEditText = findViewById(R.id.comment_edittext);

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        commentEditText.requestFocus();
        InputMethodManager immhide = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);


        Intent intent = getIntent();

        timeStamp = intent.getExtras().getString("time");


        DocumentReference docRef = db.collection("User").document(mUser.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        comment_post_button.setOnClickListener(v->{
                            String TAG = "CommentPost";
                            //시간
                            Long tsLong = System.currentTimeMillis()/1000;
                            String ts = tsLong.toString();

                            Map<String, Object> docData = new HashMap<>();
                            docData.put("contents", editText.getText().toString());
                            docData.put("timestamp", new Date());
                            docData.put("nickname", document.getString("nickname"));

                            db.collection("post").document(timeStamp)
                                    .collection("comment").document(ts)
                                    .set(docData)
                                    .addOnSuccessListener(aVoid -> {
                                        editText.setText("");
//                    adapter.notifyDataSetChanged();
                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                    })
                                    .addOnFailureListener(e ->
                                            Log.w(TAG, "Error writing document", e));
                        });

                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });




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
        if(mAdapter != null) {
            mAdapter.stopListening();
        }
        if(commentAdapter != null){
            commentAdapter.stopListening();
        }
    }


    private void queryData() {

        DocumentReference docRef = db.collection("post").document(timeStamp);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Glide.with(context)
                                .load(document.getString("UserProfile"))
                                .centerCrop()
                                .placeholder(R.mipmap.ic_launcher)
                                .into(miniProfileImage);

                        userNickNameTitle.setText(document.getString("nickname"));

                        Glide.with(context)
                                .load(document.getString("downloadUrl"))
                                .centerCrop()
                                .placeholder(R.mipmap.ic_launcher)
                                .into(imageView);

                        userNickName .setText(document.getString("nickname"));
                        Comment.getInstance().setReadMore(userContents, document.getString("contents") + "", 1);

                        LinearLayoutManager layoutManager = new LinearLayoutManager(context);

                        commentRecyclerView.setLayoutManager(layoutManager);

                        commentRecyclerView.setHasFixedSize(false);

                        Query query = FirebaseFirestore.getInstance()
                                .collection("post").document(timeStamp)
                                .collection("comment")
                                .orderBy("timestamp", Query.Direction.DESCENDING);

                        FirestoreRecyclerOptions<CommentItem> options = new FirestoreRecyclerOptions.Builder<CommentItem>()
                                .setQuery(query, CommentItem.class)
                                .build();

                        commentAdapter = new CommentRecyclerAdapter(options);

                        commentRecyclerView.setAdapter(commentAdapter);

                        if(commentAdapter != null){
                            commentAdapter.startListening();
                        }
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


    }


}
