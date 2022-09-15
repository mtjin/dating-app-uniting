package com.unilab.uniting.activities.launch.login;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.unilab.uniting.BuildConfig;
import com.unilab.uniting.R;
import com.unilab.uniting.activities.launch.signup.LaunchSignup_2PreCertification;
import com.unilab.uniting.fragments.dialog.DialogDormantFragment;
import com.unilab.uniting.model.Suggestion;
import com.unilab.uniting.model.User;
import com.unilab.uniting.utils.DateUtil;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.LaunchBasicActivity;
import com.unilab.uniting.utils.LaunchUtil;
import com.unilab.uniting.utils.LogData;
import com.unilab.uniting.utils.MyProfile;
import com.unilab.uniting.utils.Strings;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LaunchLoginActivity extends LaunchBasicActivity implements DialogDormantFragment.DormantCheckListener {

    final static String TAG = "LoginTAG";

    private Button mLoginButton;
    private Button mSignupButton;
    private Button mFindPWButton;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private RelativeLayout mLoaderLayout; //로딩레이아웃
    private TextView mEmailTextView;

    Button.OnClickListener onClickListener;

    //페이스북
    CallbackManager mCallbackManager;
    private Button mFacebookLoginButton;
    LoginButton loginButton;

    //파이어베이스
    private FirebaseAuth mAuth;
    private String mMembership = "-1";
    private String mSignUpProgress = "-1";
    private User mUser;
    private String mFacebookUid = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_login);

        init();
        setOnClickListener();
        setFacebookLogin();
        LogData.eventLog(LogData.LoginView, this);
    }

    private void init(){
        mAuth = FirebaseAuth.getInstance();
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));

        //xml 바인딩
        mEmailEditText = findViewById(R.id.launch_login_et_email);
        mPasswordEditText = findViewById(R.id.launch_login_et_password);
        mLoginButton = findViewById(R.id.launch_login_btn_login);
        mSignupButton = findViewById(R.id.launch_login_btn_signup);
        mFindPWButton = findViewById(R.id.launch_login_btn_findPW);
        mFacebookLoginButton = findViewById(R.id.launch_login_btn_facebook_login);
        mEmailTextView = findViewById(R.id.launch_login_tv_inquiry);

        //로딩, 툴바 레이아웃
        mLoaderLayout = findViewById(R.id.loaderLayout);
        mLoaderLayout.setClickable(true);
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
                Toast.makeText(LaunchLoginActivity.this, "취소되었습니다.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                Toast.makeText(LaunchLoginActivity.this, "페이스북 로그인 에러", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //버튼별 이동
    private void setOnClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.launch_login_btn_login:
                        mLoaderLayout.setVisibility(View.VISIBLE);
                        String email = mEmailEditText.getText().toString().trim();
                        String password = mPasswordEditText.getText().toString().trim();
                        signIn(email, password); //로그인
                        break;
                    case R.id.launch_login_btn_signup:
                        onBackPressed();
                        break;
                    case R.id.launch_login_btn_findPW:
                        Intent findPWIntent = new Intent(LaunchLoginActivity.this, LaunchLoginFindPwActivity.class);
                        startActivity(findPWIntent);
                        break;
                    case R.id.launch_login_btn_facebook_login:
                        AccessToken accessToken = AccessToken.getCurrentAccessToken();
                        if(accessToken != null && !accessToken.isExpired()){
                            handleFacebookAccessToken(accessToken);
                        } else {
                            LoginManager.getInstance().logInWithReadPermissions(LaunchLoginActivity.this, Arrays.asList("email", "user_friends")); // 접근해야할 정보 permission
                        }
                        break;
                    case R.id.launch_login_tv_inquiry:
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

        mLoginButton.setOnClickListener(onClickListener);
        mSignupButton.setOnClickListener(onClickListener);
        mFindPWButton.setOnClickListener(onClickListener);
        mFacebookLoginButton.setOnClickListener(onClickListener);
        mEmailTextView.setOnClickListener(onClickListener);
    }

    //파이어베이스 이메일, 비밀번호 빈칸 확인
    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailEditText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailEditText.setError("입력해주세요.");
            valid = false;
        } else {
            mEmailEditText.setError(null);
        }

        String password = mPasswordEditText.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordEditText.setError("입력해주세요.");
            valid = false;
        } else {
            mPasswordEditText.setError(null);
        }

        return valid;
    }

    //파이어베이스 이메일 로그인
    private void signIn(final String email, final String password) {
        if (!validateForm()) {
            mLoaderLayout.setVisibility(View.GONE);
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseHelper.init(mAuth, LaunchLoginActivity.this);
                            getMembership(); //DB에서 유저 멤버쉽등급 체크 후 등급에 맞는 페이지로 이동
                        } else {
                            Toast.makeText(LaunchLoginActivity.this, "이메일 또는 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
                            mLoaderLayout.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        mLoaderLayout.setVisibility(View.VISIBLE);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success");
                    FirebaseHelper.init(mAuth, LaunchLoginActivity.this);
                    mFacebookUid = token.getUserId();
                    LaunchUtil.saveShared(LaunchLoginActivity.this, FirebaseHelper.facebookUid, mFacebookUid);
                    getMembership(); //DB에서 유저 멤버쉽등급 체크 후 등급에 맞는 페이지로 이동
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                    Toast.makeText(LaunchLoginActivity.this, "로그인 오류(계정 중복 외)가 발생하였습니다. 오류 지속시 고객센터로 문의해주세요.",  Toast.LENGTH_SHORT).show();
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
                //멤버쉽, 프로그레스 초기화
                mMembership = "-1";
                mSignUpProgress = "-1";

                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).update(
                                FirebaseHelper.activationTime, DateUtil.getUnixTimeLong(),
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
                        LaunchUtil.startMembershipActivity(LaunchLoginActivity.this, mMembership, mSignUpProgress, false);
                        mLoaderLayout.setVisibility(View.GONE);
                    } else {//최초 가입인 경우
                        FirebaseUser user = mAuth.getCurrentUser();
                        Map<String, String> data = new HashMap<>();
                        data.put(FirebaseHelper.uid, FirebaseHelper.mUid);
                        data.put(FirebaseHelper.email, FirebaseHelper.mEmail);
                        data.put(FirebaseHelper.membership, LaunchUtil.SignUp);
                        data.put(FirebaseHelper.facebookUid, mFacebookUid);
                        data.put(FirebaseHelper.signUpProgress, Strings.SignUpPrgoress.PreCerti);
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
                                        LogData.setUserProperties(user_properties, LaunchLoginActivity.this);

                                        Intent intent = new Intent(LaunchLoginActivity.this, LaunchSignup_2PreCertification.class);
                                        startActivity(intent);
                                        mLoaderLayout.setVisibility(View.GONE);

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "계정 생성 실패", e);
                                        Toast.makeText(LaunchLoginActivity.this, "오류가 발생하였습니다. 관리자에게 문의해주세요.", Toast.LENGTH_SHORT).show();
                                        FirebaseHelper.auth.signOut();
                                        mLoaderLayout.setVisibility(View.GONE);
                                    }
                                });
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    Toast.makeText(LaunchLoginActivity.this, "오류가 발생하였습니다. 관리자에게 문의해주세요.", Toast.LENGTH_SHORT).show();
                    FirebaseHelper.auth.signOut();
                    mLoaderLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    //페이스북
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

    }



    @Override
    public void checkDormant(boolean isActivated) {
        LaunchUtil.processDormant(isActivated, LaunchLoginActivity.this, mLoaderLayout, false);
    }
}
