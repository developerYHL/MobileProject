package com.example.mobileproject;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    ViewPager viewPager;
    PagerAdapter adapter;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 프래그먼트 조작을 위해 프래그먼트 매니저를 얻음
        FragmentManager fragmentManager = getSupportFragmentManager();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);

        viewPager = findViewById(R.id.view_pager);
        adapter = new ViewPagerAdapter(fragmentManager,3);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
                = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        viewPager.setCurrentItem(0);
                        return true;
                    case R.id.navigation_dashboard:
                        viewPager.setCurrentItem(1);
                        return true;
                    case R.id.navigation_notifications:
                        viewPager.setCurrentItem(2);
                        return true;
                }
                return false;
            }
        };

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        // ColorFragment를 findFragmentById()로 얻음
//        FragmentHomeActivity colorFragment = (FragmentHomeActivity) fragmentManager.findFragmentById
//                (R.id.color_fragment);
//        // 프래그먼트의 색상 변경
//        colorFragment.setColor(Color.BLUE);

    }

    public void change(View view) {
        FragmentHomeActivity fragment = new FragmentHomeActivity();

        int red = new Random().nextInt(256);
        int green = new Random().nextInt(256);
        int blue = new Random().nextInt(256);

        fragment.setColor(Color.rgb(red, green, blue));

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

}
