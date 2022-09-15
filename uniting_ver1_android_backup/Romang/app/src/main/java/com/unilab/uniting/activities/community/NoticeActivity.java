package com.unilab.uniting.activities.community;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.unilab.uniting.R;
import com.unilab.uniting.model.Notice;
import com.unilab.uniting.utils.BasicActivity;
import com.unilab.uniting.utils.DateUtil;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.MyProfile;

public class NoticeActivity extends BasicActivity {

    private TextView mTitleTextView;
    private TextView mDateTextView;
    private TextView mContentTextView;
    private LinearLayout mBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        init();
        setNotice();
    }

    private void init(){
        mTitleTextView = findViewById(R.id.notice_tv_title);
        mDateTextView = findViewById(R.id.notice_tv_date);
        mContentTextView = findViewById(R.id.notice_tv_content);
        mBack = findViewById(R.id.toolbar_back);

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setNotice(){
        String docId = "";
        if(MyProfile.getUser().getGender().equals("여자")){
            docId = "female";
        } else {
            docId = "male";
        }

        FirebaseHelper.db.collection("Notice").document(docId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Notice notice = document.toObject(Notice.class);
                        long unixTime = notice.getUnixTime();
                        mDateTextView.setText(DateUtil.dayForNotice(unixTime));
                        mTitleTextView.setText(notice.getTitle());
                        mContentTextView.setText(Html.fromHtml(notice.getContent()));
                    } else {
                        Toast.makeText(NoticeActivity.this, "오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(NoticeActivity.this, "오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
