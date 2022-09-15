package com.unilab.uniting.adapter.setprofile;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.unilab.uniting.R;

import java.util.ArrayList;

public class SetProfileNoticeAdapter extends RecyclerView.Adapter<SetProfileNoticeAdapter.NoticeViewHolder> {

    //상수
    final static String TAG = "NOTICE_ADAPTER_TAG";

    Context context;
    ArrayList<QueryDocumentSnapshot> noticeItemList;


    public SetProfileNoticeAdapter(Context context, ArrayList<QueryDocumentSnapshot> noticeItemList) {
        this.context = context;
        this.noticeItemList = noticeItemList;
    }

    @NonNull
    @Override
    public NoticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_notice, parent, false);
        return new NoticeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NoticeViewHolder noticeViewHolder, final int position) {
        QueryDocumentSnapshot documentSnapshot = noticeItemList.get(position);
        if (documentSnapshot != null) {
            if(documentSnapshot.get("title") != null ){
                noticeViewHolder.notice_title.setText(Html.fromHtml((String) documentSnapshot.get("title")));
            }

            if(documentSnapshot.get("content") != null){
                noticeViewHolder.notice_content.setText(Html.fromHtml((String) documentSnapshot.get("content")));
            }

            if (documentSnapshot.get("date") != null){
                noticeViewHolder.notice_date.setText( (String) documentSnapshot.get("date"));
            } else {
                noticeViewHolder.notice_date.setVisibility(View.GONE);
            }
        }
    }

    public void clear(){
        noticeItemList.clear();
    }

    public void add(QueryDocumentSnapshot item){
        noticeItemList.add(item);
    }

    @Override
    public int getItemCount() {
        return noticeItemList.size();
    }

    class NoticeViewHolder extends RecyclerView.ViewHolder {
        TextView notice_title;
        TextView notice_content;
        TextView notice_date;
        ImageView notice_arrow;
        LinearLayout notice_title_linear;
        LinearLayout notice_content_linear;

        public NoticeViewHolder(@NonNull View itemView) {
            super(itemView);
            notice_title = itemView.findViewById(R.id.item_notice_tv_title);
            notice_content = itemView.findViewById(R.id.item_notice_tv_content);
            notice_date = itemView.findViewById(R.id.item_notice_tv_date);
            notice_arrow = itemView.findViewById(R.id.item_notice_iv_arrow);
            notice_title_linear = itemView.findViewById(R.id.item_notice_linear_title);
            notice_content_linear = itemView.findViewById(R.id.item_notice_linear_content);

            notice_title_linear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        if(notice_content_linear.getVisibility() == View.GONE){
                            notice_content_linear.setVisibility(View.VISIBLE);
                            notice_arrow.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_chevron_down_gray));

                        }else if(notice_content_linear.getVisibility() == View.VISIBLE){
                            notice_content_linear.setVisibility(View.GONE);
                            notice_arrow.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_chevron_right_gray));
                        }
                    }
                }
            });
        }
    }

}
