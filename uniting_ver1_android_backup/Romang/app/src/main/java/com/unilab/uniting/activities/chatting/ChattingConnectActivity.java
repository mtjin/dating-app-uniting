package com.unilab.uniting.activities.chatting;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.unilab.uniting.R;
import com.unilab.uniting.activities.MainActivity;
import com.unilab.uniting.activities.profilecard.ProfileCardChattingActivity;
import com.unilab.uniting.fragments.chatting.DialogChattingStartFragment;
import com.unilab.uniting.fragments.dialog.DialogBlockFragment;
import com.unilab.uniting.fragments.dialog.DialogMoreFragment;
import com.unilab.uniting.fragments.dialog.DialogOkFragment;
import com.unilab.uniting.model.ChatRoom;
import com.unilab.uniting.model.Dia;
import com.unilab.uniting.model.Fcm;
import com.unilab.uniting.model.Meeting;
import com.unilab.uniting.model.Notification;
import com.unilab.uniting.model.User;
import com.unilab.uniting.utils.BasicActivity;
import com.unilab.uniting.utils.DateUtil;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.LogData;
import com.unilab.uniting.utils.MyProfile;
import com.unilab.uniting.utils.Numbers;
import com.unilab.uniting.utils.OnSingleClickListener;
import com.unilab.uniting.utils.Strings;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChattingConnectActivity extends BasicActivity implements DialogChattingStartFragment.OpenChatListener,  DialogBlockFragment.BlockListener {

    //상수 변수
    final static String TAG = "CHATTING_CONNECT_TAG";

    //변수
    private String mLocation = "채팅";
    private ChatRoom mChatRoom;
    private String mPartnerUid;
    private User mPartnerUser;
    private Meeting mMeeting;
    private Dia dia;
    private boolean isLoading = false;

    private boolean isChatRoomChecked = false;
    private boolean isDiaChecked = false;
    private boolean freeMatching = false;
    private boolean refundedFreeMatching = false;

    //xml
    private LinearLayout mBackLinearLayout;
    private LinearLayout mMoreLinearLayout;
    private Button mOpenChatButton;
    private CircleImageView mPartnerCircleImageView;
    private CircleImageView mPartnerToolBarCircleImageView;
    private TextView mPartnerNickNameTextView;
    private TextView mPartnerNickNameToolBarTextView;
    private TextView mDateTextView;
    private Button mExitButton;
    private TextView mGuideTitleTextView;
    private TextView mGuideContentTextView;
    private RelativeLayout loaderLayout;

    View.OnClickListener onClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting_connect);

        init();
        setOnClickListener();


    }

    @Override
    protected void onStart() {
        super.onStart();
        chattingStateListener();
    }

    private void init() {
        mBackLinearLayout = findViewById(R.id.toolbar_back);
        mMoreLinearLayout = findViewById(R.id.toolbar_more);
        mOpenChatButton = findViewById(R.id.chatting_chatconnect_btn_openchat);
        mPartnerCircleImageView = findViewById(R.id.chatting_connect_civ_profile);
        mPartnerToolBarCircleImageView = findViewById(R.id.toolbar_profile);
        mPartnerNickNameTextView = findViewById(R.id.chatting_chatconnect_tv_nickname);
        mPartnerNickNameToolBarTextView = findViewById(R.id.toolbar_nickname);

        mDateTextView = findViewById(R.id.chatting_chatconnect_tv_date);
        mExitButton = findViewById(R.id.chatting_chatconnect_btn_exit);
        mGuideTitleTextView = findViewById(R.id.chatting_chatconnect_tv_guide_title);
        mGuideContentTextView = findViewById(R.id.chatting_chatconnect_tv_guide_content);


        //로딩중
        loaderLayout = findViewById(R.id.loaderLayout);
        loaderLayout.setClickable(true);

        Intent intent = getIntent();
        mChatRoom = (ChatRoom) intent.getSerializableExtra(Strings.EXTRA_CHATROOM_ID);

        //파트너 유저 객체
        if (mChatRoom.getUser0().getUid().equals(FirebaseHelper.mUid)) {
            mPartnerUser = mChatRoom.getUser1();
        } else {
            mPartnerUser = mChatRoom.getUser0();
        }

        mPartnerUid = mPartnerUser.getUid();

        //뷰 세팅
        long nowTimestamp = DateUtil.getUnixTimeLong(); //현재 시간 millisecond 설정
        long createUnixTime = mChatRoom.getCreateTimestamp(); //채팅방 만들어진 시간
        mDateTextView.setText(DateUtil.getDayFromNow(createUnixTime));

        mGuideTitleTextView.setText("오늘의 소개");
        mGuideContentTextView.setText("1. 대화 시작 버튼을 누르면 바로 채팅을 시작할 수 있습니다. \n 2. 상대방이 72시간 동안 답이 없을 경우 사용한 다이아 또는 대화 열기를 보상해드립니다. \n 무료 시작 기능은 사용 4일 후에 충전됩니다.");
        mOpenChatButton.setText("대화 시작");

        mPartnerNickNameTextView.setText(mPartnerUser.getNickname() + "님과 연결되었습니다.");
        mPartnerNickNameToolBarTextView.setText(mPartnerUser.getNickname());
        if ((!mPartnerUser.getPhotoUrl().isEmpty()) && mPartnerUser.getPhotoUrl().get(0) != null) {
            String mPartnerPhotoUrl = mPartnerUser.getPhotoUrl().get(0);
            if (!ChattingConnectActivity.this.isDestroyed()) {
                Glide.with(ChattingConnectActivity.this).load(mPartnerPhotoUrl).into(mPartnerCircleImageView);
                Glide.with(ChattingConnectActivity.this).load(mPartnerPhotoUrl).into(mPartnerToolBarCircleImageView);
            }
        }
    }

    private void chattingStateListener() {
        loaderLayout.setVisibility(View.VISIBLE);

        FirebaseHelper.db.collection("ChatRoom").document(mChatRoom.getRoomId())
                .addSnapshotListener(ChattingConnectActivity.this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            loaderLayout.setVisibility(View.GONE);
                            return;
                        }

                        if (snapshot != null && snapshot.exists()) {
                            mChatRoom = snapshot.toObject(ChatRoom.class);
                        } else {
                            Log.d(TAG, "Current data: null");
                        }

                        isChatRoomChecked = true;
                        setStateView();
                    }
                });


        FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("Dia").orderBy(FirebaseHelper.diaId, Query.Direction.DESCENDING).limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            //그럴 일은 없지만 하트 기록이 없는 경우
                            if (task.getResult() == null) {
                                setStateView();
                                return;
                            }

                            //하트 세팅
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                dia = document.toObject(Dia.class);
                            }

                            isDiaChecked = true;
                            setFreeMatching();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            Toast.makeText(ChattingConnectActivity.this, "오류 발생", Toast.LENGTH_SHORT).show();
                            loaderLayout.setVisibility(View.GONE);
                        }

                    }
                });
    }

    private void setFreeMatching(){
        try{
            long lastFreeChatTime = dia.getRecentFreeChatTime();
            if(lastFreeChatTime < DateUtil.getUnixTimeLong() - 4 * 24 * 60 * 60 * 1000){ //4일마다 충전
                freeMatching = true;
            } else {
                freeMatching = false;
            }

            int refundedFreeChat = dia.getCurrentRefundedChat();

            if(refundedFreeChat > 0){
                refundedFreeMatching = true;
            } else {
                refundedFreeMatching = false;
            }
        }catch (Exception e){
            freeMatching = false;
            refundedFreeMatching = false;
        }

        if (freeMatching || refundedFreeMatching) {
            mOpenChatButton.setText("무료 대화 시작");
        }

        if (isDiaChecked && isChatRoomChecked) {
            loaderLayout.setVisibility(View.GONE);
        }
    }

    private void setStateView() {
        if (mChatRoom.isDeleted()) { //종료된 경우 종료버튼으로 변경
            mExitButton.setVisibility(View.VISIBLE);
            mOpenChatButton.setVisibility(View.GONE);
            mGuideContentTextView.setText("상대방이 매칭을 해제하였습니다.");
        } else if (mChatRoom.getFrom().equals(FirebaseHelper.todayIntro)) { //todayIntro에서 온 경우 심플.


            if (mChatRoom.isStarted()) {
                Intent intent2 = new Intent(ChattingConnectActivity.this, ChattingRoomActivity.class);
                intent2.putExtra(Strings.EXTRA_CHATROOM_ID, mChatRoom);
                intent2.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent2);
                finish();
            } else {
                mOpenChatButton.setVisibility(View.VISIBLE);
            }
        }

        if (isDiaChecked && isChatRoomChecked) {
            loaderLayout.setVisibility(View.GONE);
        }
    }


    private void setOnClickListener() {
        onClickListener = new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                switch (v.getId()) {
                    case R.id.toolbar_back:
                        onBackPressed();
                        break;
                    case R.id.toolbar_more:
                        Bundle bundle = new Bundle();
                        bundle.putString(Strings.EXTRA_LOCATION, mLocation);
                        bundle.putSerializable(Strings.EXTRA_CHATTING, mChatRoom);
                        DialogMoreFragment dialog = DialogMoreFragment.getInstance();
                        dialog.setArguments(bundle);
                        dialog.show(getSupportFragmentManager(), DialogMoreFragment.TAG_EVENT_DIALOG);
                        break;
                    case R.id.chatting_chatconnect_btn_openchat:
                        if (mChatRoom.isDeleted()) {
                            Bundle bundle3 = new Bundle();
                            bundle3.putString(Strings.EXTRA_LOCATION, Strings.isChattingDeleted);
                            DialogOkFragment dialog3 = DialogOkFragment.getInstance();
                            dialog3.setArguments(bundle3);
                            dialog3.show(getSupportFragmentManager(), DialogOkFragment.TAG_MEETING_DIALOG2);
                        } else if (mChatRoom.getFrom().equals(FirebaseHelper.todayIntro)) {//todayIntro에서 온 경우
                            if (mChatRoom.isStarted()) {
                                Intent intent2 = new Intent(ChattingConnectActivity.this, ChattingRoomActivity.class);
                                intent2.putExtra(Strings.EXTRA_CHATROOM_ID, mChatRoom);
                                intent2.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                startActivity(intent2);
                                finish();
                            } else {
                                if(freeMatching || refundedFreeMatching){
                                    Bundle eventBundle = new Bundle();
                                    eventBundle.putInt(LogData.dia, 0);
                                    eventBundle.putString(LogData.partnerUid, mPartnerUid);
                                    eventBundle.putString(LogData.roomId, mChatRoom.getRoomId());
                                    LogData.customLog(LogData.ti_s09_pre_chat_start, eventBundle, ChattingConnectActivity.this);
                                    LogData.setStageTodayIntro(LogData.ti_s09_pre_chat_start, ChattingConnectActivity.this);

                                    openFreeChatting();
                                } else {
                                    Bundle eventBundle = new Bundle();
                                    eventBundle.putInt(LogData.dia, Numbers.OPEN_CHATTING_COST);
                                    eventBundle.putString(LogData.partnerUid, mPartnerUid);
                                    eventBundle.putString(LogData.roomId, mChatRoom.getRoomId());
                                    LogData.customLog(LogData.ti_s09_pre_chat_start,  eventBundle, ChattingConnectActivity.this);
                                    LogData.setStageTodayIntro(LogData.ti_s09_pre_chat_start, ChattingConnectActivity.this);

                                    Bundle bundle2 = new Bundle();
                                    bundle2.putString(Strings.EXTRA_LOCATION, mLocation);
                                    bundle2.putSerializable(Strings.EXTRA_CHATTING, mChatRoom);
                                    DialogChattingStartFragment dialog2 = DialogChattingStartFragment.getInstance();
                                    dialog2.setArguments(bundle2);
                                    dialog2.show(getSupportFragmentManager(), DialogChattingStartFragment.TAG_MEETING_DIALOG3);
                                }
                            }
                        }
                        break;
                    case R.id.chatting_connect_civ_profile:
                        Intent intent = new Intent(ChattingConnectActivity.this, ProfileCardChattingActivity.class);
                        intent.putExtra(Strings.partnerUid, mPartnerUid);
                        intent.putExtra(Strings.partnerUser, mPartnerUser);
                        startActivity(intent);
                        break;
                    case R.id.chatting_chatconnect_btn_exit:
                        if (!isLoading) {
                            isLoading = true;
                            loaderLayout.setVisibility(View.VISIBLE);
                            FirebaseHelper.db.collection("ChatRoom").document(mChatRoom.getRoomId()).update(FirebaseHelper.uidBanned, FirebaseHelper.mUid)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Intent intentHome = new Intent(ChattingConnectActivity.this, MainActivity.class);
                                            intentHome.putExtra(Strings.defaultPage, 0);
                                            intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            intentHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intentHome);
                                            finish();
                                            isLoading = false;
                                            loaderLayout.setVisibility(View.GONE);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ChattingConnectActivity.this, "네트워크가 불안정합니다", Toast.LENGTH_SHORT).show();
                                    isLoading = false;
                                    loaderLayout.setVisibility(View.GONE);
                                }
                            });
                        }

                        break;
                }
            }
        };

        mBackLinearLayout.setOnClickListener(onClickListener);
        mMoreLinearLayout.setOnClickListener(onClickListener);
        mPartnerCircleImageView.setOnClickListener(onClickListener);
        mOpenChatButton.setOnClickListener(onClickListener);
        mExitButton.setOnClickListener(onClickListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Remove the activity when its off the screen
        finish();
    }


    @Override
    public void openChatting() {  //채팅 열건지 물어보는 dialog
        if(dia == null){
            Toast.makeText(ChattingConnectActivity.this, "오류 발생", Toast.LENGTH_SHORT).show();
            loaderLayout.setVisibility(View.GONE);
            return;
        }

        //하트 수 부족한 경우
        if (dia.getCurrentDia() < Numbers.OPEN_CHATTING_COST) {
            Toast.makeText(ChattingConnectActivity.this, "다이아가 부족합니다.", Toast.LENGTH_SHORT).show();
            loaderLayout.setVisibility(View.GONE);
            return;
        }

        openFreeChatting();
    }

    private void openFreeChatting(){
        loaderLayout.setVisibility(View.VISIBLE);
        Fcm fcm = new Fcm(mPartnerUid, MyProfile.getUser().getNickname(), "채팅을 시작하세요!", mChatRoom, mChatRoom.getRoomId(), FirebaseHelper.chatting, FirebaseHelper.openChatting, DateUtil.getDateSec());
        Notification notification = new Notification("채팅연결", mChatRoom, DateUtil.getDateMin(), DateUtil.getUnixTimeLong(), MyProfile.getUser().getNickname(), "", mPartnerUid, false); //상대 노티피케이션 DB에 저장

        //DB로 정보 전송 (하트 내역 업데이트, 유저doc 업데이트)
        WriteBatch batch = FirebaseHelper.db.batch();
        Dia updatedDia;
        if(refundedFreeMatching){
            updatedDia = dia.useRefundedFreeChat(mChatRoom.getInteractionId(), mPartnerUid, mChatRoom.getRoomId());
        } else if (freeMatching) {
            updatedDia = dia.useFreeChat(mChatRoom.getInteractionId(), mPartnerUid, mChatRoom.getRoomId());
        } else {
            //하트 변동 내역
            updatedDia = dia.getUpdatedDia(Numbers.OPEN_CHATTING_COST, 0, 0, 0, Strings.OPEN_CHATTING_COST, mChatRoom.getInteractionId(),  mPartnerUid, "", mChatRoom.getRoomId());
        }
        batch.set(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("Dia").document(updatedDia.getDiaId()), updatedDia);
        batch.update(FirebaseHelper.db.collection("ChatRoom").document(mChatRoom.getRoomId()),
                FirebaseHelper.started, true,
                FirebaseHelper.uidPaid, FirebaseHelper.mUid,
                FirebaseHelper.senderUid, FirebaseHelper.mUid);
        batch.update(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("InteractionHistory").document(FirebaseHelper.mUid),
                FirebaseHelper.startChatList, FieldValue.arrayUnion(mPartnerUid));
        batch.update(FirebaseHelper.db.collection("Users").document(mPartnerUid).collection("InteractionHistory").document(mPartnerUid),
                FirebaseHelper.chatStartedList, FieldValue.arrayUnion(FirebaseHelper.mUid));

        batch.set(FirebaseHelper.db.collection("Users").document(mPartnerUid).collection("Notification").document(DateUtil.getTimeStampUnix()), notification);

        batch.set(FirebaseHelper.db.collection("Users").document(mPartnerUid).collection("Fcm").document(), fcm);
        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if(freeMatching || refundedFreeMatching){
                    Bundle eventBundle = new Bundle();
                    eventBundle.putInt(LogData.dia, 0);
                    eventBundle.putString(LogData.partnerUid, mPartnerUid);
                    eventBundle.putString(LogData.roomId, mChatRoom.getRoomId());
                    LogData.customLog(LogData.ti_s10_chat_start,  eventBundle, ChattingConnectActivity.this);
                    LogData.setStageTodayIntro(LogData.ti_s10_chat_start, ChattingConnectActivity.this);
                } else {
                    Bundle eventBundle = new Bundle();
                    eventBundle.putInt(LogData.dia, Numbers.OPEN_CHATTING_COST);
                    eventBundle.putString(LogData.partnerUid, mPartnerUid);
                    eventBundle.putString(LogData.roomId, mChatRoom.getRoomId());
                    LogData.customLog(LogData.ti_s10_chat_start, eventBundle, ChattingConnectActivity.this);
                    LogData.setStageTodayIntro(LogData.ti_s10_chat_start, ChattingConnectActivity.this);
                }

                loaderLayout.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, e.toString());
                Toast.makeText(ChattingConnectActivity.this, "네트워크가 불안정합니다", Toast.LENGTH_SHORT).show();
                loaderLayout.setVisibility(View.GONE);
            }
        });
    }




    @Override
    public void block() {
        Bundle eventBundle = new Bundle();
        eventBundle.putInt(LogData.dia, 0);
        eventBundle.putString(LogData.partnerUid, mPartnerUid);
        eventBundle.putString(LogData.roomId, mChatRoom.getRoomId());
        LogData.customLog(LogData.block,  eventBundle, ChattingConnectActivity.this);

        FirebaseHelper.blockUser(this, mPartnerUser,loaderLayout, true);
    }
}
