package com.unilab.uniting.activities.setprofile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.unilab.uniting.R;
import com.unilab.uniting.adapter.profilecard.ProfileCardPagerAdapter;
import com.unilab.uniting.fragments.dialog.DialogOk2Fragment;
import com.unilab.uniting.model.User;
import com.unilab.uniting.square.SquareViewPager;
import com.unilab.uniting.utils.BasicActivity;
import com.unilab.uniting.utils.CircleAnimIndicator;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.Strings;

import java.util.ArrayList;


public class SetProfile_1MyProfile extends BasicActivity {

    //상수
    final static String TAG = "SetProfileTAG";

    //뷰 세팅
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
    private ImageView mMore;
    private ImageView mBack;
    private LinearLayout mScoreLayout;

    private TextView mEdit;
    View.OnClickListener onClickListener;

    //뷰페이저
    ProfileCardPagerAdapter profileCardPagerAdapter;
    SquareViewPager viewPager;
    CircleAnimIndicator circleAnimIndicator;

    //request code
    public final static int REQUEST_CODE_EDIT_PROFILE = 80;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_card_main);

        init();
        getUser();
        setOnClickListener();

    }

    private void init(){
        mUniversityBadge = findViewById(R.id.profile_card_iv_university_badge);
        mMajorBadge = findViewById(R.id.profile_card_iv_major_badge);
        mUniversityLayout = findViewById(R.id.profile_card_linear_university);
        mMajorLayout = findViewById(R.id.profile_card_linear_major);
        mNickNameAge = findViewById(R.id.profile_card_tv_nickname_age);
        mHeight = findViewById(R.id.profile_card_tv_height);
        mUniversity = findViewById(R.id.profile_card_tv_university);
        mMajor = findViewById(R.id.profile_card_tv_major);
        mEvaluationText = findViewById(R.id.profile_card_tv_evaluation_text);
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
        mMore = findViewById(R.id.profile_card_iv_more);
        mBack = findViewById(R.id.profile_card_iv_back);
        mEdit = findViewById(R.id.profile_card_tv_edit);
        mScoreLayout = findViewById(R.id.profile_card_btn_linear_score);


        //more버튼, 좋아요 버튼, 레이팅바, 평가텍스트 삭제
        mMore.setVisibility(View.GONE);
        mLikeButton.setVisibility(View.GONE);
        mEvaluationText.setVisibility(View.GONE);
        mEdit.setVisibility(View.VISIBLE);
        mScoreLayout.setVisibility(View.GONE);
    }

    //디비로부터 값불러오는 초기화작업
    private void getUser(){
        //DB에서 프로필사진 개수 받아서 뷰페이저 생성 :
        DocumentReference docRef = FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        User user = document.toObject(User.class);
                        updateUI(user);


                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void updateUI(User user){
        //null값 제외
        ArrayList<String> imageUrlList = new ArrayList<>();
        if (user!= null && user.getPhotoUrl() != null) {
            for(int i = 0; i < user.getPhotoUrl().size(); i++){
                if(user.getPhotoUrl().get(i) != null){
                    imageUrlList.add(user.getPhotoUrl().get(i));
                }
            }
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

        //텍스트 설정
        mNickNameAge.setText(user.getNickname() + ", " + user.getAge() + "세");
        mHeight.setText(user.getHeight());
        mUniversity.setText(user.getUniversity());
        mMajor.setText(user.getMajor());
        mEvaluationText.setText(user.getNickname() + "님의 매력을 평가해주세요.");
        mLocation.setText(user.getLocation());
        String personality = user.getPersonality().toString();
        personality = personality.replace("[","");
        personality = personality.replace("]","");
        mPersonality.setText(personality);
        mBloodType.setText(user.getBloodType());
        mReligion.setText(user.getReligion());
        mDrinking.setText(user.getDrinking());
        mSmoking.setText(user.getSmoking());
        mStory0.setText(user.getStory0());
        mStory1.setText(user.getStory1());
        mStory2.setText(user.getStory2());
        mSelfIntroduction.setText(user.getSelfIntroduction());

        if(user.isOfficialInfoPublic()){
            if(user.isOfficialUniversityPublic()) {
                mUniversityLayout.setVisibility(View.VISIBLE);
                mMajorLayout.setVisibility(View.GONE);
            } else{
                mUniversityLayout.setVisibility(View.GONE);
                mMajorLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setOnClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.profile_card_iv_back:  // back(<) 클릭시 뒤로가기 효과
                        onBackPressed();
                        break;
                    case R.id.profile_card_tv_edit:
                        Intent intent = new Intent(SetProfile_1MyProfile.this, SetProfile_2Edit.class);
                        startActivityForResult(intent, REQUEST_CODE_EDIT_PROFILE);
                        break;
                    case R.id.profile_card_iv_university_badge:
                        Bundle bundle2 = new Bundle();
                        bundle2.putString(Strings.EXTRA_CONTENT, "대학을 인증한 회원입니다.");
                        DialogOk2Fragment dialog2 = DialogOk2Fragment.getInstance();
                        dialog2.setArguments(bundle2);
                        dialog2.show(getSupportFragmentManager(), DialogOk2Fragment.TAG_MEETING_DIALOG2);
                        break;
                    case R.id.profile_card_iv_major_badge:
                        Bundle bundle3 = new Bundle();
                        bundle3.putString(Strings.EXTRA_CONTENT, "전공을 인증한 회원입니다.");
                        DialogOk2Fragment dialog3 = DialogOk2Fragment.getInstance();
                        dialog3.setArguments(bundle3);
                        dialog3.show(getSupportFragmentManager(), DialogOk2Fragment.TAG_MEETING_DIALOG2);
                        break;
                }
            }
        };


        mBack.setOnClickListener(onClickListener);
        mEdit.setOnClickListener(onClickListener);
        mUniversityBadge.setOnClickListener(onClickListener);
        mMajorBadge.setOnClickListener(onClickListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_EDIT_PROFILE && resultCode ==RESULT_OK){
            getUser();
        }
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

}
