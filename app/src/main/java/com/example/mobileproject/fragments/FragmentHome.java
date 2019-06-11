package com.example.mobileproject.fragments;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.example.mobileproject.Activity.MainActivity;
import com.example.mobileproject.Adapter.RecyclerAdapter;
import com.example.mobileproject.ItemClickSupport;
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

public class FragmentHome extends Fragment implements RecyclerAdapter.MyRecyclerViewClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private FirestoreRecyclerAdapter mAdapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private RecyclerView recyclerView;

    private ImageView mPreviewImageView;

    private ProgressBar mProgressBar;

    private FirebaseUser mUser;

    // Item의 클릭 상태를 저장할 array 객체
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    // 직전에 클릭됐던 Item의 position
    private int prePosition = -1;
    private ImageView imageView2;

    public FragmentHome() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView = view.findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(false);

        // 레이아웃 매니저로 LinearLayoutManager를 설정
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
//        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
//        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener(){
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Log.e("123","???");

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
                Log.e("321","!!!");
                return true;
            }
        });

        // 표시할 임시 데이터
//        List<DetailItem> dataList = new ArrayList<>();
//        dataList.add(new DetailItem("이것은 첫번째 아이템", "안드로이드 보이라고 합니다", "https://firebasestorage.googleapis.com/v0/b/mobileproject-e978a.appspot.com/o/Chrysanthemum.jpg?alt=media&token=e9570d16-8569-4f43-9d54-0fb68c9e6391"));
//        dataList.add(new DetailItem("이것은 세번째 아이템", "이번엔 세줄\n두번째 줄\n세번째 줄 입니다", "https://firebasestorage.googleapis.com/v0/b/mobileproject-e978a.appspot.com/o/Chrysanthemum.jpg?alt=media&token=e9570d16-8569-4f43-9d54-0fb68c9e6391"));
//        dataList.add(new DetailItem("이것은 두번째 아이템", "두 줄 입력도 해 볼게요\n두 줄 입니다", "https://firebasestorage.googleapis.com/v0/b/mobileproject-e978a.appspot.com/o/Chrysanthemum.jpg?alt=media&token=e9570d16-8569-4f43-9d54-0fb68c9e6391"));
//        dataList.add(new DetailItem("이것은 네번째 아이템", "잘 되네요", "https://firebasestorage.googleapis.com/v0/b/mobileproject-e978a.appspot.com/o/Chrysanthemum.jpg?alt=media&token=e9570d16-8569-4f43-9d54-0fb68c9e6391"));

//        // 어댑터 설정
//        mAdapter = new RecyclerAdapter(dataList);
//        mAdapter.setOnClickListener(this);
//        recyclerView.setAdapter(mAdapter);

        // 어댑터 설정
//        mAdapter = new RecyclerAdapter(dataList);
//        mAdapter.setOnClickListener(this);
        //recyclerView.setAdapter(mAdapter);
        queryData();

        // ItemAnimator
//        DefaultItemAnimator animator = new DefaultItemAnimator();
//        animator.setAddDuration(1000);
//        animator.setRemoveDuration(1000);
//        animator.setMoveDuration(1000);
//        animator.setChangeDuration(1000);
//        recyclerView.setItemAnimator(animator);

        // ItemDecoration
//        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), layoutManager.getOrientation());
//        recyclerView.addItemDecoration(decoration);

        Log.e("!!!","ASDADSD");
        return view;
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
                imageView2.getLayoutParams().height = value;
                imageView2.requestLayout();
                // imageView가 실제로 사라지게하는 부분
                imageView2.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            }
        });
        // Animation start
        va.start();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mAdapter != null)
        mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    @Override
    public void onItemClicked(int position) {
        Log.d(TAG, "onItemClicked: " + position);
    }

    @Override
    public void onShareButtonClicked(int position) {
        Log.d(TAG, "onShareButtonClicked: " + position);

        //mAdapter.addItem(position, new DetailItem("추가 됨", "추가 됨", "https://firebasestorage.googleapis.com/v0/b/mobileproject-e978a.appspot.com/o/Chrysanthemum.jpg?alt=media&token=e9570d16-8569-4f43-9d54-0fb68c9e6391"));
    }

    @Override
    public void onLearnMoreButtonClicked(int position) {
        Log.d(TAG, "onLearnMoreButtonClicked: " + position);

        // 아이템 삭제
        //mAdapter.removeItem(position);
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
                holder.title.setText(model.getUid());
                holder.contents.setText(model.getContents() + "");


                Glide.with(holder.itemView)
                        .load(model.getDownloadUrl())
                        .centerCrop()
                        .placeholder(R.mipmap.ic_launcher)
                        .into(holder.imageView);

                changeVisibility(selectedItems.get(position));
            }

            @NonNull
            @Override
            public HomeItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item_detail, viewGroup, false);
                imageView2 = view.findViewById(R.id.imageView2);

                return new HomeItemHolder(view);
            }
        };

        recyclerView.setAdapter(mAdapter);
    }
}
