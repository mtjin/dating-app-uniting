package com.unilab.uniting.fragments.home;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.unilab.uniting.R;
import com.unilab.uniting.utils.LogData;
import com.unilab.uniting.utils.Strings;

public class Tab0HomeFragment extends Fragment {
    //xml
    private HomeLikeHistoryFragment mHomeLikeHistoryFragment;
    private HomeTodayIntroFragment mHomeTodayIntroFragment;
    private TextView mTodayIntroTextView;
    private TextView mLikeHistoryTextView;
    private View mTodayUnderLineView;
    private View mLikeUnderLineView;
    private FrameLayout mFrameLayout;
    private CardView mCloseUserCardView;

    public Tab0HomeFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_tab0_home , container, false);

        mTodayIntroTextView = rootView.findViewById(R.id.home_tab0_tv_todayintro);
        mLikeHistoryTextView = rootView.findViewById(R.id.home_tab0_tv_likehistory);
        mFrameLayout = rootView.findViewById(R.id.home_tab0_container);
        mTodayUnderLineView = rootView.findViewById(R.id.home_tab0_view_todayintro_underline);
        mLikeUnderLineView = rootView.findViewById(R.id.home_tab0_view_likehistory_underline);
        mCloseUserCardView = rootView.findViewById(R.id.home_tab0_cv_closeUser);

        mHomeLikeHistoryFragment = new HomeLikeHistoryFragment();
        mHomeTodayIntroFragment = new HomeTodayIntroFragment();

        //초기화면세팅 (오늘의 소개)
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.home_tab0_container, mHomeTodayIntroFragment).commit();
        mTodayUnderLineView.setVisibility(View.VISIBLE);
        mTodayIntroTextView.setTextColor(getResources().getColor(R.color.colorBlack));
        mLikeUnderLineView.setVisibility(View.INVISIBLE);
        mLikeHistoryTextView.setTextColor(getResources().getColor(R.color.colorGray));


        setOnClickListener();

        return rootView;
    }

    private void setOnClickListener() {
        //오늘의 소개 버튼클릭시
        mTodayIntroTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //초기화면세팅
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.home_tab0_container, mHomeTodayIntroFragment).commit();
                mTodayUnderLineView.setVisibility(View.VISIBLE);
                mTodayIntroTextView.setTextColor(getResources().getColor(R.color.colorBlack));
                mLikeUnderLineView.setVisibility(View.INVISIBLE);
                mLikeHistoryTextView.setTextColor(getResources().getColor(R.color.colorGray));
            }
        });

        //좋아요 히스토리 클릭시
        mLikeHistoryTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //초기화면세팅
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.home_tab0_container, mHomeLikeHistoryFragment).commit();
                mTodayUnderLineView.setVisibility(View.INVISIBLE);
                mTodayIntroTextView.setTextColor(getResources().getColor(R.color.colorGray));
                mLikeUnderLineView.setVisibility(View.VISIBLE);
                mLikeHistoryTextView.setTextColor(getResources().getColor(R.color.colorBlack));
            }
        });

        mCloseUserCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogData.eventLog(LogData.close_user_s01_pre_intro, getActivity());
                LogData.setStageCloseUser(LogData.close_user_s01_pre_intro,  getActivity());

                Bundle bundle = new Bundle();
                bundle.putString(Strings.title, "동네 친구를 소개 받으시겠어요?");
                bundle.putString(Strings.content, "24시간 내 접속한 적 있는 친구 중 가까운 친구를 소개해드려요! \n\n 오픈 이벤트로 하루 1번 무료로 소개해드립니다.");
                bundle.putString(Strings.ok, "소개 받기");
                bundle.putString(Strings.no, "취소");
                DialogCloseUserNoOkFragment dialog = DialogCloseUserNoOkFragment.getInstance();
                dialog.setArguments(bundle);
                dialog.show(getFragmentManager(), DialogCloseUserNoOkFragment.TAG_EVENT_DIALOG);

            }
        });
    }
}
