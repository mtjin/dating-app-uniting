package com.unilab.uniting.activities.launch.tutorial;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class TutorialPagerAdapter extends FragmentStatePagerAdapter {

    private int tabCount;
    private ArrayList<String> imageUrlList;
    private Context context;


    public TutorialPagerAdapter(FragmentManager fm, int tabCount, ArrayList<String> imageUrlList, Context context) {
        super(fm);
        this.tabCount = tabCount;
        this.imageUrlList = imageUrlList;
        this.context = context;
    }


    @Override
    public Fragment getItem(int position) {
        TutorialImageFragment fragment = new TutorialImageFragment();
        fragment.setImageUrl(imageUrlList.get(position));
        fragment.setIndex(position);



        return fragment;
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
