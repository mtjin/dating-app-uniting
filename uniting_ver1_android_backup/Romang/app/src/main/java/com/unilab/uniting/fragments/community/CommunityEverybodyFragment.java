package com.unilab.uniting.fragments.community;


import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.unilab.uniting.R;
import com.unilab.uniting.activities.MainActivity;
import com.unilab.uniting.adapter.community.CommunityPostAdapter;
import com.unilab.uniting.model.CommunityPost;
import com.unilab.uniting.utils.DateUtil;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.Numbers;
import com.unilab.uniting.utils.Strings;

import java.util.ArrayList;


public class CommunityEverybodyFragment extends Fragment implements  MainActivity.ViewPagerClickListener {

    //TAG
    final static String TAG = "Tab3CommunityFragmentT";

    //value
    private ArrayList<CommunityPost> mPostArrayList;
    private CommunityPostAdapter mPostAdapter;

    ViewGroup rootView;
    private long backPressedTime = 0;
    private boolean isLoading = false;
    private CommunityPost bestPost;
    //xml
    private RecyclerView mCommunityRecyclerView;

    private SwipeRefreshLayout mCommunitySwipelayout;

    public CommunityEverybodyFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_everybody_community , container, false);

        init();
        setAdapter();
        loadPostFromDB();
        setViewPagerClickListener();

        return rootView;

    }

    private void init(){
        mCommunityRecyclerView = rootView.findViewById(R.id.community_everybody_recycler);
        mCommunitySwipelayout = rootView.findViewById(R.id.community_everybody_swipelayout);

        //스와이프시 새로고침
        mCommunitySwipelayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(System.currentTimeMillis() - backPressedTime < 3000){
                    if(!isLoading){
                        mCommunitySwipelayout.setRefreshing(false);
                    }
                    return;
                }

                backPressedTime = System.currentTimeMillis();


                setAdapter();
                loadPostFromDB();
                mCommunitySwipelayout.setRefreshing(false);
            }
        });
    }


    private void setViewPagerClickListener(){
        MainActivity activity = (MainActivity) getActivity();
        activity.setCommunityDataListener(this);
    }


    //게시글 불러오기 (10개씩 불러온다)
    private void loadPostFromDB() {
        if(isLoading){
            return;
        }
        isLoading = true;

        mPostArrayList.clear();
        mPostAdapter.clear();

        final int[] count = {0};
        FirebaseHelper.db.collection("CommunityPosts")
                .whereEqualTo("board", Strings.EVERYBODY)
                .whereEqualTo(FirebaseHelper.expired, false)
                .whereEqualTo(FirebaseHelper.deleted, false)
                .whereEqualTo(FirebaseHelper.createDate, DateUtil.getDate())
                .orderBy(FirebaseHelper.view, Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                bestPost = document.toObject(CommunityPost.class);
                            }

                            if(bestPost != null){
                                mPostArrayList.add(0, bestPost);
                                mPostAdapter.setBestPost(true);
                            } else{
                                mPostAdapter.setBestPost(false);
                            }
                            mPostAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }


                        count[0]++;
                        if(count[0] == 2){
                            isLoading = false;
                        }

                    }
                });

        FirebaseHelper.db.collection("CommunityPosts").whereEqualTo("board", Strings.EVERYBODY).whereEqualTo(FirebaseHelper.expired, false).orderBy(FirebaseHelper.createTimestamp, Query.Direction.DESCENDING).limit(Numbers.LOADING_POST)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                CommunityPost communityPost = document.toObject(CommunityPost.class);
                                mPostArrayList.add(communityPost);
                            }
                            mPostAdapter.notifyDataSetChanged();

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                        count[0]++;
                        if(count[0] == 2){
                            isLoading = false;
                        }
                    }
                });
    }

    private void setAdapter(){
        mPostArrayList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        //mCommunityRecyclerView.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getActivity()), DividerItemDecoration.VERTICAL));
        mCommunityRecyclerView.setLayoutManager(layoutManager);

        //어댑터연결
        mPostAdapter = new CommunityPostAdapter(mCommunityRecyclerView, getActivity(), mPostArrayList, Strings.EVERYBODY, false);
        mCommunityRecyclerView.setAdapter(mPostAdapter);
        mCommunityRecyclerView.addOnScrollListener(new EndlessLinearRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                if(mPostArrayList.size() < 400){
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(mPostArrayList.size() > 1){
                                long createTimestamp = mPostArrayList.get(mPostArrayList.size() - 1).getCreateTimestamp();

                                FirebaseHelper.db.collection("CommunityPosts")
                                        .whereEqualTo("board", Strings.EVERYBODY)
                                        .whereEqualTo("expired", false)
                                        .orderBy(FirebaseHelper.createTimestamp, Query.Direction.DESCENDING)
                                        .startAfter(createTimestamp)
                                        .limit(Numbers.LOADING_POST)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                                        CommunityPost post = document.toObject(CommunityPost.class);
                                                        mPostArrayList.add(post);
                                                    }
                                                    mPostAdapter.notifyDataSetChanged();
                                                    Log.d(TAG, " 5: " + (mPostArrayList.size() - 1));
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



    @Override
    public void onStop() {
        super.onStop();
    }



    @Override
    public void onStart() {
        super.onStart();
        loadPostFromDB();
    }

    @Override
    public void refreshData() { //뷰페이저 클릭에 의한 refresh

        loadPostFromDB();

    }

}

