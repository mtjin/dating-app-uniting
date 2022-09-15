package com.unilab.uniting.activities.launch.login;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.unilab.uniting.R;
import com.unilab.uniting.utils.LaunchBasicActivity;

public class LaunchLoginFindPwActivity extends LaunchBasicActivity {

    private FirebaseAuth mAuth;

    private EditText mEmailEditText;
    private Button mFindPWButton;
    private LinearLayout mBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_login_find_pw);

        mAuth = FirebaseAuth.getInstance();
        mEmailEditText = findViewById(R.id.launch_login_findPw_et_email);
        mFindPWButton = findViewById(R.id.launch_login_findPw_btn_findPW);
        mBack = findViewById(R.id.toolbar_back);

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mFindPWButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmailEditText.getText().toString().trim();
                if (email.equals("")){
                    Toast.makeText(LaunchLoginFindPwActivity.this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    sendPasswordReset(email);
                }

            }
        });

    }


    private void sendPasswordReset(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LaunchLoginFindPwActivity.this, "이메일로 링크가 전송되었습니다.", Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            Toast.makeText(LaunchLoginFindPwActivity.this, "등록된 이메일이 아닙니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}

