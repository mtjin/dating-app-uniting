package com.unilab.uniting.activities.setprofile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.unilab.uniting.R;
import com.unilab.uniting.model.PushNotification;
import com.unilab.uniting.utils.BasicActivity;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.LaunchUtil;
import com.unilab.uniting.utils.OnSingleClickListener;

public class SetProfile_7NotificationSetting extends BasicActivity {
    static final String TAG = "NotificationSettingTAG";
    static final int FROM_NOTIFICATION = 1;
    Context context = this;

    //xml
    private LinearLayout mActivateLinearLayout; // 알림활성화버튼 포함 레이아웃
    private Button mActivateButton; //알람활성화 버튼
    private Switch mTotalSwitch;//
    private Switch mSoundSwitch; //소리 및 진동
    private Switch mTodayIntroSwitch; //오늘의 소개
    private Switch mLikeSwitch; //좋아요 받음
    private Switch mCheckLikeSwitch; // 보낸 좋아요 확인
    private Switch mHighScoreSwitch; //나를 높게 평가
    private Switch mConnectSwitch; //채팅 연결됨
    private Switch mMeetingSwitch; //미팅 관련 알림
    private Switch mCommunitySwitch; //커뮤니티 댓글 달림
    private Switch mChattingSwitch; //채팅 메세지
    private LinearLayout mBackLinearLayout; //뒤로가기버튼

    private PushNotification pushNotification = new PushNotification(true,true, true,true,true,true,true,true,true,true);

//    //value
//    private boolean sound = true;
//    private boolean todayIntro = true;
//    private boolean like = true;
//    private boolean checkLike = true;
//    private boolean highScore = true;
//    private boolean connect = true;
//    private boolean meeting = true;
//    private boolean community = true;
//    private boolean chatting = true;
//    private boolean allChecked = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile_7_notification_setting);

        initView();
        //스위치리스너
        setOnCheckedChangeListener();
        loadShared();
        setPushNotification();



        if(NotificationManagerCompat.from(context).areNotificationsEnabled()){
            mActivateLinearLayout.setVisibility(View.GONE);
        }

        mActivateButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                    intent.putExtra("app_package", context.getPackageName());
                    intent.putExtra("app_uid", context.getApplicationInfo().uid);
                } else {
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setData(Uri.parse("package:" + context.getPackageName()));
                }
                startActivityForResult(intent, FROM_NOTIFICATION);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FROM_NOTIFICATION:
                if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                    mActivateLinearLayout.setVisibility(View.GONE);
                }
                break;
        }
    }

    private void initView() {
        mActivateLinearLayout = findViewById(R.id.setprofile_notification7_linear_activate);
        mActivateButton = findViewById(R.id.setprofile_notification7_btn_activiate);
        mTotalSwitch = findViewById(R.id.setprofile_notification7_sw_total);
        mSoundSwitch = findViewById(R.id.setprofile_notification7_sw_sound);
        mTodayIntroSwitch = findViewById(R.id.setprofile_notification7_sw_todayintro);
        mLikeSwitch = findViewById(R.id.setprofile_notification7_sw_receivelike);
        mCheckLikeSwitch = findViewById(R.id.setprofile_notification7_sw_sendlike);
        mHighScoreSwitch = findViewById(R.id.setprofile_notification7_sw_evalutation);
        mConnectSwitch = findViewById(R.id.setprofile_notification7_sw_connected);
        mMeetingSwitch = findViewById(R.id.setprofile_notification7_sw_meeting);
        mCommunitySwitch = findViewById(R.id.setprofile_notification7_sw_community);
        mChattingSwitch = findViewById(R.id.setprofile_notification7_sw_chatting);
        mBackLinearLayout =findViewById(R.id.toolbar_back);

    }

    //스위치버튼 감지하고 변화값 쉐어드에 저장
    private void setOnCheckedChangeListener() {
        mTotalSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTotalSwitch.isChecked()){
                    pushNotification = new PushNotification(true,true, true,true,true,true,true,true,true,true);
                    setSwitch();
                    savePushInfo();
                    Toast.makeText(SetProfile_7NotificationSetting.this, "모든 알림을 활성화했습니다.", Toast.LENGTH_SHORT).show();
                }else if(!mTotalSwitch.isChecked()){
                    pushNotification = new PushNotification(false,false, false,false,false,false,false,false,false,false);
                    setSwitch();
                    savePushInfo();
                    Toast.makeText(SetProfile_7NotificationSetting.this, "모든 알림을 비활성화했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mSoundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                pushNotification.setSound(isChecked);
                savePushInfo();
            }
        });
        mTodayIntroSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                pushNotification.setTodayIntro(isChecked);
                savePushInfo();
            }
        });
        mLikeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                pushNotification.setReceiveLike(isChecked);
                savePushInfo();
            }
        });
        mCheckLikeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                pushNotification.setCheckSendLike(isChecked);
                savePushInfo();
            }
        });
        mHighScoreSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                pushNotification.setHighScore(isChecked);
                savePushInfo();
            }
        });
        mConnectSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                pushNotification.setChatConnect(isChecked);
                savePushInfo();
            }
        });
        mMeetingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                pushNotification.setMeeting(isChecked);
                savePushInfo();
            }
        });
        mCommunitySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                pushNotification.setCommunityComment(isChecked);
                savePushInfo();
            }
        });
        mChattingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                pushNotification.setChatMessage(isChecked);
                savePushInfo();
            }
        });

        mBackLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void savePushInfo() {
        SharedPreferences pref = getSharedPreferences("notificationSetting" + FirebaseHelper.mUid, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("allChecked", pushNotification.isAll());
        editor.putBoolean("sound", pushNotification.isSound());
        editor.putBoolean("todayIntro", pushNotification.isTodayIntro());
        editor.putBoolean("like", pushNotification.isReceiveLike());
        editor.putBoolean("checkLike", pushNotification.isCheckSendLike());
        editor.putBoolean("highScore", pushNotification.isHighScore());
        editor.putBoolean("connect", pushNotification.isChatConnect());
        editor.putBoolean("meeting", pushNotification.isMeeting());
        editor.putBoolean("community", pushNotification.isCommunityComment());
        editor.putBoolean("chatting", pushNotification.isChatMessage());
        editor.apply();

        LaunchUtil.checkAuth(this);
        FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("Fcm").document("PushNotification").set(pushNotification, SetOptions.merge());
    }


    private void loadShared() {
        SharedPreferences pref = getSharedPreferences("notificationSetting" + FirebaseHelper.mUid, MODE_PRIVATE);
        pushNotification.setAll(pref.getBoolean("allChecked", true));
        pushNotification.setSound(pref.getBoolean("sound", true));//소리 및 진동
        pushNotification.setTodayIntro( pref.getBoolean("todayIntro", true)); //오늘의 소개
        pushNotification.setReceiveLike(pref.getBoolean("like", true));//좋아요 받음
        pushNotification.setCheckSendLike(pref.getBoolean("checkLike", true)); // 보낸 좋아요 확인
        pushNotification.setHighScore(pref.getBoolean("highScore", true)); //나를 높게 평가
        pushNotification.setChatConnect(pref.getBoolean("connect", true));//연결됨
        pushNotification.setMeeting(pref.getBoolean("meeting", true));//미팅 관련 알림
        pushNotification.setCommunityComment(pref.getBoolean("community", true));//커뮤니티 댓글
        pushNotification.setChatMessage(pref.getBoolean("chatting", true)); //채팅 메세지
        setSwitch();
    }



    private void setPushNotification(){
        FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("Fcm").document("PushNotification")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        pushNotification = document.toObject(PushNotification.class);
                        setSwitch();

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void setSwitch(){
        mTotalSwitch.setChecked(pushNotification.isAll());
        mSoundSwitch.setChecked(pushNotification.isSound());
        mTodayIntroSwitch.setChecked(pushNotification.isTodayIntro());
        mLikeSwitch.setChecked(pushNotification.isReceiveLike());
        mCheckLikeSwitch.setChecked(pushNotification.isCheckSendLike());
        mHighScoreSwitch.setChecked(pushNotification.isHighScore());
        mConnectSwitch.setChecked(pushNotification.isChatConnect());
        mMeetingSwitch.setChecked(pushNotification.isMeeting());
        mCommunitySwitch.setChecked(pushNotification.isCommunityComment());
        mChattingSwitch.setChecked(pushNotification.isChatMessage());
    }
}
