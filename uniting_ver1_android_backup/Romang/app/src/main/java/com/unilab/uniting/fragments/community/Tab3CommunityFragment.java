package com.unilab.uniting.fragments.community;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.unilab.uniting.R;
import com.unilab.uniting.activities.community.CommunityMyPostActivity;
import com.unilab.uniting.activities.community.CommunityWriteActivity;
import com.unilab.uniting.activities.community.NoticeActivity;
import com.unilab.uniting.model.Notice;
import com.unilab.uniting.utils.DateUtil;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.MyProfile;
import com.unilab.uniting.utils.Strings;

import de.hdodenhof.circleimageview.CircleImageView;

public class Tab3CommunityFragment extends Fragment {

    //xml
    private CommunityEverybodyFragment mCommunityEverybodyFragment;
    private CommunityUniversityFragment mCommunityUniversityFragment;
    private TextView mEverybodyTextView;
    private TextView mUniversityTextView;
    private View mEverybodyUnderLineView;
    private View mUniversityUnderLineView;
    private FrameLayout mFrameLayout;
    private TextView mNoticeTitleTextView;
    private TextView mNoticeDateTextView;
    private LinearLayout mNoticeLayout;
    private CircleImageView mNoticeImageView;
    private ViewGroup rootView;
    private CardView mMyPostCardView;
    private CardView mWriteCardView;

    public Tab3CommunityFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_tab3_community , container, false);

        init();
        setOnClickListener();
        setNotice();

        return rootView;
    }

    private void init(){
        mEverybodyTextView = rootView.findViewById(R.id.community_tab3_tv_everybody);
        mUniversityTextView = rootView.findViewById(R.id.community_tab3_tv_university);
        mFrameLayout = rootView.findViewById(R.id.community_tab3_container);
        mEverybodyUnderLineView = rootView.findViewById(R.id.community_tab3_view_everybody_underline);
        mUniversityUnderLineView = rootView.findViewById(R.id.community_tab3_view_university_underline);
        mNoticeLayout = rootView.findViewById(R.id.community_tab3_linear_notice);
        mNoticeTitleTextView = rootView.findViewById(R.id.community_tab3_tv_notice_title);
        mNoticeDateTextView = rootView.findViewById(R.id.community_tab3_tv_notice_date);

        mMyPostCardView = rootView.findViewById(R.id.community_tab3_cv_mypost);
        mWriteCardView = rootView.findViewById(R.id.community_tab3_cv_write);


        mCommunityEverybodyFragment = new CommunityEverybodyFragment();
        mCommunityUniversityFragment = new CommunityUniversityFragment();

        //초기화면세팅 (오늘의 소개)
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.community_tab3_container, mCommunityEverybodyFragment).commit();
        mEverybodyUnderLineView.setVisibility(View.VISIBLE);
        mEverybodyTextView.setTextColor(getResources().getColor(R.color.colorBlack));
        mUniversityUnderLineView.setVisibility(View.INVISIBLE);
        mUniversityTextView.setTextColor(getResources().getColor(R.color.colorGray));

        if(MyProfile.getUser().isOfficialUniversityChecked()){
            mUniversityTextView.setText(MyProfile.getUser().getUniversity());
        }

    }

    private void setNotice(){
        FirebaseHelper.db.collection("Notice").document("main")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Notice notice = document.toObject(Notice.class);
                        long unixTime = notice.getUnixTime();
                        mNoticeTitleTextView.setText(notice.getTitle());
                        mNoticeDateTextView.setText(DateUtil.dayForNotice(unixTime));
                    } else {
                    }
                } else {
                }
            }
        });
    }



    private void setOnClickListener(){
        mEverybodyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //초기화면세팅
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.community_tab3_container, mCommunityEverybodyFragment).commit();
                mEverybodyUnderLineView.setVisibility(View.VISIBLE);
                mEverybodyTextView.setTextColor(getResources().getColor(R.color.colorBlack));
                mUniversityUnderLineView.setVisibility(View.INVISIBLE);
                mUniversityTextView.setTextColor(getResources().getColor(R.color.colorGray));
            }
        });

        mUniversityTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MyProfile.getUser().isOfficialUniversityChecked()) {
                    DialogBoardOkFragment dialog = DialogBoardOkFragment.getInstance();
                    dialog.show(getFragmentManager(), DialogBoardOkFragment.TAG_MEETING_DIALOG2);
                    return;
                }
                //초기화면세팅
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.community_tab3_container, mCommunityUniversityFragment).commit();
                mEverybodyUnderLineView.setVisibility(View.INVISIBLE);
                mEverybodyTextView.setTextColor(getResources().getColor(R.color.colorGray));
                mUniversityUnderLineView.setVisibility(View.VISIBLE);
                mUniversityTextView.setTextColor(getResources().getColor(R.color.colorBlack));
            }
        });

        mNoticeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), NoticeActivity.class));
            }
        });

        mWriteCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent writeIntent = new Intent(getActivity(), CommunityWriteActivity.class);
                writeIntent.putExtra(Strings.BOARD,Strings.EVERYBODY);
                startActivity(writeIntent);
            }
        });

        mMyPostCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myPostIntent = new Intent(getActivity(), CommunityMyPostActivity.class);
                startActivity(myPostIntent);
            }
        });
    }
}
