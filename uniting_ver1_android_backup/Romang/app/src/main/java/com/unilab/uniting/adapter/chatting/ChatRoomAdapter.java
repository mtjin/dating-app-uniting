package com.unilab.uniting.adapter.chatting;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.unilab.uniting.R;
import com.unilab.uniting.activities.chatting.ChattingConnectActivity;
import com.unilab.uniting.activities.chatting.ChattingRoomActivity;
import com.unilab.uniting.model.ChatRoom;
import com.unilab.uniting.model.User;
import com.unilab.uniting.utils.DateUtil;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.MyProfile;
import com.unilab.uniting.utils.Strings;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ChatRoomViewHolder> {


    Context context;
    ArrayList<ChatRoom> chatRoomArrayList;
    final static String TAG = "ChatRoomAdapterT";

    private String mPartnerUid;
    private User mPartnerUser;
    private User mUser = MyProfile.getUser();

    public ChatRoomAdapter(Context context, ArrayList<ChatRoom> chatRoomArrayList) {
        this.context = context;
        this.chatRoomArrayList = chatRoomArrayList;
    }

    @NonNull
    @Override
    public ChatRoomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_overview, viewGroup, false);
        return new ChatRoomViewHolder(view);
    }

    @Override
    public long getItemId(int position) {
        ChatRoom chatRoom = chatRoomArrayList.get(position);
        return chatRoom.getRecentTimestamp();
    }


    @Override
    public void onBindViewHolder(@NonNull ChatRoomViewHolder holder, int i) {
        ChatRoom chatRoom = chatRoomArrayList.get(i);

        if (chatRoom.getUser0().getUid().equals(FirebaseHelper.mUid)) {
            mPartnerUser = chatRoom.getUser1();
        } else {
            mPartnerUser = chatRoom.getUser0();
        }
        mPartnerUid = mPartnerUser.getUid();

        if (chatRoom.isDeleted()) { //상대방이 나간 경우
            holder.messageTextView.setText("연결해제");
            holder.turnTextView.setVisibility(View.GONE);
            holder.dateTextView.setVisibility(View.GONE);
        } else {
            //닉네임 , 프로필사진세팅
            if ((!mPartnerUser.getPhotoUrl().isEmpty()) && mPartnerUser.getPhotoUrl().get(0) != null) {
                Glide.with(context).load(mPartnerUser.getPhotoUrl().get(0)).into(holder.profileCircleImageView);
            }
            holder.dateTextView.setVisibility(View.VISIBLE);
            holder.dateTextView.setText(DateUtil.dayForChattingList(chatRoom.getRecentTimestamp()));

            holder.nickNameTextView.setText(mPartnerUser.getNickname());
            if (!chatRoom.isStarted()) { //아직 결제 안된 경우
                holder.turnTextView.setVisibility(View.VISIBLE);
                holder.turnTextView.setText("대화를 시작하세요!");
                holder.messageTextView.setVisibility(View.GONE);
            } else {//결제된 경우 메세지, 날짜 세팅 (실시간업데이트)
                holder.messageTextView.setVisibility(View.VISIBLE);
                holder.messageTextView.setText(chatRoom.getRecentMessage());
                if (chatRoom.getSenderUid().equals(mPartnerUid)) {
                    holder.turnTextView.setVisibility(View.VISIBLE);
                    holder.turnTextView.setText("답장할 차례");
                } else{
                    holder.turnTextView.setVisibility(View.INVISIBLE);
                }
                if(chatRoom.isVoiceTalkOn()){
                    holder.frameLayout.setBackgroundColor(context.getResources().getColor(R.color.colorSubPink2));
                } else{
                    holder.frameLayout.setBackgroundColor(context.getResources().getColor(R.color.colorWhite));
                }
            }
        }
    }

    public void add(ChatRoom item) {
        chatRoomArrayList.add(item);
    }

    public void clear() {
        chatRoomArrayList.clear();
    }

    @Override
    public int getItemCount() {
        return chatRoomArrayList.size();
    }

    public class ChatRoomViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profileCircleImageView;
        TextView nickNameTextView;
        TextView messageTextView;
        TextView dateTextView;
        TextView turnTextView; //대화를 시작하세요
        FrameLayout frameLayout; //전체화면

        public ChatRoomViewHolder(@NonNull View itemView) {
            super(itemView);
            profileCircleImageView = itemView.findViewById(R.id.item_chatoverview_civ_profile);
            nickNameTextView = itemView.findViewById(R.id.item_chatoverview_tv_nickname);
            messageTextView = itemView.findViewById(R.id.item_chatoverview_tv_message);
            dateTextView = itemView.findViewById(R.id.item_chatoverview_tv_dates);
            turnTextView = itemView.findViewById(R.id.item_chatoverview_tv_turn);
            frameLayout = itemView.findViewById(R.id.item_chatoverview_li_linear);

            frameLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        ChatRoom chatRoom = chatRoomArrayList.get(position);
                        if(!chatRoom.isExpired()){ //expire 된건 애초에 안띄울거긴한데 확실하게 한번 더 분기함
                            if(chatRoom.getFrom().equals(FirebaseHelper.meeting)){
                                Intent intent2 = new Intent(context, ChattingConnectActivity.class);
                                intent2.putExtra(Strings.EXTRA_CHATROOM_ID, chatRoom);
                                intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent2);
                            }else{
                                if(!chatRoom.isStarted()){ //결제 전
                                    Intent intent2 = new Intent(context, ChattingConnectActivity.class);
                                    intent2.putExtra(Strings.EXTRA_CHATROOM_ID, chatRoom);
                                    intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent2);
                                }else{ //결제 후
                                    Intent intent1 = new Intent(context, ChattingRoomActivity.class);
                                    intent1.putExtra(Strings.EXTRA_CHATROOM_ID, chatRoom);
                                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent1);
                                }
                            }
                        }
                    }
                }
            });
        }
    }





}
