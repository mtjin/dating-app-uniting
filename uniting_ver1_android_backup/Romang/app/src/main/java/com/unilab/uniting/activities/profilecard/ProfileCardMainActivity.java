package com.unilab.uniting.activities.profilecard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.unilab.uniting.GeoUtil.GeoUtil;
import com.unilab.uniting.R;
import com.unilab.uniting.activities.chatting.ChattingConnectActivity;
import com.unilab.uniting.activities.chatting.ChattingRoomActivity;
import com.unilab.uniting.adapter.profilecard.ProfileCardPagerAdapter;
import com.unilab.uniting.fragments.dialog.DialogBlockFragment;
import com.unilab.uniting.fragments.dialog.DialogMoreFragment;
import com.unilab.uniting.fragments.dialog.DialogOk2Fragment;
import com.unilab.uniting.fragments.home.DialogLikeFragment;
import com.unilab.uniting.model.ChatMessage;
import com.unilab.uniting.model.ChatRoom;
import com.unilab.uniting.model.Dia;
import com.unilab.uniting.model.Fcm;
import com.unilab.uniting.model.Interaction;
import com.unilab.uniting.model.Notification;
import com.unilab.uniting.model.User;
import com.unilab.uniting.square.SquareViewPager;
import com.unilab.uniting.utils.BasicActivity;
import com.unilab.uniting.utils.CircleAnimIndicator;
import com.unilab.uniting.utils.DateUtil;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.LaunchUtil;
import com.unilab.uniting.utils.LogData;
import com.unilab.uniting.utils.MyProfile;
import com.unilab.uniting.utils.Numbers;
import com.unilab.uniting.utils.OnSingleClickListener;
import com.unilab.uniting.utils.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class ProfileCardMainActivity extends BasicActivity implements DialogLikeFragment.SendLikeListener, DialogBlockFragment.BlockListener {

    //상수
    final static String TAG = "profilecardMainTAG";
    final static long High_Score = 1;
    final static int CHAT_PAGE = 4;

    //변수
    private String partnerUid;
    private String partnerNickname;
    private String location;
    private String mSendLikefromDB = "";
    private User mUser = MyProfile.getUser(); //본인 유저 객체
    private User mPartnerUser; //상대방 유저 객체
    private Interaction mInteraction;
    private ChatRoom mChatRoom;
    private String mMessage = "";
    private Dia dia;

    //setInitialState 변수
    private boolean isInteractionChecked = false;
    private boolean mChatConnect = false;
    private boolean isChatRoomInfoDownloaded = false;
    private boolean mReceiveLike = false;
    private boolean mSendLike = false;
    private boolean mHighScoreBoth = false;
    private boolean mReceiveHighScore = false;
    private boolean mSendScore = false;
    private boolean mSendHighScore = false;

    private boolean isDiaChecked = false;
    private boolean freeLike = false;
    private boolean isBlockChecked = false;
    private boolean isBlocked = false;
    private boolean isUserChecked = false;
    private boolean isUserGone = false;

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
    private TextView mScreeningTextView;

    private ScrollView mGuideScrollView;
    private ImageView mGuideView;
    private ImageView mMore;
    private ImageView mBack;

    OnSingleClickListener onClickListener;
    private FirebaseStorage storage = FirebaseStorage.getInstance();

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
        setLogData();
        setOnClickListener();     //onClickListener구현
    }

    @Override
    protected void onStart() {
        super.onStart();
        setStateListener();
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

        mGuideScrollView = findViewById(R.id.profile_card_sv_guide);
        mGuideView  = findViewById(R.id.profile_card_iv_guide);

        mScreeningTextView = findViewById(R.id.profile_card_tv_screening);

        //로딩중
        mLoaderLayout = findViewById(R.id.loaderLayout);
        mLoaderLayout.setClickable(true);

        //인텐트 처리, DB로부터 유저정보 받음
        final Intent intent = getIntent();
        partnerUid = intent.getStringExtra(Strings.partnerUid);
        mPartnerUser = (User) intent.getSerializableExtra(Strings.partnerUser);
        partnerNickname = mPartnerUser.getNickname();

    }


    private void updateUI(){
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
        mEvaluationText.setText(mPartnerUser.getNickname() + "님의 호감 여부를 정해 주세요.");

        mLocation.setText(mPartnerUser.getLocation());
        if(mPartnerUser.isGeoPermitted() && MyProfile.getUser().isGeoPermitted()){
            if(mPartnerUser.getGeoPoint() != null && MyProfile.getUser().getGeoPoint() != null){
                int distance = GeoUtil.getKiloDistanceFrom(mPartnerUser.getGeoPoint(), MyProfile.getUser().getGeoPoint());
                mLocation.setText(mPartnerUser.getLocation() + ",  " + distance + "km");
            }
        }

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

        if(mPartnerUser.getMembership().equals(LaunchUtil.Screening)){
            mScreeningTextView.setVisibility(View.VISIBLE);
        } else {
            mScreeningTextView.setVisibility(View.GONE);
        }
    }

    private void setLogData(){
        Bundle bundle = new Bundle();
        bundle.putInt(LogData.dia, 0);
        bundle.putString(LogData.partnerUid,partnerUid);
        LogData.customLog(LogData.ti_s03_profile_card_view,  bundle, ProfileCardMainActivity.this);
        LogData.setStageTodayIntro(LogData.ti_s03_profile_card_view, ProfileCardMainActivity.this);
    }

    private void setOnClickListener() {
        onClickListener = new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                switch (view.getId()) {
                    case R.id.profile_card_iv_back:  // back(<) 클릭시 뒤로가기 효과
                        onBackPressed();
                        break;
                    case R.id.profile_card_iv_more:  //more(...) 클릭시 신고,차단,취소 dialog 이동
                        Bundle bundle = new Bundle();
                        bundle.putString(Strings.EXTRA_LOCATION, "프로필카드");
                        bundle.putString(Strings.partnerUid, partnerUid);
                        bundle.putSerializable(Strings.partnerUser, mPartnerUser);

                        DialogMoreFragment dialog = DialogMoreFragment.getInstance();
                        dialog.setArguments(bundle);
                        dialog.show(getSupportFragmentManager(), DialogMoreFragment.TAG_EVENT_DIALOG);
                        break;
                    case R.id.profile_card_btn_chatting: //채팅 방으로 넘기기
                        if(!isChatRoomInfoDownloaded) {
                            FirebaseHelper.db.collection("ChatRoom").document(mChatRoom.getRoomId()).get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            isChatRoomInfoDownloaded = true;
                                            mChatRoom = documentSnapshot.toObject(ChatRoom.class);
                                            startChatting();
                                        }
                                    });
                        }else{
                            startChatting();
                        }
                        break;
                    case R.id.profile_card_btn_like:
                        if (mReceiveLike) {//좋아요 수락
                            //DB로 연결정보 전달 및 노티피케이션 저장
                            chatConnect();
                        } else if(mSendLike){
                            Toast.makeText(ProfileCardMainActivity.this, "이미 친구 신청을 보냈습니다.", Toast.LENGTH_SHORT).show();
                        } else if(mSendScore){ //좋아요 보내기
                            if(freeLike){
                                Bundle eventBundle = new Bundle();
                                eventBundle.putInt(LogData.dia, 0);
                                eventBundle.putString(LogData.partnerUid, partnerUid);
                                LogData.customLog(LogData.ti_s06_pre_like, eventBundle, ProfileCardMainActivity.this);
                            } else {
                                Bundle eventBundle = new Bundle();
                                eventBundle.putInt(LogData.dia, Numbers.LIKE_COST);
                                eventBundle.putString(LogData.partnerUid, partnerUid);
                                LogData.customLog(LogData.ti_s06_pre_like, eventBundle, ProfileCardMainActivity.this);
                            }
                            LogData.setStageTodayIntro(LogData.ti_s06_pre_like,ProfileCardMainActivity.this);

                            Bundle likeBundle = new Bundle();
                            likeBundle.putString(Strings.partnerUid, partnerUid);
                            likeBundle.putSerializable(Strings.partnerUser, mPartnerUser);
                            likeBundle.putBoolean(Strings.freeLike, freeLike);

                            DialogLikeFragment likeDialog = DialogLikeFragment.getInstance();
                            likeDialog.setArguments(likeBundle);
                            likeDialog.show(getSupportFragmentManager(), DialogLikeFragment.TAG_LIKE_DIALOG);
                        } else {
                            Toast.makeText(ProfileCardMainActivity.this, "먼저 상대방의 호감여부(괜찮아요/별로에요)를 정해주세요", Toast.LENGTH_SHORT).show();
                        }
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
                    case R.id.profile_card_btn_low_score:
                        scoreToDB(0);
                        break;
                    case R.id.profile_card_btn_high_score:
                        scoreToDB(High_Score);
                        break;
                }
            }
        };

        mBack.setOnClickListener(onClickListener);
        mMore.setOnClickListener(onClickListener);
        mChattingButton.setOnClickListener(onClickListener);
        mLikeButton.setOnClickListener(onClickListener);
        mMessageLinearLayout.setOnClickListener(onClickListener);
        mUniversityBadge.setOnClickListener(onClickListener);
        mMajorBadge.setOnClickListener(onClickListener);
        mLowScoreButton.setOnClickListener(onClickListener);
        mHighScoreButton.setOnClickListener(onClickListener);
    }


    private void loadImageUrl(String imgPath){
        StorageReference storageRef = storage.getReference();
        StorageReference spaceRef = storageRef.child("Guide/" + imgPath + ".png");

        spaceRef.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(ProfileCardMainActivity.this).load(uri.toString()).into(mGuideView);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });
    }


    //채팅 연결, 좋아요, 평가 등 서로의 state 세팅
    private void setStateView() {
        if (isInteractionChecked && isBlockChecked && isDiaChecked) {
            if (freeLike){
                mLikeButton.setText(" 무료 친구 신청");
            }

            if(mInteraction == null){
                if(isBlocked || isUserGone){
                    mEvaluationText.setText("삭제된 회원입니다 :(");
                    mLikeButton.setVisibility(View.GONE);
                    mChattingButton.setVisibility(View.GONE);
                    mScoreLayout.setVisibility(View.GONE);
                } else {
                    mLikeButton.setClickable(true);
                }

                mLoaderLayout.setVisibility(View.GONE);
                return;
            }

            if(mPartnerUser.getMembership().equals(LaunchUtil.Scoring) || mPartnerUser.getMembership().equals(LaunchUtil.Screening) || mPartnerUser.getMembership().equals(LaunchUtil.Fail) || mPartnerUser.getMembership().equals(LaunchUtil.SignUp)){
                mEvaluationText.setText("가입 심사중인 회원입니다. 심사 완료 후 친구 신청을 보낼 수 있습니다.");

                if(mPartnerUser.getMembership().equals(LaunchUtil.Fail)){
                    mEvaluationText.setText("탈퇴된 회원입니다.");
                }

                mLikeButton.setVisibility(View.GONE);
                mChattingButton.setVisibility(View.GONE);
                mScoreLayout.setVisibility(View.GONE);
                mLikeButton.setClickable(false);
                mLoaderLayout.setVisibility(View.GONE);
                return;
            }

            if(mInteraction.isBlocked()){
                isBlocked = true;
            }
            mMessage = mInteraction.getMessage();

            if(mInteraction.isLikeUser0to1() && mInteraction.isLikeUser1to0() && !mInteraction.getRoomId().equals("")){
                mChatConnect = true;
            } else {
                if(mInteraction.getUid0().equals(FirebaseHelper.mUid)){
                    if(mInteraction.isLikeUser1to0() && !mInteraction.isLikeUser0to1()){
                        mReceiveLike = true;
                    } else if(mInteraction.isLikeUser0to1() && !mInteraction.isLikeUser1to0()){
                        mSendLike = true;
                    } else if(!mInteraction.isLikeUser0to1() && !mInteraction.isLikeUser1to0()){
                        if(mInteraction.getScoreUser0to1() == 1 && mInteraction.getScoreUser1to0() == 1){
                            mHighScoreBoth = true;
                        } else if (mInteraction.getScoreUser0to1() != 1 && mInteraction.getScoreUser1to0() == 1) {
                            mReceiveHighScore = true;
                        } else if ( mInteraction.getScoreUser0to1() == 1) {
                            mSendHighScore = true;
                        }
                    }
                } else if (mInteraction.getUid1().equals(FirebaseHelper.mUid)){
                    if(!mInteraction.isLikeUser1to0() && mInteraction.isLikeUser0to1()){
                        mReceiveLike = true;
                    } else if(!mInteraction.isLikeUser0to1() && mInteraction.isLikeUser1to0()){
                        mSendLike = true;
                    } else if(!mInteraction.isLikeUser0to1() && !mInteraction.isLikeUser1to0()){
                        if(mInteraction.getScoreUser0to1() == 1 && mInteraction.getScoreUser1to0() == 1){
                            mHighScoreBoth = true;
                        } else if (mInteraction.getScoreUser0to1() == 1 && mInteraction.getScoreUser1to0() != 1) {
                            mReceiveHighScore = true;
                        } else if (mInteraction.getScoreUser1to0() == 1) {
                            mSendHighScore = true;
                        }
                    }
                }
            }

            if(mInteraction.getUid0().equals(FirebaseHelper.mUid)){
                if (mInteraction.getScoreUser0to1() == 0 || mInteraction.getScoreUser0to1() == 1) {
                    mSendScore = true;
                }
            } else if (mInteraction.getUid1().equals(FirebaseHelper.mUid)){
                if (mInteraction.getScoreUser1to0() == 0 || mInteraction.getScoreUser1to0() == 1) {
                    mSendScore = true;
                }
            }

            if (mChatConnect) {
                mEvaluationText.setText(partnerNickname + "님과 채팅을 시작하세요!");
                mLikeButton.setVisibility(View.GONE);
                mChattingButton.setVisibility(View.VISIBLE);
                mScoreLayout.setVisibility(View.GONE);

            } else if (mReceiveLike) {
                mEvaluationText.setText(partnerNickname + "님의 친구 신청을 받았습니다. 무료로 수락하세요!");
                mLikeButton.setText("친구 신청 수락");
                if (mMessage.length() > 1) {
                    mMessageTextView.setText(mMessage);
                    mMessageLinearLayout.setVisibility(View.VISIBLE);
                }

                if (!this.isDestroyed()) {
                    if (mPartnerUser.getPhotoUrl().get(0) != null) {
                        Glide.with(ProfileCardMainActivity.this).load(mPartnerUser.getPhotoUrl().get(0)).fitCenter().thumbnail(0.1f).into(mMessageImageView);
                    }
                }

            } else if (mSendLike) {
                mEvaluationText.setText(partnerNickname + "님에게 친구 신청을 보냈습니다. 상대방이 수락하면 매칭 탭으로 연결됩니다.");
                mLikeButton.setVisibility(View.GONE);
                mScoreLayout.setVisibility(View.GONE);
                if (mMessage.length() > 1) {
                    mMessageTextView.setText(mMessage);
                    mMessageLinearLayout.setVisibility(View.VISIBLE);
                }

                if (!this.isDestroyed()) {
                    if (MyProfile.getUser().getPhotoUrl().get(0) != null) {
                        Glide.with(ProfileCardMainActivity.this).load(MyProfile.getUser().getPhotoUrl().get(0)).fitCenter().thumbnail(0.1f).into(mMessageImageView);
                    }
                }

            } else {
                if(isBlocked || isUserGone){
                    mEvaluationText.setText("삭제된 회원입니다 :(");
                    mLikeButton.setVisibility(View.GONE);
                    mChattingButton.setVisibility(View.GONE);
                    mScoreLayout.setVisibility(View.GONE);
                    mLoaderLayout.setVisibility(View.GONE);
                    return;
                }

                if (mHighScoreBoth) {
                    mScoreLayout.setVisibility(View.GONE);
                    mEvaluationText.setText(partnerNickname + "님과 서로 호감을 표현했어요. 친구 신청을 보내 보세요!");
                } else if (mReceiveHighScore) {
                    mEvaluationText.setText(partnerNickname + "님이 호감을 보냈어요. 친구 신청을 보내 보세요!");
                } else if (mSendScore) {
                    mScoreLayout.setVisibility(View.GONE);
                    if (mSendHighScore) {
                        mEvaluationText.setText(partnerNickname + "님에게 호감을 보냈어요. 친구 신청을 보내보세요!");
                    } else {
                        mEvaluationText.setText(partnerNickname + "님의 호감도가 평가 완료되었습니다.");
                    }
                }else {
                    mEvaluationText.setText(partnerNickname + "님의 호감 여부를 정해주세요.");
                }
            }

            if(mSendScore){
                mScoreLayout.setVisibility(View.GONE);
            }



            mLikeButton.setClickable(true);
            mLoaderLayout.setVisibility(View.GONE);
        }
    }


    private void setStateListener(){
        mLoaderLayout.setVisibility(View.VISIBLE);
        mLikeButton.setClickable(false);

        FirebaseHelper.db.collection("Interaction").whereIn(FirebaseHelper.uidList, Arrays.asList(Arrays.asList(FirebaseHelper.mUid, partnerUid), Arrays.asList(partnerUid, FirebaseHelper.mUid)))
                .orderBy(FirebaseHelper.createTime, Query.Direction.ASCENDING).limit(1)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);

                            return;
                        }

                        if(snapshots.getDocumentChanges().isEmpty()){
                            Log.w(TAG, "test222222:empty");
                            isInteractionChecked = true;
                            setStateView();
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            mInteraction = dc.getDocument().toObject(Interaction.class);
                            try{
                                boolean checkLike = (boolean) dc.getDocument().get(FirebaseHelper.checkLike);
                                if(!checkLike){
                                    Notification notification = new Notification("좋아요확인", MyProfile.getUser(), DateUtil.getDateMin(),DateUtil.getUnixTimeLong(), MyProfile.getUser().getNickname(),"", partnerUid, false);
                                    Fcm fcm = new Fcm(partnerUid, MyProfile.getUser().getNickname(),  "상대방이 친구 신청을 확인했습니다.", MyProfile.getUser(), MyProfile.getUser().getUid(), "user", FirebaseHelper.checkLike, DateUtil.getDateSec());
                                    WriteBatch batch = FirebaseHelper.db.batch();
                                    batch.set( FirebaseHelper.db.collection("Users").document(partnerUid).collection("Notification").document(DateUtil.getTimeStampUnix()), notification);
                                    batch.set(FirebaseHelper.db.collection("Users").document(partnerUid).collection("Fcm").document(), fcm);
                                    batch.update( FirebaseHelper.db.collection("Interaction").document(mInteraction.getInteractionId()), FirebaseHelper.checkLike, true);
                                    batch.commit();
                                }
                            } catch (Exception error){

                            }

                            if(mInteraction.isLikeUser0to1() && mInteraction.isLikeUser1to0() && !mInteraction.getRoomId().equals("")){
                                FirebaseHelper.db.collection("ChatRoom").document(mInteraction.getRoomId())
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                isChatRoomInfoDownloaded = true;
                                                mChatRoom = documentSnapshot.toObject(ChatRoom.class);
                                                isInteractionChecked = true;
                                                setStateView();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        isInteractionChecked = true;
                                        setStateView();
                                    }
                                });
                            } else {
                                isInteractionChecked = true;
                                setStateView();
                            }
                        }

                    }
                });


        // listen
        FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("Block").document(FirebaseHelper.mUid)
                .addSnapshotListener(ProfileCardMainActivity.this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            mLoaderLayout.setVisibility(View.GONE);
                            return;
                        }

                        isBlockChecked = true;
                        if (snapshot != null && snapshot.exists()) {
                            if(snapshot.get(FirebaseHelper.blockMeList) != null){
                                try{
                                    ArrayList<String> blockMeList = (ArrayList<String>) snapshot.get(FirebaseHelper.blockMeList);
                                    if(blockMeList.contains(partnerUid)){
                                        isBlocked = true;
                                    }
                                }catch (Exception e1){
                                    isBlocked = false;
                                }
                            }
                        } else {
                            Log.d(TAG, "Current data: null");
                        }
                        setStateView();
                    }
                });

        // listen
        FirebaseHelper.db.collection("Users").document(partnerUid)
                .addSnapshotListener(ProfileCardMainActivity.this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            mLoaderLayout.setVisibility(View.GONE);
                            isUserChecked = true;
                            setStateView();
                            return;
                        }

                        if (snapshot != null && snapshot.exists()) {
                            User user = snapshot.toObject(User.class);
                            mPartnerUser = user;
                            if(!isUserChecked){
                                updateUI();
                            }

                            if(mPartnerUser.getMembership().equals(LaunchUtil.Withdraw) || mPartnerUser.getMembership().equals(LaunchUtil.Warning) || mPartnerUser.getMembership().equals(LaunchUtil.Ban)){
                                isUserGone = true;
                            }
                        } else {
                            Log.d(TAG, "Current data: null");
                        }

                        isUserChecked = true;
                        setStateView();
                    }
                });

        FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("Dia").orderBy(FirebaseHelper.diaId, Query.Direction.DESCENDING).limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            isDiaChecked = true;
                            //그럴 일은 없지만 하트 기록이 없는 경우
                            if (task.getResult() == null) {
                                setStateView();
                                return;
                            }

                            //하트 세팅
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                dia = document.toObject(Dia.class);
                                try{
                                    long lastFreeLikeTime = dia.getRecentFreeLikeTime();
                                    if(lastFreeLikeTime < DateUtil.getUnixTimeLong() - DateUtil.dayInMilliSecond){
                                        freeLike = true;
                                    } else {
                                        freeLike = false;
                                    }
                                }catch (Exception e){
                                    freeLike = false;
                                }
                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            Toast.makeText(ProfileCardMainActivity.this, "오류 발생", Toast.LENGTH_SHORT).show();
                            mLoaderLayout.setVisibility(View.GONE);
                        }
                        setStateView();
                    }
                });

    }

    private void startChatting(){
        if(!mChatRoom.isStarted()){
            Intent intentChat = new Intent(ProfileCardMainActivity.this, ChattingConnectActivity.class);
            intentChat.putExtra(Strings.EXTRA_CHATROOM_ID, mChatRoom);
            intentChat.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentChat);
        }else{
            Intent intentChat = new Intent(ProfileCardMainActivity.this, ChattingRoomActivity.class);
            intentChat.putExtra(Strings.EXTRA_CHATROOM_ID, mChatRoom);
            intentChat.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentChat);
        }

    }


    //채팅 연결 : 본인 및 상대방 DB로 연결 정보 전송
    private void chatConnect() {
        //채팅 시작 DB에 저장
        String timeStamp = DateUtil.getTimeStampUnix();
        String dateSec = DateUtil.getDateSec();
        String dateMin = DateUtil.getDateMin();
        ArrayList<String> userUidList = new ArrayList<>();
        userUidList.add(FirebaseHelper.mUid);
        userUidList.add(partnerUid);

        //채팅방
        DocumentReference chatRoomRef = FirebaseHelper.db.collection("ChatRoom").document();
        String roomId = chatRoomRef.getId();

        //연결 정보
        Map<String, Object> chatFromTodayIntro = new HashMap<>();
        chatFromTodayIntro.put(FirebaseHelper.date, dateSec);
        chatFromTodayIntro.put(FirebaseHelper.from, FirebaseHelper.todayIntro);
        chatFromTodayIntro.put(FirebaseHelper.roomId, roomId);

        DocumentReference messageRef = FirebaseHelper.db.collection("ChatRoom").document(roomId).collection("Message").document();
        String messageId = messageRef.getId();

        mChatRoom = new ChatRoom(roomId, dateSec, DateUtil.getUnixTimeLong(), DateUtil.getUnixTimeLong(), "","", FirebaseHelper.todayIntro, userUidList, MyProfile.getUser(), mPartnerUser, mInteraction.getInteractionId(), false, false, false, false,false, "", 0,  "", "", "");
        ChatMessage chatMessage = new ChatMessage(messageId, roomId, DateUtil.getUnixTimeLong(), dateSec, "대화 가능", "운영자", FirebaseHelper.mUid, "운영자", partnerUid, ChatMessage.start);
        Notification notification = new Notification("좋아요수락", mChatRoom, dateMin, DateUtil.getUnixTimeLong(),MyProfile.getUser().getNickname(), "", mPartnerUser.getUid(), false);
        Fcm fcm = new Fcm(partnerUid, MyProfile.getUser().getNickname(),  MyProfile.getUser().getNickname() + "님이 좋아요를 수락했습니다. 채팅을 시작하세요!", mChatRoom, roomId, FirebaseHelper.chatting, FirebaseHelper.connect, dateSec);

        WriteBatch batch = FirebaseHelper.db.batch();
        batch.set( FirebaseHelper.db.collection("Users").document(partnerUid).collection("Notification").document(timeStamp), notification);
        batch.set( chatRoomRef, mChatRoom);
        batch.set( messageRef, chatMessage);
        batch.set(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("TodayIntroConnected").document(partnerUid), mPartnerUser);
        batch.update(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("TodayIntroConnected").document(partnerUid), chatFromTodayIntro);
        batch.set(FirebaseHelper.db.collection("Users").document(partnerUid).collection("TodayIntroConnected").document(FirebaseHelper.mUid), mUser);
        batch.update(FirebaseHelper.db.collection("Users").document(partnerUid).collection("TodayIntroConnected").document(FirebaseHelper.mUid), chatFromTodayIntro);
        batch.set(FirebaseHelper.db.collection("Users").document(partnerUid).collection("Fcm").document(), fcm);


        if(mInteraction == null){
            Toast.makeText(ProfileCardMainActivity.this, "오류가 발생했어요. 재시작해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> interactionData = new HashMap<>();
        interactionData.put(FirebaseHelper.roomId, roomId);
        if(mInteraction.getUid0().equals(FirebaseHelper.mUid)){
            interactionData.put(FirebaseHelper.likeUser0to1, true);
        } else {
            interactionData.put(FirebaseHelper.likeUser1to0, true);
        }
        batch.set(FirebaseHelper.db.collection("Interaction").document(mInteraction.getInteractionId()), interactionData, SetOptions.merge());

        mLoaderLayout.setVisibility(View.VISIBLE);
        batch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mLoaderLayout.setVisibility(View.GONE);
                        //상대에게 알림
                        Toast.makeText(ProfileCardMainActivity.this, "친구 신청을 수락하였습니다.", Toast.LENGTH_SHORT).show();

                        Bundle eventBundle = new Bundle();
                        eventBundle.putInt(LogData.dia, 0);
                        eventBundle.putString(LogData.partnerUid, partnerUid);
                        LogData.customLog(LogData.ti_s08_like_ok,  eventBundle, ProfileCardMainActivity.this);
                        LogData.setStageTodayIntro(LogData.ti_s08_like_ok,ProfileCardMainActivity.this);
                        //InteractionHistory
                        WriteBatch batch2 = FirebaseHelper.db.batch();
                        batch2.update(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("InteractionHistory").document(FirebaseHelper.mUid),
                                FirebaseHelper.acceptLikeList , FieldValue.arrayUnion(partnerUid));
                        batch2.update(FirebaseHelper.db.collection("Users").document(partnerUid).collection("InteractionHistory").document(partnerUid),
                                FirebaseHelper.likeAcceptedList,FieldValue.arrayUnion(FirebaseHelper.mUid));
                        batch2.commit();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileCardMainActivity.this, "네트워크가 불안정합니다.", Toast.LENGTH_SHORT).show();
                        mLoaderLayout.setVisibility(View.GONE);
                    }
                });

    }

    //레이팅 점수 결과 본인 및 상대방 DB로 전송
    private void scoreToDB(double rating) {
        mLoaderLayout.setVisibility(View.VISIBLE);
        long nowUnixTime = DateUtil.getUnixTimeLong();
        Map<String, Object> rateData = new HashMap<>();
        rateData.put("dateOfScore", DateUtil.getDateMin());
        rateData.put("score", rating);
        rateData.put("unixTime", nowUnixTime);

        WriteBatch scoreBatch = FirebaseHelper.db.batch();
        scoreBatch.set(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("SendScore").document(partnerUid),mPartnerUser);
        scoreBatch.update(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("SendScore").document(partnerUid), rateData);
        scoreBatch.set(FirebaseHelper.db.collection("Users").document(partnerUid).collection("ReceiveScore").document(FirebaseHelper.mUid), mUser);
        scoreBatch.update(FirebaseHelper.db.collection("Users").document(partnerUid).collection("ReceiveScore").document(FirebaseHelper.mUid), rateData);


        if(rating == High_Score){
            Fcm fcm = new Fcm(partnerUid, MyProfile.getUser().getNickname(),  MyProfile.getUser().getNickname() + "님이 호감을 표현했습니다. 친구 신청을 보내보세요!", MyProfile.getUser(), MyProfile.getUser().getUid(), "user", FirebaseHelper.highScore, DateUtil.getDateSec());
            scoreBatch.set(FirebaseHelper.db.collection("Users").document(partnerUid).collection("Fcm").document(),fcm);
            scoreBatch.update(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("Permission").document(FirebaseHelper.mUid),
                    FirebaseHelper.permissionList , FieldValue.arrayUnion(partnerUid));
        }

        if(mInteraction != null){
            if(mInteraction.getUid0().equals(FirebaseHelper.mUid)){
                scoreBatch.update(FirebaseHelper.db.collection("Interaction").document(mInteraction.getInteractionId()), FirebaseHelper.scoreUser0to1, rating);
            } else {
                scoreBatch.update(FirebaseHelper.db.collection("Interaction").document(mInteraction.getInteractionId()), FirebaseHelper.scoreUser1to0, rating);
            }
        } else {
            DocumentReference interactionDoc = FirebaseHelper.db.collection("Interaction").document();
            ArrayList<String> uidList = new ArrayList<>();
            uidList.add(FirebaseHelper.mUid);
            uidList.add(partnerUid);
            Interaction interaction = new Interaction(interactionDoc.getId(), FirebaseHelper.mUid, partnerUid, uidList, rating, Interaction.PRE_SCORE, false,
                    false, "", "", "", false, "", MyProfile.getUser(), mPartnerUser, nowUnixTime, nowUnixTime);
            scoreBatch.set(interactionDoc, interaction, SetOptions.merge());
        }

        scoreBatch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("Evaluation").document(partnerUid).update(
                        FirebaseHelper.interacted, true, FirebaseHelper.score, rating);
                FirebaseHelper.db.collection("Tier").document(partnerUid).update(FirebaseHelper.receiveScoreList + "." + FirebaseHelper.mUid, rating,
                            FirebaseHelper.scoreCount, FieldValue.increment(1));

                //InteractionHistory
                WriteBatch batch1 = FirebaseHelper.db.batch();
                Map<String, Object> unionData = new HashMap<>();
                Map<String, Object> unionData2 = new HashMap<>();
                if(rating == 1){
                    unionData.put(FirebaseHelper.sendHighScoreList , FieldValue.arrayUnion(partnerUid));
                    unionData2.put(FirebaseHelper.receiveHighScoreList , FieldValue.arrayUnion(FirebaseHelper.mUid));
                } else if (rating == 0){
                    unionData.put(FirebaseHelper.sendLowScoreList , FieldValue.arrayUnion(partnerUid));
                    unionData2.put(FirebaseHelper.receiveLowScoreList , FieldValue.arrayUnion(FirebaseHelper.mUid));
                }
                batch1.set(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("InteractionHistory").document(FirebaseHelper.mUid), unionData, SetOptions.merge());
                batch1.set(FirebaseHelper.db.collection("Users").document(partnerUid).collection("InteractionHistory").document(partnerUid), unionData2, SetOptions.merge());
                batch1.update(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("InteractionHistory").document(FirebaseHelper.mUid), FirebaseHelper.sendScoreList + "." + partnerUid, rating);
                batch1.update( FirebaseHelper.db.collection("Users").document(partnerUid).collection("InteractionHistory").document(partnerUid), FirebaseHelper.receiveScoreList + "." + FirebaseHelper.mUid, rating);
                batch1.commit();

                Bundle bundle = new Bundle();
                bundle.putInt(LogData.dia, 0);
                bundle.putDouble(LogData.score, rating);
                bundle.putString(LogData.partnerUid, partnerUid);
                bundle.putString(LogData.location, LogData.todayIntro);
                LogData.customLog(LogData.ti_scoring, bundle, ProfileCardMainActivity.this);
                if(rating == 1){
                    LogData.customLog(LogData.ti_s05_scoring_high, bundle, ProfileCardMainActivity.this);
                    LogData.setStageTodayIntro(LogData.ti_s05_scoring_high, ProfileCardMainActivity.this);
                } else if (rating == 0 ){
                    LogData.customLog(LogData.ti_s04_scoring_low, bundle, ProfileCardMainActivity.this);
                    LogData.setStageTodayIntro(LogData.ti_s04_scoring_low, ProfileCardMainActivity.this);
                }

                mScoreLayout.setVisibility(View.GONE);
                mLoaderLayout.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileCardMainActivity.this, "네트워크가 불안정합니다.", Toast.LENGTH_SHORT).show();
                mLoaderLayout.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

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
    public void sendLike(String message) {
        mMessage = message;

        if(dia == null){
            Toast.makeText(ProfileCardMainActivity.this, "오류 발생. 앱을 재시작해주세요.", Toast.LENGTH_SHORT).show();
            mLoaderLayout.setVisibility(View.GONE);
            return;
        }

        if(freeLike){
            updateLikeAndDia();
            return;
        }

        if (dia.getCurrentDia() < Numbers.LIKE_COST) { //하트수가 적을 때
            Toast.makeText(ProfileCardMainActivity.this, "다이아가 부족합니다.", Toast.LENGTH_SHORT).show();
            mLoaderLayout.setVisibility(View.GONE);
            return;
        }

        updateLikeAndDia();

    }


    //좋아요 DB에 업로드
    private void updateLikeAndDia() {
        mLoaderLayout.setVisibility(View.VISIBLE);
        long noxUnixTime = DateUtil.getUnixTimeLong();
        Map<String, Object> messageData = new HashMap<>();
        messageData.put(FirebaseHelper.dateOfLike, DateUtil.getDateMin());
        messageData.put(FirebaseHelper.message, mMessage);
        Fcm fcm = new Fcm(partnerUid, MyProfile.getUser().getNickname(),  "친구 신청을 받았습니다. 무료로 수락하세요!", MyProfile.getUser(), MyProfile.getUser().getUid(), "user", FirebaseHelper.like, DateUtil.getDateSec());
        //하트 변동 내역, 상대 노티피케이션

        Notification notification = new Notification("좋아요보냄", MyProfile.getUser(), DateUtil.getDateMin(),DateUtil.getUnixTimeLong(),MyProfile.getUser().getNickname(), "", mPartnerUser.getUid(), false);

        WriteBatch batch = FirebaseHelper.db.batch();
        if(freeLike){
            Dia updatedDia = dia.useFreeLike(mInteraction.getInteractionId(), partnerUid);
            batch.set(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("Dia").document(updatedDia.getDiaId()), updatedDia);
        } else {
            Dia updatedDia =  dia.getUpdatedDia(Numbers.LIKE_COST, 0, 0, 0, Strings.LIKE_COST, mInteraction.getInteractionId(), partnerUid, "", "");
            batch.set(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("Dia").document(updatedDia.getDiaId()), updatedDia);
        }

        batch.set(FirebaseHelper.db.collection("Users").document(partnerUid).collection("Notification").document(DateUtil.getTimeStampUnix()), notification);
        batch.set(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("SendLikeHistory").document(partnerUid), mPartnerUser);
        batch.update(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("SendLikeHistory").document(partnerUid), messageData);
        batch.set(FirebaseHelper.db.collection("Users").document(partnerUid).collection("ReceiveLikeHistory").document(FirebaseHelper.mUid), mUser);
        batch.update(FirebaseHelper.db.collection("Users").document(partnerUid).collection("ReceiveLikeHistory").document(FirebaseHelper.mUid), messageData);
        batch.set(FirebaseHelper.db.collection("Users").document(partnerUid).collection("Fcm").document(), fcm);


        if(mInteraction == null){
            Toast.makeText(ProfileCardMainActivity.this, "오류 발생. 종료 후 재시도해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(mInteraction.getUid0().equals(FirebaseHelper.mUid)){
            batch.update(FirebaseHelper.db.collection("Interaction").document(mInteraction.getInteractionId()),
                    FirebaseHelper.likeUser0to1, true,
                    FirebaseHelper.likeApplicant, FirebaseHelper.mUid,
                    FirebaseHelper.recentTime, noxUnixTime,
                    FirebaseHelper.message, mMessage);
        } else {
            batch.update(FirebaseHelper.db.collection("Interaction").document(mInteraction.getInteractionId()),
                    FirebaseHelper.likeUser1to0, true,
                    FirebaseHelper.likeApplicant, FirebaseHelper.mUid,
                    FirebaseHelper.recentTime, noxUnixTime,
                    FirebaseHelper.message, mMessage);
        }

        batch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Transaction success!");
                        Toast.makeText(ProfileCardMainActivity.this, "친구 신청을 보냈습니다.", Toast.LENGTH_SHORT).show();

                        WriteBatch batch2 = FirebaseHelper.db.batch();
                        batch2.update(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("InteractionHistory").document(FirebaseHelper.mUid),
                                FirebaseHelper.sendLikeList , FieldValue.arrayUnion(partnerUid));
                        batch2.update(FirebaseHelper.db.collection("Users").document(partnerUid).collection("InteractionHistory").document(partnerUid),
                                FirebaseHelper.receiveLikeList,FieldValue.arrayUnion(FirebaseHelper.mUid));
                        batch2.commit();

                        if(freeLike){
                            Bundle eventBundle = new Bundle();
                            eventBundle.putInt(LogData.dia, 0);
                            eventBundle.putString(LogData.partnerUid, partnerUid);
                            LogData.customLog(LogData.ti_s07_like, eventBundle, ProfileCardMainActivity.this);
                        } else {
                            Bundle eventBundle = new Bundle();
                            eventBundle.putInt(LogData.dia, Numbers.LIKE_COST);
                            eventBundle.putString(LogData.partnerUid, partnerUid);
                            LogData.customLog(LogData.ti_s07_like, eventBundle, ProfileCardMainActivity.this);
                        }
                        LogData.setStageTodayIntro(LogData.ti_s07_like, ProfileCardMainActivity.this);
                        mLoaderLayout.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Transaction failure.", e);
                        Toast.makeText(ProfileCardMainActivity.this, "네트워크가 불안정합니다.", Toast.LENGTH_SHORT).show();
                        mLoaderLayout.setVisibility(View.GONE);
                    }
                });

    }

    @Override
    public void block() {
        FirebaseHelper.blockUser(ProfileCardMainActivity.this, mPartnerUser, mLoaderLayout, true);
        Bundle eventBundle = new Bundle();
        eventBundle.putInt(LogData.dia, 0);
        eventBundle.putString(LogData.partnerUid, partnerUid);
        if(mChatRoom != null){
            eventBundle.putString(LogData.roomId, mChatRoom.getRoomId());
        }
        LogData.customLog(LogData.block,  eventBundle, ProfileCardMainActivity.this);
    }
}