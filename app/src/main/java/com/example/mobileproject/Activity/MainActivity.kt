package com.example.mobileproject.Activity

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import com.example.mobileproject.Adapter.ViewPagerAdapter
import com.example.mobileproject.R
import com.example.mobileproject.SwipeViewPager
import com.firebase.ui.auth.AuthUI


class MainActivity : AppCompatActivity() {
    private lateinit var viewPager: SwipeViewPager
    private lateinit var adapter: PagerAdapter
    private lateinit var logoutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 프래그먼트 조작을 위해 프래그먼트 매니저를 얻음
        val fragmentManager = supportFragmentManager

        val navigation = findViewById<View>(R.id.navigation) as BottomNavigationView

        logoutButton = findViewById(R.id.logout_button)
        viewPager = findViewById(R.id.view_pager)
        adapter = ViewPagerAdapter(fragmentManager, 3)
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(i: Int, v: Float, i1: Int) {}

            override fun onPageSelected(i: Int) {

            }

            override fun onPageScrollStateChanged(i: Int) {

            }
        })

        logoutButton.setOnClickListener { v ->
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener { task ->
                        startActivity(Intent(this, LoginActivity::class.java))
                        // ...
                    }
        }

        //게시글 버튼 클릭
        findViewById<View>(R.id.camera).setOnClickListener {
            //PostActivity 로 이동
            startActivity(Intent(this, PostActivity::class.java))
        }


        val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    Log.e("!!!", "click1")
                    viewPager.currentItem = 0
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_dashboard -> {
                    Log.e("!!!", "click2")
                    viewPager.currentItem = 1
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_notifications -> {
                    Log.e("!!!", "click3")
                    viewPager.currentItem = 2
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }


}
