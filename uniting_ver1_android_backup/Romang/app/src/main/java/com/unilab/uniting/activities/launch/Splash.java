package com.unilab.uniting.activities.launch;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.unilab.uniting.BuildConfig;
import com.unilab.uniting.GeoUtil.GeoUtil;
import com.unilab.uniting.R;
import com.unilab.uniting.fragments.dialog.DialogDormantFragment;
import com.unilab.uniting.fragments.launch.DialogVersionFragment;
import com.unilab.uniting.model.User;
import com.unilab.uniting.utils.DateUtil;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.LaunchBasicActivity;
import com.unilab.uniting.utils.LaunchUtil;
import com.unilab.uniting.utils.LogData;
import com.unilab.uniting.utils.MyProfile;
import com.unilab.uniting.utils.RemoteConfig;

import java.util.HashMap;
import java.util.Map;


public class Splash extends LaunchBasicActivity implements  DialogDormantFragment.DormantCheckListener {

    final static String TAG = "splashTAG";
    //파이어베이스
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    //DB에서 받아와야할 변수값
    private String mMembership = "-1";
    private String mSignUpProgress = "-1";
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        FacebookSdk.fullyInitialize();
        AppEventsLogger.activateApp(getApplication());

        LogData.eventLog(LogData.Splash, this);


    }

    @Override
    protected void onStart() {
        super.onStart();
        checkVersion();
    }

    private void checkVersion() {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(1)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            boolean updated = task.getResult();
                            Log.d(TAG, "Config params updated: " + updated);


                            RemoteConfig.guidePost = mFirebaseRemoteConfig.getString("guide_post");
                            RemoteConfig.next_experiment = mFirebaseRemoteConfig.getString("next_experiment");
                            RemoteConfig.withdraw_event = mFirebaseRemoteConfig.getString("withdraw_event");
                            RemoteConfig.sign_up_value = mFirebaseRemoteConfig.getString("sign_up_value");
                            RemoteConfig.withdraw_btn = mFirebaseRemoteConfig.getString("withdraw_btn");

                            RemoteConfig.ABPreview.variant_test = mFirebaseRemoteConfig.getString(RemoteConfig.ABPreview.variant_name);

                            try {
                                RemoteConfig.standardAge = Integer.parseInt(mFirebaseRemoteConfig.getString(RemoteConfig.standard_age));
                            } catch (NumberFormatException e) {
                                RemoteConfig.standardAge = 24;
                            }

                            try {
                                RemoteConfig.evaluation_screening_limit = Integer.parseInt( mFirebaseRemoteConfig.getString("evaluation_screening_limit"));
                            } catch (NumberFormatException e) {
                                RemoteConfig.evaluation_screening_limit = 10;
                            }

                            try {
                                RemoteConfig.evaluation_day_limit = Integer.parseInt( mFirebaseRemoteConfig.getString("evaluation_day_limit"));
                            } catch (NumberFormatException e) {
                                RemoteConfig.evaluation_day_limit = 3;
                            }

                            try {
                                RemoteConfig.male_tier_revision_power = Double.parseDouble( mFirebaseRemoteConfig.getString("male_tier_revision_power"));
                            } catch (NumberFormatException e) {
                                RemoteConfig.male_tier_revision_power = 1.0;
                            }

                            try {
                                RemoteConfig.female_tier_revision_power = Double.parseDouble( mFirebaseRemoteConfig.getString("female_tier_revision_power"));
                            } catch (NumberFormatException e) {
                                RemoteConfig.female_tier_revision_power = 0.6;
                            }


                            try {
                                RemoteConfig.tier_count = Integer.parseInt( mFirebaseRemoteConfig.getString("tier_count"));
                            } catch (NumberFormatException e) {
                                RemoteConfig.tier_count = 10;
                            }



                            String minVersion = mFirebaseRemoteConfig.getString(RemoteConfig.MIN_VERSION);

                            int currentVersionCode = BuildConfig.VERSION_CODE;
                            int minVersionCode;
                            try {
                                minVersionCode = Integer.parseInt(minVersion);
                            } catch (NumberFormatException e) {
                                minVersionCode = currentVersionCode;
                            }

                            if (currentVersionCode < minVersionCode) {
                                Handler delayHandler = new Handler();
                                delayHandler.postDelayed(new Runnable(){
                                    @Override
                                    public void run() {
                                        DialogVersionFragment dialog = DialogVersionFragment.getInstance();
                                        dialog.show(getSupportFragmentManager(), DialogVersionFragment.TAG_MEETING_DIALOG2);
                                    }
                                }, 200);
                            } else {
                                checkCurrentUser();
                            }
                        } else {
                            checkCurrentUser();
                        }
                    }
                });
    }

    private void checkCurrentUser(){
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if(user == null){
            Handler delayHandler = new Handler();
            delayHandler.postDelayed(new Runnable(){
                @Override
                public void run() {
                    LaunchUtil.startSignUpActivity(Splash.this);
                    Log.d(TAG, "Usernull");
                }
            }, 200);
        }else{
            FirebaseHelper.init(mAuth, this);
            getMembership();
        }
    }


    private void getMembership(){
        FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                //멤버쉽, 프로그레스 초기화
                mMembership = "-1";
                mSignUpProgress = "-1";
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        boolean isLocationPermitted = false;
                        if (!GeoUtil.canGetLocation(Splash.this) || (ActivityCompat.checkSelfPermission(Splash.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(Splash.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                            isLocationPermitted = false;
                        } else {
                            isLocationPermitted = true;
                        }

                        boolean isAlarmPermitted = NotificationManagerCompat.from(Splash.this).areNotificationsEnabled();

                        FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).update(
                                FirebaseHelper.activationTime, DateUtil.getUnixTimeLong(),
                                FirebaseHelper.activationHour, DateUtil.getUnixTimeByHour(),
                                FirebaseHelper.geoPermitted, GeoUtil.getLocationPermission(Splash.this),
                                FirebaseHelper.device, FirebaseHelper.android,
                                FirebaseHelper.appsflyerUid, FirebaseHelper.mAppsflyerUid,
                                FirebaseHelper.activationCount, FieldValue.increment(1),
                                FirebaseHelper.appPushOn, isAlarmPermitted,
                                FirebaseHelper.geoPermitted, isLocationPermitted);

                        mUser = document.toObject(User.class);
                        MyProfile.init(mUser); //유저정보 디비접근없이 쉽게 접근할 수 있게 싱글톤 프로필객체에 유저정보 저장
                        LaunchUtil.sendPushTokenToDB();

                        if (mUser.getMembership() != null) {
                            mMembership = mUser.getMembership();
                        }
                        if (mUser.getSignUpProgress() != null) {
                            mSignUpProgress = mUser.getSignUpProgress();
                        }

                        String gender_custom = LogData.male;
                        if(MyProfile.getUser().getGender().equals("여자")){
                            gender_custom = LogData.female;
                        }
                        Map<String,String> userProps = new HashMap<>();
                        userProps.put(LogData.membership, mMembership);
                        userProps.put(LogData.sign_up_progress, mSignUpProgress);
                        userProps.put(LogData.gender_custom, gender_custom);
                        userProps.put(FirebaseHelper.averageOfReceiveScore, MyProfile.getUser().getAverageOfReceiveScore() + "");
                        userProps.put(FirebaseHelper.tierPercent, MyProfile.getUser().getTierPercent() + "");
                        userProps.put(FirebaseHelper.introMannerScore, MyProfile.getUser().getIntroMannerScore() + "");
                        LogData.setUserProperties(userProps, Splash.this);

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }

                LaunchUtil.startMembershipActivity(Splash.this, mMembership, mSignUpProgress, true);
            }
        });
    }


    @Override
    public void onBackPressed(){
    }

    @Override
    public void checkDormant(boolean isActivated) {
        LaunchUtil.processDormant(isActivated, Splash.this, null, true);
    }
}

