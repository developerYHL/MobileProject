package com.example.mobileproject.fragments;

import android.graphics.Rect;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.example.mobileproject.Adapter.MyPageRecyclerAdapter;
import com.example.mobileproject.Adapter.asd;
import com.example.mobileproject.holder.DetailItemHolder;
import com.example.mobileproject.model.DetailItem;
import com.example.mobileproject.Activity.MainActivity;
import com.example.mobileproject.R;
import com.example.mobileproject.Adapter.RecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class FragmentMyPage extends Fragment implements RecyclerAdapter.MyRecyclerViewClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    //private FirestoreRecyclerAdapter mAdapter;
    private MyPageRecyclerAdapter mAdapter;

    private RecyclerView recyclerView;

    private ImageView mPreviewImageView;

    private ProgressBar mProgressBar;

    private FirebaseUser mUser;

    DetailItem detailItem;



    public FragmentMyPage() {
        // Required empty public constructor
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_page, container, false);
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView = view.findViewById(R.id.mypage_recycler_view);

        recyclerView.setHasFixedSize(false);

        // 레이아웃 매니저로 LinearLayoutManager를 설정
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
//        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SpacesItemDecoration(1));
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

        queryData();
        return view;
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
                .collection("post")
                .whereEqualTo("uid", mUser.getUid())
                .orderBy("uid", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<DetailItem> options = new FirestoreRecyclerOptions.Builder<DetailItem>()
                .setQuery(query, DetailItem.class)
                .build();

        mAdapter = new com.example.mobileproject.Adapter.MyPageRecyclerAdapter(options) {
        };

        recyclerView.setAdapter(mAdapter);
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