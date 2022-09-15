package com.unilab.uniting.fragments.profilecard;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.unilab.uniting.R;
import com.unilab.uniting.activities.setprofile.SetProfile_2Edit;
import com.unilab.uniting.utils.MyProfile;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class ProfileCardPhotoFragment extends Fragment {

    //상수
    final static String TAG = "ProfileCardTAG";

    //PagerAdapter에서 이미지 url 받아오기
    private String imageUrl;
    private int index = 0;
    private int myPhotoCount = 0;
    private boolean fromScreeningActivity = false;
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setIndex(int index) {
        this.index = index;
    }
    public void setScreening(boolean screening) {
        this.fromScreeningActivity = screening;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_profile_card_photo, container, false);

        final ImageView mPhoto0ImageView = rootView.findViewById(R.id.profilecard_fragment_iv_photo0);
        final LinearLayout blurLayout = rootView.findViewById(R.id.profilecard_fragment_linear_black);
        final Button editBtn = rootView.findViewById(R.id.profilecard_fragment_btn_edit);

        for (String url : MyProfile.getUser().getPhotoUrl()) {
            if (url != null ){
                myPhotoCount++;
            }
        }

        if(getActivity()!= null && !getActivity().isDestroyed()){
            if ((index < myPhotoCount) || fromScreeningActivity ){
                Glide.with(getActivity()).load(imageUrl).into(mPhoto0ImageView);
                blurLayout.setVisibility(View.GONE);
            } else {
                Glide.with(getActivity()).load(imageUrl).apply(bitmapTransform(new BlurTransformation(25, 8))).into(mPhoto0ImageView);
                blurLayout.setVisibility(View.VISIBLE);
            }
        }

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SetProfile_2Edit.class));
            }
        });

        return rootView;
    }

}
