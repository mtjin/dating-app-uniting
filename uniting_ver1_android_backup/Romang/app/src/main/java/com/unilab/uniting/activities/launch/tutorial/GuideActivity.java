package com.unilab.uniting.activities.launch.tutorial;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.unilab.uniting.R;
import com.unilab.uniting.activities.community.CommunityWriteActivity;
import com.unilab.uniting.activities.profilecard.ProfileCardMainActivity;
import com.unilab.uniting.model.User;
import com.unilab.uniting.utils.BasicActivity;
import com.unilab.uniting.utils.DateUtil;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.LaunchUtil;
import com.unilab.uniting.utils.LogData;
import com.unilab.uniting.utils.MyProfile;
import com.unilab.uniting.utils.OnSingleClickListener;
import com.unilab.uniting.utils.Strings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class GuideActivity extends BasicActivity {

    private static final int REQUEST_CODE_GUIDE = 111;
    private static final int REQUEST_CODE_PROFILE = 300;
    private static final int REQUEST_CODE_POST = 301;

    private Button mNextBtn;
    private ImageView mGuideImgView;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private String progress = LogData.guide_s01_start;
    private ArrayList<QueryDocumentSnapshot> mUserList = new ArrayList<>();
    private String mPartnerUid = "";
    private User mPartnerUser;
    private RelativeLayout mLoaderLayout;
    private Map<String, String> urlMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        init();
        setImageUrl(LogData.guide_s01_start);
        setImageUrl(LogData.guide_s02_profile_activity);
        setImageUrl(LogData.guide_s03_post);
        setImageUrl(LogData.guide_s05_meeting);
        updateUI();
        setOnClickListener();

    }

    private void init(){
        mNextBtn = findViewById(R.id.guide_btn_next);
        mGuideImgView = findViewById(R.id.guide_iv);

        mLoaderLayout = findViewById(R.id.loaderLayout);
        mLoaderLayout.setClickable(true);

        Intent intent = getIntent();
        progress = intent.getStringExtra(LogData.stageGuide);
    }

    private void updateUI(){
        switch (progress){
            case LogData.guide_s01_start:
                loadImageUrl(LogData.guide_s01_start);
                break;
            case LogData.guide_s02_profile_activity:

                Intent intent = new Intent(GuideActivity.this, ProfileCardMainActivity.class);

                long nowUnixTime = DateUtil.getUnixTimeLong();
                long weekUnixTime = nowUnixTime - 7 * DateUtil.dayInMilliSecond;

                mLoaderLayout.setVisibility(View.VISIBLE);
                FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("TodayIntro").whereGreaterThan(FirebaseHelper.introTime, weekUnixTime).orderBy(FirebaseHelper.introTime, Query.Direction.DESCENDING)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult() != null && !task.getResult().isEmpty()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            mUserList.add(document);
                                        }

                                        Collections.sort(mUserList, new DescendingUserByScore());
                                        if(mUserList.size() > 0){
                                            mPartnerUser = mUserList.get(0).toObject(User.class);
                                            mPartnerUid = mPartnerUser.getUid();
                                        }
                                    }
                                } else {
                                }

                                if(!mPartnerUid.equals("")){
                                    intent.putExtra(Strings.partnerUid, mPartnerUid);
                                    intent.putExtra(Strings.partnerUser, mPartnerUser);
                                    startActivityForResult(intent, REQUEST_CODE_PROFILE);
                                }else {
                                    Toast.makeText(GuideActivity.this, "현재 소개된 친구가 없어 다음 가이드로 넘어갑니다:(", Toast.LENGTH_SHORT).show();
                                    setProgress(LogData.guide_s03_post);
                                    updateUI();
                                }

                                mLoaderLayout.setVisibility(View.GONE);
                            }
                        });

                break;
            case LogData.guide_s03_post:
                loadImageUrl(LogData.guide_s03_post);
                break;
            case LogData.guide_s04_post_activity:
                Intent writeIntent = new Intent(GuideActivity.this, CommunityWriteActivity.class);
                writeIntent.putExtra(Strings.BOARD,Strings.EVERYBODY);
                startActivityForResult(writeIntent, REQUEST_CODE_POST);
                break;
            case LogData.guide_s05_meeting:
                loadImageUrl(LogData.guide_s05_meeting);
                break;
            case LogData.guide_s06_complete:
                setResult(RESULT_OK);
                finish();
                break;
        }
    }

    class DescendingUserByScore implements Comparator<QueryDocumentSnapshot> {
        @Override
        public int compare(QueryDocumentSnapshot o1, QueryDocumentSnapshot o2) {
            if(o2.get(FirebaseHelper.averageOfReceiveScore) == null) {
                return -1;
            } else if(o1.get(FirebaseHelper.averageOfReceiveScore) == null){
                return 1;
            } else {
                try{
                    if((double) o2.get(FirebaseHelper.averageOfReceiveScore) > (double) o1.get(FirebaseHelper.averageOfReceiveScore)){
                        return 1;
                    } else if((double) o2.get(FirebaseHelper.averageOfReceiveScore) < (double) o1.get(FirebaseHelper.averageOfReceiveScore)){
                        return -1;
                    }
                }catch (Exception err){
                    return 0;
                }
            }
            return 0;
        }

    }

    @Override
    public void onBackPressed() {
        Toast.makeText(GuideActivity.this, "튜토리얼을 완료해주세요!", Toast.LENGTH_SHORT).show();
    }

    private void setOnClickListener(){
        mNextBtn.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                switch (progress){
                    case LogData.guide_s01_start:
                        setProgress(LogData.guide_s02_profile_activity);
                        break;
                    case LogData.guide_s02_profile_activity:
                        break;
                    case LogData.guide_s03_post:
                        setProgress(LogData.guide_s04_post_activity);
                        break;
                    case LogData.guide_s04_post_activity:
                        break;
                    case LogData.guide_s05_meeting:
                        setProgress(LogData.guide_s06_complete);
                        break;
                    case LogData.guide_s06_complete:
                        setResult(RESULT_OK);
                        finish();
                        return;
                }
                updateUI();
            }
        });
    }

    private void loadImageUrl(String imgPath){
        StorageReference storageRef = storage.getReference();
        StorageReference spaceRef = storageRef.child("Guide/" + imgPath + ".png");

        if(urlMap.get(imgPath)!= null){
            Glide.with(GuideActivity.this).load(urlMap.get(imgPath)).into(mGuideImgView);
            return;
        }

        spaceRef.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(GuideActivity.this).load(uri.toString()).into(mGuideImgView);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });
    }

    private void setImageUrl(String imgPath){
        StorageReference storageRef = storage.getReference();
        StorageReference spaceRef = storageRef.child("Guide/" + imgPath + ".png");

        spaceRef.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        urlMap.put(imgPath, uri.toString());
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });
    }


    private void setProgress(String newProgress){
        progress = newProgress;
        LaunchUtil.checkAuth(this);
        FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).update(LogData.stageGuide, newProgress);
        MyProfile.getOurInstance().setStageGuide(newProgress);
        LogData.eventLog(newProgress, GuideActivity.this);
        Map<String,String> props = new HashMap<>();
        props.put(LogData.stageGuide, newProgress);
        LogData.setUserProperties(props, GuideActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_PROFILE){
            setProgress(LogData.guide_s03_post);
            if(resultCode == RESULT_OK){
                try{
                    if( data.getDoubleExtra(FirebaseHelper.score, 0) == 1){
                        Toast.makeText(GuideActivity.this, "튜토리얼 종료 후 호감 있는 친구에게 친구 신청을 해보세요!", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception err){

                }
            }
        }

        if(requestCode == REQUEST_CODE_POST){
            setProgress(LogData.guide_s05_meeting);
        }

        updateUI();
    }

}
