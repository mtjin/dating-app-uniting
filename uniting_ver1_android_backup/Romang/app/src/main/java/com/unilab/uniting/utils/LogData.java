package com.unilab.uniting.utils;

import android.content.Context;
import android.os.Bundle;

import com.appsflyer.AppsFlyerLib;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.HashMap;
import java.util.Map;

public class LogData {

    public static void eventLog(String event,  Context context){
        AppEventsLogger logger = AppEventsLogger.newLogger(context);
        logger.logEvent(event);

        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        firebaseAnalytics.logEvent(event, null);
        AppsFlyerLib.getInstance().trackEvent(context, event, null);
    }

    public static void customLog(String event, Bundle bundle, Context context){
        AppEventsLogger logger = AppEventsLogger.newLogger(context);
        logger.logEvent(event);

        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        firebaseAnalytics.logEvent(event, bundle);

        Map<String,Object> eventValues = new HashMap<>();
        for(String key : bundle.keySet() ){
            eventValues.put(key, bundle.get(key));
        }
        AppsFlyerLib.getInstance().trackEvent(context, event, eventValues);
    }

    public static void setUserProperties(Map<String, String> parameters, Context context){
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle user_props = new Bundle();
        for( String key : parameters.keySet() ){
            if(parameters.get(key) != null){
                firebaseAnalytics.setUserProperty(key, parameters.get(key));
                user_props.putString(key, parameters.get(key));
            }
        }
        AppEventsLogger.updateUserProperties(user_props, response -> {});

    }

    public static void setStageTodayIntro(String stage, Context context){
        if(MyProfile.getUser().getStageTodayIntro() == null || MyProfile.getUser().getStageTodayIntro().compareTo(stage) < 0){
            Map<String, String> userProps = new HashMap<>();
            userProps.put(LogData.stageTodayIntro, stage);
            LogData.setUserProperties(userProps, context);
            if(FirebaseHelper.mUid == null || FirebaseHelper.mUid.equals("")){
                LaunchUtil.checkAuth(context);
            }
            FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).update(LogData.stageTodayIntro, stage);
            MyProfile.getOurInstance().setStageTodayIntro(stage);
        }
    }

    public static void setStageEvaluation(String stage, Context context){
        if(MyProfile.getUser().getStageEvaluation() == null || MyProfile.getUser().getStageEvaluation().compareTo(stage) < 0){
            Map<String, String> userProps = new HashMap<>();
            userProps.put(LogData.stageEvaluation, stage);
            LogData.setUserProperties(userProps, context);
            if(FirebaseHelper.mUid == null || FirebaseHelper.mUid.equals("")){
                LaunchUtil.checkAuth(context);
            }
            FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).update(LogData.stageEvaluation, stage);
            MyProfile.getOurInstance().setStageEvaluation(stage);
        }
    }

    public static void setStageMeeting(String stage, Context context){
        if(MyProfile.getUser().getStageMeeting() == null || MyProfile.getUser().getStageMeeting().compareTo(stage) < 0){
            Map<String, String> userProps = new HashMap<>();
            userProps.put(LogData.stageMeeting, stage);
            LogData.setUserProperties(userProps, context);
            if(FirebaseHelper.mUid == null || FirebaseHelper.mUid.equals("")){
                LaunchUtil.checkAuth(context);
            }
            FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).update(LogData.stageMeeting, stage);
            MyProfile.getOurInstance().setStageMeeting(stage);
        }
    }

    public static void setStageMeetingApplicant(String stage, Context context){
        if(MyProfile.getUser().getStageMeetingApplicant() == null || MyProfile.getUser().getStageMeetingApplicant().compareTo(stage) < 0){
            Map<String, String> userProps = new HashMap<>();
            userProps.put(LogData.stageMeetingApplicant, stage);
            LogData.setUserProperties(userProps, context);
            if(FirebaseHelper.mUid == null || FirebaseHelper.mUid.equals("")){
                LaunchUtil.checkAuth(context);
            }
            FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).update(LogData.stageMeetingApplicant, stage);
            MyProfile.getOurInstance().setStageMeetingApplicant(stage);
        }
    }

    public static void setStageMeetingHost(String stage, Context context){
        if(MyProfile.getUser().getStageMeetingHost() == null ||  MyProfile.getUser().getStageMeetingHost().compareTo(stage) < 0){
            Map<String, String> userProps = new HashMap<>();
            userProps.put(LogData.stageMeetingHost, stage);
            LogData.setUserProperties(userProps, context);
            if(FirebaseHelper.mUid == null || FirebaseHelper.mUid.equals("")){
                LaunchUtil.checkAuth(context);
            }
            FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).update(LogData.stageMeetingHost, stage);
            MyProfile.getOurInstance().setStageMeetingHost(stage);
        }
    }

    public static void setStageCommunity(String stage, Context context){
        if(MyProfile.getUser().getStageCommunity() == null || MyProfile.getUser().getStageCommunity().compareTo(stage) < 0){
            Map<String, String> userProps = new HashMap<>();
            userProps.put(LogData.stageCommunity, stage);
            LogData.setUserProperties(userProps, context);
            if(FirebaseHelper.mUid == null || FirebaseHelper.mUid.equals("")){
                LaunchUtil.checkAuth(context);
            }
            FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).update(LogData.stageCommunity, stage);
            MyProfile.getOurInstance().setStageCommunity(stage);
        }
    }

    public static void setStageCloseUser(String stage, Context context){
        if(MyProfile.getUser().getStageCloseUser() == null || MyProfile.getUser().getStageCloseUser().compareTo(stage) < 0){
            Map<String, String> userProps = new HashMap<>();
            userProps.put(LogData.stageCloseUser, stage);
            LogData.setUserProperties(userProps, context);
            if(FirebaseHelper.mUid == null || FirebaseHelper.mUid.equals("")){
                LaunchUtil.checkAuth(context);
            }
            FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).update(LogData.stageCloseUser, stage);
            MyProfile.getOurInstance().setStageCloseUser(stage);
        }
    }


    public static void setStageTutorial(int stage, Context context){
        if(MyProfile.getUser().getStageTutorial() == null || MyProfile.getUser().getStageTutorial().compareTo(LogData.tutorial_s0 + stage) < 0){
            Map<String, String> userProps = new HashMap<>();
            userProps.put(LogData.stageTutorial, LogData.tutorial_s0 + stage);
            LogData.setUserProperties(userProps, context);
            if(FirebaseHelper.mUid == null || FirebaseHelper.mUid.equals("")){
                LaunchUtil.checkAuth(context);
            }
            FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).update(LogData.stageTutorial, LogData.tutorial_s0 + stage);
            MyProfile.getOurInstance().setStageTutorial(LogData.tutorial_s0 + stage);
        }
    }


    public static final String Splash = "Splash";
    public static final String Preview = "Preview";
    public static final String LoginView = "LoginView";

    public static final String SignUp = "SignUp";
    public static final String SignUp0 = "SignUp0";
    public static final String SignUp1_Account = "SignUp1_Account";
    public static final String SignUp2_PreCerti = "SignUp2_PreCerti";
    public static final String SignUp3_Certi = "SignUp3_Certi";
    public static final String SignUp4_Profile = "SignUp4_Profile";
    public static final String SignUp5_Photo = "SignUp5_Photo";
    public static final String SignUp6_Univ = "SignUp6_Univ";
    public static final String SignUp7_Screening = "SignUp7_Screening";
    public static final String SignUp8_Edit = "SignUp8_Edit";
    public static final String Main = "Main";


    public static final String Withdraw_SignUp2 = "Withdraw_SignUp2";
    public static final String Withdraw_SignUp7 = "Withdraw_SignUp7";

    public static final String Dormant = "Dormant";
    public static final String Withdraw = "Withdraw";

    public static final String SignUpAge = "SignUpAge";
    public static final String MainNotice = "MainNotice";
    public static final String MainFirst = "MainFirst";


    //user 속성
    public static final String sign_up_progress = "sign_up_progress";
    public static final String membership = "membership";
    public static final String age_custom = "age_custom";
    public static final String birth_year = "birth_year";
    public static final String gender_custom = "gender_custom";
    public static final String male = "male";
    public static final String female = "female";
    public static final String passTime = "passTime";
    public static final String recentMainTime = "recentMainTime";

    public static final String purchaseDia = "purchaseDia";
    public static final String price = "price";
    public static final String usedDia = "usedDia";
    public static final String errorCode = "errorCode";
    public static final String purchaseError = "purchaseError";

    public static final String stageTodayIntro = "stageTodayIntro";
    public static final String stageCloseUser = "stageCloseUser";
    public static final String stageEvaluation = "stageEvaluation";
    public static final String stageMeeting = "stageMeeting";
    public static final String stageMeetingApplicant = "stageMeetingApplicant";
    public static final String stageMeetingHost = "stageMeetingHost";
    public static final String stageCommunity = "stageCommunity";
    public static final String stageTutorial = "stageTutorial";
    public static final String stageGuide = "stageGuide";
    public static final String stageGuide2 = "stageGuide2";

    public static final String tier_s00 = "tier_s00";
    public static final String tier_s01 = "tier_s01";
    public static final String tier_s02 = "tier_s02";

    public static final String guide2_s00 = "guide2_s00";
    public static final String guide2_s00_no_meeting = "guide2_s00_no_meeting";
    public static final String guide2_s01_meeting = "guide2_s01_meeting";
    public static final String guide2_s99_complete = "guide2_s99_complete";

    public static final String guide_s00 = "guide_s00";
    public static final String guide_s01_start = "guide_s01_start";
    public static final String guide_s02_profile_activity = "guide_s02_profile_activity";
    public static final String guide_s03_post = "guide_s03_post";
    public static final String guide_s04_post_activity = "guide_s04_post_activity";
    public static final String guide_s05_meeting = "guide_s05_meeting";
    public static final String guide_s06_complete = "guide_s06_complete";


    public static final String ti_s00 = "ti_s00";
    public static final String ti_s01_profile_card_pre_open = "ti_s01_profile_card_pre_open";
    public static final String ti_s02_profile_card_open = "ti_s02_profile_card_open";
    public static final String ti_s03_profile_card_view = "ti_s03_profile_card_view";
    public static final String ti_scoring = "ti_scoring";
    public static final String ti_s04_scoring_low = "ti_s04_scoring_low";
    public static final String ti_s05_scoring_high = "ti_s05_scoring_high";
    public static final String ti_s06_pre_like = "ti_s06_pre_like";
    public static final String ti_s07_like = "ti_s07_like";
    public static final String ti_s08_like_ok = "ti_s08_like_ok";
    public static final String ti_s09_pre_chat_start = "ti_s09_pre_chat_start";
    public static final String ti_s10_chat_start = "ti_s10_chat_start";
    public static final String ti_s11_chat_send_message = "ti_s11_chat_send_message";
    public static final String ti_s12_chat_receive_message = "ti_s12_chat_receive_message";
    public static final String ti_s13_pre_chat_voice_talk = "ti_s13_pre_chat_voice_talk";
    public static final String ti_s14_chat_voice_talk = "ti_s14_chat_voice_talk";


    public static final String evaluation_s00 = "evaluation_s00";
    public static final String evaluation_s01_profile_card_view = "evaluation_s01_profile_card_view";
    public static final String evaluation_scoring = "evaluation_scoring";
    public static final String evaluation_s02_scoring_low = "evaluation_s02_scoring_low";
    public static final String evaluation_s03_scoring_high = "evaluation_s03_scoring_high";
    public static final String evaluation_s04_day_limit = "evaluation_s04_day_limit";


    public static final String meeting_s00 = "meeting_s00";
    public static final String meeting_s01_post = "meeting_s01_post";
    public static final String meeting_s02_card_view = "meeting_s02_card_view";
    public static final String meeting_s03_step1_apply = "meeting_s03_step1_apply";
    public static final String meeting_s04_step1_profile_card_view = "meeting_s04_step1_profile_card_view";
    public static final String meeting_step1_result = "meeting_step1_result";
    public static final String meeting_s05_step1_result_fail = "meeting_s05_step1_result_fail";
    public static final String meeting_s06_step1_result_pass = "meeting_s06_step1_result_pass";
    public static final String meeting_s07_step2_profile_card_view = "meeting_s07_step2_profile_card_view";
    public static final String meeting_s08_step2_pre_apply = "meeting_s08_step2_pre_apply";
    public static final String meeting_s09_step2_apply = "meeting_s09_step2_apply";
    public static final String meeting_step2_result = "meeting_step2_result";
    public static final String meeting_s10_step2_result_fail = "meeting_s10_step2_result_fail";
    public static final String meeting_s11_step2_result_pass = "meeting_s11_step2_result_pass";

    public static final String meeting_delete = "meeting_delete";


    public static final String community_s00 = "community_s00";
    public static final String community_s01_post_view = "community_s01_post_view";
    public static final String community_s02_comment = "community_s02_comment";
    public static final String community_s03_post = "community_s03_post";
    public static final String community_post_like = "community_post_like";
    public static final String community_comment_like = "community_comment_like";
    public static final String community_post_delete = "community_post_delete";


    public static final String close_user_s00 = "close_user_s00";
    public static final String close_user_s01_pre_intro = "close_user_s01_pre_intro";
    public static final String close_user_s02_intro = "close_user_s02_intro";

    public static final String tutorial_s0 = "tutorial_s0";

    public static final String block = "block";

    //params
    public static final String uid = "uid";
    public static final String partnerUid = "partnerUid";
    public static final String facebookUid = "facebookUid";
    public static final String appleUid = "appleUid";
    public static final String apple = "apple";
    public static final String facebook = "facebook";
    public static final String email = "email";
    public static final String meetingId = "meetingId";
    public static final String postId = "postId";
    public static final String commentId = "commentId";
    public static final String roomId = "roomId";
    public static final String dia = "dia";
    public static final String free = "free";
    public static final String paid = "paid";
    public static final String location = "location";
    public static final String score = "score";
    public static final String evaluation = "evaluation";
    public static final String todayIntro = "todayIntro";

    public static final String SCREENING = "1";
    public static final String PASS = "2";
    public static final String FAIL = "3";

    public static final String result = "result";
    public static final String ok = "ok";
    public static final String no = "no";


}
