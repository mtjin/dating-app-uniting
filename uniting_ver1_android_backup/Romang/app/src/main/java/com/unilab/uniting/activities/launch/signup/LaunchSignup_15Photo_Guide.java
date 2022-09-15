package com.unilab.uniting.activities.launch.signup;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.unilab.uniting.R;
import com.unilab.uniting.utils.BasicActivity;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.MyProfile;

public class LaunchSignup_15Photo_Guide extends BasicActivity {

    private TextView mToolbarTitleTextView;
    private LinearLayout mBack;
    private ImageView mGood1ImgView;
    private ImageView mGood2ImgView;
    private ImageView mGood3ImgView;
    private ImageView mGood4ImgView;
    private ImageView mGood5ImgView;
    private ImageView mGood6ImgView;
    private ImageView mGood7ImgView;
    private ImageView mGood8ImgView;
    private ImageView mBad1ImgView;
    private ImageView mBad2ImgView;
    private ImageView mBad3ImgView;
    private TextView mGood1TextView;
    private TextView mGood2TextView;
    private TextView mGood3TextView;
    private TextView mGood4TextView;
    private TextView mGood5TextView;
    private TextView mGood6TextView;
    private TextView mGood7TextView;
    private TextView mGood8TextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_signup_15_photo_guide);

        init();
        updateUI();


        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void init(){
        View view = findViewById(R.id.toolbar_profile_id);
        mBack = view.findViewById(R.id.toolbar_back);
        mToolbarTitleTextView = view.findViewById(R.id.toolbar_title);
        mToolbarTitleTextView.setText("프로필 사진 가이드");
        mGood1ImgView = findViewById(R.id.signup_ohotoguide_iv_good1);
        mGood2ImgView = findViewById(R.id.signup_ohotoguide_iv_good2);
        mGood3ImgView = findViewById(R.id.signup_ohotoguide_iv_good3);
        mGood4ImgView = findViewById(R.id.signup_ohotoguide_iv_good4);
        mGood5ImgView = findViewById(R.id.signup_ohotoguide_iv_good5);
        mGood6ImgView = findViewById(R.id.signup_ohotoguide_iv_good6);
        mGood7ImgView = findViewById(R.id.signup_ohotoguide_iv_good7);
        mGood8ImgView = findViewById(R.id.signup_ohotoguide_iv_good8);
        mBad1ImgView = findViewById(R.id.signup_ohotoguide_iv_bad1);
        mBad2ImgView = findViewById(R.id.signup_ohotoguide_iv_bad2);
        mBad3ImgView = findViewById(R.id.signup_ohotoguide_iv_bad3);
        mGood1TextView = findViewById(R.id.signup_ohotoguide_tv_good1);
        mGood2TextView = findViewById(R.id.signup_ohotoguide_tv_good2);
        mGood3TextView = findViewById(R.id.signup_ohotoguide_tv_good3);
        mGood4TextView = findViewById(R.id.signup_ohotoguide_tv_good4);
        mGood5TextView = findViewById(R.id.signup_ohotoguide_tv_good5);
        mGood6TextView = findViewById(R.id.signup_ohotoguide_tv_good6);
        mGood7TextView = findViewById(R.id.signup_ohotoguide_tv_good7);
        mGood8TextView = findViewById(R.id.signup_ohotoguide_tv_good8);



    }

    private void updateUI(){
        SharedPreferences pref = getSharedPreferences(FirebaseHelper.mUid , MODE_PRIVATE);
        String gender = pref.getString("gender", "");

        if(gender.equals("남자") || ( MyProfile.getUser().getGender() != null && MyProfile.getUser().getGender().equals("남자")) ){
            mGood1ImgView.setImageDrawable(getResources().getDrawable(R.drawable.good_boy1));
            mGood1TextView.setText("얼굴이 잘 보이는 사진");
            mGood4ImgView.setImageDrawable(getResources().getDrawable(R.drawable.good_boy2));
            mGood4TextView.setText("이목구비가 뚜렷한 사진");
            mGood6ImgView.setImageDrawable(getResources().getDrawable(R.drawable.good_boy6));
            mGood7ImgView.setImageDrawable(getResources().getDrawable(R.drawable.good_boy7));
            mGood8ImgView.setImageDrawable(getResources().getDrawable(R.drawable.good_boy5));
            mGood8TextView.setText("자연스러운 사진");
            mBad1ImgView.setImageDrawable(getResources().getDrawable(R.drawable.bad_boy2));
            mBad2ImgView.setImageDrawable(getResources().getDrawable(R.drawable.bad_boy3));


        }
    }

}
