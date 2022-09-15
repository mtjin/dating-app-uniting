package com.unilab.uniting.adapter.chatting;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.unilab.uniting.R;
import com.unilab.uniting.activities.MainActivity;
import com.unilab.uniting.activities.profilecard.ProfileCardChattingActivity;
import com.unilab.uniting.model.ChatMessage;
import com.unilab.uniting.model.ChatRoom;
import com.unilab.uniting.model.RefundedDia;
import com.unilab.uniting.model.User;
import com.unilab.uniting.utils.DateUtil;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.Numbers;
import com.unilab.uniting.utils.OnSingleClickListener;
import com.unilab.uniting.utils.Strings;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    final static String TAG = "ChatMessageAdapterT";

    private Context context;
    private ArrayList<ChatMessage> chatMessageArrayList;

    private Date date;
    private long nowTimestamp;
    private ArrayList<String> dateArrayList = new ArrayList<>();
    private ArrayList<String> dayFromNowList;
    private User partnerUser;
    private String partnerPhotoUrl;
    private ChatRoom chatRoom;
    private RelativeLayout loaderLayout;



    public ChatMessageAdapter(ArrayList<ChatMessage> chatMessageArrayList, Context context, User partnerUser, ChatRoom chatRoom, RelativeLayout loaderLayout) {
        this.chatMessageArrayList = chatMessageArrayList;
        this.context = context;
        this.partnerUser = partnerUser;
        this.chatRoom = chatRoom;
        this.nowTimestamp = DateUtil.getUnixTimeLong();
        this.loaderLayout = loaderLayout;
    }

    @Override
    public int getItemCount() {
        return chatMessageArrayList.size();
    }

    public void addItem(ChatMessage item) {
        chatMessageArrayList.add(item);
    }

    public void clear() {
        chatMessageArrayList.clear();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_chatmessage_center, parent, false);
                return new ChatCenterMessageViewHolder(view);

            case 1:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_chatmessage_right, parent, false);
                return new ChatRightMessageViewHolder(view);
            case 2:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_chatmessage_left, parent, false);
                return new ChatLeftMessageViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = chatMessageArrayList.get(position);
        dayFromNowList = getDayFromNowList(chatMessageArrayList);

        if (message.getSenderNickname().equals("운영자")) {
            ChatCenterMessageViewHolder centerHolder = (ChatCenterMessageViewHolder) holder;
            if(message.getAdmin().equals(ChatMessage.end)){
                centerHolder.dateTextView.setVisibility(View.GONE);
                centerHolder.nickNameTextView.setText("대화가 종료되었습니다.");
                centerHolder.exitButton.setVisibility(View.VISIBLE);
                if(chatRoom.isRefunded()){
                    centerHolder.dateTextView.setVisibility(View.VISIBLE);
                    centerHolder.dateTextView.setText("상대방으로부터 응답이 없어 다이아룰 보상해드립니다.");
                    centerHolder.exitButton.setText("채팅 종료 후 보상 받기");
                }
            }else{
                centerHolder.exitButton.setVisibility(View.GONE);
                centerHolder.dateTextView.setText(DateUtil.getUnixToDate(message.getTimestamp()));
                centerHolder.nickNameTextView.setText(partnerUser.getNickname() + "님과 대화를 시작하세요.");
            }
        } else if (message.getSenderUid().equals(FirebaseHelper.mUid)) { //내매세지
            ChatRightMessageViewHolder rightHolder = (ChatRightMessageViewHolder) holder;
            if (dayFromNowList.get(position) != null) {
                rightHolder.lastDayTextView.setText(dayFromNowList.get(position));
                rightHolder.lastDayTextView.setVisibility(View.VISIBLE);
            } else {
                rightHolder.lastDayTextView.setVisibility(View.GONE);
            }

            rightHolder.dateTextView.setText(DateUtil.getUnixToTime(message.getTimestamp()));
            rightHolder.messageTextView.setText(message.getMessage());
        } else {
            ChatLeftMessageViewHolder leftHolder = (ChatLeftMessageViewHolder) holder;
            if (dayFromNowList.get(position) != null) {
                leftHolder.lastDayTextView.setText(dayFromNowList.get(position));
                leftHolder.lastDayTextView.setVisibility(View.VISIBLE);
            } else {
                leftHolder.lastDayTextView.setVisibility(View.GONE);
            }

            leftHolder.dateTextView.setText(DateUtil.getUnixToTime(message.getTimestamp()));
            leftHolder.messageTextView.setText(message.getMessage());
            if((!partnerUser.getPhotoUrl().isEmpty()) && partnerUser.getPhotoUrl().get(0) != null) {
                partnerPhotoUrl = partnerUser.getPhotoUrl().get(0);
                Glide.with(context).load(partnerPhotoUrl).into(leftHolder.profileCircleImageView);
            }
        }

    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage chatMessage = chatMessageArrayList.get(position);
        if (chatMessage.getSenderNickname().equals("운영자")) {
            return 0;
        } else if (chatMessage.getSenderUid().equals(FirebaseHelper.mUid)) { //내가보낸메세지
            return 1;
        } else {
            return 2;
        }
    }

    public class ChatRightMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView dateTextView;
        TextView lastDayTextView;

        public ChatRightMessageViewHolder(@NonNull final View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.item_chatmessage_right_tv_message);
            dateTextView = itemView.findViewById(R.id.item_chatmessage_right_tv_date);
            lastDayTextView = itemView.findViewById(R.id.item_chatmessage_right_tv_lastday);
        }
    }

    public class ChatLeftMessageViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profileCircleImageView;
        TextView messageTextView;
        TextView dateTextView;
        TextView lastDayTextView;

        public ChatLeftMessageViewHolder(@NonNull final View itemView) {
            super(itemView);
            profileCircleImageView = itemView.findViewById(R.id.item_chatmessage_left_civ_profile);
            messageTextView = itemView.findViewById(R.id.item_chatmessage_left_tv_message);
            dateTextView = itemView.findViewById(R.id.item_chatmessage_left_tv_date);
            lastDayTextView = itemView.findViewById(R.id.item_chatmessage_left_tv_lastday);
            profileCircleImageView.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    Intent intent = new Intent(context, ProfileCardChattingActivity.class);
                    intent.putExtra(Strings.partnerUid, partnerUser.getUid());
                    intent.putExtra(Strings.partnerUser, partnerUser);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
        }
    }

    public class ChatCenterMessageViewHolder extends RecyclerView.ViewHolder {
        TextView nickNameTextView;
        TextView dateTextView;
        Button exitButton;

        public ChatCenterMessageViewHolder(@NonNull final View itemView) {
            super(itemView);
            nickNameTextView = itemView.findViewById(R.id.item_chatmessage_center_tv_nickname);
            dateTextView = itemView.findViewById(R.id.item_chatmessage_center_tv_date);
            exitButton = itemView.findViewById(R.id.item_chatmessage_center_btn_exit);
            exitButton.setOnClickListener(new OnSingleClickListener() { //종료 버튼
                @Override
                public void onSingleClick(View v) {
                    //채팅 state 변경
                    if(chatRoom.isRefunded()){
                        loaderLayout.setVisibility(View.VISIBLE);
                        FirebaseHelper.db.collection("ChatRoom").document(chatRoom.getRoomId())
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        ChatRoom chatRoom = document.toObject(ChatRoom.class);
                                        if (chatRoom.isDeleted() && (!chatRoom.getUidDelete().equals(FirebaseHelper.mUid) )){
                                            WriteBatch batch = FirebaseHelper.db.batch();
                                            String timeStampUnix = DateUtil.getTimeStampUnix();
                                            RefundedDia refundedDia = new RefundedDia(Numbers.CHATTING_REFUND,timeStampUnix, FirebaseHelper.mUid, partnerUser.getUid(), "", chatRoom.getRoomId(), Strings.CHATTING_REFUND, DateUtil.getDateSec(), DateUtil.getUnixTimeLong());
                                            batch.set(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("RefundDia").document(timeStampUnix), refundedDia);
                                            if(chatRoom.getUidDelete().equals("")){
                                                batch.update(FirebaseHelper.db.collection("ChatRoom").document(chatRoom.getRoomId()),FirebaseHelper.uidDelete, FirebaseHelper.mUid);
                                            } else {
                                                batch.update(FirebaseHelper.db.collection("ChatRoom").document(chatRoom.getRoomId()),FirebaseHelper.uidBanned, FirebaseHelper.mUid);
                                            }

                                            batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    loaderLayout.setVisibility(View.GONE);
                                                    Intent intentHome = new Intent(context, MainActivity.class);
                                                    intentHome.putExtra(Strings.defaultPage, 4);
                                                    intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    intentHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    context.startActivity(intentHome);
                                                    Toast.makeText(context, "채팅이 종료되었습니다.", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    loaderLayout.setVisibility(View.GONE);
                                                    Toast.makeText(context, "오류 발생", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }else {
                                            loaderLayout.setVisibility(View.GONE);
                                            Intent intentHome = new Intent(context, MainActivity.class);
                                            intentHome.putExtra(Strings.defaultPage, 4);
                                            intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            intentHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            context.startActivity(intentHome);
                                        }
                                    } else {
                                        Log.d(TAG, "No such document");
                                        Toast.makeText(context, "오류 발생", Toast.LENGTH_SHORT).show();
                                        loaderLayout.setVisibility(View.GONE);
                                    }
                                } else {
                                    Log.d(TAG, "get failed with ", task.getException());
                                    Toast.makeText(context, "오류 발생", Toast.LENGTH_SHORT).show();
                                    loaderLayout.setVisibility(View.GONE);
                                }
                            }
                        });
                    } else {
                        loaderLayout.setVisibility(View.VISIBLE);
                        Map<String, Object> updateChat = new HashMap<>();
                        updateChat.put(FirebaseHelper.uidBanned, FirebaseHelper.mUid);
                        FirebaseHelper.db.collection("ChatRoom").document(chatRoom.getRoomId()).update(updateChat)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        loaderLayout.setVisibility(View.GONE);
                                        Intent intentHome = new Intent(context, MainActivity.class);
                                        intentHome.putExtra(Strings.defaultPage, 4);
                                        intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intentHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        context.startActivity(intentHome);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loaderLayout.setVisibility(View.GONE);
                                Toast.makeText(context, "네트워크가 불안정합니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });

        }
    }

    private ArrayList<String> getDayFromNowList (ArrayList<ChatMessage> items) { //날짜 넣어야할 cell 체크해주는 리스트.
        ArrayList<String> dayFromNowList = new ArrayList<>();
        for(ChatMessage message : items){
            String dayFromNow = DateUtil.getDayFromNow(message.getTimestamp());
            if (!dayFromNowList.contains(dayFromNow)) {
                dayFromNowList.add(dayFromNow);
            } else {
                dayFromNowList.add(null);
            }
        }
        return dayFromNowList;
    }

}

