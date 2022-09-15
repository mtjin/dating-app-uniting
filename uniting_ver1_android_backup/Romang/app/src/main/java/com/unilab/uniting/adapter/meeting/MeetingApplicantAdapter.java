package com.unilab.uniting.adapter.meeting;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.unilab.uniting.R;
import com.unilab.uniting.activities.meeting.ProfileCardMeetingActivity;
import com.unilab.uniting.model.Meeting;
import com.unilab.uniting.model.User;
import com.unilab.uniting.utils.Strings;

import java.util.ArrayList;

public class MeetingApplicantAdapter extends RecyclerView.Adapter<MeetingApplicantAdapter.ViewHolder> {

    //상수
    final static String TAG = "MeetingUserListStep1TAG";

    Context context;
    private ArrayList<User> userItemList;
    private String meetingUid;
    private Meeting meeting;

    public MeetingApplicantAdapter(Context context, ArrayList<User> userItemList, String meetingUid, Meeting meeting) {
        this.context = context;
        this.userItemList = userItemList;
        this.meetingUid = meetingUid;
        this.meeting = meeting;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_meeting_user_step1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
        User user = userItemList.get(position);
        if(user != null){
            if((!user.getPhotoUrl().isEmpty()) && user.getPhotoUrl().get(0) != null){
                //사진 url 리스트 받아온 후 글라이드를 통해 사진 세팅
                ArrayList<String> imageUrlList = (ArrayList<String>) user.getPhotoUrl();
                String imageUrl0 = imageUrlList.get(0);
                Glide.with(context).load(imageUrl0).centerCrop().into(viewHolder.user_image);

            }
            viewHolder.user_1st_line.setText(user.getNickname() + " · " + user.getAge() );
            viewHolder.user_2nd_line.setText(user.getHeight() + " · "+ user.getLocation());
        }
    }

    public void clear(){ userItemList.clear(); }


    @Override
    public int getItemCount() {
        return userItemList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView user_image;
        TextView user_1st_line;
        TextView user_2nd_line;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            user_image = itemView.findViewById(R.id.item_meeting_user1_iv_photo);
            user_1st_line = itemView.findViewById(R.id.item_meeting_user1_tv_1st_line);
            user_2nd_line = itemView.findViewById(R.id.item_meeting_user1_tv_2nd_line);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Intent intent = new Intent(context, ProfileCardMeetingActivity.class);
                        intent.putExtra(Strings.partnerUid,userItemList.get(position).getUid());
                        intent.putExtra(Strings.meetingUid, meetingUid);
                        intent.putExtra(Strings.partnerUser, userItemList.get(position));
                        intent.putExtra(Strings.EXTRA_MEETING, meeting);
                        intent.putExtra(Strings.EXTRA_LOCATION,Strings.LOCATION_MEETING_STEP1);
                        context.startActivity(intent);
                    }
                }
            });
        }
    }

}
