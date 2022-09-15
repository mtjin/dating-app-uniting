package com.unilab.uniting.activities.launch.signup;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.WriteBatch;
import com.unilab.uniting.R;
import com.unilab.uniting.utils.BasicActivity;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.LogData;
import com.unilab.uniting.utils.MyProfile;

import java.util.HashMap;
import java.util.Map;

public class LaunchSignup_18ProfileEdit extends BasicActivity {

    private EditText mNicknameEditText;
    private EditText mSelfIntroductionEditText;
    private EditText mStory0EditText;
    private EditText mStory1EditText;
    private EditText mStory2EditText;
    private LinearLayout mBack;
    private Button mSubmit;
    private RelativeLayout mLoaderLayout;
    private TextView mToolbarTextView;

    private String mNickname;
    private String mSelfIntroduction;
    private String mStory0;
    private String mStory1;
    private String mStory2;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_signup_18_profile_edit);



        init();
        setLogData();
        setMaxLine(); //EditText 줄 수 제한


        //초기 세팅
        mNicknameEditText.setText(MyProfile.getUser().getNickname());
        mSelfIntroductionEditText.setText(MyProfile.getUser().getSelfIntroduction());
        mStory0EditText.setText(MyProfile.getUser().getStory0());
        mStory1EditText.setText(MyProfile.getUser().getStory1());
        mStory2EditText.setText(MyProfile.getUser().getStory2());


        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isLoading){
                    isLoading = true;
                    mLoaderLayout.setVisibility(View.VISIBLE);
                    mNickname = mNicknameEditText.getText().toString().trim();
                    mSelfIntroduction = mSelfIntroductionEditText.getText().toString().trim();
                    mStory0 = mStory0EditText.getText().toString().trim();
                    mStory1 = mStory1EditText.getText().toString().trim();
                    mStory2 = mStory2EditText.getText().toString().trim();

                    Map<String, Object> storyData = new HashMap<>();
                    storyData.put(FirebaseHelper.nickname, mNickname);
                    storyData.put(FirebaseHelper.selfIntroduction, mSelfIntroduction);
                    storyData.put(FirebaseHelper.story0, mStory0);
                    storyData.put(FirebaseHelper.story1, mStory1);
                    storyData.put(FirebaseHelper.story2, mStory2);

                    WriteBatch batch = FirebaseHelper.db.batch();
                    batch.update(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid), storyData);
                    batch.update(FirebaseHelper.db.collection("AdminUsers").document(FirebaseHelper.mUid), FirebaseHelper.nickname, mNickname);
                    batch.commit()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(LaunchSignup_18ProfileEdit.this, "수정되었습니다.", Toast.LENGTH_SHORT).show();
                                    saveProfileShared();
                                    isLoading = false;
                                    mLoaderLayout.setVisibility(View.GONE);
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LaunchSignup_18ProfileEdit.this, "네트워크가 불안정합니다.", Toast.LENGTH_SHORT).show();
                            isLoading = false;
                            mLoaderLayout.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });

    }

    private void init(){
        View view = findViewById(R.id.toolbar_write_id);
        mBack = view.findViewById(R.id.toolbar_back);
        mSubmit = view.findViewById(R.id.toolbar_submit_btn);
        mNicknameEditText = findViewById(R.id.signup_profileEdit18_et_nickname);
        mSelfIntroductionEditText = findViewById(R.id.signup_profileEdit18_et_selfintro);
        mStory0EditText = findViewById(R.id.signup_profileEdit18_et_story0);
        mStory1EditText = findViewById(R.id.signup_profileEdit18_et_story1);
        mStory2EditText = findViewById(R.id.signup_profileEdit18_et_story2);
        mToolbarTextView = findViewById(R.id.toolbar_title);
        mToolbarTextView.setText("프로필 수정");

        //로딩중
        mLoaderLayout = findViewById(R.id.loaderLayout);
        mLoaderLayout.setClickable(true);
    }

    private void setLogData(){
        LogData.eventLog(LogData.SignUp8_Edit, this);
        Bundle bundle = new Bundle();
        bundle.putInt(LogData.sign_up_progress, 8);
        LogData.customLog(LogData.SignUp, bundle, this);
    }

    /*쉐어드에 입력값 저장*/
    private void saveProfileShared(){
        SharedPreferences pref = getSharedPreferences(FirebaseHelper.mUid, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(FirebaseHelper.nickname, mNickname);
        editor.putString(FirebaseHelper.selfIntroduction, mSelfIntroduction);
        editor.putString(FirebaseHelper.story0, mStory0);
        editor.putString(FirebaseHelper.story1, mStory1);
        editor.putString(FirebaseHelper.story2, mStory2);
        editor.apply();
    }

    private void setMaxLine() {

        mSelfIntroductionEditText.addTextChangedListener(new TextWatcher() {
            String previousString = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                previousString = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mSelfIntroductionEditText.getLineCount() >= 8) {
                    mSelfIntroductionEditText.setText(previousString);
                    mSelfIntroductionEditText.setSelection(mSelfIntroductionEditText.length());
                }
            }
        });

        mStory0EditText.addTextChangedListener(new TextWatcher() {
            String previousString = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                previousString = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mStory0EditText.getLineCount() >= 8) {
                    mStory0EditText.setText(previousString);
                    mStory0EditText.setSelection(mStory0EditText.length());
                }
            }
        });

        mStory1EditText.addTextChangedListener(new TextWatcher() {
            String previousString = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                previousString = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mStory1EditText.getLineCount() >= 8) {
                    mStory1EditText.setText(previousString);
                    mStory1EditText.setSelection(mStory1EditText.length());
                }
            }
        });

        mStory2EditText.addTextChangedListener(new TextWatcher() {
            String previousString = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                previousString = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mStory2EditText.getLineCount() >= 8) {
                    mStory2EditText.setText(previousString);
                    mStory2EditText.setSelection(mStory2EditText.length());
                }
            }
        });
    }
}
