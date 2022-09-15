package com.unilab.uniting.activities.launch.signup;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.functions.FirebaseFunctions;
import com.unilab.uniting.R;
import com.unilab.uniting.activities.launch.login.LaunchLoginActivity;
import com.unilab.uniting.activities.setprofile.SetProfile_9CustomerServiceTerms;
import com.unilab.uniting.fragments.dialog.DialogDormantFragment;
import com.unilab.uniting.model.Suggestion;
import com.unilab.uniting.model.User;
import com.unilab.uniting.utils.DateUtil;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.LaunchBasicActivity;
import com.unilab.uniting.utils.LaunchUtil;
import com.unilab.uniting.utils.LogData;
import com.unilab.uniting.utils.MyProfile;
import com.unilab.uniting.utils.RemoteConfig;
import com.unilab.uniting.utils.Strings;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LaunchSignup_0 extends LaunchBasicActivity implements DialogDormantFragment.DormantCheckListener {

    final static String TAG = "LaunchSignupTAG";
    private FirebaseFunctions mFunctions;

    private Button mEmailSignUpButton;
    private Button mFacebookSignUpButton;
    private Button mLoginBtn;
    private Button mPreviewBtn;
    private RelativeLayout mLoaderLayout; //로딩레이아웃
    private TextView mTermsTextView;
    private TextView mValueTextView;


    //페이스북
    CallbackManager mCallbackManager;
    LoginButton loginButton;

    //파이어베이스
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private String mMembership = "-1";
    private String mSignUpProgress = "-1";
    private String mFacebookUid = "";
    private User mUser;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_signup);

        setLogData();
        init();
        setOnClickListener();
        setFacebookLogin();

    }

    private void setLogData(){
        LogData.eventLog(LogData.SignUp0, this);
        Bundle bundle = new Bundle();
        bundle.putInt(LogData.sign_up_progress, 0);
        LogData.customLog(LogData.SignUp, bundle, this);
    }

    private void init(){
        //유저 고유토큰 설정
        mAuth = FirebaseAuth.getInstance();
        mEmailSignUpButton = findViewById(R.id.launch_signup_btn_email);
        mFacebookSignUpButton = findViewById(R.id.launch_signup_btn_facebook);
        mLoginBtn = findViewById(R.id.launch_signup_btn_login);
        mPreviewBtn = findViewById(R.id.launch_signup_btn_preview);
        mTermsTextView = findViewById(R.id.launch_signup_tv_terms);
        mValueTextView = findViewById(R.id.launch_signup_tv_value);

        //로딩, 툴바 레이아웃
        mLoaderLayout = findViewById(R.id.loaderLayout);
        mLoaderLayout.setClickable(true);

        mValueTextView.setText(RemoteConfig.sign_up_value);
        mTermsTextView.setText(Html.fromHtml("Facebook에 어떤 내용도 게시하지 않습니다. 또한, 회원가입시 유니팅의 <b>이용약관 및 개인정보 보호방침</b>에 동의하는 것으로 간주됩니다."));


        if(RemoteConfig.ABPreview.variant_test.equals(RemoteConfig.ABPreview.variant_control)){
            mPreviewBtn.setVisibility(View.VISIBLE);
        } else if(RemoteConfig.ABPreview.variant_test.equals(RemoteConfig.ABPreview.variant_preview_off)){
            mPreviewBtn.setVisibility(View.GONE);
        }
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    private void setFacebookLogin(){
        FacebookSdk.fullyInitialize();
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                Toast.makeText(LaunchSignup_0.this, "취소되었습니다.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                Toast.makeText(LaunchSignup_0.this, "페이스북 로그인 에러", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setOnClickListener(){
        mEmailSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LaunchSignup_0.this, LaunchSignup_1Account.class);
                startActivity(intent);
            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LaunchSignup_0.this, LaunchLoginActivity.class);
                startActivity(intent);
            }
        });

        mFacebookSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                if(accessToken != null && !accessToken.isExpired()){
                    handleFacebookAccessToken(accessToken);
                } else {
                    LoginManager.getInstance().logInWithReadPermissions(LaunchSignup_0.this, Arrays.asList("email", "user_friends")); // 접근해야할 정보 permission
                }
            }
        });

        mPreviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LaunchSignup_0.this, PreviewActivity.class);
                startActivity(intent);
            }
        });

        mTermsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LaunchSignup_0.this, SetProfile_9CustomerServiceTerms.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        mLoaderLayout.setVisibility(View.VISIBLE);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    FirebaseHelper.init(mAuth,LaunchSignup_0.this);
                    mFacebookUid = token.getUserId();
                    LaunchUtil.saveShared(LaunchSignup_0.this, FirebaseHelper.facebookUid, mFacebookUid);
                    getMembership(); //DB에서 유저 멤버쉽등급 체크 후 등급에 맞는 페이지로 이동
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(LaunchSignup_0.this, "로그인 오류(계정 중복 외)가 발생하였습니다. 오류 지속시 고객센터로 문의해주세요.",  Toast.LENGTH_SHORT).show();
                    Suggestion suggestion = new Suggestion("오류 내용: " + task.getException(), false, "가입 전 오류",  DateUtil.getDateSec(), "가입 전 오류", "가입 전 오류");
                    FirebaseHelper.db.collection("Suggestion").document().set(suggestion);
                    mLoaderLayout.setVisibility(View.GONE);

                }
            }
        });
    }

    //DB에서 유저 멤버쉽등급 체크 후 등급에 맞는 페이지로 이동
    private void getMembership() {
        FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid)
                                .update(FirebaseHelper.activationTime, DateUtil.getUnixTimeLong(),
                                        FirebaseHelper.activationHour, DateUtil.getUnixTimeByHour(),
                                        FirebaseHelper.device, FirebaseHelper.android,
                                        FirebaseHelper.appsflyerUid, FirebaseHelper.mAppsflyerUid);
                        mUser = document.toObject(User.class);
                        MyProfile.init(mUser); //유저정보 디비접근없이 쉽게 접근할 수 있게 싱글톤 프로필객체에 유저정보 저장
                        LaunchUtil.sendPushTokenToDB();

                        if (mUser.getMembership() != null) {
                            mMembership = mUser.getMembership();
                        }
                        if (mUser.getSignUpProgress() != null) {
                            mSignUpProgress = mUser.getSignUpProgress();
                        }
                        LaunchUtil.startMembershipActivity(LaunchSignup_0.this, mMembership, mSignUpProgress, false);
                        mLoaderLayout.setVisibility(View.GONE);
                    } else {//페이스북 최초 가입인 경우
                        Map<String, String> data = new HashMap<>();
                        data.put(FirebaseHelper.uid, FirebaseHelper.mUid);
                        data.put(FirebaseHelper.email, FirebaseHelper.mEmail);
                        data.put(FirebaseHelper.membership, LaunchUtil.SignUp);
                        data.put(FirebaseHelper.signUpProgress, Strings.SignUpPrgoress.PreCerti);
                        data.put(FirebaseHelper.facebookUid, mFacebookUid);
                        data.put(FirebaseHelper.appsflyerUid, FirebaseHelper.mAppsflyerUid);

                        MyProfile.getOurInstance().setFacebookUid(mFacebookUid);
                        WriteBatch batch = FirebaseHelper.db.batch();
                        batch.set(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid), data);
                        batch.set(FirebaseHelper.db.collection("AdminUsers").document(FirebaseHelper.mUid), data);
                        batch.commit()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Map<String,String> user_properties = new HashMap<>();
                                        user_properties.put(LogData.membership, LaunchUtil.SignUp);
                                        user_properties.put(LogData.sign_up_progress, Strings.SignUpPrgoress.PreCerti);
                                        LogData.setUserProperties(user_properties, LaunchSignup_0.this);

                                        Intent intent = new Intent(LaunchSignup_0.this, LaunchSignup_2PreCertification.class);
                                        startActivity(intent);
                                        mLoaderLayout.setVisibility(View.GONE);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(LaunchSignup_0.this, "오류가 발생하였습니다. 관리자에게 문의해주세요.", Toast.LENGTH_SHORT).show();
                                        FirebaseHelper.auth.signOut();
                                        mLoaderLayout.setVisibility(View.GONE);
                                    }
                                });
                    }
                } else {
                    Toast.makeText(LaunchSignup_0.this, "오류가 발생하였습니다. 관리자에게 문의해주세요.", Toast.LENGTH_SHORT).show();
                    FirebaseHelper.auth.signOut();
                    mLoaderLayout.setVisibility(View.GONE);
                }
            }
        });
    }


    @Override
    public void checkDormant(boolean isActivated) {
        LaunchUtil.processDormant(isActivated, LaunchSignup_0.this, mLoaderLayout, false);
    }
}
