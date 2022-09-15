package com.unilab.uniting.activities.community;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.unilab.uniting.R;
import com.unilab.uniting.fragments.community.CommunityMyCommentFragment;
import com.unilab.uniting.fragments.community.CommunityMyPostFragment;
import com.unilab.uniting.utils.BasicActivity;

public class CommunityMyPostActivity extends BasicActivity {
    //상수
    final static String TAG = "MyPostTAG";

    //value
    private CommunityMyPostFragment mCommunityMyPostFragment;
    private CommunityMyCommentFragment mCommunityMyCommentFragment;

    //xml
    private TextView mMyPostTextView;
    private TextView mMyCommentTextView;
    private LinearLayout mBack;

    View.OnClickListener onClickListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_my_post);

        mMyPostTextView = findViewById(R.id.community_mypost_tv_mypost);
        mMyCommentTextView = findViewById(R.id.community_mypost_tv_mycomment);
        mBack = findViewById(R.id.community_mypost_iv_back);

        mCommunityMyPostFragment = new CommunityMyPostFragment();
        mCommunityMyCommentFragment = new CommunityMyCommentFragment();

        //클릭 리스너
        setOnClickListener();
        mMyPostTextView.setOnClickListener(onClickListener);
        mMyCommentTextView.setOnClickListener(onClickListener);
        mBack.setOnClickListener(onClickListener);

        //초기 세팅
        getSupportFragmentManager().beginTransaction().replace(R.id.community_mypost_container, mCommunityMyPostFragment).commit();

    }

    private void setOnClickListener(){
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.community_mypost_tv_mypost:
                        getSupportFragmentManager().beginTransaction().replace(R.id.community_mypost_container, mCommunityMyPostFragment).commit();
                        mMyPostTextView.setTextColor(getResources().getColor(R.color.colorBlack));
                        mMyCommentTextView.setTextColor(getResources().getColor(R.color.colorGray));
                        break;
                    case R.id.community_mypost_tv_mycomment:
                        getSupportFragmentManager().beginTransaction().replace(R.id.community_mypost_container, mCommunityMyCommentFragment).commit();
                        mMyPostTextView.setTextColor(getResources().getColor(R.color.colorGray));
                        mMyCommentTextView.setTextColor(getResources().getColor(R.color.colorBlack));
                        break;
                    case R.id.community_mypost_iv_back:
                        onBackPressed();
                        break;
                }
            }
        };
    }
}
