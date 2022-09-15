package com.unilab.uniting.adapter.community;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unilab.uniting.R;
import com.unilab.uniting.activities.community.CommunityFullActivity;
import com.unilab.uniting.model.CommunityPost;
import com.unilab.uniting.utils.DateUtil;
import com.unilab.uniting.utils.MyProfile;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommunityPostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<CommunityPost> items;
    String board;
    boolean isMyPost;
    boolean isBestPostExist = false;

    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;
    private boolean isLoading;
    private OnLoadMoreListener onLoadMoreListener;

    //putExtra Key
    static final String EXTRA_COMMUNITY_POST = "EXTRA_COMMUNITY_POST";

    //상수
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    public final static String BOARD = "BOARD";
    public final static String UNIVERSITY = "UNIVERSITY";
    public final static String EVERYBODY = "EVERYBODY";


    @Override
    public int getItemViewType(int position) { //null값인 경우 로딩타입
        return items.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public CommunityPostAdapter(RecyclerView recyclerView, Context context, ArrayList<CommunityPost> items, String board, boolean isMyPost) {
        this.context = context;
        this.items = items;
        this.board = board;
        this.isMyPost = isMyPost;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d("체크 :" , "토탈카운트: " + totalItemCount + "  , 마지막으로보이는아이템: " + lastVisibleItem );
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                //로딩중이아니고 , 전체아이템수 <= 마지막에 보이는 아이템인덱스 + 화면에보이는개수(리사이클러뷰에 아이템이 5개씩 보이므로 5로 설정함
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
    }


    @NonNull
    @Override //로딩 뷰타입과 아이템 뷰타입으로 나뉨
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_community_post, viewGroup, false);
            return  new CommunityPostViewHolder(view);
        }else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_loading, viewGroup, false);
           return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        if(holder instanceof CommunityPostViewHolder) {
            CommunityPostViewHolder communityPostViewHolder = (CommunityPostViewHolder) holder;
            CommunityPost model = items.get(i);

            if( i == 0 ){
                if(isBestPostExist){
                    communityPostViewHolder.bestTextView.setVisibility(View.VISIBLE);
                } else {
                    communityPostViewHolder.bestTextView.setVisibility(View.GONE);
                }
            } else {
                communityPostViewHolder.bestTextView.setVisibility(View.GONE);
            }

            if (model.getWriterGender().equals("남자")) {
                communityPostViewHolder.profileCircleImageView.setImageResource(R.drawable.ic_community_male_example);
            } else {
                communityPostViewHolder.profileCircleImageView.setImageResource(R.drawable.ic_community_girl_example);
            }
            if(model.isDeleted()){
                communityPostViewHolder.contentTextView.setText("삭제된 글입니다.");
            }else{
                communityPostViewHolder.contentTextView.setText(model.getTitle());
            }
            communityPostViewHolder.dateTextView.setText(DateUtil.dayForCommunityList(model.getCreateTimestamp()));
            communityPostViewHolder.commentCountTextView.setText(model.getCommentList().size() + "");
            communityPostViewHolder.recommendCountTextView.setText(model.getLike() + "");

            if(model.getBoard()!= null){
                if(model.getBoard().equals(EVERYBODY)){
                    communityPostViewHolder.boardTextView.setText("전체 게시판");
                }else{
                    communityPostViewHolder.boardTextView.setText(MyProfile.getUser().getUniversity() + " 게시판");
                }
            }

        }else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }

    }

    @Override
    public int getItemCount() {
        return items == null ? 0 :items.size();
    }

    public void setLoaded() {
        isLoading = false;
    }

    public void addItem(CommunityPost item) {
        items.add(item);
    }

    public void clear() {
        items.clear();
    }

    public class CommunityPostViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profileCircleImageView;
        TextView contentTextView;
        TextView dateTextView;
        TextView commentCountTextView;
        TextView recommendCountTextView;
        LinearLayout linearLayout; //전체화면
        TextView boardTextView;
        TextView bestTextView;

        public CommunityPostViewHolder(@NonNull View itemView) {
            super(itemView);
            profileCircleImageView = itemView.findViewById(R.id.item_board_civ_profile);
            contentTextView = itemView.findViewById(R.id.item_board_tv_title);
            dateTextView = itemView.findViewById(R.id.item_board_tv_date);
            commentCountTextView = itemView.findViewById(R.id.item_board_tv_comment);
            recommendCountTextView = itemView.findViewById(R.id.item_board_tv_recommend);
            linearLayout = itemView.findViewById(R.id.item_board_linear);
            boardTextView = itemView.findViewById(R.id.item_board_tv_board);
            bestTextView = itemView.findViewById(R.id.item_board_tv_best);

            if(isMyPost){
                boardTextView.setVisibility(View.VISIBLE);
            }

            //작성글 풀화면
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        if(items.size()> position){
                            CommunityPost item = items.get(position);
                            Intent intent = new Intent(context, CommunityFullActivity.class);
                            intent.putExtra(EXTRA_COMMUNITY_POST, item);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                    }
                }
            });
        }
    }

    // "Loading item" ViewHolder
    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.onLoadMoreListener = mOnLoadMoreListener;
    }

    public void setBestPost(boolean isBestPostExist) {
        this.isBestPostExist = isBestPostExist;
    }
}
