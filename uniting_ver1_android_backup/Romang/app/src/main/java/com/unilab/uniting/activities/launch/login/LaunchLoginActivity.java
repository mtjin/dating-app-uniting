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
    private RelativeLayout mLoaderLayout; //??????????????????
    private TextView mEmailTextView;

    Button.OnClickListener onClickListener;

    //????????????
    CallbackManager mCallbackManager;
    private Button mFacebookLoginButton;
    LoginButton loginButton;

    //??????????????????
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

        //xml ?????????
        mEmailEditText = findViewById(R.id.launch_login_et_email);
        mPasswordEditText = findViewById(R.id.launch_login_et_password);
        mLoginButton = findViewById(R.id.launch_login_btn_login);
        mSignupButton = findViewById(R.id.launch_login_btn_signup);
        mFindPWButton = findViewById(R.id.launch_login_btn_findPW);
        mFacebookLoginButton = findViewById(R.id.launch_login_btn_facebook_login);
        mEmailTextView = findViewById(R.id.launch_login_tv_inquiry);

        //??????, ?????? ????????????
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
                Toast.makeText(LaunchLoginActivity.this, "?????????????????????.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                Toast.makeText(LaunchLoginActivity.this, "???????????? ????????? ??????", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //????????? ??????
    private void setOnClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.launch_login_btn_login:
                        mLoaderLayout.setVisibility(View.VISIBLE);
                        String email = mEmailEditText.getText().toString().trim();
                        String password = mPasswordEditText.getText().toString().trim();
                        signIn(email, password); //?????????
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
                            LoginManager.getInstance().logInWithReadPermissions(LaunchLoginActivity.this, Arrays.asList("email", "user_friends")); // ??????????????? ?????? permission
                        }
                        break;
                    case R.id.launch_login_tv_inquiry:
                        Intent emailIntent = new Intent(Intent.ACTION_SEND);
                        String[] helpEmail = {getString(R.string.help_mail)};
                        emailIntent.setType("plain/Text");
                        emailIntent.putExtra(Intent.EXTRA_EMAIL, helpEmail);
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[" + getString(R.string.app_name) + "]  ??????????????? ???????????????.");
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "?????? ?????? ?????????: " + MyProfile.getUser().getEmail() + "\n??? ?????? (AppVersion):" + BuildConfig.VERSION_NAME + "\n????????? (Device):" + Build.MODEL + "\n??????????????? OS (Android OS):" + Build.VERSION.RELEASE + "\n??????????????? SDK(API):" + Build.VERSION.SDK_INT +"\n?????? (Content):\n");
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

    //?????????????????? ?????????, ???????????? ?????? ??????
    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailEditText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailEditText.setError("??????????????????.");
            valid = false;
        } else {
            mEmailEditText.setError(null);
        }

        String password = mPasswordEditText.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordEditText.setError("??????????????????.");
            valid = false;
        } else {
            mPasswordEditText.setError(null);
        }

        return valid;
    }

    //?????????????????? ????????? ?????????
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
                            getMembership(); //DB?????? ?????? ??????????????? ?????? ??? ????????? ?????? ???????????? ??????
                        } else {
                            Toast.makeText(LaunchLoginActivity.this, "????????? ?????? ??????????????? ??????????????????.", Toast.LENGTH_SHORT).show();
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
                    getMembership(); //DB?????? ?????? ??????????????? ?????? ??? ????????? ?????? ???????????? ??????
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                    Toast.makeText(LaunchLoginActivity.this, "????????? ??????(?????? ?????? ???)??? ?????????????????????. ?????? ????????? ??????????????? ??????????????????.",  Toast.LENGTH_SHORT).show();
                    Suggestion suggestion = new Suggestion("?????? ??????: " + task.getException(), false, "?????? ??? ??????",  DateUtil.getDateSec(), "?????? ??? ??????", "?????? ??? ??????");
                    FirebaseHelper.db.collection("Suggestion").document().set(suggestion);
                    mLoaderLayout.setVisibility(View.GONE);


                }
            }
        });
    }

    //DB?????? ?????? ??????????????? ?????? ??? ????????? ?????? ???????????? ??????
    private void getMembership() {
        FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                //?????????, ??????????????? ?????????
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
                        MyProfile.init(mUser); //???????????? ?????????????????? ?????? ????????? ??? ?????? ????????? ?????????????????? ???????????? ??????
                        LaunchUtil.sendPushTokenToDB();

                        if (mUser.getMembership() != null) {
                            mMembership = mUser.getMembership();
                        }
                        if (mUser.getSignUpProgress() != null) {
                            mSignUpProgress = mUser.getSignUpProgress();
                        }
                        LaunchUtil.startMembershipActivity(LaunchLoginActivity.this, mMembership, mSignUpProgress, false);
                        mLoaderLayout.setVisibility(View.GONE);
                    } else {//?????? ????????? ??????
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
                                        Log.w(TAG, "?????? ?????? ??????", e);
                                        Toast.makeText(LaunchLoginActivity.this, "????????? ?????????????????????. ??????????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                                        FirebaseHelper.auth.signOut();
                                        mLoaderLayout.setVisibility(View.GONE);
                                    }
                                });
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    Toast.makeText(LaunchLoginActivity.this, "????????? ?????????????????????. ??????????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                    FirebaseHelper.auth.signOut();
                    mLoaderLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    //????????????
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
