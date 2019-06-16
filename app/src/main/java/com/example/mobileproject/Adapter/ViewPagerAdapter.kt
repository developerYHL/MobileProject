package com.example.mobileproject.Adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.util.Log

import com.example.mobileproject.fragments.FragmentHome
import com.example.mobileproject.fragments.FragmentMyPage
import com.example.mobileproject.fragments.NavigationFragment

class ViewPagerAdapter(fm: FragmentManager, private var mNumOfTabs: Int) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? {

        when (position) {
            0 -> {
                Log.e("1", "map")
                return FragmentHome()
            }
            1 -> {
                Log.e("2", "map")
                return FragmentMyPage()
            }
            2 -> {
                Log.e("3", "map")
                return NavigationFragment()
            }

            else -> return null
        }
    }

    override fun getCount(): Int {
        return mNumOfTabs
    }


}
