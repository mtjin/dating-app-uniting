package com.unilab.uniting.activities.setprofile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.unilab.uniting.R;
import com.unilab.uniting.fragments.dialog.DialogOkFragment;
import com.unilab.uniting.fragments.setprofile.DialogLinkFacebookFragment;
import com.unilab.uniting.fragments.setprofile.DialogLogoutOkNoFragment;
import com.unilab.uniting.utils.BasicActivity;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.MyProfile;

public class SetProfile_6Account extends BasicActivity {


    final static String TAG = "SET_PROFILE_6ACCOUNT";
    final static String EXTRA_LOCATION = "EXTRA_LOCATION";

    private LinearLayout mBackLinearLayout;
    private TextView mEmailChangeTextView;
    private TextView mPWChangeTextView;
    private TextView mPhoneNumberChangeTextView;
    private TextView mLogoutTextView;
    private TextView mWithdrawTextView;
    private TextView mFacebookTextView;

    //페이스북
    CallbackManager mCallbackManager;
    private boolean isFBLinkedAlready = false;

    View.OnClickListener onClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile_6_account);

        init();

        setOnClickListener();
        checkFBLinked();

    }

    private void init(){
        mBackLinearLayout = findViewById(R.id.toolbar_back);
        mEmailChangeTextView = findViewById(R.id.setprofile_6account_tv_changeEmail);
        mPWChangeTextView = findViewById(R.id.setprofile_6account_tv_changePW);
        mPhoneNumberChangeTextView = findViewById(R.id.setprofile_6account_tv_changeTel);
        mLogoutTextView = findViewById(R.id.setprofile_6account_tv_logout);
        mWithdrawTextView = findViewById(R.id.setprofile_6account_tv_withdraw);
        mFacebookTextView = findViewById(R.id.setprofile_6account_tv_linkFacebook);
    }

    private void setOnClickListener(){
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.toolbar_back:
                        onBackPressed();
                        break;
                    case R.id.setprofile_6account_tv_changeEmail:
                        startActivity(new Intent(SetProfile_6Account.this, SetProfile_6AccountEmail.class));
                        break;
                    case R.id.setprofile_6account_tv_changePW:
                        startActivity(new Intent(SetProfile_6Account.this, SetProfile_6AccountPassword.class));
                        break;
                    case R.id.setprofile_6account_tv_changeTel:
                        startActivity(new Intent(SetProfile_6Account.this, SetProfile_6AccountPhoneNumber.class));
                        break;
                    case R.id.setprofile_6account_tv_logout:
                        DialogLogoutOkNoFragment dialog = DialogLogoutOkNoFragment.getInstance();
                        dialog.show(getSupportFragmentManager(), DialogLogoutOkNoFragment.TAG_EVENT_DIALOG);
                        break;
                    case R.id.setprofile_6account_tv_withdraw:
                        startActivity(new Intent(SetProfile_6Account.this, SetProfile_6AccountWithdraw.class));
                        break;
                    case R.id.setprofile_6account_tv_linkFacebook:
                        if (isFBLinkedAlready){
                            Toast.makeText(SetProfile_6Account.this, "이미 연동되어있습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        AccessToken accessToken = AccessToken.getCurrentAccessToken();
                        boolean isLoggedIn = (accessToken != null && !accessToken.isExpired());
                        if (isLoggedIn) {
                            linkFacebookAccount(accessToken);  //페북과 연동하기.
                            return;
                        }

                        DialogLinkFacebookFragment dialog2 = DialogLinkFacebookFragment.getInstance();
                        dialog2.show(getSupportFragmentManager(), DialogLinkFacebookFragment.TAG_EVENT_DIALOG);
                        mCallbackManager = CallbackManager.Factory.create();
                        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                            @Override
                            public void onSuccess(LoginResult loginResult) {
                                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                                linkFacebookAccount(loginResult.getAccessToken()); //계정 연동
                            }

                            @Override
                            public void onCancel() {
                                Log.d(TAG, "facebook:onCancel");
                                Toast.makeText(SetProfile_6Account.this, "취소되었습니다.", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(FacebookException error) {
                                Log.d(TAG, "facebook:onError", error);
                                Toast.makeText(SetProfile_6Account.this, "페이스북 로그인 에러", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                }
            }
        };

        mEmailChangeTextView.setOnClickListener(onClickListener);
        mPWChangeTextView.setOnClickListener(onClickListener);
        mPhoneNumberChangeTextView.setOnClickListener(onClickListener);
        mLogoutTextView.setOnClickListener(onClickListener);
        mWithdrawTextView.setOnClickListener(onClickListener);
        mBackLinearLayout.setOnClickListener(onClickListener);
        mFacebookTextView.setOnClickListener(onClickListener);
    }

    //페이스북
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void linkFacebookAccount(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        user.linkWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInWithCredential:success");
                    FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).update(FirebaseHelper.facebookUid, token.getUserId());
                    FirebaseHelper.db.collection("UserDI").document(MyProfile.getUser().getDi()).update(FirebaseHelper.facebookUid, token.getUserId());
                    Toast.makeText(SetProfile_6Account.this, "페이스북 계정과 연동되었습니다.",  Toast.LENGTH_SHORT).show();
                } else {
                    DialogOkFragment dialog = DialogOkFragment.getInstance();
                    Bundle bundle = new Bundle();
                    bundle.putString(EXTRA_LOCATION, "facebookError");
                    dialog.setArguments(bundle);
                    dialog.show(getSupportFragmentManager(), DialogOkFragment.TAG_MEETING_DIALOG2);
                }
            }
        });
    }

    private void checkFBLinked(){
        for (UserInfo user : FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
            if (user.getProviderId().equals(FirebaseHelper.providerID_FB)) { //이미 계정이 페이스북과 연동되어있는 경우.
                Log.d("xx_xx_provider_info", "User is signed in with Facebook");
                isFBLinkedAlready = true;
            }
        }
    }

}
