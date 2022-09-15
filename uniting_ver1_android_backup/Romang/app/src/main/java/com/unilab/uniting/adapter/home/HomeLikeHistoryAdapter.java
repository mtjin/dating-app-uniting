package com.unilab.uniting.adapter.home;

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
import com.unilab.uniting.activities.profilecard.ProfileCardMainActivity;
import com.unilab.uniting.model.Interaction;
import com.unilab.uniting.model.User;
import com.unilab.uniting.utils.DateUtil;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.LaunchUtil;
import com.unilab.uniting.utils.LogData;
import com.unilab.uniting.utils.Strings;

import java.util.ArrayList;

public class HomeLikeHistoryAdapter extends RecyclerView.Adapter<HomeLikeHistoryAdapter.HomePastViewHolder> {

    //상수
    final static String TAG = "HomeLikeHistoryTAG";
    final static String EXTRA_USER = "EXTRA_USER";
    final static String EXTRA_USER_UID = "EXTRA_USER_UID";

    Context context;
    ArrayList<Interaction> interactionList;

    public HomeLikeHistoryAdapter(Context context, ArrayList<Interaction> interactionList) {
        this.context = context;
        this.interactionList = interactionList;
    }

    @NonNull
    @Override
    public HomePastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_like_history, parent, false);
        return new HomePastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final HomePastViewHolder homePastViewHolder, final int position) {
        Interaction interaction = interactionList.get(position);

        User user = new User();
        if(interaction.getUser0().getUid().equals(FirebaseHelper.mUid)){
            user = interaction.getUser1();
        } else if(interaction.getUser1().getUid().equals(FirebaseHelper.mUid)){
            user = interaction.getUser0();
        }

        if (user != null) {
            //사진 url 리스트 받아온 후 글라이드를 통해 사진 세팅
            if((!user.getPhotoUrl().isEmpty()) && user.getPhotoUrl().get(0) != null){
                ArrayList<String> imageUrlList = (ArrayList<String>) user.getPhotoUrl();
                String imageUrl0 = imageUrlList.get(0);
                Glide.with(context).load(imageUrl0).fitCenter().thumbnail(0.1f).into(homePastViewHolder.user_image);
            }
            //프로필 정보 세팅
            homePastViewHolder.user_profile.setText(user.getNickname() +  " · " + user.getAge());

            if(user.getMembership().equals(LaunchUtil.Scoring)){
                homePastViewHolder.user_dday.setVisibility(View.VISIBLE);
                homePastViewHolder.user_dday.setText("심사중");
            } else {
                //D-day 처리
                try{
                    long introTime = interaction.getRecentTime();
                    long difference = DateUtil.getUnixTimeLong() - introTime;
                    long dayFromIntroTime = difference / DateUtil.dayInMilliSecond;
                    long dDay = 6 - dayFromIntroTime;
                    if( DateUtil.dayInMilliSecond > DateUtil.getUnixTimeLong() - introTime) {
                        homePastViewHolder.user_dday.setVisibility(View.GONE);
                    } else {
                        homePastViewHolder.user_dday.setText("D - " + dDay);
                    }
                } catch (Exception e){
                    homePastViewHolder.user_dday.setVisibility(View.GONE);
                }
            }
        }



    }

    public void clear(){
        interactionList.clear();
    }

    public void add(Interaction item){
        interactionList.add(item);
    }


    @Override
    public int getItemCount() {
        return interactionList.size();
    }

    public class HomePastViewHolder extends RecyclerView.ViewHolder {
        ImageView user_image;
        TextView user_profile;
        TextView user_dday;

        public HomePastViewHolder(@NonNull View itemView) {
            super(itemView);
            user_image = itemView.findViewById(R.id.item_todayintroduce_iv_photo);
            user_profile = itemView.findViewById(R.id.item_todayintroduce_tv_profile);
            user_dday = itemView.findViewById(R.id.item_todayintroduce_tv_dday);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Intent intent = new Intent(context, ProfileCardMainActivity.class);

                        Interaction interaction = interactionList.get(position);
                        User user;
                        if(interaction.getUser0().getUid().equals(FirebaseHelper.mUid)){
                            user = interaction.getUser1();
                        } else if(interaction.getUser1().getUid().equals(FirebaseHelper.mUid)){
                            user = interaction.getUser0();
                        } else {
                            return;
                        }

                        LogData.setStageTodayIntro(LogData.ti_s02_profile_card_open, context);

                        intent.putExtra(Strings.partnerUid, user.getUid());
                        intent.putExtra(Strings.partnerUser, user);
                        context.startActivity(intent);
                    }
                }
            });
        }
    }

}
