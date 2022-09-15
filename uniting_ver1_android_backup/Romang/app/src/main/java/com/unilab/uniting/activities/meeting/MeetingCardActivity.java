package com.unilab.uniting.activities.meeting;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.unilab.uniting.R;
import com.unilab.uniting.fragments.dialog.DialogDeleteFragment;
import com.unilab.uniting.fragments.dialog.DialogMore2Fragment;
import com.unilab.uniting.fragments.dialog.DialogMoreFragment;
import com.unilab.uniting.fragments.dialog.DialogOkFragment;
import com.unilab.uniting.fragments.meeting.MeetingDialogStep1ApplyFragment;
import com.unilab.uniting.model.CommunityComment;
import com.unilab.uniting.model.CommunityPost;
import com.unilab.uniting.model.Dia;
import com.unilab.uniting.model.Fcm;
import com.unilab.uniting.model.Meeting;
import com.unilab.uniting.model.Notification;
import com.unilab.uniting.model.User;
import com.unilab.uniting.utils.BasicActivity;
import com.unilab.uniting.utils.DateUtil;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.LogData;
import com.unilab.uniting.utils.MyProfile;
import com.unilab.uniting.utils.Numbers;
import com.unilab.uniting.utils.OnSingleClickListener;
import com.unilab.uniting.utils.Strings;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class MeetingCardActivity extends BasicActivity implements MeetingDialogStep1ApplyFragment.MeetingCardListener, DialogDeleteFragment.DeleteListener {

    final String TAG = "MeetingCardActivityTAG";
    final static String LOCATION_MEETING_CARD = "LOCATION_MEETING_CARD";

    //xml
    private TextView mGuideTextView;
    private TextView mAgeTextView;
    private TextView mDateTextView;
    private TextView mTitleTextView;
    private TextView mContentTextView;
    private Button mProfileConfirmButton;
    private ImageView mPhotoImageView;
    private CircleImageView mProfileCircleImageView;
    private Button mStep1ListButton;
    private LinearLayout mBack;
    private LinearLayout mMore;
    private RelativeLayout mLoaderLayout; //로딩레이아

    private FirebaseFunctions mFunctions;

    //value
    private String mLocation  = "";
    private boolean deleted;
    private boolean expired;
    private Meeting meeting;
    private String mMeetingUid = "";
    private String mHostUid = "";
    private User mHostUser = null;
    OnSingleClickListener onClickListener;
    ListenerRegistration meetingStateListener;

    final static int CHECK_DONE = 4;
    boolean isApplyChecked = false;
    boolean isBlocked = false;
    boolean isBlockChecked = false;
    boolean isErrorOccurred = false;
    boolean isApplied = false; //이미신청했는지
    boolean isAccepted = false; //신청이 수락되었는지
    boolean isApplyBtnClicked = false;

    int countDone = 0;
    private String result = "block";
    private String meetingStep1State = FirebaseHelper.PRE_APPLY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_card);

        init();
        updateUI();
        setLogData();
        setOnClickListener();
        checkBlockByCloudFunction();

    }

    @Override
    protected void onStart() {
        super.onStart();

        //신청/수락 상태 리스너
        meetingStateListener();
        meetingDeletedListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (meetingStateListener != null){
            meetingStateListener.remove();
        }

    }

    private void init(){
        mGuideTextView = findViewById(R.id.meeting_card_tv_guide);
        mAgeTextView = findViewById(R.id.meeting_card_tv_age);
        mDateTextView = findViewById(R.id.meeting_card_tv_date);
        mTitleTextView = findViewById(R.id.meeting_card_tv_title);
        mContentTextView = findViewById(R.id.meeting_card_tv_content);
        mProfileConfirmButton = findViewById(R.id.meeting_card_tv_profileconfirm);
        mPhotoImageView = findViewById(R.id.meeting_card_iv_setimage);
        mProfileCircleImageView = findViewById(R.id.meeting_card_civ_profile);
        mStep1ListButton = findViewById(R.id.meeting_card_btn_step1list);
        mBack = findViewById(R.id.meeting_card_iv_back);
        mMore = findViewById(R.id.meeting_card_linear_more);

        //로딩 레이아웃
        mLoaderLayout = findViewById(R.id.loaderLayout);
        mLoaderLayout.setClickable(true);

        Intent resultIntent = getIntent();
        meeting = (Meeting) resultIntent.getSerializableExtra(Strings.EXTRA_MEETING);

        deleted = meeting.isDeleted();
        expired = meeting.isExpired();
        mMeetingUid = meeting.getMeetingId();
        mHostUid = meeting.getHostUid();
    }


    private void updateUI(){
        if (meeting.getHostGender().equals("남자")) {
            mProfileCircleImageView.setImageResource(R.drawable.ic_user_male);
        } else if (meeting.getHostGender().equals("여자")) {
            mProfileCircleImageView.setImageResource(R.drawable.ic_user_female);
        }

        if(meeting.getTitle().equals(meeting.getContent())){
            mTitleTextView.setVisibility(View.GONE);
        }

        if(deleted){
            mPhotoImageView.setImageURI(null);
            mAgeTextView.setVisibility(View.GONE);
            mDateTextView.setText(DateUtil.unixToTimeAndDayKR(meeting.getCreateTimestamp()));
            mTitleTextView.setText("삭제된 미팅입니다.");
            mContentTextView.setText("삭제된 미팅입니다.");
            return;
        }

        if (!meeting.getPhotoUrl().equals("") && (!this.isDestroyed())) {
            if(meeting.isBlurred()){
                Glide.with(this).load(meeting.getPhotoUrl()).fitCenter().apply(bitmapTransform(new BlurTransformation(8, 7))).into(mPhotoImageView);
            }else {
                Glide.with(this).load(meeting.getPhotoUrl()).fitCenter().thumbnail(0.1f).into(mPhotoImageView);
            }

        }
        mAgeTextView.setText(meeting.getHostUniversity());
        if(meeting.getHostUniversity().equals("")){
            mAgeTextView.setVisibility(View.GONE);
        } else {
            mAgeTextView.setVisibility(View.VISIBLE);
        }
        mDateTextView.setText(DateUtil.unixToDayKR(meeting.getCreateTimestamp()));
        mTitleTextView.setText(meeting.getTitle());
        mContentTextView.setText(meeting.getContent());
    }

    private void setLogData(){
        Bundle eventBundle = new Bundle();
        eventBundle.putInt(LogData.dia, 0);
        eventBundle.putString(LogData.meetingId, meeting.getMeetingId());
        LogData.customLog(LogData.meeting_s02_card_view,  eventBundle, MeetingCardActivity.this);

        LogData.setStageMeeting(LogData.meeting_s02_card_view, MeetingCardActivity.this);
        if(meeting.getHostUid().equals(FirebaseHelper.mUid)){
            LogData.setStageMeetingHost(LogData.meeting_s02_card_view,MeetingCardActivity.this);
        } else {
            LogData.setStageMeetingApplicant(LogData.meeting_s02_card_view,MeetingCardActivity.this);
        }
    }


    private void meetingDeletedListener(){
        FirebaseHelper.db.collection("Meetings").document(mMeetingUid)
                .addSnapshotListener(MeetingCardActivity.this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        if (snapshot != null && snapshot.exists()) {
                            Log.d(TAG, "Current data: " + snapshot.getData());
                            //수락/거절 여부 결정된 경우
                            meeting = snapshot.toObject(Meeting.class);
                            updateUI();
                        } else {
                            Log.d(TAG, "Current data: null");
                        }
                    }
                });
    }


    private void meetingStateListener() {
        if (mHostUid.equals(FirebaseHelper.mUid)) { //주최자인경우
            mProfileConfirmButton.setVisibility(View.GONE);
            mStep1ListButton.setVisibility(View.VISIBLE);
            if(deleted){
                mGuideTextView.setText("삭제된 미팅입니다.");
                mStep1ListButton.setVisibility(View.GONE);
            }else{
                mGuideTextView.setText("아래의 신청자 리스트를 클릭하여, 신청자 프로필을 확인하세요!");
            }
            return;
        }

        //참가자인 경우
        mLoaderLayout.setVisibility(View.VISIBLE);
        meetingStateListener = FirebaseHelper.db.collection("Meetings").document(mMeetingUid).collection("Applicant").document(FirebaseHelper.mUid)
                .addSnapshotListener(MeetingCardActivity.this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            mLoaderLayout.setVisibility(View.GONE);
                            return;
                        }

                        isApplyChecked = true;
                        mLoaderLayout.setVisibility(View.GONE);

                        if (snapshot != null && snapshot.exists()) {
                            isApplied = true;
                            //수락/거절 여부 결정된 경우
                            if (snapshot.get(FirebaseHelper.meetingStep1) != null) {
                                meetingStep1State = (String) snapshot.get(FirebaseHelper.meetingStep1);
                                switch (meetingStep1State){
                                    case FirebaseHelper.SCREENING:
                                        mGuideTextView.setText("프로필 열람을 신청하였습니다. 상대방이 수락하시면 알려드립니다.");
                                        break;
                                    case FirebaseHelper.PASS:
                                        //프로필 열람(step1)이 수락된 경우
                                        isAccepted = true;
                                        mGuideTextView.setText("프로필 열람 신청이 수락되었습니다. 프로필을 확인하고 연락처 교환을 신청하세요.");
                                        break;
                                    case FirebaseHelper.FAIL:
                                        //프로필 열람(step1)이 거절된 경우
                                        isAccepted = false;
                                        mGuideTextView.setText("프로필 열람 신청이 거절되어 다이아가 환급되었습니다.");
                                        mProfileConfirmButton.setVisibility(View.GONE);
                                        break;
                                }
                            }
                        } else {//아직 신청 안한 경우: 디폴트값이므로 코드 필요 없음
                            meetingStep1State = FirebaseHelper.PRE_APPLY;
                            isApplied = false;
                        }
                    }
                });
    }

    private void showDialog1(){
        MeetingDialogStep1ApplyFragment dialog = MeetingDialogStep1ApplyFragment.getInstance();
        Bundle bundle = new Bundle();
        bundle.putString(Strings.EXTRA_MEETING_UID, mMeetingUid); //방 uid 전달
        bundle.putString(Strings.EXTRA_HOST, mHostUid);
        bundle.putSerializable(Strings.EXTRA_MEETING, meeting);
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), MeetingDialogStep1ApplyFragment.TAG_MEETING_DIALOG);
    }

    private void showDialog2(String from) {
        Bundle bundle = new Bundle();
        bundle.putString(Strings.EXTRA_LOCATION, from);
        DialogOkFragment dialog2 = DialogOkFragment.getInstance();

        dialog2.setArguments(bundle);
        dialog2.show(getSupportFragmentManager(), DialogOkFragment.TAG_MEETING_DIALOG2);
    }



    private void checkBlockByCloudFunction() {
        if (meeting.getHostGender().equals(MyProfile.getUser().getGender())) {
            result = Strings.gender;
            isBlockChecked = true;
            isBlocked = true;
            return;
        }


        FirebaseHelper.checkBlock(meeting.getHostUid())
                .addOnCompleteListener(new OnCompleteListener<Map<String,Boolean>>() {
                    @Override
                    public void onComplete(@NonNull Task<Map<String,Boolean>> task) {
                        if (!task.isSuccessful()) {
                            Exception e = task.getException();
                            if (e instanceof FirebaseFunctionsException) {
                                FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                FirebaseFunctionsException.Code code = ffe.getCode();
                                Object details = ffe.getDetails();
                            }
                            isErrorOccurred = true;
                            return;
                        }

                        Map<String,Boolean> taskResult = task.getResult();
                        if (taskResult.get(FirebaseHelper.isBlocked) != null){
                            isBlocked = taskResult.get(FirebaseHelper.isBlocked);
                        }

                        if (isBlocked) {
                            if (!result.equals(Strings.gender)){
                                result = Strings.block;
                            }
                        } else {
                            result = Strings.pass;
                        }

                        isErrorOccurred = false;
                        isBlockChecked = true;
                        mLoaderLayout.setVisibility(View.GONE);
                        if(isApplyBtnClicked){
                            applyMeetingStep1();
                        }
                    }
                });
    }




    private void applyMeetingStep1(){
        if(!isApplyChecked){
            return;
        }

        isApplyBtnClicked = true;

        if(!isBlockChecked){
            mLoaderLayout.setVisibility(View.VISIBLE);
            return;
        }

        switch (meetingStep1State){
            case FirebaseHelper.PRE_APPLY:
                if(isErrorOccurred){
                    Toast.makeText(MeetingCardActivity.this, "오류 발생", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (isBlocked) {
                    showDialog2(result);
                } else {
                    showDialog1();
                }

                break;
            case FirebaseHelper.SCREENING:
                showDialog2(Strings.isApplied);
                break;
            case FirebaseHelper.PASS:
                mLoaderLayout.setVisibility(View.VISIBLE);
                FirebaseHelper.db.collection("Users").document(meeting.getHostUid()).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                mLoaderLayout.setVisibility(View.GONE);
                                mGuideTextView.setText("프로필 열람 신청이 수락되었습니다. 프로필을 확인하고 연락처 교환을 신청하세요.");
                                mHostUser = documentSnapshot.toObject(User.class);
                                Intent profileCardIntent = new Intent(MeetingCardActivity.this, ProfileCardMeetingActivity.class);
                                profileCardIntent.putExtra(Strings.partnerUid, mHostUid);
                                profileCardIntent.putExtra(Strings.meetingUid, mMeetingUid);
                                profileCardIntent.putExtra(Strings.partnerUser, mHostUser);
                                profileCardIntent.putExtra(Strings.EXTRA_MEETING, meeting);
                                profileCardIntent.putExtra(Strings.EXTRA_LOCATION, LOCATION_MEETING_CARD);
                                startActivity(profileCardIntent);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mLoaderLayout.setVisibility(View.GONE);
                        Toast.makeText(MeetingCardActivity.this, "네트워크가 불안정합니다. 재시도해주세요.", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case FirebaseHelper.FAIL:
                //프로필 열람(step1)이 거절된 경우
                mGuideTextView.setText("프로필 열람 신청이 거절되었습니다.");
                mProfileConfirmButton.setVisibility(View.GONE);
                showDialog2(Strings.isDenied);
                break;
        }

    }

    private void setOnClickListener() {
        onClickListener = new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                switch (v.getId()) {
                    //프로필확인 신청
                    case R.id.meeting_card_tv_profileconfirm:
                    case R.id.meeting_card_civ_profile:
                        if(!deleted){
                            applyMeetingStep1();
                        }else{
                            Toast.makeText(MeetingCardActivity.this, "삭제된 미팅입니다.", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.meeting_card_btn_step1list:
                        Intent step1Intent = new Intent(MeetingCardActivity.this, MeetingApplicantListActivity.class);
                        step1Intent.putExtra(Strings.EXTRA_MEETING_UID, mMeetingUid);
                        step1Intent.putExtra(Strings.EXTRA_HOST, mHostUid);
                        step1Intent.putExtra(Strings.EXTRA_MEETING, meeting);
                        startActivity(step1Intent);
                        break;
                    case R.id.meeting_card_iv_back:
                        onBackPressed();
                        break;
                    case R.id.meeting_card_linear_more:
                        Bundle bundle = new Bundle();
                        mLocation = "미팅";
                        bundle.putString(Strings.EXTRA_LOCATION, mLocation);
                        bundle.putSerializable(Strings.EXTRA_MEETING, meeting);
                        if(meeting.getHostUid().equals(MyProfile.getUser().getUid())){
                            DialogMore2Fragment dialog = DialogMore2Fragment.getInstance();
                            dialog.setArguments(bundle);
                            dialog.show(getSupportFragmentManager(), DialogMore2Fragment.TAG_EVENT_DIALOG);
                        }else{
                            DialogMoreFragment dialog = DialogMoreFragment.getInstance();
                            dialog.setArguments(bundle);
                            dialog.show(getSupportFragmentManager(), DialogMoreFragment.TAG_EVENT_DIALOG);
                        }
                        break;
                }
            }
        };

        mStep1ListButton.setOnClickListener(onClickListener);
        mBack.setOnClickListener(onClickListener);
        mMore.setOnClickListener(onClickListener);
        mProfileConfirmButton.setOnClickListener(onClickListener);
        mProfileCircleImageView.setOnClickListener(onClickListener);
    }

    @Override
    public void applyStep1() {
        mLoaderLayout.setVisibility(View.VISIBLE);
        FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("Dia").orderBy(FirebaseHelper.diaId, Query.Direction.DESCENDING).limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Dia dia = document.toObject(Dia.class);
                                if (dia.getCurrentDia() < Numbers.MEETING_STEP1_COST) { //하트수가 적을 때
                                    Toast.makeText(MeetingCardActivity.this, "다이아가 부족합니다.", Toast.LENGTH_SHORT).show();
                                    mLoaderLayout.setVisibility(View.GONE);
                                    return;
                                }

                                //하트 변동 내역 계산
                                Dia updatedDia = dia.getUpdatedDia( Numbers.MEETING_STEP1_COST, 0, 0, 0, Strings.MEETING_STEP1_COST, "", mHostUid, mMeetingUid, "");
                                Fcm fcm = new Fcm(mHostUid, meeting.getTitle(),  "미팅 프로필 열람 신청이 왔습니다.", meeting, meeting.getMeetingId(), FirebaseHelper.meeting, FirebaseHelper.profileConfirm, DateUtil.getDateSec());
                                Notification notification = new Notification("프로필열람신청", meeting, DateUtil.getDateMin(),DateUtil.getUnixTimeLong(), MyProfile.getUser().getNickname(),"", mHostUid, false);

                                //DB로 미팅 정보 전송 (미팅doc 업데이트, 하트 내역 업데이트, 유저doc 업데이트)
                                WriteBatch batch = FirebaseHelper.db.batch();
                                batch.set(FirebaseHelper.db.collection("Users").document(mHostUid).collection("Notification").document(DateUtil.getTimeStampUnix()), notification);
                                batch.set(FirebaseHelper.db.collection("Users").document(mHostUid).collection("Fcm").document(), fcm);
                                batch.update(FirebaseHelper.db.collection("Meetings").document(mMeetingUid), "applicantUidList", FieldValue.arrayUnion(FirebaseHelper.mUid));
                                batch.set(FirebaseHelper.db.collection("Meetings").document(mMeetingUid).collection("Applicant").document(FirebaseHelper.mUid), MyProfile.getUser());
                                batch.update(FirebaseHelper.db.collection("Meetings").document(mMeetingUid).collection("Applicant").document(FirebaseHelper.mUid),
                                        FirebaseHelper.meetingStep1, FirebaseHelper.SCREENING,
                                        FirebaseHelper.meetingStep2, FirebaseHelper.PRE_APPLY);
                                batch.set(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("Dia").document(updatedDia.getDiaId()), updatedDia);
                                batch.set(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("MeetingApplied").document(mMeetingUid), meeting);
                                batch.update(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("Permission").document(FirebaseHelper.mUid),
                                        FirebaseHelper.permissionList , FieldValue.arrayUnion(mHostUid));
                                batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mLoaderLayout.setVisibility(View.GONE);

                                        //InteractionHistory
                                        Map<String, Object> unionData = new HashMap<>();
                                        Map<String, Object> unionData2 = new HashMap<>();
                                        unionData.put(FirebaseHelper.meetingStep1ApplyList , FieldValue.arrayUnion(meeting.getHostUid()));
                                        unionData2.put(FirebaseHelper.meetingStep1AppliedList , FieldValue.arrayUnion(FirebaseHelper.mUid));

                                        WriteBatch batch1 = FirebaseHelper.db.batch();
                                        batch1.set(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("InteractionHistory").document(FirebaseHelper.mUid),
                                                unionData, SetOptions.merge());
                                        batch1.set(FirebaseHelper.db.collection("Users").document(meeting.getHostUid()).collection("InteractionHistory").document(meeting.getHostUid()),
                                                unionData2, SetOptions.merge());
                                        batch1.commit();

                                        Bundle eventBundle = new Bundle();
                                        eventBundle.putInt(LogData.dia, Numbers.MEETING_STEP1_COST);
                                        eventBundle.putString(LogData.meetingId, meeting.getMeetingId());
                                        LogData.customLog(LogData.meeting_s03_step1_apply,  eventBundle, MeetingCardActivity.this);

                                        LogData.setStageMeeting(LogData.meeting_s03_step1_apply, MeetingCardActivity.this);
                                        LogData.setStageMeetingApplicant(LogData.meeting_s03_step1_apply,MeetingCardActivity.this);


                                        //확인 다이어로그
                                        DialogOkFragment dialog2 = DialogOkFragment.getInstance();
                                        Bundle bundle = new Bundle();
                                        bundle.putString(Strings.EXTRA_LOCATION, "applyDone");
                                        dialog2.setArguments(bundle);
                                        dialog2.show(getSupportFragmentManager(), DialogOkFragment.TAG_MEETING_DIALOG2);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        mLoaderLayout.setVisibility(View.GONE);
                                        Toast.makeText(MeetingCardActivity.this, "네트워크가 불안정합니다2", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } else {
                            mLoaderLayout.setVisibility(View.GONE);
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            Toast.makeText(MeetingCardActivity.this, "네트워크가 불안정합니다", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void deleteMeeting() {
        mLoaderLayout.setVisibility(View.VISIBLE);
        FirebaseHelper.db.collection("Meetings").document(meeting.getMeetingId()).update(FirebaseHelper.deleted, true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mLoaderLayout.setVisibility(View.GONE);

                        Bundle eventBundle = new Bundle();
                        eventBundle.putInt(LogData.dia, 0);
                        eventBundle.putString(LogData.meetingId, meeting.getMeetingId());
                        LogData.customLog(LogData.meeting_delete, eventBundle, MeetingCardActivity.this);

                        //확인 다이어로그
                        DialogOkFragment dialog2 = DialogOkFragment.getInstance();
                        Bundle bundle = new Bundle();
                        bundle.putString(Strings.EXTRA_LOCATION, Strings.deleteMeeting);
                        dialog2.setArguments(bundle);
                        dialog2.show(getSupportFragmentManager(), DialogOkFragment.TAG_MEETING_DIALOG2);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mLoaderLayout.setVisibility(View.GONE);
                Toast.makeText(MeetingCardActivity.this, "네트워크가 불안정합니다", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void deletePost(CommunityPost post) {

    }

    @Override
    public void deleteComment(CommunityComment comment) {

    }
}
