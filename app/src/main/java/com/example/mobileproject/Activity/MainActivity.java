package com.example.mobileproject.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;

import com.example.mobileproject.Adapter.ViewPagerAdapter;
import com.example.mobileproject.R;
import com.example.mobileproject.SwipeViewPager;
import com.firebase.ui.auth.AuthUI;


public class MainActivity extends AppCompatActivity {
    SwipeViewPager viewPager;
    PagerAdapter adapter;
    Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 프래그먼트 조작을 위해 프래그먼트 매니저를 얻음
        FragmentManager fragmentManager = getSupportFragmentManager();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);

        logoutButton = findViewById(R.id.logout_button);
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

        logoutButton.setOnClickListener(v -> {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(task -> {
                        startActivity(new Intent(this, LoginActivity.class));
                        // ...
                    });
        });

        //게시글 버튼 클릭
        findViewById(R.id.camera).setOnClickListener(v -> {
            //PostActivity 로 이동
            startActivity(new Intent(this, PostActivity.class));
        });


        BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
                = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        Log.e("!!!","click1");
                        viewPager.setCurrentItem(0);
                        return true;
                    case R.id.navigation_dashboard:
                        Log.e("!!!","click2");
                        viewPager.setCurrentItem(1);
                        return true;
                    case R.id.navigation_notifications:
                        Log.e("!!!","click3");
                        viewPager.setCurrentItem(2);
                        return true;
                }
                return false;
            }
        };

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }



}
