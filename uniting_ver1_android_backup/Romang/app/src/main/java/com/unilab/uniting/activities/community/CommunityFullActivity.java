package com.unilab.uniting.activities.community;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.unilab.uniting.R;
import com.unilab.uniting.activities.PhotoViewActivity;
import com.unilab.uniting.adapter.community.CommunityCommentAdapter;
import com.unilab.uniting.fragments.dialog.DialogDeleteFragment;
import com.unilab.uniting.fragments.dialog.DialogMore2Fragment;
import com.unilab.uniting.fragments.dialog.DialogMoreFragment;
import com.unilab.uniting.fragments.dialog.DialogOkFragment;
import com.unilab.uniting.model.CommunityComment;
import com.unilab.uniting.model.CommunityPost;
import com.unilab.uniting.model.Fcm;
import com.unilab.uniting.model.Notification;
import com.unilab.uniting.utils.BasicActivity;
import com.unilab.uniting.utils.DateUtil;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.LogData;
import com.unilab.uniting.utils.MyProfile;
import com.unilab.uniting.utils.Nickname;
import com.unilab.uniting.utils.OnSingleClickListener;
import com.unilab.uniting.utils.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommunityFullActivity extends BasicActivity implements DialogDeleteFragment.DeleteListener {
    //TAG
    final static String TAG = "CommunityFullActivityT";

    //value
    private String mLocation = "";
    private ArrayList<CommunityComment> mCommentArrayList;
    private CommunityCommentAdapter mCommunityCommentAdapter;
    private String mReplyWriterUid = ""; //????????? ????????? ????????? ????????? uid
    private String mReplyCommentUid = ""; //????????? ????????? ?????? uid
    private String mReplyCommentNickname = ""; //????????? ????????? ????????? ?????????
    private String mRandomNickName = ""; //????????? ???????????????
    private List<String> mCommentWriterUidList;
    private List<String> mLikeList;
    private HashMap<String,String> mNicknameList;
    CommunityPost post;
    String gender, nickName, title, commentNum, recommendNum, content, date, postId, postWriterUid, message, photoUrl;
    long views = 0;
    boolean deleted, expired;
    InputMethodManager imm; //????????????????????????
    boolean isNewbie = true;
    boolean isLikedAlready = true;
    boolean isLikeLoading = false;
    Bus bus = CommunityBusProvider.getInstance();
    OnSingleClickListener onClickListener;
    CommunityPost reportCommunityPost; //????????? ?????????
    CommunityComment reportCommunityComment; //????????? ??????

    //xml
    private RelativeLayout mLoaderLayout;
    private CircleImageView mProfileCircleImageView;
    private TextView mNickNameTextView;
    private TextView mDateTextView;
    private TextView mTitleTextView;
    private TextView mContentTextView;
    private TextView mCommentNumTextView;
    private TextView mRecommendNumTextView;
    private ImageView mRecommendImageView;
    private ImageView mSendImageView;
    private EditText mWriteEditText;
    private RecyclerView mCommentRecyclerView;
    private ImageView mImageView;
    private LinearLayout mBackLinearLayout;
    private LinearLayout mOptionLinearLayout; //????????? ????????????
    private LinearLayout mWriteLinearLayout; //???????????? ??? ??????????????? ????????????
    private LinearLayout mTopLinearLayout; //????????? ????????????

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_full);

        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); //????????? UI ???????????? ??????
        bus.register(this); //???????????????


        init();//?????????
        initAdapter();//?????????????????? ???????????????
        updateUI();//?????? ???????????????
        setOnClickListener();//???????????????
        setPostView();
        getPostView();
        setLogData();
    }


    @Override
    protected void onStart() {
        super.onStart();

        commentListener();//??????????????? ?????? ??? ????????????
        postListener();//????????? ??? ????????? ????????? ????????? ??????.
    }

    private void init() {
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);//?????????
        mProfileCircleImageView = findViewById(R.id.community_fullcommunity_civ_profile);
        mNickNameTextView = findViewById(R.id.community_fullcommunity_tv_nickname);
        mDateTextView = findViewById(R.id.community_fullcommunity_tv_time);
        mTitleTextView = findViewById(R.id.community_fullcommunity_tv_title);
        mContentTextView = findViewById(R.id.community_fullcommunity_tv_content);
        mCommentNumTextView = findViewById(R.id.community_fullcommunity_tv_commentnum);
        mRecommendNumTextView = findViewById(R.id.community_fullcommunity_tv_recommendnum);
        mRecommendImageView = findViewById(R.id.community_fullcommunity_iv_recommend);
        mSendImageView = findViewById(R.id.community_fullcommunity_iv_send);
        mWriteEditText = findViewById(R.id.community_fullcommunity_et_write);
        mCommentRecyclerView = findViewById(R.id.community_fullcommunity_recycler);
        mImageView = findViewById(R.id.community_fullcommunity_iv_image);
        mOptionLinearLayout = findViewById(R.id.community_fulltoolbar_more);
        mWriteLinearLayout = findViewById(R.id.community_fullcommunity_linear_writelinear);
        mTopLinearLayout = findViewById(R.id.community_fullcommunity_linear_toplinear);
        mBackLinearLayout = findViewById(R.id.community_fulltoolbar_back);

        //??????, ?????? ????????????
        mLoaderLayout = findViewById(R.id.loaderLayout);
        mLoaderLayout.setClickable(true);

        Intent intent = getIntent();
        post = (CommunityPost) intent.getSerializableExtra(Strings.EXTRA_COMMUNITY_POST);

    }

    private void setPostView(){
        Map<String,Object> viewData = new HashMap<>();
        viewData.put("view", FieldValue.increment(1));
        viewData.put(FirebaseHelper.postId, post.getPostId());
        FirebaseHelper.db.collection("CommunityPosts").document(post.getPostId()).collection("PostViews").document(post.getPostId())
                .set(viewData, SetOptions.merge());
        FirebaseHelper.db.collection("CommunityPosts").document(post.getPostId())
                .set(viewData, SetOptions.merge());
    }

    private void getPostView(){
        FirebaseHelper.db.collection("CommunityPosts").document(post.getPostId()).collection("PostViews").document(post.getPostId())
                .addSnapshotListener(CommunityFullActivity.this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        if (snapshot != null && snapshot.exists()) {
                            Log.d(TAG, "Current data: " + snapshot.getData());
                            try{
                                views = (long) snapshot.get("view");
                                mDateTextView.setText(date + ",  ????????? : " + views);
                            } catch (Exception err){
                                Log.d(TAG, "test22222: " + err);
                            }
                        } else {
                            Log.d(TAG, "Current data: null");
                        }
                    }
                });

    }


    private void setLogData(){
        Bundle eventBundle = new Bundle();
        eventBundle.putInt(LogData.dia, 0);
        eventBundle.putString(LogData.postId, postId);
        LogData.customLog(LogData.community_s01_post_view,  eventBundle, CommunityFullActivity.this);
        LogData.setStageCommunity(LogData.community_s01_post_view,  CommunityFullActivity.this);
    }





    private void initAdapter() {
        mCommentArrayList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        mCommentRecyclerView.setLayoutManager(layoutManager);
        mCommunityCommentAdapter = new CommunityCommentAdapter(getApplicationContext(), mCommentArrayList);
        mCommentRecyclerView.setAdapter(mCommunityCommentAdapter);
    }

    //?????? ???????????????
    private void updateUI() {
        reportCommunityPost = post;
        gender = post.getWriterGender();
        nickName = post.getWriterNickName();
        title = post.getTitle();
        commentNum = post.getCommentList().size() + "";
        recommendNum = post.getLike() + "";
        content = post.getContent();
        date = DateUtil.unixToTimeAndDayKR(post.getCreateTimestamp());
        postId = post.getPostId();
        postWriterUid = post.getWriterUid();
        photoUrl = post.getPhotoUrl();
        deleted = post.isDeleted();
        expired = post.isExpired();
        mCommentWriterUidList = post.getCommentWriterUidList();
        mNicknameList = post.getNicknameList();
        mLikeList = post.getLikeList();

        if (mLikeList.contains(FirebaseHelper.mUid)) { //?????? ????????? ??????
            isLikedAlready = true;
        } else {
            isLikedAlready = false;
        }

        if (mNicknameList.containsKey(FirebaseHelper.mUid)){ //???????????? ?????? ???????????? ?????? ?????? (?????? ???????????? ?????? commentList?????? ????????? nicknameList?????? ?????? ??? ??????)
            isNewbie = false;
            mRandomNickName = mNicknameList.get(FirebaseHelper.mUid);
        } else {//???????????? ???????????? ?????? ?????? ??????
            isNewbie = true;
        }

        //??????
        if (gender.equals("??????")) {
            mProfileCircleImageView.setImageResource(R.drawable.ic_community_male_example);
        } else {
            mProfileCircleImageView.setImageResource(R.drawable.ic_community_girl_example);
        }
        Log.d(TAG, "?????? url : " + photoUrl);

        if (photoUrl != null) {
            if (!this.isDestroyed()) {
                Glide.with(this).load(photoUrl).thumbnail(0.1f).into(mImageView);
            }
        }

        if(photoUrl == null || photoUrl.equals("") || deleted){
            mImageView.setVisibility(View.GONE);
        }

        if (deleted) {
            mTitleTextView.setText("????????? ????????????.");
            mContentTextView.setText("????????? ????????????.");
        } else {
            mNickNameTextView.setText(nickName);
            mTitleTextView.setText(title);
            mContentTextView.setText(content);

        }

        mCommentNumTextView.setText(commentNum);
        mRecommendNumTextView.setText(recommendNum);
        mDateTextView.setText(date + ",  ????????? : " + views);
    }


    private void setOnClickListener() {
        onClickListener = new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                switch (v.getId()) {
                    case R.id.community_fullcommunity_iv_send: //????????????
                        if (!deleted) {
                            message = mWriteEditText.getText().toString();
                            if (message.length() <= 0) {
                                Toast.makeText(getApplicationContext(), "???????????? ??????????????????", Toast.LENGTH_SHORT).show();
                            } else {
                                saveCommentToDB();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "????????? ????????????.", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.community_fulltoolbar_more: //?????????
                        Bundle bundle = new Bundle();
                        mLocation = "?????????";
                        bundle.putString(Strings.EXTRA_LOCATION, mLocation);
                        bundle.putSerializable(Strings.EXTRA_COMMUNITY_POST, reportCommunityPost);
                        if (reportCommunityPost.getWriterUid().equals(MyProfile.getUser().getUid())) {
                            DialogMore2Fragment dialog = DialogMore2Fragment.getInstance();
                            dialog.setArguments(bundle);
                            dialog.show(getSupportFragmentManager(), DialogMore2Fragment.TAG_EVENT_DIALOG);
                        } else {
                            DialogMoreFragment dialog = DialogMoreFragment.getInstance();
                            dialog.setArguments(bundle);
                            dialog.show(getSupportFragmentManager(), DialogMoreFragment.TAG_EVENT_DIALOG);
                        }

                        break;
                    case R.id.community_fullcommunity_iv_image:
                        Intent photoIntent = new Intent(CommunityFullActivity.this, PhotoViewActivity.class);
                        photoIntent.putExtra(PhotoViewActivity.EXTRA_PHOTOVIEW, photoUrl);
                        startActivity(photoIntent);
                        break;
                    case R.id.community_fullcommunity_iv_recommend:
                        if (!isLikeLoading) {
                            isLikeLoading = true;
                            if (!isLikedAlready) {
                                FirebaseHelper.db.collection("CommunityPosts").document(postId).update(
                                        "like", FieldValue.increment(1),
                                        "likeList", FieldValue.arrayUnion(FirebaseHelper.mUid)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getApplicationContext(), "?????????????????????.", Toast.LENGTH_SHORT).show();

                                        Bundle eventBundle = new Bundle();
                                        eventBundle.putInt(LogData.dia, 0);
                                        eventBundle.putString(LogData.postId, post.getPostId());
                                        LogData.customLog(LogData.community_post_like,  eventBundle, CommunityFullActivity.this);

                                        isLikedAlready = true;
                                        isLikeLoading = false;
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "??????????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                                        isLikeLoading = false;
                                    }
                                });
                            } else {
                                Toast.makeText(getApplicationContext(), "?????? ????????? ????????????.", Toast.LENGTH_SHORT).show();
                                isLikeLoading = false;
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "??????????????????.", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.community_fulltoolbar_back:
                        onBackPressed();
                        break;
                }
            }
        };

        mSendImageView.setOnClickListener(onClickListener);
        mOptionLinearLayout.setOnClickListener(onClickListener);
        mRecommendImageView.setOnClickListener(onClickListener);
        mWriteLinearLayout.setOnClickListener(onClickListener);
        mImageView.setOnClickListener(onClickListener);
        mBackLinearLayout.setOnClickListener(onClickListener);
    }


    @Subscribe //????????? ?????????
    public void replyComment(CommunityComment comment) {
        Log.d(TAG, "flag??? : " + comment.getFlag());
        if (comment.getFlag() == 0) {
            Log.d(TAG, "replyComment otto ????????? ??????");
            mReplyWriterUid = comment.getCommentWriterUid();
            mReplyCommentUid = comment.getCommentId(); //???????????? ???????????? ??? ??? ??????. ???, ???????????? ?????????????????? ???????????? ????????? ????????? ?????? ?????????.
            mReplyCommentNickname = comment.getCommentWriterNickname() + "";
            mWriteEditText.setHint("#????????? ??????????????????");
            //??????????????????
            mWriteEditText.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mWriteEditText.requestFocus();
                    imm.showSoftInput(mWriteEditText, 0);

                }
            }, 100);
        } else {
            Log.d(TAG, "replyComment otto  ???????????? ??????");
            mTopLinearLayout.setBackgroundResource(R.color.colorLightGray243);
            reportCommunityComment = comment;
            mLocation = "??????";

            Bundle bundle = new Bundle();
            bundle.putString(Strings.EXTRA_LOCATION, mLocation);
            bundle.putSerializable(Strings.EXTRA_COMMUNITY_COMMENT, reportCommunityComment);
            if (reportCommunityComment.getCommentWriterUid().equals(MyProfile.getUser().getUid())) {
                DialogMore2Fragment dialog = DialogMore2Fragment.getInstance();
                dialog.setArguments(bundle);
                dialog.show(getSupportFragmentManager(), DialogMore2Fragment.TAG_EVENT_DIALOG);
            } else {
                DialogMoreFragment dialog = DialogMoreFragment.getInstance();
                dialog.setArguments(bundle);
                dialog.show(getSupportFragmentManager(), DialogMoreFragment.TAG_EVENT_DIALOG);
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
    }

    //????????? ????????? ?????????
    private void postListener() {
        FirebaseHelper.db.collection("CommunityPosts").document(postId).addSnapshotListener(CommunityFullActivity.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData());
                    CommunityPost newPost = snapshot.toObject(CommunityPost.class);
                    post = newPost;
                    //?????????????????? ??? ???????????? ??????, ????????? ????????? ????????????
                    mRecommendNumTextView.setText(post.getLike() + "");
                    mLikeList = post.getLikeList();
                    mNicknameList = post.getNicknameList();
                    if (mLikeList.contains(FirebaseHelper.mUid)) { //?????? ????????? ??????
                        isLikedAlready = true;
                    } else {
                        isLikedAlready = false;
                    }

                    if (mNicknameList.containsKey(FirebaseHelper.mUid)){ //???????????? ?????? ???????????? ?????? ?????? (?????? ???????????? ?????? commentList?????? ????????? nicknameList?????? ?????? ??? ??????)
                        isNewbie = false;
                        mRandomNickName = mNicknameList.get(FirebaseHelper.mUid);
                    } else {//???????????? ???????????? ?????? ?????? ??????
                        isNewbie = true;
                    }

                    updateUI();
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }

    //????????? ???????????? ????????? (?????????????????????????????? loadCommentFromDB() ???????????????)
    private void commentListener() {
        mCommentArrayList.clear();
        mCommunityCommentAdapter.clear();
        FirebaseHelper.db.collection("CommunityPosts").document(postId).collection("CommunityComments").whereEqualTo("expired", false).orderBy(FirebaseHelper.createTimestamp, Query.Direction.ASCENDING)
                .addSnapshotListener(CommunityFullActivity.this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            CommunityComment comment = dc.getDocument().toObject(CommunityComment.class);
                            switch (dc.getType()) {
                                case ADDED:
                                    if (comment.getReplyCommentId().equals("")) { //?????? ????????? ??????
                                        mCommentArrayList.add(comment);
                                    } else { //???????????? ??????
                                        for (int i = 0; i < mCommentArrayList.size(); i++) { //??? ???????????? ???????????? ?????????(?????? ????????? ???????????????) ???????????? ??????
                                            CommunityComment comment2 = mCommentArrayList.get(i);
                                            if (comment2.getCommentId().equals(comment.getReplyCommentId())) { //????????? ?????? ??????!
                                                int originIndex = mCommentArrayList.indexOf(comment2); //???????????? Index(??????)
                                                boolean isLastComment = true; //'?????? ??????'??? ????????? ????????? ?????? true, ?????? ?????? ????????? ?????? ?????? false.
                                                for (int j = originIndex + 1; j < mCommentArrayList.size() - 1; j++) { //????????? ???????????? ???????????? ?????? ???????????? ?????? ?????? (?????? ?????? ????????? ?????? ??? ????????? ??????????????????)
                                                    CommunityComment comment3 = mCommentArrayList.get(j);
                                                    if (comment3.getReplyCommentId().equals("")) { //?????? ?????? ?????? ??????!
                                                        isLastComment = false;
                                                        int nextIndex = mCommentArrayList.indexOf(comment3);
                                                        mCommentArrayList.add(nextIndex, comment);
                                                        break;
                                                    }
                                                }
                                                if (isLastComment) { //????????? ??? ??????????????? ?????? ?????? ????????? ?????? ??????. ?????? ???????????? ???????????? ???.
                                                    mCommentArrayList.add(comment);
                                                }
                                                break;
                                            }
                                        }
                                    }

                                    break;
                                case MODIFIED:
                                    for (int i = 0; i < mCommentArrayList.size(); i++) { //??? ???????????? ???????????? ??????(????????? ?????? ????????? ?????? ?????????) ???????????? ??????
                                        CommunityComment comment2 = mCommentArrayList.get(i);
                                        if (comment2.getCommentId().equals(comment.getCommentId())) { //?????? ?????? ??????!
                                            int originIndex = mCommentArrayList.indexOf(comment2); //???????????? Index(??????)
                                            mCommentArrayList.remove(originIndex);
                                            mCommentArrayList.add(originIndex, comment);
                                            break;
                                        }
                                    }
                                    break;
                                case REMOVED:
                                    for (int i = 0; i < mCommentArrayList.size(); i++) { //??? ???????????? ???????????? ??????(????????? ?????? ????????? ?????? ?????????) ???????????? ??????
                                        CommunityComment comment2 = mCommentArrayList.get(i);
                                        if (comment2.getCommentId().equals(comment.getCommentId())) { //?????? ?????? ??????!
                                            int originIndex = mCommentArrayList.indexOf(comment2); //???????????? Index(??????)
                                            mCommentArrayList.remove(originIndex);
                                            break;
                                        }
                                    }
                                    break;
                            }
                        }
                        mCommunityCommentAdapter.notifyDataSetChanged();
                        mCommentNumTextView.setText(mCommentArrayList.size() + ""); //???????????? ???????????? ??????

                    }
                });
    }

    private void saveCommentToDB() {
        if (isNewbie){
            if (mNicknameList.size() > 1500 ){ //???????????? 1850???????????? ????????????, ?????? 1500???????????? ???????????? ?????? ???. ????????? ????????? ??????.
                Toast.makeText(getApplicationContext(), "??? ?????? ????????? ??? ??? ????????????.", Toast.LENGTH_SHORT).show();
                return;
            }
            while (true){
                String tmpNickName = new Nickname().getRandomNickname();
                if (!mNicknameList.containsValue(tmpNickName)) {//?????? ???????????? ?????? ??????
                    mRandomNickName = tmpNickName;
                    break;
                }
            }
        }

        WriteBatch batch = FirebaseHelper.db.batch();
        if (!mReplyCommentUid.equals("") && !post.getWriterUid().equals(FirebaseHelper.mUid) && !mReplyWriterUid.equals(FirebaseHelper.mUid) && !mReplyWriterUid.equals(post.getWriterUid())) { //???????????? ??????, ???????????? ????????? ???????????? ????????? ?????? ???????????? ?????? ???????????????.
            Notification replyNoti = new Notification("??????", post, DateUtil.getDateMin(), DateUtil.getUnixTimeLong(),mRandomNickName, "", mReplyWriterUid, false);
            Fcm fcm = new Fcm(mReplyWriterUid, post.getTitle(),  "????????? ????????? ???????????? ???????????????.", post, post.getPostId(), FirebaseHelper.communityPost, FirebaseHelper.comment2, DateUtil.getDateSec());
            batch.set(FirebaseHelper.db.collection("Users").document(mReplyWriterUid).collection("Notification").document(DateUtil.getTimeStampUnix()), replyNoti); //?????? ?????? ?????????????????? DB??? ??????
            batch.set(FirebaseHelper.db.collection("Users").document(mReplyWriterUid).collection("Fcm").document(), fcm);
        }

        if (!post.getWriterUid().equals(FirebaseHelper.mUid)) { //???????????? ????????? ????????? ?????? ???????????? ????????? ??????????????? noti
            Notification postNoti = new Notification("??????", post, DateUtil.getDateMin(), DateUtil.getUnixTimeLong(),mRandomNickName, "", post.getWriterUid(), false);
            Fcm fcm = new Fcm(post.getWriterUid(), post.getTitle(),  "???????????? ????????? ???????????????.", post, post.getPostId(), FirebaseHelper.communityPost, FirebaseHelper.comment, DateUtil.getDateSec());
            batch.set(FirebaseHelper.db.collection("Users").document(post.getWriterUid()).collection("Notification").document(DateUtil.getTimeStampUnix()), postNoti); //?????? ?????????????????? DB??? ??????
            batch.set(FirebaseHelper.db.collection("Users").document(post.getWriterUid()).collection("Fcm").document(), fcm);
        }

        DocumentReference commentRef = FirebaseHelper.db.collection("CommunityPosts").document(postId).collection("CommunityComments").document();
        String commentId = commentRef.getId();
        CommunityComment comment = new CommunityComment(commentId, postId, postWriterUid, mRandomNickName, FirebaseHelper.mUid, MyProfile.getUser().getGender(), DateUtil.getDateComment(), DateUtil.getUnixTimeLong(), message, new ArrayList<String>(), 0, mReplyCommentUid, mReplyCommentNickname, 0, false, false);
        if(isNewbie){
            batch.update(FirebaseHelper.db.collection("CommunityPosts").document(postId),
                    "commentWriterUidList", FieldValue.arrayUnion(FirebaseHelper.mUid),
                    "commentList", FieldValue.arrayUnion(commentId),
                    "nicknameList." + FirebaseHelper.mUid, mRandomNickName);
        } else {
            batch.update(FirebaseHelper.db.collection("CommunityPosts").document(postId), "commentList", FieldValue.arrayUnion(commentId));
        }
        batch.set(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("CommunityMyComment").document(post.getPostId()), post); //?????? DB??? ????????? ??????
        batch.set(commentRef, comment);
        mLoaderLayout.setVisibility(View.VISIBLE);
        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mLoaderLayout.setVisibility(View.GONE);

                Bundle eventBundle = new Bundle();
                eventBundle.putInt(LogData.dia, 0);
                eventBundle.putString(LogData.postId, post.getPostId());
                eventBundle.putString(LogData.commentId, comment.getCommentId());
                LogData.customLog(LogData.community_s02_comment,  eventBundle, CommunityFullActivity.this);
                LogData.setStageCommunity(LogData.community_s02_comment,  CommunityFullActivity.this);

                isNewbie = false;
                mWriteEditText.setText("");
                imm.hideSoftInputFromWindow(mWriteEditText.getWindowToken(), 0); //??????????????????
                if (!mReplyCommentUid.equals("")){ //????????? ??????
                    mWriteEditText.setHint("????????? ??????????????????");
                    mReplyCommentUid = "";
                    mReplyCommentNickname = "";
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mLoaderLayout.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "???????????? ??????", Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public void deleteMeeting() {

    }

    @Override
    public void deletePost(CommunityPost post) {
        mLoaderLayout.setVisibility(View.VISIBLE);
        FirebaseHelper.db.collection("CommunityPosts").document(post.getPostId()).update("deleted", true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mLoaderLayout.setVisibility(View.GONE);
                        setLogDelete();
                        //?????? ???????????????
                        DialogOkFragment dialog2 = DialogOkFragment.getInstance();
                        Bundle bundle = new Bundle();
                        bundle.putString(Strings.EXTRA_LOCATION, Strings.deletePost);
                        dialog2.setArguments(bundle);
                        dialog2.show(getSupportFragmentManager(), DialogOkFragment.TAG_MEETING_DIALOG2);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mLoaderLayout.setVisibility(View.GONE);
                Toast.makeText(CommunityFullActivity.this, "??????????????? ??????????????????", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLogDelete(){
        Bundle eventBundle = new Bundle();
        eventBundle.putInt(LogData.dia, 0);
        eventBundle.putString(LogData.postId, postId);
        LogData.customLog(LogData.community_post_delete, eventBundle, CommunityFullActivity.this);
        LogData.setStageCommunity(LogData.community_post_delete,  CommunityFullActivity.this);
    }

    @Override
    public void deleteComment(CommunityComment comment) {
        mLoaderLayout.setVisibility(View.VISIBLE);
        FirebaseHelper.db.collection("CommunityPosts").document(comment.getPostId()).collection("CommunityComments").document(comment.getCommentId()).update("deleted", true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mLoaderLayout.setVisibility(View.GONE);
                        //?????? ???????????????
                        DialogOkFragment dialog2 = DialogOkFragment.getInstance();
                        Bundle bundle = new Bundle();
                        bundle.putString(Strings.EXTRA_LOCATION, Strings.deleteComment);
                        dialog2.setArguments(bundle);
                        dialog2.show(getSupportFragmentManager(), DialogOkFragment.TAG_MEETING_DIALOG2);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mLoaderLayout.setVisibility(View.GONE);
                Toast.makeText(CommunityFullActivity.this, "??????????????? ??????????????????", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

