package com.unilab.uniting.activities.setprofile;

import android.os.Bundle;
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.unilab.uniting.R;
import com.unilab.uniting.fragments.setprofile.DialogForgotPasswordFragment;
import com.unilab.uniting.utils.BasicActivity;
import com.unilab.uniting.utils.FirebaseHelper;

public class SetProfile_6AccountPassword extends BasicActivity implements DialogForgotPasswordFragment.PasswordResetListener {

    //TAG
    final static String TAG = "SetProfile6TAG";

    //xml
    private EditText mOriginalPwEditText;
    private EditText mPwEditText;
    private EditText mPwConfirmEditText;
    private Button mOkButton;
    private LinearLayout mBack;
    private RelativeLayout mLoaderLayout;
    private TextView mForgotPasswordTextView;

    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile_6_account_password);

        mOriginalPwEditText = findViewById(R.id.setprofile_6account_et_originalpw);
        mPwEditText = findViewById(R.id.setprofile_6account_et_newPW);
        mPwConfirmEditText = findViewById(R.id.setprofile_6account_et_newPW2);
        mOkButton = findViewById(R.id.setprofile_6account_btn_changePW);
        mBack = findViewById(R.id.toolbar_back);
        mForgotPasswordTextView = findViewById(R.id.setprofile_6account_tv_forgotPW);

        //로딩중
        mLoaderLayout = findViewById(R.id.loaderLayout);
        mLoaderLayout.setClickable(true);


        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mForgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogForgotPasswordFragment dialog = DialogForgotPasswordFragment.getInstance();
                dialog.show(getSupportFragmentManager(), DialogForgotPasswordFragment.TAG_EVENT_DIALOG);
            }
        });


        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isLoading){
                    isLoading = true;
                    mLoaderLayout.setVisibility(View.VISIBLE);
                    String email = FirebaseHelper.user.getEmail();
                    String originalPw = mOriginalPwEditText.getText().toString().trim();
                    String password = mPwEditText.getText().toString().trim();
                    String password2 = mPwConfirmEditText.getText().toString().trim();

                    if (!originalPw.isEmpty() && !password.isEmpty() && !password2.isEmpty()) {
                        AuthCredential credential = EmailAuthProvider.getCredential(email, originalPw);
                        if (password.equals(password2)) {
                            FirebaseHelper.user.reauthenticate(credential)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            FirebaseHelper.user.updatePassword(password)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            FirebaseHelper.db.collection("AdminUsers").document(FirebaseHelper.mUid).update(FirebaseHelper.password, password)
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            Toast.makeText(SetProfile_6AccountPassword.this, "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show();
                                                                            isLoading = false;
                                                                            mLoaderLayout.setVisibility(View.GONE);
                                                                            finish();
                                                                        }
                                                                    });
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(SetProfile_6AccountPassword.this, "비밀번호를 6자리 이상 입력해주세요.", Toast.LENGTH_SHORT).show();
                                                            isLoading = false;
                                                            mLoaderLayout.setVisibility(View.GONE);
                                                        }
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(SetProfile_6AccountPassword.this, "기존 비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                                            isLoading = false;
                                            mLoaderLayout.setVisibility(View.GONE);
                                        }
                                    });
                        } else {
                            Toast.makeText(SetProfile_6AccountPassword.this, "새 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                            isLoading = false;
                            mLoaderLayout.setVisibility(View.GONE);
                        }
                    } else {
                        Toast.makeText(SetProfile_6AccountPassword.this, "칸을 모두 채워주세요.", Toast.LENGTH_SHORT).show();
                        isLoading = false;
                        mLoaderLayout.setVisibility(View.GONE);
                    }
                }

            }
        });
    }

    @Override
    public void sendResetEmail() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null){
            Toast.makeText(SetProfile_6AccountPassword.this, "오류가 발생하였습니다. 앱을 완전히 종료 후 재시작해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        String emailAddress = auth.getCurrentUser().getEmail();
        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SetProfile_6AccountPassword.this, "가입하신 이메일로 비밀번호 재설정 이메일이 전송되었습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SetProfile_6AccountPassword.this, "오류가 발생하였습니다. 고객센터로 문의해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

