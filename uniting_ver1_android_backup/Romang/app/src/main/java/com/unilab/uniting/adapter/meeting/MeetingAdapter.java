package com.unilab.uniting.adapter.meeting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.unilab.uniting.R;
import com.unilab.uniting.activities.meeting.MeetingBusProvider;
import com.unilab.uniting.model.Meeting;
import com.unilab.uniting.utils.DateUtil;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.MyProfile;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class MeetingAdapter extends RecyclerView.Adapter<MeetingAdapter.MeetingViewHolder> {
    Context context;
    ArrayList<Meeting> items;
    final static String TAG= "MeetingAdapterT";

    public MeetingAdapter(Context context, ArrayList<Meeting> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public MeetingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
       View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_meeting, viewGroup,   false);
       return  new MeetingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MeetingViewHolder holder, int i) {
        Meeting meeting = items.get(i);
        if(meeting != null){
            //holder.ageTextView.setText(meeting.getHostAge() + "세");
            holder.titleTextView.setText(meeting.getTitle());
            //성별 처리

            if(meeting.getHostGender().equals("남자")) {
                holder.profileCircleImageView.setImageResource(R.drawable.ic_community_male_example);
            }else if(meeting.getHostGender().equals("여자")){
                holder.profileCircleImageView.setImageResource(R.drawable.ic_community_girl_example);
            }

            //미팅 사진 처리
            if(meeting.getPhotoUrl().equals("")){
                holder.photoImageView.setImageResource(R.drawable.uniting_square);
            }else{//TODO: 임시로 정사각 대충 해놓음. 나중에 크랩 기능 넣으면서 다시 해결할 것
                if(meeting.isBlurred()){
                    Glide.with(context).load(meeting.getPhotoUrl()).centerCrop().apply(bitmapTransform(new BlurTransformation(6, 5))).into(holder.photoImageView);
                }else {
                    Glide.with(context).load(meeting.getPhotoUrl()).centerCrop().thumbnail(0.1f).into(holder.photoImageView);
                }
            }

            if(!meeting.getHostUniversity().equals("")){
                holder.universityTextView.setVisibility(View.VISIBLE);
                holder.universityTextView.setText(meeting.getHostUniversity());
            }else {
                if(meeting.getHostGender().equals("남자")&&(meeting.getHostTierPercent() < 0.2 || meeting.getHostTierPercent() < MyProfile.getUser().getTierPercent() - 0.5)){
                    holder.universityTextView.setVisibility(View.VISIBLE);
                    holder.universityTextView.setText("호감형 친구");
                } else {
                    holder.universityTextView.setVisibility(View.GONE);
                }
            }

            holder.ageTextView.setText(meeting.getHostAge() + "세");

            if(meeting.getCreateTimestamp() > (DateUtil.getUnixTimeLong() - DateUtil.dayInMilliSecond) || meeting.getHostUid().equals(FirebaseHelper.mUid)){
                holder.newTextView.setVisibility(View.VISIBLE);
                if(meeting.getCreateTimestamp() > (DateUtil.getUnixTimeLong() - DateUtil.dayInMilliSecond)){
                   holder.newTextView.setText("New");
                }

                if(meeting.getHostUid().equals(FirebaseHelper.mUid)){
                    holder.newTextView.setText("내 번개");
                }

            }else {
                holder.newTextView.setVisibility(View.GONE);
            }

        }
    }

    public void add(Meeting item){
        items.add(item);
    }

    public void clear(){
        items.clear();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MeetingViewHolder extends RecyclerView.ViewHolder {
        ImageView photoImageView;
        CircleImageView profileCircleImageView;
        TextView titleTextView;
        LinearLayout linearLayout; //전체화면
        TextView universityTextView;
        TextView ageTextView;
        TextView newTextView;


        public MeetingViewHolder(@NonNull View itemView) {
            super(itemView);
            photoImageView = itemView.findViewById(R.id.item_meeting_iv_photo);
            profileCircleImageView = itemView.findViewById(R.id.item_meeting_civ_profile);
            titleTextView = itemView.findViewById(R.id.item_meeting_tv_title);
            linearLayout = itemView.findViewById(R.id.item_meeting_linear);
            universityTextView = itemView.findViewById(R.id.item_meeting_tv_university);
            ageTextView = itemView.findViewById(R.id.item_meeting_tv_age);
            newTextView = itemView.findViewById(R.id.item_meeting_tv_new);

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if ((position != RecyclerView.NO_POSITION) && position < items.size()) {
                        Meeting item = items.get(position);
                        MeetingBusProvider.getInstance().post(item);
                    }
                }
            });
        }
    }
}
