package com.unilab.uniting.activities.launch.tutorial;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.unilab.uniting.R;

public class TutorialImageFragment extends Fragment {

    //PagerAdapter에서 이미지 url 받아오기

    private String imageUrl;
    private int index = 0;
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public void setIndex(int index) {
        this.index = index;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_tutorial, container, false);

        final ImageView mPhoto0ImageView = rootView.findViewById(R.id.fragment_tutorial_iv_photo);

        if(getActivity()!= null && !getActivity().isDestroyed()){
            Glide.with(getActivity()).load(imageUrl).into(mPhoto0ImageView);
        }


        return rootView;
    }
}
