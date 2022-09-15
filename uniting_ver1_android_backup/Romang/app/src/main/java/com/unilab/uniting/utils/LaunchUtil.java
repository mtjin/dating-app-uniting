package com.unilab.uniting.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.unilab.uniting.activities.MainActivity;
import com.unilab.uniting.activities.launch.Splash;
import com.unilab.uniting.activities.launch.login.LaunchLoginActivity;
import com.unilab.uniting.activities.launch.signup.LaunchSignup_0;
import com.unilab.uniting.activities.launch.signup.LaunchSignup_15Photo;
import com.unilab.uniting.activities.launch.signup.LaunchSignup_16UniversityCertification;
import com.unilab.uniting.activities.launch.signup.LaunchSignup_17Screening;
import com.unilab.uniting.activities.launch.signup.LaunchSignup_2PreCertification;
import com.unilab.uniting.activities.launch.signup.LaunchSignup_3Certification;
import com.unilab.uniting.activities.launch.signup.LaunchSignup_4Profile;
import com.unilab.uniting.fragments.dialog.DialogDormantFragment;
import com.unilab.uniting.fragments.dialog.DialogOk2Fragment;
import com.unilab.uniting.model.User;

import java.util.HashMap;
import java.util.Map;

public class LaunchUtil  extends BasicActivity {

    public static void checkCurrentUser(Activity activity){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if(user == null){
            LaunchUtil.startSignUpActivity(activity);
        }else{
            FirebaseHelper.init(auth,activity);
        }
    }

    public static void startSignUpActivity(Activity activity){
        Intent intent = new Intent(activity, LaunchSignup_0.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.finish();

    }

    public static void startLoginActivity(Activity activity){
        Intent intent1 = new Intent(activity, LaunchSignup_0.class);
        Intent intent2 = new Intent(activity, LaunchLoginActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        Intent[] intents = new Intent[]{intent1, intent2};
        activity.startActivities(intents);
        activity.finish();

    }

    public static void startMembershipActivity(AppCompatActivity activity, String membership, String signUpProgress, boolean isSplash) {
        Map<String, String> user_properties = new HashMap<>();
        user_properties.put(LogData.membership, membership);
        user_properties.put(LogData.sign_up_progress, signUpProgress);
        LogData.setUserProperties(user_properties, activity);

        switch (membership) {
            case SignUp: //가입 중
            case Screening: //심사 중
            case Scoring: //심사 중
            case Fail: //불합격
                SharedPreferences pref = activity.getSharedPreferences(FirebaseHelper.mUid, MODE_PRIVATE);
                int progress2 = pref.getInt("progress2", 0);
                startSignUpProgress(activity, signUpProgress, progress2, isSplash);
                break;
            case Main: // 합격
            case Apple: // 합격
            case Manager: // 합격
            case Admin: // 합격
                Intent intent2 = new Intent(activity, MainActivity.class);
                activity.startActivity(intent2);
                activity.finish();
                break;
            case Dormant: //휴면
                DialogDormantFragment dialog = DialogDormantFragment.getInstance();
                dialog.show(activity.getSupportFragmentManager(), DialogDormantFragment.TAG_EVENT_DIALOG);
                break;
            case Withdraw: //탈퇴
            {
                FirebaseHelper.auth.signOut();
                if (isSplash) {
                    startSignUpActivity(activity);
                    return;
                }

                Bundle bundle = new Bundle();
                bundle.putString(Strings.EXTRA_CONTENT, "사용할 수 없는 계정입니다.");
                DialogOk2Fragment dialogOk2Fragment = DialogOk2Fragment.getInstance();
                dialogOk2Fragment.setArguments(bundle);
                dialogOk2Fragment.show(activity.getSupportFragmentManager(), DialogOk2Fragment.TAG_MEETING_DIALOG2);
            }
            break;
            case Ban:
                FirebaseHelper.auth.signOut();
                if (isSplash) {
                    startSignUpActivity(activity);
                    return;
                }

                Bundle bundle = new Bundle();
                bundle.putString(Strings.EXTRA_CONTENT, "이용약관 위반으로 서비스를 이용하실 수 없습니다.");
                DialogOk2Fragment dialogOk2Fragment = DialogOk2Fragment.getInstance();
                dialogOk2Fragment.setArguments(bundle);
                dialogOk2Fragment.show(activity.getSupportFragmentManager(), DialogOk2Fragment.TAG_MEETING_DIALOG2);
                break;
            case Warning:
                processWarning(activity, isSplash);
                break;
            default:
                if (isSplash) {
                    startSignUpActivity(activity);
                } else {
                    Toast.makeText(activity, "오류가 발생하였습니다. 관리자에게 문의해주세요.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public static void startSignUpProgress(AppCompatActivity activity, String signUpProgress, int profileProgress, boolean isSplash){
        if (isSplash){
            Intent intent1 = new Intent(activity, LaunchSignup_0.class);
            Intent intent2 = new Intent(activity, LaunchLoginActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent2.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            Intent[] intents = new Intent[]{intent1, intent2};
            activity.startActivities(intents);
        }

        switch (signUpProgress){
            default:
            case "1":{
                Intent intent1 = new Intent(activity, LaunchSignup_2PreCertification.class);
                activity.startActivity(intent1);
                break;
            }
            case "2":{
                Intent intent1 = new Intent(activity, LaunchSignup_2PreCertification.class);
                Intent intent2 = new Intent(activity, LaunchSignup_3Certification.class);
                Intent intent3 = new Intent(activity, LaunchSignup_4Profile.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent2.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent3.putExtra(progressCount, profileProgress);
                Intent[] intents = new Intent[]{intent1, intent2, intent3};
                activity.startActivities(intents);
                break;
            }
            case "3":
            case "6":{
                Intent intent1 = new Intent(activity, LaunchSignup_2PreCertification.class);
                Intent intent2 = new Intent(activity, LaunchSignup_3Certification.class);
                Intent intent3 = new Intent(activity, LaunchSignup_4Profile.class);
                Intent intent4 = new Intent(activity, LaunchSignup_15Photo.class);
                Intent intent5 = new Intent(activity, LaunchSignup_16UniversityCertification.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent2.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent3.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent3.putExtra(progressCount, 6);
                Intent[] intents = new Intent[]{intent1, intent2, intent3, intent4};
                activity.startActivities(intents);
                break;
            }
            case "4":{
                Intent intent1 = new Intent(activity, LaunchSignup_2PreCertification.class);
                Intent intent2 = new Intent(activity, LaunchSignup_3Certification.class);
                Intent intent3 = new Intent(activity, LaunchSignup_4Profile.class);
                Intent intent4 = new Intent(activity, LaunchSignup_15Photo.class);
                Intent intent5 = new Intent(activity, LaunchSignup_16UniversityCertification.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent2.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent3.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent4.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent3.putExtra(progressCount, 6);
                Intent[] intents = new Intent[]{intent1, intent2, intent3, intent4, intent5};
                activity.startActivities(intents);
                break;
            }
            case "5":{
                Intent intent5 = new Intent(activity, LaunchSignup_15Photo.class);
                Intent intent6 = new Intent(activity, LaunchSignup_16UniversityCertification.class);
                Intent intent7 = new Intent(activity, LaunchSignup_17Screening.class);
                intent5.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent6.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                Intent[] intents = new Intent[]{intent6, intent7};
                activity.startActivities(intents);
                break;
            }

        }
        activity.finish();
    }

    private static void processWarning(AppCompatActivity activity, boolean isSplash){
        FirebaseHelper.db.collection("User").document(FirebaseHelper.mUid)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                long unixTimeOfWarning = 0;
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        try {
                            unixTimeOfWarning = (long) document.get(FirebaseHelper.dateOfWarning);
                        } catch (Exception err) {
                            unixTimeOfWarning = 0;
                        }

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }

                if (unixTimeOfWarning != 0){
                    long unixTimeOfEnd = unixTimeOfWarning + 14 * 24 * 60 * 60 * 1000; //2주 뒤
                    if(unixTimeOfEnd < DateUtil.getUnixTimeLong()){
                        WriteBatch batch = FirebaseHelper.db.batch();
                        batch.update(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid), FirebaseHelper.membership, Main);
                        batch.update(FirebaseHelper.db.collection("AdminUsers").document(FirebaseHelper.mUid), FirebaseHelper.membership, Main);
                        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Intent intent = new Intent(activity, MainActivity.class);
                                activity.startActivity(intent);
                                activity.finish();
                                Toast.makeText(activity, "계정이 활성화되었습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(activity, "네트워크가 불안정합니다", Toast.LENGTH_SHORT).show();
                                FirebaseHelper.auth.signOut();
                                if (isSplash){
                                    startLoginActivity(activity);
                                }
                            }
                        });
                    } else {
                        String dateOfWarning = DateUtil.getUnixToDate(unixTimeOfWarning);
                        String dateOfEnd = DateUtil.getUnixToDate(unixTimeOfEnd);
                        Bundle bundle = new Bundle();
                        bundle.putString(Strings.EXTRA_CONTENT, "이용약관 위반으로" + dateOfWarning + "부터" + dateOfEnd + "까지 사용 정지된 계정입니다.");
                        DialogOk2Fragment dialogOk2Fragment = DialogOk2Fragment.getInstance();
                        dialogOk2Fragment.setArguments(bundle);
                        dialogOk2Fragment.show(activity.getSupportFragmentManager(), DialogOk2Fragment.TAG_MEETING_DIALOG2);
                    }
                } else {
                    Toast.makeText(activity, "오류가 발생하였습니다. 고객센터로 문의해주세요.", Toast.LENGTH_SHORT).show();
                }
                FirebaseHelper.auth.signOut();
            }
        });
    }


    //fcm토큰 얻어서 디비에 저장해줌
    public static  void sendPushTokenToDB() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        if(!isSameFcmToken(token)) { //새로발급토큰이 현재거와 다른 경우 디비에 갱신해줌
                            FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).update(FirebaseHelper.fcmToken, token)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            MyProfile.getOurInstance().setFcmToken(token);
                                            checkFcm(token);

                                        }
                                    });
                        }
                    }
                });

    }

    //새로발급받은 토큰과 원래토큰이 같은지 비교
    public static boolean isSameFcmToken(String newToken){
        if(MyProfile.getUser().getFcmToken() != null && MyProfile.getUser().getFcmToken().equals(newToken)){
            return true;
        }else{
            return  false;
        }
    }

    public static void saveShared(Activity activity, String key, String content) {
        SharedPreferences pref = activity.getSharedPreferences(FirebaseHelper.mUid , MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, content);
        editor.apply();
    }

    public static  void checkFcm(String fcmToken) {
        FirebaseFunctions mFunctions = FirebaseFunctions.getInstance(Strings.region_asia);

        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("fcmToken", fcmToken);

        mFunctions
                .getHttpsCallable("checkFcm")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, Void>() {
                    @Override
                    public Void then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        boolean result = (boolean) task.getResult().getData();
                        return null;
                    }
                });
    }

    public static void processDormant(boolean isActivated, Activity activity, RelativeLayout loaderLayout, boolean isSplash) {
        if (isActivated){
            if (!isSplash && loaderLayout != null){
                loaderLayout.setVisibility(View.VISIBLE);
            }
            WriteBatch batch = FirebaseHelper.db.batch();
            batch.update(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid), FirebaseHelper.membership, Main);
            batch.update(FirebaseHelper.db.collection("AdminUsers").document(FirebaseHelper.mUid), FirebaseHelper.membership, Main);
            batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if (!isSplash && loaderLayout != null){
                        loaderLayout.setVisibility(View.GONE);
                    }
                    Intent intent = new Intent(activity, MainActivity.class);
                    activity.startActivity(intent);
                    activity.finish();
                    Toast.makeText(activity, "계정이 활성화되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(activity, "네트워크가 불안정합니다", Toast.LENGTH_SHORT).show();
                    FirebaseHelper.auth.signOut();
                    if (isSplash){
                        startLoginActivity(activity);
                    }

                    if (!isSplash && loaderLayout != null){
                        loaderLayout.setVisibility(View.GONE);
                    }
                }
            });
        } else {
            FirebaseHelper.auth.signOut();
            if (isSplash){
                startLoginActivity(activity);
            }
        }
    }


    public static void checkAuth(Context context){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if(user == null){
            Intent intent = new Intent(context, Splash.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        }else{
            FirebaseHelper.init(auth, context);
            if(FirebaseHelper.mUid == null || FirebaseHelper.mUid.equals("")){
                Intent intent = new Intent(context, LaunchSignup_0.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
            } else {
                if(!MyProfile.getUser().getUid().equals(FirebaseHelper.mUid)){
                    FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid)
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    User user = document.toObject(User.class);
                                    MyProfile.init(user);
                                }
                            }
                        }
                    });
                }
            }

        }
    }


    public static final String SignUp = "0";
    public static final String Screening = "1";
    public static final String Scoring = "1.5";
    public static final String Main = "2";
    public static final String Fail = "3";
    public static final String Dormant = "4";
    public static final String Withdraw = "5";
    public static final String Warning = "6";
    public static final String Ban = "7";
    public static final String Apple = "88";
    public static final String Manager = "98";
    public static final String Admin = "99";

    public static final String TAG ="LaunchUtil";
    public static final String progressCount ="progressCount";

    public static final String Photo ="3";
    public static final String Certi = "4";
    public static final String ProgressScreening = "5";
    public static final String PhotoCerti = "6";


}
