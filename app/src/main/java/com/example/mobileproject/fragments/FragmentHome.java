package com.example.mobileproject.fragments;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mobileproject.Activity.MainActivity;
import com.example.mobileproject.Adapter.CommentRecyclerAdapter;
import com.example.mobileproject.ItemClickSupport;
import com.example.mobileproject.R;
import com.example.mobileproject.holder.HomeItemHolder;
import com.example.mobileproject.model.CommentItem;
import com.example.mobileproject.model.DetailItem;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentHome extends Fragment {

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

    public FragmentHome() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        View commentView = inflater.inflate(R.layout.item_detail, container, false);

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView = view.findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(false);

        // 레이아웃 매니저로 LinearLayoutManager를 설정
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layoutManager);

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener(){
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                if (selectedItems.get(position)) {
                    // 펼쳐진 Item을 클릭 시
                    selectedItems.delete(position);
                } else {
                    // 직전의 클릭됐던 Item의 클릭상태를 지움
                    selectedItems.delete(prePosition);

                    // 클릭한 Item의 position을 저장
                    selectedItems.put(position, true);
                }
// 해당 포지션의 변화를 알림
                if (prePosition != -1) mAdapter.notifyItemChanged(prePosition);
                    mAdapter.notifyItemChanged(position);
// 클릭된 position 저장
                prePosition = position;
            }
        });

        ItemClickSupport.addTo(recyclerView).setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
                return true;
            }
        });

        queryData();
        return view;
    }

    private void changeVisibility(final boolean isExpanded) {
        // height 값을 dp로 지정해서 넣고싶으면 아래 소스를 이용
        int dpValue = 150;
        float d = getActivity().getResources().getDisplayMetrics().density;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;//(int) (dpValue * d);

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
                .collection("post");

        FirestoreRecyclerOptions<DetailItem> options = new FirestoreRecyclerOptions.Builder<DetailItem>()
                .setQuery(query, DetailItem.class)
                .build();

        mAdapter = new com.example.mobileproject.Adapter.FirestoreRecyclerAdapter(options) {
            @Override
            protected void onBindViewHolder(@NonNull HomeItemHolder holder, int position, DetailItem model) {
                // Bind the Chat object to the ChatHolder
                // ...
                holder.nickname.setText(model.getNickname());

                Glide.with(holder.itemView)
                        .load(model.getDownloadUrl())
                        .centerCrop()
                        .placeholder(R.mipmap.ic_launcher)
                        .into(holder.imageView);

                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

                //holder.contents.setText(model.getContents() + "");
                setReadMore(holder.contents, model.getContents() + "", 1);


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
        recyclerView.setAdapter(mAdapter);
    }
    public static void setReadMore(final TextView view, final String text, final int maxLine) {
        final Context context = view.getContext();
        final String expanedText = " ... 더보기";

        if (view.getTag() != null && view.getTag().equals(text)) { //Tag로 전값 의 text를 비교하여똑같으면 실행하지 않음.
            return;
        }


        List<CharSequence> lines = new ArrayList<>();
        int count = view.getLineCount();
        for (int line = 0; line < count; line++) {
            int start = view.getLayout().getLineStart(line);
            int end = view.getLayout().getLineEnd(line);
            CharSequence substring = view.getText().subSequence(start, end);
            lines.add(substring);
            Log.i("linesdd",lines.size() + "");
            Log.i("linesdd","asd");

        }

            view.setTag(text); //Tag에 text 저장
        view.setText(text); // setText를 미리 하셔야  getLineCount()를 호출가능
            //getLineCount()는 UI 백그라운드에서만 가져올수 있음
            view.post(() -> {
                if (view.getLineCount() >= maxLine) { //Line Count가 설정한 MaxLine의 값보다 크다면 처리시작

                    int lineEndIndex = view.getLayout().getLineVisibleEnd(maxLine - 1); //Max Line 까지의 text length


                    String[] split = text.split("\n"); //text를 자름
                    int itemCount = 0;

                    String lessText = "";
                    for (CharSequence item : lines) {
                        String items = item.toString();
                        itemCount ++;
                        if (itemCount >= lines.size()) { //마지막 줄일때!
                            if (items.length() >= expanedText.length()) {
                                lessText += items.substring(0, items.length() - (expanedText.length())) + expanedText;
                            } else {
                                lessText += items + expanedText;
                            }
                            break; //종료
                        }
                        lessText += items + "\n";
                    }
                    SpannableString spannableString = new SpannableString(lessText);
                    spannableString.setSpan(new ClickableSpan() {//클릭이벤트
                        @Override
                        public void onClick(View v) {
                            view.setText(text);
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) { //컬러 처리
                            ds.setColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                        }
                    }, spannableString.length() - expanedText.length(), spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    view.setText(spannableString);
                    view.setMovementMethod(LinkMovementMethod.getInstance());
                }
            });
    }
}
