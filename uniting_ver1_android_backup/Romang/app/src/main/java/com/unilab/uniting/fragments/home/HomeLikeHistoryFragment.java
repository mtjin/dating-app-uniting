package com.unilab.uniting.fragments.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.unilab.uniting.R;
import com.unilab.uniting.adapter.home.HomeLikeHistoryAdapter;
import com.unilab.uniting.model.Interaction;
import com.unilab.uniting.model.User;
import com.unilab.uniting.utils.DateUtil;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.LaunchUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class HomeLikeHistoryFragment extends Fragment {

    final static String TAG = "HOME_HISTORY_TAG";

    private TextView mReceiveLikeTextView;
    private RecyclerView mReceiveLikeRecyclerView;
    private ArrayList<Interaction> mReceiveLikeList;
    private HomeLikeHistoryAdapter mReceiveLikeAdapter;

    private TextView mBothHighScoreTextView;
    private RecyclerView mBothHighScoreRecyclerView;
    private ArrayList<Interaction> mBothHighScoreList;
    private HomeLikeHistoryAdapter mBothHighScoreAdapter;

    private TextView mReceiveHighScoreTextView;
    private RecyclerView mReceiveHighScoreRecyclerView;
    private ArrayList<Interaction> mReceiveHighScoreList;
    private HomeLikeHistoryAdapter mReceiveHighScoreAdapter;

    private TextView mSendHighScoreTextView;
    private RecyclerView mSendHighScoreRecyclerView;
    private ArrayList<Interaction> mSendHighScoreList;
    private HomeLikeHistoryAdapter mSendHighScoreAdapter;

    private TextView mSendLikeTextView;
    private RecyclerView mSendLikeRecyclerView;
    private ArrayList<Interaction> mSendLikeList;
    private HomeLikeHistoryAdapter mSendLikeAdapter;

    private ArrayList<Interaction> mInteractionList = new ArrayList<>();

    public HomeLikeHistoryFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_home_like_history, container, false);

        //좋아요 받음 리스트
        mReceiveLikeTextView = rootView.findViewById(R.id.home_likehistory_tv_receivelike);
        mReceiveLikeRecyclerView = rootView.findViewById(R.id.home_likehistory_rv_receivelike);
        setReceiveLikeAdapter();

        //서로 높게 평가 리스트
        mBothHighScoreTextView = rootView.findViewById(R.id.home_likehistory_tv_both_high_score);
        mBothHighScoreRecyclerView = rootView.findViewById(R.id.home_likehistory_rv_both_high_score);
        setBothHighScoreAdapter();

        //나를 높게 평가 리스트
        mReceiveHighScoreTextView = rootView.findViewById(R.id.home_likehistory_tv_receive_high_score);
        mReceiveHighScoreRecyclerView = rootView.findViewById(R.id.home_likehistory_rv_receive_high_score);
        setReceiveHighScoreAdapter();

        //나를 높게 평가 리스트
        mSendHighScoreTextView = rootView.findViewById(R.id.home_likehistory_tv_send_high_score);
        mSendHighScoreRecyclerView = rootView.findViewById(R.id.home_likehistory_rv_send_high_score);
        setSendHighScoreAdapter();

        //좋아요 보냄 리스트
        mSendLikeTextView = rootView.findViewById(R.id.home_likehistory_tv_send_like);
        mSendLikeRecyclerView = rootView.findViewById(R.id.home_likehistory_rv_send_like);
        setSendLikeAdapter();

        loadInteractionData();

        return rootView;
    }



    private void loadInteractionData(){
        long nowUnixTime = DateUtil.getUnixTimeLong();
        long weekUnixTime = nowUnixTime - 7 * DateUtil.dayInMilliSecond;

        mReceiveLikeList.clear();
        mReceiveLikeAdapter.clear();
        mBothHighScoreList.clear();
        mBothHighScoreAdapter.clear();
        mReceiveHighScoreList.clear();
        mReceiveHighScoreAdapter.clear();
        mSendHighScoreList.clear();
        mSendHighScoreAdapter.clear();
        mSendLikeList.clear();
        mSendLikeAdapter.clear();


        FirebaseHelper.db.collection("Interaction").whereArrayContains(FirebaseHelper.uidList, FirebaseHelper.mUid).whereGreaterThan(FirebaseHelper.recentTime, weekUnixTime)
                .orderBy(FirebaseHelper.recentTime, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                mInteractionList = new ArrayList<>();

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Interaction interaction = document.toObject(Interaction.class);
                                    mInteractionList.add(interaction);
                                }

                                mInteractionList = getInteractionList(mInteractionList);


                                for(Interaction interaction: mInteractionList){
                                    if(!interaction.isBlocked()){
                                        if(interaction.getUid0().equals(FirebaseHelper.mUid)){
                                            if(interaction.isLikeUser1to0() && !interaction.isLikeUser0to1()){
                                                mReceiveLikeList.add(interaction);
                                            } else if(interaction.isLikeUser0to1() && !interaction.isLikeUser1to0()){
                                                mSendLikeList.add(interaction);
                                            } else if(!interaction.isLikeUser0to1() && !interaction.isLikeUser1to0()){
                                                if(interaction.getScoreUser0to1() == 1 && interaction.getScoreUser1to0() == 1){
                                                    mBothHighScoreList.add(interaction);
                                                } else if (interaction.getScoreUser0to1() != 1 && interaction.getScoreUser1to0() == 1) {
                                                    mReceiveHighScoreList.add(interaction);
                                                } else if (interaction.getScoreUser0to1() == 1 && interaction.getScoreUser1to0() != 1) {
                                                    User user;
                                                    if(interaction.getUser0().getUid().equals(FirebaseHelper.mUid)){
                                                        user = interaction.getUser1();
                                                    } else {
                                                        user = interaction.getUser0();
                                                    }

                                                    if(user.getMembership().equals(LaunchUtil.Scoring) || user.getMembership().equals(LaunchUtil.Main)){
                                                        mSendHighScoreList.add(interaction);
                                                    }

                                                }
                                            }
                                        } else if (interaction.getUid1().equals(FirebaseHelper.mUid)){
                                            if(!interaction.isLikeUser1to0() && interaction.isLikeUser0to1()){
                                                mReceiveLikeList.add(interaction);
                                            } else if(!interaction.isLikeUser0to1() && interaction.isLikeUser1to0()){
                                                mSendLikeList.add(interaction);
                                            } else if(!interaction.isLikeUser0to1() && !interaction.isLikeUser1to0()){
                                                if(interaction.getScoreUser0to1() == 1 && interaction.getScoreUser1to0() == 1){
                                                    mBothHighScoreList.add(interaction);
                                                } else if (interaction.getScoreUser0to1() == 1 && interaction.getScoreUser1to0() != 1) {
                                                    mReceiveHighScoreList.add(interaction);
                                                } else if (interaction.getScoreUser0to1() != 1 && interaction.getScoreUser1to0() == 1) {
                                                    User user;
                                                    if(interaction.getUser0().getUid().equals(FirebaseHelper.mUid)){
                                                        user = interaction.getUser1();
                                                    } else {
                                                        user = interaction.getUser0();
                                                    }

                                                    if(user.getMembership().equals(LaunchUtil.Scoring) || user.getMembership().equals(LaunchUtil.Main)){
                                                        mSendHighScoreList.add(interaction);
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        if(interaction.getUid0().equals(FirebaseHelper.mUid)){
                                            if(interaction.isLikeUser0to1() && !interaction.isLikeUser1to0()){
                                                mSendLikeList.add(interaction);
                                            }
                                        } else if (interaction.getUid1().equals(FirebaseHelper.mUid)){
                                            if (!interaction.isLikeUser0to1() && interaction.isLikeUser1to0()){
                                                mSendLikeList.add(interaction);
                                            }
                                        }
                                    }
                                }


                                Collections.sort(mReceiveLikeList, new DescendingInteraction());
                                Collections.sort(mSendLikeList, new DescendingInteraction());
                                Collections.sort(mBothHighScoreList, new DescendingInteraction());
                                Collections.sort(mReceiveHighScoreList, new DescendingInteraction());
                                Collections.sort(mSendHighScoreList, new DescendingInteraction());

                                mReceiveLikeAdapter.notifyDataSetChanged();
                                mBothHighScoreAdapter.notifyDataSetChanged();
                                mReceiveHighScoreAdapter.notifyDataSetChanged();
                                mSendHighScoreAdapter.notifyDataSetChanged();
                                mSendLikeAdapter.notifyDataSetChanged();

                                if(mReceiveLikeList.isEmpty()){
                                    mReceiveLikeRecyclerView.setVisibility(View.GONE);
                                }else{
                                    mReceiveLikeRecyclerView.setVisibility(View.VISIBLE);
                                }

                                if(mSendLikeList.isEmpty()){
                                    mSendLikeRecyclerView.setVisibility(View.GONE);
                                }else{
                                    mSendLikeRecyclerView.setVisibility(View.VISIBLE);
                                }

                                if(mBothHighScoreList.isEmpty()){
                                    mBothHighScoreRecyclerView.setVisibility(View.GONE);
                                }else{
                                    mBothHighScoreRecyclerView.setVisibility(View.VISIBLE);
                                }

                                if(mReceiveHighScoreList.isEmpty()){
                                    mReceiveHighScoreRecyclerView.setVisibility(View.GONE);
                                }else{
                                    mReceiveHighScoreRecyclerView.setVisibility(View.VISIBLE);
                                }

                                if(mSendHighScoreList.isEmpty()){
                                    mSendHighScoreRecyclerView.setVisibility(View.GONE);
                                }else{
                                    mSendHighScoreRecyclerView.setVisibility(View.VISIBLE);
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }


    private void setReceiveLikeAdapter() {
        mReceiveLikeList = new ArrayList<>();
        RecyclerView.LayoutManager receiveLikeLayoutManager = new GridLayoutManager(getActivity(), 3, LinearLayoutManager.VERTICAL, false);
        mReceiveLikeRecyclerView.setLayoutManager(receiveLikeLayoutManager);
        mReceiveLikeAdapter = new HomeLikeHistoryAdapter(getActivity(), mReceiveLikeList);
        mReceiveLikeRecyclerView.setAdapter(mReceiveLikeAdapter);
    }


    private ArrayList<Interaction> getInteractionList(ArrayList<Interaction> interactionList) { //중복된거 빼주는 함수
        ArrayList<Interaction> newList = interactionList;
        ArrayList<Integer> removeIndexList = new ArrayList<>();

        for(int i = 0; i < interactionList.size() ; i++){
            for(int j = 0; j < interactionList.size() ; j++){
                if(i != j){
                    boolean isSame = false;
                    if(interactionList.get(i).getInteractionId().equals(interactionList.get(j).getInteractionId())){
                        isSame = true;
                    }
                    if(interactionList.get(i).getUid0().equals(interactionList.get(j).getUid0()) && interactionList.get(i).getUid1().equals(interactionList.get(j).getUid1())){
                        isSame = true;
                    }
                    if(interactionList.get(i).getUid0().equals(interactionList.get(j).getUid1()) && interactionList.get(i).getUid0().equals(interactionList.get(j).getUid1())){
                        isSame = true;
                    }

                    if (isSame) {
                        int removeIndex;
                        if(interactionList.get(i).getCreateTime() > interactionList.get(j).getCreateTime()){
                            removeIndex = i;
                        } else {
                            removeIndex = j;
                        }

                        if(!removeIndexList.contains(removeIndex)){
                            removeIndexList.add(removeIndex);
                        }
                    }
                }
            }
        }

        Collections.sort(removeIndexList, new Descending());

        for(Integer index : removeIndexList){
            newList.remove(index);
        }

        return newList;
    }

    class Descending implements Comparator<Integer> {
        @Override
        public int compare(Integer o1, Integer o2) {
            return o2.compareTo(o1);
        }

    }

    class DescendingInteraction implements Comparator<Interaction> {
        @Override
        public int compare(Interaction o1, Interaction o2) {
            if(o2.getRecentTime() > o1.getRecentTime()){
                return 1;
            } else if(o2.getRecentTime() < o1.getRecentTime()) {
                return -1;
            }
            return 0;
        }

    }


    private void setBothHighScoreAdapter() {
        mBothHighScoreList = new ArrayList<>();
        RecyclerView.LayoutManager bothHighEvaluationLayoutManager = new GridLayoutManager(getActivity(), 3, LinearLayoutManager.VERTICAL, false);
        mBothHighScoreRecyclerView.setLayoutManager(bothHighEvaluationLayoutManager);
        mBothHighScoreAdapter = new HomeLikeHistoryAdapter(getActivity(), mBothHighScoreList);
        mBothHighScoreRecyclerView.setAdapter(mBothHighScoreAdapter);
    }




    private void setReceiveHighScoreAdapter() {
        mReceiveHighScoreList = new ArrayList<>();
        RecyclerView.LayoutManager receiveHighEvaluationLayoutManager = new GridLayoutManager(getActivity(), 3, LinearLayoutManager.VERTICAL, false);
        mReceiveHighScoreRecyclerView.setLayoutManager(receiveHighEvaluationLayoutManager);
        mReceiveHighScoreAdapter = new HomeLikeHistoryAdapter(getActivity(), mReceiveHighScoreList);
        mReceiveHighScoreRecyclerView.setAdapter(mReceiveHighScoreAdapter);
    }


    private void setSendHighScoreAdapter() {
        mSendHighScoreList = new ArrayList<>();
        RecyclerView.LayoutManager receiveHighEvaluationLayoutManager = new GridLayoutManager(getActivity(), 3, LinearLayoutManager.VERTICAL, false);
        mSendHighScoreRecyclerView.setLayoutManager(receiveHighEvaluationLayoutManager);
        mSendHighScoreAdapter = new HomeLikeHistoryAdapter(getActivity(), mSendHighScoreList);
        mSendHighScoreRecyclerView.setAdapter(mSendHighScoreAdapter);
    }


    private void setSendLikeAdapter() {
        mSendLikeList = new ArrayList<>();
        RecyclerView.LayoutManager sendLikeLayoutManager = new GridLayoutManager(getActivity(), 3, LinearLayoutManager.VERTICAL, false);
        mSendLikeRecyclerView.setLayoutManager(sendLikeLayoutManager);
        mSendLikeAdapter = new HomeLikeHistoryAdapter(getActivity(), mSendLikeList);
        mSendLikeRecyclerView.setAdapter(mSendLikeAdapter);

    }

}
