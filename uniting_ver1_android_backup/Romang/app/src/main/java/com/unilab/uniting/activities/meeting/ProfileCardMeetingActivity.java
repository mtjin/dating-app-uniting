package com.unilab.uniting.activities.meeting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.unilab.uniting.R;
import com.unilab.uniting.adapter.profilecard.ProfileCardPagerAdapter;
import com.unilab.uniting.fragments.dialog.DialogBlockFragment;
import com.unilab.uniting.fragments.dialog.DialogMoreFragment;
import com.unilab.uniting.fragments.dialog.DialogOk2Fragment;
import com.unilab.uniting.fragments.dialog.DialogOkFragment;
import com.unilab.uniting.fragments.meeting.DialogOpenMeetingFragment;
import com.unilab.uniting.fragments.meeting.MeetingDialogStep1NoFragment;
import com.unilab.uniting.fragments.meeting.MeetingDialogStep1OkFragment;
import com.unilab.uniting.fragments.meeting.MeetingDialogStep2NoFragment;
import com.unilab.uniting.fragments.meeting.MeetingDialogStep2OkFragment;
import com.unilab.uniting.fragments.meeting.ProfileCardMeetingListener;
import com.unilab.uniting.model.Dia;
import com.unilab.uniting.model.Fcm;
import com.unilab.uniting.model.Meeting;
import com.unilab.uniting.model.Notification;
import com.unilab.uniting.model.RefundedDia;
import com.unilab.uniting.model.User;
import com.unilab.uniting.square.SquareViewPager;
import com.unilab.uniting.utils.BasicActivity;
import com.unilab.uniting.utils.CircleAnimIndicator;
import com.unilab.uniting.utils.DateUtil;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.LogData;
import com.unilab.uniting.utils.MyProfile;
import com.unilab.uniting.utils.Numbers;
import com.unilab.uniting.utils.OnSingleClickListener;
import com.unilab.uniting.utils.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ProfileCardMeetingActivity extends BasicActivity implements ProfileCardMeetingListener, DialogBlockFragment.BlockListener, DialogOpenMeetingFragment.OpenMeetingListener {

    //??????
    final static String TAG = "ProfileCardMeetingTAG";
    final static int CHAT_PAGE = 4;

    //??????
    Context context;
    private String mPartnerUid;
    private String mMeetingUid;
    private String mApplicantUid;
    private Meeting mMeeting;
    private User mPartnerUser;
    private Dia dia = new Dia();

    //??? ??????
    private RelativeLayout mLoaderLayout; //??????????????????
    private ImageView mUniversityBadge;
    private ImageView mMajorBadge;
    private LinearLayout mUniversityLayout;
    private LinearLayout mMajorLayout;
    private TextView mNickNameAge;
    private TextView mHeight;
    private TextView mUniversity;
    private TextView mMajor;
    private TextView mQuestionText;
    private TextView mLocation;
    private TextView mPersonality;
    private TextView mBloodType;
    private TextView mReligion;
    private TextView mDrinking;
    private TextView mSmoking;
    private TextView mStory0;
    private TextView mStory1;
    private TextView mStory2;
    private ImageView mMessageImageView;
    private TextView mMessageTextView;
    private LinearLayout mMessageLinearLayout;


    private LinearLayout mStep1BothButton;
    private Button mStep1OkButton;
    private Button mStep1NoButton;
    private LinearLayout mStep2BothButton;
    private Button mStep2OkButton;
    private Button mStep2NoButton;
    private CardView mStep2ApplyButton;

    private LinearLayout mStoryTotalLayout;
    private LinearLayout mStory0Layout;
    private LinearLayout mStory1Layout;
    private LinearLayout mStory2Layout;


    private ImageView mMore;
    private ImageView mBack;

    //????????????
    ProfileCardPagerAdapter profileCardPagerAdapter;
    SquareViewPager viewPager;
    private CircleAnimIndicator circleAnimIndicator;

    OnSingleClickListener onClickListener;

    private boolean isFirstTry  = true;

    private String meetingStep1State = FirebaseHelper.PRE_APPLY;
    private String meetingStep2State = FirebaseHelper.PRE_APPLY;
    private String meetingStep2Applicant = ""; //2???????????? ????????? ?????? ????????? ????????? uid
    private String message = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_card_from_meeting);


        init();
        updateUI();
        setLogStep1View();
        setOnClickListener();
        meetingStateListener();
        setUser();
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
        mQuestionText = findViewById(R.id.profile_card_tv_evaluation_text);
        mLocation = findViewById(R.id.profile_card_tv_location);
        mPersonality = findViewById(R.id.profile_card_tv_personality);
        mBloodType = findViewById(R.id.profile_card_tv_bloodtype);
        mReligion = findViewById(R.id.profile_card_tv_religion);
        mDrinking = findViewById(R.id.profile_card_tv_drinking);
        mSmoking = findViewById(R.id.profile_card_tv_smoking);
        mStory0 = findViewById(R.id.profile_card_tv_story0);
        mStory1 = findViewById(R.id.profile_card_tv_story1);
        mStory2 = findViewById(R.id.profile_card_tv_story2);

        mMessageImageView = findViewById(R.id.profile_card_iv_message);
        mMessageTextView = findViewById(R.id.profile_card_tv_message);
        mMessageLinearLayout = findViewById(R.id.profile_card_linear_message);

        mStep1BothButton = findViewById(R.id.profile_card_linear_btn_step1);
        mStep1OkButton = findViewById(R.id.profile_card_btn_step1ok);
        mStep1NoButton = findViewById(R.id.profile_card_btn_step1no);
        mStep2BothButton = findViewById(R.id.profile_card_linear_btn_step2);
        mStep2OkButton = findViewById(R.id.profile_card_btn_step2ok);
        mStep2NoButton = findViewById(R.id.profile_card_btn_step2no);
        mStep2ApplyButton = findViewById(R.id.profile_card_cv_step2Apply);

        mMore = findViewById(R.id.profile_card_iv_more);
        mBack = findViewById(R.id.profile_card_iv_back);

        mStoryTotalLayout = findViewById(R.id.profile_card_linear_story_total);
        mStory0Layout = findViewById(R.id.profile_card_linear_story0);
        mStory1Layout = findViewById(R.id.profile_card_linear_story1);
        mStory2Layout = findViewById(R.id.profile_card_linear_story2);

        //??????, ?????? ????????????
        mLoaderLayout = findViewById(R.id.loaderLayout);
        mLoaderLayout.setClickable(true);

        //????????? ??????
        final Intent intent = getIntent();
        mPartnerUid = intent.getStringExtra(Strings.partnerUid); //????????? ??????????????? partner??? ??? host????????? mhostUid??? ????????? ?????? (????????? ??????????????? ???????????? partner)
        mPartnerUser = (User) intent.getSerializableExtra(Strings.partnerUser);
        mMeetingUid = intent.getStringExtra(Strings.meetingUid);
        mMeeting = (Meeting) intent.getSerializableExtra(Strings.EXTRA_MEETING);

        if(mMeeting.getHostUid().equals(FirebaseHelper.mUid)){
            mApplicantUid = mPartnerUid;
        } else {
            mApplicantUid = FirebaseHelper.mUid;
        }

    }

    private void updateUI(){
        //?????? url ????????? ????????? tabcount??? ????????? ????????? ??????
        ArrayList<String> imageUrlList = new ArrayList<>();
        if (mPartnerUser!= null && mPartnerUser.getPhotoUrl() != null) {
            for(int i = 0; i < mPartnerUser.getPhotoUrl().size(); i++){
                if(mPartnerUser.getPhotoUrl().get(i) != null){
                    imageUrlList.add(mPartnerUser.getPhotoUrl().get(i));
                }
            }
        }

        viewPager = findViewById(R.id.profile_card_meeting_vp_photo);
        profileCardPagerAdapter = new ProfileCardPagerAdapter(getSupportFragmentManager(), imageUrlList.size(), imageUrlList);
        viewPager.setAdapter(profileCardPagerAdapter);
        viewPager.addOnPageChangeListener(mOnPageChangeListener);

        //??????????????? ??????
        circleAnimIndicator = (CircleAnimIndicator) findViewById(R.id.profile_card_meeting_circleAnimIndicator);
        circleAnimIndicator.setItemMargin(15); //???????????? ??????
        circleAnimIndicator.setAnimDuration(300); //??????????????? ??????
        circleAnimIndicator.createDotPanel(imageUrlList.size(), R.drawable.ic_indicator_non, R.drawable.ic_indicator_on); //indicator ??????


        //?????? ????????? ??????
        mNickNameAge.setText(mPartnerUser.getNickname() + ", " + mPartnerUser.getAge() + "???");
        mHeight.setText(mPartnerUser.getHeight());

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

        if(mMeeting.getHostUid().equals(mPartnerUser.getUid())){ //???????????? ???????????? ??????
            mUniversityLayout.setVisibility(View.VISIBLE);
            mMajorLayout.setVisibility(View.GONE);
            String university = mMeeting.getHostUniversity();
            if(mMeeting.getHostUniversity().equals("")){
                university = "?????????";
                mUniversityBadge.setVisibility(View.GONE);
            }
            mUniversity.setText(university);
        } else { //???????????? ???????????? ??????
            mUniversity.setText(mPartnerUser.getUniversity() + "(?????????)");
            mMajor.setText(mPartnerUser.getMajor() + "(?????????)");
            if(mPartnerUser.isOfficialInfoPublic()){
                if(mPartnerUser.isOfficialUniversityPublic()) {
                    mUniversityLayout.setVisibility(View.VISIBLE);
                    mMajorLayout.setVisibility(View.GONE);
                } else {
                    mUniversityLayout.setVisibility(View.GONE);
                    mMajorLayout.setVisibility(View.VISIBLE);
                }
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

    private void setLogStep1View(){
        Bundle eventBundle = new Bundle();
        eventBundle.putInt(LogData.dia, 0);
        eventBundle.putString(LogData.meetingId, mMeeting.getMeetingId());
        LogData.customLog(LogData.meeting_s04_step1_profile_card_view, eventBundle, ProfileCardMeetingActivity.this);

        LogData.setStageMeeting(LogData.meeting_s04_step1_profile_card_view, ProfileCardMeetingActivity.this);
        if(mMeeting.getHostUid().equals(FirebaseHelper.mUid)){
            LogData.setStageMeetingHost(LogData.meeting_s04_step1_profile_card_view,ProfileCardMeetingActivity.this);
        } else {
            LogData.setStageMeetingApplicant(LogData.meeting_s04_step1_profile_card_view,ProfileCardMeetingActivity.this);
        }
    }

    private void setLogStep1Fail(){
        Bundle eventBundle = new Bundle();
        eventBundle.putInt(LogData.dia, 0);
        eventBundle.putString(LogData.meetingId, mMeeting.getMeetingId());
        LogData.customLog(LogData.meeting_s05_step1_result_fail, eventBundle, ProfileCardMeetingActivity.this);

        LogData.setStageMeeting(LogData.meeting_s05_step1_result_fail, ProfileCardMeetingActivity.this);
        if(mMeeting.getHostUid().equals(FirebaseHelper.mUid)){
            LogData.setStageMeetingHost(LogData.meeting_s05_step1_result_fail,ProfileCardMeetingActivity.this);
        } else {
            LogData.setStageMeetingApplicant(LogData.meeting_s05_step1_result_fail,ProfileCardMeetingActivity.this);
        }
    }

    private void setLogStep1Pass(){
        Bundle eventBundle = new Bundle();
        eventBundle.putInt(LogData.dia, 0);
        eventBundle.putString(LogData.meetingId, mMeeting.getMeetingId());
        LogData.customLog(LogData.meeting_s06_step1_result_pass,  eventBundle, ProfileCardMeetingActivity.this);

        LogData.setStageMeeting(LogData.meeting_s06_step1_result_pass, ProfileCardMeetingActivity.this);
        if(mMeeting.getHostUid().equals(FirebaseHelper.mUid)){
            LogData.setStageMeetingHost(LogData.meeting_s06_step1_result_pass,ProfileCardMeetingActivity.this);
        } else {
            LogData.setStageMeetingApplicant(LogData.meeting_s06_step1_result_pass,ProfileCardMeetingActivity.this);
        }
    }

    private void setLogStep2View(){
        Bundle eventBundle = new Bundle();
        eventBundle.putInt(LogData.dia, 0);
        eventBundle.putString(LogData.meetingId, mMeeting.getMeetingId());
        LogData.customLog(LogData.meeting_s07_step2_profile_card_view, eventBundle, ProfileCardMeetingActivity.this);

        LogData.setStageMeeting(LogData.meeting_s07_step2_profile_card_view, ProfileCardMeetingActivity.this);
        if(mMeeting.getHostUid().equals(FirebaseHelper.mUid)){
            LogData.setStageMeetingHost(LogData.meeting_s07_step2_profile_card_view,ProfileCardMeetingActivity.this);
        } else {
            LogData.setStageMeetingApplicant(LogData.meeting_s07_step2_profile_card_view,ProfileCardMeetingActivity.this);
        }
    }

    private void setLogStep2Fail(){
        Bundle eventBundle = new Bundle();
        eventBundle.putInt(LogData.dia, 0);
        eventBundle.putString(LogData.meetingId, mMeeting.getMeetingId());
        LogData.customLog(LogData.meeting_s10_step2_result_fail, eventBundle, ProfileCardMeetingActivity.this);

        LogData.setStageMeeting(LogData.meeting_s10_step2_result_fail, ProfileCardMeetingActivity.this);
        if(mMeeting.getHostUid().equals(FirebaseHelper.mUid)){
            LogData.setStageMeetingHost(LogData.meeting_s10_step2_result_fail,ProfileCardMeetingActivity.this);
        } else {
            LogData.setStageMeetingApplicant(LogData.meeting_s10_step2_result_fail,ProfileCardMeetingActivity.this);
        }
    }

    private void setLogStep2Pass(){
        Bundle eventBundle = new Bundle();
        eventBundle.putInt(LogData.dia, 0);
        eventBundle.putString(LogData.meetingId, mMeeting.getMeetingId());
        LogData.customLog(LogData.meeting_s11_step2_result_pass,  eventBundle, ProfileCardMeetingActivity.this);

        LogData.setStageMeeting(LogData.meeting_s11_step2_result_pass, ProfileCardMeetingActivity.this);
        if(mMeeting.getHostUid().equals(FirebaseHelper.mUid)){
            LogData.setStageMeetingHost(LogData.meeting_s11_step2_result_pass,ProfileCardMeetingActivity.this);
        } else {
            LogData.setStageMeetingApplicant(LogData.meeting_s11_step2_result_pass,ProfileCardMeetingActivity.this);
        }
    }

    //??? ?????? ?????????
    private void setOnClickListener(){
        onClickListener = new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                switch (v.getId()){
                    case R.id.profile_card_iv_more:
                        Bundle bundle = new Bundle();
                        bundle.putString(Strings.EXTRA_LOCATION, "???????????????");
                        bundle.putString(Strings.partnerUid, mPartnerUid);
                        bundle.putSerializable(Strings.partnerUser, mPartnerUser);

                        DialogMoreFragment dialog = DialogMoreFragment.getInstance();
                        dialog.setArguments(bundle);
                        dialog.show(getSupportFragmentManager(), DialogMoreFragment.TAG_EVENT_DIALOG);
                        break;
                    case R.id.profile_card_iv_back:
                        onBackPressed();
                        break;
                    case R.id.profile_card_btn_step1ok:
                        step1OkButton();
                        break;
                    case R.id.profile_card_btn_step1no:
                        step1NoButton();
                        break;
                    case R.id.profile_card_btn_step2ok:
                        step2OkButton();
                        break;
                    case R.id.profile_card_btn_step2no:
                        step2NoButton();
                        break;
                    case R.id.profile_card_cv_step2Apply:
                        step2ApplyButton();
                        break;
                    case R.id.profile_card_iv_university_badge:
                        Bundle bundle2 = new Bundle();
                        bundle2.putString(Strings.EXTRA_CONTENT, "????????? ????????? ???????????????.");
                        DialogOk2Fragment dialog2 = DialogOk2Fragment.getInstance();
                        dialog2.setArguments(bundle2);
                        dialog2.show(getSupportFragmentManager(), DialogOk2Fragment.TAG_MEETING_DIALOG2);
                        break;
                    case R.id.profile_card_iv_major_badge:
                        Bundle bundle3 = new Bundle();
                        bundle3.putString(Strings.EXTRA_CONTENT, "????????? ????????? ???????????????.");
                        DialogOk2Fragment dialog3 = DialogOk2Fragment.getInstance();
                        dialog3.setArguments(bundle3);
                        dialog3.show(getSupportFragmentManager(), DialogOk2Fragment.TAG_MEETING_DIALOG2);
                        break;
                }
            }
        };

        mMore.setOnClickListener(onClickListener);
        mBack.setOnClickListener(onClickListener);
        mStep1OkButton.setOnClickListener(onClickListener);
        mStep1NoButton.setOnClickListener(onClickListener);
        mStep2OkButton.setOnClickListener(onClickListener);
        mStep2NoButton.setOnClickListener(onClickListener);
        mStep2ApplyButton.setOnClickListener(onClickListener);
        mUniversityBadge.setOnClickListener(onClickListener);
        mMajorBadge.setOnClickListener(onClickListener);

    }

    private void setUser(){
        FirebaseHelper.db.collection("Users").document(mPartnerUid).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                mPartnerUser = document.toObject(User.class);
                                updateUI();

                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
    }

    private void meetingStateListener(){
        mQuestionText.setText(mPartnerUser.getNickname() + "?????? ????????? ?????? ????????? ?????????????????????????");
        mLoaderLayout.setVisibility(View.VISIBLE);
        FirebaseHelper.db.collection("Meetings").document(mMeetingUid).collection("Applicant").document(mApplicantUid)
                .addSnapshotListener(ProfileCardMeetingActivity.this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            mLoaderLayout.setVisibility(View.GONE);
                            return;
                        }

                        if (snapshot != null && snapshot.exists()) {
                            if (snapshot.get(FirebaseHelper.meetingStep1) != null) {
                                meetingStep1State = (String) snapshot.get(FirebaseHelper.meetingStep1);
                            }

                            if (snapshot.get(FirebaseHelper.meetingStep2) != null) {
                                meetingStep2State = (String) snapshot.get(FirebaseHelper.meetingStep2);
                            }

                            if (snapshot.get(FirebaseHelper.meetingStep2Applicant) != null) {
                                meetingStep2Applicant = (String) snapshot.get(FirebaseHelper.meetingStep2Applicant);
                            }

                            if (snapshot.get(FirebaseHelper.message) != null) {
                                message = (String) snapshot.get(FirebaseHelper.message);
                            }
                        } else {
                            Log.d(TAG, "Current data: null");
                        }

                        mLoaderLayout.setVisibility(View.GONE);
                        setStateView();
                    }
                });

    }

    private void setStateView(){
        switch (meetingStep1State){
            case FirebaseHelper.PRE_APPLY:
                break;
            case FirebaseHelper.SCREENING:
                mStep1BothButton.setVisibility(View.VISIBLE);
                mStep2ApplyButton.setVisibility(View.GONE);
                mStep2BothButton.setVisibility(View.GONE);
                break;
            case FirebaseHelper.PASS: //???????????? ??? ???????????? ?????????.
                mQuestionText.setText("????????? ????????? ????????? ?????????! \n 1. ???????????? ????????? ?????? ????????? ??????????????? ???????????????. \n 2. ???????????? ????????? ?????? ????????? ???????????? ?????????????????????." );
                mStep1BothButton.setVisibility(View.GONE);
                mStep2ApplyButton.setVisibility(View.VISIBLE);
                mStep2BothButton.setVisibility(View.GONE);
                setLogStep2View();

                switch (meetingStep2State){
                    case FirebaseHelper.PRE_APPLY:
                        break;
                    case FirebaseHelper.SCREENING:
                        mStep1BothButton.setVisibility(View.GONE);
                        mStep2ApplyButton.setVisibility(View.GONE);
                        if(meetingStep2Applicant.equals(FirebaseHelper.mUid)){
                            mQuestionText.setText("????????? ????????? ??????????????????. ???????????? ???????????? ???????????????.");
                            mStep2BothButton.setVisibility(View.GONE);
                        } else {
                            mQuestionText.setText("???????????? ?????? ???????????????. \n ?????? ????????? ????????? ????????? ??????????????? ???????????????.");
                            mStep2BothButton.setVisibility(View.VISIBLE);
                        }
                        break;
                    case FirebaseHelper.PASS:
                        setLogStep2Pass();
                        setPhoneNumber();
                        mStep1BothButton.setVisibility(View.GONE);
                        mStep2ApplyButton.setVisibility(View.GONE);
                        mStep2BothButton.setVisibility(View.GONE);
                        break;
                    case FirebaseHelper.FAIL:
                        setLogStep2Fail();
                        if(meetingStep2Applicant.equals(FirebaseHelper.mUid)){
                            mQuestionText.setText("????????? ????????? ??????????????????:(");
                        } else {
                            mQuestionText.setText("???????????? ????????? ?????? ????????? ???????????????");
                        }
                        mStep1BothButton.setVisibility(View.GONE);
                        mStep2ApplyButton.setVisibility(View.GONE);
                        mStep2BothButton.setVisibility(View.GONE);
                        break;
                }
                break;
            case FirebaseHelper.FAIL:
                mQuestionText.setText(mPartnerUser.getNickname() + "?????? ?????????????????????.");
                mStep1BothButton.setVisibility(View.GONE);
                mStep2ApplyButton.setVisibility(View.GONE);
                mStep2BothButton.setVisibility(View.GONE);
                break;
        }


        if(message.equals("")){
            mMessageLinearLayout.setVisibility(View.GONE);
        } else {
            mMessageLinearLayout.setVisibility(View.VISIBLE);
            mMessageTextView.setText(message);
            if(meetingStep2Applicant.equals(FirebaseHelper.mUid)){
                if (!this.isDestroyed()) {
                    if (MyProfile.getUser().getPhotoUrl().get(0) != null) {
                        Glide.with(ProfileCardMeetingActivity.this).load(MyProfile.getUser().getPhotoUrl().get(0)).fitCenter().thumbnail(0.1f).into(mMessageImageView);
                    }
                }
            } else {
                if (!this.isDestroyed()) {
                    if (mPartnerUser.getPhotoUrl().get(0) != null) {
                        Glide.with(ProfileCardMeetingActivity.this).load(mPartnerUser.getPhotoUrl().get(0)).fitCenter().thumbnail(0.1f).into(mMessageImageView);
                    }
                }
            }

        }

        mLoaderLayout.setVisibility(View.GONE);

    }


    //???????????? ????????? ??????????????? ?????? ???????????? ??????
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




    //?????? ?????? ????????? ???????????????
    private void step1OkButton() {
        MeetingDialogStep1OkFragment dialog7 = MeetingDialogStep1OkFragment.getInstance();
        Bundle bundle = new Bundle();
        bundle.putString(Strings.EXTRA_MEETING_UID, mMeetingUid); //??? uid ??????
        bundle.putString(Strings.EXTRA_PARTNER, mPartnerUid);
        bundle.putSerializable(Strings.EXTRA_PARTNER_USER, mPartnerUser);
        bundle.putSerializable(Strings.EXTRA_MEETING, mMeeting);
        dialog7.setArguments(bundle);
        dialog7.show(getSupportFragmentManager(), MeetingDialogStep1OkFragment.TAG_MEETING_DIALOG);
    }

    //?????? ?????? ?????????
    private void step1NoButton() {
        MeetingDialogStep1NoFragment dialog8 = MeetingDialogStep1NoFragment.getInstance();
        Bundle bundle = new Bundle();
        bundle.putString(Strings.EXTRA_MEETING_UID, mMeetingUid); //??? uid ??????
        bundle.putString(Strings.EXTRA_PARTNER, mPartnerUid);
        bundle.putSerializable(Strings.EXTRA_MEETING, mMeeting);
        dialog8.setArguments(bundle);
        dialog8.show(getSupportFragmentManager(), MeetingDialogStep1NoFragment.TAG_MEETING_DIALOG);
    }

    //?????? ?????? ?????????
    private void step2ApplyButton() {
        if (meetingStep2State.equals(FirebaseHelper.PRE_APPLY)) { //?????? ?????? ???????????? ?????? ??????
            Bundle bundle = new Bundle();
            bundle.putInt(LogData.dia, Numbers.OPEN_MEETING_COST);
            bundle.putString(LogData.partnerUid, mPartnerUid);
            bundle.putString(LogData.meetingId, mMeeting.getMeetingId());
            LogData.customLog(LogData.meeting_s08_step2_pre_apply, bundle, ProfileCardMeetingActivity.this);
            LogData.setStageMeeting(LogData.meeting_s08_step2_pre_apply, ProfileCardMeetingActivity.this);
            if(mMeeting.getHostUid().equals(FirebaseHelper.mUid)){
                LogData.setStageMeetingHost(LogData.meeting_s08_step2_pre_apply,ProfileCardMeetingActivity.this);
            } else {
                LogData.setStageMeetingApplicant(LogData.meeting_s08_step2_pre_apply,ProfileCardMeetingActivity.this);
            }

            Bundle bundle2 = new Bundle();
            bundle2.putString(Strings.partnerUid, mPartnerUid);
            bundle2.putSerializable(Strings.partnerUser, mPartnerUser);
            DialogOpenMeetingFragment dialog2 = DialogOpenMeetingFragment.getInstance();
            dialog2.setArguments(bundle2);
            dialog2.show(getSupportFragmentManager(), DialogOpenMeetingFragment.TAG_LIKE_DIALOG);
        } else { // ?????? ??????????????? ??????
            Bundle bundle3 = new Bundle();
            bundle3.putString(Strings.EXTRA_LOCATION, Strings.isApplied);
            DialogOkFragment dialog3 = DialogOkFragment.getInstance();
            dialog3.setArguments(bundle3);
            dialog3.show(getSupportFragmentManager(), DialogOkFragment.TAG_MEETING_DIALOG2);
        }
    }

    //?????? ?????? ?????? ????????? ???????????????
    private void step2OkButton() {
        MeetingDialogStep2OkFragment dialog7 = MeetingDialogStep2OkFragment.getInstance();
        Bundle bundle = new Bundle();
        bundle.putString(Strings.EXTRA_MEETING_UID, mMeetingUid); //??? uid ??????
        bundle.putString(Strings.EXTRA_PARTNER, mPartnerUid);
        bundle.putSerializable(Strings.EXTRA_PARTNER_USER, mPartnerUser);
        bundle.putSerializable(Strings.EXTRA_MEETING, mMeeting);
        dialog7.setArguments(bundle);
        dialog7.show(getSupportFragmentManager(), MeetingDialogStep1OkFragment.TAG_MEETING_DIALOG);
    }

    //?????? ?????? ?????? ?????????
    private void step2NoButton() {
        MeetingDialogStep2NoFragment dialog8 = MeetingDialogStep2NoFragment.getInstance();
        Bundle bundle = new Bundle();
        bundle.putString(Strings.EXTRA_MEETING_UID, mMeetingUid); //??? uid ??????
        bundle.putString(Strings.EXTRA_PARTNER, mPartnerUid);
        bundle.putSerializable(Strings.EXTRA_MEETING, mMeeting);
        dialog8.setArguments(bundle);
        dialog8.show(getSupportFragmentManager(), MeetingDialogStep1NoFragment.TAG_MEETING_DIALOG);
    }

    @Override
    public void step1Ok() {
        //?????? ?????? ??????
        Map<String, Object> approvalData = new HashMap<>();
        approvalData.put(FirebaseHelper.meetingStep1, FirebaseHelper.PASS);

        Notification notification = new Notification("?????????????????????", mMeeting, DateUtil.getDateMin(), DateUtil.getUnixTimeLong(), MyProfile.getUser().getNickname(),"", mPartnerUid, false);
        Fcm fcm = new Fcm(mPartnerUid, mMeeting.getTitle(),  "????????? ????????? ?????????????????????!", mMeeting, mMeeting.getMeetingId(), FirebaseHelper.meeting, FirebaseHelper.step1Ok, DateUtil.getDateSec());

        //?????? DB??? ???????????? ??????
        WriteBatch batch = FirebaseHelper.db.batch();
        batch.set(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("MeetingConnected").document(mPartnerUid), mPartnerUser);
        batch.set(FirebaseHelper.db.collection("Users").document(mPartnerUid).collection("MeetingConnected").document(FirebaseHelper.mUid), MyProfile.getUser());
        batch.update(FirebaseHelper.db.collection("Meetings").document(mMeetingUid).collection("Applicant").document(mApplicantUid), approvalData);
        batch.set(FirebaseHelper.db.collection("Users").document(mPartnerUid).collection("Notification").document(DateUtil.getTimeStampUnix()), notification);
        batch.set(FirebaseHelper.db.collection("Users").document(mPartnerUid).collection("Fcm").document(), fcm);

        //InteractionHistory
        Map<String, Object> unionData = new HashMap<>();
        Map<String, Object> unionData2 = new HashMap<>();
        Map<String, Object> unionData3 = new HashMap<>();
        unionData.put(FirebaseHelper.meetingStep1SendPassList , FieldValue.arrayUnion(mPartnerUid));
        unionData2.put(FirebaseHelper.meetingStep1ReceivePassList , FieldValue.arrayUnion(FirebaseHelper.mUid));
        unionData3.put(FirebaseHelper.permissionList , FieldValue.arrayUnion(mPartnerUid));

        batch.set(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("InteractionHistory").document(FirebaseHelper.mUid),
                unionData, SetOptions.merge());
        batch.set(FirebaseHelper.db.collection("Users").document(mPartnerUid).collection("InteractionHistory").document(mPartnerUid),
                unionData2, SetOptions.merge());
        batch.set(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("Permission").document(FirebaseHelper.mUid),
                unionData3, SetOptions.merge());


        mLoaderLayout.setVisibility(View.VISIBLE);
        batch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mLoaderLayout.setVisibility(View.GONE);
                        Toast.makeText(ProfileCardMeetingActivity.this, "????????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();

                        Bundle bundle = new Bundle();
                        bundle.putInt(LogData.dia, 0);
                        bundle.putString(LogData.result, LogData.PASS);
                        bundle.putString(LogData.partnerUid, mPartnerUid);
                        bundle.putString(LogData.meetingId, mMeeting.getMeetingId());
                        LogData.customLog(LogData.meeting_step1_result,bundle, ProfileCardMeetingActivity.this);
                        setLogStep1Pass();

                        mQuestionText.setText("????????? ????????? ???????????????!");
                        mStep1BothButton.setVisibility(View.GONE);
                        mStep2ApplyButton.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mLoaderLayout.setVisibility(View.GONE);
                        Toast.makeText(ProfileCardMeetingActivity.this, "??????????????? ??????????????????", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void step1No() {
        String timeStamp = DateUtil.getTimeStampUnix();
        RefundedDia refundedDia = new RefundedDia(Numbers.MEETING_STEP1_REFUND, timeStamp, mPartnerUid, mPartnerUid, mMeeting.getMeetingId(), "", Strings.MEETING_STEP1_REFUND, DateUtil.getDateSec(), DateUtil.getUnixTimeLong());
        WriteBatch batch = FirebaseHelper.db.batch();
        batch.set(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("RefundDia").document(timeStamp), refundedDia);
        batch.update(FirebaseHelper.db.collection("Meetings").document(mMeetingUid).collection("Applicant").document(mApplicantUid), FirebaseHelper.meetingStep1, FirebaseHelper.FAIL);

        //InteractionHistory
        Map<String, Object> unionData = new HashMap<>();
        Map<String, Object> unionData2 = new HashMap<>();
        unionData.put(FirebaseHelper.meetingStep1SendFailList , FieldValue.arrayUnion(mPartnerUid));
        unionData2.put(FirebaseHelper.meetingStep1ReceiveFailList , FieldValue.arrayUnion(FirebaseHelper.mUid));

        batch.set(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("InteractionHistory").document(FirebaseHelper.mUid),
                unionData, SetOptions.merge());
        batch.set(FirebaseHelper.db.collection("Users").document(mPartnerUid).collection("InteractionHistory").document(mPartnerUid),
                unionData2, SetOptions.merge());

        mLoaderLayout.setVisibility(View.VISIBLE);
        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mLoaderLayout.setVisibility(View.GONE);
                Toast.makeText(ProfileCardMeetingActivity.this, "????????? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show();

                Bundle bundle = new Bundle();
                bundle.putInt(LogData.dia, 0);
                bundle.putString(LogData.result, LogData.FAIL);
                bundle.putString(LogData.partnerUid, mPartnerUid);
                bundle.putString(LogData.meetingId, mMeeting.getMeetingId());
                LogData.customLog(LogData.meeting_step1_result, bundle, ProfileCardMeetingActivity.this);
                setLogStep1Fail();

                mQuestionText.setText("????????? ????????? ?????????????????????.");
                mStep1BothButton.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mLoaderLayout.setVisibility(View.GONE);
                Toast.makeText(ProfileCardMeetingActivity.this, "??????????????? ??????????????????", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void block() {
        Bundle bundle = new Bundle();
        bundle.putInt(LogData.dia, 0);
        bundle.putString(LogData.partnerUid, mPartnerUid);
        LogData.customLog(LogData.block,  bundle, ProfileCardMeetingActivity.this);

        FirebaseHelper.blockUser(this, mPartnerUser, mLoaderLayout, true);
    }

    @Override
    public void step2Ok() {
        mLoaderLayout.setVisibility(View.VISIBLE);
        Notification notification = new Notification("????????????", mMeeting, DateUtil.getDateMin(), DateUtil.getUnixTimeLong(), MyProfile.getUser().getNickname(), "", mPartnerUid, false);//?????? ?????????????????? DB??? ??????
        Fcm fcm = new Fcm(mPartnerUid, mMeeting.getTitle(),  "????????? ????????? ?????????????????????.", mMeeting, mMeeting.getMeetingId(), FirebaseHelper.meeting, FirebaseHelper.step2Ok, DateUtil.getDateSec());

        WriteBatch batch = FirebaseHelper.db.batch();
        batch.set(FirebaseHelper.db.collection("Users").document(mPartnerUid).collection("Notification").document(DateUtil.getTimeStampUnix()), notification);
        batch.set(FirebaseHelper.db.collection("Users").document(mPartnerUid).collection("Fcm").document(), fcm);

        batch.update(FirebaseHelper.db.collection("Meetings").document(mMeetingUid).collection("Applicant").document(mApplicantUid),
                FirebaseHelper.meetingStep2, FirebaseHelper.PASS);


        //InteractionHistory
        Map<String, Object> unionData = new HashMap<>();
        Map<String, Object> unionData2 = new HashMap<>();
        unionData.put(FirebaseHelper.meetingStep2SendPassList , FieldValue.arrayUnion(mPartnerUid));
        unionData2.put(FirebaseHelper.meetingStep2ReceivePassList , FieldValue.arrayUnion(FirebaseHelper.mUid));

        batch.set(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("InteractionHistory").document(FirebaseHelper.mUid),
                unionData, SetOptions.merge());
        batch.set(FirebaseHelper.db.collection("Users").document(mPartnerUid).collection("InteractionHistory").document(mPartnerUid),
                unionData2, SetOptions.merge());


        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Bundle bundle = new Bundle();
                bundle.putInt(LogData.dia, 0);
                bundle.putString(LogData.result, LogData.PASS);
                bundle.putString(LogData.partnerUid, mPartnerUid);
                bundle.putString(LogData.meetingId, mMeeting.getMeetingId());
                LogData.customLog(LogData.meeting_step2_result, bundle, ProfileCardMeetingActivity.this);

                mStep2BothButton.setVisibility(View.GONE);
                mLoaderLayout.setVisibility(View.GONE);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileCardMeetingActivity.this, "??????????????? ??????????????????", Toast.LENGTH_SHORT).show();
                mLoaderLayout.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void step2No() {
        String timeStamp = DateUtil.getTimeStampUnix();
        RefundedDia refundedDia = new RefundedDia(Numbers.MEETING_STEP2_REFUND, timeStamp, mPartnerUid,mPartnerUid,  mMeeting.getMeetingId(), "", Strings.MEETING_STEP2_REFUND, DateUtil.getDateSec(), DateUtil.getUnixTimeLong());
        WriteBatch batch = FirebaseHelper.db.batch();
        batch.set(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("RefundDia").document(timeStamp), refundedDia);
        batch.update(FirebaseHelper.db.collection("Meetings").document(mMeetingUid).collection("Applicant").document(mApplicantUid),
                FirebaseHelper.meetingStep2, FirebaseHelper.FAIL);

        //InteractionHistory
        Map<String, Object> unionData = new HashMap<>();
        Map<String, Object> unionData2 = new HashMap<>();
        unionData.put(FirebaseHelper.meetingStep2SendFailList , FieldValue.arrayUnion(mPartnerUid));
        unionData2.put(FirebaseHelper.meetingStep2ReceiveFailList , FieldValue.arrayUnion(FirebaseHelper.mUid));

        batch.set(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("InteractionHistory").document(FirebaseHelper.mUid),
                unionData, SetOptions.merge());
        batch.set(FirebaseHelper.db.collection("Users").document(mPartnerUid).collection("InteractionHistory").document(mPartnerUid),
                unionData2, SetOptions.merge());

        mLoaderLayout.setVisibility(View.VISIBLE);
        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ProfileCardMeetingActivity.this, "????????? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show();

                Bundle bundle = new Bundle();
                bundle.putInt(LogData.dia, 0);
                bundle.putString(LogData.result, LogData.FAIL);
                bundle.putString(LogData.partnerUid, mPartnerUid);
                bundle.putString(LogData.meetingId, mMeeting.getMeetingId());
                LogData.customLog(LogData.meeting_step2_result, bundle, ProfileCardMeetingActivity.this);

                mLoaderLayout.setVisibility(View.GONE);
                onBackPressed();
                //?????? ???????????? ????????? ?????????
//                Intent intentHome = new Intent(ProfileCardMeetingActivity.this, MainActivity.class);
//                intentHome.putExtra(Strings.defaultPage, 0);
//                intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intentHome);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileCardMeetingActivity.this, "??????????????? ??????????????????", Toast.LENGTH_SHORT).show();
                mLoaderLayout.setVisibility(View.GONE);

            }
        });
    }

    @Override
    public void openMeeting(String message) {
        mLoaderLayout.setVisibility(View.VISIBLE);
        FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("Dia").orderBy(FirebaseHelper.diaId, Query.Direction.DESCENDING).limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //?????? ?????? ????????? ?????? ????????? ?????? ??????
                            if (task.getResult().isEmpty()) {
                                Toast.makeText(ProfileCardMeetingActivity.this, "???????????? ???????????????.", Toast.LENGTH_SHORT).show();
                                mLoaderLayout.setVisibility(View.GONE);
                                return;
                            }

                            //?????? ??????
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                dia = document.toObject(Dia.class);
                            }

                            //?????? ??? ????????? ??????
                            if (dia.getCurrentDia() < Numbers.OPEN_MEETING_COST) {
                                Toast.makeText(ProfileCardMeetingActivity.this, "???????????? ???????????????.", Toast.LENGTH_SHORT).show();
                                mLoaderLayout.setVisibility(View.GONE);
                                return;
                            }

                            openFreeMeeting(message);

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            Toast.makeText(ProfileCardMeetingActivity.this, "??????????????? ??????????????????", Toast.LENGTH_SHORT).show();
                            mLoaderLayout.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void openFreeMeeting(String message){
        Fcm fcm = new Fcm(mPartnerUid, MyProfile.getUser().getNickname(), MyProfile.getUser().getNickname() + "??????????????? ????????? ?????? ????????? ?????????!", mMeeting, mMeeting.getMeetingId(), FirebaseHelper.meeting, FirebaseHelper.applyMeetingStep2, DateUtil.getDateSec());
        Notification notification = new Notification("????????????", mMeeting, DateUtil.getDateMin(), DateUtil.getUnixTimeLong(), MyProfile.getUser().getNickname(), "", mPartnerUid, false); //?????? ?????????????????? DB??? ??????

        //DB??? ?????? ?????? (?????? ?????? ????????????, ??????doc ????????????)
        WriteBatch batch = FirebaseHelper.db.batch();

        Dia updatedDia = dia.getUpdatedDia(Numbers.OPEN_MEETING_COST, 0, 0, 0, Strings.OPEN_MEETING_COST, "", mPartnerUid, mMeeting.getMeetingId(), "");
        batch.set(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("Dia").document(updatedDia.getDiaId()), updatedDia);
        batch.update(FirebaseHelper.db.collection("Meetings").document(mMeetingUid).collection("Applicant").document(mApplicantUid),
                FirebaseHelper.meetingStep2, FirebaseHelper.SCREENING,
                FirebaseHelper.meetingStep2Applicant, FirebaseHelper.mUid,
                FirebaseHelper.message, message);


        //InteractionHistory
        Map<String, Object> unionData = new HashMap<>();
        Map<String, Object> unionData2 = new HashMap<>();
        unionData.put(FirebaseHelper.meetingStep2ApplyList , FieldValue.arrayUnion(mPartnerUid));
        unionData2.put(FirebaseHelper.meetingStep2AppliedList , FieldValue.arrayUnion(FirebaseHelper.mUid));

        batch.set(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("InteractionHistory").document(FirebaseHelper.mUid),
                unionData, SetOptions.merge());
        batch.set(FirebaseHelper.db.collection("Users").document(mPartnerUid).collection("InteractionHistory").document(mPartnerUid),
                unionData2, SetOptions.merge());

        batch.set(FirebaseHelper.db.collection("Users").document(mPartnerUid).collection("Notification").document(DateUtil.getTimeStampUnix()), notification);
        batch.set(FirebaseHelper.db.collection("Users").document(mPartnerUid).collection("Fcm").document(), fcm);
        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Bundle bundle = new Bundle();
                bundle.putInt(LogData.dia, Numbers.OPEN_MEETING_COST);
                bundle.putString(LogData.partnerUid, mPartnerUid);
                bundle.putString(LogData.meetingId, mMeeting.getMeetingId());
                LogData.customLog(LogData.meeting_s09_step2_apply, bundle, ProfileCardMeetingActivity.this);

                LogData.setStageMeeting(LogData.meeting_s09_step2_apply, ProfileCardMeetingActivity.this);
                if(mMeeting.getHostUid().equals(FirebaseHelper.mUid)){
                    LogData.setStageMeetingHost(LogData.meeting_s09_step2_apply,ProfileCardMeetingActivity.this);
                } else {
                    LogData.setStageMeetingApplicant(LogData.meeting_s09_step2_apply,ProfileCardMeetingActivity.this);
                }


                Toast.makeText(ProfileCardMeetingActivity.this, "???????????? ???????????? ???????????? ???????????????.", Toast.LENGTH_SHORT).show();
                mLoaderLayout.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, e.toString());
                Toast.makeText(ProfileCardMeetingActivity.this, "??????????????? ??????????????????", Toast.LENGTH_SHORT).show();
                mLoaderLayout.setVisibility(View.GONE);
            }
        });
    }


    private void setPhoneNumber() {
        mQuestionText.setText("???????????????. ?????????????????????. \n ????????? ????????????: ?????????");
        FirebaseHelper.db.collection("Meetings").document(mMeeting.getMeetingId()).collection("Applicant").document(mApplicantUid).collection("PhoneNumber").document(mApplicantUid)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        if (document.get(mPartnerUid) != null) {
                            String phoneNumber = (String) document.get(mPartnerUid);
                            mQuestionText.setText("???????????????. ?????????????????????. \n ????????? ????????????: " + phoneNumber);
                            return;
                        }
                        mQuestionText.setText("???????????????. ?????????????????????. \n ????????? ????????????: ?????? ??????");
                        Toast.makeText(ProfileCardMeetingActivity.this, "????????? ?????????????????????. ?????? ????????? ??????????????? ??????????????????.", Toast.LENGTH_LONG).show();
                    } else {
                        Log.d(TAG, "No such document");
                        mQuestionText.setText("???????????????. ?????????????????????. \n ????????? ????????????: ?????? ??????");
                        Toast.makeText(ProfileCardMeetingActivity.this, "????????? ?????????????????????. ?????? ????????? ??????????????? ??????????????????.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    if(isFirstTry){
                        isFirstTry = false;
                        Handler delayHandler = new Handler();
                        delayHandler.postDelayed(new Runnable(){
                            @Override
                            public void run() {
                                setPhoneNumber();
                            }
                        }, 1000);
                    } else {
                        mQuestionText.setText("???????????????. ?????????????????????. \n ????????? ????????????: ?????? ??????");
                        Toast.makeText(ProfileCardMeetingActivity.this, "????????? ?????????????????????. ????????? ?????? ??????????????????.", Toast.LENGTH_LONG).show();
                    }

                }
            }
        });
    }

}

