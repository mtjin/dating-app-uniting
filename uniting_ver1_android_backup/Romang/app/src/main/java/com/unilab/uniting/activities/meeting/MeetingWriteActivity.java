package com.unilab.uniting.activities.meeting;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.unilab.uniting.R;
import com.unilab.uniting.fragments.dialog.DialogEditPhoto2Fragment;
import com.unilab.uniting.model.Meeting;
import com.unilab.uniting.utils.BasicActivity;
import com.unilab.uniting.utils.BitmapConverter;
import com.unilab.uniting.utils.DateUtil;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.LogData;
import com.unilab.uniting.utils.MyProfile;
import com.unilab.uniting.utils.Nickname;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;

public class MeetingWriteActivity extends BasicActivity implements DialogEditPhoto2Fragment.DialogEdit2Listener {

    final static String TAG = "MeetingWriteActivityT";

    //xml
    private EditText mTitleEditText;
    private EditText mWriteEditText;
    private ImageView mUploadImageView;
    private ImageView mSetImageView;
    private TextView mImageRuleTextView;
    private LinearLayout mBack;
    private Button mSubmit;
    private RelativeLayout mLoaderLayout;
    private TextView mToolbarTextView;

    private RadioGroup mInfoRadioGroup;
    private RadioButton mPrivateRadioButton;
    private RadioButton mUniversityRadioButton;
    private RadioButton mMajorRadioButton;
    private RadioButton mInfoRadioButton;

    private RadioGroup mBlurRadioGroup;
    private RadioButton mBlurFalseRadioButton;
    private RadioButton mBlurTrueRadioButton;
    private RadioButton mBlurRadioButton;

    //리스너
    Button.OnClickListener onClickListener;

    //value
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;
    private String mTitle = "";
    private String mContent = "";
    final static int PICK_IMAGE = 1;
    final static int CROP_IMAGE = 2;
    private String mPhotoURL = "";
    private Uri mPhotoUri = null;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_write);

        init();
        setOnClickListener();
        setRadioGroup();
    }

    private void init(){
        View view = findViewById(R.id.toolbar_write_id);
        mBack = view.findViewById(R.id.toolbar_back);
        mSubmit = view.findViewById(R.id.toolbar_submit_btn);
        mTitleEditText = findViewById(R.id.meeting_write_et_title);
        mWriteEditText = findViewById(R.id.meeting_write_et_write);
        mUploadImageView = findViewById(R.id.meeting_write_iv_uploadimage);
        mSetImageView = findViewById(R.id.meeting_write_iv_setimage);
        mImageRuleTextView = findViewById(R.id.meeting_write_tv_imagerule);
        mToolbarTextView = findViewById(R.id.toolbar_title);
        mToolbarTextView.setText("미팅·번개·셀소 작성");

        mInfoRadioGroup = findViewById(R.id.meeting_write_radioGroup);
        mPrivateRadioButton = findViewById(R.id.meeting_write_radioButton1);
        mUniversityRadioButton = findViewById(R.id.meeting_write_radioButton2);
        mMajorRadioButton = findViewById(R.id.meeting_write_radioButton3);
        mPrivateRadioButton.setChecked(true);

        mBlurRadioGroup = findViewById(R.id.meeting_write_blur_radioGroup);
        mBlurFalseRadioButton = findViewById(R.id.meeting_write_blur_false_radioButton);
        mBlurTrueRadioButton = findViewById(R.id.meeting_write_blur_true_radioButton);
        mBlurFalseRadioButton.setChecked(true);

        //로딩중
        mLoaderLayout = findViewById(R.id.loaderLayout);
        mLoaderLayout.setClickable(true);

        //글자크기 다른 힌트 세팅
        mWriteEditText.setHint(" 내용을 입력해주세요. 대학이나 전공을 공개하면 게시판 상위에 노출되어 지원자가 생길 확률이 올라갑니다. \n\n\n 다음의 경우 경고 없이 삭제조치 될 수 있습니다. \n\n ·연락처, SNS, ID등 연락 수단을 남기는 경우 \n ·타인의 개인정보를 올리는 경우 \n ·특정 회원을 비난하는 경우 \n ·욕설, 음란물 등을 올리는 경우");
        mImageRuleTextView.setText("  미팅·번개·셀소에 어울리는 사진을 올려주세요. \n\n ·가능한 예 : 본인 사진, 같이 가고 싶은 장소, 본인의 취미, 특징, 전공 등 \n ·금지 사진:  타인의 사진, 음란물 등");
    }

    private void setRadioGroup(){
        //라디오버튼 클릭했는지 체크
        mInfoRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mInfoRadioButton = findViewById(checkedId);
                if(mInfoRadioButton.equals(mUniversityRadioButton)){
                    if (!MyProfile.getUser().isOfficialUniversityChecked()) {
                        mPrivateRadioButton.setChecked(true);
                        Toast.makeText(MeetingWriteActivity.this, "대학을 인증한 회원만 공개할 수 있습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else if(mInfoRadioButton.equals(mMajorRadioButton)){
                    if (!MyProfile.getUser().isOfficialMajorChecked()) {
                        mPrivateRadioButton.setChecked(true);
                        Toast.makeText(MeetingWriteActivity.this, "전공을 인증한 회원만 공개할 수 있습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mBlurRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mBlurRadioButton = findViewById(checkedId);
            }
        });

        if(MyProfile.getUser().isOfficialUniversityChecked()){
            mInfoRadioButton = mUniversityRadioButton;
            mUniversityRadioButton.setChecked(true);
        } else {
            mInfoRadioButton = mPrivateRadioButton;
            mPrivateRadioButton.setChecked(true);
        }
        mBlurRadioButton = mBlurFalseRadioButton;
        mBlurFalseRadioButton.setChecked(true);
    }

    private void setOnClickListener() {
        onClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.toolbar_back:
                        finish();
                        break;
                    case R.id.meeting_write_iv_setimage: //사진불러오기
                        photoDialogRadio();
                        break;
                    case R.id.toolbar_submit_btn:
                        //추후 확인 다이어로그 추가하기
                        mTitle = mTitleEditText.getText().toString().trim();
                        mContent = mWriteEditText.getText().toString().trim();
                        if (mTitle.length() < 1) {
                            Toast.makeText(MeetingWriteActivity.this, "제목을 입력해주세요", Toast.LENGTH_SHORT).show();
                        } else if (mContent.length() < 1) {
                            Toast.makeText(MeetingWriteActivity.this, "내용을 입력해주세요", Toast.LENGTH_SHORT).show();
                        } else if (mPhotoUri == null) {
                            Toast.makeText(MeetingWriteActivity.this, "미팅에 어울리는 사진을 올려주세요.", Toast.LENGTH_SHORT).show();
                        } else {
                            saveMeetingToDB();
                        }
                        break;
                }
            }
        };

        mBack.setOnClickListener(onClickListener);
        mSubmit.setOnClickListener(onClickListener);
        mSetImageView.setOnClickListener(onClickListener);


    }

    private void saveMeetingToDB() {
        if(!isLoading) {
            isLoading = true;
            mLoaderLayout.setVisibility(View.VISIBLE);
            DocumentReference meetingRef = FirebaseHelper.db.collection("Meetings").document();
            String meetingId = meetingRef.getId();

            final String randomNickName = new Nickname().getRandomNickname();
            final String date = DateUtil.getDateSec();
            if (mPhotoUri != null) {
                final StorageReference postStorageRef = FirebaseHelper.storageRef.child("meetings").child(meetingId);
                UploadTask uploadTask = BitmapConverter.getUploadTask(postStorageRef, mPhotoUri);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(), "네트워크가 불안정합니다.", Toast.LENGTH_SHORT).show();
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
                                    isLoading = false;
                                    mLoaderLayout.setVisibility(View.GONE);
                                    throw task.getException();
                                }
                                return postStorageRef.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "미팅 사진 스토리지에 업로드 성공");
                                    mPhotoURL = String.valueOf(task.getResult());
                                    String hostInfo = "";

                                    if(mInfoRadioButton.equals(mPrivateRadioButton)){
                                        hostInfo = "";
                                    }else if(mInfoRadioButton.equals(mUniversityRadioButton)){
                                        hostInfo = MyProfile.getUser().getUniversity();
                                    } else if(mInfoRadioButton.equals(mMajorRadioButton)){
                                        hostInfo = MyProfile.getUser().getMajor();
                                    }

                                    boolean isBlurred = false;
                                    if(mBlurRadioButton.equals(mBlurFalseRadioButton)){
                                        isBlurred = false;
                                    } else if(mBlurRadioButton.equals(mBlurTrueRadioButton)){
                                        isBlurred = true;
                                    }

                                    Meeting meeting = new Meeting(meetingId, randomNickName, FirebaseHelper.mUid, MyProfile.getUser().getGender(), MyProfile.getUser().getAge(), hostInfo,
                                            MyProfile.getUser().getTierPercent(), MyProfile.getUser().getIntroMannerScore(), MyProfile.getUser().getMeetingMannerScore(),
                                            isBlurred, date, DateUtil.getUnixTimeLong(), mTitle, mContent, mPhotoURL, new ArrayList<String>(), false, false);
                                    WriteBatch batch = FirebaseHelper.db.batch();
                                    batch.set(meetingRef, meeting);
                                    batch.set(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("MeetingHost").document(meetingId), meeting);
                                    batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getApplicationContext(), "미팅글이 작성되었습니다.", Toast.LENGTH_SHORT).show();

                                            Bundle bundle = new Bundle();
                                            bundle.putInt(LogData.dia, 0);
                                            bundle.putString(LogData.meetingId, meeting.getMeetingId());
                                            LogData.customLog(LogData.meeting_s01_post, bundle, MeetingWriteActivity.this);

                                            LogData.setStageMeeting(LogData.meeting_s01_post, MeetingWriteActivity.this);
                                            LogData.setStageMeetingHost(LogData.meeting_s01_post,MeetingWriteActivity.this);

                                            isLoading = false;
                                            mLoaderLayout.setVisibility(View.GONE);
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "네트워크가 불안정합니다.", Toast.LENGTH_SHORT).show();
                                            isLoading = false;
                                            mLoaderLayout.setVisibility(View.GONE);
                                        }
                                    });
                                } else {
                                    Toast.makeText(getApplicationContext(), "네트워크가 불안정합니다.", Toast.LENGTH_SHORT).show();
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

    //앨범에서 가져오기 선택 다이얼로그
    private void photoDialogRadio() {
        DialogEditPhoto2Fragment dialog = DialogEditPhoto2Fragment.getInstance();
        dialog.show(getSupportFragmentManager(), DialogEditPhoto2Fragment.TAG_EVENT_DIALOG);
    }

    //삭제 성공여부 받아와서 시행 해당 이미지 삭제.
    @Override
    public void removePhoto() {
        mSetImageView.setImageResource(0);
        mUploadImageView.setImageResource(R.drawable.ic_picture);
        mPhotoUri = null;
        mImageRuleTextView.setVisibility(View.VISIBLE);
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
                    openCropActivity(sourceUri, destinationUri, 100);
                } else {
                    Toast.makeText(MeetingWriteActivity.this, "이미지를 받지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == UCrop.REQUEST_CROP) {
                Uri resultUri = UCrop.getOutput(data);
                if (resultUri != null) {
                    //이미지뷰에 세팅
                    mPhotoUri = resultUri;
                    mSetImageView.setImageResource(0);
                    mSetImageView.setImageURI(resultUri);
                    mUploadImageView.setImageResource(0);
                    mImageRuleTextView.setVisibility(View.INVISIBLE);
                } else {
                    Toast.makeText(MeetingWriteActivity.this, "이미지를 받지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(MeetingWriteActivity.this, "이미지를 받지 못했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void openCropActivity(Uri sourceUri, Uri destinationUri, int compressionQuality) {
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setCompressionQuality(compressionQuality);
        options.setMaxBitmapSize(6400);

        UCrop.of(sourceUri, destinationUri)
                .withOptions(options)
                .withAspectRatio(5, 5)
                .start(this);
    }



}
