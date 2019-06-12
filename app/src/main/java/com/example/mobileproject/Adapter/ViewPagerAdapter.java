package com.example.mobileproject.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.example.mobileproject.fragments.FragmentHome;
import com.example.mobileproject.fragments.FragmentMyPage;
import com.example.mobileproject.fragments.NavigationFragment;

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
                Log.e("1","map");
                return new FragmentHome();
            case 1:
                Log.e("2","map");
                return new FragmentMyPage();
            case 2:
                Log.e("3","map");
                return new NavigationFragment();

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }


}
