package com.unilab.uniting.activities.setprofile;

import androidx.annotation.NonNull;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.firestore.WriteBatch;
import com.unilab.uniting.R;
import com.unilab.uniting.utils.BasicActivity;
import com.unilab.uniting.utils.FirebaseHelper;

public class SetProfile_6AccountEmail extends BasicActivity {

    final static String TAG = "SetProfile6TAG";
    private EditText mOriginalEmailEditText;
    private EditText mPwEditText;
    private EditText mNewEmailEditText;
    private EditText mNewEmail2EditText;
    private Button mOkButton;
    private LinearLayout mBack;
    private RelativeLayout mLoaderLayout;

    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile_6_account_email);

        mOriginalEmailEditText = findViewById(R.id.setprofile_6account_et_original_email);
        mPwEditText = findViewById(R.id.setprofile_6account_et_pw);
        mNewEmailEditText = findViewById(R.id.setprofile_6account_et_new_email);
        mNewEmail2EditText = findViewById(R.id.setprofile_6account_et_new_email2);
        mOkButton = findViewById(R.id.setprofile_6account_btn_change_email);
        mBack = findViewById(R.id.toolbar_back);

        //로딩중
        mLoaderLayout = findViewById(R.id.loaderLayout);
        mLoaderLayout.setClickable(true);


        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLoading) {
                    isLoading = true;
                    mLoaderLayout.setVisibility(View.VISIBLE);
                    String originalEmail = mOriginalEmailEditText.getText().toString().trim();
                    String originalPw = mPwEditText.getText().toString().trim();
                    String newEmail = mNewEmailEditText.getText().toString().trim();
                    String newEmail2 = mNewEmail2EditText.getText().toString().trim();

                    Log.d(TAG, originalEmail);
                    Log.d(TAG, originalPw);
                    Log.d(TAG, newEmail);
                    Log.d(TAG, newEmail2);

                    if (!originalEmail.isEmpty() && !originalPw.isEmpty() && !newEmail.isEmpty() && !newEmail2.isEmpty()) {
                        if (newEmail.equals(newEmail2)) {
                            AuthCredential credential = EmailAuthProvider.getCredential(originalEmail, originalPw);
                            FirebaseHelper.user.reauthenticate(credential)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            FirebaseHelper.user.updateEmail(newEmail)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Log.d(TAG, newEmail);
                                                            if (task.isSuccessful()) { //AdminUsers은 유저 본인쓰기만 가능하게 할꺼라서 따로 만듦.
                                                                WriteBatch batch = FirebaseHelper.db.batch();
                                                                batch.update(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid), FirebaseHelper.email, newEmail);
                                                                batch.update(FirebaseHelper.db.collection("AdminUsers").document(FirebaseHelper.mUid), FirebaseHelper.email, newEmail);
                                                                batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        Toast.makeText(SetProfile_6AccountEmail.this, "이메일 계정이 변경되었습니다.", Toast.LENGTH_SHORT).show();
                                                                        mLoaderLayout.setVisibility(View.GONE);
                                                                        isLoading = false;
                                                                        finish();
                                                                    }
                                                                });
                                                            } else {
                                                                Toast.makeText(SetProfile_6AccountEmail.this, "새 이메일 계정의 형식을 확인해주세요.", Toast.LENGTH_SHORT).show();
                                                                mLoaderLayout.setVisibility(View.GONE);
                                                                isLoading = false;
                                                            }
                                                        }
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, e.toString());
                                            Toast.makeText(SetProfile_6AccountEmail.this, "기존 이메일과 비밀번호를 정확히 입력해 주세요.", Toast.LENGTH_SHORT).show();
                                            mLoaderLayout.setVisibility(View.GONE);
                                            isLoading = false;
                                        }
                                    });
                        } else {
                            Toast.makeText(SetProfile_6AccountEmail.this, "새 이메일 주소가 다릅니다.", Toast.LENGTH_SHORT).show();
                            mLoaderLayout.setVisibility(View.GONE);
                            isLoading = false;
                        }
                    } else {
                        Toast.makeText(SetProfile_6AccountEmail.this, "모든 칸을 입력해주세요.", Toast.LENGTH_SHORT).show();
                        mLoaderLayout.setVisibility(View.GONE);
                        isLoading = false;
                    }
                }
            }
        });
    }

}
