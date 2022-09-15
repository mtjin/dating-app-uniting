package com.unilab.uniting.adapter.community;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldValue;
import com.unilab.uniting.R;
import com.unilab.uniting.activities.community.CommunityBusProvider;
import com.unilab.uniting.model.CommunityComment;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.LogData;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommunityCommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    final static String TAG = "CommunityCommentAdapter";

    Context context;
    ArrayList<CommunityComment> items;
    ArrayList<String> alreadyLikeCommentIdList = new ArrayList<>();

    //댓글추천 트랜잭션 추천중복여부
    boolean isLoading = false;

    public CommunityCommentAdapter(Context context, ArrayList<CommunityComment> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        CommunityComment commentItem = items.get(position);
        Log.d(TAG, "대댓글대상 댓글 uid" + commentItem.getReplyCommentId());
        if (commentItem.getReplyCommentId().equals("")) { //일반댓글인 경우
            return 0;
        } else { //대댓글인 경우
            return 1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item_comment, viewGroup, false);
                return new CommunityCommentViewHolder(view);
            case 1:
                view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item_replycomment, viewGroup, false);
                return new CommunityReplyViewHolder(view);
        }
        view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_comment, viewGroup, false);
        return new CommunityCommentViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int i) {
        final CommunityComment model = items.get(i);
        if (model.getReplyCommentId().equals("")) { //일반댓글
            CommunityCommentViewHolder commentHolder = (CommunityCommentViewHolder) holder;

            if (model.getWriterGender().equals("남자")) {
                commentHolder.profileCircleImageView.setImageResource(R.drawable.ic_community_male_example);
            } else {
                commentHolder.profileCircleImageView.setImageResource(R.drawable.ic_community_girl_example);
            }

            if(model.getPostWriterUid().equals(model.getCommentWriterUid())){
                commentHolder.nickNameTextView.setText(model.getCommentWriterNickname() + " [글쓴이]");
            }else{
                commentHolder.nickNameTextView.setText(model.getCommentWriterNickname());
            }

            commentHolder.timeTextView.setText(model.getDate());

            if(model.isDeleted()){
                commentHolder.messageTextView.setText("삭제된 댓글입니다.");
            }else{
                commentHolder.messageTextView.setText(model.getContent());
            }

            commentHolder.recommendCountTextView.setText("" + model.getLike());
            commentHolder.recommendUpLinearLayout.setOnClickListener(new View.OnClickListener() {//댓글추천 클릭리스너
                @Override
                public void onClick(View v) {
                    if (alreadyLikeCommentIdList.contains(model.getCommentId())) {
                        Toast.makeText(context, "이미 추천한 댓글입니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(!isLoading){
                        isLoading = true;
                        if (!model.getLikeList().contains(FirebaseHelper.mUid)) {
                            FirebaseHelper.db.collection("CommunityPosts").document(model.getPostId()).collection("CommunityComments").document(model.getCommentId()).update(
                                    "like", FieldValue.increment(1),
                                    "likeList", FieldValue.arrayUnion(FirebaseHelper.mUid))
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(context, "추천하였습니다.", Toast.LENGTH_SHORT).show();

                                    Bundle bundle = new Bundle();
                                    bundle.putInt(LogData.dia, 0);
                                    bundle.putString(LogData.postId, model.getPostId());
                                    bundle.putString(LogData.commentId, model.getCommentId());
                                    LogData.customLog(LogData.community_comment_like,  bundle, context);

                                    alreadyLikeCommentIdList.add(model.getCommentId());
                                    isLoading = false;
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, "네트워크가 불안정합니다.", Toast.LENGTH_SHORT).show();
                                    isLoading = false;
                                }
                            });

                        } else {
                            Toast.makeText(context, "이미 추천한 댓글입니다.", Toast.LENGTH_SHORT).show();
                            isLoading = false;
                        }
                    }else{
                        Toast.makeText(context, "로딩중입니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else { //대댓글
            CommunityReplyViewHolder replyHolder = (CommunityReplyViewHolder) holder;
            if (model.getWriterGender().equals("남자")) {
                replyHolder.profileCircleImageView.setImageResource(R.drawable.ic_community_male_example);
            } else {
                replyHolder.profileCircleImageView.setImageResource(R.drawable.ic_community_girl_example);
            }

            if(model.getPostWriterUid().equals(model.getCommentWriterUid())){
                replyHolder.nickNameTextView.setText(model.getCommentWriterNickname() + " [글쓴이]");
            }else{
                replyHolder.nickNameTextView.setText(model.getCommentWriterNickname());
            }

            replyHolder.timeTextView.setText(model.getDate());

            if(model.isDeleted()){
                replyHolder.messageTextView.setText("삭제된 댓글입니다.");
            }else{
                replyHolder.messageTextView.setText(model.getContent());
            }

            replyHolder.recommendCountTextView.setText("" + model.getLike());
            replyHolder.recommendUpLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (alreadyLikeCommentIdList.contains(model.getCommentId())) {
                        Toast.makeText(context, "이미 추천한 댓글입니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(!isLoading){
                        isLoading = true;
                        if (!model.getLikeList().contains(FirebaseHelper.mUid)) {
                            FirebaseHelper.db.collection("CommunityPosts").document(model.getPostId()).collection("CommunityComments").document(model.getCommentId()).update("like", FieldValue.increment(1),
                                    "likeList", FieldValue.arrayUnion(FirebaseHelper.mUid))
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(context, "추천하였습니다.", Toast.LENGTH_SHORT).show();

                                    Bundle bundle = new Bundle();
                                    bundle.putInt(LogData.dia, 0);
                                    bundle.putString(LogData.postId, model.getPostId());
                                    bundle.putString(LogData.commentId, model.getCommentId());
                                    LogData.customLog(LogData.community_comment_like,  bundle, context);

                                    alreadyLikeCommentIdList.add(model.getCommentId());
                                    isLoading = false;
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, "네트워크가 불안정합니다.", Toast.LENGTH_SHORT).show();
                                    isLoading = false;
                                }
                            });

                        } else {
                            Toast.makeText(context, "이미 추천한 댓글입니다.", Toast.LENGTH_SHORT).show();
                            isLoading = false;
                        }
                    }else{
                        Toast.makeText(context, "로딩중입니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(CommunityComment item) {
        items.add(item);
    }

    public void clear() {
        items.clear();
    }

    public class CommunityCommentViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profileCircleImageView;
        TextView nickNameTextView;
        TextView messageTextView;
        TextView recommendCountTextView;
        TextView replyTextView;
        ImageView optionImageView;
        ImageView recommendUpImageView;
        TextView timeTextView;
        LinearLayout optionLinearLayout;
        LinearLayout recommendUpLinearLayout;

        LinearLayout linearLayout; //전체화면

        public CommunityCommentViewHolder(@NonNull View itemView) {
            super(itemView);
            profileCircleImageView = itemView.findViewById(R.id.item_comment_civ_profile);
            nickNameTextView = itemView.findViewById(R.id.item_comment_tv_nickname);
            messageTextView = itemView.findViewById(R.id.item_comment_tv_message);
            recommendCountTextView = itemView.findViewById(R.id.item_comment_tv_recommendsum);
            replyTextView = itemView.findViewById(R.id.item_comment_tv_reply);
            timeTextView = itemView.findViewById(R.id.item_comment_tv_time);
            optionImageView = itemView.findViewById(R.id.item_comment_iv_option);
            optionLinearLayout = itemView.findViewById(R.id.item_comment_linear_option);
            recommendUpLinearLayout = itemView.findViewById(R.id.item_comment_linear_recommend);
            recommendUpImageView = itemView.findViewById(R.id.item_comment_iv_recommend);
            linearLayout = itemView.findViewById(R.id.item_comment_linear);

            //대댓글 클릭 리스너
            replyTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        CommunityComment item = items.get(position);
                        item.setFlag(0);
                        CommunityBusProvider.getInstance().post(item);
                    }
                }
            });

            //신고하기(옵션) 클릭 리스너
            optionLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        CommunityComment item = items.get(position);
                        item.setFlag(1);
                        CommunityBusProvider.getInstance().post(item);
                    }
                }
            });
        }
    }

    public class CommunityReplyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profileCircleImageView;
        TextView nickNameTextView;
        TextView messageTextView;
        TextView recommendCountTextView;
        TextView replyTextView;
        TextView replyWriterTextView;
        TextView timeTextView;
        ImageView optionImageView;
        ImageView recommendUpImageView;
        LinearLayout optionLinearLayout;
        LinearLayout recommendUpLinearLayout;
        LinearLayout linearLayout; //전체화면

        public CommunityReplyViewHolder(@NonNull View itemView) {
            super(itemView);
            profileCircleImageView = itemView.findViewById(R.id.item_replycomment_civ_profile);
            nickNameTextView = itemView.findViewById(R.id.item_replycomment_tv_nickname);
            messageTextView = itemView.findViewById(R.id.item_replycomment_tv_message);
            recommendCountTextView = itemView.findViewById(R.id.item_replycomment_tv_recommendsum);
            replyTextView = itemView.findViewById(R.id.item_replycomment_tv_reply);
            timeTextView = itemView.findViewById(R.id.item_replycomment_tv_time);
            optionImageView = itemView.findViewById(R.id.item_replycomment_iv_option);
            optionLinearLayout = itemView.findViewById(R.id.item_replycomment_linear_option);
            recommendUpLinearLayout = itemView.findViewById(R.id.item_replycomment_linear_recommend);
            linearLayout = itemView.findViewById(R.id.item_replycomment_linear);
            replyWriterTextView = itemView.findViewById(R.id.item_replycomment_tv_replynickname);
            recommendUpImageView = itemView.findViewById(R.id.item_replycomment_iv_recommend);

            //대댓글 클릭 리스너
            replyTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        CommunityComment item = items.get(position);
                        item.setFlag(0);
                        CommunityBusProvider.getInstance().post(item);
                    }
                }
            });

            //신고하기(옵션) 클릭 리스너
            optionLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        CommunityComment item = items.get(position);
                        item.setFlag(1);
                        CommunityBusProvider.getInstance().post(item);
                    }
                }
            });
        }
    }
}
