package com.unilab.uniting.fragments.meeting;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.unilab.uniting.R;

public class Tab1MeetingFragment extends Fragment {

    //xml
    private TextView mMeetingTabTextView;
    private TextView mMeetingHistoryTabTextView;
    private TextView mMeetingHostTabTextView;
    private TextView mMeetingMatchedTabTextView;
    private LinearLayout mTabLinearLayout; //미팅히스토리 눌렀을때 밑에 뜨는 탭(미팅눌렀을땐 사라져야함)
    private View mMeetingUnderLine; //탭눌렀을때 언더바
    private View mMeetingHistoryUnderLine;
    //value
    private MeetingHostFragment mMeetingHostFragment;
    private MeetingMainFragment mMeetingMainFragment;
    private MeetingAppliedFragment mMeetingAppliedFragment;
    //리스너
    Button.OnClickListener onClickListener;

    public Tab1MeetingFragment() {
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_tab1_meeting , container, false);
        mMeetingTabTextView = rootView.findViewById(R.id.home_tab1_tv_meeting);
        mMeetingHistoryTabTextView = rootView.findViewById(R.id.home_tab1_tv_meetinghistory);
        mMeetingHostTabTextView = rootView.findViewById(R.id.home_tab1_tv_hostmeeting);
        mMeetingMatchedTabTextView = rootView.findViewById(R.id.home_tab1_tv_matchedmetting);
        mTabLinearLayout = rootView.findViewById(R.id.home_tab1_linear_histort_tab);
        mMeetingUnderLine = rootView.findViewById(R.id.home_tab1_view_meeting_underline);
        mMeetingHistoryUnderLine = rootView.findViewById(R.id.home_tab1_view_meetinghistory_underline);

        mMeetingHostFragment = new MeetingHostFragment();
        mMeetingMainFragment = new MeetingMainFragment();
        mMeetingAppliedFragment = new MeetingAppliedFragment();

        //버튼클리세팅
        setOnClickListener();
        mMeetingTabTextView.setOnClickListener(onClickListener);
        mMeetingHistoryTabTextView.setOnClickListener(onClickListener);
        mMeetingHostTabTextView.setOnClickListener(onClickListener);
        mMeetingMatchedTabTextView.setOnClickListener(onClickListener);

        //초기화면세팅 (미팅)
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.home_tab1_container, mMeetingMainFragment).commit();
        mMeetingUnderLine.setVisibility(View.VISIBLE);
        mMeetingTabTextView.setTextColor(getResources().getColor(R.color.colorBlack));
        mMeetingHistoryUnderLine.setVisibility(View.INVISIBLE);
        mMeetingHistoryTabTextView.setTextColor(getResources().getColor(R.color.colorGray));
        mTabLinearLayout.setVisibility(View.GONE);


        return rootView;

    }

    private void setOnClickListener(){
        onClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.home_tab1_tv_meeting: //미팅탭
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.home_tab1_container, mMeetingMainFragment).commit();
                        mMeetingUnderLine.setVisibility(View.VISIBLE);
                        mMeetingTabTextView.setTextColor(getResources().getColor(R.color.colorBlack));
                        mMeetingHistoryUnderLine.setVisibility(View.INVISIBLE);
                        mMeetingHistoryTabTextView.setTextColor(getResources().getColor(R.color.colorGray));
                        mTabLinearLayout.setVisibility(View.GONE);
                        break;
                    case R.id.home_tab1_tv_meetinghistory: //미팅히스토리탭(주최한미팅띄움)
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.home_tab1_container, mMeetingHostFragment).commit();
                        mMeetingUnderLine.setVisibility(View.INVISIBLE);
                        mMeetingTabTextView.setTextColor(getResources().getColor(R.color.colorGray));
                        mMeetingHistoryUnderLine.setVisibility(View.VISIBLE);
                        mMeetingHistoryTabTextView.setTextColor(getResources().getColor(R.color.colorBlack));
                        mTabLinearLayout.setVisibility(View.VISIBLE);
                        break;
                    case R.id.home_tab1_tv_hostmeeting: //주최한 미팅탭 클릭(위와동일)
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.home_tab1_container, mMeetingHostFragment).commit();
                        mMeetingUnderLine.setVisibility(View.INVISIBLE);
                        mMeetingTabTextView.setTextColor(getResources().getColor(R.color.colorGray));
                        mMeetingHistoryUnderLine.setVisibility(View.VISIBLE);
                        mMeetingHistoryTabTextView.setTextColor(getResources().getColor(R.color.colorBlack));
                        mMeetingHostTabTextView.setTextColor(getResources().getColor(R.color.colorBlack));
                        mMeetingMatchedTabTextView.setTextColor(getResources().getColor(R.color.colorGray));
                        mTabLinearLayout.setVisibility(View.VISIBLE);
                        break;
                    case R.id.home_tab1_tv_matchedmetting: //매칭된 미팅탭
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.home_tab1_container, mMeetingAppliedFragment).commit();
                        mMeetingUnderLine.setVisibility(View.INVISIBLE);
                        mMeetingTabTextView.setTextColor(getResources().getColor(R.color.colorGray));
                        mMeetingHistoryUnderLine.setVisibility(View.VISIBLE);
                        mMeetingHistoryTabTextView.setTextColor(getResources().getColor(R.color.colorBlack));
                        mMeetingHostTabTextView.setTextColor(getResources().getColor(R.color.colorGray));
                        mMeetingMatchedTabTextView.setTextColor(getResources().getColor(R.color.colorBlack));
                        mTabLinearLayout.setVisibility(View.VISIBLE);
                        break;
                }
            }
        };
    }

}
