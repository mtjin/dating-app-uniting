package com.unilab.uniting.activities.launch.signup;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.unilab.uniting.BuildConfig;
import com.unilab.uniting.R;
import com.unilab.uniting.activities.MainActivity;
import com.unilab.uniting.adapter.profilecard.ProfileCardScreeningPagerAdapter;
import com.unilab.uniting.fragments.launch.DialogWithdrawScreeningOkNoFragment;
import com.unilab.uniting.model.DIModel;
import com.unilab.uniting.model.User;
import com.unilab.uniting.square.SquareViewPager;
import com.unilab.uniting.utils.BasicActivity;
import com.unilab.uniting.utils.CircleAnimIndicator;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.LaunchUtil;
import com.unilab.uniting.utils.LogData;
import com.unilab.uniting.utils.MyProfile;
import com.unilab.uniting.utils.RemoteConfig;
import com.unilab.uniting.utils.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class  LaunchSignup_17Screening  extends BasicActivity implements DialogWithdrawScreeningOkNoFragment.DialogWithdrawListener {

    //상수
    public final static int REQUEST_CODE_EDIT_PROFILE = 80;
    final static String TAG = "17ScreeningT";

    //뷰 세팅
    private TextView mNickNameAge;
    private TextView mHeight;
    private TextView mUniversity;
    private TextView mMajor;
    private TextView mLocation;
    private TextView mPersonality;
    private TextView mBloodType;
    private TextView mReligion;
    private TextView mDrinking;
    private TextView mSmoking;
    private TextView mSelfIntroduction;
    private TextView mStory0;
    private TextView mStory1;
    private TextView mStory2;
    private TextView mEditTextView;
    private TextView mGuideTextView;
    private TextView mGuide2TextView;
    private TextView mWithdrawTextView;
    private RelativeLayout mLoaderLayout;

    private TextView mEmailTextView;

    View.OnClickListener onClickListener;

    //뷰페이저
    ProfileCardScreeningPagerAdapter profileCardPagerAdapter;
    SquareViewPager viewPager;
    CircleAnimIndicator circleAnimIndicator;

    private boolean isCertificationDone = false;
    private boolean isMembershipDone = false; //userdocu 리스너가 여러번 작동해서 startActivity 여러번 작동하는거 방지

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_signup_17_screening);

        init();
        updateUI();
        setLogData();
        setOnClickListener();

    }

    private void setLogData(){
        LogData.eventLog(LogData.SignUp7_Screening, this);
        Bundle bundle = new Bundle();
        bundle.putInt(LogData.sign_up_progress, 7);
        LogData.customLog(LogData.SignUp,  bundle, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        userListener();
    }

    private void init(){
        mNickNameAge = findViewById(R.id.signup_screen17_tv_nickname);
        mHeight = findViewById(R.id.signup_screen17_tv_height);
        mUniversity = findViewById(R.id.signup_screen17_tv_university);
        mMajor = findViewById(R.id.signup_screen17_tv_major);
        mLocation = findViewById(R.id.signup_screen17_tv_location);
        mPersonality = findViewById(R.id.signup_screen17_tv_personality);
        mBloodType = findViewById(R.id.signup_screen17_tv_blood);
        mReligion = findViewById(R.id.signup_screen17_tv_religion);
        mDrinking = findViewById(R.id.signup_screen17_tv_drinking);
        mSmoking = findViewById(R.id.signup_screen17_tv_smoking);
        mStory0 = findViewById(R.id.signup_screen17_tv_story0);
        mStory1 = findViewById(R.id.signup_screen17_tv_story1);
        mStory2 = findViewById(R.id.signup_screen17_tv_story2);
        mEditTextView = findViewById(R.id.launch_signup_screening_tv_edit);
        mGuideTextView = findViewById(R.id.launch_signup_screening_tv_guide);
        mGuide2TextView = findViewById(R.id.launch_signup_screening_tv_guide2);

        mSelfIntroduction =findViewById(R.id.signup_screen17_tv_introduce);
        viewPager = findViewById(R.id.signup_screening17_viewpager);
        mWithdrawTextView = findViewById(R.id.signup_screen17_tv_withdraw);

        mEmailTextView = findViewById(R.id.signup_screen17_tv_inquiry);

        //로딩, 툴바 레이아웃
        mLoaderLayout = findViewById(R.id.loaderLayout);
        mLoaderLayout.setClickable(true);


    }

    private void updateUI(){
        User user = MyProfile.getUser();

        //뷰 세팅
        mNickNameAge.setText(user.getNickname() + ", " + user.getAge() + "세");
        mHeight.setText(user.getHeight());
        mUniversity.setText(user.getUniversity());
        mMajor.setText(user.getMajor());
        mLocation.setText(user.getLocation());
        String personality = user.getPersonality().toString();
        personality = personality.replace("[","");
        personality = personality.replace("]","");
        mPersonality.setText(personality);
        mBloodType.setText(user.getBloodType());
        mReligion.setText(user.getReligion());
        mDrinking.setText(user.getDrinking());
        mSmoking.setText(user.getSmoking());

        if(MyProfile.getUser().getSignUpProgress().equals(Strings.SignUpPrgoress.Screening)) {
            isCertificationDone = true;
        }

        if(MyProfile.getUser().getGender().equals("여자")){
            mGuideTextView.setText("심사는 회원 성비나 프로필 완성도에 따라 최대 5영업일까지 소요됩니다.");
        }else{
            mGuideTextView.setText("심사는 회원 성비나 프로필 완성도에 따라 최대 5영업일까지 소요됩니다.");
        }

        if(MyProfile.getUser().getGender().equals("여자") && RemoteConfig.withdraw_btn.equals("off")){
            mWithdrawTextView.setVisibility(View.GONE);
        }else {
            mWithdrawTextView.setVisibility(View.VISIBLE);
        }

        if(!isCertificationDone){
            mEditTextView.setText("대학 인증");
            mGuide2TextView.setText(R.string.screening1);
        }else{
            mEditTextView.setText("프로필 수정");
            mGuide2TextView.setText(R.string.screening2);
        }


    }

    private void setOnClickListener(){
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.launch_signup_screening_tv_edit:
                        if(!isCertificationDone){
                            LaunchSignup_17Screening.super.onBackPressed();
                        }else{
                            Intent intent = new Intent(LaunchSignup_17Screening.this, LaunchSignup_18ProfileEdit.class);
                            startActivityForResult(intent, REQUEST_CODE_EDIT_PROFILE);
                        }
                        break;
                    case R.id.signup_screen17_tv_nickname:
                    case R.id.signup_screen17_tv_introduce:
                    case R.id.signup_screen17_tv_story0:
                    case R.id.signup_screen17_tv_story1:
                    case R.id.signup_screen17_tv_story2:
                        Intent intent = new Intent(LaunchSignup_17Screening.this, LaunchSignup_18ProfileEdit.class);
                        startActivityForResult(intent, REQUEST_CODE_EDIT_PROFILE);
                        break;
                    case R.id.signup_screen17_tv_withdraw:
                        DialogWithdrawScreeningOkNoFragment dialog = DialogWithdrawScreeningOkNoFragment.getInstance();
                        dialog.show(getSupportFragmentManager(), DialogWithdrawScreeningOkNoFragment.TAG_EVENT_DIALOG);
                        break;
                    case R.id.signup_screen17_tv_inquiry:
                        Intent emailIntent = new Intent(Intent.ACTION_SEND);
                        String[] helpEmail = {getString(R.string.help_mail)};
                        emailIntent.setType("plain/Text");
                        emailIntent.putExtra(Intent.EXTRA_EMAIL, helpEmail);
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[" + getString(R.string.app_name) + "]  고객센터에 문의합니다.");
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "답장 받을 이메일: " + MyProfile.getUser().getEmail() + "\n앱 버전 (AppVersion):" + BuildConfig.VERSION_NAME + "\n기기명 (Device):" + Build.MODEL + "\n안드로이드 OS (Android OS):" + Build.VERSION.RELEASE + "\n안드로이드 SDK(API):" + Build.VERSION.SDK_INT +"\n내용 (Content):\n");
                        emailIntent.setType("message/rfc822");
                        startActivity(emailIntent);
                        break;
                }
            }
        };

        mWithdrawTextView.setOnClickListener(onClickListener);
        mNickNameAge.setOnClickListener(onClickListener);
        mEditTextView.setOnClickListener(onClickListener);
        mSelfIntroduction.setOnClickListener(onClickListener);
        mStory0.setOnClickListener(onClickListener);
        mStory1.setOnClickListener(onClickListener);
        mStory2.setOnClickListener(onClickListener);

        mEmailTextView.setOnClickListener(onClickListener);
    }


    //디비로부터 값불러오는 초기화작업
    private void userListener() {
        LaunchUtil.checkAuth(LaunchSignup_17Screening.this);
        FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid)
                .addSnapshotListener(LaunchSignup_17Screening.this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        if (snapshot != null && snapshot.exists()) {
                            Log.d(TAG, "Current data: " + snapshot.getData());
                            User user = snapshot.toObject(User.class);
                            MyProfile.init(user);

                            //사진 세팅
                            if( snapshot.get(FirebaseHelper.screeningPhotoUrl) != null){
                                ArrayList<String> imageList = (ArrayList<String>) snapshot.get(FirebaseHelper.screeningPhotoUrl);
                                //인디케이터 설정
                                circleAnimIndicator = (CircleAnimIndicator) findViewById(R.id.profile_card_circleAnimIndicator);
                                circleAnimIndicator.setItemMargin(15); //원사이의 간격
                                circleAnimIndicator.setAnimDuration(300); //애니메이션 속도
                                circleAnimIndicator.createDotPanel(imageList.size(), R.drawable.ic_indicator_non, R.drawable.ic_indicator_on); //indicator 생성

                                //뷰페이저 설정
                                profileCardPagerAdapter = new ProfileCardScreeningPagerAdapter(getSupportFragmentManager(), imageList.size(), imageList);
                                viewPager.setAdapter(profileCardPagerAdapter);
                                viewPager.addOnPageChangeListener(mOnPageChangeListener);
                            }

                            //텍스트 설정
                            mNickNameAge.setText(user.getNickname() + ", " + user.getAge() + "세");
                            mSelfIntroduction.setText(user.getSelfIntroduction());
                            mStory0.setText(user.getStory0());
                            mStory1.setText(user.getStory1());
                            mStory2.setText(user.getStory2());

                            if (isMembershipDone) {
                                return;
                            }

                            if (user.getMembership() != null && user.getSignUpProgress() != null) {
                                String membership = user.getMembership();
                                String signUpProgress = user.getSignUpProgress();

                                Map<String,String> user_properties = new HashMap<>();
                                user_properties.put(LogData.membership, membership);
                                user_properties.put(LogData.sign_up_progress, signUpProgress);
                                LogData.setUserProperties(user_properties, LaunchSignup_17Screening.this);


                                switch (membership){
                                    case LaunchUtil.Main:
                                        Intent intent = new Intent(LaunchSignup_17Screening.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        isMembershipDone = true;
                                        break;
                                    case LaunchUtil.Fail:
                                        switch (signUpProgress){
                                            case LaunchUtil.Photo:
                                            case LaunchUtil.PhotoCerti:
                                                Intent intentPhoto = new Intent(LaunchSignup_17Screening.this, LaunchSignup_15Photo.class);
                                                intentPhoto.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                intentPhoto.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                //intentPhoto.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intentPhoto);
                                                isMembershipDone = true;
                                                break;
                                            case LaunchUtil.Certi:
                                                Intent intentCerti = new Intent(LaunchSignup_17Screening.this, LaunchSignup_16UniversityCertification.class);
                                                intentCerti.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                intentCerti.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                //intentCerti.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intentCerti);
                                                isMembershipDone = true;
                                                break;
                                        }
                                }
                            }

                        } else {
                            Log.d(TAG, "Current data: null");
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_EDIT_PROFILE && resultCode == RESULT_OK){
            userListener();
        }
    }

    @Override
    public void onBackPressed() {
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
    public void withdraw() {
        DIModel diModel = new DIModel(MyProfile.getUser().getDi(), MyProfile.getUser().getUid(), MyProfile.getUser().getFacebookUid(), "", MyProfile.getUser().getEmail(), LaunchUtil.Withdraw, MyProfile.getUser().getInviteCode(), 0);

        Map<String,String> userProps = new HashMap<>();
        userProps.put(LogData.membership, LaunchUtil.Withdraw);
        LogData.setUserProperties(userProps, LaunchSignup_17Screening.this);
        LogData.eventLog(LogData.Withdraw_SignUp7, LaunchSignup_17Screening.this);

        mLoaderLayout.setVisibility(View.VISIBLE);
        WriteBatch batch = FirebaseHelper.db.batch();
        batch.update(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid), FirebaseHelper.membership, LaunchUtil.Withdraw);
        batch.update(FirebaseHelper.db.collection("AdminUsers").document(FirebaseHelper.mUid), FirebaseHelper.membership, LaunchUtil.Withdraw);
        batch.set(FirebaseHelper.db.collection("UserDI").document(MyProfile.getUser().getDi()), diModel, SetOptions.merge());
        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mLoaderLayout.setVisibility(View.GONE);
                Toast.makeText(LaunchSignup_17Screening.this, "탈퇴 처리 되었습니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LaunchSignup_17Screening.this, LaunchSignup_0.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mLoaderLayout.setVisibility(View.GONE);
                Toast.makeText(LaunchSignup_17Screening.this, "오류 발생. 고객센터로 문의해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
