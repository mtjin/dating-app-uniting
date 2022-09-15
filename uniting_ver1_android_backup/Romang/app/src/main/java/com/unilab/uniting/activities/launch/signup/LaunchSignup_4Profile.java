package com.unilab.uniting.activities.launch.signup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.unilab.uniting.R;
import com.unilab.uniting.adapter.launch.LaunchSignUpAdapter;
import com.unilab.uniting.adapter.launch.SignUpClickListener;
import com.unilab.uniting.fragments.dialog.DialogOkNoFragment;
import com.unilab.uniting.utils.BasicActivity;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.LogData;
import com.unilab.uniting.utils.MyProfile;
import com.unilab.uniting.utils.RemoteConfig;
import com.unilab.uniting.utils.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LaunchSignup_4Profile extends BasicActivity implements DialogOkNoFragment.DialogSignUpBackListener {

    //xml
    private Button mOkButton;
    private RecyclerView mSignupRecyclerView;
    private TextView mTitleTextView;
    private LinearLayout mBack;
    private ProgressBar mProgressBar;
    private TextView mToolbarTitleTextView;
    private LinearLayout mBottomLinearLayout;
    private TextView mPersonalityTextView;

    //value
    private long backPressedTime = 0;
    final static double PROGRESS = 14.27;
    private LaunchSignUpAdapter mSignUpAdapter;
    private ArrayList<String> titleList = new ArrayList<>(); //"키", "지역", "몸매", "성격", "혈액형", "흡연","음주", "종교"
    private ArrayList<String> currentItemList = new ArrayList<>(); // 위 종류 중 현재 골라야하는 항목
    private ArrayList<String> itemList0 = new ArrayList<>(); // 키
    private ArrayList<String> itemList1 = new ArrayList<>(); // 지역

    private ArrayList<String> itemList2 = new ArrayList<>(); // 성격
    private ArrayList<String> itemList3 = new ArrayList<>(); // 혈액형
    private ArrayList<String> itemList4 = new ArrayList<>(); //흡연
    private ArrayList<String> itemList5 = new ArrayList<>(); //음주
    private ArrayList<String> itemList6 = new ArrayList<>(); // 종교
    private int position0 = -1; // itemList0 (키)에서 몇번째를 체크했는지
    private int position1 = -1;
    private ArrayList<String> checkedPersonalityList = new ArrayList<>(); //성격에서 회원이 체크한 항목
    private int position3 = -1;
    private int position4 = -1;
    private int position5 = -1;
    private int position6 = -1;

    private int checkedPosition = -1;

    private String gender = "";
    private int mProgressCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_signup_all_in_one);

        setLogData();
        init();
        loadShared();
        setView();
        RemoteConfig.setABTest(LaunchSignup_4Profile.this);

        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((checkedPosition == -1 && mProgressCount != 2) || (mProgressCount == 2 && checkedPersonalityList.size() == 0)){
                    switch (mProgressCount) {
                        case 0:
                            Toast.makeText(LaunchSignup_4Profile.this, "키를 골라주세요!", Toast.LENGTH_SHORT).show();
                            break;
                        case 1:
                            Toast.makeText(LaunchSignup_4Profile.this, "지역를 선택해주세요!", Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            Toast.makeText(LaunchSignup_4Profile.this, "성격을 선택해주세요!", Toast.LENGTH_SHORT).show();
                            break;
                        case 3:
                            Toast.makeText(LaunchSignup_4Profile.this, "혈액형 선택해주세요!", Toast.LENGTH_SHORT).show();
                            break;
                        case 4:
                            Toast.makeText(LaunchSignup_4Profile.this, "흡연 여부를 선택해주세요!", Toast.LENGTH_SHORT).show();
                            break;
                        case 5:
                            Toast.makeText(LaunchSignup_4Profile.this, "음주 여부를 선택해주세요!", Toast.LENGTH_SHORT).show();
                            break;
                        case 6:
                            Toast.makeText(LaunchSignup_4Profile.this, "종교를 선택해주세요!", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(LaunchSignup_4Profile.this, "선택지를 골라주세요!", Toast.LENGTH_SHORT).show();
                            break;
                    }

                    return;
                }

                mProgressCount++;
                double progress = mProgressCount * PROGRESS;
                int progressInt = (int) progress;
                mProgressBar.setProgress(progressInt);
                if (mProgressCount < 7) {
                    mTitleTextView.setText(titleList.get(mProgressCount));
                    currentItemList.clear();
                }

                switch (mProgressCount) {
                    case 0:
                        checkedPosition = position0;
                        currentItemList.addAll(itemList0);
                        saveProgressShared(4, mProgressCount);
                        break;
                    case 1:
                        checkedPosition = position1;
                        currentItemList.addAll(itemList1);
                        saveProgressShared(4, mProgressCount);
                        break;
                    case 2:
                        if(checkedPersonalityList.size() > 0){ //성격 한개라도 골랐으면 위의 checkedPosition = -1 검사 통과하도록
                            checkedPosition = 0;
                        }
                        currentItemList.addAll(itemList2);
                        mPersonalityTextView.setVisibility(View.VISIBLE);
                        saveProgressShared(4, mProgressCount);
                        break;
                    case 3:
                        checkedPosition = position3;
                        currentItemList.addAll(itemList3);
                        mPersonalityTextView.setVisibility(View.GONE);
                        saveProgressShared(4, mProgressCount);
                        break;
                    case 4:
                        checkedPosition = position4;
                        currentItemList.addAll(itemList4);
                        saveProgressShared(4, mProgressCount);
                        break;
                    case 5:
                        checkedPosition = position5;
                        currentItemList.addAll(itemList5);
                        saveProgressShared(4, mProgressCount);
                        break;
                    case 6:
                        checkedPosition = position6;
                        currentItemList.addAll(itemList6);
                        saveProgressShared(4, mProgressCount);
                        break;
                    case 7:
                        saveProgressShared(5, 0);
                        checkedPosition = position6;
                        mProgressCount--;
                        startActivity(new Intent(LaunchSignup_4Profile.this, LaunchSignup_15Photo.class));

                        Map<String, String> progressData = new HashMap<>();
                        progressData.put(FirebaseHelper.signUpProgress, Strings.SignUpPrgoress.Photo);
                        WriteBatch batch = FirebaseHelper.db.batch();
                        batch.set(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid), progressData, SetOptions.merge());
                        batch.set(FirebaseHelper.db.collection("AdminUsers").document(FirebaseHelper.mUid), progressData, SetOptions.merge());
                        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Map<String,String> user_properties = new HashMap<>();
                                user_properties.put(LogData.sign_up_progress, Strings.SignUpPrgoress.Photo);
                                LogData.setUserProperties(user_properties, LaunchSignup_4Profile.this);

                            }
                        });

                        break;

                }
                setAdapter();
                mSignUpAdapter.notifyDataSetChanged();
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setLogData(){
        LogData.eventLog(LogData.SignUp4_Profile, this);
        Bundle bundle = new Bundle();
        bundle.putInt(LogData.sign_up_progress, 4);
        LogData.customLog(LogData.SignUp,  bundle, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setView();
    }

    @Override
    public void onBackPressed() {
        if(mProgressCount >= 0 ){
            mProgressCount--;
        }

        if(mProgressCount >= 0){
            double progress = mProgressCount * PROGRESS;
            int progressInt =  (int) progress;
            mProgressBar.setProgress(progressInt);
            mTitleTextView.setText(titleList.get(mProgressCount));
            currentItemList.clear();
        }else {
            Bundle bundle = new Bundle();
            bundle.putString(Strings.title, "이전 단계로 돌아가시겠어요?");
            bundle.putString(Strings.content, "1분이면 가입이 완료됩니다.");
            bundle.putString(Strings.ok, "이전 단계로");
            bundle.putString(Strings.no, "가입 계속하기");
            DialogOkNoFragment dialog = DialogOkNoFragment.getInstance();
            dialog.setArguments(bundle);
            dialog.show(getSupportFragmentManager(), DialogOkNoFragment.TAG_EVENT_DIALOG);
            return;
        }
        switch (mProgressCount){
            case 0:
                currentItemList.addAll(itemList0);
                checkedPosition = position0;
                break;
            case 1:
                currentItemList.addAll(itemList1);
                checkedPosition = position1;
                mPersonalityTextView.setVisibility(View.GONE);
                break;
            case 2:
                if(checkedPersonalityList.size() > 0){ //성격 한개라도 골랐으면 위의 checkedPosition = -1 검사 통과하도록
                    checkedPosition = 0;
                }
                currentItemList.addAll(itemList2);
                mPersonalityTextView.setVisibility(View.VISIBLE);
                break;
            case 3:
                currentItemList.addAll(itemList3);
                checkedPosition = position3;
                break;
            case 4:
                currentItemList.addAll(itemList4);
                checkedPosition = position4;
                break;
            case 5:
                currentItemList.addAll(itemList5);
                checkedPosition = position5;
                break;
            case 6:
                currentItemList.addAll(itemList6);
                checkedPosition = position6;
                break;
        }
        setAdapter();
        mSignUpAdapter.notifyDataSetChanged();
    }

    //어댑터에서 클릭 일어났을 때.
    private SignUpClickListener signUpClickListener = new SignUpClickListener() {
        @Override
        public void listenClick(int position) {
            Log.d("test1123", "콜백 작동" + mProgressCount);
            mProgressCount++;
            double progress = mProgressCount * PROGRESS;
            int progressInt = (int) progress;
            mProgressBar.setProgress(progressInt);
            if (mProgressCount < 7) {
                mTitleTextView.setText(titleList.get(mProgressCount));
                currentItemList.clear();
            }

            SharedPreferences pref = getSharedPreferences(FirebaseHelper.mUid, MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            switch (mProgressCount) {
                case 0:
                    checkedPosition = position0;
                    currentItemList.addAll(itemList0);
                    saveProgressShared(4, mProgressCount);
                    break;
                case 1:
                    position0 = position;
                    editor.putString("height", itemList0.get(position0));
                    editor.putInt("heightId", position0);
                    editor.putInt("locationId", position1);
                    editor.apply();
                    saveProgressShared(4, mProgressCount);
                    checkedPosition = position1;
                    currentItemList.addAll(itemList1);
                    break;
                case 2:
                    position1 = position;
                    editor.putString("location", itemList1.get(position1));
                    editor.putInt("locationId", position1);
                    editor.apply();
                    saveProgressShared(4, mProgressCount);
                    saveProgressShared(4, mProgressCount);
                    //checkedPosition = position3_0;
                    mPersonalityTextView.setVisibility(View.VISIBLE);
                    currentItemList.addAll(itemList2);
                    break;
                case 3:
                    saveProgressShared(4, mProgressCount);
                    break;
                case 4:
                    position3 = position;
                    editor.putString("bloodType", itemList3.get(position3));
                    editor.putInt("bloodTypeId", position3);
                    editor.apply();
                    saveProgressShared(4, mProgressCount);
                    checkedPosition = position4;
                    currentItemList.addAll(itemList4);
                    break;
                case 5:
                    position4 = position;
                    editor.putString("smoking", itemList4.get(position4));
                    editor.putInt("smokingId", position4);
                    editor.apply();
                    saveProgressShared(4, mProgressCount);
                    checkedPosition = position5;
                    currentItemList.addAll(itemList5);
                    break;
                case 6:
                    position5 = position;
                    editor.putString("drinking", itemList5.get(position5));
                    editor.putInt("drinkingId", position5);
                    editor.apply();
                    saveProgressShared(4, mProgressCount);
                    checkedPosition = position6;
                    currentItemList.addAll(itemList6);
                    break;
                case 7:
                    position6 = position;
                    editor.putString("religion", itemList6.get(position6));
                    editor.putInt("religionId", position6);
                    editor.apply();
                    saveProgressShared(5, 0);
                    checkedPosition = position6;
                    mProgressCount--;
                    startActivity(new Intent(LaunchSignup_4Profile.this, LaunchSignup_15Photo.class));

                    //저장한 MyProfile에 저
                    String height = pref.getString(FirebaseHelper.height, "");
                    String religion = pref.getString(FirebaseHelper.religion, "");
                    String location = pref.getString(FirebaseHelper.location, "");
                    //성격 리스트
                    ArrayList<String> mPersonalityList = new ArrayList<>();
                    Set<String> personalitySet = pref.getStringSet(FirebaseHelper.personality, null);
                    if (personalitySet == null) {
                        mPersonalityList = new ArrayList<>(Arrays.asList());
                    } else {
                        mPersonalityList = new ArrayList<String>(personalitySet);
                    }
                    String bloodType = pref.getString(FirebaseHelper.bloodType, "");
                    String drinking = pref.getString(FirebaseHelper.drinking, "");
                    String smoking = pref.getString(FirebaseHelper.smoking, "");

                    MyProfile.getOurInstance().setLocation(location);
                    MyProfile.getOurInstance().setHeight(height);
                    MyProfile.getOurInstance().setPersonality(mPersonalityList);
                    MyProfile.getOurInstance().setBloodType(bloodType);
                    MyProfile.getOurInstance().setSmoking(smoking);
                    MyProfile.getOurInstance().setDrinking(drinking);
                    MyProfile.getOurInstance().setReligion(religion);
                    MyProfile.getOurInstance().setSignUpProgress(Strings.SignUpPrgoress.Photo);

                    Map<String, Object> user = new HashMap<>();
                    user.put(FirebaseHelper.location, location);
                    user.put(FirebaseHelper.height, height);
                    user.put(FirebaseHelper.personality, mPersonalityList);
                    user.put(FirebaseHelper.bloodType, bloodType);
                    user.put(FirebaseHelper.smoking, smoking);
                    user.put(FirebaseHelper.drinking, drinking);
                    user.put(FirebaseHelper.religion, religion);
                    user.put(FirebaseHelper.signUpProgress, Strings.SignUpPrgoress.Photo);

                    Map<String, String> progressData = new HashMap<>();
                    progressData.put(FirebaseHelper.signUpProgress, Strings.SignUpPrgoress.Photo);
                    WriteBatch batch = FirebaseHelper.db.batch();
                    batch.set(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid), user, SetOptions.merge());
                    batch.set(FirebaseHelper.db.collection("AdminUsers").document(FirebaseHelper.mUid), progressData, SetOptions.merge());
                    batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Map<String, String> user_properties = new HashMap<>();
                            user_properties.put(LogData.sign_up_progress, Strings.SignUpPrgoress.Photo);
                            LogData.setUserProperties(user_properties, LaunchSignup_4Profile.this);

                        }
                    });

                    double progress2 = mProgressCount * PROGRESS;
                    int progressInt2 = (int) progress2;
                    mProgressBar.setProgress(progressInt2);
                    break;
            }
            setAdapter();
            mSignUpAdapter.notifyDataSetChanged();
        }

        @Override
        public void listenPersonalityClick(ArrayList<String> checkedItemList) {
            SharedPreferences pref = getSharedPreferences(FirebaseHelper.mUid, MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            Set<String> personalitySet = new HashSet<String>();
            personalitySet.addAll(checkedItemList);
            editor.putStringSet("personality", personalitySet);
            editor.apply();

            checkedPersonalityList = checkedItemList;
            if (checkedItemList.size() == 3) {
                mProgressCount++;
                double progress = mProgressCount * PROGRESS;
                int progressInt = (int) progress;
                mProgressBar.setProgress(progressInt);
                mTitleTextView.setText(titleList.get(mProgressCount));
                mPersonalityTextView.setVisibility(View.GONE);

                currentItemList.clear();
                currentItemList.addAll(itemList3);
                checkedPosition = position3;

                setAdapter();
                mSignUpAdapter.notifyDataSetChanged();
            }
        }
    };

    private void init(){
        mBack = findViewById(R.id.toolbar_back);
        mTitleTextView = findViewById(R.id.signup_all_in_one_tv_title);
        mSignupRecyclerView = findViewById(R.id.signup_all_in_one_rv_checklist);
        mOkButton = findViewById(R.id.signup_bloodtype11_btn_ok);
        mProgressBar =findViewById(R.id.signup_all_in_one_pb);
        mBottomLinearLayout = findViewById(R.id.signup_all_in_one_linear);
        mPersonalityTextView = findViewById(R.id.signup_all_in_one_tv_manual);
        mToolbarTitleTextView = findViewById(R.id.toolbar_title);
        mBottomLinearLayout.setClickable(true);

        Intent intent = getIntent();
        mProgressCount = intent.getIntExtra("progressCount",0);

        SharedPreferences pref = getSharedPreferences(FirebaseHelper.mUid , MODE_PRIVATE);
        gender = pref.getString("gender", "");

        titleList = new ArrayList<String>(Arrays.asList("키", "지역", "성격", "혈액형", "흡연","음주", "종교"));
        itemList0.add("140cm 이하");
        for(int height = 140; height < 199; height++){
            itemList0.add(height + "cm");
        }
        itemList0.add("200cm 이상");
        itemList1 = new ArrayList<String>(Arrays.asList("서울", "인천", "경기", "대전", "세종", "부산", "대구", "광주", "울산", "강원", "충북", "충남", "전북", "전남", "경북", "경남", "제주", "기타"));
        itemList2 = new ArrayList<String>(Arrays.asList("열정적인", "지적인", "차분한", "귀여운","개성있는", "상냥한", "섬세한", "강인한", "감성적인", "낙천적인", "유머있는", "외향적인", "내향적인", "듬직한"));
        itemList3 = new ArrayList<String>(Arrays.asList("A형", "B형", "AB형", "O형"));
        itemList4 = new ArrayList<String>(Arrays.asList("비흡연", "흡연"));
        itemList5 = new ArrayList<String>(Arrays.asList("전혀 마시지 못함", "어쩔 수 없을 때만", "가끔 마심", "어느 정도 즐김", "술자리를 즐김"));
        itemList6 = new ArrayList<String>(Arrays.asList("무교", "기독교", "천주교", "불교", "원불교", "기타"));
    }


    /*쉐어드값 불러와서 기존에 입력했던 정보들 restore해줌*/
    private void loadShared(){
        SharedPreferences pref = getSharedPreferences(FirebaseHelper.mUid , MODE_PRIVATE);
        position0 = pref.getInt("heightId",-1);
        position1 = pref.getInt("locationId",-1);
        Set<String> personalitySet = pref.getStringSet("personality", null);
        if(personalitySet == null){
            checkedPersonalityList = new ArrayList<>(Arrays.asList());
        }else{
            checkedPersonalityList = new ArrayList<String>(personalitySet);
        }
        position3 = pref.getInt("bloodTypeId",-1);
        position4 = pref.getInt("smokingId",-1);
        position5 = pref.getInt("drinkingId",-1);
        position6 = pref.getInt("religionId",-1);
        checkedPosition = position0;
    }

    private void setView(){
        double progress = mProgressCount * PROGRESS;
        int progressInt = (int) progress;
        mProgressBar.setProgress(progressInt);
        if (mProgressCount < 7) {
            mTitleTextView.setText(titleList.get(mProgressCount));
            currentItemList.clear();
        }

        switch (mProgressCount) {
            case 0:
                checkedPosition = position0;
                currentItemList.addAll(itemList0);
                break;
            case 1:
                checkedPosition = position1;
                currentItemList.addAll(itemList1);
                break;
            case 2:
                if(checkedPersonalityList.size() > 0){ //성격 한개라도 골랐으면 위의 checkedPosition = -1 검사 통과하도록
                    checkedPosition = 0;
                }
                currentItemList.addAll(itemList2);
                mPersonalityTextView.setVisibility(View.VISIBLE);
                break;
            case 3:
                currentItemList.addAll(itemList3);
                mPersonalityTextView.setVisibility(View.GONE);
                break;
            case 4:
                checkedPosition = position4;
                currentItemList.addAll(itemList4);
                break;
            case 5:
                checkedPosition = position5;
                currentItemList.addAll(itemList5);
                break;
            case 6:
                checkedPosition = position6;
                currentItemList.addAll(itemList6);
                break;
        }
        setAdapter();
        mSignUpAdapter.notifyDataSetChanged();
    }

    private void setAdapter(){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mSignupRecyclerView.setLayoutManager(layoutManager);
        mSignUpAdapter = new LaunchSignUpAdapter(this, currentItemList, signUpClickListener, checkedPosition, checkedPersonalityList);
        mSignupRecyclerView.setAdapter(mSignUpAdapter);
    }

    /*쉐어드에 가입 단계 저장*/
    private void saveProgressShared(int progress1, int progress2) {
        SharedPreferences pref = getSharedPreferences(FirebaseHelper.mUid , MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("progress1", progress1);
        editor.putInt("progress2", progress2);
        editor.apply();
    }

    @Override
    public void back() {
        super.onBackPressed();
    }
}

