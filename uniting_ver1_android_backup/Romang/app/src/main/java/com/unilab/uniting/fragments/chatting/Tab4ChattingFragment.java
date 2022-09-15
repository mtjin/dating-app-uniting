package com.unilab.uniting.fragments.chatting;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.unilab.uniting.R;
import com.unilab.uniting.adapter.chatting.ChatRoomAdapter;
import com.unilab.uniting.model.ChatRoom;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.Numbers;

import java.util.ArrayList;
import java.util.Objects;

public class Tab4ChattingFragment extends Fragment {

    static final String TAG = "Tab4ChattingFragmentT";

    private RecyclerView mRecyclerView;
    private ChatRoomAdapter mTodayIntroChatRoomAdapter;
    private ArrayList<ChatRoom> mTodayIntroChatRoomArrayList = new ArrayList<>();

    private ViewGroup rootView;

    private int todayIntroCount = 0;
    private ListenerRegistration todayIntroListenerRegistration;


    public Tab4ChattingFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_tab4_chatting, container, false);

        init();
        setAdapter();
        loadTodayIntroChatRoom();


        return rootView;
    }

    private void init(){
        mRecyclerView = rootView.findViewById(R.id.chatting_tab4_recycler);

    }


    private void setAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getContext()), DividerItemDecoration.VERTICAL));
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setLayoutManager(layoutManager);

        mTodayIntroChatRoomAdapter = new ChatRoomAdapter(getActivity(), mTodayIntroChatRoomArrayList);
        mTodayIntroChatRoomAdapter.setHasStableIds(true);

        mRecyclerView.setAdapter(mTodayIntroChatRoomAdapter);

        mRecyclerView.addOnScrollListener(new EndlessLinearRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                loadTodayIntroChatRoom();
                mRecyclerView.setAdapter(mTodayIntroChatRoomAdapter);
            }
        });

    }

    private void loadTodayIntroChatRoom() {
        if (todayIntroListenerRegistration != null){
            todayIntroListenerRegistration.remove();
        }

        todayIntroCount++;
        mTodayIntroChatRoomArrayList.clear();
        mTodayIntroChatRoomAdapter.clear();
        todayIntroListenerRegistration = FirebaseHelper.db.collection("ChatRoom").whereArrayContains(FirebaseHelper.userUidList, FirebaseHelper.mUid).whereEqualTo(FirebaseHelper.from, FirebaseHelper.todayIntro).whereEqualTo(FirebaseHelper.expired, false).orderBy(FirebaseHelper.recentTimestamp, Query.Direction.ASCENDING).limit(Numbers.LOADING_CHATTING * todayIntroCount)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }
                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                            ChatRoom chatRoom2 = dc.getDocument().toObject(ChatRoom.class);
                            switch (dc.getType()) {
                                case ADDED:
                                    if (!(chatRoom2.isDeleted() && (chatRoom2.getUidDelete().equals(FirebaseHelper.mUid) || chatRoom2.getUidBanned().equals(FirebaseHelper.mUid)))) {//내가 먼저 종료한 채팅은 리스트에서 제외
                                        mTodayIntroChatRoomArrayList.add(0, chatRoom2);
                                    }
                                    break;
                                case MODIFIED:
                                    if (!(chatRoom2.isDeleted() && (chatRoom2.getUidDelete().equals(FirebaseHelper.mUid) || chatRoom2.getUidBanned().equals(FirebaseHelper.mUid)))) {//내가 먼저 종료한 채팅은 리스트에서 제외
                                        for (int i = 0; i < mTodayIntroChatRoomArrayList.size(); i++) {
                                            if (chatRoom2.getRoomId().equals(mTodayIntroChatRoomArrayList.get(i).getRoomId())) {
                                                mTodayIntroChatRoomArrayList.remove(i);
                                                mTodayIntroChatRoomArrayList.add(0, chatRoom2);
                                            }
                                        }
                                    } else {
                                        for (int i = 0; i < mTodayIntroChatRoomArrayList.size(); i++) {
                                            if (chatRoom2.getRoomId().equals(mTodayIntroChatRoomArrayList.get(i).getRoomId())) {
                                                mTodayIntroChatRoomArrayList.remove(i);
                                                break;
                                            }
                                        }
                                    }
                                    break;
                                case REMOVED:
                                    for (int i = 0; i < mTodayIntroChatRoomArrayList.size(); i++) {
                                        if (chatRoom2.getRoomId().equals(mTodayIntroChatRoomArrayList.get(i).getRoomId())) {
                                            mTodayIntroChatRoomArrayList.remove(i);
                                            break;
                                        }
                                    }
                                    break;
                            }
                            mTodayIntroChatRoomAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}

