package com.unilab.uniting.activities.community;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
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
import com.unilab.uniting.fragments.community.DialogBoardOkFragment;
import com.unilab.uniting.fragments.dialog.DialogEditPhoto2Fragment;
import com.unilab.uniting.model.CommunityPost;
import com.unilab.uniting.model.User;
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
import java.util.HashMap;


public class CommunityWriteActivity extends BasicActivity implements DialogEditPhoto2Fragment.DialogEdit2Listener {
    //TAG
    final static String TAG = "CommunityWriteActivityT";
    public final static String BOARD = "BOARD";
    public final static String UNIVERSITY = "UNIVERSITY";
    public final static String EVERYBODY = "EVERYBODY";

    //value
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;
    final static int PICK_IMAGE = 1;
    private Uri mFinalUri = null;
    private String mPhotoURL = "";
    private String mTitle = "";
    private String mContent = "";
    private boolean isLoading = false;
    private String mBoard;
    private boolean isSelected;

    //xml
    private LinearLayout mBack;
    private Button mSubmit;
    private ImageView mUploadImageView;
    private EditText mTitleEditText;
    private EditText mContentEditText;
    private RadioGroup mBoardRadioGroup;
    private RadioButton mBoardRadioButton;
    private RadioButton mEverybodyRadioButton;
    private RadioButton mUniversityRadioButton;
    private RelativeLayout mLoaderLayout;
    private TextView mToolbarTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_write);

        init();
        setOnClickListener();


        //??????????????? ??????????????? ??????
        mBoardRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                isSelected = true;
                mBoardRadioButton = findViewById(checkedId);
                if(mBoardRadioButton.equals(mEverybodyRadioButton)){
                    mBoard = EVERYBODY;
                }else if(mBoardRadioButton.equals(mUniversityRadioButton)){
                    if (MyProfile.getUser().isOfficialUniversityChecked()) {
                        mBoard = UNIVERSITY;
                    } else {
                        mEverybodyRadioButton.setChecked(true);
                        DialogBoardOkFragment dialog = DialogBoardOkFragment.getInstance();
                        dialog.show(getSupportFragmentManager(), DialogBoardOkFragment.TAG_MEETING_DIALOG2);
                    }
                }
                Log.d("testboard", mBoard);
            }
        });

    }

    private void init(){
        View view = findViewById(R.id.toolbar_write_id);
        mBack = view.findViewById(R.id.toolbar_back);
        mSubmit = view.findViewById(R.id.toolbar_submit_btn);
        mUploadImageView = findViewById(R.id.community_write_iv_setimage);
        mTitleEditText = findViewById(R.id.community_write_et_title);
        mContentEditText = findViewById(R.id.community_write_et_content);

        mBoardRadioGroup = findViewById(R.id.community_write_radioGroup);
        mEverybodyRadioButton = findViewById(R.id.community_write_radioButton1);
        mUniversityRadioButton = findViewById(R.id.community_write_radioButton2);

        mToolbarTextView = findViewById(R.id.toolbar_title);
        mToolbarTextView.setText("?????????");

        //?????????
        mLoaderLayout = findViewById(R.id.loaderLayout);
        mLoaderLayout.setClickable(true);

        Intent intent = getIntent();
        mBoard = intent.getStringExtra(BOARD);

        if(mBoard.equals(EVERYBODY)){
            mEverybodyRadioButton.setChecked(true);
        }else{
            mUniversityRadioButton.setChecked(true);
        }


        if(MyProfile.getUser().isOfficialUniversityChecked()){
            mUniversityRadioButton.setText(MyProfile.getUser().getUniversity());
        }

        //???????????? ?????? ?????? ??????
        mContentEditText.setHint(Html.fromHtml("<h2>" + "????????? ??????????????????" + "</h2>" + "<small>" + "" +
                "<br>" +
                "<br>" +
                "????????? ?????? ?????? ?????? ???????????? ??? ??? ????????????." +
                "<br>" +
                "-?????????, SNS, ID??? ?????? ????????? ????????? ??????" +
                "<br> " +
                "-????????? ??????????????? ????????? ??????" +
                " <br>" +
                "-?????? ????????? ???????????? ??????" +
                "<br> " +
                "-??????, ????????? ?????? ????????? ??????" + "</small>"));

    }

    private void setOnClickListener(){

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mUploadImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoDialogRadio();
            }
        });

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTitle = mTitleEditText.getText().toString().trim();
                mContent = mContentEditText.getText().toString().trim();
                //?????? ????????????
                if (mTitle.length() < 2) {
                    Toast.makeText(getApplicationContext(), "????????? ?????? ????????????.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mContent.length() <= 2) {
                    Toast.makeText(getApplicationContext(), "????????? ?????? ????????????.", Toast.LENGTH_SHORT).show();
                    return;
                }
                saveImageToStorage(mFinalUri);
            }
        });
    }



    //???????????? ???????????? ?????? ???????????????
    private void photoDialogRadio() {
        DialogEditPhoto2Fragment dialog = DialogEditPhoto2Fragment.getInstance();
        dialog.show(getSupportFragmentManager(), DialogEditPhoto2Fragment.TAG_EVENT_DIALOG);
    }


    @Override
    public void removePhoto() {
        mUploadImageView.setImageDrawable(null);
        mFinalUri = null;
    }

    //????????? ?????????????????? ??????(???????????????) ??????
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
                    Toast.makeText(this, "???????????? ?????? ???????????????.", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == UCrop.REQUEST_CROP) {
                Uri resultUri = UCrop.getOutput(data);
                if (resultUri != null) {
                    //??????????????? ??????
                    mUploadImageView.setImageResource(0);
                    mUploadImageView.setImageURI(resultUri);
                    mFinalUri = resultUri;
                } else {
                    Toast.makeText(this, "???????????? ?????? ???????????????.", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(this, "???????????? ?????? ???????????????.", Toast.LENGTH_SHORT).show();
        }
    }

    private void openCropActivity(Uri sourceUri, Uri destinationUri) {
        UCrop.of(sourceUri, destinationUri)
                .withAspectRatio(5f, 5f)
                .start(this);
    }



    private void saveImageToStorage(Uri uri) {
        if(!isLoading) {
            isLoading = true;
            mLoaderLayout.setVisibility(View.VISIBLE);
            DocumentReference postRef = FirebaseHelper.db.collection("CommunityPosts").document();
            String postId = postRef.getId();

            if (uri != null) { //????????? ?????? ??????
                final StorageReference postStorageRef = FirebaseHelper.storageRef.child("community").child(postId);
                UploadTask uploadTask = BitmapConverter.getUploadTask(postStorageRef, uri);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(), "??????????????? ??????????????????.", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(getApplicationContext(), "??????????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                                    throw task.getException();
                                }
                                return postStorageRef.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "???????????? ?????? ??????????????? ????????? ??????, saveDataToDB()??????");
                                    mPhotoURL = String.valueOf(task.getResult());
                                    saveDataToDB(postRef, postId);
                                }else{
                                    Toast.makeText(getApplicationContext(), "??????????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                                    isLoading = false;
                                    mLoaderLayout.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                });
            } else { //????????? ????????? ??????
                saveDataToDB(postRef, postId);
            }
        }
    }

    private void saveDataToDB(DocumentReference ref, String postId) {
        final String randomNickName = new Nickname().getRandomNickname();
        HashMap<String, String> nicknameData = new HashMap<>();
        nicknameData.put(FirebaseHelper.mUid, randomNickName);

        User user = MyProfile.getUser();

        String board = "";
        if(mBoard.equals(EVERYBODY)){
            board = EVERYBODY;
        }else if(mBoard.equals(UNIVERSITY)){
            board = MyProfile.getUser().getUniversity();
        }

        CommunityPost communityPost = new CommunityPost(postId, randomNickName,  FirebaseHelper.mUid, user.getGender(), DateUtil.getDate(), DateUtil.getUnixTimeLong(), mTitle, mContent, mPhotoURL, board, new ArrayList<String>(), 0, nicknameData, new ArrayList<String>(), new ArrayList<String>(), false, false);
        WriteBatch batch = FirebaseHelper.db.batch();
        batch.set(ref, communityPost);
        batch.set(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("CommunityMyPost").document(postId), communityPost);
        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(),"?????? ?????????????????????.", Toast.LENGTH_SHORT).show();

                Bundle eventBundle = new Bundle();
                eventBundle.putInt(LogData.dia, 0);
                eventBundle.putString(LogData.postId, postId);
                LogData.customLog(LogData.community_s03_post,  eventBundle, CommunityWriteActivity.this);
                LogData.setStageCommunity(LogData.community_s03_post,  CommunityWriteActivity.this);

                isLoading = false;
                mLoaderLayout.setVisibility(View.GONE);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"??????????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                isLoading = false;
                mLoaderLayout.setVisibility(View.GONE);
            }
        });

    }

    //???????????? 2??? ????????? ??????
    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            super.onBackPressed();
        } else {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "????????? ????????? ???????????????.", Toast.LENGTH_SHORT).show();
        }
    }


}
