package com.unilab.uniting.activities.setprofile;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.unilab.uniting.R;
import com.unilab.uniting.adapter.setprofile.SetProfileNoticeAdapter;
import com.unilab.uniting.utils.BasicActivity;
import com.unilab.uniting.utils.FirebaseHelper;

import java.util.ArrayList;

public class SetProfile_9CustomerServiceNotice extends BasicActivity {

    final static String TAG = "CUSTOMER_NOTICE_TAG";

    private LinearLayout mBack;
    private RecyclerView mNoticeRecyclerView;
    private ArrayList<QueryDocumentSnapshot> mNoticeList;
    private SetProfileNoticeAdapter mNoticeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile_9_customer_service_notice);

        mBack = findViewById(R.id.toolbar_back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //공지사항 리스트
        mNoticeRecyclerView = findViewById(R.id.setprofile_customer_service_rv_notice);
        mNoticeList = new ArrayList<>();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mNoticeRecyclerView.setLayoutManager(layoutManager);
        mNoticeAdapter = new SetProfileNoticeAdapter(SetProfile_9CustomerServiceNotice.this, mNoticeList);
        mNoticeRecyclerView.setAdapter(mNoticeAdapter);

        mNoticeList.clear();
        mNoticeAdapter.clear();
        //해당 uid의 유저정보 가져오기
        FirebaseHelper.db.collection("Notice").orderBy("unixTime", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(task.getResult() != null){
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    mNoticeList.add(document);
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        mNoticeAdapter.notifyDataSetChanged();

                    }
                });

    }
}
