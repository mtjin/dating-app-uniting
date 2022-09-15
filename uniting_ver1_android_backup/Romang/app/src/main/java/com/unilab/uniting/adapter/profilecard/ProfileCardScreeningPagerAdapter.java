package com.unilab.uniting.adapter.profilecard;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.unilab.uniting.fragments.profilecard.ProfileCardPhotoFragment;

import java.util.ArrayList;

public class ProfileCardScreeningPagerAdapter extends FragmentStatePagerAdapter {

    private int tabCount;
    private ArrayList<String> imageUrlList;

    public ProfileCardScreeningPagerAdapter(FragmentManager fm, int tabCount, ArrayList<String> imageUrlList) {
        super(fm);
        this.tabCount = tabCount;
        this.imageUrlList = imageUrlList;
    }


    @Override
    public Fragment getItem(int position) {
        ProfileCardPhotoFragment fragment = new ProfileCardPhotoFragment();
        fragment.setImageUrl(imageUrlList.get(position));
        fragment.setIndex(position);
        fragment.setScreening(true);
        return fragment;
    }

    @Override
    public int getCount() {
        return tabCount;
    }

}


