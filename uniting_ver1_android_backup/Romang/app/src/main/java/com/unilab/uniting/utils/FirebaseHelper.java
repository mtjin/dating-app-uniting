package com.unilab.uniting.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.appsflyer.AppsFlyerLib;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.unilab.uniting.activities.MainActivity;
import com.unilab.uniting.model.ChatMessage;
import com.unilab.uniting.model.Interaction;
import com.unilab.uniting.model.MyGeoPoint;
import com.unilab.uniting.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FirebaseHelper {
    final static String TAG = "FirebaseHelperT";
    public static FirebaseAuth auth = FirebaseAuth.getInstance(); //인증객체
    public static FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); //인증 유저 객체
    public static FirebaseFirestore db = FirebaseFirestore.getInstance(); //퍄이어스토어 디비
    public static DatabaseReference rtdb = FirebaseDatabase.getInstance().getReference(); //리얼타임데이터베이스
    public static String mUid = MyProfile.getUser().getUid();  // 유저 고유토큰
    public static String mEmail = MyProfile.getUser().getEmail();
    public static String mAppsflyerUid = "";
    public static StorageReference storageRef = FirebaseStorage.getInstance().getReference(); // 스토리지
    public static FirebaseFunctions mFunctions = FirebaseFunctions.getInstance(Strings.region_asia);

    public static void init(FirebaseAuth mAuth, Context context){
        auth = mAuth;
        user = mAuth.getCurrentUser();
        if (user != null){
            mUid = user.getUid();
            mEmail = user.getEmail();
            mAppsflyerUid = AppsFlyerLib.getInstance().getAppsFlyerUID(context);

            AppsFlyerLib.getInstance().setCustomerUserId(mUid);
            FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
            firebaseAnalytics.setUserId(mUid);
            AppEventsLogger.setUserID(mUid);
        }

    }

    public static void init(FirebaseAuth mAuth){
        auth = mAuth;
        user = mAuth.getCurrentUser();
        if (user != null){
            mUid = user.getUid();
            mEmail = user.getEmail();

            AppsFlyerLib.getInstance().setCustomerUserId(mUid);
            AppEventsLogger.setUserID(mUid);
        }

    }

    public static Task<Map<String, Boolean>> checkBlock(String partnerUid) {
        // Create the arguments to the callable function.
        Map<String, String> data = new HashMap<>();
        data.put(Strings.partnerUid, partnerUid);

        return mFunctions
                .getHttpsCallable(FirebaseHelper.checkBlockOnly)
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, Map<String,Boolean>>() {
                    @Override
                    public Map<String,Boolean> then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        Map<String,Boolean> result = (Map<String,Boolean>) task.getResult().getData();
                        Log.d("test111222","중간" + result);
                        return result;
                    }
                });
    }



    public static void blockUser(Activity activity, User partnerUser, RelativeLayout loaderLayout, boolean goMain){

        String partnerUid = partnerUser.getUid();
        FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("TodayIntro").document(partnerUid).update(FirebaseHelper.blocked, true);
        FirebaseHelper.db.collection("Users").document(partnerUid).collection("TodayIntro").document(FirebaseHelper.mUid).update(FirebaseHelper.blocked, true);
        FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("Evaluation").document(partnerUid).update(FirebaseHelper.blocked, true);
        FirebaseHelper.db.collection("Users").document(partnerUid).collection("Evaluation").document(FirebaseHelper.mUid).update(FirebaseHelper.blocked, true);

        long nowUnixTime = DateUtil.getUnixTimeLong();
        Map<String, Object> unionData = new HashMap<>();
        unionData.put(FirebaseHelper.blockUserList, FieldValue.arrayUnion(partnerUid));
        Map<String, Object> unionData2 = new HashMap<>();
        unionData2.put(FirebaseHelper.blockUserList, FieldValue.arrayUnion(MyProfile.getUser().getUid()));

        WriteBatch batch = FirebaseHelper.db.batch();
        batch.set(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("Block").document(FirebaseHelper.mUid),
                unionData, SetOptions.merge());
        batch.set(FirebaseHelper.db.collection("Users").document(partnerUid).collection("Block").document(partnerUid),
                unionData2, SetOptions.merge());


        final int[] count = {0};

        //해당 유저의 채팅방 정보 받아오기. (프로필 카드에서 삭제하는 경우 대비)
        loaderLayout.setVisibility(View.VISIBLE);
        FirebaseHelper.db.collection("ChatRoom").whereIn(FirebaseHelper.userUidList, Arrays.asList(Arrays.asList(FirebaseHelper.mUid, partnerUid), Arrays.asList(partnerUid, FirebaseHelper.mUid))).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //채팅 state 변경
                                Map<String, Object> updateChat = new HashMap<>();
                                updateChat.put(FirebaseHelper.deleted, true);
                                if (document.get(FirebaseHelper.uidDelete) != null && !((String)document.get(FirebaseHelper.uidDelete)).equals("")) {
                                    updateChat.put(FirebaseHelper.uidBanned, FirebaseHelper.mUid);
                                } else {
                                    updateChat.put(FirebaseHelper.uidDelete, FirebaseHelper.mUid);
                                }

                                DocumentReference messageRef = FirebaseHelper.db.collection("ChatRoom").document(document.getId()).collection("Message").document();
                                String messageId = messageRef.getId();
                                batch.update(FirebaseHelper.db.collection("ChatRoom").document(document.getId()), updateChat);

                                //운영진 메세지 추가
                                ChatMessage chatMessage = new ChatMessage(messageId, document.getId(), DateUtil.getUnixTimeLong(), DateUtil.getDateSec(), "대화가 종료되었습니다.", "운영자", FirebaseHelper.mUid, "운영자", partnerUid, ChatMessage.end);
                                batch.set(messageRef, chatMessage);
                            }
                        }

                        count[0] = count[0] + 1;
                        if(count[0] == 2){
                            batch.commit()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            loaderLayout.setVisibility(View.GONE);
                                            Toast.makeText(activity, "더이상 연결되지 않습니다.", Toast.LENGTH_SHORT).show();
                                            if(goMain){
                                                //메인 액티비티 홈으로 넘기기
                                                Intent intentHome = new Intent(activity, MainActivity.class);
                                                intentHome.putExtra("defaultPage", 0);
                                                intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                activity.startActivity(intentHome);
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            loaderLayout.setVisibility(View.GONE);
                                            Toast.makeText(activity, "네트워크가 불안정합니다", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });

        FirebaseHelper.db.collection("Interaction").whereIn(FirebaseHelper.uidList, Arrays.asList(Arrays.asList(FirebaseHelper.mUid, partnerUid), Arrays.asList(partnerUid, FirebaseHelper.mUid)))
                .orderBy(FirebaseHelper.createTime).limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "get success222222");
                            if(task.getResult() == null || task.getResult().isEmpty()){
                                ArrayList<String> uidList = new ArrayList<>();
                                uidList.add(FirebaseHelper.mUid);
                                uidList.add(partnerUid);
                                DocumentReference interDoc = FirebaseHelper.db.collection("Interaction").document();
                                Interaction interaction = new Interaction(interDoc.getId(), FirebaseHelper.mUid, partnerUid, uidList, Interaction.PRE_SCORE, Interaction.PRE_SCORE, false, false,
                                        "", "", "", true, FirebaseHelper.mUid, MyProfile.getUser(), partnerUser, nowUnixTime, nowUnixTime);
                                batch.set(interDoc, interaction, SetOptions.merge());
                            } else {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Interaction interaction = document.toObject(Interaction.class);
                                    batch.update(FirebaseHelper.db.collection("Interaction").document(interaction.getInteractionId()),
                                            FirebaseHelper.blocked, true,
                                            FirebaseHelper.uidBlock, FirebaseHelper.mUid);
                                }
                            }
                        } else {
                            Log.d(TAG, "get failed with22222 ", task.getException());
                        }

                        count[0] = count[0] + 1;
                        if(count[0] == 2){
                            batch.commit()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            loaderLayout.setVisibility(View.GONE);
                                            Toast.makeText(activity, "더이상 연결되지 않습니다.", Toast.LENGTH_SHORT).show();
                                            if(goMain){
                                                //메인 액티비티 홈으로 넘기기
                                                Intent intentHome = new Intent(activity, MainActivity.class);
                                                intentHome.putExtra("defaultPage", 0);
                                                intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                activity.startActivity(intentHome);
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(activity, "네트워크가 불안정합니다", Toast.LENGTH_SHORT).show();
                                            loaderLayout.setVisibility(View.GONE);
                                        }
                                    });
                        }
                    }
                });

    }

    //DB에 저장된 유저객체 필드네임 모음 (여기서만 바꾸면 전부다 바뀌도록)
    public static String uid = "uid"; //고유토큰
    public static String email = "email"; //이메일(아이디)
    public static String password = "password"; //이메일(아이디)
    public static String membership = "membership"; //인증단계 - 이메일 가입 직 후 값: 0, 학생증 업로드 후 가입승인 대기중 값 : 1, 운영진이 가입 최종 승인 후 값: 2 (추후 튜토리얼 강제화 할 예정인데 이 경우, 튜토리얼까지 비로소 완료해야 값 3되면서 최종 승인)
    public static String dateOfSignUp = "dateOfSignUp"; //가입 심사 시작일
    public static String name = "name"; //실제이름
    public static String gender = "gender"; //성별
    public static String tel = "tel"; //전화번호
    public static String birth = "birth"; //생년월일
    public static String birthYear = "birthYear"; //생년월일
    public static String age = "age"; //나이
    public static String nickname = "nickname"; //닉네임
    public static String university = "university"; //학교
    public static String major = "major"; //전공
    public static String job = "job"; //직업
    public static String location = "location"; //지역
    public static String height = "height"; //키
    public static String myBody = "myBody"; //내 체형
    public static String personality = "personality"; //성격
    public static String bloodType = "bloodType"; //혈액형
    public static String smoking = "smoking"; //흡연
    public static String drinking = "drinking"; //음주
    public static String religion = "religion"; //종교
    public static String photoUrl = "photoUrl"; //스토리지 사진 url
    public static String universityPhotoUrl = "universityPhotoUrl"; //학교증명사진 url
    public static String selfIntroduction = "selfIntroduction"; //자기 소개글
    public static String story0 = "story0"; //첫번재 스토리
    public static String story1 = "story1"; //두번째 스토리
    public static String story2 = "story2"; //세번째 스토리
    public static String inviteCode = "inviteCode"; //초대 코드

    public static String tutorialChecked = "tutorialChecked";
    public static String facebookUid = "facebookUid";
    public static String screeningPhotoUrl = "screeningPhotoUrl";
    public static String screeningResult = "screeningResult";
    public static String screeningFileName = "screeningFileName";
    public static String fcmToken = "fcmToken";

    public static String officialUniversityChecked = "officialUniversityChecked";
    public static String officialMajorChecked = "officialMajorChecked";
    public static String officialUniversityPublic = "officialUniversityPublic";
    public static String officialMajorPublic = "officialMajorPublic";
    public static String officialInfoPublic = "officialInfoPublic";
    public static String certificationType = "certificationType";

    public static String isContactBlocked = "isContactBlocked";
    public static String isUniversityBlocked = "isUniversityBlocked";
    public static String isFacebookBlocked = "isFacebookBlocked";
    public static String myPhoneNumber = "myPhoneNumber";
    public static String myFacebookUid = "myFacebookUid";
    public static String contactList = "contactList";
    public static String blockMeList = "blockMeList";
    public static String blockUserList = "blockUserList";
    public static String facebookList = "facebookList";
    public static String providerID_FB = "facebook.com";

    public static String stageTodayIntro = "stageTodayIntro";
    public static String stageCloseUser = "stageCloseUser";
    public static String stageEvaluation = "stageEvaluation";
    public static String stageMeeting = "stageMeeting";
    public static String stageMeetingApplicant = "stageMeetingApplicant";
    public static String stageMeetingHost = "stageMeetingHost";
    public static String stageCommunity = "stageCommunity";
    public static String stageTutorial = "stageTutorial";


    public static String meeting = "meeting";
    public static String todayIntro = "todayIntro";

    public static String officialUniversity = "officialUniversity"; //운영자가 학생증 보고 직접 입력한 공식 학교명 ( 추후 객관식 형태로 고르게 만드는게 나을지도?)
    public static String officialMajor = "officialMajor";//운영자가 학생증 보고 직접 입력한 공식 전공


    public static String diaId = "diaId";
    public static String meetingId = "meetingId";
    public static String postId = "postId";

    public static String toUid = "toUid";
    public static String title = "title";
    public static String body = "body";
    public static String object = "object";
    public static String objectId = "objectId";
    public static String type = "type";
    public static String date = "date";

    //fcm
    public static String chatting = "chatting";
    public static String sendMessage = "sendMessage";
    public static String openChatting = "openChatting";
    public static String applyMeetingStep2 = "applyMeetingStep2";

    public static String communityPost = "communityPost";
    public static String comment = "comment";
    public static String comment2 = "comment2";

    public static String like = "like";
    public static String step1Ok = "step1Ok";
    public static String step2Ok = "step2Ok";

    public static String checkLike = "checkLike";
    public static String highScore = "highScore";
    public static String connect = "connect";
    public static String profileConfirm = "profileConfirm";

    public static String phoneNumber = "phoneNumber";
    public static String universityEmail = "universityEmail";
    public static String timeStamp = "timeStamp";
    public static String code = "code";

    public static String signUpProgress = "signUpProgress";
    public static String publicInfo = "publicInfo";

    public static String productId = "productId";

    public static String uidList = "uidList";
    public static String createTime = "createTime";
    public static String recentTime = "recentTime";
    public static String scoreUser0to1 = "scoreUser0to1";
    public static String scoreUser1to0 = "scoreUser1to0";
    public static String likeUser0to1 = "likeUser0to1";
    public static String likeUser1to0 = "likeUser1to0";
    public static String likeApplicant = "likeApplicant";


    public static String userUidList = "userUidList";
    public static String expired = "expired";
    public static String recentTimestamp = "recentTimestamp";

    public static String appleUid = "appleUid";
    public static String appsflyerUid = "appsflyerUid";
    public static String di = "di";
    public static String nationality = "nationality";
    public static String photoComment = "photoComment";
    public static String adminComment = "adminComment";
    public static String dateOfWithdraw = "dateOfWithdraw";
    public static String dateOfWarning = "dateOfWarning";
    public static String dateOfBan = "dateOfBan";

    public static String signUp = "signUp";
    public static String active = "active";

    public static String unixTime = "unixTime";
    public static String android = "android";
    public static String permissionList = "permissionList";

    public static String recentMessage = "recentMessage";
    public static String senderUid = "senderUid";
    public static String from = "from";

    public static String uidBanned = "uidBanned";
    public static String uidDelete = "uidDelete";
    public static String deleted = "deleted";
    public static String started = "started";
    public static String uidPaid = "uidPaid";
    public static String isRefunded = "isRefunded";


    public static String activationCount = "activationCount";
    public static String activationTime = "activationTime";
    public static String activationHour = "activationHour";
    public static String device = "device";


    public static String roomId = "roomId";
    public static String isBlocked = "isBlocked";
    public static String OK = "OK";
    public static String NO = "NO";

    public static String checkBlockOnly = "checkBlockOnly";

    public static String voiceTalkOn = "voiceTalkOn";
    public static String callerUid = "callerUid";

    public static String view = "view";
    public static String createDate = "createDate";
    public static String createTimestamp = "createTimestamp";
    public static String message = "message";
    public static String score = "score";
    public static String dateOfLike = "dateOfLike";

    public static String freeLikeHistory = "freeLikeHistory";
    public static String recentFreeMatching = "recentFreeMatching";
    public static String refundedFreeMatchingCount = "refundedFreeMatchingCount";

    public static String checkChatRoomRefund = "checkChatRoomRefund";

    public static String geoPoint = "geoPoint";
    public static String geoHash = "geoHash";
    public static String geoPermitted = "geoPermitted";

    public static String introTime = "introTime";
    public static String uidBlock = "uidBlock";
    public static String blocked = "blocked";
    public static String interacted = "interacted";
    public static String closeUser = "closeUser";

    public static String averageOfReceiveScore = "averageOfReceiveScore";
    public static String averageOfSendScore = "averageOfSendScore";
    public static String sizeOfReceiveScore = "sizeOfReceiveScore";
    public static String sizeOfSendScore = "sizeOfSendScore";
    public static String scoreCount = "scoreCount";
    public static String tierRecentCount = "tierRecentCount";
    public static String tierPercent = "tierPercent";
    public static String introMannerScore = "introMannerScore";
    public static String meetingMannerScore = "meetingMannerScore";
    public static String appPushOn = "appPushOn";



    public static final String sendScoreList = "sendScoreList";
    public static final String receiveScoreList = "receiveScoreList";
    public static final String sendHighScoreList = "sendHighScoreList";
    public static final String sendLowScoreList = "sendLowScoreList";
    public static final String receiveHighScoreList = "receiveHighScoreList";
    public static final String receiveLowScoreList = "receiveLowScoreList";
    public static final String sendLikeList = "sendLikeList";
    public static final String receiveLikeList = "receiveLikeList";
    public static final String acceptLikeList = "acceptLikeList";
    public static final String likeAcceptedList = "likeAcceptedList";
    public static final String startChatList = "startChatList";
    public static final String chatStartedList = "chatStartedList";
    public static final String accumulatedMainTime = "accumulatedMainTime";
    public static final String recentMainTime = "recentMainTime";
    public static final String recentDormantTime = "recentDormantTime";
    public static final String membershipChangeDict = "membershipChangeDict";
    public static final String meetingStep1ApplyList = "meetingStep1ApplyList";
    public static final String meetingStep1AppliedList = "meetingStep1AppliedList";
    public static final String meetingStep1SendPassList = "meetingStep1SendPassList";
    public static final String meetingStep1ReceivePassList = "meetingStep1ReceivePassList";
    public static final String meetingStep1SendFailList = "meetingStep1SendFailList";
    public static final String meetingStep1ReceiveFailList = "meetingStep1ReceiveFailList";
    public static final String meetingStep2ApplyList = "meetingStep1ApplyList";
    public static final String meetingStep2AppliedList = "meetingStep1AppliedList";
    public static final String meetingStep2SendPassList = "meetingStep1SendPassList";
    public static final String meetingStep2ReceivePassList = "meetingStep1ReceivePassList";
    public static final String meetingStep2SendFailList = "meetingStep1SendFailList";
    public static final String meetingStep2ReceiveFailList = "meetingStep1ReceiveFailList";


    public final static String PRE_APPLY = "0"; //지원하기
    public final static String SCREENING = "1"; //심사중
    public final static String PASS = "2"; //사진 심사 합격
    public final static String FAIL = "3"; //불합

    //미팅 field name
    public final static String meetingStep1 = "meetingStep1";
    public final static String meetingStep2 = "meetingStep2";
    public final static String meetingStep2Applicant = "meetingStep2Applicant";

    public static GeoPoint seoulGeoPoint = new GeoPoint(37.492631,127.041548);
    public static MyGeoPoint seoulMyGeoPoint = new MyGeoPoint(37.492631,127.041548);



    public static Task<Boolean> setRecentIntroTime(String partnerUid) {
        FirebaseFunctions mFunctions = FirebaseFunctions.getInstance(Strings.region_asia);

        Map<String, String> data = new HashMap<>();
        data.put(Strings.partnerUid, partnerUid);

        return mFunctions
                .getHttpsCallable("setRecentIntroTime")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, Boolean>() {
                    @Override
                    public Boolean then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        return true;
                    }
                });
    }

}

