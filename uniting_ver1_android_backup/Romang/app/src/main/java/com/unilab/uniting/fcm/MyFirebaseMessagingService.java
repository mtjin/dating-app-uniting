package com.unilab.uniting.fcm;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.appsflyer.AppsFlyerLib;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.unilab.uniting.R;
import com.unilab.uniting.activities.MainActivity;
import com.unilab.uniting.activities.chatting.ChattingRoomActivity;
import com.unilab.uniting.activities.community.CommunityFullActivity;
import com.unilab.uniting.activities.launch.Splash;
import com.unilab.uniting.activities.meeting.MeetingCardActivity;
import com.unilab.uniting.activities.profilecard.ProfileCardMainActivity;
import com.unilab.uniting.model.ChatRoom;
import com.unilab.uniting.model.CommunityPost;
import com.unilab.uniting.model.Invite;
import com.unilab.uniting.model.Meeting;
import com.unilab.uniting.model.User;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.LaunchUtil;
import com.unilab.uniting.utils.MyProfile;
import com.unilab.uniting.utils.Strings;

import java.util.List;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";


    //putExtra
    final static String EXTRA_MEETING = "EXTRA_MEETING"; //??????
    final String EXTRA_COMMUNITY_POST = "EXTRA_COMMUNITY_POST"; //????????????
    final static String EXTRA_PROFILE = "EXTRA_PROFILE"; //???????????????

    //?????????????????? value
    //value
    private boolean sound = true;
    private boolean todayIntro = true;
    private boolean like = true;
    private boolean checkLike = true;
    private boolean highScore = true;
    private boolean connect = true;
    private boolean meeting = true;
    private boolean community = true;
    private boolean chatting = true;
    private int badgeCount = 0;

    //DB?????? ??????????????? ?????????
    private String mMembership = "-1";
    private String mSignUpProgress = "-1";
    private User mUser;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        FacebookSdk.fullyInitialize();
        AppEventsLogger.activateApp(getApplication());

        //?????????????????? ?????? ????????????
        loadNotiSettingShared();

        //json ????????? ????????????
        String title = remoteMessage.getData().get("title");
        String message = remoteMessage.getData().get("body");
        String from = remoteMessage.getData().get("location");
        String dataJson = remoteMessage.getData().get("object");
        String type = remoteMessage.getData().get("type"); //type ?????? ????????? null ?????????
        String count = remoteMessage.getData().get("badge");
        try{
            badgeCount = Integer.parseInt(count);
        } catch (Exception err){
            badgeCount = 0;
        }

        init();


        if (title == null || message == null || type == null) {
            return;
        }


        Gson gson = new Gson();

        switch (type) {
            case "todayIntro":
            default:
                if (todayIntro) {
                    sendNotification(title, message, null, sound, from);
                }
                break;
            case "meeting":
                if (meeting) {
                    Meeting meeting = gson.fromJson(dataJson, Meeting.class);
                    sendNotification(title, message, meeting, sound, from);
                }
                break;
            case "chatting":
                switch (from) {
                    case "sendMessage":
                        if (isAppIsInBackground(getApplicationContext()) && chatting) {
                            ChatRoom chatRoom = gson.fromJson(dataJson, ChatRoom.class);
                            sendNotification(title, message, "chatting", sound, from);
                        }
                        break;
                    case "openChatting":
                        if (connect) {
                            ChatRoom chatRoom = gson.fromJson(dataJson, ChatRoom.class);
                            sendNotification(title, message, "chatting", sound, from);
                        }
                        break;
                }

                break;
            case "invite":
                break;
            case "communityPost":
                if (community) {
                    CommunityPost communityPost = gson.fromJson(dataJson, CommunityPost.class);
                    sendNotification(title, message, communityPost, sound, from);
                }
                break;
            case "user": {
                switch (from) {
                    case "like":
                        if (like) {
                            User user = gson.fromJson(dataJson, User.class);
                            sendNotification(title, message, user, sound, from);
                        }
                        break;
                    case "checkLike":
                        if (checkLike) {
                            User user = gson.fromJson(dataJson, User.class);
                            sendNotification(title, message, user, sound, from);
                        }
                        break;
                    case "highScore":
                        if (highScore) {
                            User user = gson.fromJson(dataJson, User.class);
                            sendNotification(title, message, user, sound, from);
                        }
                        break;
                    case "connect":
                        if (connect) {
                            User user = gson.fromJson(dataJson, User.class);
                            sendNotification(title, message, user, sound, from);
                        }
                        break;
                }
            }
        }

    }
    // [END receive_message]

    private void init(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if(currentUser == null){
            goSplash();
            return;
        }

        if(MyProfile.getUser().getMembership() == null || MyProfile.getUser().getMembership().equals("") ||
                MyProfile.getUser().getMembership().equals(LaunchUtil.Screening) || MyProfile.getUser().getMembership().equals(LaunchUtil.Scoring)
        || MyProfile.getUser().getMembership().equals(LaunchUtil.SignUp) || MyProfile.getUser().getMembership().equals(LaunchUtil.Fail)) {
            goSplash();
            return;
        }

        FirebaseHelper.init(auth);


    }

    private void goSplash(){
        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent intent = new Intent(this, Splash.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("fcm_default_channel","fcm_default_channel",NotificationManager.IMPORTANCE_HIGH);
            if(!sound){
                channel.enableVibration(false);
            }
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notificationBuilder;
        notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("")
                        .setContentText("")
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setVibrate(new long[]{1000, 1000})
                        .setNumber(badgeCount)
                        .setLights(Color.BLUE, 1, 1)
                        .setContentIntent(pendingIntent);
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }


    // [START on_new_token]

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        AppsFlyerLib.getInstance().updateServerUninstallToken(getApplicationContext(), token);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if(user != null){
            FirebaseHelper.init(auth);
            FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).update(FirebaseHelper.fcmToken, token)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "?????? ?????? ?????? ??????. ?????? ????????? ??????????????????.", Toast.LENGTH_LONG).show();
                        }
                    });
        }

    }
    // [END on_new_token]


    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String title, String messageBody, Object object, boolean sound, String from) {
        int notifyId = 0; //?????????????????? ????????? ?????? ?????? ????????? ?????? ???????????? ???????????????. (ex ??????????????? 1??? ????????? ?????? ??????????????? ??????????????? ?????????????????? ??????????????????????????????, ???????????????????????? 1??? ?????? ??????????????? ????????????????????? ????????? ???????????? ????????????.)
        PendingIntent pendingIntent = null;
        if (object instanceof Meeting) {
            Log.d(TAG, " ?????? ?????? ??????");
            Intent intent = new Intent(this, MeetingCardActivity.class);
            Meeting meeting = (Meeting) object;
            intent.putExtra(Strings.EXTRA_MEETING, meeting);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);
            notifyId = 1;
        } else if (object instanceof CommunityPost) {
            Log.d(TAG, " ???????????? ?????? ??????");
            Intent intent = new Intent(this, CommunityFullActivity.class);
            CommunityPost communityPost = (CommunityPost) object;
            intent.putExtra(EXTRA_COMMUNITY_POST, communityPost);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);
            notifyId = 2;
        } else if (object instanceof User) {
            Log.d(TAG, " ?????? ?????? ??????");
            Intent intent = new Intent(this, ProfileCardMainActivity.class);
            User user = (User) object;
            intent.putExtra(Strings.partnerUid, user.getUid());
            intent.putExtra(Strings.partnerUser, user);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);
            notifyId = 3;
        }else if(object instanceof Invite){
            Log.d(TAG, " ???????????? ?????? ??????");
            Intent intent = new Intent(this, Splash.class);
            pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);
            notifyId = 4;
        }else if(object instanceof ChatRoom){
            Log.d(TAG, "?????? ?????? ??????");
            Intent intent;
            intent = new Intent(this, ChattingRoomActivity.class);
            ChatRoom chatRoom = (ChatRoom) object;
            intent.putExtra("EXTRA_CHATROOM_ID", chatRoom);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);
            notifyId = 5;
        }else{
            if(object == "chatting"){
                Log.d(TAG, " ????????? ?????? ??????");
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra(Strings.defaultPage,4);
                pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);
                notifyId = 6;
            } else {
                Log.d(TAG, " ????????? ?????? ??????");
                Intent intent = new Intent(this, MainActivity.class);
                pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);
                notifyId = 7;
            }

        }


        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder;
        if(sound) {
            notificationBuilder =
                    new NotificationCompat.Builder(this, channelId)
                            .setSmallIcon(R.drawable.noticon)
                            .setContentTitle(title)
                            .setContentText(messageBody)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setVibrate(new long[]{1000, 1000})
                            .setNumber(badgeCount)
                            .setLights(Color.BLUE, 1, 1)
                            .setContentIntent(pendingIntent);
        }else{//??????, ?????? ????????? ???????????????????????? ?????? ????????????? ??????..?
            notificationBuilder =
                    new NotificationCompat.Builder(this, channelId)
                            .setSmallIcon(R.drawable.noticon)
                            .setContentTitle(title)
                            .setContentText(messageBody)
                            .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
                            .setGroup("My Group")
                            .setGroupSummary(false)
                            .setDefaults(NotificationCompat.DEFAULT_ALL)
                            .setSound(null)
                            .setPriority(NotificationCompat.PRIORITY_MIN)
                            .setNumber(badgeCount)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent);
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("fcm_default_channel","fcm_default_channel",NotificationManager.IMPORTANCE_HIGH);
            if(!sound){
                channel.enableVibration(false);
            }
            notificationManager.createNotificationChannel(channel);
        }

        //?????? ??????
        if(sound){
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "MyApp::MyWakelockTag");
            wakeLock.acquire(3000);
        }

        notificationManager.notify(notifyId /* ID of notification */, notificationBuilder.build());

    }

    //???????????????????????? ??????????????? ??????????????? ??? ??????
    private boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }
        return isInBackground;
    }


    //???????????????????????? ??????????????? ??????????????? ??? ??????
/*    private boolean isAppRunning(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        for (int i = 0; i < procInfos.size(); i++) {
            if (procInfos.get(i).processName.equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }*/

    private void loadNotiSettingShared() {
        SharedPreferences pref = getSharedPreferences("notificationSetting" + FirebaseHelper.mUid, MODE_PRIVATE);
        sound = pref.getBoolean("sound", true);//?????? ??? ??????
        todayIntro = pref.getBoolean("todayIntro", true); //????????? ??????
        like = pref.getBoolean("like", true);//????????? ??????
        checkLike = pref.getBoolean("checkLike", true); // ?????? ????????? ??????
        highScore = pref.getBoolean("highScore", true); //?????? ?????? ??????
        connect = pref.getBoolean("connect", true);//?????????
        meeting = pref.getBoolean("meeting", true);//?????? ?????? ??????
        community = pref.getBoolean("community", true);//????????????
        chatting = pref.getBoolean("chatting", true); //?????? ?????????
        Log.d(TAG, "sound: " + sound);
        Log.d(TAG, "todayIntro: " + todayIntro);
        Log.d(TAG, "like: " + like);
        Log.d(TAG, "checkLike: " + checkLike);
        Log.d(TAG, "highScore: " + highScore);
        Log.d(TAG, "connect: " + connect);
        Log.d(TAG, "chatting: " + chatting);
    }




}
