package com.example.mobileproject;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    public ViewPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                Log.e("!!!","asdasd");
                return new FragmentHomeActivity();
            case 1:
                Log.e("!!!","ddddd");
                return new FragmentMyPage();
            case 2:
                Log.e("!!!","qwe");
                return new MyPageActivity();

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
