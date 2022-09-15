package com.unilab.uniting.activities.meeting;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.unilab.uniting.R;
import com.unilab.uniting.adapter.meeting.MeetingApplicantAdapter;
import com.unilab.uniting.model.Meeting;
import com.unilab.uniting.model.User;
import com.unilab.uniting.utils.BasicActivity;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.Strings;

import java.util.ArrayList;
import java.util.Arrays;

public class MeetingApplicantListActivity extends BasicActivity {

    final String TAG = "UserListStep1TAG";

    Meeting meeting;
    private String meetingId;

    private RecyclerView step1UserRecyclerView;
    private ArrayList<User> step1UserList;
    private MeetingApplicantAdapter meetingApplicantAdapter;

    private RecyclerView step2UserRecyclerView;
    private ArrayList<User> step2UserList;
    private MeetingApplicantAdapter meetingApplicantStep2Adapter;

    private LinearLayout mBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_user_list_step1);

        step1UserRecyclerView = findViewById(R.id.meeting_userlist1_rv_list);
        step2UserRecyclerView = findViewById(R.id.meeting_userlist2_rv_list);
        mBack = findViewById(R.id.toolbar_back);

        //인텐트에서 미팅 정보 획득
        Intent intent = getIntent();
        meetingId = intent.getStringExtra(Strings.EXTRA_MEETING_UID);
        meeting = (Meeting) intent.getSerializableExtra(Strings.EXTRA_MEETING);

        setAdapter();
        loadStep1UserList();
        loadStep2UserList();

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setAdapter(){
        step1UserList = new ArrayList<>();
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(MeetingApplicantListActivity.this, 2, LinearLayoutManager.VERTICAL, false);
        step1UserRecyclerView.setLayoutManager(layoutManager);
        meetingApplicantAdapter = new MeetingApplicantAdapter(MeetingApplicantListActivity.this, step1UserList, meetingId, meeting);
        step1UserRecyclerView.setAdapter(meetingApplicantAdapter);

        step2UserList = new ArrayList<>();
        RecyclerView.LayoutManager layoutManager2 = new GridLayoutManager(MeetingApplicantListActivity.this, 2, LinearLayoutManager.VERTICAL, false);
        step2UserRecyclerView.setLayoutManager(layoutManager2);
        meetingApplicantStep2Adapter = new MeetingApplicantAdapter(MeetingApplicantListActivity.this, step2UserList, meetingId, meeting);
        step2UserRecyclerView.setAdapter(meetingApplicantStep2Adapter);
    }

    private void loadStep1UserList(){
        step1UserList.clear();
        meetingApplicantAdapter.clear();
        FirebaseHelper.db.collection("Meetings").document(meetingId).collection("Applicant").whereIn(FirebaseHelper.meetingStep1, Arrays.asList(FirebaseHelper.PRE_APPLY, FirebaseHelper.SCREENING, FirebaseHelper.FAIL))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                User user = document.toObject(User.class);
                                step1UserList.add(user);
                            }
                            meetingApplicantAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void loadStep2UserList(){
        step2UserList.clear();
        meetingApplicantStep2Adapter.clear();
        FirebaseHelper.db.collection("Meetings").document(meetingId).collection("Applicant").whereEqualTo(FirebaseHelper.meetingStep1, FirebaseHelper.PASS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                User user = document.toObject(User.class);
                                step2UserList.add(user);
                            }
                            meetingApplicantStep2Adapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}