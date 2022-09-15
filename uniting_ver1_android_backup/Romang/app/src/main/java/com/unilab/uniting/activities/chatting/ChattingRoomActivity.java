package com.unilab.uniting.activities.chatting;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;
import com.unilab.uniting.R;
import com.unilab.uniting.activities.profilecard.ProfileCardChattingActivity;
import com.unilab.uniting.adapter.chatting.ChatMessageAdapter;
import com.unilab.uniting.fragments.dialog.DialogBlockFragment;
import com.unilab.uniting.fragments.dialog.DialogMoreFragment;
import com.unilab.uniting.fragments.dialog.DialogNoOkFragment;
import com.unilab.uniting.model.ChatMessage;
import com.unilab.uniting.model.ChatRoom;
import com.unilab.uniting.model.Fcm;
import com.unilab.uniting.model.User;
import com.unilab.uniting.utils.BasicActivity;
import com.unilab.uniting.utils.DateUtil;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.LogData;
import com.unilab.uniting.utils.MyProfile;
import com.unilab.uniting.utils.Numbers;
import com.unilab.uniting.utils.OnSingleClickListener;
import com.unilab.uniting.utils.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChattingRoomActivity extends BasicActivity implements SwipeRefreshLayout.OnRefreshListener, DialogBlockFragment.BlockListener, DialogNoOkFragment.DialogOkListener {

    //TAG
    final static String TAG = "ChattingRoomActivityT";
    private FirebaseFunctions mFunctions;

    //xml
    private RecyclerView mRecyclerView;
    private CircleImageView mPartnerCircleImageView;
    private TextView mPartnerNickNameTextView;
    private EditText mWriteEditText;
    private ImageView mSendImageView;
    private LinearLayout mBack;
    private LinearLayout mMore;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RelativeLayout mLoaderLayout;
    private FloatingActionButton mVoiceTalkButton;

    //value
    private User mPartnerUser;
    private ChatRoom mChatRoom;
    private ChatMessageAdapter mChatMessageAdapter;
    private ArrayList<ChatMessage> mChatMessageList;
    private String mPartnerPhotoUrl;
    private String mPartnerUid;
    private String mPartnerNickName;
    private String mReportLoaction = "";
    private boolean isLoading = false;
    private boolean isRefreshing = false;
    private boolean isLastMessage = false;
    private boolean isEndMessageAdded = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting_room);

        init();
        initAdapter();
        checkRefund();
        setOnClickListener();
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }


    private void checkRefund() {
        mFunctions = FirebaseFunctions.getInstance(Strings.region_asia);
        if(!mChatRoom.getUidPaid().equals(FirebaseHelper.mUid)){
            mLoaderLayout.setVisibility(View.GONE);
            return;
        }

        if(mChatRoom.getCreateTimestamp() > DateUtil.getUnixTimeLong() - 3 * DateUtil.dayInMilliSecond) {
            mLoaderLayout.setVisibility(View.GONE);
            return;
        }

        if(mChatRoom.getRecentTimestamp() > DateUtil.getUnixTimeLong() - 2 * DateUtil.dayInMilliSecond){
            mLoaderLayout.setVisibility(View.GONE);
            return;
        }

        refundTask()
                .addOnCompleteListener(new OnCompleteListener<Map<String,Boolean>>() {
                    @Override
                    public void onComplete(@NonNull Task<Map<String,Boolean>> task) {
                        mLoaderLayout.setVisibility(View.GONE);
                        if (!task.isSuccessful()) {
                            Exception e = task.getException();
                            if (e instanceof FirebaseFunctionsException) {
                                FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                FirebaseFunctionsException.Code code = ffe.getCode();
                                Object details = ffe.getDetails();
                            }
                        } else {
                            Map<String,Boolean> taskResult = task.getResult();
                            if (taskResult != null & taskResult.get(FirebaseHelper.isRefunded) != null){
                                mChatRoom.setRefunded(taskResult.get(FirebaseHelper.isRefunded));
                                mChatMessageAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });

    }

    private Task<Map<String, Boolean>> refundTask() {
        // Create the arguments to the callable function.
        Map<String, String> data = new HashMap<>();
        data.put(Strings.partnerUid, mPartnerUid);
        data.put(FirebaseHelper.roomId, mChatRoom.getRoomId());

        return mFunctions
                .getHttpsCallable(FirebaseHelper.checkChatRoomRefund)
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, Map<String,Boolean>>() {
                    @Override
                    public Map<String,Boolean> then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        Map<String,Boolean> result = (Map<String,Boolean>) task.getResult().getData();
                        Log.d("test111222","중간" + result);
                        if (result != null & result.get(FirebaseHelper.isRefunded) != null){
                            if(result.get(FirebaseHelper.isRefunded)){
                                mChatRoom.setRefunded(result.get(FirebaseHelper.isRefunded));
                                mChatMessageAdapter = new ChatMessageAdapter(mChatMessageList, getApplicationContext(), mPartnerUser, mChatRoom, mLoaderLayout);
                                mRecyclerView.setAdapter(mChatMessageAdapter);
                                mChatMessageAdapter.notifyDataSetChanged();
                            }
                        }
                        return result;
                    }
                });
    }


    private void init(){
        mRecyclerView = findViewById(R.id.chatting_chatroom_recycler);
        mPartnerCircleImageView = findViewById(R.id.toolbar_profile);
        mPartnerNickNameTextView = findViewById(R.id.toolbar_nickname);
        mWriteEditText = findViewById(R.id.chatting_chatroom_et_write);
        mSendImageView = findViewById(R.id.chatting_chatroom_iv_send);
        mBack = findViewById(R.id.toolbar_back);
        mMore = findViewById(R.id.toolbar_more);
        mSwipeRefreshLayout = findViewById(R.id.chatting_chatroom_swiperefresh);
        mVoiceTalkButton = findViewById(R.id.chatting_chatroom_fab_voice_talk);


        //로딩, 툴바 레이아웃
        mLoaderLayout = findViewById(R.id.loaderLayout);
        mLoaderLayout.setClickable(true);

        Intent intent = getIntent();
        mChatRoom = (ChatRoom) intent.getSerializableExtra(Strings.EXTRA_CHATROOM_ID);

        //파트너 유저 객체
        if(mChatRoom.getUser0().getUid().equals(FirebaseHelper.mUid)){
            mPartnerUser = mChatRoom.getUser1();
        }else{
            mPartnerUser = mChatRoom.getUser0();
        }

        mPartnerUid = mPartnerUser.getUid();
        mPartnerNickName = mPartnerUser.getNickname();
        mPartnerNickNameTextView.setText(mPartnerUser.getNickname());

        if (mPartnerUser != null && (!mPartnerUser.getPhotoUrl().isEmpty()) && mPartnerUser.getPhotoUrl().get(0) != null) {
            mPartnerPhotoUrl = mPartnerUser.getPhotoUrl().get(0);
            Glide.with(ChattingRoomActivity.this).load(mPartnerPhotoUrl).into(mPartnerCircleImageView);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        chatMessageListener();
        chatRoomListener();
    }


    private void initAdapter() {
        mChatMessageList = new ArrayList<>();
        mChatMessageAdapter = new ChatMessageAdapter(mChatMessageList, getApplicationContext(), mPartnerUser, mChatRoom, mLoaderLayout);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mChatMessageAdapter);
    }

    private void setOnClickListener(){
        mSendImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                mReportLoaction = "채팅";
                bundle.putString(Strings.EXTRA_LOCATION, mReportLoaction);
                bundle.putSerializable(Strings.EXTRA_CHATTING, mChatRoom);

                DialogMoreFragment dialog = DialogMoreFragment.getInstance();
                dialog.setArguments(bundle);
                dialog.show(getSupportFragmentManager(), DialogMoreFragment.TAG_EVENT_DIALOG);
            }
        });

        mVoiceTalkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mChatRoom.isDeleted()){
                    Toast.makeText(ChattingRoomActivity.this, "채팅이 종료되었습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Bundle bundle = new Bundle();
                bundle.putString(Strings.title, "보이스톡 해요!");
                bundle.putString(Strings.content, "보이스톡은 최대 10분까지 가능합니다. 오픈 이벤트 종료 후 이용방법이 변경될 수 있습니다.");
                bundle.putString(Strings.ok, "시작하기");
                bundle.putString(Strings.no, "취소");
                DialogNoOkFragment dialog = DialogNoOkFragment.getInstance();
                dialog.setArguments(bundle);
                dialog.show(getSupportFragmentManager(), DialogNoOkFragment.TAG_EVENT_DIALOG);

                Bundle eventBundle = new Bundle();
                eventBundle.putInt(LogData.dia, 0);
                eventBundle.putString(LogData.partnerUid, mPartnerUid);
                eventBundle.putString(LogData.roomId, mChatRoom.getRoomId());
                LogData.customLog(LogData.ti_s13_pre_chat_voice_talk, eventBundle, ChattingRoomActivity.this);
                LogData.setStageTodayIntro(LogData.ti_s13_pre_chat_voice_talk, ChattingRoomActivity.this);

            }
        });

        mPartnerCircleImageView.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(ChattingRoomActivity.this, ProfileCardChattingActivity.class);
                intent.putExtra(Strings.partnerUid, mPartnerUser.getUid());
                intent.putExtra(Strings.partnerUser, mPartnerUser);
                startActivity(intent);
            }
        });
    }

    private void chatMessageListener(){
        mChatMessageList.clear();
        FirebaseHelper.db.collection("ChatRoom").document(mChatRoom.getRoomId()).collection("Message").orderBy("timestamp", Query.Direction.DESCENDING).limit(Numbers.LOADING_MESSAGE)
                .addSnapshotListener(ChattingRoomActivity.this, new EventListener<QuerySnapshot>() {  //채팅 메세지가 많아질 때를 대비해서 'THE_NUMBER_OF_MESSAGE' 개씩 끊어서 가져옴.
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }
                        for(int i = snapshots.getDocumentChanges().size() - 1; i >= 0 ; i--){ //최근 메세지부터 내림차순이라서, list에 오래된거부터 넣어줌.
                            DocumentChange dc = snapshots.getDocumentChanges().get(i);
                            switch (dc.getType()) {
                                case ADDED:
                                    ChatMessage chatMessage = dc.getDocument().toObject(ChatMessage.class);
                                    mChatMessageList.add(chatMessage);
                                    break;
                                case MODIFIED:
                                    Log.d("test999", "modify");
                                    break;
                                case REMOVED:
                                    Log.d("test999", "removed");
                                    break;
                            }
                        }

                        mChatMessageAdapter.notifyDataSetChanged();
                        mRecyclerView.scrollToPosition(mChatMessageAdapter.getItemCount() - 1 );

                        for(int i = snapshots.getDocumentChanges().size() - 1; i >= 0 ; i--){ //최근 메세지부터 내림차순이라서, list에 오래된거부터 넣어줌.
                            DocumentChange dc = snapshots.getDocumentChanges().get(i);
                            switch (dc.getType()) {
                                case ADDED:
                                    ChatMessage chatMessage = dc.getDocument().toObject(ChatMessage.class);
                                    if(!chatMessage.getSenderUid().equals(FirebaseHelper.mUid)){
                                        Bundle eventBundle = new Bundle();
                                        eventBundle.putInt(LogData.dia, 0);
                                        eventBundle.putString(LogData.partnerUid, mPartnerUid);
                                        eventBundle.putString(LogData.roomId, mChatRoom.getRoomId());
                                        LogData.customLog(LogData.ti_s12_chat_receive_message,  eventBundle, ChattingRoomActivity.this);
                                        LogData.setStageTodayIntro(LogData.ti_s12_chat_receive_message, ChattingRoomActivity.this);
                                        return;
                                    }
                                    break;
                                case MODIFIED:
                                    Log.d("test999", "modify");
                                    break;
                                case REMOVED:
                                    Log.d("test999", "removed");
                                    break;
                            }
                        }

                    }
                });
    }

    private void chatRoomListener(){
        FirebaseHelper.db.collection("ChatRoom").document(mChatRoom.getRoomId())
        .addSnapshotListener(ChattingRoomActivity.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    ChatRoom chatRoom = snapshot.toObject(ChatRoom.class);
                    if(!mChatRoom.isDeleted() && chatRoom.isDeleted()){
                        mLoaderLayout.setVisibility(View.VISIBLE);
                        checkRefund();
                    }

                    mChatRoom = chatRoom;
                    mChatMessageAdapter.notifyDataSetChanged();
                    if(mChatRoom.isVoiceTalkOn()){
                        Intent intent = new Intent(ChattingRoomActivity.this, VoiceTalkActivity.class);
                        intent.putExtra(Strings.EXTRA_CHATROOM_ID, mChatRoom);
                        startActivity(intent);
                    }
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        if (isRefreshing) {
            return;
        }

        if (isLastMessage){
            Toast.makeText(ChattingRoomActivity.this, "더이상 대화 내용이 없습니다.", Toast.LENGTH_SHORT).show();
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }

        isRefreshing = true;
        ArrayList<ChatMessage> mChatMessageTempList = new ArrayList<>();
        long timestampOfTop = mChatMessageList.get(0).getTimestamp();
        FirebaseHelper.db.collection("ChatRoom").document(mChatRoom.getRoomId()).collection("Message").orderBy("timestamp", Query.Direction.DESCENDING).startAfter(timestampOfTop).limit(Numbers.LOADING_MESSAGE)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {  //채팅 메세지가 많아질 때를 대비해서 20개씩 끊어서 가져옴.
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for(int i = task.getResult().size() - 1; i >= 0 ; i--){ //최근 메세지부터 내림차순이라서, list에 오래된거부터 넣어줌.
                                DocumentSnapshot snapshot = task.getResult().getDocuments().get(i);
                                ChatMessage chatMessage = snapshot.toObject(ChatMessage.class);
                                if (chatMessage.getAdmin() != null && chatMessage.getAdmin().length() > 0) {
                                    isLastMessage = true;
                                }
                                mChatMessageTempList.add(snapshot.toObject(ChatMessage.class));
                            }
                            mChatMessageList.addAll(0, mChatMessageTempList);
                            mChatMessageAdapter.notifyDataSetChanged();
                            mRecyclerView.scrollToPosition(mChatMessageTempList.size());
                            mSwipeRefreshLayout.setRefreshing(false);
                            isRefreshing = false;

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    //메세지전송
    private void sendMessage(){
        //2줄 이상의 줄바꿈, 양옆 빈칸 없앰
        String message = mWriteEditText.getText().toString().trim();
        if(mChatRoom.isDeleted()){
            Toast.makeText(this, "채팅이 종료되었습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(message.length() > 0){
            if(!isLoading){
                isLoading = true;
                mWriteEditText.setText("");
                long timestamp = DateUtil.getUnixTimeLong();
                Map<String, Object> messageData = new HashMap<>();
                messageData.put(FirebaseHelper.recentTimestamp, timestamp );
                messageData.put(FirebaseHelper.recentMessage, message);
                messageData.put(FirebaseHelper.senderUid, FirebaseHelper.mUid);

                DocumentReference messageRef = FirebaseHelper.db.collection("ChatRoom").document(mChatRoom.getRoomId()).collection("Message").document();
                String messageId = messageRef.getId();

                ChatMessage chatMessage = new ChatMessage(messageId, mChatRoom.getRoomId(), timestamp, DateUtil.getDateSec(), message, MyProfile.getUser().getNickname(), FirebaseHelper.mUid, mPartnerUser.getNickname(), mPartnerUid, "");
                Fcm fcm = new Fcm(mPartnerUid, MyProfile.getUser().getNickname(),  message, mChatRoom, mChatRoom.getRoomId(), FirebaseHelper.chatting, FirebaseHelper.sendMessage, DateUtil.getDateSec());
                messageRef.set(chatMessage)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Bundle eventBundle = new Bundle();
                                eventBundle.putInt(LogData.dia, 0);
                                eventBundle.putString(LogData.partnerUid, mPartnerUid);
                                eventBundle.putString(LogData.roomId, mChatRoom.getRoomId());
                                LogData.customLog(LogData.ti_s11_chat_send_message, eventBundle, ChattingRoomActivity.this);
                                LogData.setStageTodayIntro(LogData.ti_s11_chat_send_message, ChattingRoomActivity.this);

                                FirebaseHelper.db.collection("ChatRoom").document(mChatRoom.getRoomId()).update(messageData); //초당 1회 업데이트
                                FirebaseHelper.db.collection("Users").document(mPartnerUid).collection("Fcm").document().set(fcm); //메세지랑 batch로 넣어도 될꺼같긴한데 아주 조금의 속도 단축 있을 수도 있어서 따로 넣음.
                                isLoading = false;
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChattingRoomActivity.this, "네트워크가 불안정합니다.", Toast.LENGTH_SHORT).show();
                        isLoading = false;
                    }
                });

            }
        }
    }

    @Override
    public void block() {
        FirebaseHelper.blockUser(this, mPartnerUser, mLoaderLayout, true);
    }

    @Override
    public void ok() {
        if (mChatRoom.getCallingTime() > 630000){
            Toast.makeText(ChattingRoomActivity.this, "10분의 통화 시간이 모두 사용되었습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> chatRoom = new HashMap<>();
        chatRoom.put(FirebaseHelper.voiceTalkOn, true);
        chatRoom.put(FirebaseHelper.callerUid, FirebaseHelper.mUid);
        chatRoom.put(FirebaseHelper.recentMessage, "보이스톡 해요");
        chatRoom.put(FirebaseHelper.recentTimestamp, DateUtil.getUnixTimeLong());

        DocumentReference messageRef = FirebaseHelper.db.collection("ChatRoom").document(mChatRoom.getRoomId()).collection("Message").document();
        String messageId = messageRef.getId();

        ChatMessage message = new ChatMessage(messageId, mChatRoom.getRoomId(), DateUtil.getUnixTimeLong(), DateUtil.getDateSec(), "보이스톡 해요", MyProfile.getUser().getNickname(), FirebaseHelper.mUid, mPartnerUser.getNickname(), mPartnerUid, "");
        WriteBatch batch = FirebaseHelper.db.batch();
        batch.update(FirebaseHelper.db.collection("ChatRoom").document(mChatRoom.getRoomId()), chatRoom);
        batch.set(messageRef, message);
        mLoaderLayout.setVisibility(View.VISIBLE);
        batch.commit().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ChattingRoomActivity.this, "네트워크 오류. 재시도해주세요.", Toast.LENGTH_SHORT).show();
                mLoaderLayout.setVisibility(View.GONE);
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mLoaderLayout.setVisibility(View.GONE);
            }
        });
    }

}
