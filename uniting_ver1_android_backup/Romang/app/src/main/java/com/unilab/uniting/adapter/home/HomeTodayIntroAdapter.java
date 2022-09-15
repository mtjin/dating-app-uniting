package com.unilab.uniting.adapter.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.unilab.uniting.R;
import com.unilab.uniting.activities.profilecard.ProfileCardMainActivity;
import com.unilab.uniting.model.User;
import com.unilab.uniting.utils.DateUtil;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.LogData;
import com.unilab.uniting.utils.Strings;

import java.util.ArrayList;

public class HomeTodayIntroAdapter extends RecyclerView.Adapter<HomeTodayIntroAdapter.HomePastViewHolder> {

    //상수
    final static String TAG = "HomeTodayIntroTAG";

    Context context;
    ArrayList<QueryDocumentSnapshot> userItemList;


    public
    HomeTodayIntroAdapter(Context context, ArrayList<QueryDocumentSnapshot> userItemList) {
        this.context = context;
        this.userItemList = userItemList;
    }

    @NonNull
    @Override
    public HomePastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_today_introduce, parent, false);
        return new HomePastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final HomePastViewHolder homePastViewHolder, final int position) {
        QueryDocumentSnapshot documentSnapshot = userItemList.get(position);
        User user = documentSnapshot.toObject(User.class);

        if (documentSnapshot != null) {
            //사진 url 리스트 받아온 후 글라이드를 통해 사진 세팅
            ArrayList<String> imageUrlList = (ArrayList<String>) user.getPhotoUrl();
            String imageUrl0 = imageUrlList.get(0);

            Glide.with(context).load(imageUrl0).fitCenter().thumbnail(0.1f).into(homePastViewHolder.user_image);

            //프로필 정보 세팅
            homePastViewHolder.user_1st_line.setText(user.getNickname() + " · " + user.getAge() );
            homePastViewHolder.user_2nd_line.setText(user.getHeight() + " · "+ user.getLocation());
            //D-day 처리
            try{
                long introTime = (long) documentSnapshot.get(FirebaseHelper.recentTime);
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

            try{
                boolean isInteracted = (boolean) documentSnapshot.get(FirebaseHelper.interacted);
                if(isInteracted){
                    homePastViewHolder.previewLayout.setVisibility(View.GONE);
                } else {
                    homePastViewHolder.previewLayout.setVisibility(View.VISIBLE);
                }
            } catch (Exception e){

            }

            try{
                boolean closeUser = (boolean) documentSnapshot.get(FirebaseHelper.closeUser);

                if(closeUser){
                    homePastViewHolder.previewLayout.setBackgroundResource(R.drawable.bg_item_today_intro_preview_orange);
                    homePastViewHolder.previewImageView.setImageResource(R.drawable.ic_add_user);
                    homePastViewHolder.previewCloserUserTextView.setVisibility(View.VISIBLE);
                } else {
                    homePastViewHolder.previewLayout.setBackgroundResource(R.drawable.bg_item_today_intro_preview_pink);
                    homePastViewHolder.previewImageView.setImageResource(R.drawable.unitingxxxhdpi);
                    homePastViewHolder.previewCloserUserTextView.setVisibility(View.GONE);
                }

            } catch (Exception e){
                homePastViewHolder.previewLayout.setBackgroundResource(R.drawable.bg_item_today_intro_preview_pink);
                homePastViewHolder.previewImageView.setImageResource(R.drawable.unitingxxxhdpi);
                homePastViewHolder.previewCloserUserTextView.setVisibility(View.GONE);
            }


        }
    }

    public void clear(){
        userItemList.clear();
    }

    public void add(QueryDocumentSnapshot item){
        userItemList.add(item);
    }

    @Override
    public int getItemCount() {
        return userItemList.size();
    }

    class HomePastViewHolder extends RecyclerView.ViewHolder {
        ImageView user_image;
        TextView user_1st_line;
        TextView user_2nd_line;
        TextView user_dday;
        LinearLayout previewLayout;
        TextView previewCloserUserTextView;
        ImageView previewImageView;


        public HomePastViewHolder(@NonNull View itemView) {
            super(itemView);
            user_image = itemView.findViewById(R.id.item_todayintroduce_iv_photo);
            user_1st_line = itemView.findViewById(R.id.item_todayintroduce_tv_1st_line);
            user_2nd_line = itemView.findViewById(R.id.item_todayintroduce_tv_2nd_line);
            user_dday = itemView.findViewById(R.id.item_todayintroduce_tv_dday);
            previewLayout = itemView.findViewById(R.id.item_todayintroduce_layout_preview);
            previewImageView = itemView.findViewById(R.id.item_todayintroduce_iv_preview);
            previewCloserUserTextView = itemView.findViewById(R.id.item_todayintroduce_tv_preview);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        if(previewLayout.getVisibility() == View.VISIBLE){
                           previewLayout.setVisibility(View.GONE);
                            String partnerUid = "";
                           try{
                               partnerUid = (String) userItemList.get(position).get(FirebaseHelper.uid);
                               FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("TodayIntro").document(partnerUid).update(FirebaseHelper.interacted, true);
                               FirebaseHelper.setRecentIntroTime(partnerUid);
                           } catch (Exception err){

                           }

                            Bundle bundle = new Bundle();
                            bundle.putInt(LogData.dia, 0);
                            bundle.putString(LogData.partnerUid,partnerUid);
                            LogData.customLog(LogData.ti_s01_profile_card_pre_open, bundle, context);
                            LogData.setStageTodayIntro(LogData.ti_s01_profile_card_pre_open, context);

                        } else {
                            String partnerUid = "";
                            try{
                                partnerUid = (String) userItemList.get(position).get(FirebaseHelper.uid);
                            } catch (Exception err){

                            }

                            Bundle bundle = new Bundle();
                            bundle.putInt(LogData.dia, 0);
                            bundle.putString(LogData.partnerUid,partnerUid);
                            LogData.customLog(LogData.ti_s02_profile_card_open, bundle, context);
                            LogData.setStageTodayIntro(LogData.ti_s02_profile_card_open, context);

                            User partnerUser =  userItemList.get(position).toObject(User.class);
                            Intent intent = new Intent(context, ProfileCardMainActivity.class);
                            intent.putExtra(Strings.partnerUid,(String) userItemList.get(position).get(FirebaseHelper.uid));
                            intent.putExtra(Strings.partnerUser, partnerUser);
                            context.startActivity(intent);
                        }
                    }
                }
            });
        }
    }




}
