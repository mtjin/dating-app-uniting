package com.unilab.uniting.activities.report;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.unilab.uniting.R;
import com.unilab.uniting.fragments.dialog.DialogEditPhoto2Fragment;
import com.unilab.uniting.model.ChatRoom;
import com.unilab.uniting.model.CommunityComment;
import com.unilab.uniting.model.CommunityPost;
import com.unilab.uniting.model.Meeting;
import com.unilab.uniting.model.Report;
import com.unilab.uniting.model.User;
import com.unilab.uniting.utils.BasicActivity;
import com.unilab.uniting.utils.DateUtil;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.MyProfile;
import com.unilab.uniting.utils.Strings;
import com.yalantis.ucrop.UCrop;

import java.io.File;

public class ReportWrite extends BasicActivity implements DialogEditPhoto2Fragment.DialogEdit2Listener {

    //putExtra Key
    final static String TAG = "ReportWriteT";

    //xml
    private EditText mWriteEditText;
    private ImageView mUploadImageView;
    private ImageView mSetImageView;
    private TextView mReportTypeTextView;
    private TextView mGuideTextView;
    private LinearLayout mBack;
    private Button mSubmit;
    private RelativeLayout mLoaderLayout;
    private TextView mToolbarTextView;


    //value
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;
    final static int PICK_IMAGE = 1;
    private String mReportLoaction; //댓글, 게시글
    private String mReportType; //신고한 유형
    private CommunityComment mCommunityComment;
    private CommunityPost mCommunityPost;
    private User mPartnerUser;
    private String partnerUid;
    private Meeting mMeeting;
    private ChatRoom mChatRoom;
    private String mPhotoURL = null;
    private Uri mFinalUri = null;
    private boolean isLoading = false;
    private Report mReport;
    private String mTimeStampUnix;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_write);

        init();
        processIntent();
        setOnClickListener();

    }

    private void init(){
        View view = findViewById(R.id.toolbar_write_id);
        mBack = view.findViewById(R.id.toolbar_back);
        mSubmit = view.findViewById(R.id.toolbar_submit_btn);
        mWriteEditText = findViewById(R.id.report_write_et_content);
        mUploadImageView = findViewById(R.id.report_write_iv_uploadimage);
        mReportTypeTextView = findViewById(R.id.report_write_tv_type);
        mGuideTextView = findViewById(R.id.report_write_tv_imagerule);
        mSetImageView = findViewById(R.id.report_write_iv_setimage);
        mToolbarTextView = findViewById(R.id.toolbar_title);
        mToolbarTextView.setText("신고하기");

        //로딩중
        mLoaderLayout = findViewById(R.id.loaderLayout);
        mLoaderLayout.setClickable(true);
    }

    private void setOnClickListener(){
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = mWriteEditText.getText().toString();
                if (content.length() < 1) {
                    Toast.makeText(ReportWrite.this, "내용을 적어주세요", Toast.LENGTH_SHORT).show();
                } else {
                    mTimeStampUnix = DateUtil.getTimeStampUnix();
                    switch (mReportLoaction){
                        case "프로필카드":
                        case "프로필평가":
                            mReport = new Report(mTimeStampUnix, FirebaseHelper.mUid, mPartnerUser.getUid(), mReportLoaction, mPartnerUser.getUid(), "", DateUtil.getDateSec(), mReportType, content, mPhotoURL, false);
                            break;
                        case "미팅":
                            mReport = new Report(mTimeStampUnix, FirebaseHelper.mUid, mMeeting.getHostUid(), mReportLoaction, mMeeting.getMeetingId(), "", DateUtil.getDateSec(), mReportType, content, mPhotoURL, false);
                            break;
                        case "게시물":
                            mReport = new Report(mTimeStampUnix, FirebaseHelper.mUid, mCommunityPost.getWriterUid(), mReportLoaction, mCommunityPost.getPostId(), "", DateUtil.getDateSec(), mReportType, content, mPhotoURL, false);
                            break;
                        case "댓글":
                            mReport = new Report(mTimeStampUnix, FirebaseHelper.mUid, mCommunityComment.getCommentWriterUid(), mReportLoaction, mCommunityComment.getCommentId(), mCommunityComment.getPostId(), DateUtil.getDateSec(), mReportType, content, mPhotoURL, false);
                            break;
                        case "채팅":
                            String reportedUid;
                            if(mChatRoom.getUserUidList().get(0).equals(FirebaseHelper.mUid)){
                                reportedUid = mChatRoom.getUserUidList().get(1);
                            }else{
                                reportedUid = mChatRoom.getUserUidList().get(0);
                            }
                            mReport = new Report(mTimeStampUnix, FirebaseHelper.mUid, reportedUid , mReportLoaction, mChatRoom.getRoomId(), "", DateUtil.getDateSec(), mReportType, content, mPhotoURL, false);
                            break;
                    }
                    saveReportToStorage(mReport);
                }
            }
        });

        mSetImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoDialogRadio();
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    //사진저장하고 디비에 신고내용 전송
    private void saveReportToStorage(Report report) {
        if(!isLoading) {
            isLoading = true;
            mLoaderLayout.setVisibility(View.VISIBLE);

            if (mFinalUri == null) { //첨부사진 없는 경우
                saveReportToDB(report);
            } else {
                final StorageReference profileStorageRef = FirebaseHelper.storageRef.child("Reports/" + mReportType + " / " + mReportLoaction).child(mTimeStampUnix);
                UploadTask uploadTask = profileStorageRef.putFile(mFinalUri);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(ReportWrite.this, "네트워크가 불안정합니다.", Toast.LENGTH_SHORT).show();
                        isLoading = false;
                        mLoaderLayout.setVisibility(View.GONE);
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    Log.d(TAG, "실패" + task.getException());
                                    isLoading = false;
                                    mLoaderLayout.setVisibility(View.GONE);
                                    throw task.getException();
                                }
                                return profileStorageRef.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    //다운로드 url
                                    Log.d(TAG, "사진 다운로드 url 반환값 :" + task.getResult());
                                    mPhotoURL = String.valueOf(task.getResult());
                                    report.setPhotoUrl(mPhotoURL);
                                    saveReportToDB(report);
                                }else{
                                    Toast.makeText(ReportWrite.this, "네트워크가 불안정합니다.", Toast.LENGTH_SHORT).show();
                                    isLoading = false;
                                    mLoaderLayout.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                });
            }
        }
    }

    private void saveReportToDB(Report report){
        WriteBatch batch = FirebaseHelper.db.batch();
        batch.set(FirebaseHelper.db.collection("Reports").document(mTimeStampUnix), report);
        if (mReportLoaction.equals("프로필카드") || mReportLoaction.equals("프로필평가") || mReportLoaction.equals("채팅")){
            batch.update(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("Block").document(FirebaseHelper.mUid),
                    FirebaseHelper.blockUserList, FieldValue.arrayUnion(partnerUid));
            batch.update(FirebaseHelper.db.collection("Users").document(partnerUid).collection("Block").document(partnerUid),
                    FirebaseHelper.blockMeList, FieldValue.arrayUnion(MyProfile.getUser().getUid()));
        }
        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot successfully written!");
                Toast.makeText(ReportWrite.this, "신고가 접수되었습니다", Toast.LENGTH_SHORT).show();
                isLoading = false;
                mLoaderLayout.setVisibility(View.GONE);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error writing document", e);
                Toast.makeText(ReportWrite.this, "네트워크가 불안정합니다.", Toast.LENGTH_SHORT).show();
                isLoading = false;
                mLoaderLayout.setVisibility(View.GONE);
            }
        });
    }

    private void processIntent() {
        Intent resultIntent = getIntent();
        mReportLoaction = resultIntent.getStringExtra(Strings.EXTRA_LOCATION);
        mReportType = resultIntent.getStringExtra(Strings.EXTRA_REPORT_TYPE);
        switch (mReportLoaction) {
            case "프로필카드":
            case "프로필평가":
                mPartnerUser = (User) resultIntent.getSerializableExtra(Strings.EXTRA_USER);
                partnerUid = mPartnerUser.getUid();
                break;
            case "미팅":
                mMeeting = (Meeting) resultIntent.getSerializableExtra(Strings.EXTRA_MEETING);
                break;
            case "게시물":
                mCommunityPost = (CommunityPost) resultIntent.getSerializableExtra(Strings.EXTRA_COMMUNITY_POST);
                break;
            case "댓글":
                mCommunityComment = (CommunityComment) resultIntent.getSerializableExtra(Strings.EXTRA_COMMUNITY_COMMENT);
                break;
            case "채팅":
                mChatRoom = (ChatRoom) resultIntent.getSerializableExtra(Strings.EXTRA_CHATTING);
                if (mChatRoom.getUserUidList().get(0).equals(FirebaseHelper.mUid)) {
                    partnerUid = mChatRoom.getUserUidList().get(1);
                } else {
                    partnerUid = mChatRoom.getUserUidList().get(0);
                }

                if (mChatRoom.getUser0().getUid().equals(partnerUid)) {
                    mPartnerUser = mChatRoom.getUser0();
                } else {
                    mPartnerUser = mChatRoom.getUser1();
                }
                break;
        }
        mReportTypeTextView.setText(mReportType);
    }

    //앨범에서 가져오기 선택 다이얼로그
    private void photoDialogRadio() {
        DialogEditPhoto2Fragment dialog = DialogEditPhoto2Fragment.getInstance();
        dialog.show(getSupportFragmentManager(), DialogEditPhoto2Fragment.TAG_EVENT_DIALOG);
    }

    //삭제 성공여부 받아와서 시행 해당 이미지 삭제.
    @Override
    public void removePhoto() {
        mSetImageView.setImageResource(0);
        mFinalUri = null;
    }

    //갤러리 사진가져온거 결과(비트맵으로) 저장
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE) {
                Uri sourceUri = data.getData();
                if (sourceUri != null) {
                    Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "cropped"));
                    openCropActivity(sourceUri, destinationUri);
                } else {
                    Toast.makeText(ReportWrite.this, "이미지를 받지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == UCrop.REQUEST_CROP) {
                Uri resultUri = UCrop.getOutput(data);
                if (resultUri != null) {
                    //이미지뷰에 세팅
                    mFinalUri = resultUri;
                    mSetImageView.setImageResource(0);
                    mSetImageView.setImageURI(resultUri);
                } else {
                    Toast.makeText(ReportWrite.this, "이미지를 받지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(ReportWrite.this, "이미지를 받지 못했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void openCropActivity(Uri sourceUri, Uri destinationUri) {
        UCrop.of(sourceUri, destinationUri)
                .withAspectRatio(5, 5)
                .start(this);
    }


    //뒤로가기 2번 클릭시 종료
    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            super.onBackPressed();
        } else {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "한번더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }


}
