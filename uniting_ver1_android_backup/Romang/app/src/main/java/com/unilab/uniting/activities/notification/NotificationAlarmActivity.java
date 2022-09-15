package com.unilab.uniting.activities.notification;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.unilab.uniting.R;
import com.unilab.uniting.adapter.notification.NotificationAdapter;
import com.unilab.uniting.model.Notification;
import com.unilab.uniting.utils.BasicActivity;
import com.unilab.uniting.utils.DateUtil;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.LaunchUtil;

import java.util.ArrayList;

public class NotificationAlarmActivity extends BasicActivity {
    static final String TAG = "NotificationAlarmTAG";
    //xml
    private LinearLayout mBackImageView;
    private RecyclerView mNotiRecyclerView;
    //va;ue
    private ArrayList<Notification> mNotiArrayList;
    private NotificationAdapter mNotificationAdapter;

    private boolean checkDone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_alarm);

        setAdapter();
        loadDataFromDB();

        mBackImageView = findViewById(R.id.notification__alarm_iv_back);
        mBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void setAdapter(){
        mNotiRecyclerView = findViewById(R.id.notification_alarm_recycler);
        mNotiArrayList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mNotiRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        mNotiRecyclerView.setLayoutManager(layoutManager);
        mNotificationAdapter = new NotificationAdapter(mNotiArrayList, getApplicationContext());
        mNotiRecyclerView.setAdapter(mNotificationAdapter);

    }

    private void loadDataFromDB() {
        LaunchUtil.checkAuth(this);

        long nowUnixTime = DateUtil.getUnixTimeLong();
        long standardUnixTime = nowUnixTime - 7 * 24 * 60 * 60 * 1000;
        FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("Notification").whereGreaterThan(FirebaseHelper.unixTime, standardUnixTime).orderBy(FirebaseHelper.unixTime, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Notification notification = document.toObject(Notification.class);
                                mNotiArrayList.add(notification);
                                if(!notification.isCheck()){
                                    FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("Notification").document(document.getId()).update("check", true);
                                }
                            }
                            mNotificationAdapter.notifyDataSetChanged();
                            removerNotificationBadge();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void removerNotificationBadge(){
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }
}
