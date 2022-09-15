package com.unilab.uniting.utils;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.unilab.uniting.R;

import java.util.HashMap;

public class RemoteConfig {
    private HashMap<String,String> abTest;

    private static final RemoteConfig sharedManager = new RemoteConfig();

    public static String MIN_VERSION = "min_version_android";
    public static String standard_age = "standard_age";
    public static String guide_post = "guide_post";

    public static int standardAge = 24;
    public static String guidePost = "";
    public static int evaluation_screening_limit = 10;
    public static int evaluation_day_limit = 3;
    public static String next_experiment = "블라인드 매칭";
    public static double male_tier_revision_power = 1.0;
    public static double female_tier_revision_power = 0.6;
    public static int tier_count = 10;
    public static String withdraw_event = "on";
    public static String sign_up_value = "";
    public static String withdraw_btn = "on";



    public class Store {
        public final static String isEventOn = "is_event_on";
        public final static String eventPercent = "eventPercent";
        public final static String eventDia50 = "eventDia50";
        public final static String eventDia100 = "eventDia100";
        public final static String eventDia300 = "eventDia300";
        public final static String eventDia500 = "eventDia500";
        public final static String eventDia1000 = "eventDia1000";

    }



    public static void setABTest(Activity activity) {
        FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build();
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
        firebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(activity, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            Log.e("remoteConfig", "Fetch Succeeded");
                        } else {
                            Log.e("remoteConfig", "Fetch Failed");
                        }
                        //ABTest 저장
                        String ABTest_variant = firebaseRemoteConfig.getString(RemoteConfig.ABCertification.variant_name);
                        Log.e("remoteConfig222", ABTest_variant);
                        HashMap<String, String> ABtest = new HashMap<>();
                        ABtest.put(RemoteConfig.ABCertification.variant_name, ABTest_variant);
                        RemoteConfig.getSharedManager().setAbTest(ABtest);
                    }
                });
    }

    public class ABCertification { //위에 저장한 abTest map에 결과를 저장한다. 이거 고치기.
        public final static String title = "Certification";
        public final static String variant_name = "force_certification";
        public final static String variant_control = "control";
        public final static String variant_force = "A";
    }

    public static class ABPreview {
        public final static String variant_name = "preview";
        public static String variant_test = "control"; //AB test 디폴트는 control

        public final static String variant_control = "control";
        public final static String variant_preview_off = "A";
        public final static String variant_B = "B";
        public final static String variant_C = "C";
    }

    public HashMap<String, String> getAbTest() {
        return abTest;
    }

    public void setAbTest(HashMap<String, String> abTest) {
        this.abTest = abTest;
    }

    public static RemoteConfig getSharedManager() {
        return sharedManager;
    }
}

