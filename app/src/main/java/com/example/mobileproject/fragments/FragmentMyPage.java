package com.example.mobileproject.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mobileproject.DetailItem;
import com.example.mobileproject.MainActivity;
import com.example.mobileproject.R;
import com.example.mobileproject.RecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

public class FragmentMyPage extends Fragment implements RecyclerAdapter.MyRecyclerViewClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerAdapter mAdapter;



    public FragmentMyPage() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_fragment_my_page, container, false);


        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(false);

        // 레이아웃 매니저로 LinearLayoutManager를 설정
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
//        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
//        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        // 표시할 임시 데이터
        List<DetailItem> dataList = new ArrayList<>();
        dataList.add(new DetailItem("이것은 첫번째 아이템", "안드로이드 보이라고 합니다"));
        dataList.add(new DetailItem("이것은 세번째 아이템", "이번엔 세줄\n두번째 줄\n세번째 줄 입니다"));
        dataList.add(new DetailItem("이것은 두번째 아이템", "두 줄 입력도 해 볼게요\n두 줄 입니다"));
        dataList.add(new DetailItem("이것은 네번째 아이템", "잘 되네요"));

        // 어댑터 설정
        mAdapter = new RecyclerAdapter(dataList);
        mAdapter.setOnClickListener(this);
        recyclerView.setAdapter(mAdapter);

        // ItemAnimator
        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setAddDuration(1000);
        animator.setRemoveDuration(1000);
        animator.setMoveDuration(1000);
        animator.setChangeDuration(1000);
        recyclerView.setItemAnimator(animator);

        // ItemDecoration
        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(decoration);

        Log.e("!!!","ASDADSD");

        return view;
    }

    @Override
    public void onItemClicked(int position) {
        Log.d(TAG, "onItemClicked: " + position);
    }

    @Override
    public void onShareButtonClicked(int position) {
        Log.d(TAG, "onShareButtonClicked: " + position);

        mAdapter.addItem(position, new DetailItem("추가 됨", "추가 됨"));
    }

    @Override
    public void onLearnMoreButtonClicked(int position) {
        Log.d(TAG, "onLearnMoreButtonClicked: " + position);

        // 아이템 삭제
        mAdapter.removeItem(position);
    }
}
