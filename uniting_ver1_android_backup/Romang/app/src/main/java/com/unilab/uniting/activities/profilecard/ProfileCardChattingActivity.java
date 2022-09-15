package com.unilab.uniting.activities.profilecard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.unilab.uniting.R;
import com.unilab.uniting.adapter.profilecard.ProfileCardPagerAdapter;
import com.unilab.uniting.fragments.dialog.DialogBlockFragment;
import com.unilab.uniting.fragments.dialog.DialogMoreFragment;
import com.unilab.uniting.model.User;
import com.unilab.uniting.square.SquareViewPager;
import com.unilab.uniting.utils.BasicActivity;
import com.unilab.uniting.utils.CircleAnimIndicator;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.OnSingleClickListener;
import com.unilab.uniting.utils.Strings;

import java.util.ArrayList;


public class ProfileCardChattingActivity extends BasicActivity implements DialogBlockFragment.BlockListener {

    //상수
    final static String TAG = "profilecardMainTAG";
    final static long High_Score = 1;
    final static int CHAT_PAGE = 4;

    //변수
    private String partnerUid;
    private User mPartnerUser; //상대방 유저 객체


    //뷰 세팅
    private LinearLayout mScoreLayout;
    private Button mLowScoreButton;
    private Button mHighScoreButton;
    private ImageView mUniversityBadge;
    private ImageView mMajorBadge;
    private LinearLayout mUniversityLayout;
    private LinearLayout mMajorLayout;
    private TextView mNickNameAge;
    private TextView mHeight;
    private TextView mUniversity;
    private TextView mMajor;
    private TextView mEvaluationText;
    private TextView mLocation;
    private TextView mPersonality;
    private TextView mBloodType;
    private TextView mReligion;
    private TextView mDrinking;
    private TextView mSmoking;
    private TextView mStory0;
    private TextView mStory1;
    private TextView mStory2;
    private TextView mSelfIntroduction;
    private Button mLikeButton;
    private Button mChattingButton;
    private ImageView mMessageImageView;
    private TextView mMessageTextView;
    private LinearLayout mMessageLinearLayout;
    private RelativeLayout mLoaderLayout;
    private LinearLayout mStoryTotalLayout;
    private LinearLayout mStory0Layout;
    private LinearLayout mStory1Layout;
    private LinearLayout mStory2Layout;

    private ImageView mMore;
    private ImageView mBack;

    OnSingleClickListener onClickListener;

    //뷰페이저
    ProfileCardPagerAdapter profileCardPagerAdapter;
    SquareViewPager viewPager;
    CircleAnimIndicator circleAnimIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_card_main);

        init();     //바인딩 및 인텐트
        updateUI();
        setOnClickListener();
    }



    private void init(){
        //바인딩
        mScoreLayout = findViewById(R.id.profile_card_btn_linear_score);
        mLowScoreButton = findViewById(R.id.profile_card_btn_low_score);
        mHighScoreButton = findViewById(R.id.profile_card_btn_high_score);
        mUniversityLayout = findViewById(R.id.profile_card_linear_university);
        mMajorLayout = findViewById(R.id.profile_card_linear_major);
        mUniversityBadge = findViewById(R.id.profile_card_iv_university_badge);
        mMajorBadge = findViewById(R.id.profile_card_iv_major_badge);

        mNickNameAge = findViewById(R.id.profile_card_tv_nickname_age);
        mHeight = findViewById(R.id.profile_card_tv_height);
        mUniversity = findViewById(R.id.profile_card_tv_university);
        mMajor = findViewById(R.id.profile_card_tv_major);
        mLocation = findViewById(R.id.profile_card_tv_location);
        mPersonality = findViewById(R.id.profile_card_tv_personality);
        mBloodType = findViewById(R.id.profile_card_tv_bloodtype);
        mReligion = findViewById(R.id.profile_card_tv_religion);
        mDrinking = findViewById(R.id.profile_card_tv_drinking);
        mSmoking = findViewById(R.id.profile_card_tv_smoking);
        mStory0 = findViewById(R.id.profile_card_tv_story0);
        mStory1 = findViewById(R.id.profile_card_tv_story1);
        mStory2 = findViewById(R.id.profile_card_tv_story2);
        mSelfIntroduction = findViewById(R.id.profile_card_tv_introduce);
        mLikeButton = findViewById(R.id.profile_card_btn_like);
        mChattingButton = findViewById(R.id.profile_card_btn_chatting);
        mMore = findViewById(R.id.profile_card_iv_more);
        mBack = findViewById(R.id.profile_card_iv_back);
        mEvaluationText = findViewById(R.id.profile_card_tv_evaluation_text);
        mMessageImageView = findViewById(R.id.profile_card_iv_message);
        mMessageTextView = findViewById(R.id.profile_card_tv_message);
        mMessageLinearLayout = findViewById(R.id.profile_card_linear_message);

        mStoryTotalLayout = findViewById(R.id.profile_card_linear_story_total);
        mStory0Layout = findViewById(R.id.profile_card_linear_story0);
        mStory1Layout = findViewById(R.id.profile_card_linear_story1);
        mStory2Layout = findViewById(R.id.profile_card_linear_story2);

        //로딩중
        mLoaderLayout = findViewById(R.id.loaderLayout);
        mLoaderLayout.setClickable(true);

        //인텐트 처리, DB로부터 유저정보 받음
        final Intent intent = getIntent();
        partnerUid = intent.getStringExtra(Strings.partnerUid);
        mPartnerUser = (User) intent.getSerializableExtra(Strings.partnerUser);

        FirebaseHelper.db.collection("Users").document(partnerUid).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                User user  = document.toObject(User.class);
                                if (!user.equals(mPartnerUser)) {
                                    mPartnerUser = user;
                                    updateUI();
                                }
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });

    }


    private void updateUI(){
        mEvaluationText.setVisibility(View.GONE);
        mScoreLayout.setVisibility(View.GONE);
        mLikeButton.setVisibility(View.GONE);

        //사진 url 리스트 받아서 tabcount에 리스트 사이즈 넣음
        ArrayList<String> imageUrlList = new ArrayList<>();
        if (mPartnerUser!= null && mPartnerUser.getPhotoUrl() != null) {
            imageUrlList = mPartnerUser.getPhotoUrl();
        }

        viewPager = findViewById(R.id.profile_card_vp_photo);
        profileCardPagerAdapter = new ProfileCardPagerAdapter(getSupportFragmentManager(), imageUrlList.size(), imageUrlList);
        viewPager.setAdapter(profileCardPagerAdapter);
        viewPager.addOnPageChangeListener(mOnPageChangeListener);

        //인디케이터 설정
        circleAnimIndicator = (CircleAnimIndicator) findViewById(R.id.profile_card_circleAnimIndicator);
        circleAnimIndicator.setItemMargin(15); //원사이의 간격
        circleAnimIndicator.setAnimDuration(300); //애니메이션 속도
        circleAnimIndicator.createDotPanel(imageUrlList.size(), R.drawable.ic_indicator_non , R.drawable.ic_indicator_on); //indicator 생성

        mNickNameAge.setText(mPartnerUser.getNickname() + ", " + mPartnerUser.getAge() + "세");
        mHeight.setText(mPartnerUser.getHeight());
        mUniversity.setText(mPartnerUser.getUniversity());
        mMajor.setText(mPartnerUser.getMajor());
        mLocation.setText(mPartnerUser.getLocation());
        String personality = mPartnerUser.getPersonality().toString();
        personality = personality.replace("[","");
        personality = personality.replace("]","");
        mPersonality.setText(personality);

        mBloodType.setText(mPartnerUser.getBloodType());
        mReligion.setText(mPartnerUser.getReligion());
        mDrinking.setText(mPartnerUser.getDrinking());
        mSmoking.setText(mPartnerUser.getSmoking());
        mStory0.setText(mPartnerUser.getStory0());
        mStory1.setText(mPartnerUser.getStory1());
        mStory2.setText(mPartnerUser.getStory2());
        mSelfIntroduction.setText(mPartnerUser.getSelfIntroduction());

        mMessageLinearLayout.setVisibility(View.GONE);

        if(mPartnerUser.isOfficialInfoPublic()){
            if(mPartnerUser.isOfficialUniversityPublic()) {
                mUniversityLayout.setVisibility(View.VISIBLE);
                mMajorLayout.setVisibility(View.GONE);
            } else{
                mUniversityLayout.setVisibility(View.GONE);
                mMajorLayout.setVisibility(View.VISIBLE);
            }
        }

        if(mPartnerUser.getStory0() != null && mPartnerUser.getStory0().equals("")){
            mStory0Layout.setVisibility(View.GONE);
        }

        if(mPartnerUser.getStory1() != null && mPartnerUser.getStory1().equals("")){
            mStory1Layout.setVisibility(View.GONE);
        }

        if(mPartnerUser.getStory2() != null && mPartnerUser.getStory2().equals("")){
            mStory2Layout.setVisibility(View.GONE);
        }

        if(mPartnerUser.getStory0() != null && mPartnerUser.getStory1() != null && mPartnerUser.getStory2() != null && mPartnerUser.getStory0().equals("") && mPartnerUser.getStory2().equals("") && mPartnerUser.getStory2().equals("")){
            mStoryTotalLayout.setVisibility(View.GONE);
        }

    }

    private void setOnClickListener(){
        mMore.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(Strings.EXTRA_LOCATION, "프로필카드");
                bundle.putString(Strings.partnerUid, partnerUid);
                bundle.putSerializable(Strings.partnerUser, mPartnerUser);

                DialogMoreFragment dialog = DialogMoreFragment.getInstance();
                dialog.setArguments(bundle);
                dialog.show(getSupportFragmentManager(), DialogMoreFragment.TAG_EVENT_DIALOG);
            }
        });

        mBack.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                onBackPressed();
            }
        });
    }



    //뷰페이저 넘길시 인디케이터 같이 움직이는 설정
    private SquareViewPager.OnPageChangeListener mOnPageChangeListener = new SquareViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

        @Override
        public void onPageSelected(int position) {
            circleAnimIndicator.selectDot(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };


    @Override
    public void block() {
        FirebaseHelper.blockUser(ProfileCardChattingActivity.this, mPartnerUser, mLoaderLayout, true);
    }
}