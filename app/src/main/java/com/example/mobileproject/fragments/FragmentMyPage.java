package com.example.mobileproject.fragments;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mobileproject.Activity.MainActivity;
import com.example.mobileproject.Adapter.CommentRecyclerAdapter;
import com.example.mobileproject.Adapter.MyPageRecyclerAdapter;
import com.example.mobileproject.DB.Comment;
import com.example.mobileproject.ItemClickSupport;
import com.example.mobileproject.R;
import com.example.mobileproject.holder.HomeItemHolder;
import com.example.mobileproject.model.CommentItem;
import com.example.mobileproject.model.DetailItem;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FragmentMyPage extends Fragment {

    private static final String TAG = MainActivity.class.getSimpleName();
    //private FirestoreRecyclerAdapter mAdapter;
    private MyPageRecyclerAdapter mAdapter;
    private FirestoreRecyclerAdapter linearAdapter;
    private CommentRecyclerAdapter commentAdapter;

    private com.example.mobileproject.Adapter.FirestoreRecyclerAdapter.MyRecyclerViewClickListener mListener;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private RecyclerView recyclerView;
    private RecyclerView linearRecyclerView;
    private RecyclerView commentRecyclerView;

    private LinearLayout gridItemLayout;
    private LinearLayout linearItemLayout;
    private LinearLayout commentLayout;

    private FirebaseUser mUser;

    DetailItem detailItem;

    private Button changeGridViewButton;
    private Button changeLinearViewButton;

    private EditText commentEditText;
    private Button commentOpenButton;

    private TextView idTextView;
    private ImageView idImageView;

    // Item의 클릭 상태를 저장할 array 객체
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    // 직전에 클릭됐던 Item의 position
    private int prePosition = -1;
    //private ImageView imageView2;


    private String[] listItems;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_GALLAY = 3;

    public FragmentMyPage() {
        // Required empty public constructor
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_page, container, false);
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView = view.findViewById(R.id.mypage_grid_recycler_view);
        linearRecyclerView = view.findViewById(R.id.mypage_linear_recycler_view);
        commentRecyclerView = view.findViewById(R.id.comment_recycler_view);
        commentLayout = view.findViewById(R.id.comment_layout);

        changeGridViewButton = view.findViewById(R.id.change_grid_button);
        changeLinearViewButton = view.findViewById(R.id.change_Linear_button);

        gridItemLayout = view.findViewById(R.id.grid_item_layout);
        linearItemLayout = view.findViewById(R.id.linear_item_layout);

        idTextView = view.findViewById(R.id.ID);
        idImageView = view.findViewById(R.id.id_imageview);

        recyclerView.setHasFixedSize(false);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new SpacesItemDecoration(1));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearRecyclerView.setLayoutManager(linearLayoutManager);
        linearRecyclerView.addItemDecoration(new SpacesItemDecoration(1));

        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) linearItemLayout.getLayoutParams();
        LinearLayout.LayoutParams gridParams = (LinearLayout.LayoutParams) gridItemLayout.getLayoutParams();

        changeGridViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearParams.weight = 0f;
                linearItemLayout.setLayoutParams(linearParams);

                gridParams.weight = 1f;
                gridItemLayout.setLayoutParams(gridParams);

                gridItemLayout.setVisibility(View.VISIBLE);
                linearItemLayout.setVisibility(View.INVISIBLE);
            }
        });

        changeLinearViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearParams.weight = 1f;
                linearItemLayout.setLayoutParams(linearParams);

                gridParams.weight = 0f;
                gridItemLayout.setLayoutParams(gridParams);

                gridItemLayout.setVisibility(View.INVISIBLE);
                linearItemLayout.setVisibility(View.VISIBLE);
            }
        });

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener(){
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Log.e("123","???");
            }
        });

        ItemClickSupport.addTo(recyclerView).setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
                Log.e("321","!!!");
                return true;
            }
        });

        ItemClickSupport.addTo(linearRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener(){
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {

            }
        });

        ItemClickSupport.addTo(linearRecyclerView).setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
                Log.e("321","!!!");
                return true;
            }
        });

        idImageView.setOnClickListener(v -> {
            // Firebase에 추가
            listItems = new String[]{"사진 촬영", "앨범에서 선택", "기본 이미지"};
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
            mBuilder.setTitle("촬영");
            mBuilder.setIcon(R.drawable.icon);
            mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(which == 0){
                        dispatchTakePictureIntent();
                    }else if(which == 1) {
                        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i, REQUEST_IMAGE_GALLAY);
                    }else {
                        deleteDb();
                        idImageView.setImageResource(R.drawable.ic_menu_camera);
                    }
                    dialog.dismiss();
                }
            });
            mBuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog mDialog = mBuilder.create();
            mDialog.show();
        });

        showID();
        queryData();
        LinearQueryData();
        return view;
    }

    private void uploadPicture() {
        StorageReference storageRef = storage.getReference()
                .child("images/" + System.currentTimeMillis() + ".jpg");

        idImageView.setDrawingCacheEnabled(true);
        idImageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) idImageView.getDrawable()).getBitmap();

        // 이미지 줄이기
        bitmap = resizeBitmapImage(bitmap, 300);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageRef.putBytes(data);
        uploadTask.addOnFailureListener(exception -> {
            Toast.makeText(getActivity(), "업로드에 실패했습니다.", Toast.LENGTH_SHORT).show();
        }).addOnSuccessListener(taskSnapshot -> {
            // 성공
            storageRef.getDownloadUrl().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Log.d(TAG, "uploadPicture: " + downloadUri);
                    writeDb(downloadUri);
                } else {
                    // Handle failures
                    // ...
                }
            });
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public Bitmap resizeBitmapImage(Bitmap source, int maxResolution) {
        int width = source.getWidth();
        int height = source.getHeight();
        int newWidth = width;
        int newHeight = height;
        float rate = 0.0f;

        if (width > height) {
            if (maxResolution < width) {
                rate = maxResolution / (float) width;
                newHeight = (int) (height * rate);
                newWidth = maxResolution;
            }
        } else {
            if (maxResolution < height) {
                rate = maxResolution / (float) height;
                newWidth = (int) (width * rate);
                newHeight = maxResolution;
            }
        }

        return Bitmap.createScaledBitmap(source, newWidth, newHeight, true);
    }

    private void writeDb(Uri downloadUri) {
        Map<String, Object> post = new HashMap<>();
        post.put("downloadUrl", downloadUri.toString());

        db.collection("User").document(mUser.getUid())
                .set(post, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "성공", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "실패", Toast.LENGTH_SHORT).show();
                        }
                    });
    }

    private void deleteDb() {
        Map<String, Object> post = new HashMap<>();
        post.put("downloadUrl", FieldValue.delete());

        db.collection("User").document(mUser.getUid())
                .set(post, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "성공", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "실패", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void changeVisibility(final boolean isExpanded) {
        // height 값을 dp로 지정해서 넣고싶으면 아래 소스를 이용
        int dpValue = 150;
        float d = getActivity().getResources().getDisplayMetrics().density;
        int height = (int) (dpValue * d);

        // ValueAnimator.ofInt(int... values)는 View가 변할 값을 지정, 인자는 int 배열
        ValueAnimator va = isExpanded ? ValueAnimator.ofInt(0, height) : ValueAnimator.ofInt(height, 0);
        // Animation이 실행되는 시간, n/1000초
        va.setDuration(600);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // value는 height 값
                int value = (int) animation.getAnimatedValue();
                // imageView의 높이 변경
                commentLayout.getLayoutParams().height = value;
                commentLayout.requestLayout();
                // imageView가 실제로 사라지게하는 부분
                commentLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            }
        });
        // Animation start
        va.start();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mAdapter != null)
            linearAdapter.startListening();
            mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        linearAdapter.stopListening();
        mAdapter.stopListening();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_IMAGE_GALLAY && data != null){
            Uri image = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), image);
                idImageView.setImageBitmap(bitmap);
                uploadPicture();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //IMAGE_CAPTURE
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            idImageView.setImageBitmap(imageBitmap);
            uploadPicture();
        }
    }

    private void queryData() {
        Query query = FirebaseFirestore.getInstance()
                .collection("post")
                .whereEqualTo("uid", mUser.getUid());

        FirestoreRecyclerOptions<DetailItem> options = new FirestoreRecyclerOptions.Builder<DetailItem>()
                .setQuery(query, DetailItem.class)
                .build();

        mAdapter = new com.example.mobileproject.Adapter.MyPageRecyclerAdapter(options) {
        };

        recyclerView.setAdapter(mAdapter);
    }

    private  void showID(){
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("User").document(mUser.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                idTextView.setText(documentSnapshot.getString("nickname"));
                if(documentSnapshot.getString("downloadUrl") != null) {
                    Glide.with(getActivity())
                            .load(documentSnapshot.getString("downloadUrl"))
                            .into(idImageView);
                }else {
                    idImageView.setImageResource(R.drawable.ic_menu_camera);
                }
            }
        });

    }

    private void LinearQueryData() {


        Query query = FirebaseFirestore.getInstance()
                .collection("post")
                .whereEqualTo("uid", mUser.getUid());

        FirestoreRecyclerOptions<DetailItem> options = new FirestoreRecyclerOptions.Builder<DetailItem>()
                .setQuery(query, DetailItem.class)
                .build();

        linearAdapter = new com.example.mobileproject.Adapter.FirestoreRecyclerAdapter(options) {
            @Override
            protected void onBindViewHolder(@NonNull HomeItemHolder holder, int position, DetailItem model) {
                // Bind the Chat object to the ChatHolder
                // ...
                holder.nickname.setText(model.getNickname());
                holder.contents.setText(model.getContents() + "");
                //Log.e("log",model.getTimeStamp());

                Glide.with(holder.itemView)
                        .load(model.getDownloadUrl())
                        .centerCrop()
                        .placeholder(R.mipmap.ic_launcher)
                        .into(holder.imageView);


                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

                Comment.getInstance().setReadMore(holder.contents, model.getContents() + "", 1);

                holder.contents.setText(model.getContents() + "");

                holder.commentRecyclerView.setLayoutManager(layoutManager);

                holder.commentRecyclerView.setHasFixedSize(true);

                Query query = FirebaseFirestore.getInstance()
                        .collection("post").document(model.getTimeStamp())
                        .collection("comment");

                FirestoreRecyclerOptions<CommentItem> options = new FirestoreRecyclerOptions.Builder<CommentItem>()
                        .setQuery(query, CommentItem.class)
                        .build();

                commentAdapter = new CommentRecyclerAdapter(options);

                holder.commentRecyclerView.setAdapter(commentAdapter);

                if(commentAdapter != null){
                    commentAdapter.startListening();
                }

                //댓글달기 버튼 눌렀을때
                holder.commentOpenButton.setOnClickListener(v->{
                    if (selectedItems.get(position)) {
                        // 펼쳐진 Item을 클릭 시
                        Log.e("delete1","delete");
                        selectedItems.delete(position);
                    } else {
                        // 직전의 클릭됐던 Item의 클릭상태를 지움
                        Log.e("delete2","delete");
                        selectedItems.delete(prePosition);
                        // 클릭한 Item의 position을 저장
                        selectedItems.put(position, true);
                    }
                    // 해당 포지션의 변화를 알림
                    if (prePosition != -1) linearAdapter.notifyItemChanged(prePosition);
                    linearAdapter.notifyItemChanged(position);
                    // 클릭된 position 저장
                    prePosition = position;
                });

                holder.commentPost.setOnClickListener(v -> {

                    //시간
                    Long tsLong = System.currentTimeMillis()/1000;
                    String ts = tsLong.toString();

                    Map<String, Object> docData = new HashMap<>();
                    docData.put("contents", commentEditText.getText().toString());
                    docData.put("timestamp", new Date());
                    docData.put("nickname", model.getNickname());

                    db.collection("post").document(model.getTimeStamp())
                            .collection("comment").document(ts)
                            .set(docData)
                            .addOnSuccessListener(aVoid ->
                                    Log.d(TAG, "DocumentSnapshot successfully written!"))
                            .addOnFailureListener(e ->
                                    Log.w(TAG, "Error writing document", e));
                });

                changeVisibility(selectedItems.get(position));
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



        linearRecyclerView.setAdapter(linearAdapter);
    }
}



class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public SpacesItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;

        // Add top margin only for the first item to avoid double space between items
        if (parent.getChildLayoutPosition(view) == 0) {
            outRect.top = space;
        } else {
            outRect.top = 0;
        }
    }
}