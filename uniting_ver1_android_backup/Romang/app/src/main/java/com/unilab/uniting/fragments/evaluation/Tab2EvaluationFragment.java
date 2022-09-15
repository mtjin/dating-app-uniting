package com.unilab.uniting.fragments.evaluation;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;
import com.unilab.uniting.R;
import com.unilab.uniting.activities.MainActivity;
import com.unilab.uniting.adapter.profilecard.ProfileCardPagerAdapter;
import com.unilab.uniting.fragments.dialog.DialogMoreFragment;
import com.unilab.uniting.model.Fcm;
import com.unilab.uniting.model.Interaction;
import com.unilab.uniting.model.User;
import com.unilab.uniting.square.SquareViewPager;
import com.unilab.uniting.utils.CircleAnimIndicator;
import com.unilab.uniting.utils.DateUtil;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.LogData;
import com.unilab.uniting.utils.MyProfile;
import com.unilab.uniting.utils.OnSingleClickListener;
import com.unilab.uniting.utils.RemoteConfig;
import com.unilab.uniting.utils.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

public class Tab2EvaluationFragment extends Fragment implements MainActivity.BlockEvaluationListener, MainActivity.ViewPagerClickListener {

    private FirebaseFunctions mFunctions;
    private ViewGroup rootView;

    //상수 및 변수
    final static String TAG = "evaluationTAG";
    private String mPartnerUid;
    private User mPartnerUser;
    private User mWaitingUser;
    private ArrayList<User> mMyUserList = new ArrayList<>();
    private ArrayList<User> mScreeningUserList = new ArrayList<>();
    private ArrayList<User> mTierUserList = new ArrayList<>();
    private boolean isRefreshing = false;
    private boolean isCurrentUserFromScreeningList = false;
    private boolean isCurrentUserFromTierList = false;
    private boolean isWaitingUserFromScreeningList = false;
    private boolean isWaitingUserFromTierList = false;
    private int count = 0;
    private ListenerRegistration listener1;
    private ListenerRegistration listener2;
    private ListenerRegistration listener3;
    private String partnerGender = "남자";

    double upperTierPercent = 1.0;
    double lowerTierPercent = 0.0;

    //뷰 세팅
    private LinearLayout mScoreLayout;
    private Button mLowScoreButton;
    private Button mHighScoreButton;
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
    private RelativeLayout mLoaderLayout; //로딩레이아웃
    private FrameLayout mRefreshLayout; //새로고침 화면
    private Button mRefreshButton;
    private TextView mEventDiaTextView;
    private TextView mScreeningTextView;
    private TextView mScreeningGuideTextView;

    private LinearLayout mStoryTotalLayout;
    private LinearLayout mStory0Layout;
    private LinearLayout mStory1Layout;
    private LinearLayout mStory2Layout;

    private ImageView mMore;

    //뷰페이저
    ProfileCardPagerAdapter profileCardPagerAdapter;
    SquareViewPager viewPager;
    SquareViewPager.OnPageChangeListener mOnPageChangeListener;

    FragmentManager childFragMang;

    public Tab2EvaluationFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //프래그먼트를 인플레이터로 뷰 객체화
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_tab2_evaluation, container, false);


        init();
        childFragMang = getChildFragmentManager();

        if(MyProfile.getUser().getGender().equals("")){
            return rootView;
        }

        mLowScoreButton.setClickable(false);
        mHighScoreButton.setClickable(false);
        loadUser();
        setBlockListener();
        setOnClickListener();
        setViewPagerClickListener();

        return rootView;

    }

    private void setViewPagerClickListener(){
        MainActivity activity = (MainActivity) getActivity();
        activity.setEvaluationDataListener(this);
    }


    private void init() {
        mFunctions = FirebaseFunctions.getInstance(Strings.region_asia);

        //바인딩
        mScoreLayout = rootView.findViewById(R.id.profile_card_btn_linear_score);
        mLowScoreButton = rootView.findViewById(R.id.profile_card_btn_low_score);
        mHighScoreButton = rootView.findViewById(R.id.profile_card_btn_high_score);
        viewPager = rootView.findViewById(R.id.profile_card_vp_photo);
        mNickNameAge = rootView.findViewById(R.id.profile_card_tv_nickname_age);
        mHeight = rootView.findViewById(R.id.profile_card_tv_height);
        mUniversity = rootView.findViewById(R.id.profile_card_tv_university);
        mMajor = rootView.findViewById(R.id.profile_card_tv_major);
        mEvaluationText = rootView.findViewById(R.id.profile_card_tv_evaluation_text);
        mLocation = rootView.findViewById(R.id.profile_card_tv_location);
        mPersonality = rootView.findViewById(R.id.profile_card_tv_personality);
        mBloodType = rootView.findViewById(R.id.profile_card_tv_bloodtype);
        mReligion = rootView.findViewById(R.id.profile_card_tv_religion);
        mDrinking = rootView.findViewById(R.id.profile_card_tv_drinking);
        mSmoking = rootView.findViewById(R.id.profile_card_tv_smoking);
        mStory0 = rootView.findViewById(R.id.profile_card_tv_story0);
        mStory1 = rootView.findViewById(R.id.profile_card_tv_story1);
        mStory2 = rootView.findViewById(R.id.profile_card_tv_story2);
        mEventDiaTextView = rootView.findViewById(R.id.profile_card_tv_dia_event);

        mMore = rootView.findViewById(R.id.profile_card_iv_more);

        mStoryTotalLayout = rootView.findViewById(R.id.profile_card_linear_story_total);
        mStory0Layout = rootView.findViewById(R.id.profile_card_linear_story0);
        mStory1Layout = rootView.findViewById(R.id.profile_card_linear_story1);
        mStory2Layout = rootView.findViewById(R.id.profile_card_linear_story2);

        mScreeningTextView = rootView.findViewById(R.id.evaluation_tv_screening);
        mScreeningGuideTextView = rootView.findViewById(R.id.evaluation_tv_screening_guide);

        //로딩, 새로고침 레이아웃
        mRefreshLayout = rootView.findViewById(R.id.profile_card_linear_refresh);
        mRefreshButton = rootView.findViewById(R.id.profile_card_btn_refresh);
        mLoaderLayout = rootView.findViewById(R.id.loaderLayout);
        mLoaderLayout.setClickable(true);
        mRefreshLayout.setClickable(true);

        if(MyProfile.getUser().getGender().equals("남자")){
            partnerGender = "여자";
        } else if(MyProfile.getUser().getGender().equals("여자")){
            partnerGender = "남자";
        }

        if(MyProfile.getUser().getGender().equals("여자")){
            lowerTierPercent = 0.0;
            if(MyProfile.getUser().getTierPercent() < 0.3){
                upperTierPercent = 0.6;
            } else {
                upperTierPercent = MyProfile.getUser().getTierPercent() + 0.3;
            }
        }

        if(MyProfile.getUser().getGender().equals("남자")){
            upperTierPercent = 1.0;
            lowerTierPercent = MyProfile.getUser().getTierPercent() - 0.7;
        }

    }


    //초기 세팅
    private void loadUser() {
        //Before값 불러오기
        mLoaderLayout.setVisibility(View.VISIBLE);
        mScreeningUserList = new ArrayList<>();
        mTierUserList = new ArrayList<>();
        mMyUserList = new ArrayList<>();

        listener1 = FirebaseHelper.db.collection("Evaluation")
                .whereEqualTo(FirebaseHelper.gender, partnerGender)
                .whereLessThan(FirebaseHelper.scoreCount, RemoteConfig.evaluation_screening_limit)
                .orderBy(FirebaseHelper.scoreCount, Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        count++;
                        if(count == 3){
                            mLoaderLayout.setVisibility(View.GONE);
                        }


                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            mRefreshLayout.setVisibility(View.VISIBLE);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            User newUser = dc.getDocument().toObject(User.class);
                            try {
                                Map<String, Double> receiveScoreList = (Map<String,Double>) dc.getDocument().get(FirebaseHelper.receiveScoreList);
                                if(receiveScoreList.get(FirebaseHelper.mUid) == null){
                                    switch (dc.getType()) {
                                        case ADDED:
                                            if(!newUser.getUid().equals(FirebaseHelper.mUid)){
                                                FirebaseHelper.checkBlock(newUser.getUid())
                                                        .addOnCompleteListener(new OnCompleteListener<Map<String,Boolean>>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Map<String,Boolean>> task) {
                                                                if (!task.isSuccessful()) {
                                                                    Exception e = task.getException();
                                                                    return;
                                                                }

                                                                Map<String,Boolean> taskResult = task.getResult();
                                                                if (taskResult.get(FirebaseHelper.isBlocked) != null){
                                                                    boolean isBlocked = taskResult.get(FirebaseHelper.isBlocked);
                                                                    if(!isBlocked){
                                                                        mScreeningUserList.add(newUser);
                                                                        setWaitingList();
                                                                    }
                                                                }


                                                            }
                                                        });

                                            }
                                            break;
                                        case MODIFIED:
                                            for(int i = 0; i < mScreeningUserList.size(); i++){
                                                if(newUser.getUid().equals(mScreeningUserList.get(i).getUid()) && !newUser.getUid().equals(FirebaseHelper.mUid)){
                                                    mScreeningUserList.remove(i);
                                                    mScreeningUserList.add(i, newUser);
                                                }
                                                break;
                                            }
                                            break;
                                        case REMOVED:
                                            for(int i = 0; i < mScreeningUserList.size(); i++){
                                                if(newUser.getUid().equals(mScreeningUserList.get(i).getUid())){
                                                    mScreeningUserList.remove(i);
                                                }
                                                break;
                                            }
                                            break;
                                    }
                                } else {
                                    for(int i = 0; i < mScreeningUserList.size(); i++){
                                        if(newUser.getUid().equals(mScreeningUserList.get(i).getUid())){
                                            mScreeningUserList.remove(i);
                                        }
                                        break;
                                    }
                                }
                            }catch (Exception err){

                            }
                        }
                        setWaitingList();
                        setLogData();

                    }
                });

        listener2 = FirebaseHelper.db.collection("Tier")
                .whereEqualTo(FirebaseHelper.gender, partnerGender)
                .whereLessThan(FirebaseHelper.scoreCount, RemoteConfig.tier_count)
                .orderBy(FirebaseHelper.scoreCount, Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        count++;
                        if(count == 3){
                            mLoaderLayout.setVisibility(View.GONE);
                        }


                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            mRefreshLayout.setVisibility(View.VISIBLE);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            User newUser = dc.getDocument().toObject(User.class);
                            try {
                                Map<String, Double> receiveScoreList = (Map<String,Double>) dc.getDocument().get(FirebaseHelper.receiveScoreList);
                                if(receiveScoreList.get(FirebaseHelper.mUid) == null){
                                    switch (dc.getType()) {
                                        case ADDED:
                                            if(!newUser.getUid().equals(FirebaseHelper.mUid)) {
                                                if(newUser.getTierPercent() < upperTierPercent && newUser.getTierPercent() > lowerTierPercent){
                                                    FirebaseHelper.checkBlock(newUser.getUid())
                                                            .addOnCompleteListener(new OnCompleteListener<Map<String,Boolean>>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Map<String,Boolean>> task) {
                                                                    if (!task.isSuccessful()) {
                                                                        Exception e = task.getException();
                                                                        return;
                                                                    }

                                                                    Map<String,Boolean> taskResult = task.getResult();
                                                                    if (taskResult.get(FirebaseHelper.isBlocked) != null){
                                                                        boolean isBlocked = taskResult.get(FirebaseHelper.isBlocked);
                                                                        if(!isBlocked){
                                                                            mTierUserList.add(newUser);
                                                                            setWaitingList();
                                                                        }
                                                                    }


                                                                }
                                                            });
                                                }
                                            }
                                            break;
                                        case MODIFIED:
                                            for(int i = 0; i < mTierUserList.size(); i++){
                                                if (newUser.getUid().equals(mTierUserList.get(i).getUid()) && !newUser.getUid().equals(FirebaseHelper.mUid)) {
                                                    mTierUserList.remove(i);
                                                    mTierUserList.add(i, newUser);
                                                }
                                                break;
                                            }
                                            break;
                                        case REMOVED:
                                            for(int i = 0; i < mTierUserList.size(); i++){
                                                if (newUser.getUid().equals(mTierUserList.get(i).getUid())) {
                                                    mTierUserList.remove(i);
                                                }
                                                break;
                                            }
                                            break;
                                    }
                                } else {
                                    for(int i = 0; i < mTierUserList.size(); i++){
                                        if (newUser.getUid().equals(mTierUserList.get(i).getUid())) {
                                            mTierUserList.remove(i);
                                        }
                                        break;
                                    }
                                }
                            }catch (Exception err){

                            }
                        }
                        setWaitingList();
                        setLogData();

                    }
                });

        listener3 = FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("Evaluation").whereEqualTo(FirebaseHelper.interacted, false)
                .whereEqualTo(FirebaseHelper.blocked, false).orderBy(FirebaseHelper.introTime, Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        count++;
                        if(count == 3){
                            mLoaderLayout.setVisibility(View.GONE);
                        }

                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            mRefreshLayout.setVisibility(View.VISIBLE);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            User newUser = dc.getDocument().toObject(User.class);
                            switch (dc.getType()) {
                                case ADDED:
                                    mMyUserList.add(newUser);
                                    break;
                                case MODIFIED:
                                    for(int i = 0; i < mMyUserList.size(); i++){
                                        if(newUser.getUid().equals(mMyUserList.get(i).getUid())){
                                            mMyUserList.remove(i);
                                            mMyUserList.add(i, newUser);
                                        }
                                        break;
                                    }
                                    break;
                                case REMOVED:
                                    for(int i = 0; i < mMyUserList.size(); i++){
                                        if(newUser.getUid().equals(mMyUserList.get(i).getUid())){
                                            mMyUserList.remove(i);
                                        }
                                        break;
                                    }
                                    break;
                            }
                        }

                        setWaitingList();
                        setLogData();

                    }
                });
    }



    private boolean isDayLimitDone() {
        String today = DateUtil.getDate();
        if(getActivity() != null){
            SharedPreferences pref = getActivity().getSharedPreferences(FirebaseHelper.mUid, MODE_PRIVATE);
            Set<String> defaultSet = new HashSet<String>();
            Set<String> partnerSet = pref.getStringSet(today, defaultSet);
            Log.d(TAG, partnerSet.toString());
            return (partnerSet.size() >= RemoteConfig.evaluation_day_limit);
        } else {
            return false;
        }


    }

    private void savePartnerUidShared(String uid) {
        if(getActivity() != null){
            SharedPreferences pref = getActivity().getSharedPreferences(FirebaseHelper.mUid, MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();

            String today = DateUtil.getDate();
            Set<String> defaultSet = new HashSet<String>();
            Set<String> partnerSet = pref.getStringSet(today, defaultSet);
            partnerSet.add(uid);
            Set<String> newSet = partnerSet;

            editor.remove(today);
            editor.apply();

            editor.putStringSet(today, newSet);
            editor.apply();
            editor.commit();
        }
    }

    private void setWaitingList(){
        //레이팅바 리셋
        mLowScoreButton.setClickable(true);
        mHighScoreButton.setClickable(true);

        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        int randomNumber = random.nextInt(10);

        if(mScreeningUserList.size() > 0 && mTierUserList.size() >0){
            if(randomNumber < 5){
                mWaitingUser = mScreeningUserList.get(0);
                isWaitingUserFromScreeningList = true;
                isWaitingUserFromTierList = false;
            }else {
                mWaitingUser = mTierUserList.get(0);
                isWaitingUserFromScreeningList = false;
                isWaitingUserFromTierList = true;
            }
        }else if(mScreeningUserList.size() > 0){
            mWaitingUser = mScreeningUserList.get(0);
            isWaitingUserFromScreeningList = true;
            isWaitingUserFromTierList = false;
        } else if (mTierUserList.size() > 0 ){
            mWaitingUser = mTierUserList.get(0);
            isWaitingUserFromScreeningList = false;
            isWaitingUserFromTierList = true;
        }else if (mMyUserList.size() > 0){
            mWaitingUser = mMyUserList.get(0);
            isWaitingUserFromScreeningList = false;
            isWaitingUserFromTierList = false;
        } else {
            mWaitingUser = null;
            isWaitingUserFromScreeningList = false;
            isWaitingUserFromTierList = false;
        }

        if(mPartnerUser == null){
            updateUI();
        }

    }

    private void updateUI() {
        if(mWaitingUser == null){
            mLowScoreButton.setClickable(false);
            mHighScoreButton.setClickable(false);
            mLoaderLayout.setVisibility(View.GONE);
            mRefreshLayout.setVisibility(View.VISIBLE);
            if(isRefreshing){
                Toast.makeText(getActivity(), "현재 추천 프로필 카드가 없습니다.", Toast.LENGTH_SHORT).show();
                isRefreshing = false;
            }
            return;
        }

        mPartnerUser = mWaitingUser;
        mPartnerUid = mPartnerUser.getUid();
        isCurrentUserFromScreeningList = isWaitingUserFromScreeningList;
        isCurrentUserFromTierList = isWaitingUserFromTierList;

        if(!isCurrentUserFromScreeningList && !isCurrentUserFromTierList && isDayLimitDone()) {
            mPartnerUser = null;
            mLoaderLayout.setVisibility(View.GONE);
            mRefreshLayout.setVisibility(View.VISIBLE);
            if(isRefreshing){
                Toast.makeText(getActivity(), "현재 추천 프로필 카드가 없습니다.", Toast.LENGTH_SHORT).show();
                isRefreshing = false;
            }
            setLogLimitDone();
            return;
        }

        mRefreshLayout.setVisibility(View.GONE);
        if(isCurrentUserFromScreeningList){
            mScreeningTextView.setVisibility(View.VISIBLE);
            mScreeningGuideTextView.setVisibility(View.VISIBLE);
        } else {
            mScreeningTextView.setVisibility(View.GONE);
            mScreeningGuideTextView.setVisibility(View.GONE);
        }

        User user = mPartnerUser;
        //null값 제외
        ArrayList<String> imageUrlList = new ArrayList<>();
        if (user != null && user.getPhotoUrl() != null) {
            for (int i = 0; i < user.getPhotoUrl().size(); i++) {
                if (user.getPhotoUrl().get(i) != null) {
                    imageUrlList.add(user.getPhotoUrl().get(i));
                }
            }
        }


        if(isAdded()){
            profileCardPagerAdapter = new ProfileCardPagerAdapter(childFragMang, imageUrlList.size(), imageUrlList);
            setCircleAnimIndicator(rootView, imageUrlList); //인디케이터 생성
            viewPager.setAdapter(profileCardPagerAdapter);
            viewPager.addOnPageChangeListener(mOnPageChangeListener);

            //DB에서 텍스트 받기
            mNickNameAge.setText(user.getNickname() + ", " + user.getAge() + "세");
            mHeight.setText(user.getHeight());
            mUniversity.setText(user.getUniversity());
            mMajor.setText(user.getMajor());
            mEvaluationText.setText(user.getNickname() + "님의 호감도를 정해주세요.");
            mLocation.setText(user.getLocation());
            if (user.getPersonality() != null) {
                String personality = user.getPersonality().toString();
                personality = personality.replace("[", "");
                personality = personality.replace("]", "");
                mPersonality.setText(personality);
            }
            mBloodType.setText(user.getBloodType());
            mReligion.setText(user.getReligion());
            mDrinking.setText(user.getDrinking());
            mSmoking.setText(user.getSmoking());
            mStory0.setText(user.getStory0());
            mStory1.setText(user.getStory1());
            mStory2.setText(user.getStory2());


            if(user.getStory0() == null || user.getStory0().equals("")){
                mStory0Layout.setVisibility(View.GONE);
            }

            if( user.getStory1() == null || user.getStory1().equals("")){
                mStory1Layout.setVisibility(View.GONE);
            }

            if(user.getStory2() == null || user.getStory2().equals("")){
                mStory2Layout.setVisibility(View.GONE);
            }

            if(mStory0Layout.getVisibility() == View.GONE &&  mStory0Layout.getVisibility() == View.GONE &&  mStory0Layout.getVisibility() == View.GONE){
                mStoryTotalLayout.setVisibility(View.GONE);
            }
        }



        mLoaderLayout.setVisibility(View.GONE);
    }

    private void setLogData(){
        Bundle bundle = new Bundle();
        bundle.putInt(LogData.dia, 0);
        bundle.putString(LogData.partnerUid, mPartnerUid);
        LogData.customLog(LogData.evaluation_s01_profile_card_view,  bundle, getActivity());
        LogData.setStageEvaluation(LogData.evaluation_s01_profile_card_view, getActivity());
    }

    private void setLogLimitDone(){
        Bundle bundle = new Bundle();
        bundle.putInt(LogData.dia, 0);
        LogData.customLog(LogData.evaluation_s04_day_limit, bundle, getActivity());
        LogData.setStageEvaluation(LogData.evaluation_s04_day_limit, getActivity());
    }

    private void setOnClickListener() {
        //more 클릭시 신고,차단,취소 dialog 이동
        mMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPartnerUser == null){
                    mRefreshLayout.setVisibility(View.VISIBLE);
                    setWaitingList();
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString(Strings.EXTRA_LOCATION, "프로필평가");
                bundle.putString(Strings.partnerUid, mPartnerUid);
                bundle.putSerializable(Strings.partnerUser, mPartnerUser);

                DialogMoreFragment dialog = DialogMoreFragment.getInstance();
                dialog.setArguments(bundle);
                if(getFragmentManager() != null){
                    dialog.show(getFragmentManager(), DialogMoreFragment.TAG_EVENT_DIALOG);
                }

            }
        });

        //새로 고침 버튼
        mRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoaderLayout.setVisibility(View.VISIBLE);
                isRefreshing = true;
                refreshEvaluationList()
                        .addOnCompleteListener(new OnCompleteListener<Boolean>() {
                            @Override
                            public void onComplete(@NonNull Task<Boolean> task) {
                                if (!task.isSuccessful()) {
                                    Exception e = task.getException();
                                    if (e instanceof FirebaseFunctionsException) {
                                        FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                        FirebaseFunctionsException.Code code = ffe.getCode();
                                        Object details = ffe.getDetails();
                                    }
                                }
                                setWaitingList();
                                mLoaderLayout.setVisibility(View.GONE);

                            }
                        });

            }
        });

        mLowScoreButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                scoreToDB(0);
            }
        });

        mHighScoreButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                scoreToDB(1);
            }
        });

    }

    private Task<Boolean> refreshEvaluationList() {
        Map<String, Object> data = new HashMap<>();
        data.put("uid", FirebaseHelper.mUid);

        return mFunctions
                .getHttpsCallable("refreshEvaluationList")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, Boolean>() {
                    @Override
                    public Boolean then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        Map<String,Boolean> result = (Map<String,Boolean>) task.getResult().getData();
                        boolean isSuccessful = false;
                        if(result.get("isSuccessful") != null){
                            isSuccessful = result.get("isSuccessful");
                        }

                        if(!isSuccessful){
                            Toast.makeText(getActivity(), "현재 추천 프로필 카드가 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                        return isSuccessful;
                    }
                });
    }

    private void scoreToDB(double score) {
        if(mPartnerUser == null){
            setWaitingList();
            return;
        }

        mLoaderLayout.setVisibility(View.VISIBLE);
        long nowUnixTime = DateUtil.getUnixTimeLong();
        //본인 DB 업로드
        Map<String, Object> scoreData = new HashMap<>();
        scoreData.put("dateOfScore", DateUtil.getDateMin());
        scoreData.put("score", score);
        scoreData.put(FirebaseHelper.unixTime,nowUnixTime);

        Map<String, Object> scoreData2 = new HashMap<>();
        scoreData2.put("dateOfScore", DateUtil.getDateMin());
        scoreData2.put("score", score);
        scoreData2.put(FirebaseHelper.interacted,true);
        scoreData2.put(FirebaseHelper.unixTime,nowUnixTime);

        WriteBatch batch = FirebaseHelper.db.batch();
        if(isCurrentUserFromScreeningList){
            batch.update( FirebaseHelper.db.collection("Evaluation").document(mPartnerUid),
                    FirebaseHelper.receiveScoreList + "." + FirebaseHelper.mUid, score,
                    FirebaseHelper.scoreCount, FieldValue.increment(1));
        }else if(isCurrentUserFromTierList){
            batch.update( FirebaseHelper.db.collection("Tier").document(mPartnerUid),
                    FirebaseHelper.receiveScoreList + "." + FirebaseHelper.mUid, score,
                    FirebaseHelper.scoreCount, FieldValue.increment(1));
        }
        batch.set(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("Evaluation").document(mPartnerUid), mPartnerUser, SetOptions.merge());
        batch.set(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("Evaluation").document(mPartnerUid), scoreData2, SetOptions.merge());
        batch.set(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("SendScore").document(mPartnerUid), mPartnerUser);
        batch.update(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("SendScore").document(mPartnerUid), scoreData);
        batch.set(FirebaseHelper.db.collection("Users").document(mPartnerUid).collection("ReceiveScore").document(FirebaseHelper.mUid), MyProfile.getUser());
        batch.update(FirebaseHelper.db.collection("Users").document(mPartnerUid).collection("ReceiveScore").document(FirebaseHelper.mUid), scoreData);


        if (score == Interaction.HIGH_SCORE) {
            Fcm fcm = new Fcm(mPartnerUid, MyProfile.getUser().getNickname(), MyProfile.getUser().getNickname() + "님이 높은 평가를 보냈습니다.", MyProfile.getUser(), mPartnerUid, "user", FirebaseHelper.highScore, DateUtil.getDateSec());
            batch.set(FirebaseHelper.db.collection("Users").document(mPartnerUid).collection("Fcm").document(), fcm);

            Map<String, Object> unionData3 = new HashMap<>();
            unionData3.put(FirebaseHelper.permissionList, FieldValue.arrayUnion(mPartnerUid));
            batch.set(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("Permission").document(FirebaseHelper.mUid),
                    unionData3, SetOptions.merge());
        }

        FirebaseHelper.db.collection("Interaction").whereIn(FirebaseHelper.uidList, Arrays.asList(Arrays.asList(FirebaseHelper.mUid, mPartnerUid), Arrays.asList(mPartnerUid, FirebaseHelper.mUid)))
                .orderBy(FirebaseHelper.createTime).limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "get success222222");
                            if(task.getResult() == null || task.getResult().isEmpty()){
                                ArrayList<String> uidList = new ArrayList<>();
                                uidList.add(FirebaseHelper.mUid);
                                uidList.add(mPartnerUid);
                                DocumentReference interDoc = FirebaseHelper.db.collection("Interaction").document();
                                Interaction interaction = new Interaction(interDoc.getId(), FirebaseHelper.mUid, mPartnerUid, uidList, score, Interaction.PRE_SCORE, false, false,
                                        "", "", "", false, "", MyProfile.getUser(), mPartnerUser, nowUnixTime, nowUnixTime);
                                batch.set(interDoc, interaction, SetOptions.merge());
                            } else {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Interaction interaction = document.toObject(Interaction.class);
                                    if(interaction.getUid0().equals(FirebaseHelper.mUid)){
                                        batch.update(FirebaseHelper.db.collection("Interaction").document(interaction.getInteractionId()), FirebaseHelper.scoreUser0to1, score);
                                    } else {
                                        batch.update(FirebaseHelper.db.collection("Interaction").document(interaction.getInteractionId()), FirebaseHelper.scoreUser1to0, score);
                                    }

                                }
                            }

                        } else {
                            Log.d(TAG, "get failed with22222 ", task.getException());
                        }

                        if(!isCurrentUserFromScreeningList && !isCurrentUserFromTierList){
                            savePartnerUidShared(mPartnerUser.getUid());
                        }

                        batch.commit()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        for(int i = 0; i < mScreeningUserList.size(); i++){
                                            if(mPartnerUser.getUid().equals(mScreeningUserList.get(i).getUid())){
                                                mScreeningUserList.remove(i);
                                            }
                                            break;
                                        }

                                        for(int i = 0; i < mTierUserList.size(); i++){
                                            if(mPartnerUser.getUid().equals(mTierUserList.get(i).getUid())){
                                                mTierUserList.remove(i);
                                            }
                                            break;
                                        }

                                        for(int i = 0; i < mMyUserList.size(); i++){
                                            if(mPartnerUser.getUid().equals(mMyUserList.get(i).getUid())){
                                                mMyUserList.remove(i);
                                            }
                                            break;
                                        }

                                        mPartnerUser = null;
                                        setWaitingList();

                                        Bundle bundle = new Bundle();
                                        bundle.putInt(LogData.dia, 0);
                                        bundle.putDouble(LogData.score, score);
                                        bundle.putString(LogData.partnerUid, mPartnerUid);
                                        bundle.putString(LogData.location, LogData.evaluation);
                                        LogData.customLog(LogData.evaluation_scoring,  bundle, getActivity());
                                        if(score == 1){
                                            LogData.customLog(LogData.evaluation_s03_scoring_high,  bundle, getActivity());
                                            LogData.setStageEvaluation(LogData.evaluation_s03_scoring_high, getActivity());
                                        } else if(score == 0){
                                            LogData.customLog(LogData.evaluation_s02_scoring_low,  bundle, getActivity());
                                            LogData.setStageEvaluation(LogData.evaluation_s02_scoring_low, getActivity());
                                        }

                                        //InteractionHistory
                                        WriteBatch batch1 = FirebaseHelper.db.batch();
                                        Map<String, Object> unionData = new HashMap<>();
                                        Map<String, Object> unionData2 = new HashMap<>();
                                        if(score ==1){
                                            unionData.put(FirebaseHelper.sendHighScoreList , FieldValue.arrayUnion(mPartnerUid));
                                            unionData2.put(FirebaseHelper.receiveHighScoreList , FieldValue.arrayUnion(FirebaseHelper.mUid));
                                        } else if (score ==0){
                                            unionData.put(FirebaseHelper.sendLowScoreList , FieldValue.arrayUnion(mPartnerUid));
                                            unionData2.put(FirebaseHelper.receiveLowScoreList , FieldValue.arrayUnion(FirebaseHelper.mUid));
                                        }
                                        batch1.set(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("InteractionHistory").document(FirebaseHelper.mUid), unionData, SetOptions.merge());
                                        batch1.set(FirebaseHelper.db.collection("Users").document(mPartnerUid).collection("InteractionHistory").document(mPartnerUid), unionData2, SetOptions.merge());
                                        batch1.update(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("InteractionHistory").document(FirebaseHelper.mUid), FirebaseHelper.sendScoreList + "." + mPartnerUid, score);
                                        batch1.update( FirebaseHelper.db.collection("Users").document(mPartnerUid).collection("InteractionHistory").document(mPartnerUid), FirebaseHelper.receiveScoreList + "." + FirebaseHelper.mUid, score);
                                        batch1.commit();

                                        Map<String,Object> event = new HashMap<>();
                                        event.put("totalCount", FieldValue.increment(1));
                                        FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("Event").document("EvaluationEvent").set(event, SetOptions.merge());
                                        mLoaderLayout.setVisibility(View.GONE);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error writing document", e);
                                        Toast.makeText(getActivity(), "네트워크가 불안정합니다", Toast.LENGTH_SHORT).show();
                                        mLoaderLayout.setVisibility(View.GONE);
                                    }
                                });
                    }
                });

    }

    private void setCircleAnimIndicator(ViewGroup rootView, ArrayList<String> imageUrlList) {
        CircleAnimIndicator circleAnimIndicator;

        //인디케이터 설정
        circleAnimIndicator = rootView.findViewById(R.id.profile_card_circleAnimIndicator);
        circleAnimIndicator.setItemMargin(15); //원사이의 간격
        circleAnimIndicator.setAnimDuration(300); //애니메이션 속도
        circleAnimIndicator.createDotPanel(imageUrlList.size(), R.drawable.ic_indicator_non, R.drawable.ic_indicator_on); //indicator 생성

        //뷰페이저 넘길시 인디케이터 같이 움직이는 설정
        mOnPageChangeListener = new SquareViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                circleAnimIndicator.selectDot(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        };
    }


    private void setBlockListener(){
        MainActivity activity = (MainActivity) getActivity();
        activity.setBlockEvaluationListener(this);
    }

    @Override
    public void blockEvaluation() {
        FirebaseHelper.blockUser(getActivity(), mPartnerUser, mLoaderLayout, false);
    }


    @Override
    public void refreshData() {
        if(listener1 != null){
            listener1.remove();
        }

        if(listener2 != null){
            listener2.remove();
        }

        if(listener3 != null){
            listener2.remove();
        }

        count = 0;
        loadUser();
    }

    @Override
    public void onPause() {
        super.onPause();

        if(listener1 != null){
            listener1.remove();
        }

        if(listener2 != null){
            listener2.remove();
        }

        if(listener3 != null){
            listener2.remove();
        }
    }
}