package com.unilab.uniting.activities.launch.signup;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.unilab.uniting.R;
import com.unilab.uniting.fragments.dialog.DialogDormantFragment;
import com.unilab.uniting.model.User;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.LaunchBasicActivity;
import com.unilab.uniting.utils.LaunchUtil;
import com.unilab.uniting.utils.LogData;
import com.unilab.uniting.utils.Strings;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class LaunchSignup_1Account extends LaunchBasicActivity implements DialogDormantFragment.DormantCheckListener {
    //TAG
    final static String TAG = "LaunchSignup1TAG";

    //xml
    private EditText mNicknameEditText;
    private EditText mEmailEditText;
    private EditText mPwEditText;
    private EditText mPwConfirmEditText;
    private Button mOkButton;
    private TextView mToolbarTitleTextView;
    private LinearLayout mBack;

    //로딩중 레이아웃
    private RelativeLayout loaderLayout;

    //파이어베이스
    private String mUid;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    //DB에서 받아와야할 변수값
    private String mMembership = "-1";
    private String mSignUpProgress = "-1";
    private User mUser;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_signup_1_account);

        setLogData();
        init();
        setOnClickListener();
    }

    private void setLogData(){
        LogData.eventLog(LogData.SignUp1_Account, this);
        Bundle bundle = new Bundle();
        bundle.putInt(LogData.sign_up_progress, 1);
        LogData.customLog(LogData.SignUp, bundle, this);
    }

    private void init(){
        //유저 고유토큰 설정
        mAuth = FirebaseAuth.getInstance();

        //바인딩
        mNicknameEditText = findViewById(R.id.signup_account1_et_nickname);
        mEmailEditText = findViewById(R.id.signup_account1_et_email);
        mPwEditText = findViewById(R.id.signup_account1_et_pw);
        mPwConfirmEditText = findViewById(R.id.signup_account1_et_pwconfirm);
        mOkButton = findViewById(R.id.signup_account1_et_ok);

        View view = findViewById(R.id.toolbar_profile_id);
        mBack = view.findViewById(R.id.toolbar_back);
        mToolbarTitleTextView = view.findViewById(R.id.toolbar_title);
        mToolbarTitleTextView.setText("계정 생성");

        //로딩중
        loaderLayout = findViewById(R.id.loaderLayout);
        loaderLayout.setClickable(true);

    }


    private void setOnClickListener(){
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //버튼 클릭시
        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickname = mNicknameEditText.getText().toString().trim();
                String email = mEmailEditText.getText().toString().trim();
                String password = mPwEditText.getText().toString().trim();
                String pwConfirm = mPwConfirmEditText.getText().toString().trim();

                //2초 내 재클릭 방지
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000){ return; }
                mLastClickTime = SystemClock.elapsedRealtime();

                //이메일에 공백 없는지, 패스워드 일치하는지 확인
                if (StringUtils.isBlank(email) || StringUtils.isBlank(password) || StringUtils.isBlank(pwConfirm)) { //입력 안 한 값이 있는 경우
                    Toast.makeText(LaunchSignup_1Account.this, "빈 칸을 모두 채워주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!(email.contains("@") || email.contains("."))) {
                    Toast.makeText(LaunchSignup_1Account.this, "이메일 형식이 맞지 않습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6){
                    Toast.makeText(LaunchSignup_1Account.this, "비밀번호는 6자 이상으로 채워주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(pwConfirm)) { //패스워드 서로 다른 경우
                    Toast.makeText(LaunchSignup_1Account.this, "비밀번호가 서로 다릅니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(nickname.length() >15){
                    Toast.makeText(LaunchSignup_1Account.this, "닉네임은 15글자 이하로 해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }


                checkEmailAlert(email, password, nickname);
            }
        });
    }

    private void checkEmailAlert(final String email, final String password, String nickname) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LaunchSignup_1Account.this);
        alertDialogBuilder.setTitle("입력한 이메일(" + email + ")이 맞나요?");
        alertDialogBuilder
                .setMessage("입력하신 이메일은 비밀번호를 찾는데 사용됩니다.")
                .setCancelable(false)
                .setPositiveButton("계정 만들기", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        createAccount(email, password, nickname);
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    //계정생성
    private void createAccount(final String email, final String password, String nickname) {
        loaderLayout.setVisibility(View.VISIBLE);
        Log.d(TAG, "createAccount:" + email);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseHelper.init(mAuth, LaunchSignup_1Account.this);
                            saveDB(email, password, nickname); //DB에 저장 후 다음 액티비티로 이동
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LaunchSignup_1Account.this, "중복 계정입니다. 해당 이메일로 가입한적이 없으시다면 고객센터로 문의해주세요.", Toast.LENGTH_LONG).show();
                            loaderLayout.setVisibility(View.GONE);
                        }
                    }
                });
    }

    //DB 저장
    private void saveDB(String email,String password, String nickname) {
        FirebaseUser user = mAuth.getCurrentUser();
        mUid = user.getUid();
        FirebaseHelper.init(mAuth, this);

        Map<String, String> data = new HashMap<>();
        Map<String, String> adminData = new HashMap<>();
        data.put(FirebaseHelper.uid, FirebaseHelper.mUid);
        data.put(FirebaseHelper.email, email);
        data.put(FirebaseHelper.membership, LaunchUtil.SignUp);
        data.put(FirebaseHelper.signUpProgress, Strings.SignUpPrgoress.PreCerti);

        adminData = data;
        adminData.put(FirebaseHelper.password, password);

        Map<String, String> idData = new HashMap<>();
        idData.put(FirebaseHelper.uid, FirebaseHelper.mUid);
        idData.put(FirebaseHelper.email, email);

        WriteBatch batch = FirebaseHelper.db.batch();
        batch.set(FirebaseHelper.db.collection("Users").document(mUid), data);
        batch.set(FirebaseHelper.db.collection("AdminUsers").document(mUid), adminData);
        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "디비저장 성공, 다음 액티비티로 이동");
                        saveProfileShared(email, password, nickname); //쉐어드에 값 임시저장

                        Map<String,String> user_properties = new HashMap<>();
                        user_properties.put(LogData.membership, LaunchUtil.SignUp);
                        user_properties.put(LogData.sign_up_progress, Strings.SignUpPrgoress.PreCerti);
                        LogData.setUserProperties(user_properties, LaunchSignup_1Account.this);

                        loaderLayout.setVisibility(View.GONE);
                        Intent intent = new Intent(LaunchSignup_1Account.this, LaunchSignup_2PreCertification.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "계정 생성 실패", e);
                        loaderLayout.setVisibility(View.GONE);
                        mAuth.signOut();
                        Toast.makeText(LaunchSignup_1Account.this, "계정 생성 실패. 재시도 해주세요.", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    /*쉐어드에 입력값 저장*/
    private void saveProfileShared(String email, String password, String nickname){
        SharedPreferences pref = getSharedPreferences(FirebaseHelper.mUid, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(FirebaseHelper.email, email);
        editor.putString(FirebaseHelper.password, password);
        editor.putString(FirebaseHelper.nickname, nickname);
        editor.apply();
    }


    @Override
    public void checkDormant(boolean isActivated) {
        LaunchUtil.processDormant(isActivated, LaunchSignup_1Account.this, loaderLayout, false);
    }

}
