package com.unilab.uniting.adapter.notification;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unilab.uniting.R;
import com.unilab.uniting.activities.chatting.ChattingConnectActivity;
import com.unilab.uniting.activities.chatting.ChattingRoomActivity;
import com.unilab.uniting.activities.community.CommunityFullActivity;
import com.unilab.uniting.activities.meeting.MeetingCardActivity;
import com.unilab.uniting.activities.profilecard.ProfileCardMainActivity;
import com.unilab.uniting.model.ChatRoom;
import com.unilab.uniting.model.CommunityPost;
import com.unilab.uniting.model.Meeting;
import com.unilab.uniting.model.Notification;
import com.unilab.uniting.model.User;
import com.unilab.uniting.utils.Strings;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    static final String TAG = "NotificationAdapterT";
    ArrayList<Notification> items;
    Context context;

    //putExtra Key
    final String EXTRA_COMMUNITY_POST = "EXTRA_COMMUNITY_POST";
    final static String EXTRA_MEETING = "EXTRA_MEETING"; //미팅

    public NotificationAdapter(ArrayList<Notification> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public NotificationAdapter.NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_notification, viewGroup,   false);
        return  new NotificationViewHolder(view);
    }

    //Notification notification = new Notification("좋아요확인"

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.NotificationViewHolder holder, int i) {
        Notification item = items.get(i);
        holder.dateTextView.setText(item.getDate());
        switch (item.getType()) {
            case "댓글":
                holder.messageTextView.setText(item.getNickname() + "님이 게시물에 댓글을 달았습니다");
                break;
            case "대댓글":
                holder.messageTextView.setText(item.getNickname() + "님이 댓글에 대댓글을 달았습니다");
                break;
            case "프로필열람신청"://미팅 Step1 신청
                holder.messageTextView.setText(item.getNickname() + "님이 프로필 열람을 신청했습니다");
                break;
            case "프로필열람수락": //미팅 Step1 수락
                holder.messageTextView.setText(item.getNickname() + "님이 프로필 열람을 수락했습니다.");
                break;
            case "미팅신청": //미팅 Step2 유료 신청
                holder.messageTextView.setText(item.getNickname() + "님이 연락처 교환을 신청했습니다. 무료로 수락하세요!");
                break;
            case "미팅수락"://미팅 Step2 수락
                holder.messageTextView.setText(item.getNickname() + "님이 연락처 교환을 수락했어요:)");
                break;
            case "좋아요보냄":
                holder.messageTextView.setText(item.getNickname() + "님이 친구 신청을 보냈습니다.");
                break;
            case "좋아요수락":
                holder.messageTextView.setText(item.getNickname() + "님이 친구 신청을 수락하였습니다.");
                break;
            case "좋아요확인":
                holder.messageTextView.setText(item.getNickname() + "님이 친구 신청을 확인했습니다.");
                break;
            case "초대":
                holder.messageTextView.setText("초대코드 인증으로 다이아 30개가 지급되었습니다.");
                break;
            case "채팅연결":
                holder.messageTextView.setText(item.getNickname() + "님과의 채팅이 열렸습니다. 대화해보세요!");
                break;
            default:
                holder.messageTextView.setText(item.getContent());
                break;
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView dateTextView;
        LinearLayout linearLayout; //전체화면

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.item_notification_tv_message);
            dateTextView = itemView.findViewById(R.id.item_notification_tv_date);
            linearLayout = itemView.findViewById(R.id.item_notification_li_linear);
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION) {
                        switch (items.get(position).getType()) {
                            case "댓글":
                            case "대댓글": {
                                CommunityPost item = items.get(position).getCommunityPost();
                                Intent intent = new Intent(context, CommunityFullActivity.class);
                                intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra(EXTRA_COMMUNITY_POST, item);
                                context.startActivity(intent);
                            }
                            break;
                            case "프로필열람신청":
                            case "프로필열람수락":
                            case "미팅신청": //미팅 Step2 신청
                            case "미팅수락"://미팅 Step2 수락
                            {
                                Meeting item = items.get(position).getMeeting();
                                Intent intent = new Intent(context, MeetingCardActivity.class);
                                intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra(EXTRA_MEETING, item);
                                context.startActivity(intent);
                            }
                            break;
                            case "좋아요수락":
                            {
                                ChatRoom item = items.get(position).getChatRoom();
                                Intent intent = new Intent(context, ChattingConnectActivity.class);
                                intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra(Strings.EXTRA_CHATROOM_ID, item);
                                context.startActivity(intent);
                            }
                            break;
                            case "좋아요보냄":
                            case "좋아요확인": {
                                User item = items.get(position).getUser();
                                Intent intent = new Intent(context, ProfileCardMainActivity.class);
                                intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("partnerUid", item.getUid());
                                intent.putExtra("partnerUser", item);
                                context.startActivity(intent);
                            }
                            break;
                            case "채팅연결": {
                                ChatRoom item = items.get(position).getChatRoom();
                                Intent intent = new Intent(context, ChattingRoomActivity.class);
                                intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra(Strings.EXTRA_CHATROOM_ID, item);
                                context.startActivity(intent);
                            }
                            break;
                        }
                    }
                }
            });
        }
    }
}
