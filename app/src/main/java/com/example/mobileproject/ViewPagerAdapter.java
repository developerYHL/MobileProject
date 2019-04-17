package com.example.mobileproject;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.mobileproject.fragments.FragmentHome;
import com.example.mobileproject.fragments.FragmentMyPage;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    int mNumOfTabs;
    public ViewPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new FragmentHome();
            case 1:
                return new FragmentMyPage();

            default:
                return null;
        }
    }





    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
