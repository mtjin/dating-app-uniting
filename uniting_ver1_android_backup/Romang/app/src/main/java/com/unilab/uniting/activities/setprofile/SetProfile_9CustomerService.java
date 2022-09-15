package com.unilab.uniting.activities.setprofile;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.unilab.uniting.BuildConfig;
import com.unilab.uniting.R;
import com.unilab.uniting.activities.launch.tutorial.TutorialActivity;
import com.unilab.uniting.utils.BasicActivity;
import com.unilab.uniting.utils.MyProfile;

public class SetProfile_9CustomerService extends BasicActivity {

    private LinearLayout mBackLinearLayout;
    private TextView mHelpTextView;
    private TextView mNoticeTextView;
    private TextView mQuestionTextView;
    private TextView mTerms1TextView;
    private TextView mTerms2TextView;
    private TextView mTutorialTextView;

    View.OnClickListener onClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile_9_customer_service);

        mBackLinearLayout = findViewById(R.id.toolbar_back);
        mHelpTextView = findViewById(R.id.setprofile_9customer_tv_help);
        mNoticeTextView = findViewById(R.id.setprofile_9customer_tv_notice);
        mQuestionTextView = findViewById(R.id.setprofile_9customer_tv_question);
        mTerms1TextView = findViewById(R.id.setprofile_9customer_tv_terms);
        mTerms2TextView = findViewById(R.id.setprofile_9customer_tv_terms2);
        mTutorialTextView = findViewById(R.id.setprofile_9customer_tv_tutorial);

        setOnClickListener();


    }


    private void setOnClickListener(){
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.toolbar_back:
                        onBackPressed();
                        break;
                    case R.id.setprofile_9customer_tv_help:
                        startActivity(new Intent(SetProfile_9CustomerService.this, SetProfile_9CustomerServiceHelp.class));
                        break;
                    case R.id.setprofile_9customer_tv_notice:
                        startActivity(new Intent(SetProfile_9CustomerService.this, SetProfile_9CustomerServiceNotice.class));
                        break;
                    case R.id.setprofile_9customer_tv_question:
                        Toast.makeText(SetProfile_9CustomerService.this, "help@uniting.kr 으로도 문의 가능합니다.", Toast.LENGTH_SHORT).show();
                        Intent email = new Intent(Intent.ACTION_SEND);
                        String[] helpEmail = {"help@uniting.kr"};
                        email.setType("plain/Text");
                        email.putExtra(Intent.EXTRA_EMAIL, helpEmail);
                        email.putExtra(Intent.EXTRA_SUBJECT, "[" + getString(R.string.app_name) + "]  고객센터에 " + MyProfile.getUser().getNickname() +"님이 문의합니다.");
                        email.putExtra(Intent.EXTRA_TEXT, "가입 이메일: " + MyProfile.getUser().getEmail() + "\n앱 버전 (AppVersion):" + BuildConfig.VERSION_NAME + "\n기기명 (Device):" + Build.MODEL + "\n안드로이드 OS (Android OS):" + Build.VERSION.RELEASE + "\n안드로이드 SDK(API):" + Build.VERSION.SDK_INT +"\n내용 (Content):\n");
                        email.setType("message/rfc822");
                        startActivity(email);
                        break;
                    case R.id.setprofile_9customer_tv_tutorial:
                        startActivity(new Intent(SetProfile_9CustomerService.this, TutorialActivity.class));
                        break;
                    case R.id.setprofile_9customer_tv_terms:
                        startActivity(new Intent(SetProfile_9CustomerService.this, SetProfile_9CustomerServiceTerms.class));
                        break;
                    case R.id.setprofile_9customer_tv_terms2:
                        startActivity(new Intent(SetProfile_9CustomerService.this, SetProfile_9CustomerServiceTerms2.class));
                        break;

                }
            }
        };

        mBackLinearLayout.setOnClickListener(onClickListener);
        mHelpTextView.setOnClickListener(onClickListener);
        mNoticeTextView.setOnClickListener(onClickListener);
        mQuestionTextView.setOnClickListener(onClickListener);
        mTerms1TextView.setOnClickListener(onClickListener);
        mTerms2TextView.setOnClickListener(onClickListener);
        mTutorialTextView.setOnClickListener(onClickListener);
    }
}
