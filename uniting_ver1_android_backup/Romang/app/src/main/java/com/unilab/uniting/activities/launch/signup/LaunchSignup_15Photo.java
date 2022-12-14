package com.unilab.uniting.activities.launch.signup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.unilab.uniting.R;
import com.unilab.uniting.fragments.dialog.DialogEditPhoto2Fragment;
import com.unilab.uniting.fragments.dialog.DialogOkNoFragment;
import com.unilab.uniting.fragments.launch.DialogFailOkFragment;
import com.unilab.uniting.model.PushNotification;
import com.unilab.uniting.model.User;
import com.unilab.uniting.utils.BasicActivity;
import com.unilab.uniting.utils.BitmapConverter;
import com.unilab.uniting.utils.DateUtil;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.LaunchUtil;
import com.unilab.uniting.utils.LogData;
import com.unilab.uniting.utils.MyProfile;
import com.unilab.uniting.utils.Nickname;
import com.unilab.uniting.utils.RemoteConfig;
import com.unilab.uniting.utils.Strings;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LaunchSignup_15Photo extends BasicActivity implements DialogEditPhoto2Fragment.DialogEdit2Listener, DialogOkNoFragment.DialogSignUpBackListener {

    final static String TAG = "LaunchSignup_15PhotoT";
    final static String EXTRA_MESSAGE = "EXTRA_MESSAGE";

    //requestCode
    final static int PICK_IMAGE = 1;
    final static String SCREENING = "1"; //?????????
    final static String PASS = "2"; //?????? ?????? ??????
    final static String FAIL = "3"; //??????

    //value
    private long backPressedTime = 0;
    String photoComment = "????????? ?????????????????? ?????? 2??? ?????? ???????????????.";
    private boolean isFailed = false;
    private boolean isLoading = false;
    private String fcmToken = "";
    private int uploadCount = 0;
    private int uriPhotoCount = 0;
    private int selectNum = 0; //????????? ?????? 0,1,2,3,4,5
    View.OnClickListener onClickListener;
    private ArrayList<String> mPersonalityList = new ArrayList<>(); //?????? ?????????
    private ArrayList<String> mPhotoUriList = new ArrayList<>(); // ?????? Uri ?????????
    HashMap<Integer, String> photoURLMap = new HashMap<>(); //?????? ????????? ????????? ?????? ???
    private ArrayList<String> mResultPhotoURLList = new ArrayList<>();//?????? ????????? ??? url ??? ?????? ?????????
    private boolean isCertificationForced = false;
    private String signUpProgress = Strings.SignUpPrgoress.Certi;
    private String membership = LaunchUtil.SignUp;


    //xml
    private LinearLayout mBack;
    private com.unilab.uniting.square.SquareImageView mPhoto0ImageView, mPhoto1ImageView, mPhoto2ImageView, mPhoto3ImageView, mPhoto4ImageView, mPhoto5ImageView;
    private TextView mGuideTextView;
    private Button mOkButton;
    private RelativeLayout loaderLayout; //??????????????????
    private TextView mToolbarTitleTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_signup_15_photo);

        setLogData();
        init();
        updateUI();
        setOnClickListener();
        setFailComment();
    }

    private void setLogData(){
        LogData.eventLog(LogData.SignUp5_Photo, this);
        Bundle bundle = new Bundle();
        bundle.putInt(LogData.sign_up_progress, 5);
        LogData.customLog(LogData.SignUp,  bundle, this);
    }

    private void init(){
        mPhoto0ImageView = findViewById(R.id.signup_photo15_iv_photo0);
        mPhoto1ImageView = findViewById(R.id.signup_photo15_iv_photo1);
        mPhoto2ImageView = findViewById(R.id.signup_photo15_iv_photo2);
        mPhoto3ImageView = findViewById(R.id.signup_photo15_iv_photo3);
        mPhoto4ImageView = findViewById(R.id.signup_photo15_iv_photo4);
        mPhoto5ImageView = findViewById(R.id.signup_photo15_iv_photo5);
        mGuideTextView = findViewById(R.id.signup_photo15_tv_guide);
        mOkButton = findViewById(R.id.signup_photo15_btn_ok);
        mBack = findViewById(R.id.toolbar_back);
        mToolbarTitleTextView = findViewById(R.id.toolbar_title);
        mToolbarTitleTextView.setText("????????? ??????");

        //?????? ????????????
        loaderLayout = findViewById(R.id.loaderLayout);
        loaderLayout.setClickable(true);


        if(( RemoteConfig.getSharedManager().getAbTest() != null && (( RemoteConfig.getSharedManager().getAbTest().get(RemoteConfig.ABCertification.variant_name)) != null))){
            if( RemoteConfig.getSharedManager().getAbTest().get(RemoteConfig.ABCertification.variant_name).equals(RemoteConfig.ABCertification.variant_force)){
                isCertificationForced = true;
            }
        }


    }

    private void updateUI(){
        //?????????????????? ??? ?????????
        SharedPreferences pref = getSharedPreferences(FirebaseHelper.mUid , MODE_PRIVATE);
        int photoCount = pref.getInt("photoCount", 0);
        if(photoCount>0){
            if(photoCount > 0){
                mPhotoUriList.add(pref.getString("photo0", ""));
            }
            if(photoCount > 1){
                mPhotoUriList.add(pref.getString("photo1", ""));
            }
            if(photoCount > 2){
                mPhotoUriList.add(pref.getString("photo2", ""));
            }
            if(photoCount > 3){
                mPhotoUriList.add(pref.getString("photo3", ""));
            }
            if(photoCount > 4){
                mPhotoUriList.add(pref.getString("photo4", ""));
            }
            if(photoCount > 5){
                mPhotoUriList.add(pref.getString("photo5", ""));
            }
        }

        Log.d(TAG, "??????????????? ?????????(??????) " + mPhotoUriList.size());
        for (int i = 0; i < mPhotoUriList.size(); i++) {
            if (i == 0) {
                mPhoto0ImageView.setImageURI(Uri.parse(mPhotoUriList.get(i)));
            }
            if (i == 1) {
                mPhoto1ImageView.setImageURI(Uri.parse(mPhotoUriList.get(i)));
            }
            if (i == 2) {
                mPhoto2ImageView.setImageURI(Uri.parse(mPhotoUriList.get(i)));
            }
            if (i == 3) {
                mPhoto3ImageView.setImageURI(Uri.parse(mPhotoUriList.get(i)));
            }
            if (i == 4) {
                mPhoto4ImageView.setImageURI(Uri.parse(mPhotoUriList.get(i)));
            }
            if (i == 5) {
                mPhoto5ImageView.setImageURI(Uri.parse(mPhotoUriList.get(i)));
            }
        }
    }

    private void setFailComment(){
        if (MyProfile.getUser().getMembership().equals(LaunchUtil.Fail)) {
            photoComment = "????????? ?????????????????? ?????? 2??? ?????? ???????????????.";
            FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            try {
                                photoComment = (String) document.get(FirebaseHelper.photoComment);
                            } catch (Exception ex){
                                photoComment = "????????? ?????????????????? ?????? 2??? ?????? ???????????????.";
                            }
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }

                    Bundle bundle = new Bundle();
                    bundle.putString(Strings.EXTRA_MESSAGE, photoComment);
                    DialogFailOkFragment dialog2 = DialogFailOkFragment.getInstance();
                    dialog2.setArguments(bundle);
                    dialog2.show(getSupportFragmentManager(), DialogFailOkFragment.TAG_MEETING_DIALOG2);

                }
            });

        }
    }

    private void setOnClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.signup_photo15_iv_photo0: //????????????
                        selectNum = 0;
                        photoDialogRadio();
                        break;
                    case R.id.signup_photo15_iv_photo1:
                        selectNum = 1;
                        photoDialogRadio();
                        break;
                    case R.id.signup_photo15_iv_photo2:
                        selectNum = 2;
                        photoDialogRadio();
                        break;
                    case R.id.signup_photo15_iv_photo3:
                        selectNum = 3;
                        photoDialogRadio();
                        break;
                    case R.id.signup_photo15_iv_photo4:
                        selectNum = 4;
                        photoDialogRadio();
                        break;
                    case R.id.signup_photo15_iv_photo5:
                        selectNum = 5;
                        photoDialogRadio();
                        break;
                    case R.id.signup_photo15_tv_guide:
                        startActivity(new Intent(LaunchSignup_15Photo.this, LaunchSignup_15Photo_Guide.class));
                        break;
                    case R.id.signup_photo15_btn_ok:
                        updatePhotoToStorage();
                        break;
                    case R.id.toolbar_back:
                        onBackPressed();
                        break;
                }
            }
        };

        mPhoto0ImageView.setOnClickListener(onClickListener);
        mPhoto1ImageView.setOnClickListener(onClickListener);
        mPhoto2ImageView.setOnClickListener(onClickListener);
        mPhoto3ImageView.setOnClickListener(onClickListener);
        mPhoto4ImageView.setOnClickListener(onClickListener);
        mPhoto5ImageView.setOnClickListener(onClickListener);
        mGuideTextView.setOnClickListener(onClickListener);
        mOkButton.setOnClickListener(onClickListener);
        mBack.setOnClickListener(onClickListener);
    }

    //???????????? ???????????? ?????? ???????????????
    private void photoDialogRadio() {
        DialogEditPhoto2Fragment dialog = DialogEditPhoto2Fragment.getInstance();
        dialog.show(getSupportFragmentManager(), DialogEditPhoto2Fragment.TAG_EVENT_DIALOG);
    }

    @Override
    public void removePhoto() {
        if (mPhotoUriList.size() > selectNum ) { //?????????????????? ???????????? ????????????
            mPhotoUriList.remove(selectNum);
            arrangeImageResource(selectNum);
        }
    }


    //????????? ?????????????????? ?????? imageView??? ????????????, photoUriList??? ??????.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode){
                case PICK_IMAGE:
                    Uri sourceUri = data.getData();
                    if (sourceUri != null) {
                        Uri destinationUri =  Uri.fromFile(new File(getCacheDir(), "ProfilePhoto" + uriPhotoCount));
                        openCropActivity(sourceUri, destinationUri);
                    } else {
                        Toast.makeText(this, "???????????? ?????? ???????????????.", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case UCrop.REQUEST_CROP:
                    uriPhotoCount++;
                    Uri resultUri = UCrop.getOutput(data);
                    if (resultUri != null) {
                        //??????????????? ??????
                        try {
                            if (mPhotoUriList.size() <= selectNum) { //??????????????? ????????? ???????????? ????????? ??????
                                Log.d(TAG, "??????????????? ????????? ???????????? ????????? ??????");
                                setImageResource(mPhotoUriList.size(), resultUri);
                                mPhotoUriList.add(mPhotoUriList.size(), resultUri.toString());
                                saveShared();
                            } else { //?????? ??????????????? ??????
                                Log.d(TAG, "?????? ??????????????? ??????");
                                setImageResource(selectNum, resultUri); //??????????????? ????????????
                                mPhotoUriList.remove(selectNum);// ????????? ?????????
                                mPhotoUriList.add(selectNum, resultUri.toString());//??????????????? ????????????
                                saveShared();
                            }
                            Log.d("CropTest", "??????????????????." + resultUri);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(this, "???????????? ?????? ???????????????.", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }

        } else if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(this, "???????????? ?????? ???????????????.", Toast.LENGTH_SHORT).show();
        }
    }

    //DB??? ????????? ?????? ??????
    private void saveShared() {
        Log.d(TAG, "svaeShared() ?????????");
        for (int i = 0; i < mPhotoUriList.size(); i++) {
            Log.d(TAG, "?????? ??????????????? => " + mPhotoUriList.get(i));
        }

        SharedPreferences pref = getSharedPreferences(FirebaseHelper.mUid , MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("photoCount", mPhotoUriList.size());
        if(mPhotoUriList.size() >= 1){
            editor.putString("photo0", mPhotoUriList.get(0));
        }
        if(mPhotoUriList.size() >= 2){
            editor.putString("photo1", mPhotoUriList.get(1));
        }
        if(mPhotoUriList.size() >= 3){
            editor.putString("photo2", mPhotoUriList.get(2));
        }
        if(mPhotoUriList.size() >= 4){
            editor.putString("photo3", mPhotoUriList.get(3));
        }
        if(mPhotoUriList.size() >= 5){
            editor.putString("photo4", mPhotoUriList.get(4));
        }
        if(mPhotoUriList.size() >= 6){
            editor.putString("photo5", mPhotoUriList.get(5));
        }
        editor.commit();
    }

    //?????? num????????? ?????? set
    private void setImageResource(int photoUriListSize, Uri resultUri) {
        switch (photoUriListSize){
            case 0:
                mPhoto0ImageView.setImageURI(null);
                mPhoto0ImageView.setImageURI(resultUri);
                break;
            case 1:
                mPhoto1ImageView.setImageURI(null);
                mPhoto1ImageView.setImageURI(resultUri);
                break;
            case 2:
                mPhoto2ImageView.setImageURI(null);
                mPhoto2ImageView.setImageURI(resultUri);
                break;
            case 3:
                mPhoto3ImageView.setImageURI(null);
                mPhoto3ImageView.setImageURI(resultUri);
                break;
            case 4:
                mPhoto4ImageView.setImageURI(null);
                mPhoto4ImageView.setImageURI(resultUri);
                break;
            case 5:
                mPhoto5ImageView.setImageURI(null);
                mPhoto5ImageView.setImageURI(resultUri);
                break;
        }
    }

    //??????????????? ?????????
    private void arrangeImageResource(int selectNum) { //????????? ???????????? ?????? ??????
        if (selectNum == 0) {
            if (mPhotoUriList.size() == 0) {
                mPhoto0ImageView.setImageDrawable(null);
            }
            if (mPhotoUriList.size() > 0) {
                mPhoto0ImageView.setImageURI(Uri.parse(mPhotoUriList.get(0)));
                mPhoto1ImageView.setImageDrawable(null);
            }
        }
        if (selectNum <= 1) {
            if (mPhotoUriList.size() == 1) {
                mPhoto1ImageView.setImageDrawable(null);
            }
            if (mPhotoUriList.size() > 1) {
                mPhoto1ImageView.setImageURI(Uri.parse(mPhotoUriList.get(1)));
                mPhoto2ImageView.setImageDrawable(null);
            }
        }
        if (selectNum <= 2) {
            if (mPhotoUriList.size()  == 2) {
                mPhoto2ImageView.setImageDrawable(null);
            }
            if (mPhotoUriList.size() > 2) {
                mPhoto2ImageView.setImageURI(Uri.parse(mPhotoUriList.get(2)));
                mPhoto3ImageView.setImageDrawable(null);
            }
        }
        if (selectNum <= 3) {
            if (mPhotoUriList.size()  == 3) {
                mPhoto3ImageView.setImageDrawable(null);
            }
            if (mPhotoUriList.size() > 3) {
                mPhoto3ImageView.setImageURI(Uri.parse(mPhotoUriList.get(3)));
                mPhoto4ImageView.setImageDrawable(null);
            }
        }
        if (selectNum <= 4) {
            if (mPhotoUriList.size()  == 4) {
                mPhoto4ImageView.setImageDrawable(null);
            }
            if (mPhotoUriList.size() > 4) {
                mPhoto4ImageView.setImageURI(Uri.parse(mPhotoUriList.get(4)));
                mPhoto5ImageView.setImageDrawable(null);
            }
        }
        if (selectNum <= 5) {
            if (mPhotoUriList.size()  == 5) {
                mPhoto5ImageView.setImageDrawable(null);
            }
        }
    }
    //??????????????? ?????? ????????????
    private void takePhotoFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    private void openCropActivity(Uri sourceUri, Uri destinationUri) {
        UCrop.of(sourceUri, destinationUri)
                .withAspectRatio(5, 5)
                .start(this);
    }


    //Storage??? ???????????? ?????? ??? updateProfileToDB() ??????
    private void updatePhotoToStorage() {
        if (mPhotoUriList.size() < 2) {
            Toast.makeText(this, "????????? ??? ??? ?????? ?????? ?????????.", Toast.LENGTH_SHORT).show();
            return;
        }

        uploadCount = 0;
        photoURLMap = new HashMap<>(); //?????? ????????? ????????? ?????? ???
        mResultPhotoURLList = new ArrayList<>();//?????? ????????? ??? url ??? ?????? ?????????

        loaderLayout.setVisibility(View.VISIBLE);
        isLoading = true;
        //?????????????????????
        for (int i = 0; i < mPhotoUriList.size(); i++) {
            Uri file = Uri.parse(mPhotoUriList.get(i));
            final StorageReference profileStorageRef = FirebaseHelper.storageRef.child("users/" + FirebaseHelper.mUid + "/signUp/").child("ProfilePhoto" + i);
            final int finalPosition = i;
            UploadTask uploadTask = BitmapConverter.getUploadTask(profileStorageRef, file);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //????????? ????????? ?????? url ????????????
                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                Log.d(TAG, "??????" + task.getException());
                                loaderLayout.setVisibility(View.GONE);
                                isLoading = false;
                                throw task.getException();
                            }
                            // Continue with the task to get the download URL
                            return profileStorageRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                //???????????? url ???????????? ??????
                                Log.d(TAG, "?????? ???????????? url ????????? :" + task.getResult());
                                uploadCount++;
                                photoURLMap.put(finalPosition, String.valueOf(task.getResult()));
                                if(uploadCount == mPhotoUriList.size()){
                                    for (int j = 0; j < photoURLMap.size(); j++) {
                                        mResultPhotoURLList.add(photoURLMap.get(j));
                                    }
                                    saveProfileToDB();
                                }

                            } else {
                                Toast.makeText(LaunchSignup_15Photo.this, "?????? ???????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                                loaderLayout.setVisibility(View.GONE);
                                isLoading = false;
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(LaunchSignup_15Photo.this, "?????? ???????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                    loaderLayout.setVisibility(View.GONE);
                    isLoading = false;
                }
            });
        }
    }

    private void saveProfileToDB() {
        Log.d(TAG, "saveProfileToDB() ??????");
        SharedPreferences pref = getSharedPreferences(FirebaseHelper.mUid , MODE_PRIVATE);
        String defaultNickname =  new Nickname().getRandomNickname();
        String nickname = pref.getString(FirebaseHelper.nickname, defaultNickname);
        if(nickname.equals("")){
            nickname = defaultNickname;
        }

//        String uid = FirebaseHelper.mUid;
//        String email = FirebaseHelper.mEmail;
//        String password = pref.getString(FirebaseHelper.password, "");

//        String di = pref.getString(FirebaseHelper.di, "");
//        String nationality = pref.getString(FirebaseHelper.nationality, "");
//        String phoneNumber = pref.getString(FirebaseHelper.phoneNumber, "");
//        String birth = pref.getString(FirebaseHelper.birth, "");
//        int birthYear = pref.getInt(FirebaseHelper.birthYear, 0);
//        String name = pref.getString(FirebaseHelper.name, "");
//        String gender = pref.getString(FirebaseHelper.gender, "");

//        String height = pref.getString(FirebaseHelper.height, "");
//        String religion = pref.getString(FirebaseHelper.religion, "");
//        String location = pref.getString(FirebaseHelper.location, "");
//        //?????? ?????????
//        Set<String> personalitySet = pref.getStringSet(FirebaseHelper.personality, null);
//        if(personalitySet == null){
//            mPersonalityList = new ArrayList<>(Arrays.asList());
//        }else{
//            mPersonalityList = new ArrayList<String>(personalitySet);
//        }
//        String bloodType = pref.getString(FirebaseHelper.bloodType, "");
//        String drinking = pref.getString(FirebaseHelper.drinking, "");
//        String smoking = pref.getString(FirebaseHelper.smoking, "");

//        String facebookUid = pref.getString(FirebaseHelper.facebookUid, "");
//        String appleUid = pref.getString(FirebaseHelper.appleUid, "");

        String selfIntroduction = pref.getString(FirebaseHelper.selfIntroduction,"");
        String story0 = pref.getString(FirebaseHelper.story0,"");
        String story1 = pref.getString(FirebaseHelper.story1,"");
        String story2 = pref.getString(FirebaseHelper.story2,"");


        //?????? ?????? ????????? ????????? ?????? (?????????????????? PASS, null??? ?????? ???????????????)
        ArrayList<String> defaultList = new ArrayList<>();
        ArrayList<String> screeningResultList = new ArrayList<>();
        ArrayList<String> screeningFileNameList = new ArrayList<>();
        for(int i = 0; i < mResultPhotoURLList.size(); i++){
            screeningResultList.add(SCREENING); //?????? ???????????? ????????? PASS
            screeningFileNameList.add("users/" + FirebaseHelper.mUid + "/signUp/ProfilePhoto" + i); //?????? ???????????? ?????? ?????? ????????? null
            defaultList.add(null); //?????? ?????????
        }

        //?????? ???????????? ???, ???????????? ??????, ?????? ?????? ??????, ?????? ?????? doc ?????? ????????? ?????? ??????.

        Map<String, Object> screeningDefault = new HashMap<>();
        screeningDefault.put(FirebaseHelper.screeningPhotoUrl, mResultPhotoURLList);
        screeningDefault.put(FirebaseHelper.screeningResult, screeningResultList);
        screeningDefault.put(FirebaseHelper.screeningFileName, screeningFileNameList);

        Map<String, Object> create = new HashMap<>();
        Map<String, Object> interactionData = new HashMap<>();
        Map<String, Object> freeInteractionData = new HashMap<>();
        Map<String, Object> evaluationData = new HashMap<>();
        Map<String, Object> permissionData = new HashMap<>();
        interactionData.put(FirebaseHelper.sendLikeList, new ArrayList<String>());
        freeInteractionData.put(FirebaseHelper.freeLikeHistory, new HashMap<String,Boolean>());
        create.put("EX", "EX");
        evaluationData.put("date", DateUtil.getDateSec());
        evaluationData.put(FirebaseHelper.gender, MyProfile.getUser().getGender());

        permissionData.put(FirebaseHelper.permissionList, new ArrayList<String>());

        String usedDate = DateUtil.getDateSec();
        PushNotification pushNotification = new PushNotification(true,true,true,true,true,true,true,true,true,true);


        //fcm?????? ?????????.
        String finalNickname = nickname;
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            Toast.makeText(LaunchSignup_15Photo.this, "?????? ?????? ??????. ??????????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                        } else {
                            fcmToken = task.getResult().getToken(); //fcmToken ??????????????? ????????? ??????????????????.
                        }


                        signUpProgress = Strings.SignUpPrgoress.Certi;
                        membership = LaunchUtil.SignUp;

                        if (MyProfile.getUser().isOfficialUniversityChecked()){
                            membership = LaunchUtil.Screening;
                            signUpProgress = Strings.SignUpPrgoress.Screening;
                        }

                        if (!isCertificationForced){
                            membership = LaunchUtil.Screening;
                        }

//                        Map<String, Object> user = new HashMap<>();
//                        user.put(FirebaseHelper.uid, uid);
//                        user.put(FirebaseHelper.di, di);
//                        user.put(FirebaseHelper.nationality, nationality);
//                        user.put(FirebaseHelper.email, email);
//                        user.put(FirebaseHelper.membership, membership);
//                        user.put(FirebaseHelper.dateOfSignUp, DateUtil.getDateSec());
//                        user.put(FirebaseHelper.gender, gender);
//                        user.put(FirebaseHelper.birthYear, birthYear);
//                        user.put(FirebaseHelper.nickname, finalNickname);
//                        user.put(FirebaseHelper.location, location);
//                        user.put(FirebaseHelper.geoPoint, FirebaseHelper.seoulMyGeoPoint);
//                        user.put(FirebaseHelper.geoHash, "");
//                        user.put(FirebaseHelper.geoPermitted, false);
//                        user.put(FirebaseHelper.height, height);
//                        user.put(FirebaseHelper.personality, mPersonalityList);
//                        user.put(FirebaseHelper.bloodType, bloodType);
//                        user.put(FirebaseHelper.smoking, smoking);
//                        user.put(FirebaseHelper.drinking, drinking);
//                        user.put(FirebaseHelper.religion, religion);
//                        user.put(FirebaseHelper.photoUrl, defaultList);
//                        user.put(FirebaseHelper.officialUniversityPublic, true);
//                        user.put(FirebaseHelper.officialMajorPublic, false);
//                        user.put(FirebaseHelper.officialInfoPublic, false);
//                        user.put(FirebaseHelper.device, FirebaseHelper.android);
//                        user.put(FirebaseHelper.signUpProgress, signUpProgress);
//                        user.put(FirebaseHelper.stageTodayIntro, LogData.ti_s00);
//                        user.put(FirebaseHelper.stageCloseUser, LogData.close_user_s00);
//                        user.put(FirebaseHelper.stageEvaluation, LogData.evaluation_s00);
//                        user.put(FirebaseHelper.stageMeeting, LogData.meeting_s00);
//                        user.put(FirebaseHelper.stageMeetingApplicant, LogData.meeting_s00);
//                        user.put(FirebaseHelper.stageMeetingHost, LogData.meeting_s00);
//                        user.put(FirebaseHelper.stageCommunity, LogData.community_s00);
//                        user.put(FirebaseHelper.stageTutorial, LogData.tutorial_s0 + "0");
//


                        Map<String, Object> userAdmin = new HashMap<>();
                        userAdmin.put(FirebaseHelper.membership, membership);
                        userAdmin.put(FirebaseHelper.dateOfSignUp, DateUtil.getDateSec());
                        userAdmin.put(FirebaseHelper.signUpProgress, signUpProgress);
                        userAdmin.put(FirebaseHelper.nickname, finalNickname);

                        User user = new User(FirebaseHelper.mUid, MyProfile.getUser().getDi(), MyProfile.getUser().getNationality(), FirebaseHelper.mEmail, membership, DateUtil.getDateSec(), MyProfile.getUser().getGender(), MyProfile.getUser().getBirthYear(), User.birthToAge(MyProfile.getUser().getBirthYear()), finalNickname,
                                MyProfile.getUser().getUniversity(), MyProfile.getUser().getMajor(), MyProfile.getUser().getLocation(), FirebaseHelper.seoulMyGeoPoint, "", false, MyProfile.getUser().getHeight(), MyProfile.getUser().getPersonality(), MyProfile.getUser().getBloodType(),
                                MyProfile.getUser().getSmoking(), MyProfile.getUser().getDrinking(),
                                MyProfile.getUser().getReligion(), defaultList, selfIntroduction,story0, story1, story2, MyProfile.getUser().getInviteCode(), MyProfile.getUser().getFacebookUid(), "", FirebaseHelper.mAppsflyerUid,
                                MyProfile.getUser().getOfficialUniversity(), MyProfile.getUser().getOfficialMajor(),
                                MyProfile.getUser().isOfficialUniversityChecked(), MyProfile.getUser().isOfficialMajorChecked(), true,
                                false, false, MyProfile.getUser().getCertificationType(), fcmToken, FirebaseHelper.android, signUpProgress,
                                LogData.ti_s00,LogData.close_user_s00,LogData.evaluation_s00,LogData.meeting_s00,LogData.meeting_s00,LogData.meeting_s00,
                                LogData.community_s00,LogData.tutorial_s0 + "0", LogData.guide_s00, LogData.guide2_s00,
                                0,0,0,0,0.5,0,0,0,0,0.5,0.5,
                                DateUtil.getUnixTimeLong(), false);



                        WriteBatch batch = FirebaseHelper.db.batch();
                        batch.set(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid), user, SetOptions.merge());
                        batch.update(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid), screeningDefault);
                        batch.set(FirebaseHelper.db.collection("AdminUsers").document(FirebaseHelper.mUid), userAdmin, SetOptions.merge());
                        batch.set(FirebaseHelper.db.collection("AdminUsers").document(FirebaseHelper.mUid).collection("ProfilePhoto").document(FirebaseHelper.mUid), screeningDefault);
                        batch.set(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("Permission").document(FirebaseHelper.mUid), permissionData);
                        batch.set(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("InteractionHistory").document(FirebaseHelper.mUid), interactionData);
                        batch.set(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("Fcm").document("PushNotification"), pushNotification);
                        batch.commit()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Map<String,String> user_properties = new HashMap<>();
                                        user_properties.put(LogData.membership, membership);
                                        user_properties.put(LogData.sign_up_progress, signUpProgress);
                                        LogData.setUserProperties(user_properties, LaunchSignup_15Photo.this);

                                        //MyProfile??? ??????
                                        loaderLayout.setVisibility(View.GONE);
                                        isLoading = false;
                                        MyProfile.init(user);
                                        if(MyProfile.getUser().isOfficialUniversityChecked()){
                                            Intent intent = new Intent(LaunchSignup_15Photo.this, LaunchSignup_17Screening.class);
                                            startActivity(intent);
                                        } else {
                                            Intent intent = new Intent(LaunchSignup_15Photo.this, LaunchSignup_16UniversityCertification.class);
                                            startActivity(intent);
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LaunchSignup_15Photo.this, "??????????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                                loaderLayout.setVisibility(View.GONE);
                                isLoading = false;
                            }
                        });
                    }
                });
    }

    @Override
    public void onBackPressed() {
        Bundle bundle = new Bundle();
        bundle.putString(Strings.title, "?????? ????????? ??????????????????????");
        bundle.putString(Strings.content, "?????? ??? ?????????!");
        bundle.putString(Strings.ok, "?????? ?????????");
        bundle.putString(Strings.no, "?????? ????????????");
        DialogOkNoFragment dialog = DialogOkNoFragment.getInstance();
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), DialogOkNoFragment.TAG_EVENT_DIALOG);
    }

    @Override
    public void back() {
        super.onBackPressed();
    }
}
