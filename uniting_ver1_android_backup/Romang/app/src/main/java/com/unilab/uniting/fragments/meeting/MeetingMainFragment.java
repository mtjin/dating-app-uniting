package com.unilab.uniting.fragments.meeting;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.unilab.uniting.R;
import com.unilab.uniting.activities.MainActivity;
import com.unilab.uniting.activities.meeting.MeetingBusProvider;
import com.unilab.uniting.activities.meeting.MeetingCardActivity;
import com.unilab.uniting.activities.meeting.MeetingWriteActivity;
import com.unilab.uniting.adapter.meeting.MeetingAdapter;
import com.unilab.uniting.model.Meeting;
import com.unilab.uniting.model.User;
import com.unilab.uniting.utils.DateUtil;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.MyProfile;
import com.unilab.uniting.utils.Numbers;
import com.unilab.uniting.utils.Strings;

import java.util.ArrayList;


public class MeetingMainFragment extends Fragment implements MainActivity.ViewPagerClickListener {
    final static String TAG = "MeetingMainFragmentT";

    //xml
    private FrameLayout mFrameLayout;
    private RecyclerView mRecyclerView;
    private MeetingAdapter mMeetingAdapter;
    private ArrayList<Meeting> mMeetingArrayList;

    private ViewGroup rootView;

    //value
    private CardView mWriteCardView;
    Bus bus = MeetingBusProvider.getInstance();

    private boolean isLoading = false;
    private String partnerGender = "";

    public MeetingMainFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bus.register(this); //정류소등록
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_meeting_main, container, false);


        init();
        setViewPagerClickListener();
        setListener();

        return rootView;
    }

    private void init(){
        mFrameLayout = rootView.findViewById(R.id.home_tab1_container);
        mWriteCardView = rootView.findViewById(R.id.meeting_tab1_cv_write);
        mRecyclerView = rootView.findViewById(R.id.meeting_meetingmain_recycler);
        mMeetingArrayList = new ArrayList<>();

        if(MyProfile.getUser().getGender().equals("여자")){
            partnerGender = "남자";
        } else if(MyProfile.getUser().getGender().equals("남자")) {
            partnerGender = "여자";
        }
    }

    private void setViewPagerClickListener(){
        MainActivity activity = (MainActivity) getActivity();
        activity.setMeetingDataListener(this);
    }

    private void setListener(){
        mWriteCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent writeIntent = new Intent(getActivity(), MeetingWriteActivity.class);
                startActivity(writeIntent);
            }
        });

        //당김 새로고침
        SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.meeting_main_swipelayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setAdapter();
                loadMeetingFromDB();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void loadMeetingFromDB() {
        if(isLoading){
            return;
        }
        isLoading = true;

        mMeetingArrayList = new ArrayList<>();
        long nowUnixTime = DateUtil.getUnixTimeLong();
        long weekUnixTime = nowUnixTime - 7 * DateUtil.dayInMilliSecond;


//        final int[] count = {0};
//        FirebaseHelper.db.collection("Meetings")
//                .whereEqualTo("hostUid", FirebaseHelper.mUid)
//                .whereEqualTo("deleted", false)
//                .whereEqualTo("expired", false)
//                .orderBy(FirebaseHelper.createTimestamp, Query.Direction.DESCENDING)
//                .limit(1)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d(TAG, document.getId() + " => " + document.getData());
//                                Meeting meeting = document.toObject(Meeting.class);
//                                mMeetingArrayList.add(0, meeting);
//                            }
//                        } else {
//                            Log.d(TAG, "Error getting documents: ", task.getException());
//                        }
//                        count[0]++;
//                        if(count[0] == 2){
//                            mMeetingArrayList = sortMeetingByUnitingRule(MyProfile.getUser(), mMeetingArrayList);
//                            setAdapter();
//                            isLoading = false;
//                        }
//
//                    }
//                });


        FirebaseHelper.db.collection("Meetings")
                .whereEqualTo("hostGender", partnerGender)
                .whereEqualTo("deleted", false)
                .whereEqualTo("expired", false)
                .whereGreaterThan(FirebaseHelper.createTimestamp, weekUnixTime)
                .orderBy(FirebaseHelper.createTimestamp, Query.Direction.DESCENDING)
                .limit(Numbers.LOADING_MEETING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Meeting meeting = document.toObject(Meeting.class);
                                mMeetingArrayList.add(meeting);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                        mMeetingArrayList = sortMeetingByUnitingRule(MyProfile.getUser(), mMeetingArrayList);
                        setAdapter();
                        isLoading = false;

                    }
                });
    }

    private void setAdapter() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mMeetingAdapter = new MeetingAdapter(getActivity(), mMeetingArrayList);

        mRecyclerView.setAdapter(mMeetingAdapter);
        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(gridLayoutManager) { //TODO: 일단 구글에서 가져온 endless~~리스너로 나눠서 가져오게 해놓음.
            @Override
            public void onLoadMore(int current_page) {
                if(mMeetingArrayList.size() < 400) { //TODO: 400개까지만 되게 + 0.5초씩 딜레이 걸음(굳이 안해도 되는데 걍 해놓음ㅎㅎ) + 프로그레스바 설정 고민
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(mMeetingArrayList.size() > 0){
                                long createTimestamp = mMeetingArrayList.get(mMeetingArrayList.size() - 1).getCreateTimestamp();
                                for(Meeting meeting: mMeetingArrayList){
                                    if(meeting.getCreateTimestamp() < createTimestamp){
                                        createTimestamp = meeting.getCreateTimestamp();
                                    }
                                }

                                long nowUnixTime = DateUtil.getUnixTimeLong();
                                long weekUnixTime = nowUnixTime - 7 * DateUtil.dayInMilliSecond;

                                FirebaseHelper.db.collection("Meetings")
                                        .whereEqualTo("hostGender", partnerGender)
                                        .whereEqualTo("deleted", false)
                                        .whereEqualTo("expired", false)
                                        .whereGreaterThan(FirebaseHelper.createTimestamp, weekUnixTime)
                                        .orderBy(FirebaseHelper.createTimestamp, Query.Direction.DESCENDING)
                                        .startAfter(createTimestamp).limit(Numbers.LOADING_MEETING)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                                        Meeting meeting = document.toObject(Meeting.class);
                                                        mMeetingArrayList.add(meeting);
                                                    }

                                                    mMeetingArrayList = sortMeetingByUnitingRule(MyProfile.getUser(), mMeetingArrayList);
                                                    mMeetingAdapter.notifyDataSetChanged();
                                                } else {
                                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                                }
                                            }
                                        });
                            }
                        }
                    }, 500);
                }
            }
        });
    }

    private ArrayList<Meeting> sortMeetingByUnitingRule(User myUser, ArrayList<Meeting> meetingList){
        double upperTierPercent = 1.0;
        double lowerTierPercent = 0.0;

        if(myUser.getGender().equals("여자")){
            lowerTierPercent = 0.0;
            if(myUser.getTierPercent() < 0.3){
                upperTierPercent = 0.6;
            } else {
                upperTierPercent = myUser.getTierPercent() + 0.3;
            }
        }

        if(myUser.getGender().equals("남자")){
            upperTierPercent = 1.0;
            lowerTierPercent = myUser.getTierPercent() - 0.7;
        }

        long nowUnixTime = DateUtil.getUnixTimeLong();

        Meeting myMeeting = null;
        ArrayList<Meeting> day0List = new ArrayList<>();
        ArrayList<Meeting> day1List = new ArrayList<>();
        ArrayList<Meeting> day2List = new ArrayList<>();
        ArrayList<Meeting> day37List = new ArrayList<>();

        ArrayList<Meeting> resultList = new ArrayList<>();

        for(Meeting meeting: meetingList) {
            if(meeting.getHostUid().equals(myUser.getUid())){
                myMeeting = meeting;
            } else {
                if ((meeting.getHostTierPercent() < upperTierPercent && meeting.getHostTierPercent() > lowerTierPercent)|| (myUser.getGender().equals("여자") && !meeting.getHostUniversity().equals(""))) {
                    long diff = (nowUnixTime - meeting.getCreateTimestamp()) / DateUtil.dayInMilliSecond;
                    int diffInt = (int) diff;
                    switch (diffInt) {
                        case 0:
                            day0List.add(meeting);
                            break;
                        case 1:
                            day1List.add(meeting);
                            break;
                        case 2:
                            day2List.add(meeting);
                            break;
                        default:
                            day37List.add(meeting);
                            break;
                    }
                }
            }
        }

        ArrayList<Meeting> day0ResultList = sortMeetingByUniv(day0List);
        ArrayList<Meeting> day1ResultList = sortMeetingByUniv(day1List);
        ArrayList<Meeting> day2ResultList = sortMeetingByUniv(day2List);


        if(myMeeting != null){
            resultList.add(myMeeting);
        }
        resultList.addAll(day0ResultList);
        resultList.addAll(day1ResultList);
        resultList.addAll(day2ResultList);
        resultList.addAll(day37List);

        return resultList;
    }

    private ArrayList<Meeting> sortMeetingByUniv(ArrayList<Meeting> meetingList){
        ArrayList<Meeting> resultList = new ArrayList<>();

        for(Meeting meeting: meetingList) {
            if(meeting.getHostUniversity().contains("대")){
                resultList.add(meeting);
            }
        }

        for(Meeting meeting: meetingList) {
            if(!meeting.getHostUniversity().contains("대") && !meeting.getHostUniversity().equals("")){
                resultList.add(meeting);
            }
        }

        for(Meeting meeting: meetingList) {
            if(meeting.getHostUniversity().equals("")){
                resultList.add(meeting);
            }
        }
        return resultList;
    }

    @Subscribe //클릭된 리사이클러뷰 미팅 아이템
    public void clickedItem(Meeting meeting) {
        Intent intent = new Intent(getActivity(), MeetingCardActivity.class);
        intent.putExtra(Strings.EXTRA_MEETING, meeting);
        startActivity(intent);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        //미팅데이터불러오기
        setAdapter();
        loadMeetingFromDB();

    }

    @Override
    public void refreshData() {
        setAdapter();
        loadMeetingFromDB();
    }
}
