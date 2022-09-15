package com.unilab.uniting.fragments.meeting;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.unilab.uniting.R;
import com.unilab.uniting.activities.meeting.MeetingBusProvider;
import com.unilab.uniting.activities.meeting.MeetingCardActivity;
import com.unilab.uniting.model.Meeting;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.Numbers;

import java.util.ArrayList;

public class MeetingAppliedFragment extends Fragment {

    //상수
    final static String TAG = "MeetingAppliedTAG";
    final static String EXTRA_MEETING = "EXTRA_MEETING";

    //xml
    private RecyclerView mRecyclerView;
    private com.unilab.uniting.adapter.meeting.MeetingAdapter mMeetingAdapter;
    private ArrayList<Meeting> mMeetingArrayList;

    Bus bus = MeetingBusProvider.getInstance();

    public MeetingAppliedFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bus.register(this); //정류소등록

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_meeting_applied, container, false);

        mRecyclerView = rootView.findViewById(R.id.meeting_meetingmain_recycler);
        setAdapter();

        return rootView;

    }


    private void setAdapter(){
        mMeetingArrayList = new ArrayList<>();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mMeetingAdapter = new com.unilab.uniting.adapter.meeting.MeetingAdapter(getActivity(), mMeetingArrayList);
        mRecyclerView.setAdapter(mMeetingAdapter);
        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(gridLayoutManager) { //TODO: 일단 구글에서 가져온 endless~~리스너로 나눠서 가져오게 해놓음.
            @Override
            public void onLoadMore(int current_page) {
                if(mMeetingArrayList.size() < 400) { //TODO: 400개까지만 되게 + 0.5초씩 딜레이 걸음(굳이 안해도 되는데 걍 해놓음ㅎㅎ) + 프로그레스바 설정 고민
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            long createTimestamp = mMeetingArrayList.get(mMeetingArrayList.size() - 1).getCreateTimestamp();
                            FirebaseHelper.db.collection("Meetings").whereArrayContains("applicantUidList", FirebaseHelper.mUid).whereEqualTo("expired", false).orderBy(FirebaseHelper.createTimestamp, Query.Direction.DESCENDING).startAfter(createTimestamp).limit(Numbers.LOADING_MEETING_HISTORY)
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
                                                mMeetingAdapter.notifyDataSetChanged();
                                                Log.d(TAG, " 5: " + (mMeetingArrayList.size() - 1));
                                            } else {
                                                Log.d(TAG, "Error getting documents: ", task.getException());
                                            }
                                        }
                                    });
                        }
                    }, 500);
                }
            }
        });
    }

    private void loadMeetingFromDB(){
        FirebaseHelper.db.collection("Meetings").whereArrayContains("applicantUidList", FirebaseHelper.mUid).whereEqualTo("expired", false).orderBy(FirebaseHelper.createTimestamp, Query.Direction.DESCENDING).limit(Numbers.LOADING_MEETING_HISTORY)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Meeting meeting = document.toObject(Meeting.class);
                                mMeetingArrayList.add(meeting);
                                mMeetingAdapter.notifyItemInserted(mMeetingArrayList.size() -1);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    @Subscribe //클릭된 리사이클러뷰 미팅 아이템
    public void clickedItem(Meeting meeting) {
        Intent intent = new Intent(getActivity(), MeetingCardActivity.class);
        intent.putExtra(EXTRA_MEETING, meeting);
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
        loadMeetingFromDB();
    }

}
