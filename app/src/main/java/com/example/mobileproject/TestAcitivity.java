package com.example.mobileproject;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TestAcitivity extends Fragment {
    private int mColor = Color.BLACK;
    private TextView mHelloTextView;

    public TestAcitivity() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_test_acitivity, container, false);
        mHelloTextView = (TextView) view.findViewById(R.id.hello_text);
        mHelloTextView.setBackgroundColor(mColor);
        return view;
    }

    public void setColor(int color) {
        // 텍스트 뷰의 배경색을 변경
        mColor = color;
        if (mHelloTextView != null) {
            mHelloTextView.setBackgroundColor(mColor);
        }

    }
}
