package com.unilab.uniting.fragments.community;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.unilab.uniting.R;
import com.unilab.uniting.adapter.community.CommunityPostAdapter;
import com.unilab.uniting.model.CommunityPost;
import com.unilab.uniting.utils.FirebaseHelper;

import java.util.ArrayList;


public class CommunityMyPostFragment extends Fragment {

    final static String TAG = "CommunityMyPostTAG";
    public final static String BOARD = "BOARD";
    public final static String UNIVERSITY = "UNIVERSITY";
    public final static String EVERYBODY = "EVERYBODY";
    public final static String BOTH = "BOTH";

    //xml
    private ArrayList<CommunityPost> mMyPostArrayList;
    private CommunityPostAdapter mPostAdapter;
    private RecyclerView mRecyclerView;

    public CommunityMyPostFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_community_mypost, container, false);
        mRecyclerView = rootView.findViewById(R.id.community_mypost_recycler);
        //리사이클러뷰 어댑터 세팅
        setAdapter();
        loadMyPostFromDB();

        return rootView;
    }


    private void setAdapter() {
        mMyPostArrayList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mPostAdapter = new CommunityPostAdapter(mRecyclerView,getActivity(), mMyPostArrayList, BOTH,true);
        mRecyclerView.setAdapter(mPostAdapter);
    }

    private void loadMyPostFromDB(){
        FirebaseHelper.db.collection("CommunityPosts").whereEqualTo("writerUid", FirebaseHelper.mUid).whereEqualTo("expired", false).orderBy(FirebaseHelper.createTimestamp, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        mPostAdapter.clear();
                        mMyPostArrayList.clear();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                CommunityPost communityPost = document.toObject(CommunityPost.class);
                                mMyPostArrayList.add(communityPost);
                            }
                            mPostAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

}
