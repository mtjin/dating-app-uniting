package com.unilab.uniting.activities.report;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.unilab.uniting.R;
import com.unilab.uniting.model.ChatRoom;
import com.unilab.uniting.model.CommunityComment;
import com.unilab.uniting.model.CommunityPost;
import com.unilab.uniting.model.Meeting;
import com.unilab.uniting.model.User;
import com.unilab.uniting.utils.BasicActivity;
import com.unilab.uniting.utils.Strings;

public class ReportActivity extends BasicActivity {
    //xml
    private RelativeLayout mReport0RelativeLayout;
    private RelativeLayout mReport1RelativeLayout;
    private RelativeLayout mReport2RelativeLayout;
    private RelativeLayout mReport4RelativeLayout;
    private LinearLayout mBack;

    //putExtra Key


    //value
    private String mReportLoaction; //댓글, 게시글
    private CommunityComment mCommunityComment;
    private CommunityPost mCommunityPost;
    private User mUser;
    private Meeting mMeeting;
    private ChatRoom mChatRoom;
    View.OnClickListener onClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        mReport0RelativeLayout = findViewById(R.id.report_report_rel_report0);
        mReport1RelativeLayout = findViewById(R.id.report_report_rel_report1);
        mReport2RelativeLayout = findViewById(R.id.report_report_rel_report2);
        mReport4RelativeLayout = findViewById(R.id.report_report_rel_report4);
        mBack = findViewById(R.id.toolbar_back);

        processIntent();
        setOnClickListener();
        mReport0RelativeLayout.setOnClickListener(onClickListener);
        mReport1RelativeLayout.setOnClickListener(onClickListener);
        mReport2RelativeLayout.setOnClickListener(onClickListener);
        mReport4RelativeLayout.setOnClickListener(onClickListener);
        mBack.setOnClickListener(onClickListener);
    }

    private void processIntent() {
        Intent resultIntent = getIntent();
        mReportLoaction = resultIntent.getStringExtra(Strings.EXTRA_LOCATION);
        switch (mReportLoaction) {
            case "게시물":
                mCommunityPost = (CommunityPost) resultIntent.getSerializableExtra(Strings.EXTRA_COMMUNITY_POST);
                break;
            case "댓글":
                mCommunityComment = (CommunityComment) resultIntent.getSerializableExtra(Strings.EXTRA_COMMUNITY_COMMENT);
                break;
            case "프로필카드":
            case "프로필평가":
                mUser = (User) resultIntent.getSerializableExtra(Strings.EXTRA_USER);
                break;
            case "미팅":
                mMeeting = (Meeting) resultIntent.getSerializableExtra(Strings.EXTRA_MEETING);
                break;
            case "채팅":
                mChatRoom = (ChatRoom) resultIntent.getSerializableExtra(Strings.EXTRA_CHATTING);
                break;
        }
    }

    private void setOnClickListener() {
        final Intent intent = new Intent(ReportActivity.this, ReportWrite.class);
        intent.putExtra(Strings.EXTRA_LOCATION, mReportLoaction);
        switch (mReportLoaction){
            case "게시물":
                intent.putExtra(Strings.EXTRA_COMMUNITY_POST, mCommunityPost);
                break;
            case "댓글":
                intent.putExtra(Strings.EXTRA_COMMUNITY_COMMENT, mCommunityComment);
                break;
            case "프로필카드":
            case "프로필평가":
                intent.putExtra(Strings.EXTRA_USER, mUser);
                break;
            case "미팅":
                intent.putExtra(Strings.EXTRA_MEETING, mMeeting);
                break;
            case "채팅":
                intent.putExtra(Strings.EXTRA_CHATTING, mChatRoom);
                break;
        }

        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.report_report_rel_report0:
                        intent.putExtra(Strings.EXTRA_REPORT_TYPE,"불쾌감을 주는 회원");
                        startActivity(intent);
                        break;
                    case R.id.report_report_rel_report1:
                        intent.putExtra(Strings.EXTRA_REPORT_TYPE,"허위 사진 혹은 사칭");
                        startActivity(intent);
                        break;
                    case R.id.report_report_rel_report2:
                        intent.putExtra(Strings.EXTRA_REPORT_TYPE,"부적절한 목적의 이용");
                        startActivity(intent);
                        break;
                    case R.id.report_report_rel_report4:
                        intent.putExtra(Strings.EXTRA_REPORT_TYPE,"기타");
                        startActivity(intent);
                        break;
                    case R.id.toolbar_back:
                        onBackPressed();
                        break;
                }
            }
        };
    }
}
