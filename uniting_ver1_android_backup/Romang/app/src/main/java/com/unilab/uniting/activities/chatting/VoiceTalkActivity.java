package com.unilab.uniting.activities.chatting;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.remotemonster.sdk.Config;
import com.remotemonster.sdk.RemonCall;
import com.remotemonster.sdk.RemonException;
import com.remotemonster.sdk.data.AudioType;
import com.remotemonster.sdk.data.CloseType;
import com.unilab.uniting.R;
import com.unilab.uniting.model.ChatMessage;
import com.unilab.uniting.model.ChatRoom;
import com.unilab.uniting.model.User;
import com.unilab.uniting.utils.BasicActivity;
import com.unilab.uniting.utils.DateUtil;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.LogData;
import com.unilab.uniting.utils.MyProfile;
import com.unilab.uniting.utils.Strings;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class VoiceTalkActivity extends BasicActivity implements SensorEventListener {

    public static final String[] MANDATORY_PERMISSIONS = {
            "android.permission.INTERNET",
            //"android.permission.CAMERA",
            "android.permission.RECORD_AUDIO",
            "android.permission.MODIFY_AUDIO_SETTINGS",
            "android.permission.ACCESS_NETWORK_STATE",
            "android.permission.CHANGE_WIFI_STATE",
            "android.permission.ACCESS_WIFI_STATE",
            "android.permission.READ_PHONE_STATE",
            "android.permission.BLUETOOTH",
            "android.permission.BLUETOOTH_ADMIN",
            //"android.permission.WRITE_EXTERNAL_STORAGE"
    };

    public static final int CONNECT_PERMISSION_REQUEST = 100;
    public static final int CALLEE_PERMISSION_REQUEST = 101;
    public static final int PERMISSION_REQUEST_AFTER_SETTING = 102;
    private static final int SENSOR_SENSITIVITY = 4;

    public static final String REST_HOST = "https://signal.remotemonster.com/rest/";
    public static final String WSS_HOST = "wss://signal.remotemonster.com/ws";

    private CircleImageView mProfileImageView;
    private TextView mPartnerNickNameTextView;
    private TextView mStatusTextView;
    private LinearLayout mMuteLayout;
    private ImageView mMuteImageView;
    private LinearLayout mSpeakerLayout;
    private ImageView mSpeakerImageView;
    private LinearLayout mNoLayout;
    private LinearLayout mOkLayout;

    private RemonCall remonCall;
    private RemonException latestError = null;
    private Config remonConfig = null;
    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private MediaPlayer mediaPlayer;

    private ListenerRegistration listenerRegistration;
    private ChatRoom mChatRoom;
    private User mPartnerUser;
    private String mPartnerUid;
    private boolean isMuteTapped = false;
    private boolean isSpeakerTapped = false;
    private int callingTime = 0;
    private int waitingTime = 0;
    private Timer mCallingTimer;
    private Timer mWaitingTimer;
    private boolean isCloseMessageSent = false;
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_talk);

        init();
        updateUI();
        setSensorManager();
        setRemonCall();
        setOnClickListener();
        setChatRoomListener();
        setWaitingTimer();

        try {
            setRingTone();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (mChatRoom.getCallerUid().equals(FirebaseHelper.mUid)) {
            if (Build.VERSION.SDK_INT >= 23) {
                requestPermissions(MANDATORY_PERMISSIONS, CONNECT_PERMISSION_REQUEST);
            } else {
                startCall();
            }
        } else {
            if (Build.VERSION.SDK_INT >= 23) {
                requestPermissions(MANDATORY_PERMISSIONS, CALLEE_PERMISSION_REQUEST);
            }
        }

    }


    private void init() {
        mProfileImageView = findViewById(R.id.voice_talk_iv_profile);
        mPartnerNickNameTextView = findViewById(R.id.voice_talk_tv_nickname);
        mStatusTextView = findViewById(R.id.voice_talk_tv_status);
        mMuteLayout = findViewById(R.id.voice_talk_linear_mute);
        mMuteImageView = findViewById(R.id.voice_talk_iv_mute);
        mSpeakerLayout = findViewById(R.id.voice_talk_linear_speaker);
        mSpeakerImageView = findViewById(R.id.voice_talk_iv_speaker);
        mNoLayout = findViewById(R.id.voice_talk_linear_no);
        mOkLayout = findViewById(R.id.voice_talk_linear_ok);

        Intent intent = getIntent();
        mChatRoom = (ChatRoom) intent.getSerializableExtra(Strings.EXTRA_CHATROOM_ID);

        //????????? ?????? ??????
        if (mChatRoom.getUser0().getUid().equals(FirebaseHelper.mUid)) {
            mPartnerUser = mChatRoom.getUser1();
        } else {
            mPartnerUser = mChatRoom.getUser0();
        }
        mPartnerUid = mPartnerUser.getUid();

    }

    private void updateUI() {
        if (mPartnerUser != null && (!mPartnerUser.getPhotoUrl().isEmpty()) && mPartnerUser.getPhotoUrl().get(0) != null) {
            String photoUrl = mPartnerUser.getPhotoUrl().get(0);
            Glide.with(VoiceTalkActivity.this).load(photoUrl).into(mProfileImageView);
        }
        mPartnerNickNameTextView.setText(mPartnerUser.getNickname());

        if (mChatRoom.getCallerUid().equals(FirebaseHelper.mUid)) {
            mOkLayout.setVisibility(View.GONE);
        } else {
            mOkLayout.setVisibility(View.VISIBLE);
        }

    }

    private void setOnClickListener() {
        mMuteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMuteTapped = !isMuteTapped;
                remonCall.setLocalAudioEnabled(!isMuteTapped);
                if (isMuteTapped) {
                    mMuteLayout.setBackgroundResource(R.drawable.bg_call_gray);
                    mMuteImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_mute_white));
                } else {
                    mMuteLayout.setBackgroundResource(R.color.colorWhite);
                    mMuteImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_mute));
                }

            }
        });

        mSpeakerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSpeakerTapped = !isSpeakerTapped;
                remonCall.setSpeakerphoneOn(isSpeakerTapped);
                if (isSpeakerTapped) {
                    mSpeakerLayout.setBackgroundResource(R.drawable.bg_call_gray);
                    mSpeakerImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_audio_white));
                } else {
                    mSpeakerLayout.setBackgroundResource(R.color.colorWhite);
                    mSpeakerImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_audio));
                }


            }
        });

        mNoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeVoiceTalk();
            }
        });

        mOkLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    requestPermissions(MANDATORY_PERMISSIONS, CONNECT_PERMISSION_REQUEST);
                } else {
                    startCall();
                }
            }
        });
    }


    private void setRingTone() throws IOException {
        AssetFileDescriptor afd = getAssets().openFd("raw/Happy_Mistake.mp3");
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
        mediaPlayer.prepare();
    }

    private void setRemonCall() {
        remonCall = RemonCall.builder()
                .context(VoiceTalkActivity.this)
                .serviceId(getResources().getString(R.string.remon_serviceId))    // RemoteMonster ??????????????? ???????????? ????????? id??? ???????????????.
                .key(getResources().getString(R.string.remon_serviceKey))    // RemoteMonster????????? ?????? ????????? key??? ???????????????.
                .isVideoCall(false)
                .audioType(AudioType.VOICE)
//                .restUrl(REST_HOST)
//                .wssUrl(WSS_HOST)
                .build();

        initRemonCallback();

    }

    private void startCall() {
        remonConfig = new com.remotemonster.sdk.Config();
        remonConfig.setServiceId(getResources().getString(R.string.remon_serviceId));
        remonConfig.setKey(getResources().getString(R.string.remon_serviceKey));
        remonConfig.setRestHost(REST_HOST);
        remonConfig.setSocketUrl(WSS_HOST);
        remonConfig.setVideoCall(false);
        remonConfig.setAudioType(AudioType.VOICE);

        String chId = mChatRoom.getRoomId().replaceAll("[^a-zA-Z0-9]", "");
        if (remonCall != null) {
            remonCall.setSpeakerphoneOn(false);
            remonCall.connect(chId);
        }

    }

    private void closeVoiceTalk() {
        if (remonCall != null) {
            remonCall.close();
            remonCall = null;
        }
        remonConfig = null;
        if (mCallingTimer != null) {
            mCallingTimer.cancel();
        }
        if (mWaitingTimer != null) {
            mWaitingTimer.cancel();
        }

        sendCloseMessage();

        Map<String, Boolean> callData = new HashMap<>();
        callData.put(FirebaseHelper.voiceTalkOn, false);

        Map<String, Object> roomData = new HashMap<>();
        roomData.put(FirebaseHelper.voiceTalkOn, false);
        roomData.put(FirebaseHelper.callerUid, "");
        roomData.put(FirebaseHelper.recentMessage, "???????????? ??????");
        roomData.put(FirebaseHelper.recentTimestamp, DateUtil.getUnixTimeLong());
        roomData.put(FirebaseHelper.senderUid, FirebaseHelper.mUid);

        WriteBatch batch = FirebaseHelper.db.batch();
        batch.set(FirebaseHelper.db.collection("ChatRoom").document(mChatRoom.getRoomId()).collection("VoiceTalk").document(mChatRoom.getRoomId()), callData, SetOptions.merge());
        batch.set(FirebaseHelper.db.collection("ChatRoom").document(mChatRoom.getRoomId()), roomData, SetOptions.merge());
        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(VoiceTalkActivity.this, "??????????????? ???????????????.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(VoiceTalkActivity.this, "?????? ??????", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setChatRoomListener() {
        listenerRegistration = FirebaseHelper.db.collection("ChatRoom").document(mChatRoom.getRoomId())
                .addSnapshotListener(VoiceTalkActivity.this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }

                        if (snapshot != null && snapshot.exists()) {
                            ChatRoom chatRoom = snapshot.toObject(ChatRoom.class);
                            mChatRoom = chatRoom;
                            if (!mChatRoom.isVoiceTalkOn()) {
                                if (remonCall != null) {
                                    remonCall.close();
                                    remonCall = null;
                                }
                                remonConfig = null;
                                if (mCallingTimer != null) {
                                    mCallingTimer.cancel();
                                }
                                if (mWaitingTimer != null) {
                                    mWaitingTimer.cancel();
                                }
                                sendCloseMessage();
                                finish();
                            }
                        }
                    }
                });
    }

    private void sendCloseMessage() {
        if (isCloseMessageSent) {
            return;
        }

        isCloseMessageSent = true;

        if (!mChatRoom.getCallerUid().equals(mPartnerUid)) {
            String message = "???????????? ??????";
            if (callingTime > 1) {
                String minutes = getStringFrom(callingTime / 60);
                String seconds = getStringFrom(callingTime % 60);
                message = "???????????? ?????? " + minutes + " : " + seconds;
            }
            DocumentReference messageRef = FirebaseHelper.db.collection("ChatRoom").document(mChatRoom.getRoomId()).collection("Message").document();
            String messageId = messageRef.getId();
            ChatMessage chatMessage = new ChatMessage(messageId, mChatRoom.getRoomId(), DateUtil.getUnixTimeLong(), DateUtil.getDateSec(), message, MyProfile.getUser().getNickname(), FirebaseHelper.mUid, mPartnerUser.getNickname(), mPartnerUid, "");
            messageRef.set(chatMessage);
        }

    }

    private void initRemonCallback() {
        // RemonCall, RemonCast ??? ???????????? ????????? ??? ???????????? ???????????????.
        remonCall.onInit(() -> {
            runOnUiThread(() -> mStatusTextView.setText("???????????? ??????"));
            mediaPlayer.start();

        });
        remonCall.onMessage(msg -> {
        });

        // ?????? ?????? ??? ?????? ????????? ????????? ?????? ???????????? ???????????????.
        remonCall.onConnect((channelId) -> {
            // Do something
        });

        remonCall.onComplete(() -> {
            Bundle eventBundle = new Bundle();
            eventBundle.putInt(LogData.dia, 0);
            eventBundle.putString(LogData.partnerUid, mPartnerUid);
            eventBundle.putString(LogData.roomId, mChatRoom.getRoomId());
            LogData.customLog(LogData.ti_s14_chat_voice_talk,  eventBundle, VoiceTalkActivity.this);
            LogData.setStageTodayIntro(LogData.ti_s14_chat_voice_talk, VoiceTalkActivity.this);

            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            if (mWaitingTimer != null) {
                mWaitingTimer.cancel();
            }

            runOnUiThread(() -> mOkLayout.setVisibility(View.GONE));
            setCallingTimer();

            Map<String, Object> voiceTalkData = new HashMap<>();
            voiceTalkData.put(FirebaseHelper.voiceTalkOn, true);
            voiceTalkData.put(FirebaseHelper.recentTimestamp, DateUtil.getUnixTimeLong());
            FirebaseHelper.db.collection("ChatRoom").document(mChatRoom.getRoomId()).collection("VoiceTalk").document(mChatRoom.getRoomId()).set(voiceTalkData, SetOptions.merge());
        });

        // ???????????? ????????? ?????????, close() ????????? ????????? ???????????? ???????????????.
        remonCall.onClose((closeType) -> {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            // CloseType.MINE : ????????? close() ??? ????????? ????????? ?????? ??????
            // CloseType.OTHER : ???????????? close() ??? ????????? ????????? ?????? ??????
            // CloseType.OTHER_UNEXPECTED : ???????????? ???????????? ????????? ????????? ??????
            // CloseType.UNKNOWN : ????????? ??? ??? ?????? ????????? ????????? ??????

            // ????????? ?????? ???????????? ??????
            if (closeType == CloseType.UNKNOWN && latestError != null) {
                Toast.makeText(VoiceTalkActivity.this, "???????????? ?????????", Toast.LENGTH_SHORT).show();
            }

            runOnUiThread(() -> mStatusTextView.setText("???????????? ??????"));
            closeVoiceTalk();

        });

        // ????????? ????????? ??? ???????????? ????????? ???????????????.
        // ????????? ???????????? ?????? ?????? ?????? ??? onClose??? ?????? ?????????,
        // ??????????????? ?????? ux ????????? onClose?????? ??????????????? ?????????.
        remonCall.onError((error) -> {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            latestError = error;
            Log.d("error:", error + "");
        });
        remonCall.onStat(report -> {

        });
    }

    private void setCallingTimer() {
        mCallingTimer = new Timer();
        TimerTask callingTimerTask = new TimerTask() {
            @Override
            public void run() {
                callingTime++;
                String minutes = getStringFrom(callingTime / 60);
                String seconds = getStringFrom(callingTime % 60);
                String time = minutes + " : " + seconds;
                runOnUiThread(() -> mStatusTextView.setText(time));

                if (callingTime * 1000 > (630000 - mChatRoom.getCallingTime())) {
                    closeVoiceTalk();
                }
            }
        };

        mCallingTimer.schedule(callingTimerTask, 0, 1000);
    }

    private String getStringFrom(int time) {
        if (time < 10) {
            return "0" + time;
        } else {
            return "" + time;
        }
    }

    private void setWaitingTimer() {
        mWaitingTimer = new Timer();
        TimerTask callingTimerTask = new TimerTask() {
            @Override
            public void run() {
                waitingTime++;
                if (waitingTime > 60) {
                    closeVoiceTalk();
                }
            }
        };
        mWaitingTimer.schedule(callingTimerTask, 0, 1000);
    }

    @Override
    protected void onDestroy() {
        closeVoiceTalk();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }

        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length > 0) {
            for (int i = 0; i < grantResults.length; i++) {
                if (Build.VERSION.SDK_INT >= 23 && grantResults[i] != PackageManager.PERMISSION_GRANTED && !shouldShowRequestPermissionRationale(permissions[i])) {
                    permissionSetting(requestCode);
                    return;
                }
            }

            for (int grantResult : grantResults) {
                if (Build.VERSION.SDK_INT >= 23 && grantResult != PackageManager.PERMISSION_GRANTED) {
                    requestPermissionAgain();
                    return;
                }
            }

            if (requestCode == CONNECT_PERMISSION_REQUEST) {
                startCall();
            }

        } else {
            Toast.makeText(VoiceTalkActivity.this, "??????????????? ????????? ????????? ?????? ?????????????????????.", Toast.LENGTH_SHORT).show();
            closeVoiceTalk();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && Build.VERSION.SDK_INT >= 23) {
            requestPermissions(MANDATORY_PERMISSIONS, requestCode);
        }
    }

    private void requestPermissionAgain() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("??????????????? ?????? ?????? ??????");
        alertDialogBuilder
                .setMessage("??????????????? ?????? ???????????? ?????? ????????? ???????????????.")
                .setCancelable(false)
                .setPositiveButton("????????????", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (Build.VERSION.SDK_INT >= 23) {
                            requestPermissions(MANDATORY_PERMISSIONS, CONNECT_PERMISSION_REQUEST);
                        }
                    }
                })
                .setNegativeButton("????????????", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        closeVoiceTalk();
                        Toast.makeText(VoiceTalkActivity.this, "??????????????? ????????? ????????? ?????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void permissionSetting(int requestCode) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("??????????????? ?????? ?????? ??????");
        alertDialogBuilder
                .setMessage("??????????????? ?????? ???????????? ?????? ????????? ???????????????. ???????????? ?????????, ?????? ?????? ????????? ??????????????????.")
                .setCancelable(false)
                .setPositiveButton("????????????", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (Build.VERSION.SDK_INT >= 23) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivityForResult(intent, requestCode);
                        }
                    }
                })
                .setNegativeButton("????????????", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        closeVoiceTalk();
                        Toast.makeText(VoiceTalkActivity.this, "??????????????? ????????? ????????? ?????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            closeVoiceTalk();
        } else {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "????????? ????????? ???????????????.", Toast.LENGTH_SHORT).show();
        }

    }

    //?????? ??????
    private void setSensorManager() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            if (event.values[0] >= -SENSOR_SENSITIVITY && event.values[0] <= SENSOR_SENSITIVITY) {
                //near
                WindowManager.LayoutParams params = getWindow().getAttributes();
                params.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
                params.screenBrightness = 0;
                getWindow().setAttributes(params);
            } else {
                //far
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                WindowManager.LayoutParams params = getWindow().getAttributes();
                params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                params.screenBrightness = 100;
                getWindow().setAttributes(params);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
