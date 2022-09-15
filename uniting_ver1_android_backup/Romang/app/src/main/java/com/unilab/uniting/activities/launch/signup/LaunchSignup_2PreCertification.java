package com.unilab.uniting.activities.launch.signup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.unilab.uniting.R;
import com.unilab.uniting.fragments.dialog.DialogOkNoFragment;
import com.unilab.uniting.utils.BasicActivity;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.LaunchUtil;
import com.unilab.uniting.utils.LogData;
import com.unilab.uniting.utils.Strings;

import java.util.HashMap;
import java.util.Map;

public class LaunchSignup_2PreCertification extends BasicActivity implements DialogOkNoFragment.DialogSignUpBackListener {

    private Button mOkButton;
    private TextView mToolbarTitleTextView;
    private LinearLayout mBack;
    private RelativeLayout mLoaderLayout;
    private TextView mGuide1TextView;
    private TextView mGuide2TextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_signup_2_precertification);

        init();
        setLogData();
        setOnClickListener();
    }

    private void setLogData(){
        LogData.eventLog(LogData.SignUp2_PreCerti, this);
        Bundle bundle = new Bundle();
        bundle.putInt(LogData.sign_up_progress, 2);
        LogData.customLog(LogData.SignUp,  bundle, this);

        Map<String,String> userProps = new HashMap<>();
        userProps.put(LogData.sign_up_progress, Strings.SignUpPrgoress.PreCerti);
        LogData.setUserProperties(userProps, LaunchSignup_2PreCertification.this);
    }

    private void init(){
        mOkButton = findViewById(R.id.signup_pre_certification_btn_ok);
        mGuide1TextView = findViewById(R.id.signup_pre_certification_tv_guide1);
        mGuide2TextView = findViewById(R.id.signup_pre_certification_tv_guide2);

        View view = findViewById(R.id.toolbar_profile_id);
        mBack = view.findViewById(R.id.toolbar_back);
        mToolbarTitleTextView = view.findViewById(R.id.toolbar_title);
        mToolbarTitleTextView.setText("본인 인증");

        //로딩, 툴바 레이아웃
        mLoaderLayout = findViewById(R.id.loaderLayout);
        mLoaderLayout.setClickable(true);

        mGuide1TextView.setText(Html.fromHtml(" 유니팅은 신뢰할 수 있는 서비스를 위해 철저한 본인 인증을 진행하고 있습니다. <br><br>모든 회원이 본인인증을 거치며, 유령회원, 알바회원은 단 한명도 없습니다. 조금 귀찮더라도 꼭 인증 부탁드려요\uD83D\uDE03"));
        mGuide2TextView.setText(Html.fromHtml(" 유니팅은 대학생들의 커뮤니티에요. 익명 커뮤니티, 미팅, 번개 그리고 채팅과 보이스톡까지 마음껏 즐겨보세요 \uD83D\uDC4F"));
    }

    private void setOnClickListener(){
        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LaunchSignup_2PreCertification.this, LaunchSignup_3Certification.class);
                startActivity(intent);
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    @Override
    public void onBackPressed() {
        Bundle bundle = new Bundle();
        bundle.putString(Strings.title, "가입을 취소하시겠어요?");
        bundle.putString(Strings.content, "지금까지 입력한 정보는 모두 삭제됩니다.");
        bundle.putString(Strings.ok, "가입 취소");
        bundle.putString(Strings.no, "가입 계속하기");
        DialogOkNoFragment dialog = DialogOkNoFragment.getInstance();
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), DialogOkNoFragment.TAG_EVENT_DIALOG);
    }

    @Override
    public void back() {
        SharedPreferences pref = getSharedPreferences(FirebaseHelper.mUid , MODE_PRIVATE);
        String di = pref.getString(FirebaseHelper.di, "");
        String facebookUid = pref.getString(FirebaseHelper.facebookUid, "");

        Map<String, Object> DIData = new HashMap<>();
        DIData.put(FirebaseHelper.uid, FirebaseHelper.mUid);
        DIData.put(FirebaseHelper.di, di);
        DIData.put(FirebaseHelper.facebookUid, facebookUid);
        DIData.put(FirebaseHelper.email, FirebaseHelper.mEmail);
        DIData.put(FirebaseHelper.membership, LaunchUtil.Withdraw);
        DIData.put(FirebaseHelper.dateOfWithdraw, 0);

        Map<String, Object> withdrawData = new HashMap<>();
        withdrawData.put(FirebaseHelper.membership, LaunchUtil.Withdraw);
        withdrawData.put(FirebaseHelper.dateOfWithdraw, 0);

        WriteBatch batch = FirebaseHelper.db.batch();
        batch.set(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid), withdrawData, SetOptions.merge());
        batch.set(FirebaseHelper.db.collection("AdminUsers").document(FirebaseHelper.mUid), withdrawData, SetOptions.merge());
        if (!di.equals("")){
            batch.set(FirebaseHelper.db.collection("UserDI").document(di), DIData, SetOptions.merge());
        }

        Map<String,String> userProps = new HashMap<>();
        userProps.put(LogData.membership, LaunchUtil.Withdraw);
        LogData.setUserProperties(userProps, LaunchSignup_2PreCertification.this);
        LogData.eventLog(LogData.Withdraw_SignUp2, LaunchSignup_2PreCertification.this);

        mLoaderLayout.setVisibility(View.VISIBLE);
        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                FirebaseHelper.auth.signOut();
                LoginManager.getInstance().logOut();

                Intent intent = new Intent(LaunchSignup_2PreCertification.this, LaunchSignup_0.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mLoaderLayout.setVisibility(View.GONE);
                Toast.makeText(LaunchSignup_2PreCertification.this, "오류 발생. 고객센터로 문의해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
